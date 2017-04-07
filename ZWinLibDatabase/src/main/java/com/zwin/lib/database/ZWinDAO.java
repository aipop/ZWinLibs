package com.zwin.lib.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.zwin.lib.database.anno.DatabaseHelper;
import com.zwin.lib.database.entity.DatabaseInfo;
import com.zwin.lib.database.entity.FieldType;
import com.zwin.lib.database.entity.TableProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ZWinDAO {
	private static final String TAG = "ZWinDAO";

	protected Context context;
	protected String tableName;		// 数据表名
	private ZWinSQLiteOpenHelper helper;
	private SQLiteDatabase database;
	
	public ZWinDAO(Context context) {
		this.context = context;
	}
	
	/**
	 * 初始化
	 * @author ZhengWx
	 * @date 2017年2月9日 下午2:28:28
	 * @param databaseName 数据库名称
	 * @param version 数据库版本号
	 * @param tableName 数据表名称
	 * @param updateListener 数据库版本升级监听器
	 * @since 1.0
	 */
	@SuppressLint("NewApi")
	protected void create(String databaseName, int version, String tableName, final DbUpdateListener updateListener) {
		this.tableName = tableName;
		try {
			if (helper != null && helper.getDatabaseName().equals(databaseName)) {
				if (database == null || !database.isOpen()) {
					database = helper.getWritableDatabase();
				}
			} else {
				helper = new ZWinSQLiteOpenHelper(context, version, databaseName) {

					@Override
					public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
						if (updateListener != null) {
							updateListener.onUpgrade(db, oldVersion, newVersion);
						}
					}
				};
				database = helper.getWritableDatabase();
			}
			
		} catch (Exception e) {
			Log.e(TAG, "初始化失败");
			e.printStackTrace();
			return;
		}
	}
	
	public void execSQL(String sql) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			
		}
		try {
			database.execSQL(sql);
		} catch (Exception e) {
			Log.e(TAG, "执行SQL语句 ["+sql+"] 时出现异常："+e.toString());
		}
	}
	
	/**
	 * 插入单个数值
	 * @author ZhengWx
	 * @date 2017年2月9日 下午2:30:31
	 * @param values
	 * @return
	 * @since 1.0
	 */
	protected boolean insert(ContentValues values) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return false;
		}
		
		long num = database.insert(tableName, null, values);
		Log.d(TAG, "insert result = "+num);
		close();

		return num >= 0;
	}
	
	/**
	 * 插入多个数值
	 * @author ZhengWx
	 * @date 2017年2月9日 下午2:42:46
	 * @param values
	 * @return true: 全部插入成功，false: 至少有一个插入失败
	 * @since 1.0
	 */
	protected boolean insert(List<ContentValues> values) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return false;
		}
		
		boolean isSuccess = true;
		for (ContentValues contentValues : values) {
			long num = database.insert(tableName, null, contentValues);
			Log.d(TAG, "insert result = " + num);
			if (num < 0) {
				Log.e(TAG, "插入值 ["+contentValues+"] 到表 ["+tableName+"] 时失败");
				isSuccess = false;
			}
		}
		close();
		
		return isSuccess;
	}

	/**
	 * 更新数据
	 * @author ZhengWx
	 * @date 2015-8-27 上午8:57:14
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 * @since 1.0
	 */
	protected boolean update(ContentValues values, String whereClause, String[] whereArgs) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return false;
		}
		int num = database.update(tableName, values, whereClause, whereArgs);
		Log.d(TAG, "update result = "+num);
		close();
		
		return num > 0;
	}

	protected boolean delete(String whereClause, String[] whereArgs) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return false;
		}
		
		int num = database.delete(tableName, whereClause, whereArgs);
		Log.d(TAG, "delete result = "+num);
		close();
		
		return num > 0;
	}
	
	/**
	 * 删除表
	 * @author ZhengWx
	 * @date 2016年9月25日 下午7:39:06
	 * @return
	 * @since 1.0
	 */
	protected boolean deleteTable() {
		if (context == null) {
			return false;
		}
		return context.deleteDatabase(DatabaseHelper.getInstance().getDefaultDatabaseName(context));
	}
	
	/**
	 * 删除默认数据库
	 * @author ZhengWx
	 * @date 2017年3月12日 下午5:52:42
	 * @return
	 * @since 1.0
	 */
	public boolean deleteDb() {
		if (context == null) {
			return false;
		}
		return context.deleteDatabase(DatabaseHelper.getInstance().getDefaultDatabaseName(context));
	}
	
	/**
	 * 某条数据是否存在，存在则更新否则插入
	 * @param values
	 * @return
	 */
	protected boolean replace(ContentValues values) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return false;
		}
		
		long num = database.replace(tableName, null, values);
		Log.d(TAG, "replace result = "+num);
		close();
		
		return num >= 0;
	}
	
	protected List<Map<String, String>> query(String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {

		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return null;
		}
		Cursor cursor = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			cursor = database.query(tableName, columns, selection,
					selectionArgs, groupBy, having, orderBy);
			int count = cursor.getColumnCount();
			while (cursor.moveToNext()) {

				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < count; i++) {
					String col_name = cursor.getColumnName(i);
					String col_value = cursor.getString(i);
					map.put(col_name, col_value);
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
			close();
		}

		return list;
	}

	protected <T> List<T> query(Class<T> clazz, DatabaseInfo info, String selection, String orderBy) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return null;
		}
		if (clazz == null) {
			return null;
		}
		Cursor cursor = null;
		try {
			cursor = database.query(tableName, null, selection, null, null, null, orderBy);
			if (cursor != null && cursor.getCount() > 0) {
				List<T> objs = new ArrayList<T>();
				while(cursor.moveToNext()) {	// 表行数据
					T obj = clazz.newInstance();
					int size = info.propertys.size();

					for (int j = 0; j < size; j++) {	// 表列数据(字段)
						TableProperty property = info.propertys.get(j);
						int columnIndex = cursor.getColumnIndex(property.columnName);
						if (columnIndex < 0) {
							continue;
						}
						FieldType fieldType = FieldType.getTypeByName(property.fieldType);
//				    	String tempFieldName = null;
//				    	if (fieldType == FieldType.BOOLEAN || fieldType == FieldType.OBJ_BOOLEAN) {	// boolean类型需特殊处理
//				    		tempFieldName = property.fieldName.substring(2, property.fieldName.length());
//				    	} else {
//				    		tempFieldName = property.fieldName;
//				    	}
//				    	String methodName = String.format("set%s%s",
//				    			tempFieldName.substring(0, 1).toUpperCase(Locale.CHINA),
//				    			tempFieldName.substring(1));
//				    	Method method = null;
						Field field = null;
						for (Class<?> tempClazz = clazz; tempClazz != Object.class; tempClazz = tempClazz.getSuperclass()) {
							try {
								field = tempClazz.getDeclaredField(property.fieldName);
								field.setAccessible(true);
								break;
//				    			method = tempClazz.getMethod(methodName, tempClazz.getDeclaredField(property.fieldName).getType());
//				    			method.setAccessible(true);					    		
							} catch (Exception e) {
							}
						}

//				    	if (method == null) {
//				    		Log.e(TAG, "没有找到 ["+property.fieldName+"] 对应的set方法：["+methodName+"(Argument value)]，-\n-检查该方法是否存在，是否声明成public，参数定义是否正确");
//				    		continue;
//				    	}
						if (field == null) {
							Log.e(TAG, "没有找到 ["+property.fieldName+"]，请检查该字段是否有在本类或父类中声明");
							continue;
						}
						switch (fieldType) {
							case OBJ_STRING:
								field.set(obj, cursor.getString(columnIndex));
//				    		method.invoke(obj, cursor.getString(columnIndex));
								break;

							case BOOLEAN:
							case OBJ_BOOLEAN:
								boolean booleanValue = cursor.getInt(columnIndex) == 1 ? true : false;
//				    		method.invoke(obj, booleanValue);
								field.set(obj, booleanValue);
								break;

							case BYTE:
							case OBJ_BYTE:
//				    		method.invoke(obj, (byte)cursor.getInt(columnIndex));
								field.set(obj, (byte)cursor.getInt(columnIndex));
								break;

							case BYTES:
							case OBJ_BYTES:
//				    		method.invoke(obj, cursor.getBlob(columnIndex));
								field.set(obj, cursor.getBlob(columnIndex));
								break;

							case CHAR:
							case OBJ_CHARACTER:
//				    		method.invoke(obj, (char)cursor.getInt(columnIndex));
								field.set(obj, (char)cursor.getInt(columnIndex));
								break;

							case DOUBLE:
							case OBJ_DOUBLE:
//				    		method.invoke(obj, cursor.getDouble(columnIndex));
								field.set(obj, cursor.getDouble(columnIndex));
								break;

							case FLOAT:
							case OBJ_FLOAT:
//				    		method.invoke(obj, cursor.getFloat(columnIndex));
								field.set(obj, cursor.getFloat(columnIndex));
								break;

							case INT:
							case OBJ_INTEGER:
//				    		method.invoke(obj, cursor.getInt(columnIndex));
								field.set(obj, cursor.getInt(columnIndex));
								break;

							case LONG:
							case OBJ_LONG:
//				    		method.invoke(obj, cursor.getLong(columnIndex));
								field.set(obj, cursor.getLong(columnIndex));
								break;

							case SHORT:
							case OBJ_SHORT:
//				    		method.invoke(obj, (short)cursor.getInt(columnIndex));
								field.set(obj, (short)cursor.getInt(columnIndex));
								break;
						}
					}
					objs.add(obj);
				}
				return objs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
			close();
		}

		return null;
	}

	/**
	 * 指定条件查询数据
	 * @author ZhengWx
	 * @date 2015-8-27 上午8:57:46
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @return
	 * @since 1.0
	 */
	protected List<Map<String, String>> query(String[] columns, String selection, String[] selectionArgs) {
		return query(columns, selection, selectionArgs, null, null, null);
	}

	/**
	 * 查询所有数据
	 * @author ZhengWx
	 * @date 2015-8-28 下午5:21:43
	 * @return
	 * @since 1.0
	 */
	protected List<Map<String, String>> query() {
		return query(null, null, null, null, null, null);
	}

	/**
	 * 查询某条数据是否存在
	 * @param selection
	 * @param selectionArgs
	 * @deprecated 用query方式代替
	 * @return
	 */
	protected boolean checkRecordExist(String selection, String[] selectionArgs) {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return false;
		}
		Cursor cursor = null;
		cursor = database.query(tableName, new String[]{"*"}, selection,
				selectionArgs, null, null, null);
		int count = cursor.getColumnCount();
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		close();

		return count > 0;
	}
	
	/**
	 * 获取所有的列名
	 * @author ZhengWx
	 * @date 2015-8-27 上午8:57:56
	 * @return
	 * @since 1.0
	 */
	protected String[] getAllColumnNames() {
		if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return null;
		}
		String[] columnNames = null;
		Cursor cursor = null;
		cursor = database.query(tableName, new String[] { "*" }, null,
				null, null, null, null);
		columnNames = cursor.getColumnNames();
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		close();
		
		return columnNames;
	}
	
	/**
     * 判断某张表是否存在
     * @return
     */
    protected boolean isTableExist() {
    	if (database == null) {
			Log.e(TAG, "未调create()方法进行初始化或初始化失败");
			return false;
		}
		if (TextUtils.isEmpty(this.tableName)) {
			return false;
		}
		Cursor cursor = null;
		try {
			// SQL语句不能大写
			String sql = String.format("select count(*) as c from sqlite_master where type ='table' and name ='%s' ", this.tableName);
			cursor = database.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				Log.d(TAG, "table count = "+count);
				return count > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != cursor && !cursor.isClosed()) {
				cursor.close() ;
			}
		}
        
		return false;
    }
	
	protected void close() {
		if (database != null) {
			database.close();
//			database = null;
		}
	}

}
