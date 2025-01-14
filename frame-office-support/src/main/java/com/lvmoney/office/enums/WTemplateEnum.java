package com.lvmoney.office.enums;/**
 * 描述:
 * 包名:com.lvmoney.jwt.annotation
 * 版本信息: 版本1.0
 * 日期:2019/2/28
 * Copyright xxxx科技有限公司
 */


/**
 * @describe：
 * @author: lvmoney /xxxx科技有限公司
 * @version:v1.0 2018年10月30日 下午3:29:38
 */
public enum WTemplateEnum {
    TABLE("#"),
    STRING("\u0000"),
    PICTURE("@"),
    NUMBERIC("*");

    private String value;

    private WTemplateEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WTemplateEnum getByValue(String value) {
        WTemplateEnum[] wTemplateEnums = values();
        for (WTemplateEnum wTemplateEnum : wTemplateEnums) {
            if (wTemplateEnum.value.equals(value)) {
                return wTemplateEnum;
            }
        }
        return null;
    }


}
