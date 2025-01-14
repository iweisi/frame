package com.lvmoney.common.utils;/**
 * 描述:
 * 包名:com.lvmoney.common.utils
 * 版本信息: 版本1.0
 * 日期:2019/8/20
 * Copyright XXXXXX科技有限公司
 */


import com.lvmoney.common.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.regex.Pattern;

/**
 * @describe：
 * @author: lvmoney/XXXXXX科技有限公司
 * @version:v1.0 2019/8/20 9:01
 */
public class IpUtil {
    private final static Logger logger = LoggerFactory.getLogger(IpUtil.class);

    /**
     * @describe:判断ip是否属于某个网段
     * @param: [network, mask]
     * @return: boolean
     * @author: lvmoney /XXXXXX科技有限公司
     * 2019/8/20 9:06
     */
    public static boolean isInRange(String network, String mask) {
        String[] networkips = network.split("\\.");
        int ipAddr = (Integer.parseInt(networkips[0]) << 24)
                | (Integer.parseInt(networkips[1]) << 16)
                | (Integer.parseInt(networkips[2]) << 8)
                | Integer.parseInt(networkips[3]);
        int type = Integer.parseInt(mask.replaceAll(".*/", ""));
        int mask1 = 0xFFFFFFFF << (32 - type);
        String maskIp = mask.replaceAll("/.*", "");
        String[] maskIps = maskIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(maskIps[0]) << 24)
                | (Integer.parseInt(maskIps[1]) << 16)
                | (Integer.parseInt(maskIps[2]) << 8)
                | Integer.parseInt(maskIps[3]);

        return (ipAddr & mask1) == (cidrIpAddr & mask1);
    }

    /**
     * @describe: 判断是不是ip
     * @param: [ip]
     * @return: boolean
     * @author: lvmoney /XXXXXX科技有限公司
     * 2019/8/20 9:06
     */
    public static boolean isIp(String ip) {
        String ipReg = "^(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.)(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.){2}([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))$";// ip的正则表达式
        Pattern ipPattern = Pattern.compile(ipReg);
        boolean flag = ipPattern.matcher(ip).matches();
        return flag;
    }


    public static String getLocalhostIp() {
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();
            String localip = ia.getHostAddress();
            return localip;
        } catch (Exception e) {
            logger.error("获取本机ip地址报错:{}", e.getMessage());
            return CommonConstant.LOCALHOST_IP;
        }
    }

    public static void main(String[] args) {
        System.out.println(isInRange("10.20.10.69", "10.20.10.0/16"));
        System.out.println(isInRange("10.168.1.100", "10.168.0.224/21"));
        System.out.println(isInRange("192.168.0.1", "192.168.0.0/24"));
        System.out.println(isInRange("10.168.0.0", "10.168.0.0/32"));


    }

    /**
     * @describe: 获得request 的真实ip
     * @param: [request]
     * @return: java.lang.String
     * @author: lvmoney /XXXXXX科技有限公司
     * 2019/8/20 10:08
     */
    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }
}
