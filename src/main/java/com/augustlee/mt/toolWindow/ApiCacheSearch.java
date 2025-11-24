package com.augustlee.mt.toolWindow;

import com.alibaba.fastjson.JSON;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.augustlee.mt.toolWindow.mws.SearchCacheManager;
import com.augustlee.mt.toolWindow.mws.dto.ClassIndexDTO;
import com.augustlee.mt.toolWindow.tool.ApiPathState;
import com.augustlee.mt.toolWindow.tool.CookieInputState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ApiCacheSearch {

    private static final String CACHE_SIZE = "Cache size: ";

    private final JPanel MAIN_PANEL = new JPanel();

    private final JLabel CACHE_SIZE_LABEL = new JLabel(CACHE_SIZE + "0");
    private final JButton REFRESH_BUTTON = new JButton("Refresh");
    private final JTextArea COOKIE_TEXT_AREA = new JTextArea(8, 30);

    private final JLabel API_LABEL = new JLabel("API：");
    private final JTextField API_TEXT_FIELD = new JTextField(30);

    private final JButton SEARCH_BUTTON = new JButton("Search");

    private final SearchCacheManager SEARCH_CACHE_MANAGER;

    private Project project;
    private CookieInputState cookieState;

    public ApiCacheSearch(Project project, CookieInputState cookieState, ApiPathState apiPathState){
        this.SEARCH_CACHE_MANAGER = new SearchCacheManager(apiPathState);
        this.project = project;
        this.cookieState = cookieState;
        this.initLayout();
        this.initComponent();

        this.CACHE_SIZE_LABEL.setText(CACHE_SIZE + this.SEARCH_CACHE_MANAGER.getApiCount());
    }

    public JPanel getMainJPanel(){
        return this.MAIN_PANEL;
    }


    private void initLayout() {
        MAIN_PANEL.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 左侧容器
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.add(Box.createVerticalStrut(5));
        leftColumn.add(CACHE_SIZE_LABEL);
        leftColumn.add(Box.createVerticalStrut(5));
        leftColumn.add(REFRESH_BUTTON);

        // 添加左侧容器（设置weighty为0防止垂直扩展）
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0; // 新增
        MAIN_PANEL.add(leftColumn, gbc);

        // Cookie文本区域（设置weighty为0）
        JBScrollPane cookieScrollPane = new JBScrollPane(COOKIE_TEXT_AREA);
        gbc.gridx = 1;
        gbc.weighty = 0; // 新增
        gbc.fill = GridBagConstraints.BOTH;
        MAIN_PANEL.add(cookieScrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // API标签和输入框（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0; // 新增
        MAIN_PANEL.add(API_LABEL, gbc);

        gbc.gridx = 1;
        MAIN_PANEL.add(API_TEXT_FIELD, gbc);

        // 搜索按钮（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0; // 新增
        MAIN_PANEL.add(SEARCH_BUTTON, gbc);

        // 添加底部占位符将内容推至顶部
        gbc.gridy = 3;
        gbc.weighty = 1; // 占据剩余垂直空间
        MAIN_PANEL.add(Box.createGlue(), gbc);
    }

    private void initComponent(){

        this.COOKIE_TEXT_AREA.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String content = COOKIE_TEXT_AREA.getText();
                if (cookieState != null) {
                    cookieState.setCookieContent(content);
                }
            }
        });
        this.COOKIE_TEXT_AREA.setLineWrap(true);
        this.COOKIE_TEXT_AREA.setText(cookieState.getCookieContent());

        this.SEARCH_BUTTON.addActionListener(this::searchApi);
        this.REFRESH_BUTTON.addActionListener(this::refresh);
    }

    private void refresh(ActionEvent actionEvent) {
        try{
            this.SEARCH_BUTTON.setEnabled(false);
            this.REFRESH_BUTTON.setEnabled(false);
            this.SEARCH_CACHE_MANAGER.refresh();
            this.CACHE_SIZE_LABEL.setText(CACHE_SIZE + this.SEARCH_CACHE_MANAGER.getApiCount());
        } catch (Exception e) {
            Messages.showErrorDialog(project, e.getMessage(), "Refresh Failed");
            e.printStackTrace();
        } finally {
            this.SEARCH_BUTTON.setEnabled(true);
            this.REFRESH_BUTTON.setEnabled(true);
        }

    }

    private void searchApi(ActionEvent actionEvent) {
        String path = this.API_TEXT_FIELD.getText();
        try{
            path = path.trim();
            ClassIndexDTO classIndexDTO = this.SEARCH_CACHE_MANAGER.getClassIndex(path);
            if(classIndexDTO == null){
                Messages.showErrorDialog(project, "API not found: " + path, "Error");
                return;
            }
            System.out.println(JSON.toJSONString(classIndexDTO));
            goToCode(classIndexDTO.getServiceName(), classIndexDTO.getMethodName(), project);
        } catch (Exception e) {
            Messages.showErrorDialog(project, e.getMessage(), "Search Failed");
            e.printStackTrace();
        }


    }

    private void goToCode(String serviceName, String methodName, Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (DumbService.isDumb(project)) {
                Messages.showErrorDialog(project, "IDE is updating indices. Please try again later.", "Error");
                return;
            }

            ApplicationManager.getApplication().runReadAction(() -> {
                // 1. 查找类
                PsiClass targetClass = JavaPsiFacade.getInstance(project)
                        .findClass(serviceName, GlobalSearchScope.allScope(project));

                if (targetClass == null) {
                    Messages.showErrorDialog(project, "Class not found: " + serviceName, "Error");
                    return;
                }

                // 2. 查找方法
                PsiMethod targetMethod = null;
                for (PsiMethod method : targetClass.getAllMethods()) {
                    if (methodName.equals(method.getName())) {
                        targetMethod = method;
                        break;
                    }
                }

                if (targetMethod == null) {
                    Messages.showErrorDialog(project, "Method not found: " + methodName, "Error");
                    return;
                }

                // 3. 导航到方法
                NavigationUtil.activateFileWithPsiElement(targetMethod, true);
            });
        });
        }





}
