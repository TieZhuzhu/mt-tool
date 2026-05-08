package com.augustlee.mt.toolWindow.mws.dto;

/**
 * 方法反查接口地址结果。
 *
 * @author August Lee
 * @since 2026/05/08
 */
public class ApiMethodSearchResultDTO {

    /**
     * 接口地址。
     */
    private final String path;

    /**
     * 命中的入口类全限定名。
     */
    private final String entryServiceName;

    /**
     * 命中的入口方法名。
     */
    private final String entryMethodName;

    /**
     * 命中深度。
     * 0 表示直接命中，大于 0 表示上钻命中。
     */
    private final int depth;

    /**
     * 是否直接命中。
     */
    private final boolean directMatch;

    public ApiMethodSearchResultDTO(String path, String entryServiceName, String entryMethodName, int depth, boolean directMatch) {
        this.path = path;
        this.entryServiceName = entryServiceName;
        this.entryMethodName = entryMethodName;
        this.depth = depth;
        this.directMatch = directMatch;
    }

    public String getPath() {
        return path;
    }

    public String getEntryServiceName() {
        return entryServiceName;
    }

    public String getEntryMethodName() {
        return entryMethodName;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isDirectMatch() {
        return directMatch;
    }

    /**
     * 获取入口方法展示文案。
     *
     * @return 入口方法展示文案
     */
    public String getEntryMethodDisplayText() {
        return this.entryServiceName + "#" + this.entryMethodName;
    }

    /**
     * 获取入口方法短名展示文案。
     *
     * @return 入口方法短名展示文案
     */
    public String getEntryMethodShortDisplayText() {
        String simpleClassName = this.entryServiceName;
        int lastDotIndex = simpleClassName == null ? -1 : simpleClassName.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < simpleClassName.length() - 1) {
            simpleClassName = simpleClassName.substring(lastDotIndex + 1);
        }
        return simpleClassName + "#" + this.entryMethodName;
    }

    /**
     * 获取命中方式展示文案。
     *
     * @return 命中方式展示文案
     */
    public String getHitTypeDisplayText() {
        if (this.directMatch) {
            return "直接命中";
        }
        return "上钻第 " + this.depth + " 层命中";
    }
}
