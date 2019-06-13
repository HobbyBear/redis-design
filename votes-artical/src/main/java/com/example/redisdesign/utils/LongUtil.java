package com.example.redisdesign.utils;

import java.text.DecimalFormat;

/**
 * @author xch
 * @since 2019/6/13 17:25
 **/
public class LongUtil {

    public static  Long parse(Object o) {
        if (o instanceof Double){
            //把科学计数法的double转换为double类型
            return Long.valueOf(new DecimalFormat("0").format(o));
        }
        return Long.valueOf(String.valueOf(o));
    }

}
