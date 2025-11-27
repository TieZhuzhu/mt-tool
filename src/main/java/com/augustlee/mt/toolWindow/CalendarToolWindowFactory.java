package com.augustlee.mt.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.augustlee.mt.toolWindow.tool.ApiPathState;
import com.augustlee.mt.toolWindow.tool.CookieInputState;
import com.augustlee.mt.toolWindow.otherdevtools.OtherDevToolsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

final class CalendarToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CalendarToolWindowContent toolWindowContent = new CalendarToolWindowContent(project, toolWindow);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class CalendarToolWindowContent {

        private Project project;

        // 创建主面板
        private JPanel mainPanel = new JPanel();

        // 创建选项卡面板
        private JTabbedPane tabbedPane = new JBTabbedPane();

        public CalendarToolWindowContent(Project project, ToolWindow toolWindow) {
            this.project = project;
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(tabbedPane, BorderLayout.CENTER);

            CookieInputState cookieState = ApplicationManager.getApplication().getService(CookieInputState.class);
            ApiPathState apiPathState = ApplicationManager.getApplication().getService(ApiPathState.class);

            ApiCacheSearch apiCacheSearch = new ApiCacheSearch(project, cookieState, apiPathState);
            tabbedPane.addTab("API Search Cache", apiCacheSearch.getMainJPanel());

            ApiSearch apiSearch = new ApiSearch(project, cookieState);
            tabbedPane.addTab("API Search", apiSearch.getMainJPanel());

            ApiMavenSearch apiMavenSearch = new ApiMavenSearch(project, cookieState);
            tabbedPane.addTab("API Maven Search", apiMavenSearch.getMainJPanel());

            OtherDevToolsPanel otherDevToolsPanel = new OtherDevToolsPanel(project);
            tabbedPane.addTab("Other Dev Tools", otherDevToolsPanel.getMainPanel());



//            contentPanel.setLayout(new BorderLayout(0, 20));
//            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
//            contentPanel.add(createCookieTool(toolWindow), BorderLayout.NORTH);
//            contentPanel.add(createUriTool(toolWindow), BorderLayout.CENTER);





        }


        public JPanel getContentPanel() {
            return mainPanel;
        }

    }

}
