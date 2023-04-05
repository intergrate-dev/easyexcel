package cn.yiynx.example.extensions;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;

/**
 * manifold List扩展类测试
 */
public class ListExtTest {

    /**
     * ListExt测试
     * @see cn.yiynx.example.extensions.java.util.List.ListExt#xOf(Object) 
     * @see cn.yiynx.example.extensions.java.util.List.ListExt#xOf(Object[]) 
     */
    @Test
    public void testListOf() {
        List<Long> list1 = List.xOf();
        Assert.isTrue(list1.isEmpty(), "失败");
        List<String> list2 = List.xOf("1");
        list2.forEach(System.out::println);
        List<Integer> list3 = List.xOf(1, 2, 3);
        list3.forEach(System.out::println);
    }
}
