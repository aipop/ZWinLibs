package com.zwin.lib.log;

import android.util.Log;

/**
 * Log工具，类似android.util.Log
 * @ClassName LogUtil
 * @author ZhengWx
 * @date 2015年5月8日 下午1:50:24
 */
public class LogUtilBySystem {
	private static boolean allowLog = true;

    private LogUtilBySystem() {
    }
    
    public static void config(boolean allowLog) {
    	LogUtilBySystem.allowLog = allowLog;
    }
    
    public static void d(String tag, String content) {
        if (!allowLog) {
        	return;
        }
        
        Log.d(tag, content);
    }

    public static void d(String tag, String content, Throwable e) {
    	if (!allowLog) {
        	return;
        }
    	
    	Log.d(tag, content, e);
    }

    public static void e(String tag, String content) {
        if (!allowLog) {
        	return;
        }

        Log.e(tag, content);
    }

    public static void e(String tag, Throwable e) {
    	if (!allowLog) {
    		return;
    	}
    	String msg = (e == null ? "null" : e.getMessage());
    	Log.e(tag, msg, e);
    }
    
    public static void e(String tag, String content, Throwable e) {
    	if (!allowLog) {
        	return;
        }

    	Log.e(tag, content, e);
    }

    public static void i(String tag, String content) {
        if (!allowLog) {
        	return;
        }

        Log.i(tag, content);
    }

    public static void i(String tag, String content, Throwable e) {
        if (!allowLog) {
        	return;
        }
        
        Log.i(tag, content, e);
    }

    public static void v(String tag, String content) {
        if (!allowLog) {
        	return;
        }

        Log.v(tag, content);
    }

    public static void v(String tag, String content, Throwable e) {
        if (!allowLog) {
        	return;
        }

        Log.v(tag, content, e);
    }

    public static void w(String tag, String content) {
        if (!allowLog) {
        	return;
        }
        
        Log.w(tag, content);
    }

    public static void w(String tag, String content, Throwable e) {
        if (!allowLog) {
        	return;
        }

        Log.w(tag, content, e);
    }

    public static void w(String tag, Throwable e) {
        if (!allowLog) {
        	return;
        }

        Log.w(tag, e);
    }
}