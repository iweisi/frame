package com.lvmoney.oauth2.center.server.vo;

public enum RoleEnum {

    ROLE_USER("普通用户"),
    ROLE_ADMIN("管理员"),
    ROLE_SUPER("超级");

    private String meaning;

    public String getMeaning() {
        return meaning;
    }

    RoleEnum() {
    }

    RoleEnum(String meaning) {
        this.meaning = meaning;
    }
}
