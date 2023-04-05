package cn.yiynx.example;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.yiynx.example.util.paralle.ParallelUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ParallelUtilTest {

    @Test
    public void test() throws InterruptedException {
        AtomicLong consumerIndex = new AtomicLong(); // 消费计数
        ParallelUtil.parallel(Long.class, 2, 10).asyncProducer(index -> {
            try {
                Thread.sleep(RandomUtil.randomInt(100)); // 模拟生产数据耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("中断异常！", e);
            }
            log.info("生产:{}", index);
            return index;
        }).syncConsumer(result -> {
            Assert.equals(consumerIndex.incrementAndGet(), result, "消费结果顺序错误！");
            try {
                Thread.sleep(RandomUtil.randomInt(100)); // 模拟消费数据耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("中断异常！", e);
            }
            log.info("消费:{}", result);
        }).start();
    }
}
