package com.augustlee.mt.toolWindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * 工具窗口工厂类
 * 作为整个工具窗口的唯一入口，负责加载和管理各个功能模块
 *
 * @see CalendarToolWindowFactory
 * @author August Lee
 * @since 2025/11/28 10:09
 */
final class CalendarToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CalendarToolWindowContent toolWindowContent = new CalendarToolWindowContent(project);
        Content content = ContentFactory.getInstance()
                .createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
