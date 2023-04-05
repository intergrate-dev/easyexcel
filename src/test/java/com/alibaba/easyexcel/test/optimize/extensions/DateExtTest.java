package cn.yiynx.example.extensions;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * manifold Date扩展测试
 */
public class DateExtTest {

    @Test
    public void test() {
        var dateFormat = "yyyy-MM-dd HH:mm:ss";
        Date now = Date.xNow();
        Assert.equals(now.xFormat(dateFormat), DateUtil.format(now, dateFormat));
    }

}
