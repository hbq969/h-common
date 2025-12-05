package com.github.hbq969.code.common.utils;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 复杂密码生成器
 * 规则：必须包含大小写字母、数字、特殊字符，长度8-32位
 */
public class ComplexPasswordGenerator {

    // 密码正则模式
    private static final Pattern COMPLEX_PASS_PATTERN = PatternPool.get(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*_()?\\-+=;:,.]).{8,32}$"
    );

    // 小写字母
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";

    // 大写字母
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // 数字
    private static final String DIGITS = "0123456789";

    // 特殊字符（与正则中的一致）
    private static final String SPECIAL_CHARS = "!@#$%^&*_()?\\-+=;:,.";

    /**
     * 生成随机复杂密码
     *
     * @param length 密码长度（8-32之间）
     * @return 符合规则的密码
     */
    public static String generatePassword(int length) {
        if (length < 8 || length > 32) {
            throw new IllegalArgumentException("密码长度必须在8-32位之间");
        }

        // 确保每种类型至少有一个字符
        List<Character> passwordChars = new ArrayList<>();

        // 1. 添加小写字母
        passwordChars.add(RandomUtil.randomChar(LOWER_CASE));

        // 2. 添加大写字母
        passwordChars.add(RandomUtil.randomChar(UPPER_CASE));

        // 3. 添加数字
        passwordChars.add(RandomUtil.randomChar(DIGITS));

        // 4. 添加特殊字符
        passwordChars.add(RandomUtil.randomChar(SPECIAL_CHARS));

        // 5. 填充剩余长度
        String allChars = LOWER_CASE + UPPER_CASE + DIGITS + SPECIAL_CHARS;
        for (int i = 4; i < length; i++) {
            passwordChars.add(RandomUtil.randomChar(allChars));
        }

        // 6. 打乱顺序
        Collections.shuffle(passwordChars);

        // 7. 转换为字符串
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    /**
     * 生成默认长度的随机复杂密码（12位）
     *
     * @return 符合规则的密码
     */
    public static String generatePassword() {
        return generatePassword(12); // 默认12位，安全性较好
    }

    /**
     * 验证密码是否符合复杂度规则
     *
     * @param password 待验证的密码
     * @return 是否符合规则
     */
    public static boolean isValidPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return false;
        }
        return COMPLEX_PASS_PATTERN.matcher(password).matches();
    }

    /**
     * 生成密码并验证，确保符合规则（防止小概率生成失败的情况）
     *
     * @param length 密码长度
     * @return 符合规则的密码
     */
    public static String generateAndVerifyPassword(int length) {
        String password;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            password = generatePassword(length);
            attempts++;
            if (attempts >= maxAttempts) {
                // 如果多次生成都不符合规则，使用备用方案
                password = createPasswordByTemplate(length);
                break;
            }
        } while (!isValidPassword(password));

        return password;
    }

    /**
     * 备用方案：通过模板方式确保密码符合规则
     */
    private static String createPasswordByTemplate(int length) {
        // 确保每种类型都有
        StringBuilder password = new StringBuilder();

        // 模板：小写 + 大写 + 数字 + 特殊 + 随机字符
        password.append(RandomUtil.randomChar(LOWER_CASE));      // 小写
        password.append(RandomUtil.randomChar(UPPER_CASE));      // 大写
        password.append(RandomUtil.randomChar(DIGITS));          // 数字
        password.append(RandomUtil.randomChar(SPECIAL_CHARS));   // 特殊

        // 填充剩余长度
        String allChars = LOWER_CASE + UPPER_CASE + DIGITS + SPECIAL_CHARS;
        for (int i = 4; i < length; i++) {
            password.append(RandomUtil.randomChar(allChars));
        }

        // 将密码字符打乱
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RandomUtil.randomInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }

    /**
     * 获取密码强度评分（0-100分）
     *
     * @param password 密码
     * @return 强度分数
     */
    public static int getPasswordStrength(String password) {
        if (StrUtil.isBlank(password)) {
            return 0;
        }

        int score = 0;

        // 长度评分
        int length = password.length();
        if (length >= 8) score += 10;
        if (length >= 12) score += 10;
        if (length >= 16) score += 10;

        // 字符类型评分
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (SPECIAL_CHARS.indexOf(c) >= 0) hasSpecial = true;
        }

        if (hasLower) score += 10;
        if (hasUpper) score += 10;
        if (hasDigit) score += 10;
        if (hasSpecial) score += 15;

        // 模式检查（避免简单模式）
        if (!hasSimplePattern(password)) {
            score += 15;
        }

        // 重复字符检查
        if (!hasRepeatingChars(password)) {
            score += 10;
        }

        return Math.min(score, 100);
    }

    /**
     * 检查是否包含简单模式（如：1234, abcd, qwert等）
     */
    private static boolean hasSimplePattern(String password) {
        String[] patterns = {"1234", "2345", "3456", "4567", "5678", "6789",
                "abcd", "bcde", "cdef", "defg", "efgh", "fghi",
                "qwert", "asdf", "zxcv"};

        String lowerPass = password.toLowerCase();
        for (String pattern : patterns) {
            if (lowerPass.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有重复字符（连续3个以上相同字符）
     */
    private static boolean hasRepeatingChars(String password) {
        if (password.length() < 3) return false;

        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            if (c1 == c2 && c2 == c3) {
                return true;
            }
        }
        return false;
    }

    /**
     * 批量生成密码
     *
     * @param count  生成数量
     * @param length 密码长度
     * @return 密码列表
     */
    public static List<String> generatePasswords(int count, int length) {
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            passwords.add(generateAndVerifyPassword(length));
        }
        return passwords;
    }

    public static void main(String[] args) {
        // 1. 生成默认长度的密码
        String password1 = ComplexPasswordGenerator.generatePassword();
        System.out.println("默认密码: " + password1);
        System.out.println("是否有效: " + ComplexPasswordGenerator.isValidPassword(password1));
        System.out.println("强度评分: " + ComplexPasswordGenerator.getPasswordStrength(password1));

        // 2. 生成指定长度的密码
        String password2 = ComplexPasswordGenerator.generatePassword(16);
        System.out.println("16位密码: " + password2);
        System.out.println("是否有效: " + ComplexPasswordGenerator.isValidPassword(password2));
        System.out.println("强度评分: " + ComplexPasswordGenerator.getPasswordStrength(password2));

        // 3. 验证密码
        String[] testPasswords = {
                "Abc123!@",       // 有效
                "weakpass",       // 无效：无大写、数字、特殊字符
                "ABC123!@",       // 无效：无小写
                "Abcdefg!",       // 无效：无数字
                "Abc123456",      // 无效：无特殊字符
                "A1!b2@c3#d4$"    // 有效
        };

        for (String pwd : testPasswords) {
            System.out.printf("密码 '%s' 验证结果: %s%n",
                    pwd,
                    ComplexPasswordGenerator.isValidPassword(pwd) ? "有效" : "无效"
            );
        }

        // 4. 批量生成密码
        List<String> passwords = ComplexPasswordGenerator.generatePasswords(5, 10);
        System.out.println("批量生成的密码:");
        for (int i = 0; i < passwords.size(); i++) {
            System.out.printf("密码%d: %s (强度: %d)%n",
                    i + 1,
                    passwords.get(i),
                    ComplexPasswordGenerator.getPasswordStrength(passwords.get(i))
            );
        }
    }
}