package cn.yiynx.example.extensions;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * manifold String Templates测试
 */
public class StringTemplatesTest {

    @Test
    public void test() {
        Assert.isTrue(stringTemplates("123", 456, new Date()).matches("\\d*"), "字符串模板变量替换失败！");
    }

    public String stringTemplates(String string, Integer integer, Date date) {
        return "$string$integer${date.getTime()}";
    }
}
