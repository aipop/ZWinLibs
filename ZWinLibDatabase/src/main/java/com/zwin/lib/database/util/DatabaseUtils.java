package com.zwin.lib.database.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseUtils {
	private final static String TAG = "DatabaseUtil";
    
    /**
     * 拷贝assets目录下的db文件到/data/data/./databases目录下
     * <br><b><li>注意：如果/data/data/./databases目录下已经存在相同名称的db文件，则会被覆盖</b>
     * @param context 上下文对象
     * @param assetsDbFile assets里db文件目录，如：databaseDir/city.db
     * @return
     * @since 1.0
     */
	public static boolean copyAssetsDbToSystemDir(Context context, String assetsDbFile) {
		InputStream istream = null;
		OutputStream ostream = null;
		try {
			String temp[] = assetsDbFile.trim().split("/");
			String dbFileName = temp[temp.length - 1];
			String dbPath = String.format("%s/databases/", context.getFilesDir().getParentFile().getAbsolutePath());
			File dbPathFile = new File(dbPath);
			if (!dbPathFile.exists()) {
				dbPathFile.mkdir();
			}
			String dbFile = dbPath+dbFileName;
			AssetManager am = context.getAssets();
			istream = am.open(assetsDbFile);
			ostream = new FileOutputStream(dbFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = istream.read(buffer)) > 0) {
				ostream.write(buffer, 0, length);
			}
			istream.close();
			ostream.close();
			
			return true;
		} catch (Exception e) {
			Log.e(TAG, String.format("拷贝assets目录下文件：[%s]失败：%s", assetsDbFile, e.toString()));
		} finally {
			try {
				if (istream != null) {					
					istream.close();
				}
				if (ostream != null) {					
					ostream.close();
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
    
}
