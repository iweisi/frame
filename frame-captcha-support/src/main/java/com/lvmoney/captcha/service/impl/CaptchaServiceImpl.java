package com.lvmoney.captcha.service.impl;/**
 * 描述:
 * 包名:com.lvmoney.captcha.service.impl
 * 版本信息: 版本1.0
 * 日期:2019/2/17
 * Copyright xxxx科技有限公司
 */


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.lvmoney.captcha.ro.ValidateCodeRo;
import com.lvmoney.captcha.service.CaptchaService;
import com.lvmoney.captcha.vo.ValidateCodeVo;
import com.lvmoney.captcha.vo.ValidateResultVo;
import com.lvmoney.common.exceptions.BusinessException;
import com.lvmoney.common.exceptions.CommonException;
import com.lvmoney.common.utils.FileUtil;
import com.lvmoney.common.utils.JsonUtil;
import com.lvmoney.common.utils.SnowflakeIdFactoryUtil;
import com.lvmoney.redis.service.BaseRedisService;
import com.revengemission.commons.captcha.core.VerificationCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @describe：
 * @author: lvmoney /xxxx科技有限公司
 * @version:v1.0 2018年10月30日 下午3:29:38
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    private final static Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    @Value("${customer.valid.expire:18000}")
    String expire;
    @Autowired
    BaseRedisService baseRedisService;

    @Autowired
    DefaultKaptcha defaultKaptcha;

    // 渲染随机背景颜色
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    //渲染固定背景颜色
    private Color getBgColor() {
        return new Color(200, 200, 200);
    }


    /**
     * 画验证码图形
     *
     * @param isDrawLine
     * @return
     */
    private ValidateCodeVo drawImg(boolean isDrawLine, int validCodeSize, int fc, int bc, String fontType) {
        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
//        if(validCodeSize<4||validCodeSize>8){//验证码的长度在4~到8个字符
//
//        }
        // 在内存中创建图象
        int width = 120, height = 18;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics g = image.getGraphics();
        Random random = new Random();
        // 设定背景色
        g.setColor(getRandColor(fc, bc));
        g.fillRect(0, 0, width, height);
        // 设定字体
        //g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        g.setFont(new Font(fontType, Font.PLAIN, 20));
        // 画边框
        //g.setColor(new Color());
        //g.drawRect(0,0,width-1,height-1);

        /**随机产生155条干扰线，使图象中的认证码不易被其它程序探测到*/

        if (isDrawLine) {
            g.setColor(getRandColor(120, 140));
            for (int i = 0; i < 155; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x, y, x + xl, y + yl);
            }
        }

        // 取随机产生的认证码(8位数字和字母混合)
        String sRand = "";
        String verCode = "";
        char[] seds = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (int i = 0; i < validCodeSize; i++) {
            int index = random.nextInt(seds.length);
            char cc = seds[index];
            if ((i + 1) % 2 == 0) {
                verCode += cc;
                g.setColor(new Color(255, 0, 0));
            } else {
                g.setColor(new Color(0, 0, 0));
            }
            sRand += cc;
            // 将认证码随机打印不同的颜色显示出来
            //g.setColor(new Color(20+random.nextInt(110),20+random.nextInt(110),20+random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(cc + "", 13 * i + 6, 16);
        }
        // 图象生效
        g.dispose();
        validateCodeVo.setBufferedImage(image);
        validateCodeVo.setValue(sRand);
        return validateCodeVo;
    }

    public ValidateResultVo encodeBase64ImgCode(boolean is2Redis, boolean isDrawLine, int validCodeSize, int fc, int bc, String fontType) {
        ValidateCodeVo validateCodeVo = drawImg(isDrawLine, validCodeSize, fc, bc, fontType);
        BufferedImage codeImg = validateCodeVo.getBufferedImage();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            boolean flag = ImageIO.write(codeImg, "JPEG", out);
        } catch (IOException e) {
            logger.error("生成验证码报错:{}", e.getMessage());
            throw new BusinessException(CommonException.Proxy.VALIDCOE_ERROR);
        }
        ValidateResultVo validateResultVo = new ValidateResultVo();
        if (is2Redis) {
            SnowflakeIdFactoryUtil idWorker = new SnowflakeIdFactoryUtil(1, 2);
            ValidateCodeRo validateCodeRo = new ValidateCodeRo();
            String serialNumber = String.valueOf(idWorker.nextId());
            validateResultVo.setSerialNumber(serialNumber);
            validateCodeRo.setValue(validateCodeVo.getValue());
            validateCodeRo.setSerialNumber(serialNumber);
            validateCodeRo.setExpire(Long.parseLong(expire));
            this.saveValidaCode2Redis(validateCodeRo);
        }
        byte[] b = out.toByteArray();
        validateResultVo.setCode(FileUtil.file2Base64(b));
        validateResultVo.setValue(validateCodeVo.getValue());
        return validateResultVo;
    }

    @Override
    public ValidateResultVo encodeBase64ImgCode() {
        String createText = defaultKaptcha.createText();
        // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage challenge = defaultKaptcha.createImage(createText);
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(challenge, "jpg", jpegOutputStream);
            byte[] b = jpegOutputStream.toByteArray();
            ValidateResultVo validateResultVo = new ValidateResultVo();
            validateResultVo.setCode(FileUtil.file2Base64(b));
            validateResultVo.setValue(createText);
            SnowflakeIdFactoryUtil idWorker = new SnowflakeIdFactoryUtil(1, 2);
            ValidateCodeRo validateCodeRo = new ValidateCodeRo();
            String serialNumber = String.valueOf(idWorker.nextId());
            validateResultVo.setSerialNumber(serialNumber);
            validateCodeRo.setValue(createText);
            validateCodeRo.setSerialNumber(serialNumber);
            validateCodeRo.setExpire(Long.parseLong(expire));
            this.saveValidaCode2Redis(validateCodeRo);
            return validateResultVo;
        } catch (IOException e) {
            logger.error("生成验证码报错:{}", e.getMessage());
            throw new BusinessException(CommonException.Proxy.VALIDCOE_ERROR);
        }
    }

    @Override
    public ValidateResultVo getCaptcha(int width, int height, int length) {
        ValidateResultVo validateResultVo = new ValidateResultVo();
        String captcha = VerificationCodeUtil.generateVerificationCode(length, null);
        validateResultVo.setValue(captcha);
        try {
            String base64EncodedGraph = VerificationCodeUtil.outputImage(width, height, captcha);
            validateResultVo.setCode(base64EncodedGraph);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SnowflakeIdFactoryUtil idWorker = new SnowflakeIdFactoryUtil(1, 2);
        String serialNumber = String.valueOf(idWorker.nextId());
        validateResultVo.setSerialNumber(serialNumber);
        ValidateCodeRo validateCodeRo = new ValidateCodeRo();
        BeanUtils.copyProperties(validateResultVo, validateCodeRo);
        validateCodeRo.setExpire(Long.parseLong(expire));
        saveValidaCode2Redis(validateCodeRo);
        return validateResultVo;
    }


    @Override
    public ValidateCodeRo getValidate(String serialNumber) {
        Object obj = baseRedisService.getString(serialNumber);
        ValidateCodeRo validateCodeRo = JSON.parseObject(obj.toString(), new TypeReference<ValidateCodeRo>() {
        });
        return validateCodeRo;
    }

    @Override
    public void deleteValidate(String serialNumber) {
        baseRedisService.deleteKey(serialNumber);
    }

    @Override
    public void saveValidaCode2Redis(ValidateCodeRo validateCodeRo) {
        baseRedisService.set(validateCodeRo.getSerialNumber(), JsonUtil.t2JsonString(validateCodeRo), validateCodeRo.getExpire());
    }

}
