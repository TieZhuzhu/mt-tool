package com.augustlee.mt.toolWindow.common.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.util.function.Consumer;

/**
 * Cookie 获取辅助对话框
 * <p>
 * 通过三步引导用户在浏览器中获取 Cookie 并填入插件：
 * 1. 打开目标登录页
 * 2. 在浏览器 Console 执行 JS 代码将 Cookie 复制到剪贴板
 * 3. 插件读取剪贴板自动填入
 * <p>
 * 100% 兼容所有 IntelliJ 版本，无需 JCEF 或额外依赖。
 *
 * @author August Lee
 */
public class CookieHelperDialog extends DialogWrapper {

    private static final String JS_SNIPPET = "copy(document.cookie)";

    private final Project project;
    private final String loginUrl;
    private final Consumer<String> cookieFiller;

    /** 自动检测剪贴板的轮询定时器 */
    private Timer clipboardTimer;

    /** 上一次读取到的剪贴板内容，用于去重 */
    private String lastClipboardContent = "";

    public CookieHelperDialog(Project project, String loginUrl, Consumer<String> cookieFiller) {
        super(project);
        this.project = project;
        this.loginUrl = loginUrl;
        this.cookieFiller = cookieFiller;
        setTitle("获取 Cookie 操作指引");
        setOKButtonText("关闭");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.setPreferredSize(new Dimension(480, 0));

        root.add(buildStep1Panel());
        root.add(Box.createVerticalStrut(12));
        root.add(buildStep2Panel());
        root.add(Box.createVerticalStrut(12));
        root.add(buildStep3Panel());

        return root;
    }

    /**
     * 步骤一：打开登录页
     */
    private JPanel buildStep1Panel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("步骤 1：打开登录页并完成登录");
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        JButton openBtn = new JButton("打开登录页");
        openBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(loginUrl));
            } catch (Exception ex) {
                Messages.showErrorDialog(project, "无法打开浏览器: " + ex.getMessage(), "打开失败");
            }
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrapper.add(openBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(btnWrapper, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 步骤二：复制 JS 代码
     */
    private JPanel buildStep2Panel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("步骤 2：登录后按 F12 打开 Console，执行以下代码");
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        JTextField codeField = new JTextField(JS_SNIPPET);
        codeField.setEditable(false);
        codeField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        codeField.setBackground(UIManager.getColor("TextField.inactiveBackground"));

        JButton copyCodeBtn = new JButton("复制代码");
        copyCodeBtn.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(JS_SNIPPET), null);
            copyCodeBtn.setText("已复制 ✓");
            Timer resetTimer = new Timer(1500, ev -> copyCodeBtn.setText("复制代码"));
            resetTimer.setRepeats(false);
            resetTimer.start();
        });

        JPanel codeRow = new JPanel(new BorderLayout(6, 0));
        codeRow.add(codeField, BorderLayout.CENTER);
        codeRow.add(copyCodeBtn, BorderLayout.EAST);

        JLabel hint = new JLabel("<html><font color='gray'>执行后 Cookie 会自动复制到剪贴板</font></html>");

        panel.add(title, BorderLayout.NORTH);
        panel.add(codeRow, BorderLayout.CENTER);
        panel.add(hint, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * 步骤三：填入 Cookie
     */
    private JPanel buildStep3Panel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("步骤 3：执行代码后，点击下方按钮填入 Cookie");
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        JButton fillBtn = new JButton("填入 Cookie");
        fillBtn.setForeground(new Color(0, 120, 0));

        JCheckBox autoDetectBox = new JCheckBox("自动检测剪贴板", true);

        fillBtn.addActionListener(e -> fillCookieFromClipboard());

        autoDetectBox.addActionListener(e -> {
            if (autoDetectBox.isSelected()) {
                startClipboardPolling(fillBtn);
            } else {
                stopClipboardPolling();
            }
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionRow.add(fillBtn);
        actionRow.add(Box.createHorizontalStrut(12));
        actionRow.add(autoDetectBox);

        panel.add(title, BorderLayout.NORTH);
        panel.add(actionRow, BorderLayout.CENTER);

        // 默认启动自动检测
        SwingUtilities.invokeLater(() -> startClipboardPolling(fillBtn));

        return panel;
    }

    /**
     * 从剪贴板读取 Cookie 并填入
     */
    private void fillCookieFromClipboard() {
        String content = readClipboard();
        if (content == null || content.trim().isEmpty()) {
            Messages.showErrorDialog(project, "剪贴板为空，请先在浏览器 Console 执行代码", "未检测到 Cookie");
            return;
        }
        if (!looksLikeCookie(content)) {
            Messages.showErrorDialog(project, "剪贴板内容格式不像 Cookie，请确认已执行 copy(document.cookie)", "格式异常");
            return;
        }
        doFill(content.trim());
    }

    /**
     * 启动剪贴板轮询定时器
     */
    private void startClipboardPolling(JButton fillBtn) {
        stopClipboardPolling();
        lastClipboardContent = readClipboard();
        clipboardTimer = new Timer(500, e -> {
            String current = readClipboard();
            if (current != null && !current.equals(lastClipboardContent) && looksLikeCookie(current)) {
                lastClipboardContent = current;
                stopClipboardPolling();
                int choice = Messages.showYesNoDialog(
                        project,
                        "检测到剪贴板中有 Cookie 内容，是否立即填入？",
                        "检测到 Cookie",
                        Messages.getQuestionIcon()
                );
                if (choice == Messages.YES) {
                    doFill(current.trim());
                }
            } else if (current != null) {
                lastClipboardContent = current;
            }
        });
        clipboardTimer.start();
    }

    /**
     * 停止剪贴板轮询
     */
    private void stopClipboardPolling() {
        if (clipboardTimer != null && clipboardTimer.isRunning()) {
            clipboardTimer.stop();
        }
    }

    /**
     * 执行填入并关闭对话框
     */
    private void doFill(String cookie) {
        SwingUtilities.invokeLater(() -> {
            cookieFiller.accept(cookie);
            close(OK_EXIT_CODE);
        });
    }

    /**
     * 读取系统剪贴板文本内容
     */
    private String readClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                return (String) clipboard.getData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 简单判断内容是否像 Cookie 格式（包含 key=value 对）
     */
    private boolean looksLikeCookie(String content) {
        if (content == null || content.length() < 5) {
            return false;
        }
        return content.contains("=") && !content.trim().startsWith("{") && !content.trim().startsWith("[");
    }

    @Override
    protected void dispose() {
        stopClipboardPolling();
        super.dispose();
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }
}
