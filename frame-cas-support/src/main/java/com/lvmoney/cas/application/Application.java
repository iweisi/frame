/**
 * 描述:源数据程序入口
 * 包名:com.lvmoney.pay.gateway.application
 * 版本信息: 版本1.0
 * 日期:2018年9月30日  上午8:51:33
 * Copyright xxxx科技有限公司
 */

package com.lvmoney.cas.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.unicon.cas.client.configuration.EnableCasClient;

/**
 * @describe：数据检索，springboot主方法
 * @author: lvmoney /xxxx科技有限公司
 * @version:v1.0 2018年9月30日 上午8:51:33
 */
@SpringBootApplication(scanBasePackages = {"com.lvmoney"})
@EnableCasClient//开启cas
public class Application {

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }
}