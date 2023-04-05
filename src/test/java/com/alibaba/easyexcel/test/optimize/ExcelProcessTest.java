package cn.yiynx.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * total: 1000000, pageSize: 1000000, 耗时: 15430ms
 * total: 1000000, pageSize: 500000, 耗时: 16114ms
 * total: 1000000, pageSize: 200000, 耗时: 16500ms
 * total: 1000000, pageSize: 100000, 耗时: 16734ms
 * 
 * 队列方案
 * https://www.codeleading.com/article/49653104168/
 */
@Slf4j
public class ExcelProcessTest {

    // @Autowired
    // @Qualifier("excelThreadPool")
    // private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    
    @Test
    public void exportTest(){
        Long count = 1000000L;
        // 导出文件路径
        String fileName = "E:/yzk/work-dir/project/new/example/src/test/java/cn/yiynx/example/excelTest.xlsx";
        // 查询条件
        Long start = System.currentTimeMillis();
        // 每页多少条数据
        int pageSize = 500000;
        Long sheetNum = count % pageSize == 0 ? count / pageSize:count / pageSize + 1;
        // 多线程去读
        // 1.初始化map容量 防止扩容带来的效率损耗
        Map<Integer, List<TableBloodRelationship>> pageMap = new ConcurrentHashMap<>(Math.toIntExact(sheetNum));
        CountDownLatch countDownLatch = new CountDownLatch(Math.toIntExact(sheetNum));
        // 注意 easyexcel 暂时不支持多线程并发写入！！！ 详情请看github上issues
        TableBloodRelationship queryCondition = new TableBloodRelationship();

        int corePoolSize = 5;
        int maximumPoolSize = 10;
        ThreadPoolExecutor exector = (ThreadPoolExecutor) Executors.newFixedThreadPool(corePoolSize);
        exector.setCorePoolSize(corePoolSize);
        exector.setMaximumPoolSize(maximumPoolSize);
        for (int i = 0 ;i< sheetNum;i++){
            int curPage = i;
            // threadPoolTaskExecutor.submit(()->{
            exector.submit(()->{
                // 获取数据存放到map中
                queryCondition.setStartNum(curPage * pageSize);
                queryCondition.setPageSize(pageSize);
                pageMap.put(curPage, selectTableBloodRelationshipExport(queryCondition));
                // 消耗掉一个
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 写入
        try (ExcelWriter excelWriter = EasyExcel.write(fileName, TableBloodRelationship.class).build()) {
            pageMap.forEach((k,v)->{
                log.info("thread: {}, 正在写入{}条数据", Thread.currentThread().getName(), pageSize);
                WriteSheet writeSheet = EasyExcel.writerSheet(k, "第"+(k+1)+"批数据").build();
                excelWriter.write(v, writeSheet);
                // 写完当前数据立刻删除  不删除会产生内存泄漏即无法回收Map中巨大的空间 导致oom
                pageMap.remove(k);
            });
            excelWriter.finish();
        }
        Long end = System.currentTimeMillis();
        log.info("total: {}, pageSize: {}, 耗时: {}", count, pageSize, (end-start)+"ms");

    }

    private List<TableBloodRelationship> selectTableBloodRelationshipExport(TableBloodRelationship queryCondition) {
        List<TableBloodRelationship> datas = new ArrayList<>();
        int start = queryCondition.getStartNum();
        for (int i = start; i < queryCondition.getPageSize() + start; i++) {
            TableBloodRelationship tbr = new TableBloodRelationship();
            tbr.setAppName("appName_" + i);
            tbr.setPageSize(queryCondition.getPageSize());
            tbr.setStartNum(i);
            tbr.setScenarioName("scenarioName_" + i);
            tbr.setProvinceId(queryCondition.getProvinceId());
            tbr.setDataSourceSystem("dataSourceSystem_" + i);
            tbr.setSharedTableEn("sharedTableEn_" + i);
            datas.add(tbr);
        }
        return datas;
    }
    
}
