package com.augustlee.mt.toolWindow.mws.service;

import com.augustlee.mt.toolWindow.common.log.ConsoleLogger;
import com.augustlee.mt.toolWindow.mws.dto.ApiIndexDTO;
import com.augustlee.mt.toolWindow.mws.dto.ApiMethodQueryDTO;
import com.augustlee.mt.toolWindow.mws.dto.ApiMethodSearchResultDTO;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * 方法调用链上钻查询服务。
 *
 * @author August Lee
 * @since 2026/05/08
 */
public class ApiMethodCallTraceService {

    private static final ConsoleLogger LOG = ConsoleLogger.getInstance(ApiMethodCallTraceService.class);

    /**
     * 上钻层级硬上限。
     */
    public static final int MAX_TRACE_DEPTH_LIMIT = 10;

    private final Project project;
    private final SearchCacheManager searchCacheManager;

    public ApiMethodCallTraceService(Project project, SearchCacheManager searchCacheManager) {
        this.project = project;
        this.searchCacheManager = searchCacheManager;
    }

    /**
     * 根据方法查询接口地址。
     *
     * @param queryDTO 查询参数
     * @param maxTraceDepth 最大上钻层级
     * @return 查询结果
     */
    public List<ApiMethodSearchResultDTO> searchApiByMethod(ApiMethodQueryDTO queryDTO, int maxTraceDepth) {
        if (queryDTO == null) {
            return Collections.emptyList();
        }

        int normalizedTraceDepth = Math.max(0, Math.min(maxTraceDepth, MAX_TRACE_DEPTH_LIMIT));
        List<PsiMethod> targetMethodList = findTargetMethodList(queryDTO);
        if (targetMethodList.isEmpty()) {
            return Collections.emptyList();
        }

        Queue<MethodTraceNode> methodTraceQueue = new ArrayDeque<>();
        for (PsiMethod targetMethod : targetMethodList) {
            methodTraceQueue.offer(new MethodTraceNode(targetMethod, 0));
        }

        Set<String> visitedMethodKeySet = new LinkedHashSet<>();
        Map<String, ApiMethodSearchResultDTO> resultMap = new LinkedHashMap<>();

        while (!methodTraceQueue.isEmpty()) {
            MethodTraceNode methodTraceNode = methodTraceQueue.poll();
            PsiMethod currentMethod = methodTraceNode.getPsiMethod();
            String currentMethodKey = buildPsiMethodKey(currentMethod);
            if (currentMethodKey == null || !visitedMethodKeySet.add(currentMethodKey)) {
                continue;
            }

            List<PsiMethod> relatedMethodList = findRelatedMethodList(currentMethod);
            collectApiResult(relatedMethodList, methodTraceNode.getDepth(), resultMap);
            if (methodTraceNode.getDepth() >= normalizedTraceDepth) {
                continue;
            }

            List<PsiMethod> callerMethodList = findCallerMethodList(relatedMethodList);
            for (PsiMethod callerMethod : callerMethodList) {
                methodTraceQueue.offer(new MethodTraceNode(callerMethod, methodTraceNode.getDepth() + 1));
            }
        }

        return new ArrayList<>(resultMap.values());
    }

    /**
     * 定位待查询方法。
     *
     * @param queryDTO 查询参数
     * @return 命中的方法列表
     */
    private List<PsiMethod> findTargetMethodList(ApiMethodQueryDTO queryDTO) {
        PsiClass targetClass = JavaPsiFacade.getInstance(project)
                .findClass(queryDTO.getServiceName(), GlobalSearchScope.allScope(project));
        if (targetClass == null) {
            throw new IllegalArgumentException("未找到类：" + queryDTO.getServiceName());
        }

        PsiMethod[] methodArray = targetClass.findMethodsByName(queryDTO.getMethodName(), true);
        if (methodArray == null || methodArray.length == 0) {
            throw new IllegalArgumentException("未找到方法：" + queryDTO.getServiceName() + "#" + queryDTO.getMethodName());
        }

        List<PsiMethod> matchedMethodList = new ArrayList<>();
        for (PsiMethod psiMethod : methodArray) {
            if (isMethodMatched(queryDTO, psiMethod)) {
                matchedMethodList.add(psiMethod);
            }
        }

        if (matchedMethodList.isEmpty() && queryDTO.isParameterSpecified()) {
            throw new IllegalArgumentException("未找到与参数签名匹配的方法：" + queryDTO.getServiceName() + "#" + queryDTO.getMethodName());
        }

        return matchedMethodList;
    }

    /**
     * 判断方法是否与查询条件匹配。
     *
     * @param queryDTO 查询参数
     * @param psiMethod 方法
     * @return 是否匹配
     */
    private boolean isMethodMatched(ApiMethodQueryDTO queryDTO, PsiMethod psiMethod) {
        if (!queryDTO.isParameterSpecified()) {
            return true;
        }

        List<String> expectedParameterTypeList = queryDTO.getParameterTypeList();
        PsiParameter[] parameterArray = psiMethod.getParameterList().getParameters();
        if (expectedParameterTypeList.size() != parameterArray.length) {
            return false;
        }

        for (int i = 0; i < parameterArray.length; i++) {
            String expectedType = normalizeTypeName(expectedParameterTypeList.get(i));
            PsiType psiType = parameterArray[i].getType();
            String canonicalText = normalizeTypeName(psiType.getCanonicalText());
            String presentableText = normalizeTypeName(psiType.getPresentableText());
            String shortText = normalizeShortTypeName(canonicalText);
            if (!Objects.equals(expectedType, canonicalText)
                    && !Objects.equals(expectedType, presentableText)
                    && !Objects.equals(expectedType, shortText)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 收集当前方法命中的接口结果。
     *
     * <p>这里会同时检查当前方法、父接口方法、实现类方法，避免“调用链命中实现类，但缓存记录在接口方法”时查不到结果。</p>
     *
     * @param relatedMethodList 关联方法列表
     * @param depth 当前深度
     * @param resultMap 结果集
     */
    private void collectApiResult(List<PsiMethod> relatedMethodList, int depth, Map<String, ApiMethodSearchResultDTO> resultMap) {
        if (relatedMethodList == null || relatedMethodList.isEmpty()) {
            return;
        }

        for (PsiMethod psiMethod : relatedMethodList) {
            String serviceName = getQualifiedClassName(psiMethod);
            if (serviceName == null || serviceName.isEmpty()) {
                continue;
            }

            List<ApiIndexDTO> apiIndexDTOList = searchCacheManager.getApiIndexListByMethod(serviceName, psiMethod.getName());
            if (apiIndexDTOList.isEmpty()) {
                continue;
            }

            for (ApiIndexDTO apiIndexDTO : apiIndexDTOList) {
                if (apiIndexDTO == null || apiIndexDTO.getPath() == null || apiIndexDTO.getPath().trim().isEmpty()) {
                    continue;
                }
                String path = apiIndexDTO.getPath().trim();
                resultMap.putIfAbsent(path, new ApiMethodSearchResultDTO(
                        path,
                        apiIndexDTO.getServiceName(),
                        apiIndexDTO.getMethodName(),
                        depth,
                        depth == 0
                ));
            }
        }
    }

    /**
     * 查找调用当前方法的上层方法。
     *
     * @param methodList 方法列表
     * @return 调用方方法列表
     */
    private List<PsiMethod> findCallerMethodList(List<PsiMethod> methodList) {
        Map<String, PsiMethod> callerMethodMap = new LinkedHashMap<>();
        if (methodList == null || methodList.isEmpty()) {
            return new ArrayList<>();
        }

        for (PsiMethod psiMethod : methodList) {
            Collection<PsiReference> referenceCollection = MethodReferencesSearch.search(
                    psiMethod,
                    GlobalSearchScope.projectScope(project),
                    true
            ).findAll();

            for (PsiReference psiReference : referenceCollection) {
                PsiMethod callerMethod = PsiTreeUtil.getParentOfType(psiReference.getElement(), PsiMethod.class, false);
                if (callerMethod == null) {
                    continue;
                }
                String callerMethodKey = buildPsiMethodKey(callerMethod);
                if (callerMethodKey != null) {
                    callerMethodMap.putIfAbsent(callerMethodKey, callerMethod);
                }
            }
        }
        return new ArrayList<>(callerMethodMap.values());
    }

    /**
     * 查找关联方法。
     *
     * <p>为提升“上钻反查”的命中率，需要将当前方法、父接口方法、实现类方法视为同一条方法链上的等价节点。</p>
     *
     * @param psiMethod 当前方法
     * @return 关联方法列表
     */
    private List<PsiMethod> findRelatedMethodList(PsiMethod psiMethod) {
        Map<String, PsiMethod> relatedMethodMap = new LinkedHashMap<>();
        addRelatedMethod(relatedMethodMap, psiMethod);
        if (psiMethod == null) {
            return new ArrayList<>();
        }

        for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
            addRelatedMethod(relatedMethodMap, superMethod);
        }
        for (PsiMethod deepestSuperMethod : psiMethod.findDeepestSuperMethods()) {
            addRelatedMethod(relatedMethodMap, deepestSuperMethod);
        }
        for (PsiMethod overridingMethod : OverridingMethodsSearch.search(
                psiMethod,
                GlobalSearchScope.projectScope(project),
                true
        ).findAll()) {
            addRelatedMethod(relatedMethodMap, overridingMethod);
        }
        return new ArrayList<>(relatedMethodMap.values());
    }

    /**
     * 添加关联方法。
     *
     * @param relatedMethodMap 关联方法集合
     * @param psiMethod 方法
     */
    private void addRelatedMethod(Map<String, PsiMethod> relatedMethodMap, PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        String methodKey = buildPsiMethodKey(psiMethod);
        if (methodKey == null || methodKey.isEmpty()) {
            return;
        }
        relatedMethodMap.putIfAbsent(methodKey, psiMethod);
    }

    /**
     * 构建 PSI 方法唯一键。
     *
     * @param psiMethod 方法
     * @return 方法唯一键
     */
    private String buildPsiMethodKey(PsiMethod psiMethod) {
        String className = getQualifiedClassName(psiMethod);
        if (className == null || className.isEmpty()) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder(className)
                .append('#')
                .append(psiMethod.getName())
                .append('(');
        PsiParameter[] parameterArray = psiMethod.getParameterList().getParameters();
        for (int i = 0; i < parameterArray.length; i++) {
            if (i > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(normalizeTypeName(parameterArray[i].getType().getCanonicalText()));
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    /**
     * 获取方法所属类全限定名。
     *
     * @param psiMethod 方法
     * @return 类全限定名
     */
    private String getQualifiedClassName(PsiMethod psiMethod) {
        if (psiMethod == null || psiMethod.getContainingClass() == null) {
            return null;
        }
        return psiMethod.getContainingClass().getQualifiedName();
    }

    /**
     * 规范化类型名称。
     *
     * @param typeName 类型名
     * @return 规范化后的类型名
     */
    private String normalizeTypeName(String typeName) {
        if (typeName == null) {
            return "";
        }
        String normalizedTypeName = typeName.replace(" ", "").trim();
        int genericStartIndex = normalizedTypeName.indexOf('<');
        if (genericStartIndex >= 0) {
            normalizedTypeName = normalizedTypeName.substring(0, genericStartIndex);
        }
        return normalizedTypeName.replace("...", "[]");
    }

    /**
     * 获取类型短名。
     *
     * @param typeName 类型全名
     * @return 类型短名
     */
    private String normalizeShortTypeName(String typeName) {
        String normalizedTypeName = normalizeTypeName(typeName);
        int lastDotIndex = normalizedTypeName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return normalizedTypeName;
        }
        return normalizedTypeName.substring(lastDotIndex + 1);
    }

    /**
     * 方法遍历节点。
     */
    private static class MethodTraceNode {

        private final PsiMethod psiMethod;

        private final int depth;

        private MethodTraceNode(PsiMethod psiMethod, int depth) {
            this.psiMethod = psiMethod;
            this.depth = depth;
        }

        private PsiMethod getPsiMethod() {
            return psiMethod;
        }

        private int getDepth() {
            return depth;
        }
    }
}
