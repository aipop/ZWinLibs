# ZWinLibs
===
一个集成数据库操作库、网络请求库、日志打印库等的项目库
ZWinLibDatabase
----
对数据库操作的工具库，用法如下：
DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);
databaseManager.insert(javaBean);
其中javaBean是一个普通bean对象，可以对改bean对象和字段进行注解，从而达到对数据库操作的效果，支持的标签如下：
*表格标签@Table
databaseName：数据库名称
tableName：表名称
vesion：版本号
*字段标签@Property
name：数据表里的字段名
isPrimaryKey：是否作为主键
isAutoincrement：是否自增长
isUnique：是否值唯一
isNotNull：否允许值为空，默认允许为null
isIndex：是否作为索引
isUniqueIndex：是否作为唯一性索引
isTableColumn：是否作为表属性插入到表中
示例Bean对象如下：
@Table(databaseName="database_bean_2_data", tableName="database_bean")
public class DatabaseBean2 {
	@Property(isPrimaryKey=true)
	private int id;
	@Property(isNotNull=true)
	private String str;
	private int count;
	private boolean isCome;
  }
