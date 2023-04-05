package cn.yiynx.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

/**
 * 多线程查询数据内部类
 */
@Slf4j
public class ReadExifInfoThread implements Callable<List<TableBloodRelationship>> {
    
    private int startNum;
    private int pageZize;
    /**
     * 源端系统
     */
    private final String dataSourceSystem;
    /**
     * 共享层表名
     */
    private final String sharedTableEn;
    /**
     * 应用名称
     */
    private final String appName;
    /**
     * 场景名称
     */
    private final String scenarioName;
    /**
     * 场景名称
     */
    private String provinceId;

    public ReadExifInfoThread(TableBloodRelationship queryCondition, int startNum, int pageZize) {
        this.dataSourceSystem = queryCondition.getDataSourceSystem();
        this.sharedTableEn = queryCondition.getSharedTableEn();
        this.appName = queryCondition.getAppName();
        this.scenarioName = queryCondition.getScenarioName();
        this.startNum = startNum;
        this.pageZize = pageZize;
        this.provinceId = queryCondition.getProvinceId();
    }

    @Override
    public List<TableBloodRelationship> call() {
        TableBloodRelationship queryCondition = new TableBloodRelationship();
        queryCondition.setStartNum(startNum * pageZize);
        queryCondition.setPageSize(pageZize);
        queryCondition.setDataSourceSystem(dataSourceSystem);
        queryCondition.setSharedTableEn(sharedTableEn);
        queryCondition.setScenarioName(scenarioName);
        queryCondition.setAppName(appName);
        queryCondition.setProvinceId(provinceId);
        long startTime = System.currentTimeMillis();
        List<TableBloodRelationship> exifInfoList = null;
        try {
            //从数据库查询要写入excle的数据
            // exifInfoList = exportExcelservice.selectTableBloodRelationshipExport(queryCondition);
            exifInfoList = selectTableBloodRelationshipExport(queryCondition);
            long endTime = System.currentTimeMillis();
            long spendTime = endTime - startTime;
            log.info(Thread.currentThread().getName() + "查询耗时：" + spendTime + "；分页是从【" + queryCondition.getStartNum() + "】开始");
        } catch (Exception e) {
            log.error("多线程查询导出数据失败", e);
        }
        return exifInfoList;
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
