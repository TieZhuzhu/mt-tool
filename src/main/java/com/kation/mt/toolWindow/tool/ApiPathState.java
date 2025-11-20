package com.kation.mt.toolWindow.tool;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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