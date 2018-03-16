package uyun.xianglong.examples.sclaz.benchmark

import redis.clients.jedis.{Jedis, JedisPool}


object RedisUtils {

  case class RedisConfig(host: String) extends Serializable

  private val config = implicitly[RedisConfig]
  private val pool = new JedisPool(config.host)

  def execute[T](func: Jedis => T)(implicit config: RedisConfig): T = {
    val jedis = pool.getResource
    try {
      func(jedis)
    } finally {
      jedis.close()
    }
  }
}
