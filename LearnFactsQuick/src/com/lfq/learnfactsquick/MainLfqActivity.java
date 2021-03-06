package com.lfq.learnfactsquick;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class MainLfqActivity extends Activity {
	private static final String TAG = "MainLfqActivity"; 
	public static final doSyncTo doSyncTo = null;
	private RelativeLayout main_layout;
	Button edit_acrostics, edit_alphabet, edit_dictionary, edit_events,
			edit_mnemonics, edit_numbers, edit_tables, edit_users, edit_words;
	Button show_tables, show_words, show_mnemonics, show_numbers,
			show_timeline;
	Button major_system, celebrity_numbers, anagram_generator,
			mnemonic_generator, dictionary;
	Button help_menu;
	private DatabaseAcrostics dacr;
	private DatabaseMisc dmis;
	private static SQLiteDatabase acr_db, misc_db;

	private MenuItem go_back_item, menu_item_autosync_on,
			menu_item_autosync_off;

	private static MainLfqActivity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private String text;
	private Boolean is_database_load, is_update;
	AlertDialog dialog;
	private static String set_database;
	static doSyncTo do_sync_to;
	static doLoadDatabases do_load_db;
	private Boolean is_do_from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		this_act = this;
		is_database_load = true;
		is_update = false;
		text = "";
		set_database = "";
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main_lfq);
		setViews();
		setListeners();
		if (sharedPref.getBoolean("IS_DATABASES_CREATED", false) == true) {
			dacr = new DatabaseAcrostics(this_act);
			acr_db = dacr.getWritableDatabase();
			dmis = new DatabaseMisc(this_act);
			misc_db = dmis.getWritableDatabase();
			is_database_load = false;
			Cursor c_sync = misc_db
					.rawQuery("SELECT _id FROM sync_table", null);
			Boolean is_do_to = (c_sync.getCount() > 0);
			c_sync.close();
			if (is_do_to) {
				new doSyncTo().execute();
			} else {
				//NEED TO UPDATE SYNC TO TIME HERE:
				
				is_do_from = (Synchronize.getFromCount(false, "db") > 0);
				if (is_do_from) {
					new doSyncFrom().execute();
				}
			}
		} else {
			new doLoadDatabases().execute();
		}
	}

	@SuppressLint("NewApi")
	public void setViews() {
		main_layout = (RelativeLayout) findViewById(R.id.main_lfq_layout);
		edit_acrostics = (Button) findViewById(R.id.edit_acrostics);
		edit_alphabet = (Button) findViewById(R.id.edit_alphabet);
		edit_dictionary = (Button) findViewById(R.id.edit_dictionary);
		edit_events = (Button) findViewById(R.id.edit_events);
		edit_mnemonics = (Button) findViewById(R.id.edit_mnemonics);
		edit_numbers = (Button) findViewById(R.id.edit_numbers);
		edit_words = (Button) findViewById(R.id.edit_words);
		edit_tables = (Button) findViewById(R.id.edit_tables);
		edit_users = (Button) findViewById(R.id.edit_users);

		show_tables = (Button) findViewById(R.id.show_tables);
		show_words = (Button) findViewById(R.id.show_words);
		show_mnemonics = (Button) findViewById(R.id.show_mnemonics);
		show_numbers = (Button) findViewById(R.id.show_numbers);
		show_timeline = (Button) findViewById(R.id.show_timeline);

		major_system = (Button) findViewById(R.id.major_system);
		celebrity_numbers = (Button) findViewById(R.id.celebrity_numbers);
		anagram_generator = (Button) findViewById(R.id.anagram_generator);
		mnemonic_generator = (Button) findViewById(R.id.mnemonic_generator);
		dictionary = (Button) findViewById(R.id.dictionary);

		help_menu = (Button) findViewById(R.id.help_menu);

		int bg_color_int = sharedPref.getInt("BG Color", Color.GREEN);
		main_layout.setBackgroundColor(bg_color_int);
		int myDrawable = sharedPref.getInt("BG Button", R.drawable.button);
		edit_acrostics.setBackgroundResource(myDrawable);
		edit_alphabet.setBackgroundResource(myDrawable);
		edit_dictionary.setBackgroundResource(myDrawable);
		edit_events.setBackgroundResource(myDrawable);
		edit_mnemonics.setBackgroundResource(myDrawable);
		edit_numbers.setBackgroundResource(myDrawable);
		edit_words.setBackgroundResource(myDrawable);
		edit_tables.setBackgroundResource(myDrawable);
		edit_users.setBackgroundResource(myDrawable);

		show_tables.setBackgroundResource(myDrawable);
		show_words.setBackgroundResource(myDrawable);
		show_mnemonics.setBackgroundResource(myDrawable);
		show_numbers.setBackgroundResource(myDrawable);
		show_timeline.setBackgroundResource(myDrawable);

		major_system.setBackgroundResource(myDrawable);
		celebrity_numbers.setBackgroundResource(myDrawable);
		anagram_generator.setBackgroundResource(myDrawable);
		mnemonic_generator.setBackgroundResource(myDrawable);
		dictionary.setBackgroundResource(myDrawable);

		help_menu.setBackgroundResource(myDrawable);

	}

	private void setListeners() {

		edit_acrostics.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditAcrostics.class);
				this_act.startActivity(intent);
			}
		});
		edit_alphabet.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditAlphabet.class);
				this_act.startActivity(intent);
			}
		});
		edit_dictionary.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditDictionary.class);
				this_act.startActivity(intent);

			}
		});
		edit_events.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditEvents.class);
				this_act.startActivity(intent);
			}
		});
		edit_mnemonics.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditMnemonics.class);
				this_act.startActivity(intent);
			}
		});
		edit_numbers.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditNumbers.class);
				this_act.startActivity(intent);
			}
		});

		edit_words.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditNewWords.class);
				this_act.startActivity(intent);
			}
		});
		edit_tables.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, EditTables.class);
				this_act.startActivity(intent);
			}
		});
		/*
		 * edit_users.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { Intent intent = new Intent(this_act,
		 * EditUsers.class); this_act.startActivity(intent); } });
		 */

		show_tables.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, ShowAcrosticsTables.class);
				this_act.startActivity(intent);
			}
		});
		show_words.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, NewWords.class);
				this_act.startActivity(intent);
			}
		});
		show_mnemonics.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, Mnemonics.class);
				this_act.startActivity(intent);
			}
		});
		show_numbers.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, Numbers.class);
				this_act.startActivity(intent);
			}
		});
		show_timeline.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, Timeline.class);
				this_act.startActivity(intent);
			}
		});

		major_system.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, MajorSystemGenerator.class);
				this_act.startActivity(intent);
			}
		});
		celebrity_numbers.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, CelebritiesToNumbers.class);
				this_act.startActivity(intent);
			}
		});
		anagram_generator.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, AnagramGenerator.class);
				this_act.startActivity(intent);
			}
		});
		mnemonic_generator.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, MnemonicGenerator.class);
				this_act.startActivity(intent);

			}
		});
		dictionary.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, Dictionary.class);
				this_act.startActivity(intent);

			}
		});

		help_menu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(this_act, HelpMenu.class);
				this_act.startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_lfq, menu);
		go_back_item = menu.findItem(R.id.action_bar_go_back);
		go_back_item.setVisible(false);
		menu_item_autosync_off = menu.findItem(R.id.autosync_off);
		menu_item_autosync_on = menu.findItem(R.id.autosync_on);
		if (sharedPref.getBoolean("AUTO SYNC", false) == true
				&& Helpers.isNetworkAvailable()) {
			menu_item_autosync_on.setChecked(true);
			menu_item_autosync_off.setChecked(false);
		} else {
			editor.putBoolean("AUTO SYNC", false);
			menu_item_autosync_on.setChecked(false);
			menu_item_autosync_off.setChecked(true);
		}
		editor.commit();
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// closeDatabases();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if (is_database_load == false) {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int myDrawable = -1, myColor = -1, buttonColor = -1, auto = -1;

		int id = item.getItemId();
		switch (id) {
		case R.id.background_blue:
			myColor = Color.BLUE;
			break;
		case R.id.background_black:
			myColor = Color.BLACK;
			break;
		case R.id.background_green:
			myColor = Color.GREEN;
			break;
		case R.id.background_gray:
			myColor = Color.LTGRAY;
			break;
		case R.id.background_cyan:
			myColor = Color.CYAN;
			break;
		case R.id.background_magenta:
			myColor = Color.MAGENTA;
			break;
		case R.id.background_red:
			myColor = Color.RED;
			break;
		case R.id.background_white:
			myColor = Color.WHITE;
			break;
		case R.id.background_yellow:
			myColor = Color.YELLOW;
			break;
		// CASE CHOOSE BUTTON COLOR:

		case R.id.buttons_blue:
			myDrawable = R.drawable.button_blue;
			buttonColor = Color.BLUE;
			break;
		case R.id.buttons_green:
			myDrawable = R.drawable.button_green;
			buttonColor = Color.GREEN;
			break;
		case R.id.buttons_gray:
			myDrawable = R.drawable.button_gray;
			buttonColor = Color.GRAY;
			break;
		case R.id.buttons_cyan:
			myDrawable = R.drawable.button_cyan;
			buttonColor = Color.CYAN;
			break;
		case R.id.buttons_magenta:
			myDrawable = R.drawable.button_magenta;
			buttonColor = Color.MAGENTA;
			break;
		case R.id.buttons_red:
			myDrawable = R.drawable.button_red;
			buttonColor = Color.RED;
			break;
		case R.id.buttons_yellow:
			myDrawable = R.drawable.button_yellow;
			buttonColor = Color.YELLOW;
			break;
		case R.id.autosync_on:
			auto = 1;
			break;
		case R.id.autosync_off:
			auto = 0;
			break;
		}
		if (myColor != -1) {
			editor.putInt("BG Color", myColor);
			editor.commit();
			main_layout.setBackgroundColor(myColor);
		}
		if (myDrawable != -1) {
			editor.putInt("BG Button", myDrawable);
			editor.putInt("BUTTON Color", buttonColor);
			editor.commit();
			edit_acrostics.setBackgroundResource(myDrawable);
			edit_alphabet.setBackgroundResource(myDrawable);
			edit_dictionary.setBackgroundResource(myDrawable);
			edit_events.setBackgroundResource(myDrawable);
			edit_mnemonics.setBackgroundResource(myDrawable);
			edit_numbers.setBackgroundResource(myDrawable);
			edit_words.setBackgroundResource(myDrawable);
			edit_tables.setBackgroundResource(myDrawable);
			edit_users.setBackgroundResource(myDrawable);

			show_tables.setBackgroundResource(myDrawable);
			show_words.setBackgroundResource(myDrawable);
			show_mnemonics.setBackgroundResource(myDrawable);
			show_numbers.setBackgroundResource(myDrawable);
			show_timeline.setBackgroundResource(myDrawable);

			major_system.setBackgroundResource(myDrawable);
			celebrity_numbers.setBackgroundResource(myDrawable);
			anagram_generator.setBackgroundResource(myDrawable);
			mnemonic_generator.setBackgroundResource(myDrawable);
			dictionary.setBackgroundResource(myDrawable);

			help_menu.setBackgroundResource(myDrawable);

		}
		if (auto != -1) {
			if (auto == 1 && Helpers.isNetworkAvailable()) {
				editor.putBoolean("AUTO SYNC", true);
				menu_item_autosync_on.setChecked(true);
				menu_item_autosync_off.setChecked(false);
			} else {
				editor.putBoolean("AUTO SYNC", false);
				menu_item_autosync_on.setChecked(false);
				menu_item_autosync_off.setChecked(true);
			}
			editor.commit();
		}
		return super.onOptionsItemSelected(item);
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			do_load_db = this;
			dialog = new AlertDialog.Builder(this_act).create();
			text = "Loading databases. Please wait...";
			dialog.setTitle("Loading databases. Please wait...");
			dialog.setMessage(text);
			is_database_load = true;
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			publishProgress("Loading acrostics database...");
			dacr = DatabaseAcrostics.getInstance(this_act, this);
			acr_db = dacr.getWritableDatabase();
			publishProgress("LOADED.<br />");

			publishProgress("Loading miscellaneous database...");
			dmis = DatabaseMisc.getInstance(this_act, this);
			misc_db = dmis.getWritableDatabase();
			publishProgress("LOADED.<br />");

			publishProgress("DATABASES ALL LOADED.");
			editor.putBoolean("IS_DATABASES_CREATED", true).commit();

			is_database_load = false;

			return null;
		}

		public void doProgress(String value) {
			publishProgress(value);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			text += values[0];
			dialog.setMessage(Html.fromHtml("<b>" + text + "</b>"));
		}

		@Override
		protected void onPostExecute(String file_url) {
			dialog.dismiss();
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
			.format(Calendar.getInstance().getTime());
			sharedPref.edit().putString("TIME_SYNCED_FROM", timeStamp).commit();
			Log.d(TAG, "SET TIME_SYCNED FROM=" + timeStamp);
		}

	}

	static public doSyncTo getDoSyncTo() {
		return do_sync_to;
	}

	static public doLoadDatabases getDoLoadDatabases() {
		return do_load_db;
	}

	public class doSyncTo extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			do_sync_to = this;
			text = "";
			dialog = new AlertDialog.Builder(this_act).create();
			dialog.setTitle("Synchronizing to LFQ.com. Please wait...");
			dialog.setMessage("");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Synchronize.doSyncTo(this);
			// Synchronize.updateAcrostics(this);
			return null;
		}

		public void doProgress(String value) {
			publishProgress(value);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			text += values[0] + "<br /><br />";
			dialog.setMessage(Html.fromHtml("<b>" + text + "</b>"));
		}

		@Override
		protected void onPostExecute(String file_url) {
			dialog.dismiss();
			is_do_from = (Synchronize.getFromCount(false, "db") > 0);
			if (is_do_from) {
				new doSyncFrom().execute();
			}
		}

	}

	public class doSyncFrom extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			text = "";
			dialog = new AlertDialog.Builder(this_act).create();
			dialog.setTitle("Synchronizing from LFQ.com. Please wait...");
			dialog.setMessage("");
			if (Synchronize.getConflictCount() > 0 || true) {
				dialog.setButton(DialogInterface.BUTTON_POSITIVE,
						"SYNC CONFLICTS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								new doSyncConflicts().execute();
							}
						});
			}
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Synchronize.doSyncFrom(this);
			System.out.println("doing sync from");
			return null;
		}

		public void doProgress(String value) {
			publishProgress(value);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (values[0].split("@").length > 1) {
				if (is_update == false) {
					is_update = true;
				}
				View checkBoxView = View.inflate(LfqApp.getInstance(),
						R.layout.checkbox, null);
				CheckBox checkBox = (CheckBox) checkBoxView
						.findViewById(R.id.checkbox);
				checkBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
					}
				});
				checkBox.setText(values[0].split("@")[0]);
				dialog.setView(checkBoxView);
			} else {
				text += values[0] + "<br />";
				dialog.setMessage(Html.fromHtml("<b>" + text + "</b>"));
			}
		}

		@Override
		protected void onPostExecute(String file_url) {
		}
	}

	class doSyncConflicts extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			text = "";
			dialog = new AlertDialog.Builder(this_act).create();
			dialog.setTitle("Synchronizing conflicts. Please wait...");
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Synchronize.doSyncConflicts(this);
			return null;
		}

		public void doProgress(String value) {
			publishProgress(value);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (values[0].split("@").length > 1) {
				if (is_update == false) {
					is_update = true;
				}
				View checkBoxView = View.inflate(LfqApp.getInstance(),
						R.layout.checkbox, null);
				CheckBox checkBox = (CheckBox) checkBoxView
						.findViewById(R.id.checkbox);
				checkBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
					}
				});
				checkBox.setText(values[0].split("@")[0]);
				dialog.setView(checkBoxView);
			} else {
				text += values[0] + "<br />";
				dialog.setMessage(Html.fromHtml("<b>" + text + "</b>"));

			}
		}

		@Override
		protected void onPostExecute(String file_url) {

		}

	}

	// private static SQLiteDatabase sync_db, acr_db, alp_db, dictionary_db,
	// events_db, misc_db, mne_db, newwords_db, numbers_db, users_db;
	public static void closeDatabases() {
		acr_db.close();
		misc_db.close();
	}

	public static SQLiteDatabase getDatabase() {
		if (set_database.equals("acr_db")) {
			return acr_db;
		}
		if (set_database.equals("misc_db")) {
			return misc_db;
		}
		return null;
	}

	public static void setDatabase(String mydb) {
		set_database = mydb;
	}

	public static SQLiteDatabase getAcrosticsDb() {
		return acr_db;
	}

	public static SQLiteDatabase getMiscDb() {
		return misc_db;
	}
}