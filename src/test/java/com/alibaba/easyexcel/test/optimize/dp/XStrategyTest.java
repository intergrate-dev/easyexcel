package cn.yiynx.example.dp;

import cn.hutool.core.lang.Assert;
import cn.yiynx.example.util.dp.strategy.XStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static cn.yiynx.example.dp.Test1Strategy.X_STRATEGY_TYPE_TEST1;
import static cn.yiynx.example.dp.Test2Strategy.X_STRATEGY_TYPE_TEST2;

@Slf4j
public class XStrategyTest {

    @Test
    public void test() throws IllegalArgumentException {
        Assert.equals(XStrategy.strategy(X_STRATEGY_TYPE_TEST1).handler("1"), "1");
        Assert.equals(XStrategy.strategy(X_STRATEGY_TYPE_TEST2).handler("2"), "2");
        try {
            Assert.equals(XStrategy.strategy("other").handler("3"), "3");
        } catch (IllegalArgumentException e) {
            log.error("参数异常！", e);
            Assert.isTrue(e.getMessage().contains("not found"));
        }
    }
}
