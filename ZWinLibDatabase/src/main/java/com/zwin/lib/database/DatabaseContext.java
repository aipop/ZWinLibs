package com.zwin.lib.database;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DatabaseContext extends ContextWrapper {
	private String databasePath;
	
	/**
	 * 上下文对象，指定数据库路径
	 * <br><b>注意：如果是SD卡路径，需加上读写权限：</b>
	 * <br>&lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/&gt;
	 * @author ZhengWx
	 * @date 2017年3月13日 下午2:57:36
	 * @param context 上下文对象
	 * @param databasePath 数据库路径
	 * @since 1.0
	 */
	public DatabaseContext(Context context, String databasePath) {
		super(context);
		this.databasePath = databasePath;
	}

	@Override
	public File getDatabasePath(String name) {
		if (!databasePath.startsWith("/")) {
			databasePath = "/"+databasePath;
		}
		if (!databasePath.endsWith("/")) {
			databasePath += "/";
		}
		String dbPath = databasePath + name;	// 数据库路径
		// 判断目录是否存在，不存在则创建该目录
		File dirFile = new File(databasePath);
		if (!dirFile.exists()) {
			if (!dirFile.mkdirs()) {
				Log.e("DatabaseContext", "创建数据库目录["+databasePath+"]时失败");
				return null;
			}
		}

		// 判断文件是否存在，不存在则创建该文件
		File dbFile = new File(dbPath);
		if (!dbFile.exists()) {
			try {
				if (!dbFile.createNewFile()) {
					return null;
				}
			} catch (IOException e) {
				Log.e("DatabaseContext", "创建数据库文件["+dbPath+"]时失败："+e.toString());
				e.printStackTrace();
				return null;
			}
		}

		return dbFile;
	}
	
	@Override  
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);  
        return result;  
    }  
      
    @Override  
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {  
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);  
        return result;  
    }
	
}