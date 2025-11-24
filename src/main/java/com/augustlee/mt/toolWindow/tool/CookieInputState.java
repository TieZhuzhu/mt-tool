package com.augustlee.mt.toolWindow.tool;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "CookieInputState", storages = @Storage("cookieInputState.xml"))
public class CookieInputState implements PersistentStateComponent<CookieInputState> {
    private String cookieContent = "";

    @Nullable
    @Override
    public CookieInputState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CookieInputState state) {
        this.cookieContent = state.cookieContent;
        // 同步更新 Config.COOKIE_NAME，确保加载的 Cookie 能够被使用
        if (this.cookieContent != null && !this.cookieContent.isEmpty()) {
            Config.COOKIE_NAME = this.cookieContent;
        }
    }

    public String getCookieContent() {
        return cookieContent;
    }

    public void setCookieContent(String cookieContent) {
        Config.COOKIE_NAME = cookieContent;
        this.cookieContent = cookieContent;
    }
}