package com.zwin.lib.database.entity;

/**
 * 表字段属性
 * @ClassName TableProperty
 * @author ZhengWx
 * @date 2016年5月1日 下午6:58:39
 */
public class TableProperty {
	/**
	 * 数据表字段名
	 */
	public String columnName;
	/**
	 * 数据表字段类型
	 */
	public String columnType;
	/**
	 * 字段名
	 */
	public String fieldName;
	/**
	 * 字段类型
	 */
	public String fieldType;
	/**
	 * 字段值
	 */
	public Object columnValue; 
	/**
	 * 是否是主键
	 */
	public boolean isPrimaryKey;
	/**
	 * 是否自增长
	 */
	public boolean isAutoincrement;
	/**
	 * 是否值唯一
	 */
	public boolean isUnique;
	/**
	 * 是否作为索引
	 */
	public boolean isIndex;
	/**
	 * 是否作为唯一索引
	 */
	public boolean isUniqueIndex;
	/**
	 * 是否允许值为空，默认允许为null
	 */
	public boolean isNotNull = false;
}
