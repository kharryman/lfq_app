package com.lfq.learnfactsquick;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseMisc extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "lfq_misc.db";
	private static final int DATABASE_VERSION = 1;
	Context myContext;
	private String url;
	private com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases loader_main;
	private com.lfq.learnfactsquick.AnagramGenerator.doLoadDatabases loader_anagram;
	private com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases loader_acrostics;
	private com.lfq.learnfactsquick.EditAlphabet.doLoadDatabases loader_alphabet;
	private com.lfq.learnfactsquick.MnemonicGenerator.doLoadDatabases loader_mne_generator;
	private com.lfq.learnfactsquick.ShowAcrosticsTables.doLoadDatabases loader_show_acrostics;
	private com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases loader_sync;

	private enum activity {
		ANAGRAM, EDIT_ACROSTICS, EDIT_ALPHABET, MAIN, MNE_GENERATOR, SHOW_ACROSTICS, SYNC
	};

	private activity act;

	private static DatabaseMisc sInstance;

	public static synchronized DatabaseMisc getInstance(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMisc(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public static synchronized DatabaseMisc getInstance(Context context,
			com.lfq.learnfactsquick.AnagramGenerator.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMisc(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public static synchronized DatabaseMisc getInstance(Context context,
			com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMisc(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public static synchronized DatabaseMisc getInstance(Context context,
			com.lfq.learnfactsquick.EditAlphabet.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMisc(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public static synchronized DatabaseMisc getInstance(Context context,
			com.lfq.learnfactsquick.MnemonicGenerator.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMisc(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public static synchronized DatabaseMisc getInstance(Context context,
			com.lfq.learnfactsquick.ShowAcrosticsTables.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMisc(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public static synchronized DatabaseMisc getInstance(Context context,
			com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMisc(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	
	public DatabaseMisc(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext = context;
		act = activity.MAIN;
	}	

	public DatabaseMisc(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_main = myLoader;
		myContext = context;
		act = activity.MAIN;
	}

	public DatabaseMisc(Context context,
			com.lfq.learnfactsquick.AnagramGenerator.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_anagram = myLoader;
		myContext = context;
		act = activity.ANAGRAM;
	}

	public DatabaseMisc(Context context,
			com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_acrostics = myLoader;
		myContext = context;
		act = activity.EDIT_ACROSTICS;
	}

	public DatabaseMisc(Context context,
			com.lfq.learnfactsquick.EditAlphabet.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_alphabet = myLoader;
		myContext = context;
		act = activity.EDIT_ALPHABET;
	}

	public DatabaseMisc(Context context,
			com.lfq.learnfactsquick.MnemonicGenerator.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_mne_generator = myLoader;
		myContext = context;
		act = activity.MNE_GENERATOR;
	}

	public DatabaseMisc(Context context,
			com.lfq.learnfactsquick.ShowAcrosticsTables.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_show_acrostics = myLoader;
		myContext = context;
		act = activity.SHOW_ACROSTICS;
	}

	public DatabaseMisc(Context context,
			com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_sync = myLoader;
		myContext = context;
		act = activity.SYNC;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE `app_color_options` (`_id` integer PRIMARY KEY AUTOINCREMENT, `BackgroundColor` tinyint, `ButtonColor` tinyint);");
		database.execSQL("INSERT INTO `app_color_options` (`BackgroundColor`, `ButtonColor`) VALUES(0,0);");
		database.execSQL("CREATE TABLE `sync_table` (`_id` integer PRIMARY KEY AUTOINCREMENT, `Username` tinytext, `Password` tinytext, `SQL` text, `DB` tinytext, `Action` tinytext, `Table_name` tinytext, `Name` tinytext, `id` integer, `Device_Id` tinytext, `Is_Image` tinytext, `Image` blob);");
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_lfq_database.php";
		String table = "", text = "";
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("database", "misc"));
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
				System.out.println(json.getString("DEBUG") + ", count="
						+ ct_sqls);
				table = json.getString("Table" + ct_tables);
				for (int j = 0; j < ct_sqls; j++) {
					String sql_get = json.getString("sql" + j);
					if (!sql_get.contains(table)) {
						table = json.getString("Table" + ct_tables);
						System.out.println("table=" + table + ct_tables);
						ct_tables++;
						text = "Loading table " + table + "...";
						switch (act) {
						case ANAGRAM:
							loader_anagram.doProgress(text);
							break;
						case EDIT_ACROSTICS:
							loader_acrostics.doProgress(text);
							break;
						case EDIT_ALPHABET:
							loader_alphabet.doProgress(text);
							break;
						case MAIN:
							loader_main.doProgress(text);
							break;
						case MNE_GENERATOR:
							loader_mne_generator.doProgress(text);
							break;
						case SHOW_ACROSTICS:
							loader_show_acrostics.doProgress(text);
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
		// text += OldSynchronize.setDatabaseDate("DATE_MSC_SYNCED");
		String timeStamp = new SimpleDateFormat("yyyy/MM/DD HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		SharedPreferences sharedPref = myContext.getSharedPreferences(
				myContext.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		sharedPref.edit().putString("TIME_SYNCED_FROM", timeStamp).commit();
		switch (act) {
		case ANAGRAM:
			loader_anagram.doProgress(text);
			break;
		case EDIT_ACROSTICS:
			loader_acrostics.doProgress(text);
			break;
		case EDIT_ALPHABET:
			loader_alphabet.doProgress(text);
			break;
		case MAIN:
			loader_main.doProgress(text);
			break;
		case MNE_GENERATOR:
			loader_mne_generator.doProgress(text);
			break;
		case SHOW_ACROSTICS:
			loader_show_acrostics.doProgress(text);
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