package cn.yiynx.example;

import cn.hutool.core.lang.Assert;
import cn.yiynx.example.xif.Message;
import cn.yiynx.xif.core.Xif;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static cn.yiynx.example.xif.MessageXifHandler.XIF_GROUP_MESSAGE;

@Slf4j
@SpringBootTest
public class XifTest {

    @Test
    void testXif() {
        log.info("testXif");
        Assert.equals("type1", Xif.handler(XIF_GROUP_MESSAGE, new Message<>().setType("type1")));
        Assert.equals("type2", Xif.handler(XIF_GROUP_MESSAGE, new Message<>().setType("type2")));
        Assert.equals("else", Xif.handler(XIF_GROUP_MESSAGE, new Message<>().setType("type3")));
    }
}
