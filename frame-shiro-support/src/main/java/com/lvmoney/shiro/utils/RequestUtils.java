package com.lvmoney.shiro.utils;

import com.lvmoney.shiro.vo.UserVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class RequestUtils {
    /**
     * 获取当前登录的用户，若用户未登录，则返回未登录 json
     *
     * @return
     */
    public static UserVo currentLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            Object principal = subject.getPrincipals().getPrimaryPrincipal();
            if (principal instanceof UserVo) {
                return (UserVo) principal;
            }
        }
        return null;
    }
}
