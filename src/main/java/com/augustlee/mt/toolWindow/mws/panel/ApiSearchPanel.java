package com.augustlee.mt.toolWindow.mws.panel;

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
import com.augustlee.mt.toolWindow.common.state.CookieInputState;
import com.augustlee.mt.toolWindow.mws.service.SearchManager;
import com.augustlee.mt.toolWindow.mws.dto.ClassIndexDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * API 搜索面板
 *
 * @see ApiSearchPanel
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class ApiSearchPanel {

    private final JPanel MAIN_PANEL = new JPanel();

    private final JLabel COOKIE_LABEL = new JLabel("Cookie：");
    private final JTextArea COOKIE_TEXT_AREA = new JTextArea(8, 30);

    private final JLabel API_LABEL = new JLabel("API：");
    private final JTextField API_TEXT_FIELD = new JTextField(30);

    private final JButton SEARCH_BUTTON = new JButton("Search");

    private final SearchManager SEARCH_MANAGER = new SearchManager();

    private Project project;
    private CookieInputState cookieState;

    public ApiSearchPanel(Project project, CookieInputState cookieState){
        this.project = project;
        this.cookieState = cookieState;
        this.initLayout();
        this.initComponent();
    }

    public JPanel getMainJPanel(){
        return this.MAIN_PANEL;
    }


    private void initLayout(){
        // 创建"查找API"选项卡面板
        MAIN_PANEL.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：Cookie标签和多行文本框
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        MAIN_PANEL.add(COOKIE_LABEL, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridheight = 1;
        JScrollPane scrollPane = new JBScrollPane(COOKIE_TEXT_AREA);
        MAIN_PANEL.add(scrollPane, gbc);

        // 第二行：API标签和普通文本框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        MAIN_PANEL.add(API_LABEL, gbc);

        gbc.gridx = 1;
        MAIN_PANEL.add(API_TEXT_FIELD, gbc);

        // 第三行：搜索按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        MAIN_PANEL.add(SEARCH_BUTTON, gbc);

        // 添加垂直填充组件
        gbc.gridy = 3;
        gbc.weighty = 1.0;  // 占用剩余垂直空间
        gbc.fill = GridBagConstraints.VERTICAL;
        MAIN_PANEL.add(Box.createVerticalGlue(), gbc);
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
        if (cookieState != null) {
            this.COOKIE_TEXT_AREA.setText(cookieState.getCookieContent());
        }


        this.SEARCH_BUTTON.addActionListener(this::searchApi);

    }

    private void searchApi(ActionEvent actionEvent) {
        String path = this.API_TEXT_FIELD.getText();

        try{
            path = path.trim();
            ClassIndexDTO classIndexDTO = this.SEARCH_MANAGER.getClassIndex(path);
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

