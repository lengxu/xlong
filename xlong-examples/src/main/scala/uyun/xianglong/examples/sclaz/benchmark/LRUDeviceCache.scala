package uyun.xianglong.examples.sclaz.benchmark

import com.google.common.cache.{Cache, CacheBuilder}

object LRUDeviceCache {
  val cache: Cache[String, ModelDetail] = CacheBuilder.newBuilder()
    .maximumSize(100000)
    .concurrencyLevel(8)
    .build[String, ModelDetail]()
}
