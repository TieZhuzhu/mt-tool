package com.augustlee.mt.toolWindow.code.art.enums;

/**
 * Maven组件名称枚举
 *
 * @see ComponentNameEnum
 * @author August Lee
 * @since 2025/11/21 17:26
 */
public enum ComponentNameEnum {

    // Gateway 模块
    SEASHOP_MALL("com.meituan.shangou:seashop-mallapi", "seashop-mall"),
    SEASHOP_MALL_API("com.meituan.shangou:seashop-mallapi", "seashop-mall-api"),
    SEASHOP_M("com.meituan.shangou:seashop-mapi", "seashop-m"),
    SEASHOP_M_API("com.meituan.shangou:seashop-mapi", "seashop-m-api"),
    SEASHOP_SELLER("com.meituan.shangou:seashop-sellerapi", "seashop-seller"),
    SEASHOP_SELLER_API("com.meituan.shangou:seashop-sellerapi", "seashop-sellerapi"),

    // Service-App 模块
    SEASHOP_BASE("com.meituan.shangou:seashop-base", "seashop-base"),
    SEASHOP_BASE_API("com.meituan.shangou:seashop-base-api", "seashop-base-api"),
    SEASHOP_ERP("com.meituan.shangou:seashop-erp", "seashop-erp"),
    SEASHOP_ERP_API("com.meituan.shangou:seashop-erp-api", "seashop-erp-api"),
    SEASHOP_ORDER("com.meituan.shangou:seashop-order", "seashop-order"),
    SEASHOP_ORDER_API("com.meituan.shangou:seashop-order-api", "seashop-order-api"),
    SEASHOP_PAY("com.meituan.shangou:seashop-pay", "seashop-pay"),
    SEASHOP_PAY_API("com.meituan.shangou:seashop-pay-api", "seashop-pay-api"),
    SEASHOP_PRODUCT("com.meituan.shangou:seashop-product", "seashop-product"),
    SEASHOP_PRODUCT_API("com.meituan.shangou:seashop-product-api", "seashop-product-api"),
    SEASHOP_PROMOTION("com.meituan.shangou:seashop-promotion", "seashop-promotion"),
    SEASHOP_PROMOTION_API("com.meituan.shangou:seashop-promotion-api", "seashop-promotion-api"),
    SEASHOP_TRADE("com.meituan.shangou:seashop-trade", "seashop-trade"),
    SEASHOP_TRADE_API("com.meituan.shangou:seashop-trade-api", "seashop-trade-api"),
    SEASHOP_USER("com.meituan.shangou:seashop-user", "seashop-user"),
    SEASHOP_USER_API("com.meituan.shangou:seashop-user-api", "seashop-user-api");

    private final String fullName;
    private final String displayName;

    ComponentNameEnum(String fullName, String displayName) {
        this.fullName = fullName;
        this.displayName = displayName;
    }

    /**
     * 获取完整的组件名称（用于API查询）
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * 获取显示名称（用于下拉框显示）
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据显示名称查找枚举
     */
    public static ComponentNameEnum findByDisplayName(String displayName) {
        for (ComponentNameEnum component : values()) {
            if (component.getDisplayName().equals(displayName)) {
                return component;
            }
        }
        return null;
    }

    /**
     * 根据完整名称查找枚举
     */
    public static ComponentNameEnum findByFullName(String fullName) {
        for (ComponentNameEnum component : values()) {
            if (component.getFullName().equals(fullName)) {
                return component;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

