package com.miaoshaproject.service;

/**
 * @author wangyu
 * @created 2020/12/16 9:52 下午
 */
//封装本地缓存操作类
public interface CacheService {

    //存方法
    void setCommonCache(String key, Object value);

    //取方法
    Object getFromCommonCache(String key);

}
