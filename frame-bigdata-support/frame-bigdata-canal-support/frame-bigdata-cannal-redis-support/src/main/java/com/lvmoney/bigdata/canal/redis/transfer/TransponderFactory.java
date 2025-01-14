package com.lvmoney.bigdata.canal.redis.transfer;
/**
 * 描述:
 * 包名:com.lvmoney.common.config
 * 版本信息: 版本1.0
 * 日期:2019/2/28
 * Copyright xxxx科技有限公司
 */

import com.alibaba.otter.canal.client.CanalConnector;
import com.lvmoney.bigdata.canal.redis.annotation.CanalEventListener;
import com.lvmoney.bigdata.canal.redis.vo.ListenerPointVo;
import com.lvmoney.bigdata.canal.redis.properties.CanalProp;


import java.util.List;
import java.util.Map;


/**
 * @describe：信息转换工厂类接口层,显示声明为函数式接口可用兰姆达表达式
 * @author: lvmoney/xxxx科技有限公司
 * @version:v1.0 2019/2/28 10:05
 */
@FunctionalInterface
public interface TransponderFactory {

    /**
     * @param connector     canal 连接工具
     * @param config        canal 链接信息
     * @param listeners     实现接口的监听器
     * @param annoListeners 注解监听拦截
     * @return
     * @author lvmoney
     * @time 2018/5/28 14:43
     */
    MessageTransponder newTransponder(CanalConnector connector, Map.Entry<String, CanalProp.Instance> config, List<CanalEventListener> listeners, List<ListenerPointVo> annoListeners);
}
