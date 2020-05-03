package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static class ListEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "list";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_DATE = "data";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_AUTHORITY ="authority";
    }

    private TodoContract() {
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ListEntry.TABLE_NAME + " (" +
                    ListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ListEntry.COLUMN_CONTENT + " TEXT," +
                    ListEntry.COLUMN_DATE + " DATE," +
                    ListEntry.COLUMN_AUTHORITY + " INT," +
                    ListEntry.COLUMN_STATE + " INT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ListEntry.TABLE_NAME;

}
