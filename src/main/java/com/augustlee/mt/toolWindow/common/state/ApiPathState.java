package com.augustlee.mt.toolWindow.common.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.augustlee.mt.toolWindow.common.util.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * API 路径状态管理
 * 用于持久化保存 API 路径配置信息
 *
 * @see ApiPathState
 * @author August Lee
 * @since 2025/11/28 10:09
 */
@State(name = "ApiPathState", storages = @Storage("apiPathState.xml"))
public class ApiPathState implements PersistentStateComponent<ApiPathState> {
    private String apiPathJson = "";

    @Nullable
    @Override
    public ApiPathState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ApiPathState state) {
        this.apiPathJson = state.apiPathJson;
    }

    public String getApiPathJson() {
        return apiPathJson;
    }

    public void setApiPathJson(String apiPathJson) {
        Config.COOKIE_NAME = apiPathJson;
        this.apiPathJson = apiPathJson;
    }
}