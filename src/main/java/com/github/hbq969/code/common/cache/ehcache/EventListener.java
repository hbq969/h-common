package com.github.hbq969.code.common.cache.ehcache;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * @author : hbq969@gmail.com
 * @description : Ehcache监听器
 * @createTime : 18:28:25, 2023.04.01, 周六
 */
@Slf4j
public class EventListener implements CacheEventListener {

  @Override
  public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    log.info("Ehcache删除KEY: {}", element.getObjectKey());
  }

  @Override
  public void notifyElementPut(Ehcache cache, Element element) throws CacheException {

  }

  @Override
  public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {

  }

  @Override
  public void notifyElementExpired(Ehcache cache, Element element) {
    log.info("Ehcache中KEY过期: {}", element.getObjectKey());
  }

  @Override
  public void notifyElementEvicted(Ehcache cache, Element element) {
    log.info("Ehcache驱逐KEY: {}", element.getObjectKey());
  }

  @Override
  public void notifyRemoveAll(Ehcache cache) {

  }

  @Override
  public void dispose() {

  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return null;
  }
}
