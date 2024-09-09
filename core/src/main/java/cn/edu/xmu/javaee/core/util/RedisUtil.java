package cn.edu.xmu.javaee.core.util;

import io.lettuce.core.api.sync.RedisStringCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.data.redis.connection.RedisStringCommands.BitOperation;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Redis工具类
 * @author Ming Qiu
 **/
@Component
public class RedisUtil {

    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */

    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Serializable get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @param timeout 过期时间， -1为永不过期
     * @return true成功 false失败
     */
    public  boolean set(String key, Serializable value, long timeout) throws RuntimeException{
        if (timeout > 0) {
            // 为了防止雪崩，随机生成过期时间，范围为 timeout ~ timeout + timeout / 5
            timeout = randomizeTimeout(timeout);
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        }else{
            redisTemplate.opsForValue().set(key, value);
        }
        return true;
    }

    /**
     * 递减
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta > 0) {
            return redisTemplate.opsForValue().increment(key, -delta);
        }else {
            return 0;
        }
    }

    /**
     * 判断redis中是否存在键值
     * @param key   键
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置key的过期时间
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit unit){
        return redisTemplate.expire(key,timeout,unit);
    }

    /**
     * 获得redis中set集合
     * @param key   键
     * @return
     */
    public Set<Serializable > getSet(String key) {
        return  redisTemplate.opsForSet().members(key);
    }

    /**
     * 将Redis中key对应的值和集合otherKeys中的所有键对应的值并在destKey所对应的集合中
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long unionAndStoreSet(String key, Collection<String> otherKeys, String destKey){
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 将keys的值并起来存在destKey中
     * @param keys
     * @param destKey
     * @return
     */
    public Long unionAndStoreSet(Collection<String> keys, String destKey){
        return redisTemplate.opsForSet().unionAndStore(keys, destKey);
    }
    /**
     * 将values加入key的集合中
     * @param key
     * @param values
     * @return
     */
    public Long addSet(String key, Serializable... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 判断value是否是key的集合中的一元
     * @param key
     * @param value
     * @return
     */
    public Boolean isMemberSet(String key, Serializable value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    /**
     * 获取List中第index元素
     * @param key
     * @param index
     * @return
     */
    public Serializable indexList(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取list中start至end的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Serializable> rangeList(String key, long start, long end){
        return redisTemplate.opsForList().range(key, start,end);
    }

    /**
     * 从队列中移除value对象
     * @param key redis的key
     * @param count 前多少个相同的值
     * @param value 对象
     * @return
     */
    public Long removeList(String key, long count, Object value){
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 获取List中元素个数
     * @param key
     * @return
     */
    public Long sizeList(String key){
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 将元素加到队头
     * @param key
     * @param value
     * @return
     */
    public long leftPushList(String key, Serializable value){
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将多个元素加到队头
     * @param key
     * @param values
     * @return
     */
    public long leftPushAllList(String key, Serializable... values){
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 将元素加到队尾
     * @param key
     * @param value
     * @return
     */
    public long rightPushList(String key, Serializable value){
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将多个元素加到队尾
     * @param key
     * @param values
     * @return
     */
    public long rightPushAllList(String key, Serializable... values){
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    public Serializable leftPopList(String key){
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 执行脚本
     * @param script
     * @param keyList
     * @param values
     * @return
     */
    public <T> T executeScript(DefaultRedisScript<T> script,List<String> keyList, Object... values){
        return redisTemplate.execute(script,keyList,values);
    }

    /**
     * 获得hash值
     * @author Ming Qiu
     * <p>
     * date: 2022-11-09 22:45
     * @param key
     * @param field
     * @param value
     */
    public void setHash(String key, String field, Serializable value){
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 设置哈希值
     * @author Ming Qiu
     * <p>
     * date: 2022-11-09 22:45
     * @param key
     * @param field
     * @return
     */
    public Serializable getHash(String key, String field) {
        return (Serializable) redisTemplate.opsForHash().get(key, field);
    }

    public boolean getBit(String key, long offset){
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    public boolean setBit(String key, long offset, boolean value){
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public long bitCount(String key, long start, long end){
        return (long) redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes(), start, end));
    }

    public Long bitAnd(String destKey, String... keys){
        byte[][] bytes = new byte[keys.length][];
        for (int i = 0; i < keys.length; i++) {
            bytes[i] = keys[i].getBytes();
        }
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitOp(BitOperation.AND, destKey.getBytes(), bytes));
    }

    public Long bitOr(String destKey, String... keys){
        byte[][] bytes = new byte[keys.length][];
        for (int i = 0; i < keys.length; i++) {
            bytes[i] = keys[i].getBytes();
        }
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitOp(BitOperation.OR, destKey.getBytes(), bytes));
    }

    /**
     * 随机化 key 过期时间
     * @param timeout 过期时间
     * @return 随机化后的过期时间
     */
    private long randomizeTimeout(long timeout) {
        long min = 1;
        long max = timeout / 5;
        return timeout + (long) (new Random().nextDouble() * (max - min));
    }

}
