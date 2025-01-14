/**
 * 描述:
 * 包名:com.lvmoney.redis.service
 * 版本信息: 版本1.0
 * 日期:2019年1月7日  下午5:18:00
 * Copyright xxxx科技有限公司
 */

package com.lvmoney.redis.service.impl;

import com.lvmoney.common.utils.JsonUtil;
import com.lvmoney.common.vo.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import com.lvmoney.common.exceptions.BusinessException;
import com.lvmoney.common.exceptions.CommonException;
import com.lvmoney.redis.service.BaseRedisService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @describe：
 * @author: lvmoney /xxxx科技有限公司
 * @version:v1.0 2019年1月7日 下午5:18:00
 */

@SuppressWarnings("rawtypes")
@Service("frameBaseRedisService")
public class BaseRedisServiceImpl implements BaseRedisService {
    private final static Logger logger = LoggerFactory.getLogger(BaseRedisServiceImpl.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Resource(name = "stringRedisTemplate")
    ValueOperations<String, String> valOpsStr;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    ValueOperations<Object, Object> valOpsObj;

    public void set(String key, Object object, Long time) {
        // 让该方法能够支持多种数据类型存放
        if (object instanceof String) {
            setString(key, object);
        }
        // 如果存放时Set类型
        if (object instanceof Set) {
            setSet(key, object);
        }
        // 设置有效期

        if (time != null && time > 0l) {
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    public void setString(String key, Object object) {
        if (object instanceof String) {
            stringRedisTemplate.opsForValue().set(key, object.toString());
        } else {
            String value = JsonUtil.t2JsonString(object);
            // 存放string类型
            stringRedisTemplate.opsForValue().set(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public void setSet(String key, Object object) {
        Set<String> valueSet = (Set<String>) object;
        for (String string : valueSet) {
            stringRedisTemplate.opsForSet().add(key, string);
        }
    }

    @Override
    public void setExpire(String key, Long time) {
        stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    public Object getString(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("从redis中获取数据报错:{}", e.getMessage());
            throw new BusinessException(CommonException.Proxy.REDIS_NOT_EXSIT);
        }
    }

    public void deleteKey(String key) {
        stringRedisTemplate.delete(key);
    }

    public void deleteWildcardKey(String key) {
        Set<Object> keys = redisTemplate.keys(key + "*");
        redisTemplate.delete(keys);
    }

    @SuppressWarnings("unchecked")
    public Page getListPage(Page page, String key) {
        if (page.isAll()) {
            List list = this.getListAll(key);
            Long total = this.getListSize(key);
            page.setData(list);
            page.setTotal(total);
            return page;
        }
        if (StringUtils.isBlank(key)) {
            throw new BusinessException(CommonException.Proxy.REDIS_KEY_IS_REQUIRED);
        }
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        int start = (pageNo - 1) * pageSize;
        int end = pageSize * pageNo - 1;
        List list = redisTemplate.opsForList().range(key, start, end);
        Long total = this.getListSize(key);
        page.setData(list);
        page.setTotal(total);
        return page;
    }

    public void delObj(Object o) {
        redisTemplate.delete(o);
    }

    @Override
    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public void rmValueByList(String key, Long count, Object obj) {
        redisTemplate.opsForList().remove(key, count, obj);
    }

    @Override
    public Long getMapSize(String key) {
        return Long.parseLong(String.valueOf(redisTemplate.opsForHash().values(key).size()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addList(String key, List obj, Long time) {
        redisTemplate.opsForList().rightPushAll(key, obj);
        if (time != null && time > 0l) {
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }

    }

    @Override
    public void addList(String key, List obj) {
        redisTemplate.opsForList().rightPushAll(key, obj);
    }

    @Override
    public void addMap(String key, Map obj, Long time) {
        redisTemplate.opsForHash().putAll(key, obj);
        if (time != null && time > 0l) {
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }


    @Override
    public Object getValueByMapKey(String key, String mapKey) {
        return redisTemplate.opsForHash().get(key, mapKey);
    }


    @Override
    public Object getMapByKey(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    @Override
    public Page getValueByKey(Page page, String key) {
        if (page.isAll()) {
            page.setTotal(this.getMapSize(key));
            page.setData(redisTemplate.opsForHash().values(key));
            return page;
        } else {
            Long total = this.getMapSize(key);
            page.setTotal(total);
            int pageNo = page.getPageNo();
            int pageSize = page.getPageSize();
            int start = (pageNo - 1) * page.getPageSize();
            int end = pageNo * pageSize;
            if (start > total) {
                page.setData(null);
                return page;
            }
            if (end > total) {
                end = Integer.parseInt(String.valueOf(total));
            }
            page.setData(redisTemplate.opsForHash().values(key).subList(start, end));
            return page;
        }

    }

    @Override
    public boolean isExistMapKey(String key, String mapKey) {
        return redisTemplate.opsForHash().hasKey(key, mapKey);
    }

    @Override
    public Long deleteValueByMapKey(String key, String... mapKey) {
        return redisTemplate.opsForHash().delete(key, mapKey);
    }

    @Override
    public List getListAll(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void renameKey(String key, String newKey) {
        redisTemplate.rename(key, newKey);
    }

    public void flushdb() {
        redisTemplate.execute(new RedisCallback<Object>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "success";
            }
        });
    }


    //    @Transactional           //哪怕加了这个注解spring的配置文件里redistemplate配置也要开启事务支持
    public void mutli() {
        flushdb();
        ValueOperations<Object, Object> vo = redisTemplate.opsForValue();
        redisTemplate.setEnableTransactionSupport(true);

        redisTemplate.multi();
        vo.set("b", "1");
        vo.increment("b", 2);
        vo.get("b");
        redisTemplate.discard();

        redisTemplate.multi();
        vo.set("a", "1");
        vo.increment("a", 2);
        vo.get("a");
        redisTemplate.exec();
        // System.out.println("-------");

        redisTemplate.setEnableTransactionSupport(false);
        List<Object> rs = null;
        do {
            redisTemplate.watch("a");
            redisTemplate.multi();
            vo.increment("a", 2);
            vo.increment("a", 2);
            rs = redisTemplate.exec();
        } while (rs == null);//多重检测，直到执行成功。


    }


}
