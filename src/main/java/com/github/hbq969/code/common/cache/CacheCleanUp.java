package com.github.hbq969.code.common.cache;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存定期清理接口
 * @createTime : 14:54:21, 2023.04.02, 周日
 */
public interface CacheCleanUp {

  /**
   * 定期清理缓存，防止缓存过多
   */
  void cleanUp();
}
