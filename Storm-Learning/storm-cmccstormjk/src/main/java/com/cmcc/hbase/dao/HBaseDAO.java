package com.cmcc.hbase.dao;


import java.util.List;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

public interface HBaseDAO {

    void save(Put put, String tableName);

    void insert(String tableName, String rowKey, String family, String quailifer, String value);

    void insert(String tableName, String rowKey, String family, String quailifer[], String value[]);

    void save(List<Put> Put, String tableName);

    Result getOneRow(String tableName, String rowKey);

    List<Result> getRows(String tableName, String rowKey_like);

    List<Result> getRows(String tableName, String rowKeyLike, String cols[]);

    List<Result> getRows(String tableName, String startRow, String stopRow);

    void deleteRecords(String tableName, String rowKeyLike);

    void deleteTable(String tableName);

    void createTable(String tableName, String[] columnFamilys);
}
