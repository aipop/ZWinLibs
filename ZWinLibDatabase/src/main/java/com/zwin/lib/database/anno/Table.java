package com.zwin.lib.database.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表格属性
 * @ClassName TableProperty
 * @author ZhengWx
 * @date 2016年4月4日 下午11:27:02
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 数据库名称
	 * @author ZhengWx
	 * @date 2016年4月4日 下午11:27:12
	 * @return
	 * @since 1.0
	 */
	String databaseName() default "";
	/**
	 * 数据库版本
	 * @author ZhengWx
	 * @date 2016年8月9日 下午7:55:30
	 * @return
	 * @since 1.0
	 */
	int version() default 1;
	/**
	 * 表格名称
	 * @author ZhengWx
	 * @date 2016年4月4日 下午11:27:22
	 * @return
	 * @since 1.0
	 */
	String tableName() default "";
}
