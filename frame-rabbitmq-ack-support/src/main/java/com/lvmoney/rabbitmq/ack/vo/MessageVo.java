package com.lvmoney.rabbitmq.ack.vo;/**
 * 描述:
 * 包名:com.lvmoney.jwt.annotations
 * 版本信息: 版本1.0
 * 日期:2019/1/21
 * Copyright xxxx科技有限公司
 */


import java.io.Serializable;

/**
 * @describe：
 * @author: lvmoney /xxxx科技有限公司
 * @version:v1.0 2018年10月30日 下午3:29:38
 */
public class MessageVo<T> implements Serializable {
    private String msgType;
    private T t;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    /**
     * 发送时间
     */

    private long date;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

}
