/**
 * 描述:
 * 包名:com.lvmoney.common.utils
 * 版本信息: 版本1.0
 * 日期:2019年1月10日  下午3:49:46
 * Copyright xxxx科技有限公司
 */

package com.lvmoney.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @describe：通配符工具
 * @author: lvmoney /xxxx科技有限公司
 * @version:v1.0 2019年1月10日 下午3:49:46
 */

public class WildcardUtil {
    public static boolean simpleWildcardMatch(String pattern, String str) {
        return wildcardMatch(pattern, str, "*");
    }

    public static boolean wildcardMatch(String pattern, String str, String wildcard) {
        if (StringUtils.isEmpty(pattern) || StringUtils.isEmpty(str)) {
            return false;
        }
        final boolean startWith = pattern.startsWith(wildcard);
        final boolean endWith = pattern.endsWith(wildcard);
        String[] array = StringUtils.split(pattern, wildcard);
        int currentIndex = -1;
        int lastIndex = -1;
        switch (array.length) {
            case 0:
                return true;
            case 1:
                currentIndex = str.indexOf(array[0]);
                if (startWith && endWith) {
                    return currentIndex >= 0;
                }
                if (startWith) {
                    return currentIndex + array[0].length() == str.length();
                }
                if (endWith) {
                    return currentIndex == 0;
                }
                return str.equals(pattern);
            default:
                for (String part : array) {
                    currentIndex = str.indexOf(part);
                    if (currentIndex > lastIndex) {
                        lastIndex = currentIndex;
                        continue;
                    }
                    return false;
                }
                return true;
        }
    }

    public static void main(String[] args) {
        Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("/css/*", "test");
        System.out.println(testMap.containsKey("/css/test.css"));
        System.out.println(WildcardUtil.simpleWildcardMatch("test.*", "test.test"));
    }
}
