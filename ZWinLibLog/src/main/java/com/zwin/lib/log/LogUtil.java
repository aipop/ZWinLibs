package com.zwin.lib.log;

/**
 * 打印log工具<p>
 * 通过调用<b>LogUtil.d()，LogUtil.i()，LogUtil.w()，LogUtil.e()</b>来打印。<p>
 * 通过调用<b>LogUtil.config()</b>方法进行配置
 * @ClassName LogUtil
 * @author ZhengWx
 * @date 2016年11月16日 下午4:45:47
 */
public class LogUtil {
	private static boolean saveLog = false;	// 是否保存log，false：不保存，true：保存
	private static boolean allowLog = true;	// 是否打印log，false：不打印，true：打印
	
	/**
	 * 配置log保存
	 * @author ZhengWx
	 * @date 2016年6月5日 下午1:56:06
	 * @param saveLog 是否保存log
	 * @param logPath log保存目录。可以为null，为null则默认是SD卡根目录
	 * @param logFileName log文件名。可以为null，为null则以“log_yyyy-MM-dd.txt”来命名
	 * @since 1.0
	 */
	public static void config(boolean saveLog, String logPath, String logFileName) {
		LogUtil.saveLog = saveLog;
		if (saveLog) {
			LogUtilBy4j.config(allowLog);
			LogUtilBy4j.config(logPath, logFileName);
		}
	}
	
	/**
	 * 配置是否允许打印log
	 * @author ZhengWx
	 * @date 2016年6月5日 下午1:56:50
	 * @param allowLog 是否允许打印log
	 * @since 1.0
	 */
	public static void config(boolean allowLog) {
		if (saveLog) {
			LogUtilBy4j.config(allowLog);
		} else {
			LogUtilBySystem.config(allowLog);
		}
	}
	
	public static void d(String content) {
		String tag = generateTag();
		if (saveLog) {
			LogUtilBy4j.d(tag, content);
		} else {
			LogUtilBySystem.d(tag, content);
		}
    }
	
	public static void d(String content, Throwable e) {
		String tag = generateTag();
		if (saveLog) {
			LogUtilBy4j.d(tag, content, e);
		} else {
			LogUtilBySystem.d(tag, content, e);
		}
	}

    public static void e(String content) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.e(tag, content);
		} else {
			LogUtilBySystem.e(tag, content);
		}
    }

    public static void e(Throwable e) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.e(tag, e);
		} else {
			LogUtilBySystem.e(tag, e);
		}
    }

    public static void e(String content, Throwable e) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.e(tag, content, e);
		} else {
			LogUtilBySystem.e(tag, content, e);
		}
    }

    public static void i(String content) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.i(tag, content);
		} else {
			LogUtilBySystem.i(tag, content);
		}
    }

    public static void i(String content, Throwable e) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.i(tag, content, e);
		} else {
			LogUtilBySystem.i(tag, content, e);
		}
    }

    public static void w(String content) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.w(tag, content);
		} else {
			LogUtilBySystem.w(tag, content);
		}
    }

    public static void w(String content, Throwable e) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.w(tag, content, e);
		} else {
			LogUtilBySystem.w(tag, content, e);
		}
    }

    public static void w(Throwable e) {
		String tag = generateTag();
    	if (saveLog) {
			LogUtilBy4j.w(tag, e);
		} else {
			LogUtilBySystem.w(tag, e);
		}
    }

	private static String generateTag() {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
		String tag = "%s.%s(L:%d)"; // 占位符
		String clazzName = caller.getClassName(); // 获取到类名
		String callerClazzName = clazzName.substring(clazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());

		return tag;
	}

}