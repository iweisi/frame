package com.lvmoney.k8s.base.constant;/**
 * 描述:
 * 包名:com.lvmoney.k8s.base.constant
 * 版本信息: 版本1.0
 * 日期:2019/8/16
 * Copyright XXXXXX科技有限公司
 */


import static com.lvmoney.common.constant.CommonConstant.FILE_SEPARATOR;

/**
 * @describe：
 * @author: lvmoney/XXXXXX科技有限公司
 * @version:v1.0 2019/8/16 15:20
 */
public class BaseConstant {

    /**
     * yaml 文件后缀
     */
    public static final String YAML_SUFFIX = ".yaml";

    public static final String YAML_FILE_PATH = System.getProperty("user.dir") + FILE_SEPARATOR + "data" + FILE_SEPARATOR + "yaml";

    /**
     * 虚拟路径后缀
     */
    public static final String WEBSITE_SUFFIX = ".com";

    /**
     * 虚拟路径前缀
     */
    public static final String WEBSITE_PREFIX = "www.";

    /**
     * ingress 默认端口
     */
    public static final int INGRESS_DEFAULT_PORT = 80;

    /**
     * 虚拟服务映射路径默认为根
     */
    public static final String VIRTUAL_SERVICE_EXAC = "/";

    /**
     * 服务版本:v1
     */
    public static final String VERSION_V1 = "v1";
    /**
     * 服务版本:v2
     */
    public static final String VERSION_V2 = "v2";


    /**
     * 服务超时时间
     */
    public static final String ISTIO_SERVICE_TIMEOUT = "9s";
    /**
     * 每次尝试持续时间
     */
    public static final String ISTIO_SERVICE_PERTRYTIMEOUT = "3s";
    /**
     * 时间单位:秒
     */
    public static final String TIME_UNIT_S = "s";
    /**
     * 时间单位:毫秒
     */
    public static final String TIME_UNIT_MS = "ms";
}
