package com.github.hbq969.code.common.utils;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * @author : hbq969@gmail.com
 * @description : 字符串工具类
 * @createTime : 14:50:19, 2023.03.31, 周五
 */
@Slf4j
public final class StrUtils {

    public static final String PREFIX_VARIABLE = "${";

    public static final String SUFFIX_VARIABLE = "}";
    public static final char PLACE_HOLDER_SUFFIX = '}';
    public static final char PLACE_HOLDER_PREFIX = '{';
    public static final char PLACE_HOLDER_DOLLAR = '$';
    public static final char placeholderNum = '#';

    private static final char HexCharArr[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f'};

    private static final String HexStr = "0123456789abcdef";

    /**
     * 判断字符串是否为空, <code>null</code>字符串也判定为空
     *
     * @param str
     * @return
     */
    public static boolean strEmpty(String str) {
        return str == null || "".equals(str) || "null".equalsIgnoreCase(str);
    }

    /**
     * 判断字符串是否不为空, <code>null</code>字符串也判定为空
     *
     * @param str
     * @return
     */
    public static boolean strNotEmpty(String str) {
        return !strEmpty(str);
    }

    /**
     * 判断字符串是否为 <code>${}</code> 定义的变量
     *
     * @param variable
     * @return
     */
    public static boolean isVariable(String variable) {
        return !strEmpty(variable) && variable.trim().startsWith(PREFIX_VARIABLE) && variable.trim()
                .endsWith(SUFFIX_VARIABLE);
    }

    /**
     * 解析 <code>${}</code> 包裹的变量名
     *
     * @param variable
     * @return
     */
    public static String getVariableKey(String variable) {
        if (isVariable(variable)) {
            variable = variable.trim();
            variable = variable.substring(PREFIX_VARIABLE.length(),
                    variable.length() - SUFFIX_VARIABLE.length());
            int idx = variable.indexOf(':');
            if (idx > 0) {
                return variable.substring(0, idx);
            } else {
                return variable;
            }
        } else {
            return variable;
        }
    }

    /**
     * 对关键字做脱敏处理
     *
     * @param key        关键字
     * @param startIndex 脱敏的开始位置
     * @param endIndex   脱敏的结束位置
     * @return
     */
    public static String desensitive(String key, int startIndex, int endIndex) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        char[] chars = key.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i >= startIndex && i <= endIndex) {
                sb.append("*");
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 对关键字做脱敏处理
     *
     * @param key        关键字
     * @param startIndex 脱敏的开始位置
     * @return
     */
    public static String desensitive(String key, int startIndex) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        return desensitive(key, startIndex, key.length() - 1);
    }

    /**
     * 文本替换，忽略正则表达式
     *
     * @param str
     * @param quote
     * @param replacement
     * @return
     */
    public static String replaceWithQuote(String str, String quote, String replacement) {
        String q = Pattern.quote(quote);
        return strEmpty(str) ? str : str.replaceAll(q, replacement);
    }

    /**
     * 文本替换
     *
     * @param str
     * @param regex
     * @param replacement
     * @return
     */
    public static String replaceWithRegex(String str, String regex, String replacement) {
        return strEmpty(str) ? str : str.replaceAll(regex, replacement);
    }

    /**
     * 使用一组替换值 逐个按照次序替换文本
     *
     * @param str
     * @param quote
     * @param replacements
     * @return
     */
    public static String replaceWithQuote(String str, String quote, List<String> replacements) {
        String q = Pattern.quote(quote);
        if (strEmpty(str)) {
            return str;
        }
        Count c = Count.unsafe();
        while (str.contains(quote)) {
            if (c.intValue() < replacements.size()) {
                String replacement = replacements.get(c.intValue());
                if (replacement.contains(quote)) {
                    throw new IllegalArgumentException();
                }
                str = str.replaceFirst(q, replacement);
            } else {
                break;
            }
            c.incrementAndGet();
        }
        return str;
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @return
     */
    public static String replacePlaceHolders(String replacement, Object map) {
        return replacePlaceHolders(replacement, map, "");
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @return
     */
    public static String replacePlaceHoldersWithSymbol(String replacement, Object map, char symbol) {
        return replacePlaceHoldersWithSymbol(replacement, map, "", symbol);
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @param defaultValue
     * @return
     */
    public static String replacePlaceHolders(String replacement, Object map, String defaultValue) {
        return replacePlaceHoldersWithSymbol(replacement, map, defaultValue, PLACE_HOLDER_DOLLAR);
    }

    /**
     * 替换给定字符串中的占位符变量值
     *
     * @param replacement
     * @param map
     * @param defaultValue
     * @return
     */
    public static String replacePlaceHoldersWithSymbol(String replacement, Object map,
                                                       String defaultValue, char symbol) {
        Assert.notNull(replacement, "被替换的字符串为空");
        char[] chars = replacement.toCharArray();
        Stack<Character> stack = new Stack<>();
        StringBuilder variable = new StringBuilder(35);
        StringBuilder holder = new StringBuilder(replacement.length());
        int len = chars.length;
        boolean isVariable = false;
        char c;
        Class<?> clz = map.getClass();
        for (int i = 0; i < len; i++) {
            c = chars[i];
            if (isPlaceHolderDollarCharacter(chars, len, c, i, symbol)) {
                stack.push(c);
            }
            if (isVariable && c == PLACE_HOLDER_SUFFIX) {
                String name = variable.toString();
                int x = name.indexOf(':');
                if (x > 0) {
                    defaultValue = name.substring(x + 1);
                    name = name.substring(0, x);
                }
                if (map instanceof Map) {
                    holder.append(MapUtils.getString((Map) map, name, defaultValue));
                } else {
                    Object value;
                    try {
                        value = getValueFromObject(clz, map, name, defaultValue);
                    } catch (NoSuchFieldException e) {
                        log.warn("{}.{} 不存在，请检查。", clz.getName(), name);
                        continue;
                    }
                    holder.append(value);
                }
                isVariable = false;
                stack.pop();
                variable.setLength(0);
                continue;
            }
            if (isVariable) {
                variable.append(c);
            } else {
                if (isPlaceHolderDollarCharacter(chars, len, c, i, symbol)) {
                } else if (c == PLACE_HOLDER_PREFIX && i > 0 && chars[i - 1] == symbol) {
                } else {
                    holder.append(c);
                }
            }
            if (!stack.isEmpty() && stack.peek() == symbol && c == PLACE_HOLDER_PREFIX) {
                isVariable = true;
            }
        }
        return holder.toString();
    }

    private static Object getValueFromObject(Class<?> clz, Object target, String fn, Object defaultValue) throws NoSuchFieldException {
        Field f = clz.getDeclaredField(fn);
        f.setAccessible(true);
        Object value = ReflectionUtils.getField(f, target);
        if (ObjectUtil.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String byteArrToHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        int i = 0;
        for (byte bt : bytes) {
            chars[i++] = HexCharArr[bt >>> 4 & 0xf];
            chars[i++] = HexCharArr[bt & 0xf];
        }
        return new String(chars);
    }

    /**
     * 将16进制字符串转成字节数组
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexToByteArr(String hexStr) {
        char[] chars = hexStr.toCharArray();
        byte[] buf = new byte[chars.length / 2];
        int index = 0;
        for (int i = 0; i < chars.length; i++) {
            int highBit = HexStr.indexOf(chars[i]);
            int lowBit = HexStr.indexOf(chars[++i]);
            buf[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return buf;
    }

    /**
     * 使用分隔符连接symbol
     *
     * @param symbol
     * @param separator
     * @param size
     * @return
     */
    public static String symbolSplic(String symbol, String separator, int size) {
        if (symbol == null || separator == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder((symbol.length() + separator.length()) * size);
        for (int i = 0; i < size; i++) {
            sb.append(separator).append(symbol);
        }
        return sb.substring(separator.length());
    }

    private static boolean isPlaceHolderDollarCharacter(char[] chars, int len, char c, int i,
                                                        char symbol) {
        return c == symbol && i < len - 1 && chars[i + 1] == PLACE_HOLDER_PREFIX;
    }

    public static String emptyToDefaultStr(String str, String defaultStr) {
        return strEmpty(str) ? defaultStr : str;
    }
}
