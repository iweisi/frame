package com.lvmoney.bigdata.canal.redis.application;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.client.*;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2019/7/3.
 */
public class CanalRedisApplication {
    public static void main(String args[]) {

        // 创建链接，hostname位canal服务器ip port位canal服务器端口，username，password可不填
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.1.171",
                11111), "example", "", "");
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            while (true) {
                Message message = connector.
                        getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    printEntry(message.getEntries());
                }


                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据

            }

        } finally {
            connector.disconnect();
        }
    }

    private static void printEntry(List<Entry> entrys) {
        int i = 0;
        for (Entry entry : entrys) {
            System.out.println("i=" + i++);
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }
            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }

            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));
            int f = 0;
            for (RowData rowData : rowChage.getRowDatasList()) {
                System.out.println("f=" + f++);
                System.out.println("-------> before");
                printColumn(rowData.getBeforeColumnsList());
                System.out.println("-------> after");
                printColumn(rowData.getAfterColumnsList());

                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }

    }

    /**
     * 打印变化的数据
     *
     * @param columns
     */
    private static void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    public Map<String, Object> build2Map(List<Entry> entrys) {
        Map<String, Object> result = new HashMap<>();
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }
            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }
            for (RowData rowData : rowChage.getRowDatasList()) {
                List<Column> columns = rowData.getAfterColumnsList();
                for (Column column : columns) {
                    result.put(column.getName(), column.getValue());
                }
            }
        }
        return result;
    }
}
