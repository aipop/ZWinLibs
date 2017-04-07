package com.zwin.lib.database.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段属性
 * @ClassName Property
 * @author ZhengWx
 * @date 2016年4月4日 下午11:25:51
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	/**
	 * 数据表里的字段名
	 * @author ZhengWx
	 * @date 2016年4月4日 下午11:25:38
	 * @return
	 * @since 1.0
	 */
	String name() default "";
	/**
	 * 是否作为主键
	 * @author ZhengWx
	 * @date 2016年4月4日 下午11:26:00
	 * @return
	 * @since 1.0
	 */
	boolean isPrimaryKey() default false;
	/**
	 * 是否自增长
	 * <br><b><li>注意：如果为true，则isPrimaryKey也必须是true，且只能有一个主键
	 * @author ZhengWx
	 * @date 2016年6月7日 上午11:36:15
	 * @return
	 * @since 1.0
	 */
	boolean isAutoincrement() default false;
	/**
	 * 是否值唯一
	 * @author ZhengWx
	 * @date 2016年4月4日 下午11:26:33
	 * @return
	 * @since 1.0
	 */
	boolean isUnique() default false;
	/**
	 * 是否允许值为空，默认允许为null
	 * @author ZhengWx
	 * @date 2016年5月1日 下午12:05:22
	 * @return
	 * @since 1.0
	 */
	boolean isNotNull() default false;
	/**
	 * 是否作为索引
	 * @author ZhengWx
	 * @date 2016年6月7日 下午5:16:23
	 * @return
	 * @since 1.0
	 */
	boolean isIndex() default false;
	/**
	 * 是否作为唯一性索引
	 * @author ZhengWx
	 * @date 2016年6月7日 下午5:16:31
	 * @return
	 * @since 1.0
	 */
	boolean isUniqueIndex() default false;
	/**
	 * 是否作为表属性插入到表中
	 * <p>true: 生成表属性，并插入表中，false: 不生成表属性也不会插入表中
	 * @author ZhengWx
	 * @date 2016年8月22日 下午4:36:18
	 * @return
	 * @since 1.0
	 */
	boolean isTableColumn() default true;
}
