package com.lvmoney.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Administrator on 2019/7/4.
 */
public class PinyinUtil {

    private static Logger logger = LoggerFactory.getLogger(PinyinUtil.class);

    public static HanyuPinyinOutputFormat pinyinOutputFormat;

    static {
        pinyinOutputFormat = new HanyuPinyinOutputFormat();
        pinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写
        pinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // WITH_TONE_NUMBER//第几声
        pinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static String prepareKeyword(final String keyword) {
        if (keyword != null) {
            return keyword.replaceAll("[\\s 　]*", "");
        } else {
            return null;
        }
    }

    public static Map<String, String> getPinYin(String src) {
        if (src != null) {
            String keyword = prepareKeyword(src);
            char[] charArray = keyword.toCharArray();
            String[] pinyinArray = null;
            Map<String, String> tempCharMap = new HashMap<String, String>();
            Map<String, String> tempStrPinyinMap = new HashMap<String, String>();

            Map<String, String> resultPinyinMap = new HashMap<String, String>();
            String key_char = null;
            String key_str = null;

            try {
                for (int i = 0; i < charArray.length; i++) {
                    tempCharMap = new HashMap<String, String>();
                    // 判断是否为汉字字符函数
                    if (Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
                        pinyinArray =
                                PinyinHelper.toHanyuPinyinStringArray(charArray[i], pinyinOutputFormat);
                        if (pinyinArray != null) {
                            for (int j = 0; j < pinyinArray.length; j++) {
                                tempCharMap.put(pinyinArray[j], Character.toString(charArray[i]));
                            }
                        }
                    } else {
                        tempCharMap.put(Character.toString(charArray[i]),
                                Character.toString(charArray[i]));
                    }

                    Iterator<String> it_str = null;
                    Iterator<String> it_char = tempCharMap.keySet().iterator();
                    while (it_char.hasNext()) {
                        key_char = it_char.next();

                        if (i == 0) {
                            resultPinyinMap.put(key_char, "");
                        } else {
                            it_str = tempStrPinyinMap.keySet().iterator();
                            while (it_str.hasNext()) {
                                key_str = it_str.next();
                                resultPinyinMap.remove(key_str);

                                key_str += key_char;
                                resultPinyinMap.put(key_str, "");
                            }
                        }
                    }

                    tempStrPinyinMap.clear();
                    tempStrPinyinMap.putAll(resultPinyinMap);
                }

            } catch (Exception e) {
                logger.error("转码错误", e);
            } finally {
                tempCharMap.clear();
                tempStrPinyinMap.clear();
            }
            return resultPinyinMap;

        } else {
            return null;
        }
    }

    /**
     * 汉语拼音全拼
     * <p>未处理多音字情况
     *
     * @param chinese
     * @return
     * @date 下午2:17:06  2014年12月23日
     */
    public static String getFullSpell(String chinese) {
        if (StringUtils.isBlank(chinese))
            return null;
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("u:", "v");
    }

    /**
     * 获取汉字拼音首字母
     * <p>未处理多音字情况
     *
     * @param chinese
     * @return
     */
    public static String getFirstSpell(String chinese) {
        if (StringUtils.isBlank(chinese))
            return null;
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim().replaceAll("u:", "v");
    }

    public static Map<String, String> getFirstLetter(String src) {
        if (src != null) {
            String keyword = prepareKeyword(src);
            char[] charArray = keyword.toCharArray();
            String[] pinyinArray = null;
            Map<Character, String> tempCharMap = new HashMap<Character, String>();
            Map<String, String> tempStrPinyinMap = new HashMap<String, String>();

            Map<String, String> resultPinyinMap = new HashMap<String, String>();
            Character key_char = null;
            String key_str = null;

            try {
                for (int i = 0; i < charArray.length; i++) {
                    tempCharMap = new HashMap<Character, String>();
                    // 判断是否为汉字字符函数
                    if (Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
                        pinyinArray =
                                PinyinHelper.toHanyuPinyinStringArray(charArray[i], pinyinOutputFormat);
                        if (pinyinArray != null) {
                            for (int j = 0; j < pinyinArray.length; j++) {
                                tempCharMap.put(pinyinArray[j].charAt(0),
                                        Character.toString(charArray[i]));
                            }
                        }
                    } else {
                        tempCharMap.put(charArray[i], Character.toString(charArray[i]));
                    }

                    Iterator<String> it_str = null;
                    Iterator<Character> it_char = tempCharMap.keySet().iterator();
                    while (it_char.hasNext()) {
                        key_char = it_char.next();

                        if (i == 0) {
                            resultPinyinMap.put(Character.toString(key_char), "");
                        } else {
                            it_str = tempStrPinyinMap.keySet().iterator();
                            while (it_str.hasNext()) {
                                key_str = it_str.next();
                                resultPinyinMap.remove(key_str);

                                key_str += key_char;
                                resultPinyinMap.put(key_str, "");
                            }
                        }
                    }

                    tempStrPinyinMap.clear();
                    tempStrPinyinMap.putAll(resultPinyinMap);
                }

            } catch (Exception e) {
                logger.error("转码错误", e);
            } finally {
                tempCharMap.clear();
                tempStrPinyinMap.clear();
            }
            return resultPinyinMap;

        } else {
            return null;
        }
    }

    public static Map<String, String> getPingYinAndChinese(String src) {
        if (src != null) {
            String keyword = prepareKeyword(src);
            char[] charArray = keyword.toCharArray();
            String[] pinyinArray = null;
            Map<String, String> tempCharMap = new HashMap<String, String>();
            Map<String, String> tempStrPinyinMap = new HashMap<String, String>();

            Map<String, String> resultPinyinMap = new HashMap<String, String>();
            String key_char = null;
            String key_str = null;

            try {
                for (int i = 0; i < charArray.length; i++) {
                    tempCharMap = new HashMap<String, String>();
                    // 判断是否为汉字字符函数
                    if (Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
                        pinyinArray =
                                PinyinHelper.toHanyuPinyinStringArray(charArray[i], pinyinOutputFormat);
                        if (pinyinArray != null) {
                            for (int j = 0; j < pinyinArray.length; j++) {
                                tempCharMap.put(pinyinArray[j], Character.toString(charArray[i]));
                            }
                        }
                    }
                    tempCharMap
                            .put(Character.toString(charArray[i]), Character.toString(charArray[i]));


                    Iterator<String> it_str = null;
                    Iterator<String> it_char = tempCharMap.keySet().iterator();
                    while (it_char.hasNext()) {
                        key_char = it_char.next();

                        if (i == 0) {
                            resultPinyinMap.put(key_char, "");
                        } else {
                            it_str = tempStrPinyinMap.keySet().iterator();
                            while (it_str.hasNext()) {
                                key_str = it_str.next();
                                resultPinyinMap.remove(key_str);

                                key_str += key_char;
                                resultPinyinMap.put(key_str, "");
                            }
                        }
                    }

                    tempStrPinyinMap.clear();
                    tempStrPinyinMap.putAll(resultPinyinMap);
                }

            } catch (Exception e) {
                logger.error("转码错误", e);
            } finally {
                tempCharMap.clear();
                tempStrPinyinMap.clear();
            }
            return resultPinyinMap;

        } else {
            return null;
        }
    }

    public static void main(String[] args) {

        System.out.println("成长a潜伏共长");
        Map map = getPinYin("长成潜伏共");
        map.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);

        });
        Iterator<String> it = (new PinyinUtil()).getFirstLetter("长安").keySet().iterator();
        while (it.hasNext()) {
            System.out.println("" + it.next());
        }
    }


    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String cn2FirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (_t != null) {
                        pybf.append(_t[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }

    /**
     * 获取汉字串拼音，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    public static String cn2Spell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }


    /**
     * 汉字转换位汉语拼音首字母，英文字符不变，特殊字符丢失 支持多音字，生成方式如（长沙市长:cssc,zssz,zssc,cssz）
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    // 取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strs != null) {
                        for (int j = 0; j < strs.length; j++) {
                            // 取首字母
                            pinyinName.append(strs[j].charAt(0));
                            if (j != strs.length - 1) {
                                pinyinName.append(",");
                            }
                        }
                    }
                    // else {
                    // pinyinName.append(nameChar[i]);
                    // }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
            pinyinName.append(" ");
        }
        // return pinyinName.toString();
        return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen ,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    // 取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strs != null) {
                        for (int j = 0; j < strs.length; j++) {
                            pinyinName.append(strs[j]);
                            if (j != strs.length - 1) {
                                pinyinName.append(",");
                            }
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
            pinyinName.append(" ");
        }
        // return pinyinName.toString();
        return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
    }

    /**
     * 去除多音字重复数据
     *
     * @param theStr
     * @return
     */
    private static List<Map<String, Integer>> discountTheChinese(String theStr) {
        // 去除重复拼音后的拼音列表
        List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
        // 用于处理每个字的多音字，去掉重复
        Map<String, Integer> onlyOne = null;
        String[] firsts = theStr.split(" ");
        // 读出每个汉字的拼音
        for (String str : firsts) {
            onlyOne = new Hashtable<String, Integer>();
            String[] china = str.split(",");
            // 多音字处理
            for (String s : china) {
                Integer count = onlyOne.get(s);
                if (count == null) {
                    onlyOne.put(s, new Integer(1));
                } else {
                    onlyOne.remove(s);
                    count++;
                    onlyOne.put(s, count);
                }
            }
            mapList.add(onlyOne);
        }
        return mapList;
    }

    /**
     * 解析并组合拼音，对象合并方案(推荐使用)
     *
     * @return
     */
    private static String parseTheChineseByObject(List<Map<String, Integer>> list) {
        Map<String, Integer> first = null; // 用于统计每一次,集合组合数据
        // 遍历每一组集合
        for (int i = 0; i < list.size(); i++) {
            // 每一组集合与上一次组合的Map
            Map<String, Integer> temp = new Hashtable<String, Integer>();
            // 第一次循环，first为空
            if (first != null) {
                // 取出上次组合与此次集合的字符，并保存
                for (String s : first.keySet()) {
                    for (String s1 : list.get(i).keySet()) {
                        String str = s + s1;
                        temp.put(str, 1);
                    }
                }
                // 清理上一次组合数据
                if (temp != null && temp.size() > 0) {
                    first.clear();
                }
            } else {
                for (String s : list.get(i).keySet()) {
                    String str = s;
                    temp.put(str, 1);
                }
            }
            // 保存组合数据以便下次循环使用
            if (temp != null && temp.size() > 0) {
                first = temp;
            }
        }
        String returnStr = "";
        if (first != null) {
            // 遍历取出组合字符串
            for (String str : first.keySet()) {
                returnStr += (str + ",");
            }
        }
        if (returnStr.length() > 0) {
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        return returnStr;
    }

}
