package cn.yiynx.example.dp;

import cn.yiynx.example.util.dp.strategy.XStrategyInterface;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoService(XStrategyInterface.class)
public class Test1Strategy implements XStrategyInterface {
    public static final String X_STRATEGY_TYPE_TEST1 = "test1";
    @Override
    public String type() {
        return X_STRATEGY_TYPE_TEST1;
    }

    @Override
    public Object handler(Object param) {
        log.info("param:{}", param);
        return param;
    }
}
