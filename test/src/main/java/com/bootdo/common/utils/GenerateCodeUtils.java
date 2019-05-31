package com.bootdo.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class GenerateCodeUtils {
	
	/**
	 * 生成编号：业务类型+时间戳（年月日时)+几位随机数
	 * @param pre  前缀，一般是指业务类型
	 * @param length 随机数长度
	 * @author shisha
	 */
	public static String genCode(String pre,int length) {
		return pre +  getDateTime() + getRandom(length); 
	}

    /**
       * 生成时间戳
     */
    private static String getDateTime(){
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        return sdf.format(new Date());
    }
    
    /**
          * 生成固定长度随机码 		
       *   例子 Random random = new Random();
	  int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
     * @param len 长度
     */
    private static String getRandom(long len) {
        long min = 1,max = 9; 
        for (int i = 1; i < len; i++) {
            min *= 10;
            max *= 10;
        }
        long rangeLong = (((long) (new Random().nextDouble() * (max - min)))) + min ;
        return String.valueOf(rangeLong);
    }
    
	
}

