package cn.yiynx.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.Test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 多线程查询，同步插入到单个sheet
 * 
 */
@Slf4j
public class WriteDataTest {

    public static void main(String[] args) {
        File file = new File("E:/yzk/work-dir/project/example/src/test/java/cn/yiynx/example/test.xlsx");
        try {
            new WriteDataTest().writeExcel(500, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.error("fail, error: {}", e);
        }
    }

    /* @Test
    public void test() {
        File file = new File("E:/yzk/work-dir/project/example/src/test/java/cn/yiynx/example/test.xlsx");
        try {
            writeExcel(500, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.error("fail, error: {}", e);
        }
    } */

    public void writeExcel(int exifInfoCount, OutputStream outputStream) {
        //每个sheet保存的数据量
        int num = 200;
        int corePoolSize = 5;
        int maximumPoolSize = 10;
        //用线程池管理多线程
        ThreadPoolExecutor exector = (ThreadPoolExecutor) Executors.newFixedThreadPool(corePoolSize);
        exector.setCorePoolSize(corePoolSize);
        exector.setMaximumPoolSize(maximumPoolSize);
        
        List<ReadExifInfoThread> tasks = Lists.newCopyOnWriteArrayList();
        ExcelWriter excelWriter = EasyExcel.write(outputStream, TableBloodRelationship.class).build();
        //exifInfoCount 写入excel数据总量
        //pageCount 要写入sheet页数量。同分页
        int pageCount = exifInfoCount % num == 0 ? (exifInfoCount / num) : (exifInfoCount / num + 1);
        TableBloodRelationship queryCondition = new TableBloodRelationship();
        queryCondition.setProvinceId("1");
        for (int i = 0; i < pageCount; i++) {
            queryCondition.setStartNum(i * num);
            queryCondition.setPageSize(num);
            ReadExifInfoThread readExifInfoThread = new ReadExifInfoThread(queryCondition, i, num);
            tasks.add(readExifInfoThread);
        }
        try {
            //用invokeAll方法提交任务，返回数据的顺序和tasks中的任务顺序一致，如果第一个线程查0-10000行数据，第二个线程查10000-10001行数据，
            //第二个线程大概率比第一个线程先执行完，但是futures中第一位数据是0-10000的数据。
            List<Future<List<TableBloodRelationship>>> futures = exector.invokeAll(tasks);
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "台账信息" + (1)).build();
            for (int i = 0; i < pageCount; i++) {
                List<TableBloodRelationship> exifInfoList = futures.get(i).get();
                excelWriter.write(exifInfoList, writeSheet);
            }
        } catch (Exception e) {
           log.error("台账导出数据失败", e);
        }
        exector.shutdown();
        excelWriter.finish();

        /* WriteSheet writeSheet = EasyExcel.writerSheet(0, "台账信息" + (1)).build();
        for (int i = 0; i < pageCount; i++) {
            excelWriter.write(selectTableBloodRelationshipExport(queryCondition), writeSheet);
        }
        excelWriter.finish(); */

    }

    private List<TableBloodRelationship> selectTableBloodRelationshipExport(TableBloodRelationship queryCondition) {
        List<TableBloodRelationship> datas = new ArrayList<>();
        int start = queryCondition.getStartNum();
        for (int i = start; i < queryCondition.getPageSize() + start; i++) {
            TableBloodRelationship tbr = new TableBloodRelationship();
            tbr.setAppName("appName_" + i);
            tbr.setPageSize(queryCondition.getPageSize());
            tbr.setStartNum(start);
            tbr.setScenarioName("scenarioName_" + i);
            tbr.setProvinceId(queryCondition.getProvinceId());
            tbr.setDataSourceSystem("dataSourceSystem_" + i);
            tbr.setSharedTableEn("sharedTableEn_" + i);
        }
        return datas;
    }

    

}
