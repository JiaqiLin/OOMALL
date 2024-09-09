package cn.edu.xmu.javaee.core.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RedisUtilTest {

    private RedisUtil redisUtil;

    private java.lang.reflect.Method randomizeTimeout;

    @BeforeEach
    public void initializeRandomizeTimeout() throws NoSuchMethodException {
        redisUtil = new RedisUtil(new RedisTemplate<>());
        randomizeTimeout = redisUtil.getClass().getDeclaredMethod("randomizeTimeout", long.class);
        randomizeTimeout.setAccessible(true);
    }

    /**
     * 测试 randomizeTimeout 方法生成的数是否在指定范围内
     */
    @Test
    public void randomizeTimeout1() {
        long originalTimeout = 10;
        // 样本数
        long numSamples = 10000;
        Stream.generate(() -> {
                    try {
                        return randomizeTimeout.invoke(redisUtil, originalTimeout);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .limit(numSamples)
                .forEach((timeout) -> {
                    assertTrue(originalTimeout <= (long) timeout && (long) timeout < originalTimeout + originalTimeout / 5.0);
                });
    }

    /**
     * 测试 randomizeTimeout 方法生成的数是否均匀
     */
    @Test
    public void randomizeTimeout2() {
        long originalTimeout = 3600;
        // 样本数
        long numSamples = 10000;
        double upperProbability = 0.2;
        Stream.generate(() -> {
                    try {
                        return randomizeTimeout.invoke(redisUtil, originalTimeout);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .limit(numSamples)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values()
                .forEach((count) -> {
                    assertTrue((double) count / numSamples < upperProbability);
                });
    }

}
