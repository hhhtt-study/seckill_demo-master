package com.example.seckilldemo.utils;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码校验类
 *
 * @author: LC
 * @date 2022/3/2 2:16 下午
 * @ClassName: ValidatorUtil
 */
public class ValidatorUtil {

    private static final Pattern mobile_patten = Pattern.compile("^((13[0-9])|(14[5-9])|(15[0-3])|(15[5-9])|(166)|(17[0-8])|(18[0-9])|(19[1-9]))\\d{8}$");

    /**
     * 手机号码校验
     * @author LC
     * @operation add
     * @date 2:19 下午 2022/3/2
     * @param mobile
     * @return boolean
     **/
    public static boolean isMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        Matcher matcher = mobile_patten.matcher(mobile);
        return matcher.matches();
    }
}
