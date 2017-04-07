package com.zwin.lib.database;

import android.database.sqlite.SQLiteDatabase;

public interface DbUpdateListener {
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
