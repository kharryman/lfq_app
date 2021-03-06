package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.lfq.learnfactsquick.Constants.cols.acrostics;
import com.lfq.learnfactsquick.Constants.cols.user_review_times;
import com.lfq.learnfactsquick.Constants.cols.user_new_words;
import com.lfq.learnfactsquick.Constants.tables;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditNewWords extends Activity {
	private LinearLayout edit_newwords_layout, reviews_layout;
	private TextView newword_results, login_status;
	private Spinner select_year_newwords, select_month_newwords,
			select_day_newwords, select_days_before, select_number_newwords;
	private EditText input_newwords, username_input, password_input;
	private CheckBox check_change_review_times, check_all_same_table;
	private RadioButton edit_newwords, insert_newwords, delete_newwords;
	private Button get_words, get_next_newwords, get_last_newwords,
			latest_newwords, do_edit_newwords, do_login, do_logout,
			do_one_table;
	private Cursor c = null;
	private String text, results;
	private ArrayAdapter<String> yearsAdapter, reviewsAdapter;

	ArrayAdapter<String> tablesAdapter;
	private Calendar tod;
	private String username, password;
	private Boolean logged_in;
	private ContentValues cv;
	private List<TextView> show_completes;
	private List<EditText> edit_reviews;
	private List<Spinner> select_tables, select_words;
	private List<ArrayAdapter<String>> words_adapters;
	private android.widget.LinearLayout.LayoutParams params;
	private String[] review_array = null;
	private List<String> review_words;
	private String sql, sql2;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private String autosync_text;

	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private static Boolean is_database_load;
	private static Boolean is_one_table_clicked;
	private SelectNumberListener select_number_listener;
	private boolean is_setting_tables;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_one_table_clicked = false;
		is_database_load = true;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		text = "";
		logged_in = false;
		username = "";
		password = "";
		autosync_text = "";
		cv = new ContentValues();

		show_completes = new ArrayList<TextView>();
		edit_reviews = new ArrayList<EditText>();
		select_tables = new ArrayList<Spinner>();
		select_words = new ArrayList<Spinner>();
		words_adapters = new ArrayList<ArrayAdapter<String>>();
		review_words = new ArrayList<String>();
		select_number_listener = new SelectNumberListener();
		new doLoadDatabases().execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (is_database_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (is_database_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		doResume();
		// if (is_database_load == false) {
		// new doLoadDatabases().execute();
		// }
	}

	// 125 .log
	@Override
	public void onBackPressed() {
		if (is_database_load == false) {
			saveChanges();
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_lfq, menu);
		MenuItem bg_color = menu.findItem(R.id.background_color_settings);
		MenuItem button_color = menu.findItem(R.id.button_color_settings);
		MenuItem go_back_item = menu.findItem(R.id.action_bar_go_back);
		go_back_item.setVisible(true);
		bg_color.setVisible(false);
		button_color.setVisible(false);
		RelativeLayout back_text_layout = (RelativeLayout) go_back_item
				.getActionView();
		int buttonDrawable = sharedPref.getInt("BG Button", R.drawable.button);
		back_text_layout.setBackgroundResource(buttonDrawable);
		back_text_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
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
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.autosync_on:
			if (Helpers.isNetworkAvailable()) {
				editor.putBoolean("AUTO SYNC", true);
				menu_item_autosync_on.setChecked(true);
				menu_item_autosync_off.setChecked(false);
				editor.commit();
			}
			break;
		case R.id.autosync_off:
			editor.putBoolean("AUTO SYNC", false);
			menu_item_autosync_on.setChecked(false);
			menu_item_autosync_off.setChecked(true);
			editor.commit();
			break;
		}
		return true;
	}

	public void saveChanges() {
		editor.putBoolean("EDIT NEWWORDS CHECK CHANGE REVIEW TIMES",
				check_change_review_times.isChecked());
		editor.putString("EDIT NEWWORDS NUMBER NEWWORDS",
				select_number_newwords.getSelectedItem().toString());
		editor.putString("EDIT NEWWORDS NEWWORDS INPUT", input_newwords
				.getText().toString());

		editor.putBoolean("EDIT NEWWORDS EDIT NEWWORDS",
				edit_newwords.isChecked());
		editor.putBoolean("EDIT NEWWORDS DELETE NEWWORDS",
				delete_newwords.isChecked());
		editor.putBoolean("EDIT NEWWORDS INSERT", insert_newwords.isChecked());
		editor.putBoolean("EDIT NEWWORDS ALL SAME",
				check_all_same_table.isChecked());
		if (select_tables.size() > 0) {
			System.out.println("selected table index = "
					+ select_tables.get(0).getSelectedItemPosition());
			editor.putInt("EDIT NEWWORDS SELECTED TABLE INDEX", select_tables
					.get(0).getSelectedItemPosition());
		}
		editor.putBoolean("EDIT NEWWORDS ONE TABLE", is_one_table_clicked);

		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		setTitle("EDIT NEW WORDS");
		// LAYOUTS:
		edit_newwords_layout = (LinearLayout) findViewById(R.id.edit_newwords_edit_newwords_layout);
		reviews_layout = (LinearLayout) findViewById(R.id.edit_newwords_review_layout);

		// BUTTONS:
		get_words = (Button) findViewById(R.id.edit_newwords_get_words);
		get_next_newwords = (Button) findViewById(R.id.get_next_newwords);
		get_last_newwords = (Button) findViewById(R.id.get_last_newwords);
		latest_newwords = (Button) findViewById(R.id.latest_newwords);
		do_edit_newwords = (Button) findViewById(R.id.do_edit_newwords);
		do_login = (Button) findViewById(R.id.edit_newwords_login);
		do_logout = (Button) findViewById(R.id.edit_newwords_logout);
		do_one_table = (Button) findViewById(R.id.edit_newwords_do_one_table);

		// CHECKBOXES:
		check_change_review_times = (CheckBox) findViewById(R.id.check_change_review_times);
		check_all_same_table = (CheckBox) findViewById(R.id.check_all_same_table);

		// EDITTEXTS:
		username_input = (EditText) findViewById(R.id.edit_newwords_username);
		password_input = (EditText) findViewById(R.id.edit_newwords_password);
		input_newwords = (EditText) findViewById(R.id.input_newwords);

		// RADIOBUTTONS:
		edit_newwords = (RadioButton) findViewById(R.id.edit_newwords);
		delete_newwords = (RadioButton) findViewById(R.id.delete_newwords);
		insert_newwords = (RadioButton) findViewById(R.id.insert_newwords);

		// RADIOBUTTONS:

		// SCROLLVIEWS:

		// SPINNERS:
		select_number_newwords = (Spinner) findViewById(R.id.select_number_newwords);
		select_year_newwords = (Spinner) findViewById(R.id.select_year_newwords);
		tod = Calendar.getInstance();
		int year = tod.get(Calendar.YEAR);
		yearsAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		yearsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_year_newwords.setAdapter(yearsAdapter);
		for (int i = -20; i < 20; i++) {
			yearsAdapter.add(Integer.toString(year + i));
		}
		select_year_newwords.setSelection(20);
		select_month_newwords = (Spinner) findViewById(R.id.select_month_newwords);
		select_month_newwords.setSelection(tod.get(Calendar.MONTH));
		select_day_newwords = (Spinner) findViewById(R.id.select_day_newwords);
		select_day_newwords.setSelection(tod.get(Calendar.DAY_OF_MONTH) - 1);
		select_days_before = (Spinner) findViewById(R.id.select_days_before);
		reviewsAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		reviewsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_days_before.setAdapter(reviewsAdapter);
		tablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		tablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// TEXTVIEWS:
		newword_results = (TextView) findViewById(R.id.newword_results);

		login_status = (TextView) findViewById(R.id.edit_newwords_login_status);

		// tablesAdapter = new
		// ArrayAdapter.createFromResource(act2,R.array.empty_options,R.layout.my_spinner);

	}

	public void loadButtons() {
		get_words.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		get_next_newwords.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		get_last_newwords.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		latest_newwords.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_edit_newwords.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_one_table.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));

	}

	public void setListeners() {
		do_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				password = password_input.getText().toString();
				username = username_input.getText().toString();
				String login_results = Helpers.login(username, password);
				String[] login_spl = login_results.split("@@@");
				if (login_spl[0].equals("true")) {
					logged_in = true;
					new doLoadDatabases().execute();
				}
				login_status.setText(login_spl[1]);

			}
		});

		do_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logged_in = false;
				password_input.setText("");
				login_status.setText("LOGGED OUT. BYE BYE " + username);
				setStandardReviewAdapter();
			}
		});

		get_words.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (logged_in == false) {
					newword_results.setText(Html
							.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				text = "";
				for (int i = 0; i < select_words.size(); i++) {
					c = MainLfqActivity.getAcrosticsDb().rawQuery(
							"SELECT " + acrostics.Information+ "," + acrostics.Acrostics + " FROM "
									+ select_tables.get(i).getSelectedItem()
											.toString()
									+ " WHERE " + acrostics.Name + "='"
									+ select_words.get(i).getSelectedItem()
											.toString() + "'", null);
					if (c.moveToFirst()) {
						text += (i + 1)
								+ ")"
								+ select_words.get(i).getSelectedItem()
										.toString() + "<br />";
						text += "Information:<br />"
								+ c.getString(c.getColumnIndex(acrostics.Information))
								+ "<br />";
						text += "Acrostics:<br />"
								+ c.getString(c.getColumnIndex(acrostics.Acrostics))
								+ "<br />";
					}
					c.close();
				}
				input_newwords.setText(Html.fromHtml(text));
			}
		});

		do_edit_newwords.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (logged_in == false) {
					newword_results.setText(Html
							.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				newword_results.setText("");
				autosync_text = "";
				if (check_change_review_times.isChecked()) {
					reviewsAdapter.clear();
					String this_entry = "";
					String previous_entry = "";
					for (int i = 0; i < 10; i++) {
						if (i > 0) {
							this_entry = edit_reviews.get(i).getText()
									.toString();
							previous_entry = edit_reviews.get(i - 1).getText()
									.toString();
							if (!this_entry.equals("")) {
								if (previous_entry.equals("")) {
									newword_results.setText(Html
											.fromHtml("<b>NOT UPDATED! CAN'T LEAVE PREVOIOUS ENTRIES BLANK.</b>"));
									return;
								}
								if (Integer.parseInt(this_entry) <= Integer
										.parseInt(previous_entry)) {
									newword_results.setText(Html
											.fromHtml("<b>NOT UPDATED! EACH NEXT REVIEW TIME MUST BE GREATER THAN THE LAST.</b>"));
									return;
								}

							}
						}
					}
					String sync_review_values = "";
					cv.clear();
					for (int i = 0; i < 10; i++) {
						if (!edit_reviews.get(i).getText().toString()
								.equals("")) {
							cv.put(review_array[i], edit_reviews.get(i)
									.getText().toString());
							System.out.println("rev_arr[i]=" + review_array[i]
									+ ", edit="
									+ edit_reviews.get(i).getText().toString());
							reviewsAdapter.add(edit_reviews.get(i).getText()
									.toString());
							sync_review_values += review_array[i] + "='"
									+ edit_reviews.get(i).getText().toString()
									+ "',";
						} else {
							cv.put(review_array[i], "");
						}
					}
					MainLfqActivity.getMiscDb().update("user_review_times",
							cv, "UserName='" + username + "'", null);
					cv.clear();
					// STRIP THE TRAILING ',':
					sync_review_values = sync_review_values.substring(0,
							sync_review_values.length() - 1);
					sql = "UPDATE " + Helpers.db_prefix
							+ "misc." + tables.user_review_times + " SET "
							+ sync_review_values + " WHERE " + user_review_times.UserName + "='"
							+ username + "'";
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[] image)
					autosync_text += Synchronize.autoSync(sql, "misc_db",
							"update", tables.user_review_times, username, false,
							null);
					newword_results.setText(Html.fromHtml("<b>UPDATED "
							+ username + "'s REVIEW TIMES!" + autosync_text
							+ "</b>"));
					return;
				}// END IF check_review_times IS CHECKED
				int month_number = select_month_newwords
						.getSelectedItemPosition() + 1;
				int day_number = select_day_newwords.getSelectedItemPosition() + 1;
				String month_display_number = String.valueOf(month_number);
				String day_display_number = String.valueOf(day_number);
				if (month_display_number.length() == 1) {
					month_display_number = "0" + month_display_number;
				}
				if (day_display_number.length() == 1) {
					day_display_number = "0" + day_display_number;
				}
				String edidate = select_year_newwords.getSelectedItem()
						.toString()
						+ "/"
						+ month_display_number
						+ "/"
						+ day_display_number;
				results = "";
				int number_words = 0;
				if (edit_newwords.isChecked()) {
					results = "";
					number_words = Integer.parseInt(select_number_newwords
							.getSelectedItem().toString());
					newword_results.setText("");
					Cursor c_edit = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM " + tables.user_new_words + " WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Date + "='"
									+ edidate + "'", null);
					int ct_usr_wors = 0;
					if (c_edit.moveToFirst()) {
						do {
							MainLfqActivity.getMiscDb().execSQL(
									"UPDATE "
											+ tables.user_new_words
											+ " SET " + user_new_words.Word + "='"
											+ select_words.get(ct_usr_wors)
													.getSelectedItem()
													.toString()
											+ "','" + user_new_words.Table_name + "='"
											+ select_tables.get(ct_usr_wors)
													.getSelectedItem()
													.toString()
											+ "' WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Date + "='" + edidate
											+ "' LIMIT 1", null);
							results += "UPDATED "
									+ select_words.get(ct_usr_wors)
											.getSelectedItem().toString()
									+ " IN " + tables.user_new_words + ".";
							cv.clear();
							sql = "UPDATE "
									+ Helpers.db_prefix
									+ "newwords."
									+ tables.user_new_words
									+ " SET " + user_new_words.Word + "='"
									+ select_words.get(ct_usr_wors)
											.getSelectedItem().toString()
									+ "','" + user_new_words.Table_name + "='"
									+ select_tables.get(ct_usr_wors)
											.getSelectedItem().toString()
									+ "' WHERE "+ user_new_words.Username + "='" + username + "' AND " + user_new_words.Date + "='" + edidate
									+ "' LIMIT 1";
							// autoSync(sql, db, action, table, name, bool
							// is_image, byte[]
							// image)
							autosync_text += Synchronize.autoSync(sql, "misc_db",
									"update", tables.user_new_words, edidate, false, null);
							ct_usr_wors++;
						} while (c_edit.moveToNext());
					}
					// FOR THE WORDS NOT UPDATED UP TO THE NUMBER SPECIFIED...:
					for (int i = ct_usr_wors; i < number_words; i++) {
						// INSERT TO user_newwords_table:
						MainLfqActivity.getMiscDb().execSQL(
								"INSERT INTO "
										+ tables.user_new_words
										+ " (" + user_new_words.Username + "," + user_new_words.Date + "," + user_new_words.Word + "," + user_new_words.Table_name + ") VALUES('"
										+ username
										+ "','" 
										+ edidate
										+ "','"
										+ select_words.get(i).getSelectedItem()
												.toString()
										+ "','"
										+ select_tables.get(i)
												.getSelectedItem().toString()
										+ "')", null);
						cv.clear();
						sql = "INSERT INTO "
								+ tables.user_new_words
								+ " (" + user_new_words.Username + "," + user_new_words.Date + "," + user_new_words.Word + "," + user_new_words.Table_name + ") VALUES('"
								+ username
								+ "','"
								+ edidate
								+ "','"
								+ select_words.get(i).getSelectedItem()
										.toString()
								+ "','"
								+ select_tables.get(i).getSelectedItem()
										.toString() + "')";
						// autoSync(sql, db, action, table, name, bool is_image,
						// byte[]
						// image)
						autosync_text += Synchronize.autoSync(sql, "misc_db",
								"insert", tables.user_new_words, edidate, false, null);
						// INSERT TO user_reviewwords_table:
						cv.clear();
						// autoSync(sql, db, action, table, name, bool is_image,
						// byte[]
						// image)
						results += "INSERTED ADDITIONAL: "
								+ select_words.get(i).getSelectedItem()
										.toString() + " INTO " + tables.user_new_words
								+ ".";
					}
					newword_results.setText(results + autosync_text);
				}
				if (insert_newwords.isChecked()) {
					results = "";
					number_words = Integer.parseInt(select_number_newwords
							.getSelectedItem().toString());
					Cursor c_ins = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM " + tables.user_new_words + " WHERE "+ user_new_words.Username + "='" + username + "' AND " + user_new_words.Date + "='"
									+ edidate + "'", null);
					if (c_ins.moveToFirst()) {
						newword_results.setText("Date " + edidate
								+ " already exists.");
					} else {
						int j = 0;
						results += " INSERTED " + number_words
								+ " CHOSEN WORDS: ";
						sql = "INSERT INTO " + Helpers.db_prefix + "misc."
								+ tables.user_new_words
								+ " (" + user_new_words.Username + "," + user_new_words.Table_name + "," + user_new_words.Word + "," + user_new_words.Date + ") Values";
						for (int i = 0; i < number_words; i++) {
							j = i + 1;							 
							cv.clear();
						    cv.put(user_new_words.Username, username);							 
							cv.put(user_new_words.Table_name, select_tables.get(i)
									.getSelectedItem().toString());
							cv.put(user_new_words.Word, select_words.get(i)
									.getSelectedItem().toString());
							cv.put(user_new_words.Date, edidate);
							MainLfqActivity.getMiscDb().insert(tables.user_new_words,
									null, cv);

							if (i != 0) {
								sql += ",";
								sql2 += ",";
							}
							sql += "('"
									+ username
									+ "','"
									+ select_tables.get(i).getSelectedItem()
											.toString()
									+ "','"
									+ select_words.get(i).getSelectedItem()
											.toString() + "','" + edidate
									+ "')";
							sql2 += "('"
									+ username
									+ "','"									
									+ select_tables.get(i).getSelectedItem()
											.toString()
									+ "','"
									+ select_words.get(i).getSelectedItem()
											.toString() + "')";
							System.out.println("INSERT NEWWORDS SQL1=" + sql
									+ ". SQL2=" + sql2);
							results += j
									+ ")"
									+ select_words.get(i).getSelectedItem()
											.toString() + " ";
						}// END for loop of # new words
							// autoSync(sql, db, action, table, name, bool
							// is_image, byte[]
							// image)
						autosync_text += Synchronize.autoSync(sql, "misc_db",
								"insert", tables.user_new_words, edidate, false, null);
						results += ".";
						newword_results.setText(results + autosync_text);
					}
					c_ins.close();
				}
				if (delete_newwords.isChecked()) {
					MainLfqActivity.getMiscDb().execSQL(
							"DELETE FROM " + tables.user_new_words + " WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Date + "='"
									+ edidate + "'");
					newword_results.setText(edidate + " DELETED FROM "
							+ tables.user_new_words + ".");
					cv.clear();
					sql = "DELETE FROM " + Helpers.db_prefix + "newwords."
							+ tables.user_new_words + " WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Date + "='" + edidate + "'";
					autosync_text += Synchronize.autoSync(sql, "misc_db",
							"delete", tables.user_new_words, edidate, false, null);
					newword_results
							.setText("DELETED NEWWORDS." + autosync_text);
				}
				System.out.println("doSetOneTables called do_edit_newwords");
				new doSetOneTables().execute();
			}
		});

		get_last_newwords.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (logged_in == false) {
					newword_results.setText(Html
							.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				Calendar c_last = Calendar.getInstance();
				c_last.set(Calendar.YEAR, Integer.parseInt(select_year_newwords
						.getSelectedItem().toString()));
				c_last.set(Calendar.MONTH,
						select_month_newwords.getSelectedItemPosition());
				c_last.set(Calendar.DAY_OF_MONTH,
						select_day_newwords.getSelectedItemPosition() + 1);
				c_last.add(Calendar.DATE, -1);
				select_year_newwords.setSelection(c_last.get(Calendar.YEAR)
						- tod.get(Calendar.YEAR) + 20);
				select_month_newwords.setSelection(c_last.get(Calendar.MONTH));
				select_day_newwords.setSelection(c_last
						.get(Calendar.DAY_OF_MONTH) - 1);
				getWords();
			}
		});

		get_next_newwords.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (logged_in == false) {
					newword_results.setText(Html
							.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				Calendar c_nex = Calendar.getInstance();
				c_nex.set(Calendar.YEAR, Integer.parseInt(select_year_newwords
						.getSelectedItem().toString()));
				c_nex.set(Calendar.MONTH,
						select_month_newwords.getSelectedItemPosition());
				c_nex.set(Calendar.DAY_OF_MONTH,
						select_day_newwords.getSelectedItemPosition() + 1);
				c_nex.add(Calendar.DATE, 1);
				select_year_newwords.setSelection(c_nex.get(Calendar.YEAR)
						- tod.get(Calendar.YEAR) + 20);
				select_month_newwords.setSelection(c_nex.get(Calendar.MONTH));
				select_day_newwords.setSelection(c_nex
						.get(Calendar.DAY_OF_MONTH) - 1);
				getWords();
			}
		});

		latest_newwords.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (logged_in == false) {
					newword_results.setText(Html
							.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				select_year_newwords.setSelection(20);
				select_month_newwords.setSelection(tod.get(Calendar.MONTH));
				select_day_newwords.setSelection(tod.get(Calendar.DAY_OF_MONTH) - 1);
				getWords();
			}
		});

		select_days_before
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (logged_in == false) {
							newword_results.setText(Html
									.fromHtml("<b>NOT LOGGED IN!</b>"));
							return;
						}
						Calendar c_get_bef = Calendar.getInstance();
						c_get_bef.set(Calendar.YEAR, Integer
								.parseInt(select_year_newwords
										.getSelectedItem().toString()));
						c_get_bef.set(Calendar.MONTH,
								select_month_newwords.getSelectedItemPosition());
						c_get_bef
								.set(Calendar.DAY_OF_MONTH, select_day_newwords
										.getSelectedItemPosition() + 1);
						c_get_bef.add(Calendar.DATE, -Integer
								.parseInt(select_days_before.getSelectedItem()
										.toString()));
						select_year_newwords.setSelection(c_get_bef
								.get(Calendar.YEAR)
								- tod.get(Calendar.YEAR)
								+ 20);
						select_month_newwords.setSelection(c_get_bef
								.get(Calendar.MONTH));
						select_day_newwords.setSelection(c_get_bef
								.get(Calendar.DAY_OF_MONTH) - 1);
						getWords();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

		check_change_review_times
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (check_change_review_times.isChecked()) {
							reviews_layout.removeAllViews();
							edit_reviews.clear();
							if (logged_in == false) {
								newword_results.setText(Html
										.fromHtml("<b>NOT LOGGED IN!</b>"));
								return;
							}
							for (int i = 1; i <= 10; i++) {
								LinearLayout review_layout = new LinearLayout(
										this_act);
								review_layout
										.setOrientation(LinearLayout.HORIZONTAL);
								TextView prompt_review = new TextView(this_act);
								prompt_review.setText("REVIEW " + i + ":");
								review_layout.addView(prompt_review);
								final EditText edit_review = new EditText(
										this_act);
								params = new LinearLayout.LayoutParams(100,
										LinearLayout.LayoutParams.WRAP_CONTENT);
								edit_review.setLayoutParams(params);
								edit_review
										.setBackgroundResource(R.drawable.rounded_edittext_red);
								edit_review
										.setInputType(InputType.TYPE_CLASS_NUMBER);
								edit_review.setMaxLines(1);
								review_layout.addView(edit_review);
								edit_reviews.add(edit_review);
								reviews_layout.addView(review_layout);
							}
							for (int i = 0; i < reviewsAdapter.getCount(); i++) {
								edit_reviews.get(i).setText(
										reviewsAdapter.getItem(i).toString());
							}
						} else {
							reviews_layout.removeAllViews();
						}

					}
				});

		do_one_table.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (logged_in == false) {
					newword_results.setText(Html
							.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				is_one_table_clicked = true;
				System.out.println("doSetOneTables called from_do_one_table");
				new doSetOneTables().execute();

			}
		});
	}

	public void getAutoNewWords() {
		if (logged_in == false) {
			newword_results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
			return;
		}
		System.out.println("AUTONEW WORDS CALLED");
		newword_results.setText("");
		show_completes.clear();
		select_tables.clear();
		select_words.clear();
		words_adapters.clear();
		edit_newwords_layout.removeAllViews();
		for (int i = 1; i <= Integer.parseInt(select_number_newwords
				.getSelectedItem().toString()); i++) {
			// SET LAYOUT
			LinearLayout edit_newword_single_layout = new LinearLayout(this_act);
			edit_newword_single_layout.setOrientation(LinearLayout.HORIZONTAL);
			// ADD PROMPT
			TextView prompt_newword = new TextView(this_act);
			prompt_newword.setText(Html.fromHtml("<b>" + i + "</b>"));
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f);
			prompt_newword.setLayoutParams(params);
			edit_newword_single_layout.addView(prompt_newword);

			// ADD SELECT TABLE:
			final Spinner select_table = new Spinner(this_act);
			select_table.setAdapter(tablesAdapter);
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f);
			select_table.setLayoutParams(params);
			edit_newword_single_layout.addView(select_table);
			select_tables.add(select_table);
			// ADD SELECT WORD:
			final ArrayAdapter<String> wordsAdapter = new ArrayAdapter<String>(
					this_act, android.R.layout.simple_spinner_item,
					new ArrayList<String>());
			wordsAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			words_adapters.add(wordsAdapter);
			Spinner select_word = new Spinner(this_act);
			select_word.setAdapter(wordsAdapter);
			select_words.add(select_word);
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f);
			select_word.setLayoutParams(params);
			edit_newword_single_layout.addView(select_word);
			// ADD SHOW COMPLETES RESULTS:
			final TextView show_complete = new TextView(this_act);
			show_completes.add(show_complete);
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f);
			show_complete.setLayoutParams(params);
			edit_newword_single_layout.addView(show_complete);
			edit_newwords_layout.addView(edit_newword_single_layout);

			select_table.setOnTouchListener(new View.OnTouchListener() {

				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					is_one_table_clicked = false;
					return false;
				}
			});

			select_table
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

							if (is_setting_tables==false){
							String table = select_table.getSelectedItem()
									.toString();
							sql = "SELECT DISTINCT " + user_new_words.Word + " FROM " + tables.user_new_words
									+ " WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Table_name + "='" + table + "'";
							c = MainLfqActivity.getMiscDb().rawQuery(sql, null);
							review_words.clear();
							int ct_rev_wor = 0;
							if (c.moveToFirst()) {
								do {
									review_words.add(c.getString(0));
									ct_rev_wor++;
								} while (c.moveToNext());
							}
							c.close();
							String rev_str = TextUtils.join("','", review_words
									.toArray(new String[review_words.size()]));
							// System.out.println("rev_str=" + rev_str);
							c = MainLfqActivity.getAcrosticsDb().rawQuery(
									"SELECT DISTINCT " + acrostics.Name + " FROM " + table
											+ " WHERE " + acrostics.Acrostics + "<>'' AND " + acrostics.Name + " NOT IN ('"
											+ rev_str + "')", null);

							int ct_available_words = 0;
							wordsAdapter.clear();
							if (c.moveToFirst()) {
								do {
									ct_available_words++;
									wordsAdapter.add(c.getString(0));
								} while (c.moveToNext());
							}
							Comparator<String> comparator = new Comparator<String>() {

								@SuppressLint("DefaultLocale")
								@Override
								public int compare(String str1, String str2) {
									return str1.toUpperCase(Locale.US)
											.compareTo(str2.toUpperCase());
								}
							};
							wordsAdapter.sort(comparator);
							show_complete.setText(Html.fromHtml("<b>"
									+ ct_available_words + " words</b>"));
							c.close();							
							
							
							
                          /*
							String table = select_table.getSelectedItem()
									.toString();
							c = MainLfqActivity.getMiscDb().rawQuery(
									"SELECT Word FROM " + user_review_table
											+ " WHERE Table_name='" + table
											+ "'", null);
							review_words.clear();
							System.out.println("REVIEWED WORDS:");
							int ct_rev_wor = 0;
							if (c.moveToFirst()) {
								do {
									review_words.add(c.getString(0));
									ct_rev_wor++;
								} while (c.moveToNext());
							}
							System.out.println(ct_rev_wor);
							c.close();
							c = MainLfqActivity.getAcrosticsDb().rawQuery(
									"SELECT Name FROM " + table
											+ " WHERE Acrostics<>''", null);
							int ct_available_words = 0;
							wordsAdapter.clear();
							Boolean is_reviewed = false;
							if (c.moveToFirst()) {
								do {
									is_reviewed = false;
									for (String str : review_words) {
										if (str.toLowerCase(Locale.ENGLISH)
												.equals(c.getString(0)
														.toLowerCase(
																Locale.ENGLISH))) {
											System.out.println("rev=" + str
													+ ", acr_wor="
													+ c.getString(0));
											is_reviewed = true;
											break;
										}
									}
									if (!is_reviewed) {
										ct_available_words++;
										wordsAdapter.add(c.getString(0));
									}
								} while (c.moveToNext());
							}
							Comparator<String> comparator = new Comparator<String>() {

								@SuppressLint("DefaultLocale")
								@Override
								public int compare(String str1, String str2) {
									return str1.toUpperCase(Locale.US)
											.compareTo(str2.toUpperCase());
								}
							};
							wordsAdapter.sort(comparator);
							show_complete.setText(Html.fromHtml("<b>"
									+ ct_available_words + " words</b>"));
							c.close();
							*/
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
						}

					});

		}// END FOR LOOP
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			is_database_load = true;
			setContentView(R.layout.edit_newwords);
			setViews();
			loadButtons();
			text = "Loading databases. Please wait...";
			newword_results.setText(Html.fromHtml("<b>" + text + "<b>"));

		}

		@Override
		protected String doInBackground(String... params) {
			return null;
		}

		public void doProgress(String value) {
			publishProgress(value);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			newword_results.setText(Html.fromHtml(values[0]));
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected void onPostExecute(String file_url) {
			doResume();
		}

	}

	private void doResume() {
		check_change_review_times.setChecked(sharedPref.getBoolean(
				"EDIT NEWWORDS CHECK CHANGE REVIEW TIMES", false));
		setStandardReviewAdapter();
		setTables();
		setListeners();
		select_number_newwords.setOnTouchListener(select_number_listener);
		select_number_newwords
				.setOnItemSelectedListener(select_number_listener);
		select_number_newwords
				.setSelection(((ArrayAdapter) select_number_newwords
						.getAdapter()).getPosition(sharedPref.getString(
						"EDIT NEWWORDS NUMBER NEWWORDS", select_number_newwords
								.getItemAtPosition(0).toString())));
		edit_newwords.setChecked(sharedPref.getBoolean(
				"EDIT NEWWORDS EDIT NEWWORDS", false));
		if (Helpers.getLoginStatus() == true) {
			username = Helpers.getUsername();
			password = Helpers.getPassword();
			username_input.setText(username);
			logged_in = true;
			login_status.setText("WELCOME " + username + ".");
			setReviewAdapter();
			getAutoNewWords();
			if (select_tables.size() > 0) {
				select_tables.get(0).setSelection(
						sharedPref.getInt("EDIT NEWWORDS SELECTED TABLE INDEX",
								0));
			}
			if (sharedPref.getBoolean("EDIT NEWWORDS ONE TABLE", false)) {
				// new doSetOneTables().execute();
				// System.out.println("EXECUTING 1 table!!!!!");
			}
			getWords();
		}
		input_newwords.setText(sharedPref.getString(
				"EDIT NEWWORDS NEWWORDS INPUT", ""));
		check_all_same_table.setChecked(sharedPref.getBoolean(
				"EDIT NEWWORDS ALL SAME", false));
		delete_newwords.setChecked(sharedPref.getBoolean(
				"EDIT NEWWORDS DELETE NEWWORDS", false));
		insert_newwords.setChecked(sharedPref.getBoolean(
				"EDIT NEWWORDS INSERT", false));
		is_database_load = false;
	}

	private void setStandardReviewAdapter() {
		reviewsAdapter.clear();
		reviewsAdapter.add("0");
		reviewsAdapter.add("1");
		reviewsAdapter.add("7");
		reviewsAdapter.add("30");
		reviewsAdapter.add("180");
	}

	private void setReviewAdapter() {
		if (logged_in == false) {
			newword_results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
			return;
		}
		reviewsAdapter.clear();
		Cursor c_reviews = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT * FROM " + tables.user_review_times + " WHERE " + user_review_times.UserName+ "='" + username
						+ "'", null);
		if (c_reviews.moveToFirst()) {
			review_array = new String[] { user_review_times.Time1, user_review_times.Time2, user_review_times.Time3, user_review_times.Time4,
					user_review_times.Time5, user_review_times.Time6, user_review_times.Time7, user_review_times.Time8, user_review_times.Time9, user_review_times.Time10 };
			for (int i = 0; i < review_array.length; i++) {
				if (!c_reviews.getString(
						c_reviews.getColumnIndex(review_array[i])).equals("")) {
					reviewsAdapter.add(c_reviews.getString(c_reviews
							.getColumnIndex(review_array[i])));
				}
			}
		}
		c_reviews.close();
	}

	public void getWords() {
		if (logged_in == false) {
			newword_results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
			return;
		}
		results = "";
		int ct_get = 1;
		int month_number = select_month_newwords.getSelectedItemPosition() + 1;
		int day_number = select_day_newwords.getSelectedItemPosition() + 1;
		String month_display_number = String.valueOf(month_number);
		String day_display_number = String.valueOf(day_number);
		if (month_display_number.length() == 1) {
			month_display_number = "0" + month_display_number;
		}
		if (day_display_number.length() == 1) {
			day_display_number = "0" + day_display_number;
		}
		String edidate = select_year_newwords.getSelectedItem().toString()
				+ "/" + month_display_number + "/" + day_display_number;
		Cursor c_get = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT * FROM " + tables.user_new_words + " WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Date + "='" + edidate
						+ "'", null);
		if (c_get.moveToFirst()) {
			do {
				Cursor c_get_acrostics = MainLfqActivity.getAcrosticsDb()
						.rawQuery(
								"SELECT " + acrostics.Information + "," + acrostics.Acrostics + " FROM "
										+ c_get.getString(c_get
												.getColumnIndex(user_new_words.Table_name))
										+ " WHERE Name='"
										+ c_get.getString(c_get
												.getColumnIndex(user_new_words.Word)) + "'",
								null);
				if (c_get_acrostics.moveToFirst()) {
					results += ct_get
							+ ")"
							+ c_get.getString(c_get.getColumnIndex(user_new_words.Word))
							+ "<br />"
							+ " INFORMATION:<br />"
							+ c_get_acrostics.getString(c_get_acrostics
									.getColumnIndex(acrostics.Information))
							+ "<br />ACROSTICS:<br />"
							+ c_get_acrostics.getString(c_get_acrostics
									.getColumnIndex(acrostics.Acrostics)) + "<br />";
					ct_get++;
				}
			} while (c_get.moveToNext());
		}
		newword_results.setText(edidate + " WORDS:");
		c_get.close();
		input_newwords.setText(Html.fromHtml(results));
	}

	private void setTables() {
		tablesAdapter.clear();
		Cursor c_acr_tables = MainLfqActivity.getAcrosticsDb().rawQuery(
				"SELECT name FROM sqlite_master "
						+ " WHERE type='table' ORDER BY name", null);
		c_acr_tables.moveToFirst();
		do {
			tablesAdapter.add(c_acr_tables.getString(0));
		} while (c_acr_tables.moveToNext());
		c_acr_tables.close();
	}

	class doSetOneTables extends AsyncTask<String, String, String> {
		List<List<String>> list_words;
		String my_table;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			is_setting_tables = true;
			if (select_tables.size() == 0) {
				return;
			}
			for (int i = 0; i < words_adapters.size(); i++) {
				words_adapters.get(i).clear();
			}
			list_words = new ArrayList<List<String>>();
			if (check_all_same_table.isChecked()) {
				my_table = select_tables.get(0).getSelectedItem().toString();
				for (int i = 0; i < select_tables.size(); i++) {
					select_tables.get(i).setSelection(
							tablesAdapter.getPosition(my_table));
				}
			}
		}

		@Override
		protected String doInBackground(String... params) {
			System.out.println("check_all_same_table="
					+ check_all_same_table.isChecked());
			// FOR ALL 1 TABLE:
			if (check_all_same_table.isChecked()) {
				System.out.println("DOING CHECK ALL!");
				list_words.add(new ArrayList<String>());
				my_table = select_tables.get(0).getSelectedItem().toString();
				// System.out.println("my_table=" + my_table);
				sql = "SELECT DISTINCT " + user_new_words.Word + " FROM " + tables.user_new_words
						+ " WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Table_name + "='" + my_table + "'";
				c = MainLfqActivity.getMiscDb().rawQuery(sql, null);
				System.out.println("user newwords sql=" + sql);
				review_words.clear();
				System.out.println("REVIEWED WORDS:");
				int ct_rev_wor = 0;
				if (c.moveToFirst()) {
					do {
						review_words.add(c.getString(0));
						ct_rev_wor++;
					} while (c.moveToNext());
				}
				c.close();
				String rev_str = TextUtils.join("','",
						review_words.toArray(new String[review_words.size()]));
				System.out.println("rev_str=" + rev_str);
				c = MainLfqActivity.getAcrosticsDb().rawQuery(
						"SELECT DISTINCT " + acrostics.Name + " FROM " + my_table
								+ " WHERE " + acrostics.Acrostics + "<>'' AND " + acrostics.Name + " NOT IN ('"
								+ rev_str + "')", null);
				int ct_available_words = 0;

				if (c.moveToFirst()) {
					do {
						ct_available_words++;
						list_words.get(0).add(c.getString(0));
					} while (c.moveToNext());
				}
				for (int i = 1; i < select_tables.size(); i++) {
					list_words.add(new ArrayList<String>());
					list_words.get(i).addAll(list_words.get(0));

				}
			} else {
				// FOR DIFFERENT TABLES:
				System.out.println("DOING DIFFERENT TABLES!");
				String table;
				Boolean is_reviewed;
				for (int i = 0; i < select_tables.size(); i++) {
					list_words.add(new ArrayList<String>());
					table = select_tables.get(i).getSelectedItem().toString();
					sql = "SELECT DISTINCT " + user_new_words.Word + " FROM " + tables.user_new_words
							+ " WHERE " + user_new_words.Username + "='" + username + "' AND " + user_new_words.Table_name + "='" + table + "'";
					// System.out.println("user newwords sql=" + sql);
					c = MainLfqActivity.getMiscDb().rawQuery(sql, null);
					review_words.clear();
					int ct_rev_wor = 0;
					if (c.moveToFirst()) {
						do {
							review_words.add(c.getString(0));
							ct_rev_wor++;
						} while (c.moveToNext());
					}
					c.close();
					String rev_str = TextUtils.join("','", review_words
							.toArray(new String[review_words.size()]));
					// System.out.println("rev_str=" + rev_str);
					c = MainLfqActivity.getAcrosticsDb().rawQuery(
							"SELECT DISTINCT " + acrostics.Name + " FROM " + table
									+ " WHERE " + acrostics.Acrostics + "<>'' AND " + acrostics.Name + " NOT IN ('"
									+ rev_str + "')", null);

					int ct_available_words = 0;
					if (c.moveToFirst()) {
						do {
							ct_available_words++;
							list_words.get(i).add(c.getString(0));
						} while (c.moveToNext());
					}
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

		@Override
		protected void onPostExecute(String file_url) {
			if (select_tables.size() == 0) {
				return;
			}
			Comparator<String> comparator = new Comparator<String>() {
				@SuppressLint("DefaultLocale")
				@Override
				public int compare(String str1, String str2) {
					return str1.toUpperCase(Locale.US).compareTo(
							str2.toUpperCase());
				}
			};
			System.out.println("words_adapter size=" + words_adapters.size()
					+ ", list_words size=" + list_words.size());
			for (int i = 0; i < words_adapters.size(); i++) {
				words_adapters.get(i).addAll(list_words.get(i));
				words_adapters.get(i).sort(comparator);
				show_completes.get(i).setText(
						Html.fromHtml("<b>" + list_words.get(i).size()
								+ " words</b>"));
			}
			if (check_all_same_table.isChecked()) {
				for (int i = 0; i < select_words.size(); i++) {
					select_words.get(i).setSelection(i);
				}
			}
			is_setting_tables = false;
		}

	}

	public class SelectNumberListener implements
			AdapterView.OnItemSelectedListener, View.OnTouchListener {
		public Boolean userSelect = false;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (userSelect) {
				getAutoNewWords();
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}
}