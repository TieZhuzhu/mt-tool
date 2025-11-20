package com.kation.mt.toolWindow.mws.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GroupEnum {


    SGB2B_SEASHOP_SDC_OPEN_API(55523, "sgb2b_seashop_sdc_open_api", "/sdc-gw1/openapi", "闪电帮帮开放api"),
    SGB2B_SEASHOP_SDC_SELLER_API(54237, "sgb2b_seashop_sdc_seller_api", "/sdc-gw1/sellerApi", "闪电帮帮卖家端api"),
    SGB2B_SEASHOP_SDC_MALL_API(54236, "sgb2b_seashop_sdc_mall_api", "/sdc-gw1/mallApi", "闪电帮帮买家端api"),
    SGB2B_SEASHOP_SDC_MANAGER_API(54235, "sgb2b_seashop_sdc_manager_api", "/sdc-gw1/mApi", "闪电帮帮M端api"),
    SGB2B_SEASHOP_MALL_API_2(50256, "sgb2b_seashop_mall_api_2", "/mallApi", "闪电仓买家api"),
    SGB2B_SEASHOP_OPEN_API_1(50243, "sgb2b_seashop_open_api_1", "/openapi", "闪电仓开放api"),
    SGB2B_SEASHOP_SELLER_API_2(48254, "sgb2b_seashop_seller_api_2", "/sellerApi", "闪电仓卖家端API-分组2"),
    SGB2B_SEASHOP_MALL_API_1(40456, "sgb2b_seashop_mall_api_1", "/mallApi", "闪电仓买家api"),
    SGB2B_SEASHOP_OPEN_API_JST(37129, "sgb2b_seashop_open_api_jst", "/JstNotify", "闪电仓回调-聚水潭"),
    SGB2B_SEASHOP_OPEN_API_WDGJ(37128, "sgb2b_seashop_open_api_wdgj", "/Wdgj_Api", "闪电仓回调-菠萝派"),
    SGB2B_SEASHOP_OPEN_API_WDT(37125, "sgb2b_seashop_open_api_wdt", "/WDT_API", "闪电仓回调-旺店通"),
    SGB2B_SEASHOP_SELLER_API_1(36302, "sgb2b_seashop_seller_api_1", "/sellerApi", "闪电仓卖家端API-分组1"),
    SGB2B_SEASHOP_MANAGER_API_1(36301, "sgb2b_seashop_manager_api_1", "/mApi", "闪电仓平台API-分组1"),
    SGB2B_SEASHOP_OPEN_API(36198, "sgb2b_seashop_open_api", "/mtapi", "闪电仓开放网关-主要对接牵牛花"),
    SGB2B_SEASHOP_MANAGER_API(35119, "sgb2b_seashop_manager_api", "/mApi", "闪电仓M端"),
    SGB2B_SEASHOP_SELLER_API(35118, "sgb2b_seashop_seller_api", "/sellerApi", "闪电仓卖家端"),
    SGB2B_SEASHOP_MALL_API(34970, "sgb2b_seashop_mall_api", "/mallApi", "闪电仓买家api");

    private final int id;
    private final String name;
    private final String commonPrefix;
    private final String description;

    GroupEnum(int id, String name, String commonPrefix, String description) {
        this.id = id;
        this.name = name;
        this.commonPrefix = commonPrefix;
        this.description = description;
    }

    public static List<GroupEnum> getByCommonPrefix(String path){
        return Arrays.stream(GroupEnum.values())
                .filter(groupEnum -> path.startsWith(groupEnum.getCommonPrefix()))
                .collect(Collectors.toList());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCommonPrefix() {
        return commonPrefix;
    }

    public String getDescription() {
        return description;
    }


}
