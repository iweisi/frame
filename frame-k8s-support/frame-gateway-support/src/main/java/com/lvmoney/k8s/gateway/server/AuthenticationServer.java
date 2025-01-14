package com.lvmoney.k8s.gateway.server;/**
 * 描述:
 * 包名:com.lvmoney.k8s.gateway.server
 * 版本信息: 版本1.0
 * 日期:2019/8/14
 * Copyright XXXXXX科技有限公司
 */


import com.lvmoney.common.utils.vo.ResultData;
import com.lvmoney.k8s.gateway.vo.resp.Oauth2TokenCheck;
import com.lvmoney.k8s.gateway.vo.resp.ShiroAuthorityCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @describe：
 * @author: lvmoney/XXXXXX科技有限公司
 * @version:v1.0 2019/8/14 8:41
 */
public interface AuthenticationServer {
    @GetMapping("/user/token/check")
    @ResponseBody
    ResultData<Oauth2TokenCheck> oauth2CheckToken();


    @GetMapping("/user/authority/check")
    @ResponseBody
    ResultData<ShiroAuthorityCheck> shiroCheckAuthority(@RequestParam(value = "servletPath") String servletPath);
}
