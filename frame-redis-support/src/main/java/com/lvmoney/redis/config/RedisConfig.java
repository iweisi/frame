package com.lvmoney.redis.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.lvmoney.redis.converter.FrameFastJsonRedisSerializer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @describe：redis序列化配置，如果不配置通过工具看redis中的数据会有乱码，但是也不会出现反序列化出错的问题（GenericFastJsonRedisSerializer）
 * @author: lvmoney /xxxx科技有限公司
 * @version:v1.0 2019年1月18日 上午11:22:38
 */
@Configuration
@EnableCaching//开启注解
public class RedisConfig extends CachingConfigurerSupport {
    //缓存管理器 spring boot 2.0后 配置缓存管理器 和2.0以前 不一样 根据自己的版本 配置
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisTemplate) {
        return RedisCacheManager.create(redisTemplate);
    }

    // 以下两种redisTemplate自由根据场景选择
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 配置redisTemplate
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        // set key serializer
        StringRedisSerializer serializer = new StringRedisSerializer();
        // 设置key序列化类，否则key前面会多了一些乱码
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        // fastjson serializer 如果用GenericFastJsonRedisSerializer，直接保存实体反序列化会报错.
        // 可以把对应的实体序列化（FastJsonUtil）后再保存就好了。
        // GenericFastJsonRedisSerializer fastSerializer = new GenericFastJsonRedisSerializer();
        /**
         * 忽略了@type字段，以免反序列化报错
         */
        FrameFastJsonRedisSerializer fastSerializer = new FrameFastJsonRedisSerializer();
        redisTemplate.setValueSerializer(fastSerializer);
        redisTemplate.setHashValueSerializer(fastSerializer);
        // 如果 KeySerializer 或者 ValueSerializer 没有配置，则对应的 KeySerializer、ValueSerializer 才使用这个 Serializer
        redisTemplate.setDefaultSerializer(fastSerializer);
        LettuceConnectionFactory factory = (LettuceConnectionFactory) connectionFactory;
        // factory
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        //开启事务支持
//        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
//        stringRedisTemplate.setEnableTransactionSupport(true);
        return stringRedisTemplate;
    }


}
