package com.lvmoney.k8s.base.enums;/**
 * 描述:
 * 包名:com.lvmoney.k8s.base.enums
 * 版本信息: 版本1.0
 * 日期:2019/8/19
 * Copyright XXXXXX科技有限公司
 */


/**
 * @describe：
 * @author: lvmoney/XXXXXX科技有限公司
 * @version:v1.0 2019/8/19 14:36
 */
public enum YamlType {
    IDeploy("IDeploy"),

    IGateway("IGateway"),
    IDestinationRule("IDestinationRule");
    private String value;

    YamlType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}