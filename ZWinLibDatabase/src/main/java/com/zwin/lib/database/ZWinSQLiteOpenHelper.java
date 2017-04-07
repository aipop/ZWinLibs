package com.zwin.lib.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作类
 * @author ZhengWx
 * @date 2015年8月26日 下午5:32:28
 */
public abstract class ZWinSQLiteOpenHelper extends SQLiteOpenHelper {
	
	public ZWinSQLiteOpenHelper(Context context, int version, String databaseName) {
		super(context, databaseName, null, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	}
}
