package com.lvmoney.oauth2.center.server.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lvmoney.common.exceptions.BusinessException;
import com.lvmoney.oauth2.center.server.exception.Oauth2Exception;
import com.lvmoney.oauth2.center.server.service.Oauth2RedisService;
import com.lvmoney.oauth2.center.server.service.UserAccountService;
import com.lvmoney.oauth2.center.server.vo.UserAccountVo;
import com.lvmoney.oauth2.center.server.vo.UserInfo;
import com.lvmoney.oauth2.center.server.vo.resp.UserMeRespVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ProfileController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+)=*$");

    @Autowired
    Oauth2RedisService oauth2RedisService;

    @Autowired
    TokenStore tokenStore;

    @ResponseBody
    @RequestMapping("/user/me")
    public UserMeRespVo info(@RequestParam(value = "access_token", required = false) String paramToken,
                             @RequestHeader(value = "Authorization", required = false) String headerToken,
                             @CookieValue(value = "access_token", required = false) String cookieToken) {
        UserMeRespVo userMeRespVo = new UserMeRespVo();
        try {
            String token = null;
            if (StringUtils.isNoneBlank(headerToken)) {
                Matcher matcher = authorizationPattern.matcher(headerToken);
                if (matcher.matches()) {
                    token = matcher.group("token");
                }
            }

            if (token == null && StringUtils.isNoneBlank(paramToken)) {
                token = paramToken;
            }

            if (token == null && StringUtils.isNoneBlank(cookieToken)) {
                token = cookieToken;
            }

            if (token != null) {
                OAuth2AccessToken auth2AccessToken = tokenStore.readAccessToken(token);
                if (auth2AccessToken.isExpired()) {
                    throw new BusinessException(Oauth2Exception.Proxy.OAUTH2_TOKEM_EXPIRED);
                }

                String username = auth2AccessToken.getAdditionalInformation().get("username").toString();
                UserInfo userInfo = oauth2RedisService.getOauth2UserVo(username);
                userMeRespVo.setUsername(username);
                userMeRespVo.setGender(userInfo.getGender());
                userMeRespVo.setNickname(userInfo.getNickname());
                userMeRespVo.setGrantType(auth2AccessToken.getAdditionalInformation().get("grantType").toString());
//                userMeRespVo.setUserId(userAccountVo.getId());
//                userMeRespVo.setAuthorities(auth2AccessToken.getAdditionalInformation().get("authorities").toString());
//                userMeRespVo.setStatus(1);
            } else {
                throw new BusinessException(Oauth2Exception.Proxy.OAUTH2_ACCESS_TOKEM_REQUIRED);
            }
        } catch (Exception e) {
            LOGGER.error("校验access_token报错:{}", e.getMessage());
            throw new BusinessException(Oauth2Exception.Proxy.OAUTH2_TOKEM_EXPIRED);

        }

        return userMeRespVo;
    }


}
