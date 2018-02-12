package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.lfq.learnfactsquick.Constants.cols.dictionarya;
import com.lfq.learnfactsquick.Constants.cols.global_numbers;
import com.lfq.learnfactsquick.Constants.cols.user_events;
import com.lfq.learnfactsquick.Constants.cols.user_numbers_table;
import com.lfq.learnfactsquick.Constants.cols.user_numbers;
import com.lfq.learnfactsquick.Constants.tables;
import com.lfq.learnfactsquick.Constants.cols.events_table;
import com.lfq.learnfactsquick.Constants.cols.sync_table;
import com.lfq.learnfactsquick.Constants.cols.user_events;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class Timeline extends Activity {
	private RelativeLayout above_controls;
	private LinearLayout event_layout, words_layout, savewords_layout,
			prompt_events_layout;
	private TextView prompt_timeline_event, prompt_timeline_date,
			prompt_timeline_words, results, results2, login_status;
	private TextView event_tv, show_year, show_date;
	private Spinner select_timeline_completed, select_timeline_month,
			select_timeline_day, select_timeline_number_major_words;
	private Spinner select_number_major_words_again, select_years;
	private EditText input_username, password_input;
	private EditText event_edit_et;
	private CheckBox check_condensed_words, check_timeline_get_words,
			check_shared_events, check_save_timeline_global,
			check_save_timeline_personal;
	private Button do_timeline_get_date_events, do_timeline_get_year_events,
			do_timeline_show_all_saved, do_timeline_left, do_timeline_right,
			do_edit_timeline_event, do_save_words, do_login, do_logout,
			do_timeline_above, get_user_personal_events,
			get_user_historical_events;
	private ScrollView timeline_event_scroll, timeline_words_scroll;

	private Cursor c = null, c2 = null, c_get_last, c_get_next, c_years = null,
			c_totals = null;
	private android.widget.RelativeLayout.LayoutParams params;
	private Boolean is_event_edit, logged_in, display_savedwords;
	private List<String> input_saved_numbers, input_saved_words,
			input_saved_infos, months, days, months_complete;
	private List<String> list_saved_numbers, list_saved_words,
			list_saved_infos;
	private int number, ct_tot, ct_all;
	private String out, begnum, midnum, endnum, which_events, id, ign_lets,
			dbl_lets, username, password, wriwor, year, date, edit_date_number,
			event, display_date, date_number_string;
	private List<List<String>> edit_save_words;
	private String edit_save_words_string;
	private int own_word_ind;
	private List<EditText> input_words;
	private List<RadioGroup> radio_groups;
	private ArrayAdapter<String> timesDoneAdapter, yearsAdapter;
	private String text, type_clause, saved_words;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	// private String sql;
	private static Boolean is_database_load, is_years_load;
	// private static String device_id;
	private static String table, lfq_table, sql, database_string;
	private int total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		// TelephonyManager telephonyManager = (TelephonyManager) this
		// .getSystemService(Context.TELEPHONY_SERVICE);
		// device_id = telephonyManager.getDeviceId();
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		is_years_load = false;
		text = "";
		logged_in = false;
		display_savedwords = true;
		year = "";
		date = "";
		edit_date_number = "";
		ign_lets = "aeiouwxy";
		dbl_lets = "cgpst";
		display_date = "";
		out = "";
		which_events = "";
		id = "";
		own_word_ind = 0;
		username = "";
		password = "";
		edit_save_words_string = "";
		months = new ArrayList<String>();
		days = new ArrayList<String>();
		months_complete = new ArrayList<String>();
		input_words = new ArrayList<EditText>();
		radio_groups = new ArrayList<RadioGroup>();
		input_saved_numbers = new ArrayList<String>();
		input_saved_words = new ArrayList<String>();
		input_saved_infos = new ArrayList<String>();
		list_saved_numbers = new ArrayList<String>();
		list_saved_words = new ArrayList<String>();
		list_saved_infos = new ArrayList<String>();
		edit_save_words = new ArrayList<List<String>>();
		months.addAll(Arrays.asList(new String[] { "01", "02", "03", "04",
				"05", "06", "07", "08", "09", "10", "11", "12" }));
		days.addAll(Arrays.asList(new String[] { "01", "02", "03", "04", "05",
				"06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
				"16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
				"26", "27", "28", "29", "30", "31" }));
		months_complete.addAll(Arrays.asList(new String[] { "January",
				"February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" }));
		number = 0;
		is_event_edit = false;
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
	}

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
		editor.putString("TIMELINE USERNAME INPUT", input_username.getText()
				.toString());
		editor.putBoolean("TIMELINE CHECK CONDENSED",
				check_condensed_words.isChecked());
		editor.putBoolean("TIMELINE CHECK GET WORDS",
				check_timeline_get_words.isChecked());
		editor.putBoolean("TIMELINE CHECK SHARED EVENTS",
				check_shared_events.isChecked());
		editor.putBoolean("TIMELINE CHECK SAVE GLOBAL",
				check_save_timeline_global.isChecked());
		editor.putBoolean("TIMELINE CHECK SAVE PERSONAL",
				check_save_timeline_personal.isChecked());
		editor.putString("TIMELINE SELECT NUMBER MAJOR WORDS",
				select_timeline_number_major_words.getSelectedItem().toString());
		editor.commit();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		// LAYOUTS:
		above_controls = (RelativeLayout) findViewById(R.id.timeline_above_controls);
		above_controls.setVisibility(View.VISIBLE);
		prompt_events_layout = (LinearLayout) findViewById(R.id.prompt_timeline_events_layout);
		event_layout = (LinearLayout) findViewById(R.id.timeline_event_layout);
		savewords_layout = (LinearLayout) findViewById(R.id.timeline_savewords_layout);
		words_layout = (LinearLayout) findViewById(R.id.timeline_words_layout);

		// BUTTONS:
		do_login = (Button) findViewById(R.id.timeline_login);
		do_logout = (Button) findViewById(R.id.timeline_logout);
		do_timeline_get_date_events = (Button) findViewById(R.id.do_timeline_get_date_events);
		do_timeline_get_year_events = (Button) findViewById(R.id.do_timeline_get_year_events);
		get_user_personal_events = (Button) findViewById(R.id.do_timeline_get_user_personal_events);
		get_user_historical_events = (Button) findViewById(R.id.do_timeline_get_user_historical_events);
		do_timeline_show_all_saved = (Button) findViewById(R.id.do_timeline_show_all_saved);
		do_timeline_left = (Button) findViewById(R.id.do_timeline_left);
		do_timeline_above = (Button) findViewById(R.id.do_timeline_above);
		do_timeline_above.setVisibility(View.GONE);
		do_timeline_right = (Button) findViewById(R.id.do_timeline_right);
		do_edit_timeline_event = (Button) findViewById(R.id.do_edit_timeline_event);
		do_save_words = (Button) findViewById(R.id.do_save_timeline_words);

		// CHECKBOXES:
		check_condensed_words = (CheckBox) findViewById(R.id.check_condensed_words);
		check_timeline_get_words = (CheckBox) findViewById(R.id.check_timeline_get_words);
		check_timeline_get_words.setChecked(true);
		check_save_timeline_global = (CheckBox) findViewById(R.id.check_save_timeline_global);
		check_save_timeline_personal = (CheckBox) findViewById(R.id.check_save_timeline_personal);
		check_shared_events = (CheckBox) findViewById(R.id.check_timeline_shared_events);

		// EDITTEXTS:
		input_username = (EditText) findViewById(R.id.timeline_username);
		password_input = (EditText) findViewById(R.id.timeline_password);

		// SCROLLVIEWS:
		timeline_event_scroll = (ScrollView) findViewById(R.id.timeline_event_scroll);
		timeline_words_scroll = (ScrollView) findViewById(R.id.timeline_words_scroll);

		// SPINNERS:
		select_timeline_completed = (Spinner) findViewById(R.id.select_timeline_completed);
		timesDoneAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		timesDoneAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_timeline_completed.setAdapter(timesDoneAdapter);
		select_timeline_month = (Spinner) findViewById(R.id.select_timeline_month);
		select_timeline_day = (Spinner) findViewById(R.id.select_timeline_day);
		select_timeline_number_major_words = (Spinner) findViewById(R.id.select_timeline_number_major_words);
		select_years = (Spinner) findViewById(R.id.select_timeline_years);
		yearsAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		yearsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_years.setAdapter(yearsAdapter);

		// TEXTVIEWS:
		login_status = (TextView) findViewById(R.id.timeline_login_status);
		results = (TextView) findViewById(R.id.timeline_results);
		results2 = (TextView) findViewById(R.id.timeline_results2);
		prompt_timeline_event = (TextView) findViewById(R.id.prompt_timeline_event);
		prompt_timeline_date = (TextView) findViewById(R.id.prompt_timeline_date);
		prompt_timeline_words = (TextView) findViewById(R.id.prompt_timeline_words);
		prompt_timeline_event.setText(Html.fromHtml("<b><u>EVENT:</u></b>"));
		prompt_timeline_words.setText(Html
				.fromHtml("<u><b>SELECT MAJOR WORDS FOR DATE:</b></u>"));
		show_year = (TextView) findViewById(R.id.prompt_timeline_show_year);
		show_date = (TextView) findViewById(R.id.prompt_timeline_show_date);
	}

	public void loadButtons() {
		get_user_personal_events.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		get_user_historical_events.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_timeline_get_date_events.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		do_timeline_get_year_events.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		do_timeline_show_all_saved.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		do_timeline_left.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_timeline_above.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_timeline_right.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_edit_timeline_event.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		do_save_words.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {

		check_shared_events.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setTable();
			}
		});

		do_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				password = password_input.getText().toString();
				username = input_username.getText().toString();
				String login_results = Helpers.login(username, password);
				String[] login_spl = login_results.split("@@@");
				if (login_spl[0].equals("true")) {
					logged_in = true;
				}
				login_status.setText(login_spl[1]);
				setTable();
			}
		});

		do_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logged_in = false;
				password_input.setText("");
				login_status.setText("LOGGED OUT. BYE BYE " + username);
			}
		});

		do_timeline_above.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				above_controls.setVisibility(View.VISIBLE);
				prompt_timeline_words.setVisibility(View.VISIBLE);
				event_layout.setVisibility(View.VISIBLE);
				do_timeline_left.setVisibility(View.VISIBLE);
				do_timeline_right.setVisibility(View.VISIBLE);
				prompt_events_layout.setVisibility(View.VISIBLE);
				savewords_layout.setVisibility(View.VISIBLE);
				params = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 75);
				params.addRule(RelativeLayout.BELOW,
						prompt_events_layout.getId());
				timeline_event_scroll.setLayoutParams(params);
				do_timeline_above.setVisibility(View.GONE);
				params = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 60);
				params.addRule(RelativeLayout.BELOW, savewords_layout.getId());
				timeline_words_scroll.setLayoutParams(params);
			}
		});

		check_save_timeline_personal
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (check_save_timeline_personal.isChecked()) {
							if (logged_in == false) {
								results.setText(Html
										.fromHtml("<b><u>SORRY, NOT LOGGED IN!</u></b>"));
								check_save_timeline_personal.setChecked(false);
							}
						}
					}
				});

		do_timeline_get_date_events
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setTable();
						display_savedwords = true;
						which_events = "date_events";
						getEvents("shared_date_events");

					}
				});

		do_timeline_get_year_events
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						display_savedwords = true;
						which_events = "year_events";
						getEvents("shared_year_events");
					}

				});

		do_timeline_show_all_saved
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						prompt_timeline_words.setVisibility(View.GONE);
						above_controls.setVisibility(View.GONE);
						event_layout.setVisibility(View.GONE);
						do_timeline_left.setVisibility(View.GONE);
						do_timeline_right.setVisibility(View.GONE);
						prompt_events_layout.setVisibility(View.GONE);
						savewords_layout.setVisibility(View.GONE);
						do_timeline_above.setVisibility(View.VISIBLE);
						params = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.MATCH_PARENT, 600);
						params.addRule(RelativeLayout.BELOW,
								savewords_layout.getId());
						timeline_words_scroll.setLayoutParams(params);
						words_layout.removeAllViews();
						event_layout.removeAllViews();
						String save_words = "", save_mnes = "";
						c = MainLfqActivity.getMiscDb().rawQuery(
								"SELECT * FROM " + table + " WHERE "
										+ events_table.Number1 + "<>'' AND "
										+ events_table.Year
										+ " LIKE '% BC' ORDER BY "
										+ events_table.Year + " DESC", null);
						if (c.moveToFirst()) {
							do {
								TextView tv = new TextView(this_act);
								tv.setTextSize(15);
								save_words = "";
								save_mnes = "";
								if (!c.getString(
										c.getColumnIndex(events_table.Number1))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number1))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic1));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info1))
											+ ")";
								}
								if (!c.getString(
										c.getColumnIndex(events_table.Number2))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number2))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic2));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info2))
											+ ")";
								}
								if (!c.getString(
										c.getColumnIndex(events_table.Number3))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number3))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic3));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info3))
											+ ")";
								}
								if (!c.getString(
										c.getColumnIndex(events_table.Number4))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number4))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic4));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info4))
											+ ")";
								}
								tv.setText(Html.fromHtml("<b>DATE:"
										+ c.getString(c
												.getColumnIndex(events_table.Date))
										+ "<br/>EVENT:<br/>"
										+ c.getString(c
												.getColumnIndex(events_table.Event))
										+ "<br/>SAVED WORDS:<br/>" + save_words
										+ "<br />" + save_mnes + "<br/></b>"));
								words_layout.addView(tv);
							} while (c.moveToNext());
						}
						c.close();
						c = MainLfqActivity.getMiscDb().rawQuery(
								"SELECT * FROM " + table + " WHERE "
										+ events_table.Number1 + "<>'' AND "
										+ events_table.Year
										+ " NOT LIKE '% BC' ORDER BY "
										+ events_table.Year + " ASC", null);

						if (c.moveToFirst()) {
							do {
								TextView tv = new TextView(this_act);
								tv.setTextSize(15);
								save_words = "";
								save_mnes = "";
								if (!c.getString(
										c.getColumnIndex(events_table.Number1))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number1))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic1));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info1))
											+ ")";
								}
								if (!c.getString(
										c.getColumnIndex(events_table.Number2))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number2))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic2));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info2))
											+ ")";
								}
								if (!c.getString(
										c.getColumnIndex(events_table.Number3))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number3))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic3));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info3))
											+ ")";
								}
								if (!c.getString(
										c.getColumnIndex(events_table.Number4))
										.equals("")) {
									save_words += c.getString(c
											.getColumnIndex(events_table.Number4))
											+ " ";
									save_mnes += c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic4));
									save_mnes += "("
											+ c.getString(c
													.getColumnIndex(events_table.Number_Info4))
											+ ")";
								}
								tv.setText(Html.fromHtml("<b>DATE:"
										+ c.getString(c
												.getColumnIndex(events_table.Date))
										+ "<br/>EVENT:<br/>"
										+ c.getString(c
												.getColumnIndex(events_table.Event))
										+ "<br/>SAVED WORDS:<br/>" + save_words
										+ "<br />" + save_mnes + "<br/></b>"));
								words_layout.addView(tv);
							} while (c.moveToNext());
						}
						c.close();
						results.setText(ct_tot + " OF " + ct_all + " EVENTS");
						results2.setText("");
					}
				});

		do_timeline_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				display_savedwords = true;
				if (number <= 1) {
					return;
				}
				event_layout.removeAllViews();
				event_tv = new TextView(this_act);
				event_layout.addView(event_tv);
				is_event_edit = false;
				if (which_events.equals("user_personal")
						|| which_events.equals("user_historical")) {
					c_get_last = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM " + table + " WHERE "
									+ user_events.Date + "='" + date + "' AND "
									+ user_events.Year + "='" + year + "' AND "
									+ user_events.Type + "='" + type_clause
									+ "' AND " + user_events._id + "<'" + id
									+ "' ORDER BY " + user_events._id
									+ " DESC LIMIT 1", null);
					if (c_get_last.getCount() == 0) {
						c_get_last.close();
						c_get_last = MainLfqActivity.getMiscDb().rawQuery(
								"SELECT * FROM " + table + " WHERE "
										+ user_events.Date + "<'" + date
										+ "' AND " + user_events.Year + "='"
										+ year + "' AND " + user_events.Type
										+ "='" + type_clause + "' ORDER BY "
										+ user_events.Date + " DESC,"
										+ user_events._id + " DESC LIMIT 1",
								null);
						if (c_get_last.getCount() == 0) {
							if (year.contains("BC")) {// IS BC YEAR:
								c_get_last.close();
								c_get_last = MainLfqActivity
										.getMiscDb()
										.rawQuery(
												"SELECT * FROM " + table
														+ " WHERE "
														+ user_events.Year
														+ " LIKE '% BC' AND "
														+ user_events.Year
														+ ">'" + year
														+ "' AND "
														+ user_events.Type
														+ "='" + type_clause
														+ "' ORDER BY "
														+ user_events.Year
														+ " ASC,"
														+ user_events._id
														+ " DESC LIMIT 1", null);
							} else {// FOR AD Year:
								c_get_last.close();
								c_get_last = MainLfqActivity
										.getMiscDb()
										.rawQuery(
												"SELECT * FROM "
														+ table
														+ " WHERE "
														+ user_events.Year
														+ " NOT LIKE '% BC' AND "
														+ user_events.Year
														+ "<'" + year
														+ "' AND "
														+ user_events.Type
														+ "='" + type_clause
														+ "' ORDER BY "
														+ user_events.Year
														+ " DESC,"
														+ user_events._id
														+ " DESC LIMIT 1", null);
								if (c_get_last.getCount() == 0) {// YEAR IS
																	// EARLIEST
																	// AD, SO
																	// GET
																	// BC:
									c_get_last.close();
									c_get_last = MainLfqActivity
											.getMiscDb()
											.rawQuery(
													"SELECT * FROM "
															+ table
															+ " WHERE "
															+ user_events.Year
															+ " LIKE '% BC' AND "
															+ user_events.Type
															+ "='"
															+ type_clause
															+ "' ORDER BY "
															+ user_events.Year
															+ " ASC,"
															+ user_events._id
															+ " DESC LIMIT 1",
													null);

								}
							}
						}
					}
				}
				if (which_events.equals("year_events")) {
					year = select_years.getSelectedItem().toString();
					if (year.length() == 0) {
						results.setText(Html
								.fromHtml("<b>MUST ENTER A YEAR</b>"));
						return;
					}
					c_get_last = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM " + table + " WHERE "
									+ events_table.Date + "='" + date
									+ "' AND " + events_table.Year + "='"
									+ year + "' AND " + events_table._id + "<'"
									+ id + "' ORDER BY " + events_table._id
									+ " DESC LIMIT 1", null);

					if (c_get_last.getCount() == 0) {
						c_get_last.close();
						c_get_last = MainLfqActivity
								.getMiscDb()
								.rawQuery(
										"SELECT * FROM "
												+ table
												+ " WHERE "
												+ events_table.Date
												+ "<'"
												+ date
												+ "' AND "
												+ events_table.Year
												+ "='"
												+ year
												+ "' ORDER BY Date DESC,_id DESC LIMIT 1",
										null);
					}
					display_date = months_complete.get(Integer
							.parseInt(c_get_last.getString(
									c_get_last.getColumnIndex("Date"))
									.substring(0, 2)) - 1)
							+ " "
							+ Integer.parseInt(c_get_last.getString(
									c_get_last.getColumnIndex("Date"))
									.substring(3));

				}
				if (which_events.equals("date_events")) {
					date_number_string = String.valueOf(select_timeline_month
							.getSelectedItemPosition() + 1)
							+ select_timeline_day.getSelectedItem().toString();
					c_get_last = MainLfqActivity.getMiscDb()
							.rawQuery(
									"SELECT * FROM " + table + " WHERE Date='"
											+ date + "' AND Year='" + year
											+ "' AND _id<" + id, null);
					if (c_get_last.getCount() == 0) {
						if (year.contains("BC")) {// IS BC YEAR:
							c_get_last.close();
							c_get_last = MainLfqActivity
									.getMiscDb()
									.rawQuery(
											"SELECT * FROM "
													+ table
													+ " WHERE Date='"
													+ date
													+ "' AND Year LIKE '% BC' AND Year>'"
													+ year
													+ "' ORDER BY Year ASC,_id DESC LIMIT 1",
											null);
						} else {// FOR AD Year:
							c_get_last.close();
							c_get_last = MainLfqActivity
									.getMiscDb()
									.rawQuery(
											"SELECT * FROM "
													+ table
													+ " WHERE Date='"
													+ date
													+ "' AND Year NOT LIKE '% BC' AND Year<'"
													+ year
													+ "' ORDER BY Year DESC,_id DESC LIMIT 1",
											null);
							if (c_get_last.getCount() == 0) {// YEAR IS EARLIEST
																// AD, SO GET
																// BC:
								c_get_last.close();
								c_get_last = MainLfqActivity
										.getMiscDb()
										.rawQuery(
												"SELECT * FROM "
														+ table
														+ " WHERE Date='"
														+ date
														+ "' AND Year LIKE '% BC' ORDER BY Year ASC,_id DESC LIMIT 1",
												null);

							}
						}
					}
				}
				if (c_get_last.moveToFirst()) {
					event_tv.setText(c_get_last.getString(c_get_last
							.getColumnIndex("Event")));
					id = c_get_last.getString(c_get_last.getColumnIndex("_id"));
					saved_words = getSavedWords(
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number1)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Info1)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Mnemonic1)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number2)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Info2)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Mnemonic2)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number3)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Info3)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Mnemonic3)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number4)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Info4)),
							c_get_last.getString(c_get_last
									.getColumnIndex(events_table.Number_Mnemonic4)));

					date = c_get_last.getString(c_get_last
							.getColumnIndex("Date"));
					date_number_string = String.valueOf(Integer
							.parseInt(c_get_last.getString(
									c_get_last.getColumnIndex("Date"))
									.substring(0, 2)))
							+ String.valueOf(Integer.parseInt(c_get_last
									.getString(
											c_get_last.getColumnIndex("Date"))
									.substring(3)));
					year = c_get_last.getString(c_get_last
							.getColumnIndex("Year"));
					getMajorWords(year, c_get_last.getString(c_get_last
							.getColumnIndex("Date")), date_number_string,
							saved_words);
					number--;
				}
				show_year.setText(Html.fromHtml("<b>YEAR:" + year + "</b>"));
				show_date.setText(Html.fromHtml("<b>DATE:" + date + "</b>"));
				c_get_last.close();
				results2.setText(Html.fromHtml("<b>" + number + " OF " + total
						+ " EVENTS.</b>"));
			}
		});

		do_timeline_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				display_savedwords = true;
				if (number >= total) {
					return;
				}
				event_layout.removeAllViews();
				event_tv = new TextView(this_act);
				event_layout.addView(event_tv);
				is_event_edit = false;
				if (which_events.equals("user_personal")
						|| which_events.equals("user_historical")) {
					c_get_next = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM " + table + " WHERE "
									+ user_events.Username + "='" + username
									+ ", " + user_events.Date + "='" + date
									+ "' AND " + user_events.Year + "='" + year
									+ "' AND " + user_events.Type + "='"
									+ type_clause + "' AND " + user_events._id
									+ ">'" + id + "' ORDER BY "
									+ user_events._id + " ASC LIMIT 1", null);
					if (c_get_next.getCount() == 0) {
						c_get_next.close();
						c_get_next = MainLfqActivity.getMiscDb().rawQuery(
								"SELECT * FROM " + table + " WHERE "
										+ user_events.Username + "='"
										+ username + "' " + user_events.Date
										+ ">'" + date + "' AND "
										+ user_events.Year + "='" + year
										+ "' AND " + user_events.Type + "='"
										+ type_clause + "' ORDER BY "
										+ user_events.Date + " ASC, "
										+ user_events._id + " ASC LIMIT 1",
								null);
						if (c_get_next.getCount() == 0) {
							if (year.contains("BC")) {// IS BC YEAR:
								c_get_next.close();
								c_get_next = MainLfqActivity.getMiscDb()
										.rawQuery(
												"SELECT * FROM " + table
														+ " WHERE "
														+ user_events.Username
														+ "='" + username
														+ "' AND "
														+ user_events.Year
														+ " LIKE '% BC' AND "
														+ user_events.Year
														+ "<'" + year
														+ "' AND "
														+ user_events.Type
														+ "='" + type_clause
														+ "' ORDER BY "
														+ user_events.Year
														+ " DESC, "
														+ user_events._id
														+ " ASC LIMIT 1", null);
								if (c_get_next.getCount() == 0) {// NO MORE BC,
																	// SO
									// GET FIRST AD:
									c_get_next.close();
									c_get_next = MainLfqActivity
											.getMiscDb()
											.rawQuery(
													"SELECT * FROM "
															+ table
															+ " WHERE "
															+ user_events.Username
															+ "='"
															+ username
															+ "' AND "
															+ user_events.Year
															+ " NOT LIKE '% BC' AND "
															+ user_events.Type
															+ "='"
															+ type_clause
															+ "' ORDER BY "
															+ user_events.Year
															+ ", "
															+ user_events._id
															+ " LIMIT 1", null);
								}

							} else {// FOR AD Year:
								c_get_next.close();
								c_get_next = MainLfqActivity
										.getMiscDb()
										.rawQuery(
												"SELECT * FROM "
														+ table
														+ " WHERE "
														+ user_events.Username
														+ "='"
														+ username
														+ "' AND "
														+ user_events.Year
														+ " NOT LIKE '% BC' AND "
														+ user_events.Year
														+ ">'" + year
														+ "' AND "
														+ user_events.Type
														+ "='" + type_clause
														+ "' ORDER BY "
														+ user_events.Year
														+ " ASC, "
														+ user_events._id
														+ " ASC LIMIT 1", null);
							}
						}
					}
				}
				if (which_events.equals("year_events")) {
					year = select_years.getSelectedItem().toString();
					if (year.length() == 0) {
						results.setText(Html
								.fromHtml("<b>MUST ENTER A YEAR</b>"));
						return;
					}
					c_get_next = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM " + table + " WHERE Date='" + date
									+ "' AND Year='" + year + "' AND _id>'"
									+ id + "' ORDER BY _id DESC LIMIT 1", null);

					if (c_get_next.getCount() == 0) {
						c_get_next.close();
						c_get_next = MainLfqActivity
								.getMiscDb()
								.rawQuery(
										"SELECT * FROM "
												+ table
												+ " WHERE Date>'"
												+ date
												+ "' AND Year='"
												+ year
												+ "' ORDER BY Date ASC,_id ASC LIMIT 1",
										null);
					}
					display_date = months_complete.get(Integer
							.parseInt(c_get_next.getString(
									c_get_next.getColumnIndex("Date"))
									.substring(0, 2)) - 1)
							+ " "
							+ Integer.parseInt(c_get_next.getString(
									c_get_next.getColumnIndex("Date"))
									.substring(3));

				}
				if (which_events.equals("date_events")) {
					c_get_next = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM " + table + " WHERE "
									+ events_table.Date + "='" + date
									+ "' AND " + events_table.Year + "='"
									+ year + "' AND " + events_table._id + ">'"
									+ id + "'", null);
					if (c_get_next.getCount() == 0) {
						if (year.contains("BC")) {
							c_get_next.close();
							c_get_next = MainLfqActivity.getMiscDb().rawQuery(
									"SELECT * FROM " + table + " WHERE "
											+ events_table.Date + "='" + date
											+ "' AND " + events_table.Year
											+ " LIKE '% BC' AND "
											+ events_table.Year + "<'" + year
											+ "' ORDER BY " + events_table.Year
											+ " DESC," + events_table._id
											+ " ASC LIMIT 1", null);
							if (c_get_next.getCount() == 0) {// NO MORE BC, SO
																// GET FIRST AD:
								c_get_next.close();
								c_get_next = MainLfqActivity
										.getMiscDb()
										.rawQuery(
												"SELECT * FROM "
														+ table
														+ " WHERE "
														+ events_table.Date
														+ "='"
														+ date
														+ "' AND "
														+ events_table.Year
														+ " NOT LIKE '% BC' ORDER BY "
														+ events_table.Year
														+ ","
														+ events_table._id
														+ " LIMIT 1", null);
							}
						} else {// FOR AD Year:
							c_get_next.close();
							c_get_next = MainLfqActivity.getMiscDb().rawQuery(
									"SELECT * FROM " + table + " WHERE "
											+ events_table.Date + "='" + date
											+ "' AND " + events_table.Year
											+ " NOT LIKE '% BC' AND "
											+ events_table.Year + " >'" + year
											+ "' ORDER BY " + events_table.Year
											+ "," + events_table._id
											+ " LIMIT 1", null);
						}
					}
				}
				if (c_get_next.moveToFirst()) {
					event_tv.setText(c_get_next.getString(c_get_next
							.getColumnIndex("Event")));
					id = c_get_next.getString(c_get_next.getColumnIndex("_id"));

					saved_words = getSavedWords(
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number1)),
							c.getString(c_get_next
									.getColumnIndex(events_table.Number_Info1)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number_Mnemonic1)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number2)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number_Info2)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number_Mnemonic2)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number3)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number_Info3)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number_Mnemonic3)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number4)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number_Info4)),
							c_get_next.getString(c_get_next
									.getColumnIndex(events_table.Number_Mnemonic4)));
					date = c_get_next.getString(c_get_next
							.getColumnIndex("Date"));
					date_number_string = String.valueOf(Integer
							.parseInt(c_get_next.getString(
									c_get_next.getColumnIndex("Date"))
									.substring(0, 2)))
							+ String.valueOf(Integer.parseInt(c_get_next
									.getString(
											c_get_next.getColumnIndex("Date"))
									.substring(3)));
					year = c_get_next.getString(c_get_next
							.getColumnIndex("Year"));
					getMajorWords(year, c_get_next.getString(c_get_next
							.getColumnIndex("Date")), date_number_string,
							saved_words);
					number++;
				}
				show_year.setText(Html.fromHtml("<b>YEAR:" + year + "</b>"));
				show_date.setText(Html.fromHtml("<b>DATE:" + date + "</b>"));
				c_get_next.close();
				results2.setText(Html.fromHtml("<b>" + number + " OF " + total
						+ " EVENTS.</b>"));

			}
		});

		do_edit_timeline_event.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (is_event_edit == false) {
					String event = event_tv.getText().toString();
					event_layout.removeAllViews();
					event_edit_et = new EditText(this_act);
					event_edit_et
							.setBackgroundResource(R.drawable.rounded_edittext_red);
					event_edit_et.setMaxLines(5);
					event_edit_et.setText(event);
					event_layout.addView(event_edit_et);
					is_event_edit = true;
				} else {
					String event = event_edit_et.getText().toString();
					event_layout.removeAllViews();
					event_tv = new TextView(this_act);
					event_tv.setText(event);
					ContentValues values = new ContentValues();
					values.put("Event", event);
					MainLfqActivity.getMiscDb().update(
							tables.events_table,
							values,
							events_table.Year + "=?, " + events_table.Date
									+ "=?", new String[] { year, date });
					results.setText(Html.fromHtml("<b>EVENT " + year + " "
							+ date + " UPDATED."));
					// SYNCHRONIZE EVENT IN EVENT DATABASE:
					sql = "UPDATE " + lfq_table + " SET Event='" + event
							+ "' WHERE " + events_table.Year + "='" + year
							+ "' AND " + events_table.Date + "='" + date
							+ "' AND ID='" + id + "'";

					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[]
					// image)
					Synchronize.autoSync(sql, database_string, "update",
							lfq_table, String.valueOf(id), false, null);
					event_layout.addView(event_tv);
					is_event_edit = false;
				}
			}
		});

		do_save_words.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int rb_click_count = 0;
				for (int i = 0; i < own_word_ind; i++) {
					if (radio_groups.get(i).getCheckedRadioButtonId() != -1) {
						rb_click_count++;
					}
				}
				if (rb_click_count != own_word_ind) {
					results.setText(Html
							.fromHtml("<b><u>PLEASE SELECT WORDS!</u></b>"));
					return;
				}
				ContentValues values_events = new ContentValues();
				ContentValues values_global = new ContentValues();
				ContentValues values_personal = new ContentValues();
				String sync_upd_events_str = "";
				int entry_number_global = 0, entry_number_personal = 0;
				int entry_index = 0;// FOR BOTH global and personal.
				if (check_save_timeline_global.isChecked()
						|| check_save_timeline_personal.isChecked()) {
					if (is_event_edit == false) {
						event = event_tv.getText().toString();
					} else {
						event = event_edit_et.getText().toString();
					}
					if (check_save_timeline_global.isChecked()) {
						// GET Entry Number:
						Cursor c_max = MainLfqActivity.getMiscDb().rawQuery(
								"SELECT MAX(" + global_numbers.Entry_Number
										+ ") AS MAX FROM "
										+ tables.global_numbers, null);
						if (c_max.moveToFirst()) {
							entry_number_global = c_max.getInt(0);
						}
					}
					if (check_save_timeline_personal.isChecked()) {
						// GET Entry Number:
						Cursor c_max = MainLfqActivity.getMiscDb().rawQuery(
								"SELECT MAX(" + user_numbers.Entry_Number
										+ ") AS MAX FROM "
										+ tables.user_numbers, null);
						if (c_max.moveToFirst()) {
							entry_number_personal = c_max.getInt(0);
						}
					}
				}
				String save_results = "";
				for (int i = 0; i < edit_save_words.size(); i++) {
					// DO EVENTS:
					values_events.put("Number" + (i + 1), edit_save_words
							.get(i).get(0));
					values_events.put("Number" + (i + 1), edit_save_words
							.get(i).get(1));
					values_events.put("Number_Info" + (i + 1), edit_save_words
							.get(i).get(2));
					sync_upd_events_str += "Number" + (i + 1) + "='"
							+ edit_save_words.get(i).get(0) + "', "
							+ "Number_Mnemonic" + (i + 1) + "='"
							+ edit_save_words.get(i).get(1) + "', "
							+ "Number_Info" + (i + 1) + "='"
							+ edit_save_words.get(i).get(2) + "'";
					if (check_save_timeline_personal.isChecked()) {
						entry_index++;
						values_personal.clear();
						values_personal.put(user_numbers.Title, event);
						values_personal.put(global_numbers.Entry_Number,
								entry_number_personal);
						values_personal.put(user_numbers.Entry_Index,
								entry_index);
						values_personal.put(global_numbers.Entry,
								edit_save_words.get(i).get(0));
						values_personal.put(user_numbers.Entry_Mnemonic,
								edit_save_words.get(i).get(1));
						values_personal.put(user_numbers.Entry_Info,
								edit_save_words.get(i).get(2));
						MainLfqActivity.getMiscDb().insert(tables.user_numbers, null,
								values_personal);
						save_results += " INSERTED INTO " + username
								+ "'S HISTORICAL TABLE.";
						// SYNCHRONIZE NUMBER PERSONAL HISTORICAL TABLE
						sql = "INSERT INTO " + Helpers.db_prefix + "_misc."
								+ tables.user_numbers + "(" + user_numbers.Title
								+ "," + user_numbers.Entry + ","
								+ user_numbers.Entry_Number + ","
								+ user_numbers.Entry_Index + ","
								+ user_numbers.Entry_Mnemonic + ","
								+ user_numbers.Entry_Info + "," + user_numbers.Type
								+ ") VALUES('" + event + "','"
								+ edit_save_words.get(i).get(0) + "','"
								+ entry_number_personal + "','" + entry_index + "','"
								+ edit_save_words.get(i).get(1) + "','"
								+ edit_save_words.get(i).get(2)
								+ "','PERSONAL NUMBERS')";
						save_results += Synchronize.autoSync(sql, "misc_db",
								"insert", tables.user_numbers, edit_date_number, false,
								null);						
					}
					if (check_save_timeline_global.isChecked()) {
						// UPDATE NUMBER GLOBAL TABLE--------------------------------
						entry_index++;
						values_global.clear();
						values_global.put(global_numbers.Title, event);
						values_global.put(global_numbers.Entry_Number,
								edit_date_number);
						values_global.put(global_numbers.Entry_Index,
								entry_index);
						values_global.put(global_numbers.Entry, edit_save_words
								.get(i).get(0));
						values_global.put(global_numbers.Entry_Mnemonic,
								edit_save_words.get(i).get(1));
						values_global.put(global_numbers.Entry_Mnemonic_Info,
								edit_save_words.get(i).get(2));
						MainLfqActivity.getMiscDb().insert(tables.global_numbers,
								null, values_global);
						save_results += " INSERTED INTO GLOBAL NUMBER TABLE.";
						sql = "INSERT INTO " + Helpers.db_prefix + "misc."
								+ tables.global_numbers + "("
								+ global_numbers.Title + ","
								+ global_numbers.Entry_Number + ","
								+ global_numbers.Entry_Index + ","
								+ global_numbers.Entry + ","
								+ global_numbers.Entry_Mnemonic + ","
								+ global_numbers.Entry_Mnemonic_Info + ") VALUES('"
								+ event + "','" + entry_number_global + "','"
								+ entry_index + "','"
								+ edit_save_words.get(i).get(0) + "','"
								+ edit_save_words.get(i).get(1) + "','"
								+ edit_save_words.get(i).get(2) + "')";
						save_results += Synchronize.autoSync(sql, "misc_db",
								"insert", tables.global_numbers, edit_date_number,
								false, null);						
						//-------------------------------------------------------------
					}
				}//END edit_save_words LOOP...
				// UPDATE events_table-----------------------------------------------
				MainLfqActivity.getMiscDb().update(
						tables.events_table,
						values_events,
						events_table.Year + "=?, " + events_table.Date
								+ "=?", new String[] { year, date });
				save_results += "UPDATED SAVED WORDS ON "
						+ display_date + ", " + year + ".";
				sql = "UPDATE " + lfq_table + " SET " + sync_upd_events_str
						+ " WHERE " + events_table.Year + "='" + year
						+ "' AND " + events_table.Date + "='" + date + "'";
				Synchronize.autoSync(sql, database_string, "update", table,
						date, false, null);
				//-----------------------------------------------------------------
				results.setText(Html.fromHtml("<b>" + save_results + "</b>."));
			}
		});

		get_user_personal_events.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setTable();
				which_events = "user_personal";
				getEvents("user_personal_events");
			}
		});
		get_user_historical_events
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						setTable();
						which_events = "user_historical";
						getEvents("user_historical_events");
					}
				});

	}

	public void getMajorWords(String myYear, String myDate,
			String myDateNumber, String my_saved_words) {

		if (!my_saved_words.equals("")) {
			savewords_layout.setVisibility(View.GONE);
			params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 550);
		}
		if (my_saved_words.equals("") || display_savedwords == false) {
			savewords_layout.setVisibility(View.VISIBLE);
			params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 500);
		}
		params.addRule(RelativeLayout.BELOW, savewords_layout.getId());
		timeline_words_scroll.setLayoutParams(params);
		above_controls.setVisibility(View.GONE);
		do_timeline_above.setVisibility(View.VISIBLE);
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 100);
		params.addRule(RelativeLayout.BELOW, prompt_events_layout.getId());
		timeline_event_scroll.setLayoutParams(params);
		own_word_ind = 0;
		input_words.clear();
		//LISTS TO SAVE DICTIONARY WORDS:
		list_saved_numbers.clear();
		list_saved_words.clear();
		list_saved_infos.clear();
		//INPUTS TO SAVE SELECTIONS:
		input_saved_numbers.clear();
		input_saved_words.clear();
		input_saved_infos.clear();
		radio_groups.clear();
		edit_save_words.clear();
		String date_num = myDateNumber;
		String num_wors_str = select_timeline_number_major_words
				.getSelectedItem().toString();

		if (display_savedwords == false) {
			num_wors_str = select_number_major_words_again.getSelectedItem()
					.toString();
		}

		int num_wors = 0;
		int num_wors2 = 0;
		int num_wors3 = 0;

		if (!num_wors_str.equals("ALL")) {
			num_wors = Integer.parseInt(num_wors_str);
		}

		if (num_wors != 1 && !num_wors_str.equals("ALL")) {
			num_wors2 = num_wors / 2;
			num_wors3 = num_wors / 3;
		}

		if (num_wors == 1) {
			num_wors2 = 1;
			num_wors3 = 1;
		}

		if (num_wors_str.equals("ALL")) {
			num_wors = 100;
			num_wors2 = 50;
			num_wors3 = 33;
		}

		out = "";
		results.setText(out);
		words_layout.removeAllViews();

		myYear = myYear.replace(" BC", "");
		String original_year = myYear;
		String[] seanumyeaspl = Helpers.explode(myYear);
		String seanumyea = original_year;
		if (seanumyeaspl[0].equals("0") && !seanumyeaspl[1].equals("0")) {
			seanumyea = seanumyea.substring(1);
		}
		if (seanumyeaspl[0].equals("0") && seanumyeaspl[1].equals("0")
				&& !seanumyeaspl[2].equals("0")) {
			seanumyea = seanumyea.substring(2);
		}
		if (seanumyeaspl[0].equals("0") && seanumyeaspl[1].equals("0")
				&& seanumyeaspl[2].equals("0")) {
			seanumyea = seanumyea.substring(3);
		}
		edit_date_number = seanumyea + date_num;

		if (display_savedwords == false || my_saved_words.equals("")) {
			// BEGIN IF NOT CONDENSED(DATE AND YEAR IS
			// SEPARATE...
			// ******************************************************************************************
			Boolean found4 = false;
			if (!check_condensed_words.isChecked()) {
				// ADD RADIOGROUPS FOR YEAR OF EVENT:
				// ************************************************

				RadioGroup rgy_all = new RadioGroup(this_act);
				found4 = addRadioGroup(rgy_all, "YEAR(" + seanumyea + "):",
						seanumyea, num_wors);

				if (found4 == false) {
					String yeabeg2 = original_year.substring(0, 2);
					String yealas2 = original_year.substring(2, 4);
					RadioGroup rgya = new RadioGroup(this_act);
					addRadioGroup(rgya, "Year(" + yeabeg2 + "):", yeabeg2,
							num_wors2);
					RadioGroup rgyb = new RadioGroup(this_act);
					addRadioGroup(rgyb, "Year(" + yealas2 + "):", yealas2,
							num_wors2);
				}
				// ADD RADIOGROUPS FOR DATE OF EVENT:
				// ************************************************

				RadioGroup rg = new RadioGroup(this_act);
				found4 = addRadioGroup(rg, "DATE(" + date_num + "):", date_num,
						num_wors);
				if (found4 == false) {
					if (date_num.length() == 3) {
						date_num += "0";
					} else if (date_num.length() == 2) {
						date_num += "00";
					}
					String seldatnumbeg2 = date_num.substring(0, 2);
					RadioGroup rga = new RadioGroup(this_act);
					addRadioGroup(rga, "DATE(" + seldatnumbeg2 + "):",
							seldatnumbeg2, num_wors2);
					// *****************************************************
					String seldatnumlas2 = date_num.substring(2, 4);
					addRadioGroup(rga, "DATE(" + seldatnumlas2 + "):",
							seldatnumlas2, num_wors2);
				}

			}// END IF NOT CONDENSED(DATE AND YEAR IS SEPERATE
				// BEGIN IF CONDENSED(DATE AND YEAR IS SEPERATE
				// ****************************************************************************
			if (check_condensed_words.isChecked()) {
				boolean foundall = false;
				boolean foundyear = false;
				boolean founddate = false;
				String compnumb = seanumyea + date_num;
				int comnumlen = compnumb.length();
				int yearlen = seanumyea.length();
				int datelen = date_num.length();
				RadioGroup rgall = new RadioGroup(this_act);
				foundall = addRadioGroup(rgall, "COMPLETE DATE(" + compnumb
						+ "):", compnumb, num_wors);
				if (foundall == false) {
					RadioGroup rgyea = new RadioGroup(this_act);
					foundyear = addRadioGroup(rgyea,
							"YEAR(" + seanumyea + "):", seanumyea, num_wors2);
					if (foundyear == true) {
						RadioGroup rgdat = new RadioGroup(this_act);
						founddate = addRadioGroup(rgdat, "DATE(" + date_num
								+ "):", date_num, num_wors);
					}
					if (founddate == false || foundyear == false) {
						if (comnumlen == 5 || comnumlen == 6) {
							String begnum = compnumb.substring(0, 3);
							String endnum = compnumb.substring(3);
							RadioGroup rg56_beg = new RadioGroup(this_act);
							addRadioGroup(rg56_beg, "COMBO(" + begnum + " of "
									+ compnumb + "):", begnum, num_wors2);
							RadioGroup rg56_end = new RadioGroup(this_act);
							addRadioGroup(rg56_end, "COMBO(" + endnum + " of "
									+ compnumb + "):", endnum, num_wors2);
						}
						if (comnumlen == 7) {
							if (datelen == 4 && founddate == false) {
								begnum = compnumb.substring(0, 4);
								endnum = compnumb.substring(4);
							}
							if (yearlen == 4 && foundyear == false) {
								begnum = compnumb.substring(0, 3);
								endnum = compnumb.substring(3);
							}
							Boolean foundbeg = false;
							Boolean foundend = false;
							RadioGroup rg7_beg = new RadioGroup(this_act);
							foundbeg = addRadioGroup(rg7_beg, "COMBO(" + begnum
									+ " of " + compnumb + "):", begnum,
									num_wors2);
							RadioGroup rg7_end = new RadioGroup(this_act);
							foundend = addRadioGroup(rg7_end, "COMBO(" + endnum
									+ " of " + compnumb + "):", endnum,
									num_wors2);
							if (foundbeg == false || foundend == false) {
								begnum = compnumb.substring(0, 3);
								midnum = compnumb.substring(3, 5);
								endnum = compnumb.substring(5, 7);
								RadioGroup rg7_beg2 = new RadioGroup(this_act);
								addRadioGroup(rg7_beg2, "COMBO(" + begnum
										+ " of " + compnumb + "):", begnum,
										num_wors3);
								RadioGroup rg7_mid = new RadioGroup(this_act);
								addRadioGroup(rg7_mid, "COMBO(" + midnum
										+ " of " + compnumb + "):", midnum,
										num_wors3);
								RadioGroup rg7_end2 = new RadioGroup(this_act);
								addRadioGroup(rg7_end2, "COMBO(" + endnum
										+ " of " + compnumb + "):", endnum,
										num_wors3);
							}
						}
						if (comnumlen == 8) {
							Boolean found8_beg_half = false;
							Boolean found8_end_half = false;
							if (foundyear == false) {
								begnum = compnumb.substring(0, 3);
								endnum = compnumb.substring(3);
							}
							if (founddate == false) {
								begnum = compnumb.substring(0, 5);
								endnum = compnumb.substring(5);
							}
							RadioGroup rg8_beg_half = new RadioGroup(this_act);
							found8_beg_half = addRadioGroup(rg8_beg_half,
									"COMBO(" + begnum + " of " + compnumb
											+ "):", begnum, num_wors2);
							if (found8_beg_half == true) {
								RadioGroup rg8_end_half = new RadioGroup(
										this_act);
								found8_end_half = addRadioGroup(rg8_end_half,
										"COMBO(" + endnum + " of " + compnumb
												+ "):", endnum, num_wors2);

							}
							if (!found8_beg_half || !found8_end_half) {
								begnum = compnumb.substring(0, 3);
								midnum = compnumb.substring(3, 6);
								endnum = compnumb.substring(6, 8);
								RadioGroup rg8_beg = new RadioGroup(this_act);
								addRadioGroup(rg8_beg, "COMBO(" + begnum
										+ " of " + compnumb + "):", begnum,
										num_wors3);
								RadioGroup rg8_mid = new RadioGroup(this_act);
								addRadioGroup(rg8_mid, "COMBO(" + midnum
										+ " of " + compnumb + "):", midnum,
										num_wors3);
								RadioGroup rg8_end = new RadioGroup(this_act);
								addRadioGroup(rg8_end, "COMBO(" + endnum
										+ " of " + compnumb + "):", endnum,
										num_wors3);
							}
						}// ENDS IF COMPLETE DATE IS 8
					}// END IF founddate or foundyear is false
				}// ENDS IF foundall is false
			}// ENDS IF check_condensed_words is checked
		}// ENDS IF SaveWords=="" is true

		display_savedwords = false;
		TextView prompt_saved_words = new TextView(this_act);
		prompt_saved_words.setText("SAVED WORDS:");
		words_layout.addView(prompt_saved_words);
		TextView saved_words_tv = new TextView(this_act);
		saved_words_tv.setText(Html.fromHtml("<b><u>" + my_saved_words
				+ "</u></b>"));
		words_layout.addView(saved_words_tv);

		LinearLayout do_again_layout = new LinearLayout(this_act);
		do_again_layout.setOrientation(LinearLayout.HORIZONTAL);
		Button do_words_again = new Button(this_act);
		do_words_again.setText("Choose Words Again?");
		final String final_myYear = myYear;
		final String final_myDate = myDate;
		final String final_myDateNumber = myDateNumber;
		final String final_saved_words = my_saved_words;
		do_words_again.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savewords_layout.setVisibility(View.VISIBLE);
				getMajorWords(final_myYear, final_myDate, final_myDateNumber,
						final_saved_words);
			}
		});
		do_again_layout.addView(do_words_again);
		do_words_again.setTextSize(11);
		TextView prompt_again_number = new TextView(this_act);
		prompt_again_number.setText("  How Many?");
		do_again_layout.addView(prompt_again_number);
		select_number_major_words_again = new Spinner(this_act);
		ArrayAdapter<CharSequence> numberMajorWords = ArrayAdapter
				.createFromResource(this_act, R.array.number_timeline_words,
						android.R.layout.simple_spinner_item);
		numberMajorWords
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_number_major_words_again.setAdapter(numberMajorWords);
		do_again_layout.addView(select_number_major_words_again);
		words_layout.addView(do_again_layout);

	}

	public String formatWord(String[] worspl, int mark_max) {
		int worct = worspl.length;
		int marletct = 0;
		String wriwor = "";
		// ignlets=aeiouwxy

		marletct = 0;
		for (int i = 0; i < worct; i++) {
			if (ign_lets.contains(worspl[i])
					|| (marletct == mark_max && !worspl[i].equals("h"))) {
				wriwor += worspl[i].toLowerCase(Locale.US);
			}
			if (marletct < mark_max
					|| (marletct == mark_max && (worspl[i].toLowerCase(
							Locale.US).equals("h") || worspl[i].toLowerCase(
							Locale.US).equals("g")))) {
				if (!ign_lets.contains(worspl[i])
						&& !worspl[i].toLowerCase(Locale.US).equals("h")) {
					wriwor += worspl[i].toUpperCase(Locale.US);
					marletct++;
				}
				if (worspl[i].toLowerCase(Locale.US).equals("h") && i == 0) {
					wriwor += worspl[i].toLowerCase(Locale.US);
				}
				if (i > 0) {
					if (worspl[i].equals("g") && worspl[i - 1].equals("g")) {
						marletct--;
					}
					if (worspl[i].equals("h")
							&& dbl_lets.contains(worspl[i - 1]) != false) {
						wriwor += worspl[i].toUpperCase(Locale.US);
					}
					if (worspl[i].equals("h")
							&& dbl_lets.contains(worspl[i - 1]) == false) {
						wriwor += worspl[i].toLowerCase(Locale.US);
					}
				}
			}// end if marletct<numct
		}// end for loop format letters
		return wriwor;
	}

	// @FOR PARSING DATABASE SAVED WORDS INTO A STRING:
	public String getSavedWords(String num1, String inf1, String mne1,
			String num2, String inf2, String mne2, String num3, String inf3,
			String mne3, String num4, String inf4, String mne4) {
		String saved_words = "";
		if (!num1.equals("")) {
			saved_words += num1;
			if (!inf1.equals("")) {
				saved_words += "(" + inf1 + ") ";
			}
			saved_words += mne1 + ". ";
		}
		if (!num2.equals("")) {
			saved_words += num1;
			if (!inf2.equals("")) {
				saved_words += "(" + inf2 + ") ";
			}
			saved_words += mne2 + ". ";
		}
		if (!num3.equals("")) {
			saved_words += num3;
			if (!inf3.equals("")) {
				saved_words += "(" + inf3 + ") ";
			}
			saved_words += mne3 + ". ";
		}
		if (!num4.equals("")) {
			saved_words += num4;
			if (!inf4.equals("")) {
				saved_words += "(" + inf4 + ") ";
			}
			saved_words += mne4 + ". ";
		}
		return saved_words;
	}

	public boolean addRadioGroup(RadioGroup rg, String prompt,
			String find_word, int limit) {
		Cursor myc = MainLfqActivity.getMiscDb()
				.rawQuery(
						"SELECT * FROM " + tables.dictionarya + " WHERE "
								+ dictionarya.Number + " LIKE '" + find_word
								+ "%' ORDER BY " + dictionarya.Word + " LIMIT "
								+ limit, null);
		boolean flag = false;
		final int final_find_word_length = find_word.length();
		if (myc.moveToFirst()) {

			TextView prompt_tv = new TextView(this_act);
			prompt_tv.setText(Html.fromHtml("<b>" + prompt + "</b>"));

			LinearLayout own_word_layout = new LinearLayout(this_act);
			own_word_layout.setOrientation(LinearLayout.HORIZONTAL);
			TextView prompt_own_word = new TextView(this_act);
			prompt_own_word.setText(Html.fromHtml("<b>Input Word: </b>"));
			EditText input_own_word = new EditText(this_act);
			input_own_word.setWidth(300);
			input_words.add(input_own_word);
			input_saved_numbers.add("");
			input_saved_words.add("");
			input_saved_infos.add("");
			input_own_word
					.setBackgroundResource(R.drawable.rounded_edittext_red);
			own_word_layout.addView(prompt_own_word);
			own_word_layout.addView(input_own_word);
			final int final_own_word_ind = own_word_ind;
			final String final_find_word = find_word;
			input_own_word.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable arg0) {
					input_saved_words.set(
							final_own_word_ind,
							formatWord(Helpers.explode(list_saved_words
									.get(final_own_word_ind)),
									final_find_word_length));
					input_saved_numbers
							.set(final_own_word_ind, final_find_word);
					input_saved_infos.set(final_own_word_ind,
							list_saved_infos.get(final_own_word_ind));
					edit_save_words.clear();
					edit_save_words_string = "";
					String edit_numbers = "";
					String edit_words_infos = "";
					for (int i = 0; i < own_word_ind; i++) {
						edit_save_words.add(new ArrayList<String>());
						edit_save_words.get(i).add(input_saved_numbers.get(i));
						edit_save_words.get(i).add(input_saved_words.get(i));
						edit_save_words.get(i).add(input_saved_infos.get(i));
						edit_numbers += input_saved_numbers.get(i) + " ";
						edit_words_infos +=  input_saved_words.get(i) + "(" + input_saved_infos.get(i) + ") ";
					}
					edit_save_words_string = edit_numbers + edit_words_infos;
					results.setText(Html.fromHtml("<b>SAVE WORDS:"
							+ edit_save_words_string + "</b>"));
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
				}

				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
				}

			});

			do {
				wriwor = formatWord(Helpers.explode(myc.getString(myc
						.getColumnIndex(dictionarya.Word))), find_word.length());
				RadioButton rb = new RadioButton(this_act);
				rb.setText(find_word
						+ " "
						+ wriwor
						+ " "
						+ myc.getString(myc
								.getColumnIndex(dictionarya.Definition)));
				rg.addView(rb);

				rb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						input_words.get(final_own_word_ind).setEnabled(false);
						input_words.get(final_own_word_ind)
								.setBackgroundResource(
										R.drawable.rounded_edittext);
						
						input_saved_words.set(final_own_word_ind,
								((RadioButton) v).getText().toString());

						edit_save_words = "";
						for (int i = 0; i < own_word_ind; i++) {
							edit_save_words += input_saved_words.get(i) + " ";
						}
						results.setText(Html.fromHtml("<b>SAVED WORDS:"
								+ edit_save_words + "</b>"));
						
						input_saved_words.set(
								final_own_word_ind,
								formatWord(Helpers.explode(list_saved_words
										.get(final_own_word_ind)),
										final_find_word_length));
						input_saved_numbers
								.set(final_own_word_ind, final_find_word);
						input_saved_infos.set(final_own_word_ind,
								list_saved_infos.get(final_own_word_ind));
						edit_save_words.clear();
						edit_save_words_string = "";
						String edit_numbers = "";
						String edit_words_infos = "";
						for (int i = 0; i < own_word_ind; i++) {
							edit_save_words.add(new ArrayList<String>());
							edit_save_words.get(i).add(input_saved_numbers.get(i));
							edit_save_words.get(i).add(input_saved_words.get(i));
							edit_save_words.get(i).add(input_saved_infos.get(i));
							edit_numbers += input_saved_numbers.get(i) + " ";
							edit_words_infos +=  input_saved_words.get(i) + "(" + input_saved_infos.get(i) + ") ";
						}
						edit_save_words_string = edit_numbers + edit_words_infos;
						results.setText(Html.fromHtml("<b>SAVE WORDS:"
								+ edit_save_words_string + "</b>"));
						
					}
				});
				flag = true;
			} while (myc.moveToNext());
			RadioButton rb_end = new RadioButton(this_act);
			rb_end.setText(Html.fromHtml("<b>Write Own Word?(" + prompt
					+ ")</b>"));
			rg.addView(rb_end);

			words_layout.addView(prompt_tv);
			words_layout.addView(rg);
			words_layout.addView(own_word_layout);
			rb_end.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					input_words.get(final_own_word_ind).setEnabled(true);
					input_words.get(final_own_word_ind).setBackgroundResource(
							R.drawable.rounded_edittext_red);
					input_saved_words.set(final_own_word_ind,
							input_words.get(final_own_word_ind).getText()
									.toString());
					edit_save_words = "SAVED WORDS:";
					for (int i = 0; i < own_word_ind; i++) {
						edit_save_words += input_saved_words.get(i) + " ";
					}
					results.setText(Html.fromHtml("<b>" + edit_save_words
							+ "</b>"));
				}
			});
			radio_groups.add(rg);
			own_word_ind++;
		}// END IF myc.moveToFirst();
		myc.close();
		return flag;
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.timeline);
			setTitle("TIMELINE");
			setViews();
			loadButtons();
			text = "Loading databases. Please wait...";
			results.setText(Html.fromHtml("<b>" + text + "<b>"));

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
			results.setText(Html.fromHtml(values[0]));
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected void onPostExecute(String file_url) {
			results.setText("");
			input_username.setText(sharedPref.getString(
					"TIMELINE USERNAME INPUT", ""));
			check_condensed_words.setChecked(sharedPref.getBoolean(
					"TIMELINE CHECK CONDENSED", true));
			check_timeline_get_words.setChecked(sharedPref.getBoolean(
					"TIMELINE CHECK GET WORDS", false));
			check_shared_events.setChecked(sharedPref.getBoolean(
					"TIMELINE CHECK SHARED EVENTS", false));
			check_save_timeline_global.setChecked(sharedPref.getBoolean(
					"TIMELINE CHECK SAVE GLOBAL", false));
			check_save_timeline_personal.setChecked(sharedPref.getBoolean(
					"TIMELINE CHECK SAVE PERSONAL", false));
			select_timeline_number_major_words
					.setSelection(((ArrayAdapter) select_timeline_number_major_words
							.getAdapter()).getPosition(sharedPref.getString(
							"TIMELINE SELECT NUMBER MAJOR WORDS",
							select_timeline_number_major_words
									.getItemAtPosition(0).toString())));
			setListeners();
			new doInitializeSpinners().execute();
			setTable();
		}

	}

	class doInitializeSpinners extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Calendar tod = Calendar.getInstance();
			select_timeline_month.setSelection(tod.get(Calendar.MONTH));
			select_timeline_day
					.setSelection(tod.get(Calendar.DAY_OF_MONTH) - 1);
			timesDoneAdapter.clear();
		}

		@Override
		protected String doInBackground(String... params) {
			ct_tot = 0;
			ct_all = 0;
			c_totals = MainLfqActivity.getMiscDb().rawQuery(
					"SELECT " + events_table._id + " FROM "
							+ tables.events_table, null);
			ct_all = c_totals.getCount();
			c_totals.close();
			for (int i = 0; i < months.size(); i++) {
				for (int j = 0; j < days.size(); j++) {
					c_totals = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT " + events_table._id + " FROM "
									+ tables.events_table + " WHERE "
									+ events_table.Date + "='" + months.get(i)
									+ "-" + days.get(j) + "' AND "
									+ events_table.Number1 + "<>''", null);
					publishProgress(months_complete.get(i) + " " + days.get(j)
							+ " HAS " + c_totals.getCount() + " SAVED WORDS.",
							"NOT_LAST");
					ct_tot += c_totals.getCount();
					c_totals.close();
				}
			}
			publishProgress(ct_tot + " OF " + ct_all, "LAST");
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (values[1] != "LAST") {
				timesDoneAdapter.add(values[0]);
			} else {
				timesDoneAdapter.insert(values[0], 0);
			}

		}

		@Override
		protected void onPostExecute(String file_url) {

		}

	}

	class doLoadYears extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			is_years_load = true;
			yearsAdapter.clear();
			results.setText(Html.fromHtml("<b>LOADING YEARS...</b>"));
		}

		@Override
		protected String doInBackground(String... params) {
			c_years = MainLfqActivity.getMiscDb().rawQuery(
					"SELECT DISTINCT " + events_table.Year + " FROM " + table
							+ " WHERE " + events_table.Year
							+ " LIKE '% BC' ORDER BY " + events_table.Year
							+ " DESC", null);
			if (c_years.moveToFirst()) {
				do {
					publishProgress(c_years.getString(0));
				} while (c_years.moveToNext());
			}
			c_years.close();
			c_years = MainLfqActivity.getMiscDb().rawQuery(
					"SELECT DISTINCT " + events_table.Year + " FROM " + table
							+ " WHERE " + events_table.Year
							+ " NOT LIKE '% BC' ORDER BY " + events_table.Year
							+ " ASC", null);
			if (c_years.moveToFirst()) {
				do {
					publishProgress(c_years.getString(0));
				} while (c_years.moveToNext());
			}
			c_years.close();
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			yearsAdapter.add(values[0]);
		}

		@Override
		protected void onPostExecute(String file_url) {
			results.setText("");
		}

	}

	public void getEvents(String get_events) {
		if (get_events.equals("shared_date_events")) {
			String month_number = String.valueOf(select_timeline_month
					.getSelectedItemPosition() + 1);
			String day_number = String.valueOf(select_timeline_day
					.getSelectedItemPosition() + 1);
			date_number_string = month_number + day_number;
			String month_string = month_number;
			String day_string = day_number;
			if (month_number.length() < 2) {
				month_string = "0" + month_string;
			}
			if (day_number.length() < 2) {
				day_string = "0" + day_string;
			}
			date = month_string + "-" + day_string;
		}

		if (get_events.equals("shared_year_events")) {
			year = select_years.getSelectedItem().toString();
			if (year.length() == 0) {
				results.setText(Html.fromHtml("<b>MUST ENTER A YEAR</b>"));
				return;
			}
		}
		if (get_events.equals("user_personal_events")) {
			type_clause = "PERSONAL DATES";
		}
		if (get_events.equals("user_historical_events")) {
			type_clause = "HISTORICAL DATES";
		}

		if (check_timeline_get_words.isChecked()) {
			prompt_timeline_words.setVisibility(View.VISIBLE);
			event_layout.setVisibility(View.VISIBLE);
			do_timeline_left.setVisibility(View.VISIBLE);
			do_timeline_right.setVisibility(View.VISIBLE);
			prompt_events_layout.setVisibility(View.VISIBLE);
			savewords_layout.setVisibility(View.VISIBLE);
			results.setText("");
			results2.setText("");
			event_layout.removeAllViews();

			// ADD EVENT TEXTVIEW:
			String event = "";
			event_tv = new TextView(this_act);
			event_layout.addView(event_tv);
			is_event_edit = false;

			if (get_events.equals("shared_date_events")) {
				c2 = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT " + events_table._id + " FROM "
								+ tables.events_table + " WHERE "
								+ events_table.Date + "='" + date + "'", null);
				total = c2.getCount();
				c2.close();
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM " + tables.events_table + " WHERE "
								+ events_table.Date + "='" + date
								+ "' ORDER BY " + events_table.Year
								+ " LIMIT 1", null);
			}

			if (get_events.equals("shared_year_events")) {
				c2 = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT " + events_table._id + " FROM "
								+ tables.events_table + " WHERE "
								+ events_table.Year + "='" + year + "'", null);
				total = c2.getCount();
				c2.close();
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM " + tables.events_table + " WHERE "
								+ events_table.Year + "='" + year
								+ "' ORDER BY " + events_table.Date + " ASC, "
								+ events_table._id + " ASC LIMIT 1", null);
			}
			if (get_events.equals("user_personal_events")
					|| get_events.equals("user_historical_events")) {
				c2 = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT " + user_events._id + " FROM "
								+ tables.user_events + " WHERE "
								+ user_events.Username + "='" + username
								+ "' AND " + user_events.Type + "='"
								+ type_clause + "'", null);
				total = c2.getCount();
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM " + tables.user_events + " WHERE "
								+ user_events.Username + "='" + username
								+ "' AND " + user_events.Type + "='"
								+ type_clause + "' ORDER BY "
								+ user_events.Year + " LIMIT 1", null);
			}
			if (c.moveToFirst()) {
				id = c.getString(c.getColumnIndex("_id"));
				event = c.getString(c.getColumnIndex("Event"));
				year = c.getString(c.getColumnIndex("Year"));
				date = c.getString(c.getColumnIndex("Date"));
				date_number_string = String.valueOf(Integer.parseInt(c
						.getString(c.getColumnIndex("Date")).substring(0, 2)))
						+ String.valueOf(Integer.parseInt(c.getString(
								c.getColumnIndex("Date")).substring(3)));
				display_date = months_complete
						.get(Integer.parseInt(c.getString(
								c.getColumnIndex("Date")).substring(0, 2)) - 1)
						+ " "
						+ String.valueOf(Integer.parseInt(c.getString(
								c.getColumnIndex("Date")).substring(3)));
				event_tv.setText(event);

				saved_words = getSavedWords(
						c.getString(c.getColumnIndex(events_table.Number1)),
						c.getString(c.getColumnIndex(events_table.Number_Info1)),
						c.getString(c
								.getColumnIndex(events_table.Number_Mnemonic1)),
						c.getString(c.getColumnIndex(events_table.Number2)),
						c.getString(c.getColumnIndex(events_table.Number_Info2)),
						c.getString(c
								.getColumnIndex(events_table.Number_Mnemonic2)),
						c.getString(c.getColumnIndex(events_table.Number3)),
						c.getString(c.getColumnIndex(events_table.Number_Info3)),
						c.getString(c
								.getColumnIndex(events_table.Number_Mnemonic3)),
						c.getString(c.getColumnIndex(events_table.Number4)),
						c.getString(c.getColumnIndex(events_table.Number_Info4)),
						c.getString(c
								.getColumnIndex(events_table.Number_Mnemonic4)));

				results2.setText(Html.fromHtml("<b>1 OF " + total
						+ " EVENTS.</b>"));
				show_year.setText(Html.fromHtml("<b>YEAR:" + year + "</b>"));
				show_date.setText(Html.fromHtml("<b>DATE:" + date + "</b>"));
				getMajorWords(year, date, date_number_string, saved_words);
			}
			number = 1;
			c.close();
		}
		if (!check_timeline_get_words.isChecked()) {
			prompt_timeline_words.setVisibility(View.GONE);
			event_layout.setVisibility(View.GONE);
			do_timeline_left.setVisibility(View.GONE);
			do_timeline_right.setVisibility(View.GONE);
			do_timeline_above.setVisibility(View.VISIBLE);
			prompt_events_layout.setVisibility(View.GONE);
			savewords_layout.setVisibility(View.GONE);
			above_controls.setVisibility(View.GONE);
			params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 600);
			params.addRule(RelativeLayout.BELOW, savewords_layout.getId());
			timeline_words_scroll.setLayoutParams(params);
			if (get_events.equals("shared_date_events")) {
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM " + tables.events_table + " WHERE "
								+ events_table.Date + "='" + date + "' AND "
								+ user_events.Number1 + "<>''", null);
			}
			if (get_events.equals("shared_year_events")) {
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM " + table + " WHERE "
								+ events_table.Year + "='" + year + "' AND "
								+ events_table.Number1 + "<>'' ORDER BY "
								+ events_table.Date, null);
			}
			if (get_events.equals("user_personal_events")
					|| get_events.equals("user_historical_events")) {
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM " + tables.user_events + " WHERE "
								+ user_events.Username + "='" + username
								+ "' AND " + user_events.Number1 + "<>'' AND "
								+ user_events.Type + "='" + type_clause + "'",
						null);
			}
			words_layout.removeAllViews();
			event_layout.removeAllViews();
			total = c.getCount();
			if (c.moveToFirst()) {
				do {
					TextView tv = new TextView(this_act);
					tv.setTextSize(15);
					tv.setText(Html.fromHtml("<b>DATE:"
							+ c.getString(c.getColumnIndex("Year"))
							+ ", "
							+ date
							+ "<br/>EVENT:<br/>"
							+ c.getString(c.getColumnIndex("Event"))
							+ "<br/>SAVED WORDS:<br/>"
							+ getSavedWords(
									c.getString(c
											.getColumnIndex(events_table.Number1)),
									c.getString(c
											.getColumnIndex(events_table.Number_Info1)),
									c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic1)),
									c.getString(c
											.getColumnIndex(events_table.Number2)),
									c.getString(c
											.getColumnIndex(events_table.Number_Info2)),
									c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic2)),
									c.getString(c
											.getColumnIndex(events_table.Number3)),
									c.getString(c
											.getColumnIndex(events_table.Number_Info3)),
									c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic3)),
									c.getString(c
											.getColumnIndex(events_table.Number4)),
									c.getString(c
											.getColumnIndex(events_table.Number_Info4)),
									c.getString(c
											.getColumnIndex(events_table.Number_Mnemonic4)))
							+ "<br/></b>"));
					words_layout.addView(tv);
				} while (c.moveToNext());
			}
			results.setText(Html.fromHtml("<b>SHOWING " + total
					+ " TOTAL EVENTS."));
			results2.setText("");
			c.close();
		}
	}

	public void setTable() {
		if (check_shared_events.isChecked()
				|| Helpers.getLoginStatus() == false) {
			table = "events_table";
			MainLfqActivity.setDatabase("events_db");
			database_string = "events_db";
			lfq_table = "" + Helpers.db_prefix + "events.events_table";
			get_user_personal_events.setVisibility(View.GONE);
			get_user_historical_events.setVisibility(View.GONE);
			do_timeline_get_date_events.setVisibility(View.VISIBLE);
			do_timeline_get_year_events.setVisibility(View.VISIBLE);
			prompt_timeline_date.setVisibility(View.VISIBLE);
			select_timeline_month.setVisibility(View.VISIBLE);
			select_timeline_day.setVisibility(View.VISIBLE);
			select_years.setVisibility(View.VISIBLE);
			if (is_years_load == false) {
				new doLoadYears().execute();
			}

		} else {
			username = Helpers.getUsername();
			table = tables.user_events;
			MainLfqActivity.setDatabase("nw_db");
			database_string = "nw_db";
			lfq_table = "" + Helpers.db_prefix + "newwords." + table;
			do_timeline_get_date_events.setVisibility(View.GONE);
			do_timeline_get_year_events.setVisibility(View.GONE);
			prompt_timeline_date.setVisibility(View.GONE);
			select_timeline_month.setVisibility(View.GONE);
			select_timeline_day.setVisibility(View.GONE);
			get_user_personal_events.setVisibility(View.VISIBLE);
			get_user_historical_events.setVisibility(View.VISIBLE);
			select_years.setVisibility(View.GONE);

		}
		if (Helpers.getLoginStatus() == false) {
			login_status.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
		}
	}
}