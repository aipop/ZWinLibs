package com.zwin.lib.log;

import android.os.Environment;
import android.text.TextUtils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Log工具，使用Log4j库，可以保存log日志<p>
 * 使用前先调<b>LogUtilBy4j.init()</b>进行初始化。<p>
 * 可以通过调<b>LogUtilBy4j.config()</b>方法对日志文件名和路径进行配置
 * @ClassName LogUtil
 * @author ZhengWx
 * @date 2015年5月8日 下午1:50:24
 */
public class LogUtilBy4j {

	public static final String TAG = "LogUtilBy4j";
    private static LogConfigurator logConfigurator;
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
    private static boolean allowLog = true;	// 允许log输出

    private LogUtilBy4j() {
    }

    /**
     * 初始化
     * @author ZhengWx
     * @date 2016-11-19 下午5:13:26
     * @param logFile log文件绝对路径，如：<p>/storage/emulated/0/log_2016-11-19.txt
     * @since 1.0
     */
    public static void init(String logFile) {
    	if (TextUtils.isEmpty(logFile)) {
    		logFile = Environment.getExternalStorageDirectory().getPath()
    				+"/"
    				+"log_"+formatter.format(new Date(System.currentTimeMillis()))+".txt";
    	}
    	if (logConfigurator == null) {
            try {
            	logConfigurator = new LogConfigurator();
                logConfigurator.setFileName(logFile);
                logConfigurator.setRootLevel(Level.DEBUG);
                logConfigurator.setFilePattern("%d{MM/dd/HH:mm:ss} %-5p [%c{2}] %n%m%n--------------------------------------%n");
                logConfigurator.setMaxFileSize(1024*1024*2);
                logConfigurator.setMaxBackupSize(5);
                logConfigurator.setImmediateFlush(true);
                logConfigurator.configure();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        	logConfigurator.setFileName(logFile);
			logConfigurator.configure();
        }
    }
    
    public static void init() {
    	init(null);
    }
    
    /**
     * 配置保存的路径和文件
     * @author ZhengWx
     * @date 2016年6月5日 下午4:00:29
     * @param logPath 保存的路径
     * @param logFile 保存的文件
     * @since 1.0
     */
    public static void config(String logPath, String logFile) {
    	if (TextUtils.isEmpty(logPath)) {
    		logPath = Environment.getExternalStorageDirectory().getPath()+"/";
    	} else if (!logPath.endsWith("/")) {
			logPath = logPath+"/";
		}
    	
    	if (TextUtils.isEmpty(logFile)) {
    		logFile = "log_"+formatter.format(new Date(System.currentTimeMillis()))+".txt";
    	} else if (logFile.startsWith("/")) {
			logFile = logFile.substring(1);
		}
    	
    	init(logPath+logFile);
    }
    
    public static void config(boolean allowLog) {
    	LogUtilBy4j.allowLog = allowLog;
    }

    public static void d(String tag, String content) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).debug(content);
    }

    public static void d(String tag, String content, Throwable e) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).debug(content, e);
    }

    public static void e(String tag, String content) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).error(content);
    }

    public static void e(String tag, Throwable e) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).error(e.getMessage(), e);
    }

    public static void e(String tag, String content, Throwable e) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).error(content, e);
    }

    public static void i(String tag, String content) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).info(content);
    }

    public static void i(String tag, String content, Throwable e) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).info(content, e);
    }

    public static void w(String tag, String content) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).warn(content);
    }

    public static void w(String tag, String content, Throwable e) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).warn(content, e);
    }

    public static void w(String tag, Throwable e) {
        if (!allowLog) {
            return;
        }
        Logger.getLogger(tag).warn(e.getMessage(), e);
    }
}