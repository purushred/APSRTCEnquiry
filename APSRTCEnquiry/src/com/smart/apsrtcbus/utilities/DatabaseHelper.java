package com.smart.apsrtcbus.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	private static final String DB_NAME="APSRTC_DB";
	private static final int DB_VERSION=1;
	private static final String TABLE_NAME="SEARCH_TABLE";
	
	// Table Column Names
	private static final String SERVICE_CLASS_ID="SERVICE_CLASS_ID";
	private static final String DATE_STR="DATE_STR";
	private static final String FROM_SERVICE_ID="FROM_SERVICE_ID";
	private static final String TO_SERVICE_ID="TO_SERVICE_ID";
	
	// Table Creation Statement
			
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
