package com.zwin.lib.database;

import java.util.List;
import java.util.Map;

import android.content.ContentValues;

/**
 * 数据库访问操作
 * @ClassName DAOInterface
 * @author ZhengWx
 * @date 2015-8-27 上午8:56:25
 */
public interface IDAODefine {

	/**
	 * 执行SQL语句
	 * @author ZhengWx
	 * @date 2017年3月20日 上午9:36:22
	 * @param sql
	 * @return
	 * @since 1.0
	 */
	public boolean execSQL(String sql);
	
	/**
	 * 插入单个数值
	 * @author ZhengWx
	 * @date 2017年2月9日 下午2:30:31
	 * @param values
	 * @return
	 * @since 1.0
	 */
	public boolean insert(ContentValues values);
	
	/**
	 * 插入多个数值
	 * @author ZhengWx
	 * @date 2017年2月9日 下午2:42:46
	 * @param values
	 * @return true: 全部插入成功，false: 至少有一个插入失败
	 * @since 1.0
	 */
	public boolean insert(List<ContentValues> values);

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
	public boolean update(ContentValues values, String whereClause, String[] whereArgs);

	/**
	 * 删除数据
	 * @author ZhengWx
	 * @date 2017年3月20日 上午9:39:13
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 * @since 1.0
	 */
	public boolean delete(String whereClause, String[] whereArgs);
	
	/**
	 * 某条数据是否存在，存在则更新否则插入
	 * @param values
	 * @return
	 */
	public boolean replace(ContentValues values);
	/**
	 * 查询数据
	 * @author ZhengWx
	 * @date 2017年3月20日 上午9:38:34
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 * @since 1.0
	 */
	public List<Map<String, String>> query(String[] columns,
										   String selection, String[] selectionArgs, String groupBy,
										   String having, String orderBy);
	
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
	public List<Map<String, String>> query(String[] columns, String selection, String[] selectionArgs);

	/**
	 * 查询所有数据
	 * @author ZhengWx
	 * @date 2015-8-28 下午5:21:43
	 * @return
	 * @since 1.0
	 */
	public List<Map<String, String>> query();

	/**
	 * 获取所有的列名
	 * @author ZhengWx
	 * @date 2015-8-27 上午8:57:56
	 * @return
	 * @since 1.0
	 */
	public String[] getAllColumnNames();
	
	/**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
	public boolean isTableExist();
    
    /**
	 * 删除表
	 * @author ZhengWx
	 * @date 2016年9月25日 下午7:39:06
	 * @return
	 * @since 1.0
	 */
    public boolean deleteTable();
	
	/**
	 * 删除默认数据库
	 * @author ZhengWx
	 * @date 2017年3月12日 下午5:52:42
	 * @return
	 * @since 1.0
	 */
	public boolean deleteDb();
	
    /**
     * 关闭数据库
     * @author ZhengWx
     * @date 2017年3月20日 上午9:34:34
     * @since 1.0
     */
	public void close();
}
