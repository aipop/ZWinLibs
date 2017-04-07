package com.zwin.lib.database.anno;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zwin.lib.database.entity.DatabaseInfo;
import com.zwin.lib.database.entity.FieldType;
import com.zwin.lib.database.entity.TableProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper {
	public static final String TAG = "DatabaseHelper";
	
	private static DatabaseHelper databaseHelper;

	private DatabaseHelper() {
	}

	public static DatabaseHelper getInstance() {
		if (databaseHelper == null) {
			synchronized (DatabaseHelper.class) {
				if (databaseHelper == null) {
					databaseHelper = new DatabaseHelper();
				}
			}
		}

		return databaseHelper;
	}
	
	/**
	 * 解析表信息和字段
	 * @author ZhengWx
	 * @date 2017年3月16日 下午1:26:15
	 * @param context 上下文对象
	 * @param obj 表数据对象
	 * @param theColumnNames 指定的表字段名，如果不为空，则只有是主键或包含在theColumnNames里的表字段会别解析
	 * @return
	 * @since 1.0
	 */
	public DatabaseInfo makePropertyAndValue(Context context, final Object obj, List<String> theColumnNames) {
		if (obj == null) {
			Log.e(TAG, "object == null");
			return null;
		}
		final DatabaseInfo databaseInfo = makePropertyForTable(context, obj.getClass());
		final List<TableProperty> propertys = new ArrayList<TableProperty>();
		
		if (databaseInfo.cValues == null) {	
			databaseInfo.cValues = new ContentValues();
		}
		/*
		Class<?> clazz = obj.getClass();
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {	// 遍历字段
				field.setAccessible(true);
				TableProperty property = null;
				String fieldTypeName = field.getType().getCanonicalName();
				String columnName = null;	// 表字段名称
				try {
	        		
					if (field.isAnnotationPresent(Property.class)) {
						Property column = (Property) field.getAnnotation(Property.class);
						if (!column.isTableColumn()) {
							continue;
						}
						if (TextUtils.isEmpty(column.name())) {
							columnName = field.getName();
						} else {
							columnName = column.name();
						}
						
						if (column.isPrimaryKey()) {
							if (databaseInfo.primaryKeys == null) {
								databaseInfo.primaryKeys = new HashMap<String, Object>();
							}
							databaseInfo.primaryKeys.put(columnName, field.get(obj));
						}
						
						if (theColumnNames != null && theColumnNames.size() > 0 && !theColumnNames.contains(columnName)) {
							continue;
						}
						
						if (property == null) {
		    				property = new TableProperty();
		    			}
						property.isPrimaryKey = column.isPrimaryKey();
						property.isAutoincrement = column.isAutoincrement();
						property.isUnique = column.isUnique();
						property.isIndex = column.isIndex();
						property.isUniqueIndex = column.isUniqueIndex();
						property.isNotNull = column.isNotNull();

					} else {
						columnName = field.getName();
						if (theColumnNames != null && theColumnNames.size() > 0 && !theColumnNames.contains(columnName)) {
							continue;
						}
						property = new TableProperty();
					}
					property.fieldType = fieldTypeName;
	                property.fieldName = field.getName();
	                property.columnName = columnName;
	                property.columnType = FieldType.getColumnType(fieldTypeName);
	                if (!property.isPrimaryKey || !property.isAutoincrement) {	// 自增主键必须值为null，否则不能自增
	                	property.columnValue = field.get(obj);
	                	makeContentValues(databaseInfo.cValues, property.columnValue, property.columnName);
	                }
				} catch (Exception e) {
	        		e.printStackTrace();
	        	}
				
	            if (property != null) {
	    			propertys.add(property);
	    		}
			}
		}
		*/
		makeProperty(obj.getClass(), theColumnNames, new MakePropertyCallback() {
			
			@Override
			public void makePrimaryKey(String columnName, Field field) throws Exception {
				if (databaseInfo.primaryKeys == null) {
					databaseInfo.primaryKeys = new HashMap<String, Object>();
				}
				databaseInfo.primaryKeys.put(columnName, field.get(obj));
			}
			
			@Override
			public void makeFieldDone(TableProperty property, Field field) {
				try {
					if (!property.isPrimaryKey || !property.isAutoincrement) {	// 自增主键的字段值必须为null，否则不能自增
	                	property.columnValue = field.get(obj);
	                	makeContentValues(databaseInfo.cValues, property.columnValue, property.columnName);
	                }
				} catch (Exception e) {
				}
				if (property != null) {
	    			propertys.add(property);
	    		}
			}
		});
		
		if (propertys.size() > 0) {
        	databaseInfo.propertys = propertys;
        }
		
		return databaseInfo;
	}
	
	/**
	 * 解析表信息和字段
	 * @author ZhengWx
	 * @date 2017年3月16日 下午1:33:37
	 * @param context 上下文对象
	 * @param obj 表数据对象
	 * @return
	 * @since 1.0
	 */
	public DatabaseInfo makePropertyAndValue(Context context, Object obj) {
		return makePropertyAndValue(context, obj, null);
	}
	
	/**
	 * 解析表信息
	 * @author ZhengWx
	 * @date 2017年3月16日 下午2:03:53
	 * @param context 上下文对象
	 * @param clazz 表数据的class对象
	 * @return
	 * @since 1.0
	 */
	public DatabaseInfo makePropertyForTable(Context context, Class<?> clazz) {
		if (clazz == null) {
			Log.e(TAG, "class == null");
			return null;
		}
		DatabaseInfo databaseInfo = new DatabaseInfo();
		for (Class<?> tempClazz = clazz; tempClazz != Object.class; tempClazz = tempClazz.getSuperclass()) {
			Table tablePro = tempClazz.getAnnotation(Table.class);
			if (tablePro == null) {
				continue;
			}
			if (TextUtils.isEmpty(databaseInfo.databaseName)) {
				databaseInfo.databaseName = tablePro.databaseName();
			}

			if (TextUtils.isEmpty(databaseInfo.tableName)) {
				databaseInfo.tableName = tablePro.tableName();
			}

			if (databaseInfo.version == 1) {
				databaseInfo.version = tablePro.version();
			}

		}

		if (TextUtils.isEmpty(databaseInfo.databaseName)) {
			databaseInfo.databaseName = getDefaultDatabaseName(context);
		}
		if (TextUtils.isEmpty(databaseInfo.tableName)) {
			databaseInfo.tableName = getDefaultTableName(clazz);
		}

		return databaseInfo;
	}
	
	/**
	 * 解析表字段
	 * @author ZhengWx
	 * @date 2017年3月16日 下午2:04:47
	 * @param context 上下文对象
	 * @param clazz 表数据class对象
	 * @return
	 * @since 1.0
	 */
	public DatabaseInfo makePropertyForColumnName(Context context, Class<?> clazz) {
		final DatabaseInfo databaseInfo = makePropertyForTable(context, clazz);
		if (databaseInfo == null) {
			return null;
		}
		final List<TableProperty> propertys = new ArrayList<TableProperty>();
		/*
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			TableProperty property = null;
			String fieldTypeName = field.getType().getCanonicalName();
			try {
                if (property == null) {
    				property = new TableProperty();
    			}
                
                property.fieldType = fieldTypeName;
                property.fieldName = field.getName();
                property.columnName = field.getName();
                property.columnType = FieldType.getColumnType(fieldTypeName);
				if (field.isAnnotationPresent(Property.class)) {

					Property column = (Property) field.getAnnotation(Property.class);
					if (!column.isTableColumn()) {
						continue;
					}
					
					if (!TextUtils.isEmpty(column.name())) {
						property.columnName = column.name();
					}
					property.isPrimaryKey = column.isPrimaryKey();
					property.isAutoincrement = column.isAutoincrement();
					property.isUnique = column.isUnique();
					property.isIndex = column.isIndex();
					property.isUniqueIndex = column.isUniqueIndex();
					property.isNotNull = column.isNotNull();

					if (property.isPrimaryKey) {
						if (databaseInfo.primaryKeys == null) {
							databaseInfo.primaryKeys = new HashMap<String, Object>();
						}
						databaseInfo.primaryKeys.put(property.columnName, null);
					}
				}
			} catch (Exception e) {
        		e.printStackTrace();
        	}
			
            if (property != null) {
    			propertys.add(property);
    		}
		}
		*/
		makeProperty(clazz, null, new MakePropertyCallback() {
			
			@Override
			public void makePrimaryKey(String columnName, Field field) throws Exception {
				if (databaseInfo.primaryKeys == null) {
					databaseInfo.primaryKeys = new HashMap<String, Object>();
				}
				databaseInfo.primaryKeys.put(columnName, null);
			}
			
			@Override
			public void makeFieldDone(TableProperty property, Field field) {
				if (property != null) {
	    			propertys.add(property);
	    		}
			}
		});
		
		if (propertys.size() > 0) {
        	databaseInfo.propertys = propertys;
        }
		
		return databaseInfo;
	}
	
	private void makeProperty(Class<?> clazz, List<String> theColumnNames, MakePropertyCallback callback) {
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {	// 遍历字段

				if (field.isSynthetic()) {	// 过滤自动生成的变量，如：$change
					continue;
				}
				int modifiers = field.getModifiers();
				if (modifiers == Modifier.FINAL
						|| modifiers == (Modifier.PUBLIC+Modifier.FINAL)
						|| modifiers == (Modifier.PROTECTED+Modifier.FINAL)
						|| modifiers == (Modifier.PRIVATE+Modifier.FINAL)
						|| modifiers == (Modifier.PUBLIC+Modifier.STATIC+Modifier.FINAL)
						|| modifiers == (Modifier.PROTECTED+Modifier.STATIC+Modifier.FINAL)
						|| modifiers == (Modifier.PRIVATE+Modifier.STATIC+Modifier.FINAL)) {	// 过滤常量
					continue;
				}
				field.setAccessible(true);
				TableProperty property = null;
				String fieldTypeName = field.getType().getCanonicalName();
				String columnName = null;	// 表字段名称
				try {
	        		
					if (field.isAnnotationPresent(Property.class)) {
						Property column = field.getAnnotation(Property.class);
						if (!column.isTableColumn()) {
							continue;
						}
						if (TextUtils.isEmpty(column.name())) {
							columnName = field.getName();
						} else {
							columnName = column.name();
						}
						
						if (column.isPrimaryKey()) {
							callback.makePrimaryKey(columnName, field);
						}
						
						if (theColumnNames != null && theColumnNames.size() > 0 && !theColumnNames.contains(columnName)) {
							continue;
						}
						
						if (property == null) {
		    				property = new TableProperty();
		    			}
						property.isPrimaryKey = column.isPrimaryKey();
						property.isAutoincrement = column.isAutoincrement();
						property.isUnique = column.isUnique();
						property.isIndex = column.isIndex();
						property.isUniqueIndex = column.isUniqueIndex();
						property.isNotNull = column.isNotNull();

					} else {
						columnName = field.getName();
						if (theColumnNames != null && theColumnNames.size() > 0 && !theColumnNames.contains(columnName)) {
							continue;
						}
						property = new TableProperty();
					}
					property.fieldType = fieldTypeName;
	                property.fieldName = field.getName();
	                property.columnName = columnName;
	                property.columnType = FieldType.getColumnType(fieldTypeName);
				} catch (Exception e) {
	        		e.printStackTrace();
	        	}
				callback.makeFieldDone(property, field);
			}
		}
	}
	
	/**
	 * 把字段存到ContentValues里
	 * @author ZhengWx
	 * @date 2017年3月16日 下午2:06:25
	 * @param cValues
	 * @param value 字段值
	 * @param columnName 表字段名
	 * @since 1.0
	 */
	private void makeContentValues(ContentValues cValues, Object value, String columnName) {
		if (!TextUtils.isEmpty(columnName)) {
			if (value == null) {
				cValues.put(columnName, (String)null);
			}
			if (value instanceof Integer) {
				cValues.put(columnName, (Integer)value);
			} else if (value instanceof Boolean) {
				cValues.put(columnName, (Boolean)value);
			}  else if (value instanceof String || value instanceof Character) {
				cValues.put(columnName, (String)value);
			} else if (value instanceof Float) {
				cValues.put(columnName, (Float)value);
			} else if (value instanceof Double) {
				cValues.put(columnName, (Double)value);
			} else if (value instanceof Long) {
				cValues.put(columnName, (Long)value);
			} else if (value instanceof Short) {
				cValues.put(columnName, (Short)value);
			} else if (value instanceof Byte) {
				cValues.put(columnName, (Byte)value);
			} else if (value instanceof byte[]) {
				cValues.put(columnName, (byte[])value);
			}
		}
	}
	
	/**
	 * 默认数据库名称
	 * @author ZhengWx
	 * @date 2017年3月16日 下午2:05:56
	 * @param context
	 * @return
	 * @since 1.0
	 */
	public String getDefaultDatabaseName(Context context) {
		if (context == null) {			
			return null;
		}
		return context.getPackageName().replace(".", "_");
	}
	
	/**
	 * 默认表名称
	 * @author ZhengWx
	 * @date 2017年3月16日 下午2:06:09
	 * @param clazz
	 * @return
	 * @since 1.0
	 */
	public String getDefaultTableName(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		return clazz.getName().replace(".", "_");
	}
	
	public interface MakePropertyCallback {
		void makePrimaryKey(String columnName, Field field) throws Exception;
		void makeFieldDone(TableProperty property, Field field);
	}
	
}
