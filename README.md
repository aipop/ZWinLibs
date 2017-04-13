# ZWinLibs
一个集成数据库操作库、网络请求库、日志打印库等的项目库<br>
ZWinLibDatabase
----
对数据库操作的工具库，用法如下：<br>
DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);<br>
databaseManager.insert(javaBean);<br>
其中javaBean是一个普通bean对象，可以对改bean对象和字段进行注解，从而达到对数据库操作的效果，支持的标签如下：<br>
*表格标签@Table<br>
databaseName：数据库名称<br>
tableName：表名称<br>
vesion：版本号<br>
*字段标签@Property<br>
name：数据表里的字段名<br>
isPrimaryKey：是否作为主键<br>
isAutoincrement：是否自增长<br>
isUnique：是否值唯一<br>
isNotNull：否允许值为空，默认允许为null<br>
isIndex：是否作为索引<br>
isUniqueIndex：是否作为唯一性索引<br>
isTableColumn：是否作为表属性插入到表中<br>
示例Bean对象如下：<br>
@Table(databaseName="database_bean_2_data", tableName="database_bean")<br>
public class DatabaseBean2 {<br>
	@Property(isPrimaryKey=true)<br>
	private int id;<br>
	@Property(isNotNull=true)<br>
	private String str;<br>
	private int count;<br>
	private boolean isCome;<br>
  }
