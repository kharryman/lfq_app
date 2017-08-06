package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseUsers extends SQLiteOpenHelper {	
	private static final String DATABASE_NAME = "lfq_users.db";
	private static final int DATABASE_VERSION = 1;
	private String url;
	
	Context myContext;
	
	private enum activity {
		EDIT_USERS, EDIT_NEWWORDS, EDIT_NUMBERS, MAIN, NEWWORDS, NUMBERS, SYNC, NOTHING
	};

	private activity act;
	private com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases loader_main;
	private com.lfq.learnfactsquick.EditUsers.doLoadDatabases loader_users;	
	private com.lfq.learnfactsquick.EditNewWords.doLoadDatabases loader_edit_newwords;
	private com.lfq.learnfactsquick.EditNumbers.doLoadDatabases loader_edit_numbers;
	private com.lfq.learnfactsquick.NewWords.doLoadDatabases loader_newwords;
	private com.lfq.learnfactsquick.Numbers.doLoadDatabases loader_numbers;
	private com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases loader_sync;	

	
	private static DatabaseUsers sInstance;
	public static synchronized DatabaseUsers getInstance(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseUsers getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers (context.getApplicationContext());
		}
		return sInstance;
	}
	public static synchronized DatabaseUsers getInstance(Context context,
			com.lfq.learnfactsquick.EditUsers.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers (context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseUsers getInstance(Context context,
			com.lfq.learnfactsquick.EditNewWords.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers (context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseUsers getInstance(Context context,
			com.lfq.learnfactsquick.EditNumbers.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers (context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseUsers getInstance(Context context,
			com.lfq.learnfactsquick.NewWords.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers (context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseUsers getInstance(Context context,
			com.lfq.learnfactsquick.Numbers.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers (context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseUsers getInstance(Context context,
			com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseUsers (context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	
	public DatabaseUsers(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_main = myLoader;
		myContext = context;		
		act = activity.MAIN;
	}
	public DatabaseUsers(Context context) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
		myContext = context;
		act = activity.NOTHING;
	}
	public DatabaseUsers(Context context, com.lfq.learnfactsquick.EditUsers.doLoadDatabases myLoader) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_users = myLoader;
		myContext = context;
		act = activity.EDIT_USERS;
	}
	public DatabaseUsers(Context context, com.lfq.learnfactsquick.EditNewWords.doLoadDatabases myLoader) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_edit_newwords = myLoader;
		myContext = context;
		act = activity.EDIT_NEWWORDS;
	}
	public DatabaseUsers(Context context, com.lfq.learnfactsquick.EditNumbers.doLoadDatabases myLoader) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_edit_numbers = myLoader;
		myContext = context;
		act = activity.EDIT_NUMBERS;
	}
	public DatabaseUsers(Context context, com.lfq.learnfactsquick.NewWords.doLoadDatabases myLoader) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_newwords = myLoader;
		myContext = context;
		act = activity.NEWWORDS;
	}
	public DatabaseUsers(Context context, com.lfq.learnfactsquick.Numbers.doLoadDatabases myLoader) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_numbers = myLoader;
		myContext = context;
		act = activity.NUMBERS;
	}
	public DatabaseUsers(Context context, com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_sync = myLoader;
		myContext = context;
		act = activity.SYNC;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {				
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_lfq_database.php";
		String table = "", text = "";
		List<NameValuePair> args = new ArrayList<NameValuePair>();		
		args.add(new BasicNameValuePair("database", "userdata"));		
		args.add(new BasicNameValuePair("is_new_tables", "yes"));
		args.add(new BasicNameValuePair("start_index", null));
		int ct_sqls = 0;
		int ct_tables = 0;
		JSONObject json = null;
		boolean is_continue = false;
		do {
			ct_tables = 0;
			try {				
				json = OldSynchronize.makeHttpRequest(url, "POST", args);
				if (json == null) {
					return;
				}				
				ct_sqls = json.getInt("Count");
				System.out.println(json.getString("DEBUG") + ", count=" + ct_sqls);
				table = json.getString("Table" + ct_tables);
				for (int j = 0; j < ct_sqls; j++) {
					String sql_get = json.getString("sql" + j);
					if (!sql_get.contains(table)) {						
						table = json.getString("Table" + ct_tables);
						System.out.println("table="+table + ct_tables);
						ct_tables++;
						text = "Loading table " + table + "...";						
						switch (act) {					
						case EDIT_USERS:
							loader_users.doProgress(text);
							break;
						case EDIT_NEWWORDS:
							loader_edit_newwords.doProgress(text);
							break;
						case EDIT_NUMBERS:
							loader_edit_numbers.doProgress(text);
							break;
						case MAIN:
							loader_main.doProgress(text);
							break;
						case NEWWORDS:
							loader_newwords.doProgress(text);
							break;
						case NUMBERS:
							loader_numbers.doProgress(text);
							break;
						case SYNC:
							loader_sync.doProgress(text);
							break;
						default:
							break;						
						}
					}					
					if (sql_get != null && !sql_get.equals("")
							&& !sql_get.equals("null")) {
						database.execSQL(sql_get);
					}
				}
				is_continue = json.getBoolean("CONTINUE");
				if (is_continue) {
					args.set(
							2,
							new BasicNameValuePair("start_index", json
									.getString("Start_Index")));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} while (is_continue);
		ct_tables++;
		text = "ALL COMPLETE!!!    LOADED " + ct_tables + " TABLES!";
        text += OldSynchronize.setDatabaseDate("DATE_USR_SYNCED");
		switch (act) {					
		case EDIT_USERS:
			loader_users.doProgress(text);
			break;
		case EDIT_NEWWORDS:
			loader_edit_newwords.doProgress(text);
			break;
		case MAIN:
			loader_main.doProgress(text);
			break;
		case NEWWORDS:
			loader_newwords.doProgress(text);
			break;
		case EDIT_NUMBERS:
			loader_edit_numbers.doProgress(text);
			break;		
		case NUMBERS:
			loader_numbers.doProgress(text);
			break;
		case SYNC:
			loader_sync.doProgress(text);
			break;
		default:
			break;						
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
