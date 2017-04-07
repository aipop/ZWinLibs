package com.zwin.lib.database.entity;

import android.content.ContentValues;

import java.util.List;
import java.util.Map;

/**
 * 数据库信息
 * @ClassName DatabaseProperty
 * @author ZhengWx
 * @date 2016年5月1日 下午8:58:32
 */
public class DatabaseInfo {
	/**
	 * 数据库名称
	 */
	public String databaseName;
	/**
	 * 版本号
	 */
	public int version = 1;
	/**
	 * 表格名称
	 */
	public String tableName;
	/**
	 * 表字段属性集
	 */
	public List<TableProperty> propertys;
	public ContentValues cValues;
	/**
	 * 主键字段名-值，多个则为联合主键
	 */
	public Map<String, Object> primaryKeys;

}
