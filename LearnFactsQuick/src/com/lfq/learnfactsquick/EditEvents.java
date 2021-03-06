package com.lfq.learnfactsquick;

import java.util.ArrayList;

import com.lfq.learnfactsquick.Constants.cols.user_events;
import com.lfq.learnfactsquick.Constants.tables;
import com.lfq.learnfactsquick.Constants.cols.events_table;
import com.lfq.learnfactsquick.Constants.cols.global_numbers;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditEvents extends Activity {
	private Spinner select_month, select_day, select_year;
	private TextView results, event_results;
	private static EditText event_text;
	private EditText input_year;
	private RadioGroup user_type_radios;
	private CheckBox check_shared_table, check_input_bc;
	private RadioButton insert_event, delete_event, edit_event,
			check_get_dates, check_get_years, check_user_historical,
			check_user_personal;
	private Button get, edit, left, right, latest;

	private Cursor c = null, c_get_last = null, c_get_next = null;;
	private String event;
	private ArrayAdapter<String> yearsAdapter;
	private List<String> months_complete;
	private String day, month, date, year;
	private int day_number, month_number;
	private ContentValues values;
	private String sql, text, results_text;
	private int ct_tot;
	Calendar tod;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private String autosync_text;
	private SelectYearListener select_year_listener;
	private SelectMonthListener select_month_listener;
	private SelectDayListener select_day_listener;
	private static String username;
	private static Boolean is_database_load, is_after_years_load;
	private static String table, lfq_table, database_string;
	private int id, number, month_index, day_index, year_index, count;
	private String type;
	private int total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		tod = Calendar.getInstance();
		is_after_years_load = false;
		total = 0;
		table = "";
		database_string = "";
		sql = "";
		text = "";
		day = "";
		month = "";
		year_index = 0;
		ct_tot = 0;
		date = "";
		year = "";
		results_text = "";
		number = 1;
		year_index = 0;
		select_year_listener = new SelectYearListener();
		select_month_listener = new SelectMonthListener();
		select_day_listener = new SelectDayListener();
		months_complete = new ArrayList<String>();
		months_complete.addAll(Arrays.asList(new String[] { "January",
				"February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" }));
		values = new ContentValues();
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
		if (is_database_load == false) {
			new doLoadDatabases().execute();
		}
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

	public void saveChanges() {
		editor.putBoolean("EDIT EVENT CHECK INSERT", insert_event.isChecked());
		editor.putBoolean("EDIT EVENT CHECK DELETE", delete_event.isChecked());
		editor.putBoolean("EDIT EVENT CHECK EDIT", edit_event.isChecked());
		editor.putBoolean("EDIT EVENT CHECK DATES", check_get_dates.isChecked());
		editor.putBoolean("EDIT EVENT CHECK YEARS", check_get_years.isChecked());
		editor.putBoolean("EDIT EVENT CHECK SHARED TABLE",
				check_shared_table.isChecked());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
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

	public void setViews() {

		// LAYOUTS:
		setTitle("EDIT EVENTS");

		// BUTTONS:
		get = (Button) findViewById(R.id.get_edit_events);
		edit = (Button) findViewById(R.id.edit_edit_event);
		left = (Button) findViewById(R.id.do_edit_event_left);
		right = (Button) findViewById(R.id.do_edit_event_right);
		latest = (Button) findViewById(R.id.do_edit_event_set_latest);

		// CHECKBOXES:
		check_shared_table = (CheckBox) findViewById(R.id.check_edit_event_shared_table);
		check_input_bc = (CheckBox) findViewById(R.id.check_edit_event_input_bc);

		// EDITTEXTS:
		event_text = (EditText) findViewById(R.id.edit_event_input);
		input_year = (EditText) findViewById(R.id.edit_event_input_year);
		input_year.setVisibility(View.GONE);

		// SPINNERS:
		select_month = (Spinner) findViewById(R.id.select_edit_event_month);
		select_month.setOnTouchListener(select_month_listener);
		select_month.setOnItemSelectedListener(select_month_listener);
		select_month.setSelection(tod.get(Calendar.MONTH));

		select_day = (Spinner) findViewById(R.id.select_edit_event_day);
		select_day.setOnTouchListener(select_day_listener);
		select_day.setOnItemSelectedListener(select_day_listener);
		select_day.setSelection(tod.get(Calendar.DAY_OF_MONTH) - 1);

		// RADIOBUTTONS:
		insert_event = (RadioButton) findViewById(R.id.check_edit_event_insert);
		delete_event = (RadioButton) findViewById(R.id.check_edit_event_delete);
		edit_event = (RadioButton) findViewById(R.id.check_edit_event_edit);
		check_get_dates = (RadioButton) findViewById(R.id.check_edit_event_get_dates);
		check_get_years = (RadioButton) findViewById(R.id.check_edit_event_get_years);
		check_user_personal = (RadioButton) findViewById(R.id.check_edit_event_user_personal);
		check_user_historical = (RadioButton) findViewById(R.id.check_edit_event_user_historical);

		// RADIOGROUPS:
		user_type_radios = (RadioGroup) findViewById(R.id.edit_event_user_type_radios);

		// SPINNERS:
		select_year = (Spinner) findViewById(R.id.select_edit_event_year);
		select_year.setOnTouchListener(select_year_listener);
		select_year.setOnItemSelectedListener(select_year_listener);
		yearsAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		yearsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_year.setAdapter(yearsAdapter);

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.show_edit_event_results);
		event_results = (TextView) findViewById(R.id.event_results);
	}

	public void loadButtons() {
		get.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		edit.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		left.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		right.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		latest.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {

		check_shared_table.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setTable();
			}
		});
		latest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar tod = Calendar.getInstance();
				select_month.setSelection(tod.get(Calendar.MONTH));
				select_day.setSelection(tod.get(Calendar.DAY_OF_MONTH) - 1);
			}
		});
		left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new doLeft().execute();
			}
		});
		right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new doRight().execute();
			}
		});

		check_get_dates.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				select_month.setEnabled(true);
				select_day.setEnabled(true);
				latest.setEnabled(true);
				get.setEnabled(true);
				edit.setEnabled(true);
				doGetDates();
			}
		});
		check_get_years.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new doLoadSelectYears().execute();
			}
		});

		edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!check_shared_table.isChecked()) {
					if (Helpers.getLoginStatus() == false) {
						results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
						return;
					}
					if (check_user_personal.isChecked()) {
						type = "PERSONAL DATES";
					}
					if (check_user_historical.isChecked()) {
						type = "HISTORICAL DATES";
					}
				}
				if (insert_event.isChecked()) {
					year = input_year.getText().toString();
				}
				if (insert_event.isChecked() && year.equals("")) {
					results.setText(Html.fromHtml("<b>MUST ENTER A YEAR</b>"));
					return;
				}
				if (insert_event.isChecked() && year.length() > 5) {
					results.setText(Html
							.fromHtml("<b>ENTER ONLY A 4 DIGIT YEAR.</b>"));
					return;
				}
				while (year.length() < 4) {
					year = "0" + year;
				}
				if (check_input_bc.isChecked()) {
					year += " BC";
				}
				event = event_text.getText().toString();
				month_number = select_month.getSelectedItemPosition() + 1;
				day_number = select_day.getSelectedItemPosition() + 1;
				month = String.valueOf(month_number);
				day = String.valueOf(day_number);
				if (day.length() < 2) {
					day = "0" + day;
				}
				if (month.length() < 2) {
					month = "0" + month;
				}
				String date = month + "-" + day;
				if (insert_event.isChecked()) {
					values.clear();
					values.put(events_table.Year, year);
					values.put(events_table.Date, date);
					values.put(events_table.Event, event);
					MainLfqActivity.getDatabase().insert(table, null, values);
					if (check_shared_table.isChecked()) {
						sql = "INSERT INTO " + lfq_table + " ("
								+ events_table.Year + "," + events_table.Date
								+ "," + events_table.Event + "," + ") VALUES('"
								+ year + "','" + date + "','" + event + "')";
					} else {
						sql = "INSERT INTO " + lfq_table + " ("
								+ user_events.Date + ","
								+ user_events.Event + ","
								+ user_events.Type + ") VALUES('" + date
								+ "','" + event + "','" + type + "')";
						System.out.println(sql);
					}
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[] image)
					autosync_text += Synchronize.autoSync(sql, database_string,
							"insert", year + "-" + date, event, false, null);
					event_results.setText(Html
							.fromHtml("RESULTS: Inserted event:" + event
									+ ".<br /> On date:" + date + "<br />"
									+ autosync_text));

				}
				if (edit_event.isChecked()) {
					values.clear();
					values.put(events_table.Year, year);
					values.put(events_table.Date, date);
					values.put(events_table.Event, event);
					values.put("_id", id);
					if (!check_shared_table.isChecked()) {
						values.put(user_events.Type, type);
					}
					String[] selectionArgs = { String.valueOf(id) };
					MainLfqActivity.getDatabase().update(table, values,
							"_id=?", selectionArgs);
					if (!check_shared_table.isChecked()) {
						sql = "UPDATE " + lfq_table + " SET "
								+ user_events.Date + "='" + date + "', "
								+ user_events.Year + "', "
								+ user_events.Event + "='" + event
								+ "' WHERE " + user_events.Year + "='"
								+ year + "' AND ID='" + id + "'";
					} else {
						sql = "UPDATE " + lfq_table + " SET "
								+ events_table.Year + "='" + year + "', "
								+ events_table.Date + "='" + date + "', "
								+ events_table.Event + "='" + event + "', "
								+ events_table.Type + "='" + type + "' WHERE "
								+ events_table.Year + "='" + year
								+ "' AND ID='" + id + "'";
					}
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[] image)
					autosync_text = Synchronize.autoSync(sql, database_string,
							"update", year + "-" + date, String.valueOf(id),
							false, null);
					c = MainLfqActivity.getDatabase().rawQuery(
							"SELECT * FROM " + table + " WHERE "
									+ events_table.Date + "='" + date
									+ "' AND " + events_table.Year + "=? AND "
									+ events_table.Event + "=?", selectionArgs);
					if (!c.moveToFirst()) {
						event_results.setText("RESULTS: event doesn't exist."
								+ autosync_text);
					} else {
						event_results.setText("RESULTS: Updated " + date
								+ ". and new event is: " + event + "."
								+ autosync_text);
					}

				}
				if (delete_event.isChecked()) {
					String[] selectionArgs = { year, String.valueOf(id) };
					MainLfqActivity.getDatabase().delete(
							table,
							events_table.Year + "=? AND " + events_table._id
									+ "=?", selectionArgs);
					// SYNC TO LFQ:
					autosync_text = "";
					sql = "DELETE FROM " + lfq_table + " WHERE "
							+ events_table.Date + "='" + date + "' AND "
							+ events_table.Year + "='" + year + "' AND "
							+ events_table._id + "='" + id + "'";
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[] image)
					autosync_text = Synchronize.autoSync(sql, database_string,
							"delete", year + "-" + date, String.valueOf(id),
							false, null);
					event_results.setText(Html
							.fromHtml("RESULTS: Deleted event: " + event
									+ ".<br />" + autosync_text));
				}

			}
		});

		get.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (is_after_years_load == true) {
					if (yearsAdapter.getCount() > 0) {
						year = yearsAdapter.getItem(0);
					}
					is_after_years_load = false;
				} else {
					if (yearsAdapter.getCount() > 0) {
						year = select_year.getSelectedItem().toString();
					}
				}

				month_number = select_month.getSelectedItemPosition() + 1;
				day_number = select_day.getSelectedItemPosition() + 1;
				month = String.valueOf(month_number);
				day = String.valueOf(day_number);
				if (day.length() < 2) {
					day = "0" + day;
				}
				if (month.length() < 2) {
					month = "0" + month;
				}
				date = month + "-" + day;
				c = MainLfqActivity.getDatabase().rawQuery(
						"SELECT * FROM " + table + " WHERE "
								+ events_table.Date + "='" + date + "' AND "
								+ events_table.Year + "='" + year + "'", null);
				if (c.moveToFirst()) {
					if (c.getColumnIndex(events_table.Event) != -1) {
						if (c.getString(c.getColumnIndex(events_table.Event)) != null) {
							event_text.setText(c.getString(c
									.getColumnIndex(events_table.Event)));
						}
					}
					id = c.getInt(c.getColumnIndex(events_table._id));
				}
				c.close();
			}

		});

		insert_event.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				input_year.setVisibility(View.VISIBLE);
				user_type_radios.setVisibility(View.VISIBLE);
				check_input_bc.setVisibility(View.VISIBLE);
			}
		});
		delete_event.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				input_year.setVisibility(View.GONE);
				user_type_radios.setVisibility(View.GONE);
				check_input_bc.setVisibility(View.GONE);
			}
		});
		edit_event.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				input_year.setVisibility(View.GONE);
				user_type_radios.setVisibility(View.GONE);
				check_input_bc.setVisibility(View.GONE);
			}
		});

	}

	class doLeft extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			left.setEnabled(false);
		}

		@Override
		protected String doInBackground(String... params) {
			if (yearsAdapter.getCount() > 0) {
				year = select_year.getSelectedItem().toString();
			} else {
				return null;
			}
			System.out.println("year=" + year + "date=" + date + "table="
					+ table + ", id=" + id);
			// GET TOTAL:
			if (check_get_years.isChecked()) {
				c = MainLfqActivity.getDatabase().rawQuery(
						"SELECT " + events_table._id + " FROM " + table
								+ " WHERE " + events_table.Year + "='" + year
								+ "'", null);
			} else {
				if (count == 0) {
					return null;
				}
				c = MainLfqActivity.getDatabase().rawQuery(
						"SELECT " + events_table._id + " FROM " + table
								+ " WHERE " + events_table.Date + "='" + date
								+ "'", null);
			}
			total = c.getCount();
			c.close();
			// -----------
			// GET DATES:
			if (check_get_dates.isChecked()) {
				c_get_last = MainLfqActivity.getDatabase().rawQuery(
						"SELECT * FROM " + table + " WHERE "
								+ events_table.Date + "='" + date + "' AND "
								+ events_table.Year + "='" + year + "' AND "
								+ events_table._id + "<'" + id + "' ORDER BY "
								+ events_table._id + " DESC LIMIT 1", null);
				System.out.println("called 1st c_get_last");
				if (c_get_last.getCount() == 0) {
					if (year.contains("BC")) {// IS BC YEAR:
						c_get_last.close();
						c_get_last = MainLfqActivity.getDatabase().rawQuery(
								"SELECT * FROM " + table + " WHERE "
										+ events_table.Date + "='" + date
										+ "' AND " + events_table.Year
										+ " LIKE '% BC' AND "
										+ events_table.Year + ">'" + year
										+ "' ORDER BY " + events_table.Year
										+ " ASC," + events_table._id
										+ " DESC LIMIT 1", null);
						System.out.println("called 2nd c_get_last");
					} else {// FOR AD Year:
						c_get_last.close();
						c_get_last = MainLfqActivity.getDatabase().rawQuery(
								"SELECT * FROM " + table + " WHERE "
										+ events_table.Date + "='" + date
										+ "' AND " + events_table.Year
										+ " NOT LIKE '% BC' AND "
										+ events_table.Year + "<'" + year
										+ "' ORDER BY " + events_table.Year
										+ " DESC," + events_table._id
										+ " DESC LIMIT 1", null);
						System.out.println("called 3rd c_get_last");
						if (c_get_last.getCount() == 0) {// YEAR IS EARLIEST
															// AD, SO GET
															// BC:
							c_get_last.close();
							c_get_last = MainLfqActivity.getDatabase()
									.rawQuery(
											"SELECT * FROM " + table
													+ " WHERE "
													+ events_table.Date + "='"
													+ date + "' AND "
													+ events_table.Year
													+ " LIKE '% BC' ORDER BY "
													+ events_table.Year
													+ " ASC,"
													+ events_table._id
													+ " DESC LIMIT 1", null);
							System.out.println("called 4th c_get_last");

						}
					}
				}
				if (year_index > 0) {
					year_index--;
				}

			}
			// GET YEARS:
			if (check_get_years.isChecked()) {
				c_get_last = MainLfqActivity.getDatabase().rawQuery(
						"SELECT * FROM " + table + " WHERE "
								+ events_table.Date + "='" + date + "' AND "
								+ events_table.Year + "='" + year + "' AND "
								+ events_table._id + "<'" + id + "' ORDER BY "
								+ events_table._id + " DESC LIMIT 1", null);

				if (c_get_last.getCount() == 0) {
					c_get_last.close();
					c_get_last = MainLfqActivity.getDatabase().rawQuery(
							"SELECT * FROM " + table + " WHERE "
									+ events_table.Date + "<'" + date
									+ "' AND " + events_table.Year + "='"
									+ year + "' ORDER BY " + events_table.Date
									+ " DESC," + events_table._id
									+ " DESC LIMIT 1", null);
				}
			}
			if (c_get_last.moveToFirst() && number > 1) {
				number--;
				results_text = "SHOWING " + number + " OF " + total
						+ " EVENTS ";
				if (check_get_dates.isChecked()) {
					results_text += "ON "
							+ select_month.getSelectedItem().toString() + " "
							+ select_day.getSelectedItem().toString() + ".";
				}
				if (check_get_years.isChecked()) {
					results_text += "IN " + year + ".";
				}
				id = c_get_last.getInt(c_get_last
						.getColumnIndex(events_table._id));
				if (check_get_years.isChecked()) {
					String date_split[] = c_get_last.getString(
							c_get_last.getColumnIndex(events_table.Date))
							.split("-");
					date = c_get_last.getString(c_get_last
							.getColumnIndex(events_table.Date));
					month_index = Integer.parseInt(date_split[0]) - 1;
					day_index = Integer.parseInt(date_split[1]) - 1;
				}
			} else {
				results_text = "SHOWING " + number + " OF " + total
						+ " EVENTS ";
				if (check_get_dates.isChecked()) {
					results_text += "ON "
							+ select_month.getSelectedItem().toString() + " "
							+ select_day.getSelectedItem().toString();
				}
				if (check_get_years.isChecked()) {
					results_text += "IN " + year;
				}
				results_text += ". NO LAST EVENT.";
			}
			publishProgress("");
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (c_get_last.moveToFirst()) {
				if (check_get_dates.isChecked()) {
					select_year.setSelection(year_index);
				}
				if (check_get_years.isChecked()) {
					select_month.setSelection(month_index);
					select_day.setSelection(day_index);
				}
				event_text.setText(c_get_last.getString(c_get_last
						.getColumnIndex(events_table.Event)));
			}
			c_get_last.close();
			results.setText(results_text);
			left.setEnabled(true);
		}

		@Override
		protected void onPostExecute(String file_url) {
		}

	}

	class doRight extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			right.setEnabled(false);
		}

		@Override
		protected String doInBackground(String... params) {
			results_text = "";
			if (yearsAdapter.getCount() > 0) {
				year = select_year.getSelectedItem().toString();
			} else {
				System.out.println("YEARSADAPTER=0");
				return null;
			}
			System.out.println("year=" + year + "date=" + date + "table="
					+ table + ", id=" + id);
			if (check_get_years.isChecked()) {
				c = MainLfqActivity.getDatabase().rawQuery(
						"SELECT " + events_table._id + " FROM " + table
								+ " WHERE " + events_table.Year + "='" + year
								+ "'", null);
			} else {// FOR check_get_dates:
				if (count == 0) {
					System.out.println("COUNT=0");
					return null;
				}
				c = MainLfqActivity.getDatabase().rawQuery(
						"SELECT " + events_table._id + " FROM " + table
								+ " WHERE " + events_table.Date + "='" + date
								+ "'", null);
			}
			total = c.getCount();
			c.close();
			System.out.println("IN GET NEXT: number=" + number + ", total="
					+ total);
			// GET DATES:
			if (check_get_dates.isChecked()) {
				c_get_next = MainLfqActivity.getDatabase().rawQuery(
						"SELECT * FROM " + table + " WHERE "
								+ events_table.Date + "='" + date + "' AND "
								+ events_table.Year + "='" + year + "' AND "
								+ events_table._id + ">'" + id + "' LIMIT 1",
						null);
				System.out.println("called 1st c_get_next");
				if (c_get_next.getCount() == 0) {
					if (year.contains("BC")) {
						c_get_next.close();
						c_get_next = MainLfqActivity.getDatabase().rawQuery(
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
									.getDatabase()
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
													+ events_table.Year + ","
													+ events_table._id
													+ " LIMIT 1", null);
						}
					} else {// FOR AD Year:
						c_get_next.close();
						c_get_next = MainLfqActivity.getDatabase().rawQuery(
								"SELECT * FROM " + table + " WHERE "
										+ events_table.Date + "='" + date
										+ "' AND " + events_table.Year
										+ " NOT LIKE '% BC' AND "
										+ events_table.Year + ">'" + year
										+ "' ORDER BY " + events_table.Year
										+ "," + events_table._id + " LIMIT 1",
								null);
						System.out.println("called 4th c_get_next");
					}
					if (year_index < yearsAdapter.getCount() - 1) {
						year_index++;
					}
				}
			}
			// GET YEARS:
			if (check_get_years.isChecked()) {
				c_get_next = MainLfqActivity.getDatabase().rawQuery(
						"SELECT * FROM " + table + " WHERE "
								+ events_table.Date + "='" + date + "' AND "
								+ events_table.Year + "='" + year + "' AND "
								+ events_table._id + ">'" + id + "' LIMIT 1",
						null);
				if (c_get_next.getCount() == 0) {
					c_get_next.close();
					c_get_next = MainLfqActivity.getDatabase()
							.rawQuery(
									"SELECT * FROM " + table + " WHERE "
											+ events_table.Date + ">'" + date
											+ "' AND " + events_table.Year
											+ "='" + year + "' ORDER BY "
											+ events_table.Date + ","
											+ events_table._id + " LIMIT 1",
									null);
				}
			}
			if (c_get_next.moveToFirst() && number < total) {
				if (check_get_years.isChecked()) {
					String date_split[] = c_get_next.getString(
							c_get_next.getColumnIndex(events_table.Date))
							.split("-");
					date = c_get_next.getString(c_get_next
							.getColumnIndex(events_table.Date));
					month_index = Integer.parseInt(date_split[0]) - 1;
					day_index = Integer.parseInt(date_split[1]) - 1;
				}
				number++;
				results_text += "SHOWING " + number + " OF " + total
						+ " EVENTS ";
				if (check_get_dates.isChecked()) {
					results_text += "ON "
							+ select_month.getSelectedItem().toString() + " "
							+ select_day.getSelectedItem().toString();
				}
				if (check_get_years.isChecked()) {
					results_text += "IN " + year;
				}
				id = c_get_next.getInt(c_get_next
						.getColumnIndex(events_table._id));
			} else {
				results_text += "SHOWING " + number + " OF " + total
						+ " EVENTS ";
				if (check_get_dates.isChecked()) {
					results_text += "ON "
							+ select_month.getSelectedItem().toString() + " "
							+ select_day.getSelectedItem().toString();
				}
				if (check_get_years.isChecked()) {
					results_text += "IN " + year;
				}
				results_text += ". NO NEXT EVENT";
				event = "NO EVENT";
			}

			publishProgress("");
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			System.out.println("number=" + number + ", total=" + total);
			if (check_get_dates.isChecked()) {
				select_year.setSelection(year_index);
			}
			if (c_get_next.moveToFirst()) {
				event_text.setText(c_get_next.getString(c_get_next
						.getColumnIndex(events_table.Event)));
				if (check_get_years.isChecked()) {
					select_month.setSelection(month_index);
					select_day.setSelection(day_index);
				}
			}
			c_get_next.close();
			results.setText(results_text);
			right.setEnabled(true);
		}

		@Override
		protected void onPostExecute(String file_url) {
		}

	}

	public void setTable() {
		if (check_shared_table.isChecked() == false
				&& Helpers.getLoginStatus() == true) {
			username = Helpers.getUsername();
			table = username + "_eventtable";
			MainLfqActivity.setDatabase("nw_db");
			database_string = "nw_db";
			lfq_table = "" + Helpers.db_prefix + "newwords." + table;
		} else {
			if (!Helpers.getLoginStatus()) {
				results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
				check_shared_table.setChecked(true);
			}
			table = "events_table";
			MainLfqActivity.setDatabase("events_db");
			database_string = "events_db";
			lfq_table = "" + Helpers.db_prefix + "events.events_table";
		}
	}

	class doLoadSelectYears extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			yearsAdapter.clear();
			select_month.setEnabled(false);
			select_day.setEnabled(false);
			latest.setEnabled(false);
			get.setEnabled(false);
			edit.setEnabled(false);
			check_get_years.setEnabled(false);
			event_text.setEnabled(false);
			left.setEnabled(false);
			right.setEnabled(false);
			results.setText(Html.fromHtml("<b>LOADING YEARS...</b>"));
			number = 1;
			year_index = 0;
			setTable();
		}

		@Override
		protected String doInBackground(String... params) {
			c = MainLfqActivity.getDatabase().rawQuery(
					"SELECT DISTINCT " + events_table.Year + " FROM " + table
							+ " WHERE " + events_table.Year
							+ " LIKE '% BC' ORDER BY " + events_table.Year
							+ " DESC", null);
			if (c.moveToFirst()) {
				do {
					year = c.getString(0);
					publishProgress(year);
				} while (c.moveToNext());
			}
			c.close();
			c = MainLfqActivity.getDatabase().rawQuery(
					"SELECT DISTINCT " + events_table.Year + " FROM " + table
							+ " WHERE " + events_table.Year
							+ " NOT LIKE '% BC' ORDER BY " + events_table.Year
							+ " ASC", null);
			if (c.moveToFirst()) {
				do {
					year = c.getString(0);
					publishProgress(year);
				} while (c.moveToNext());
			}
			c.close();
			year_index = 0;
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			yearsAdapter.add(values[0]);
			// System.out.println("published:"+values[0]);
		}

		@Override
		protected void onPostExecute(String file_url) {
			c = MainLfqActivity.getDatabase().rawQuery(
					"SELECT " + events_table.Date + " FROM " + table
							+ " WHERE " + events_table.Year + "='"
							+ yearsAdapter.getItem(0) + "'", null);
			if (c.moveToFirst()) {
				select_month.setSelection(Integer.parseInt(c.getString(0)
						.substring(0, 2)) - 1);
				select_day.setSelection(Integer.parseInt(c.getString(0)
						.substring(3)) - 1);
			}
			c.close();
			latest.setEnabled(true);
			get.setEnabled(true);
			edit.setEnabled(true);
			check_get_years.setEnabled(true);
			event_text.setEnabled(true);
			left.setEnabled(true);
			right.setEnabled(true);
			if (yearsAdapter.getCount() > 0) {
				year = yearsAdapter.getItem(0);
			}
			// GET TOTAL COUNT:
			c = MainLfqActivity.getDatabase().rawQuery(
					"SELECT " + events_table._id + " FROM " + table + " WHERE "
							+ events_table.Year + "='" + year + "'", null);
			ct_tot = c.getCount();
			c.close();
			// ---------------
			results.setText("SHOWING 1 OF " + ct_tot + " IN YEAR " + year + ".");
			is_after_years_load = true;
			get.performClick();
		}

	}

	public void doGetDates() {
		setTable();
		number = 1;
		year_index = 0;
		day_number = select_day.getSelectedItemPosition() + 1;
		month_number = select_month.getSelectedItemPosition() + 1;
		System.out.println("day_number=" + day_number + ", month_number="
				+ month_number);
		month = String.valueOf(month_number);
		day = String.valueOf(day_number);
		if (day.length() < 2) {
			day = "0" + day;
		}
		if (month.length() < 2) {
			month = "0" + month;
		}
		date = month + "-" + day;
		yearsAdapter.clear();
		// GET TOTAL COUNT:
		c = MainLfqActivity.getDatabase().rawQuery(
				"SELECT " + events_table._id + " FROM " + table + " WHERE "
						+ events_table.Date + "='" + date + "'", null);
		ct_tot = c.getCount();
		System.out.println("ct tot=" + ct_tot);
		c.close();
		// ---------------
		System.out.println("table=" + table + ", date=" + date);
		Cursor c_first = MainLfqActivity.getDatabase().rawQuery(
				"SELECT * FROM " + table + " WHERE " + events_table.Date + "='"
						+ date + "' AND " + events_table.Year
						+ " LIKE '% BC' ORDER BY " + events_table.Year
						+ " DESC," + events_table._id + " ASC LIMIT 1", null);
		count = 0;
		if (c_first.getCount() == 0) {
			c_first = MainLfqActivity.getDatabase().rawQuery(
					"SELECT * FROM " + table + " WHERE " + events_table.Date
							+ "='" + date + "' AND " + events_table.Year
							+ " NOT LIKE '% BC' ORDER BY " + events_table.Year
							+ "," + events_table._id + " LIMIT 1", null);
		}
		if (c_first.moveToFirst()) {
			count = 1;
			id = c_first.getInt(c_first.getColumnIndex(events_table._id));
		} else {
			count = 0;
		}
		System.out.println("get dates id=" + id);
		c_first.close();
		// SET (DISTINCT) BC AND AD years to yearsAdapter:
		System.out.println("table=" + table + ", date=" + date + ", year="
				+ year);
		c = MainLfqActivity.getDatabase().rawQuery(
				"SELECT DISTINCT " + events_table.Year + " FROM " + table
						+ " WHERE " + events_table.Date + "='" + date
						+ "' AND " + events_table.Year
						+ " LIKE '% BC' ORDER BY " + events_table.Year
						+ " DESC", null);
		if (c.moveToFirst()) {
			do {
				yearsAdapter.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
		c = MainLfqActivity.getDatabase().rawQuery(
				"SELECT DISTINCT " + events_table.Year + " FROM " + table
						+ " WHERE " + events_table.Date + "='" + date
						+ "' AND + " + events_table.Year
						+ " NOT LIKE '% BC' ORDER BY " + events_table.Year
						+ " ASC", null);
		if (c.moveToFirst()) {
			do {
				yearsAdapter.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
		select_year.setSelection(0);
		results.setText("SHOWING " + count + " OF " + ct_tot + " EVENTS ON "
				+ month + " " + day + ".");
		is_after_years_load = false;
		get.performClick();
	}

	@SuppressLint("ClickableViewAccessibility")
	public class SelectYearListener implements
			AdapterView.OnItemSelectedListener, View.OnTouchListener {
		boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (userSelect) {
				new doSelectYear().execute();
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	public class SelectMonthListener implements
			AdapterView.OnItemSelectedListener, View.OnTouchListener {
		boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (userSelect) {
				if (check_get_dates.isChecked()) {
					doGetDates();
				}
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	public class SelectDayListener implements
			AdapterView.OnItemSelectedListener, View.OnTouchListener {
		boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (userSelect) {
				if (check_get_dates.isChecked()) {
					doGetDates();
				}
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	class doSelectYear extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			total = 0;
		}

		@Override
		protected String doInBackground(String... params) {
			System.out.println("CALLING SELECT YEAR LISTENER");

			if (yearsAdapter.getCount() > 0) {
				year = select_year.getSelectedItem().toString();
			}
			if (check_get_dates.isChecked()) {
				c = MainLfqActivity.getDatabase().rawQuery(
						"SELECT " + events_table.Date + " FROM " + table
								+ " WHERE " + events_table.Date + "='" + date
								+ "'", null);
				total = c.getCount();
				c.close();
				if (year.contains("BC")) {
					c = MainLfqActivity.getDatabase().rawQuery(
							"SELECT " + events_table.Date + " FROM " + table
									+ " WHERE " + events_table.Year + ">='"
									+ year + "' AND " + events_table.Year
									+ " LIKE '% BC' AND " + events_table.Date
									+ "='" + date + "'", null);
					number = c.getCount();
					c.close();
				} else {
					c = MainLfqActivity.getDatabase().rawQuery(
							"SELECT " + events_table.Date + " FROM " + table
									+ " WHERE " + events_table.Year
									+ " LIKE '% BC' AND " + events_table.Date
									+ "='" + date + "'", null);
					number = c.getCount();
					c.close();
					c = MainLfqActivity.getDatabase().rawQuery(
							"SELECT " + events_table.Date + " FROM " + table
									+ " WHERE " + events_table.Year + "<='"
									+ year + "' AND " + events_table.Year
									+ " NOT LIKE '% BC' AND "
									+ events_table.Date + "='" + date + "'",
							null);
					number += c.getCount();
					c.close();
				}
				publishProgress("");

			}
			if (check_get_years.isChecked()) {
				c = MainLfqActivity.getDatabase().rawQuery(
						"SELECT " + events_table.Date + " FROM " + table
								+ " WHERE " + events_table.Year + "='" + year
								+ "'", null);
				total = c.getCount();
				publishProgress("");
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (check_get_dates.isChecked()) {
				results.setText("SHOWING " + number + " OF " + total
						+ " EVENTS ON " + date + ".");
				get.performClick();
			}
			if (check_get_years.isChecked()) {
				if (c.moveToFirst()) {
					select_month.setSelection(Integer.parseInt(c.getString(0)
							.substring(0, 2)) - 1);
					select_day.setSelection(Integer.parseInt(c.getString(0)
							.substring(3)) - 1);
				}
				results.setText("SHOWING 1 OF " + total + " EVENTS IN " + year
						+ ".");
				c.close();
				get.performClick();
			}
		}

		@Override
		protected void onPostExecute(String file_url) {
		}
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_events);
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

		@Override
		protected void onPostExecute(String file_url) {
			setListeners();
			results.setText("");
			if (sharedPref.getInt("EDIT EVENT CHOICES RADIOS",
					R.id.check_edit_event_edit) != -1) {
				((RadioButton) findViewById(sharedPref
						.getInt("EDIT EVENT CHOICES RADIOS",
								R.id.check_edit_event_edit))).setChecked(true);
			} else {
				edit_event.setChecked(true);
			}
			insert_event.setChecked(sharedPref.getBoolean(
					"EDIT EVENT CHECK INSERT", false));
			delete_event.setChecked(sharedPref.getBoolean(
					"EDIT EVENT CHECK DELETE", false));
			edit_event.setChecked(sharedPref.getBoolean(
					"EDIT EVENT CHECK EDIT", false));
			check_shared_table.setChecked(sharedPref.getBoolean(
					"EDIT EVENT CHECK SHARED TABLE", false));
			setTable();
			check_get_dates.setChecked(sharedPref.getBoolean(
					"EDIT EVENT CHECK DATES", false));
			check_get_years.setChecked(sharedPref.getBoolean(
					"EDIT EVENT CHECK YEARS", false));
			// check_get_dates.performClick();
		}
	}
}
