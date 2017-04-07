package com.zwin.lib.database;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zwin.lib.database.anno.DatabaseHelper;
import com.zwin.lib.database.entity.DatabaseInfo;
import com.zwin.lib.database.entity.FieldType;
import com.zwin.lib.database.entity.TableProperty;
import com.zwin.lib.database.util.DatabaseUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数据库管理类<br>
 * 支持对数据库的增、删、改、查等基本操作。<br>
 * <li>如果要指定保存或打开数据库的路径，构造方法context参数传DatabaseContext()对象
 * <li>如果要打开assets目录下的db文件，先将文件拷到SD卡里或通过DatabaseUtil.copyAssetsDbToSystemDir()拷到/data/data/...目录下<br>
 * </br>使用示例如下：
 * <pre>
 * DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);
 * databaseManager.insert(bean);
 * @see DatabaseContext
 * @see DatabaseUtils
 * @author ZhengWx
 */
public class DatabaseManager extends ZWinDAO {
	public static final String TAG = "DatabaseManager";
	private DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
	private DbUpdateListener updateListener;
	
	/**
	 * 数据库管理类
	 * @author ZhengWx
	 * @date 2017年3月13日 下午2:47:54
	 * @param context 上下文对象，如果需要指定数据库路径，传DatabaseContext()对象
	 * @see DatabaseContext
	 * @since 1.0
	 */
	public DatabaseManager(Context context) {
		super(context);
    }

	/**
	 * 初始化
	 * @author ZhengWx
	 * @date 2017年2月9日 下午2:29:39
	 * @param databasePro
	 * @since 1.0
	 */
	private synchronized void create(DatabaseInfo databasePro) {
		if (databasePro == null) {
			Log.e(TAG, "databasePro == null");
			return;
		}
		
		super.create(databasePro.databaseName, databasePro.version, databasePro.tableName, updateListener);
		if (!super.isTableExist()) {			
			List<String> sqls = makeSQL(databasePro);
			if (sqls != null) {
				for (String sql : sqls) {
					super.execSQL(sql);
				}
			}
		}
	}
	
	/**
	 * 插入数据
	 * @author ZhengWx
	 * @date 2016年10月3日 下午4:35:44
	 * @param obj 插入的数据对象
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean insert(Object obj) {
		DatabaseInfo info = databaseHelper.makePropertyAndValue(context, obj);
		if (info == null) {
			return false;
		}
		create(info);
		return super.insert(info.cValues);
	}
	
	/**
	 * 插入数据集合
	 * @author ZhengWx
	 * @date 2016年10月3日 下午4:36:20
	 * @param objs 插入的数据对象集合
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean insertList(List<?> objs) {
		if (objs == null || objs.size() == 0) {
			return false;
		}
		
		int size = objs.size();
		List<ContentValues> contentValues = new ArrayList<ContentValues>();
		for (int i = 0; i < size; i++) {
			DatabaseInfo info = databaseHelper.makePropertyAndValue(context, objs.get(i));
			if (info == null) {
				continue;
			}
			
			if (i == 0) {
				create(info);
			}
			
			contentValues.add(info.cValues);
		}
		
		return super.insert(contentValues);
	}
	
	/**
	 * 指定更新条件及要更新的字段进行数据更新
	 * @author ZhengWx
	 * @date 2016年8月22日 下午2:22:37
	 * @param obj 更新的数据对象
	 * @param what 要更新的表字段名集合
	 * @param where 更新条件，如："username='张三'"，或"name like '%设计模式'"等条件语句。
	 * <br>如果where的值为null，则以主键作为update的条件
	 * <br><b><li>注意：如果where中有boolean类型字段，值需转成1或0，即：true-1，false-0
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean updateWhatByWhere(Object obj, List<String> what, String where) {
		DatabaseInfo info = databaseHelper.makePropertyAndValue(context, obj, what);
		if (info == null) {
			return false;
		}
		create(info);
		StringBuilder whereClause = null;
		String[] whereArgs = null;
		
		if (TextUtils.isEmpty(where)) {
			
			if (info.primaryKeys == null || info.primaryKeys.size() < 0) {
				Log.e(TAG, "数据表中未指定主键，必须至少有一个主键作为update的条件");
				return false;
			}
			
			int size = info.primaryKeys.size();
			int i = 0;
			for (Map.Entry<String, Object> entry : info.primaryKeys.entrySet()) {
				String primaryKeyName = entry.getKey();
				Object primaryKeyValue = entry.getValue();
				if (primaryKeyValue == null) {
					Log.e(TAG, "主键["+primaryKeyName+"] = "+primaryKeyValue+"，空值不能作为update的条件");
					return false;
				}
				
				if (whereArgs == null) {
					whereArgs = new String[size];
				}
				if (whereClause == null) {
					whereClause = new StringBuilder();
				}
				whereArgs[i] = String.valueOf(primaryKeyValue);
				whereClause.append(primaryKeyName);
				whereClause.append(" = ?");
				if (i < size-1) {					
					whereClause.append(" and ");
				}
				i++;
			}
		} else {
			whereClause = new StringBuilder(where);
		}
		return super.update(info.cValues, whereClause.toString(), whereArgs);
	}
	
	/**
	 * 指定更新条件进行数据更新
	 * @author ZhengWx
	 * @date 2016年8月22日 下午2:24:14
	 * @param obj 更新的数据对象
	 * @param where 更新条件，如："username='张三'"，或"name like '%设计模式'"等条件语句。
	 * <br>如果where的值为null，则以主键作为update的条件
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean updateByWhere(Object obj, String where) {
		return updateWhatByWhere(obj, null, where);
	}
	
	/**
	 * 以主键作为更新条件进行数据更新
	 * @author ZhengWx
	 * @date 2016年8月22日 下午2:22:43
	 * @param obj 更新的数据对象
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean update(Object obj) {
		return updateWhatByWhere(obj, null, null);
	}
	
	/**
	 * 指定条件进行数据删除
	 * @author ZhengWx
	 * @date 2016年9月25日 下午9:09:02
	 * @param clazz 表数据的class对象
	 * @param where 删除条件，如："username='张三'"，或"name like '%设计模式'"等条件语句。
	 * <br><b><li>注意：如果where中有boolean类型字段，值需转成1或0，即：true-1，false-0
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean deleteByWhere(Class<?> clazz, String where) {
		DatabaseInfo info = databaseHelper.makePropertyForTable(context, clazz);
		if (info == null) {
			return false;
		}
		create(info);
		
		return super.delete(where, null);
	}
	
	/**
	 * 删除Bean对应的数据表中所有的数据
	 * @author ZhengWx
	 * @date 2016年9月25日 下午9:09:28
	 * @param clazz 表数据的class对象
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean deleteAll(Class<?> clazz) {
		DatabaseInfo info = databaseHelper.makePropertyForTable(context, clazz);
		if (info == null) {
			return false;
		}
		create(info);
		
		return super.delete(null, null);
	}
	
	/**
	 * 以主键作为删除条件进行数据删除
	 * <br><b><li>注意：该方法仅适用有且只有一个主键时，联合主键不适用
	 * @author ZhengWx
	 * @date 2016年9月25日 下午9:10:15
	 * @param clazz 表数据的class对象
	 * @param id 主键的值
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean deleteById(Class<?> clazz, Object id) {
		if (id == null) {
			Log.e(TAG, "主键值为null");
			return false;
		}
		DatabaseInfo info = databaseHelper.makePropertyForColumnName(context, clazz);
		if (info == null) {
			return false;
		}
		create(info);

		if (info.primaryKeys == null || info.primaryKeys.size() != 1) {
			Log.e(TAG, "必须有且只有一个主键作为deleteById的条件");
			return false;
		}
		
		String primaryKeyName = null;
		Iterator<String> keys = info.primaryKeys.keySet().iterator();
		primaryKeyName = keys.next();
		if (TextUtils.isEmpty(primaryKeyName)) {
			Log.e(TAG, "必须有且只有一个主键作为deleteById的条件");
			return false;
		}
		
		String whereClause = primaryKeyName+" = ?";
		String[] whereArgs = new String[]{ String.valueOf(id) };
	
		return super.delete(whereClause.toString(), whereArgs);
	}
	
	/**
	 * 删除指定数据
	 * @author ZhengWx
	 * @date 2017年3月10日 下午3:36:00
	 * @param obj 删除数据的对象
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean delete(Object obj) {
		if (obj == null) {
			return false;
		}
		DatabaseInfo info = databaseHelper.makePropertyAndValue(context, obj);
		if (info == null) {
			return false;
		}
		create(info);
		
		int size = info.propertys.size();
		StringBuilder whereClause = new StringBuilder();
		String[] whereArgs = new String[size];
		
		for (int i = 0; i < size; i++) {
			TableProperty tablePro = info.propertys.get(i);
			if (tablePro == null) {
				return false;
			}
			String columnValue = null;
			switch (FieldType.getTypeByName(tablePro.fieldType)) {
			case BOOLEAN:
				columnValue = (Boolean)tablePro.columnValue?"1":"0";
				break;

			default:
				columnValue = String.valueOf(tablePro.columnValue);
				break;
			}
			whereArgs[i] = columnValue;
			whereClause.append(tablePro.columnName);
			whereClause.append(" = ?");
			if (i < size-1) {					
				whereClause.append(" and ");
			}
		}
		return super.delete(whereClause.toString(), whereArgs);
	}
	
	/**
	 * 删除表
	 * @author ZhengWx
	 * @date 2017年3月13日 上午9:24:07
	 * @param clazz 表数据的class对象
	 * @since 1.0
	 */
	public synchronized void deleteTable(Class<?> clazz) {
		DatabaseInfo info = databaseHelper.makePropertyForTable(context, clazz);
		if (info == null) {
			return;
		}
		create(info);
		execSQL("DROP TABLE "+tableName);
	}
	
	/**
	 * 指定条件进行数据查询
	 * @author ZhengWx
	 * @date 2016年10月3日 下午6:22:24
	 * @param clazz 表数据的class对象
	 * @param where 查询条件，如："username='张三'"，或"name like '%设计模式'"等条件语句。
	 * <br><b><li>注意：如果where中有boolean类型字段，值需转成1或0，即：true-1，false-0
	 * @param orderBy 排序语句，如："id desc"
	 * @return
	 * @since 1.0
	 */
	public synchronized <T> List<T> queryByWhere(Class<T> clazz, String where, String orderBy) {
		DatabaseInfo info = databaseHelper.makePropertyForColumnName(context, clazz);
		if (info == null) {
			return null;
		}
		create(info);
		
		return super.query(clazz, info, where, orderBy);
	}
	
	/**
	 * 指定条件进行数据查询
	 * @author ZhengWx
	 * @date 2016年10月3日 下午6:22:24
	 * @param clazz 表数据的class对象
	 * @param where 查询条件，如："username='张三'"，或"name like '%设计模式'"等条件语句。
	 * <br><b><li>注意：如果where中有boolean类型字段，值需转成1或0，即：true-1，false-0
	 * @return
	 * @since 1.0
	 */
	public synchronized <T> List<T> queryByWhere(Class<T> clazz, String where) {
		return queryByWhere(clazz, where, null);
	}
	
	/**
	 * 通过主键查询
	 * <br><b><li>注意：该方法仅适用有且只有一个主键时，联合主键不适用
	 * @author ZhengWx
	 * @date 2017年3月9日 下午10:35:49
	 * @param clazz 表数据的class对象
	 * @param id 主键的值
	 * @return
	 * @since 1.0
	 */
	public synchronized <T> T queryById(Class<T> clazz, Object id) {
		if (id == null) {
			Log.e(TAG, "主键值为null");
			return null;
		}
		DatabaseInfo info = databaseHelper.makePropertyForColumnName(context, clazz);
		if (info == null) {
			return null;
		}
		create(info);
		
		if (info.primaryKeys == null || info.primaryKeys.size() != 1) {
			Log.e(TAG, "必须有且只有一个主键作为queryById的条件");
			return null;
		}
		
		String primaryKeyName = null;
		Iterator<String> keys = info.primaryKeys.keySet().iterator();
		primaryKeyName = keys.next();
		if (TextUtils.isEmpty(primaryKeyName)) {
			Log.e(TAG, "必须有且只有一个主键作为queryById的条件");
			return null;
		}
		
		String selection = primaryKeyName+" = "+String.valueOf(id);
		List<T> resultList = super.query(clazz, info, selection, null);
		if (resultList != null && resultList.size() == 1) {
			return resultList.get(0);
		}
		
		return null;
	}

	/**
	 * 查询表所有数据
	 * @author ZhengWx
	 * @date 2017年3月17日 上午11:33:44
	 * @param clazz 表数据的class对象
	 * @return
	 * @since 1.0
	 */
	public synchronized <T> List<T> queryAll(Class<T> clazz) {
		return queryByWhere(clazz, null);
	}
	
	/**
	 * 根据唯一索引更新或插入数据
	 * <br>如果obj中唯一索引字段的值与表中的一致，则进行数据更新，否则进行数据插入
	 * @author ZhengWx
	 * @date 2017年3月16日 上午9:42:43
	 * @param obj 插入或更新数据的对象
	 * @return
	 * @since 1.0
	 */
	public synchronized boolean replace(Object obj) {
		DatabaseInfo info = databaseHelper.makePropertyAndValue(context, obj);
		if (info == null) {
			return false;
		}
		create(info);
		return super.replace(info.cValues);
	}
	
	/**
	 * 获取表的所有列名
	 * @author ZhengWx
	 * @date 2017年3月12日 下午5:21:11
	 * @param clazz 表数据的class对象
	 * @return
	 * @since 1.0
	 */
	public synchronized String[] getAllColumnNames(Class<?> clazz) {
		DatabaseInfo info = databaseHelper.makePropertyForTable(context, clazz);
		if (info == null) {
			return null;
		}
		create(info);
		
		return super.getAllColumnNames();
	}
	
	/**
	 * 判断某张表是否存在
	 * @author ZhengWx
	 * @date 2017年3月16日 上午11:27:42
	 * @param clazz 表数据的class对象
	 * @return
	 * @since 1.0
	 */
	public boolean isTableExist(Class<?> clazz) {
		DatabaseInfo info = databaseHelper.makePropertyForTable(context, clazz);
		if (info == null) {
			return false;
		}
		create(info);
		
		return super.isTableExist();
	}
	
	/**
	 * 生成SQL语句
	 * @author ZhengWx
	 * @date 2017年3月9日 上午10:32:36
	 * @param databasePro
	 * @return
	 * @since 1.0
	 */
	private List<String> makeSQL(DatabaseInfo databasePro) {
		StringBuilder tableSQL = new StringBuilder();
		if (databasePro.propertys == null) {
			return null;
		}
		try {
			StringBuilder primaryKey = null;	// 主键
			StringBuilder indexColumn = null;	// 索引
			StringBuilder uniqueIndexColumn = null;	// 唯一索引
			int size = databasePro.propertys.size();
			for (int i = 0; i < size; i++) {
				TableProperty property = databasePro.propertys.get(i);
				if (!TextUtils.isEmpty(property.columnName) && !TextUtils.isEmpty(property.columnType)) {
                	tableSQL.append(property.columnName);
                	tableSQL.append(" ");
                	tableSQL.append(property.columnType);
                	
                	if (databasePro.primaryKeys != null && databasePro.primaryKeys.size() == 1) {
                		if (property.isPrimaryKey) {
                    		tableSQL.append(" PRIMARY KEY");
                    	}
                    	
                    	if (property.isAutoincrement) {
                    		tableSQL.append(" AUTOINCREMENT");
                    	}
                	} else {
                    	if (property.isPrimaryKey) {
	                		if (primaryKey == null) {
	                			primaryKey = new StringBuilder();
	                		}
	                		primaryKey.append(property.columnName);
	                		primaryKey.append(", ");
                    	}
                	}
                	
                	if (property.isNotNull) {
                		tableSQL.append(" NOT NULL");
                	}
                	if (property.isUnique) {
                		tableSQL.append(" UNIQUE");
                	}

                	if (property.isIndex) {
                		if (indexColumn == null) {
                			indexColumn = new StringBuilder(); 
                		}
                		indexColumn.append(property.columnName);
                		indexColumn.append(", ");
                	}
                	if (property.isUniqueIndex) {
                		if (uniqueIndexColumn == null) {
                			uniqueIndexColumn = new StringBuilder();
                		}
                		uniqueIndexColumn.append(property.columnName);
                		uniqueIndexColumn.append(", ");
                	}
                	tableSQL.append(", ");
                }
			}
			
			List<String> sqls = new ArrayList<String>();
			
			// 创建表格SQL
			if (tableSQL!= null && tableSQL.length() > 0) {
	        	tableSQL.delete(tableSQL.length()-2, tableSQL.length());
	        	StringBuilder createTableSQL = null;
	        	if (primaryKey != null && primaryKey.length() > 0) {	// 设置主键
	        		primaryKey.delete(primaryKey.length()-2, primaryKey.length());
	        		createTableSQL = new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS %s(%s, PRIMARY KEY(%s));", 
		        			databasePro.tableName, tableSQL.toString(), primaryKey.toString()));
	        	} else {
	        		createTableSQL = new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS %s(%s);", databasePro.tableName, tableSQL.toString()));
	        	}
	        	sqls.add(createTableSQL.toString());
	        	Log.d(TAG, "createTableSQL = "+createTableSQL);
	        }
			// 创建索引SQL
			if (indexColumn != null && indexColumn.length() > 0) {
				indexColumn.delete(indexColumn.length()-2, indexColumn.length());
				String createIndexSQL = String.format("CREATE INDEX IF NOT EXISTS index_%s ON %s (%s);", databasePro.tableName, databasePro.tableName, indexColumn.toString());
				sqls.add(createIndexSQL);
				Log.d(TAG, "createIndexSQL = "+createIndexSQL);
			}
			// 创建唯一索引SQL
			if (uniqueIndexColumn != null && uniqueIndexColumn.length() > 0) {
				uniqueIndexColumn.delete(uniqueIndexColumn.length()-2, uniqueIndexColumn.length());
				String createUniqueIndexSQL = String.format("CREATE UNIQUE INDEX IF NOT EXISTS index_%s ON %s (%s);", databasePro.tableName, databasePro.tableName, uniqueIndexColumn.toString());
				sqls.add(createUniqueIndexSQL);
				Log.d(TAG, "createUniqueIndexSQL = "+createUniqueIndexSQL);
			}
			
			return sqls;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void setDbUpdateListener(DbUpdateListener updateListener) {
		this.updateListener = updateListener;
	}
	
}
