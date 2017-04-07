package com.zwin.lib.database.entity;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

/**
 * 类字段类型与数据库字段类型对应关系
 * @ClassName FieldType
 * @author ZhengWx
 * @date 2016年5月1日 下午5:40:18
 */
public enum FieldType {
	INT("int", "INTEGER"),
	BOOLEAN("boolean", "BOOLEAN"),
	CHAR("char", "CHAR"),
	FLOAT("float", "FLOAT"),
	DOUBLE("double", "REAL"),
	LONG("long", "LONG"),
	SHORT("short", "SMALLINT"),
	BYTE("byte", "SMALLINT"),
	BYTES("byte[]", "BLOB"),
	OBJ_INTEGER("java.lang.Integer", "INTEGER"),
	OBJ_BOOLEAN("java.lang.Boolean", "BOOLEAN"),
	OBJ_CHARACTER("java.lang.Character", "CHAR"),
	OBJ_STRING("java.lang.String", "NVARCHAR"),
	OBJ_FLOAT("java.lang.Float", "FLOAT"),
	OBJ_DOUBLE("ava.lang.Double", "REAL"),
	OBJ_LONG("java.lang.Long", "LONG"),
	OBJ_SHORT("java.lang.Short", "SMALLINT"),
	OBJ_BYTE("java.lang.Byte", "SMALLINT"),
	OBJ_BYTES("java.lang.Byte[]", "BLOB");
	
	private String fieldType;	// 字段类型
	private String columnType;	// 数据库字段类型
	private FieldType(String fieldType, String columnType) {
		this.fieldType = fieldType;
		this.columnType = columnType;
	}

	public String getFieldType() {
		return fieldType;
	}

	public String getColumnType() {
		return columnType;
	}
	
	private static final Map<String, FieldType> ENUMLIST = new HashMap<String, FieldType>();
    static {
        for(FieldType tag : values()) {
        	ENUMLIST.put(tag.getFieldType(), tag);
        }
    }
    
    public static String getColumnType(String fieldType) {
    	if (TextUtils.isEmpty(fieldType)) {
    		return null;
    	}
    	
    	FieldType field = ENUMLIST.get(fieldType);
    	
    	if (field == null) {
    		return null;
    	}
    	
    	return field.getColumnType();
    }
    
    /**
     * 根据字段名获取字段类型
     * @author ZhengWx
     * @date 2016年10月6日 下午12:41:17
     * @param typeName
     * @return
     * @since 1.0
     */
    public static FieldType getTypeByName(String typeName) {
    	return ENUMLIST.get(typeName);
    }
	
}
