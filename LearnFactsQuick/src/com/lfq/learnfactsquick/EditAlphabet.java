package com.lfq.learnfactsquick;

import java.util.ArrayList;

import com.lfq.learnfactsquick.Constants.cols.alphabet;
import com.lfq.learnfactsquick.Constants.cols.alphabet_tables;
import com.lfq.learnfactsquick.Constants.tables;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditAlphabet extends Activity {
	private Spinner select_adjective, select_letter, select_category,
			select_table;
	private TextView results, show_insertions, show_number_insertions;
	private EditText alphabet_text, edit_table_name;
	private CheckBox check_dont_show;
	private RadioButton insert_alphabet, delete_alphabet, edit_alphabet;
	private Button get, edit, insert_adjective_table, delete_adjective_table,
			get_next_alphabet, get_last_alphabet;
	private Cursor c = null, c2 = null;
	private String alpent, adjective, letter, table_name, category;
	ArrayAdapter<String> dataAdapter, tablesAdapter, categoriesAdapter;
	private String alp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int index = 0;
	private ContentValues values;
	private android.widget.RelativeLayout.LayoutParams params;
	private String sql, sql1, sql2, text;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private String autosync_text;
	// private int id;

	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		values = new ContentValues();
		// id = 0;
		sql = "";
		sql1 = "";
		sql2 = "";
		text = "";
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
		editor.putBoolean("EDIT ALPHABET CHECK DON'T SHOW",
				check_dont_show.isChecked());

		editor.putBoolean("EDIT ALPHABET CHECK INSERT",
				insert_alphabet.isChecked());
		editor.putBoolean("EDIT ALPHABET CHECK DELETE",
				delete_alphabet.isChecked());
		editor.putBoolean("EDIT ALPHABET CHECK EDIT", edit_alphabet.isChecked());

		editor.putString("EDIT ALPHABET INPUT", alphabet_text.getText()
				.toString());
		editor.putString("EDIT ALPHABET INPUT TABLE NAME", edit_table_name
				.getText().toString());
		editor.putString("EDIT ALPHABET SELECT ADJECTIVE", select_adjective
				.getSelectedItem().toString());
		editor.putString("EDIT ALPHABET SELECT LETTER", select_letter
				.getSelectedItem().toString());
		editor.putString("EDIT ALPHABET SELECT CATEGORY", select_category
				.getSelectedItem().toString());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void setViews() {
		// LAYOUTS:
		setTitle("EDIT ALPHABETS");

		// BUTTONS:
		get_next_alphabet = (Button) findViewById(R.id.get_next_alphabet);
		get_last_alphabet = (Button) findViewById(R.id.get_last_alphabet);
		get = (Button) findViewById(R.id.get_alphabets);
		edit = (Button) findViewById(R.id.edit_specific_alphabet);
		insert_adjective_table = (Button) findViewById(R.id.insert_adjective_table);
		delete_adjective_table = (Button) findViewById(R.id.delete_adjective_table);

		// CHECKBOXES:
		check_dont_show = (CheckBox) findViewById(R.id.check_dont_show);

		// EDITTEXTS:
		alphabet_text = (EditText) findViewById(R.id.alphabet_text);
		edit_table_name = (EditText) findViewById(R.id.edit_table_name);

		// RADIOBUTTONS:
		insert_alphabet = (RadioButton) findViewById(R.id.insert_alphabet);
		delete_alphabet = (RadioButton) findViewById(R.id.delete_alphabet);
		edit_alphabet = (RadioButton) findViewById(R.id.edit_alphabet);

		// SPINNERS:
		select_adjective = (Spinner) findViewById(R.id.select_adjective);
		select_letter = (Spinner) findViewById(R.id.select_letter);
		select_category = (Spinner) findViewById(R.id.select_category);
		categoriesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		categoriesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_category.setAdapter(categoriesAdapter);
		select_table = (Spinner) findViewById(R.id.select_alp_tables);
		tablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		tablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_table.setAdapter(tablesAdapter);
		dataAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_adjective.setAdapter(dataAdapter);

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.show_alphabet_results);
		show_insertions = (TextView) findViewById(R.id.show_insertions);
		show_number_insertions = (TextView) findViewById(R.id.show_number_insertions);
		show_number_insertions.setTypeface(null, Typeface.BOLD);

		int measuredHeight = 0;
		WindowManager w = getWindowManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			measuredHeight = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			measuredHeight = d.getHeight();

		}

		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				(int) (measuredHeight * 0.4));
		params.addRule(RelativeLayout.BELOW,
				R.id.edit_alphabet_last_next_layout);
		alphabet_text.setLayoutParams(params);

	}

	public void OrientationEventListener(Context context, int rate) {

	}

	public void loadButtons() {
		get_next_alphabet.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		get_last_alphabet.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		get.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		edit.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		insert_adjective_table.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		delete_adjective_table.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
	}

	public void setListeners() {

		select_letter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!check_dont_show.isChecked()) {
					adjective = select_adjective.getSelectedItem().toString();
					letter = select_letter.getSelectedItem().toString();
					getAlphabets(adjective, letter, "");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		select_adjective
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (!check_dont_show.isChecked()) {
							adjective = select_adjective.getSelectedItem()
									.toString();
							letter = select_letter.getSelectedItem().toString();
							getAlphabets(adjective, letter, "");
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

		select_category.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String category = select_category.getSelectedItem().toString();
				getTables(category);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				adjective = select_adjective.getSelectedItem().toString();
				letter = select_letter.getSelectedItem().toString();
				alpent = alphabet_text.getText().toString();
				if (insert_alphabet.isChecked()) {
					insertAlphabet(adjective, letter, alpent);
				}
				if (edit_alphabet.isChecked()) {
					editAlphabet(adjective, letter, alpent);
				}
				if (delete_alphabet.isChecked()) {
					deleteAlphabet(adjective, letter, alpent);
				}
				showComplete();
			}
		});

		get.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adjective = select_adjective.getSelectedItem().toString();
				letter = select_letter.getSelectedItem().toString();
				getAlphabets(adjective, letter, "");
			}
		});

		get_next_alphabet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				adjective = select_adjective.getSelectedItem().toString();
				letter = select_letter.getSelectedItem().toString();
				getAlphabets(adjective, letter, "get_next");
			}
		});

		get_last_alphabet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				adjective = select_adjective.getSelectedItem().toString();
				letter = select_letter.getSelectedItem().toString();
				getAlphabets(adjective, letter, "get_last");
			}
		});

		insert_adjective_table.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				table_name = edit_table_name.getText().toString();
				category = select_category.getSelectedItem().toString();
				insertTable(table_name, category);

			}
		});

		delete_adjective_table.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				table_name = edit_table_name.getText().toString();
				category = select_category.getSelectedItem().toString();
				deleteTable(table_name, category);

			}
		});

	}

	public void loadSelectAdjectives() {
		Cursor cursor = MainLfqActivity.getMiscDb().rawQuery(
				" SELECT " + alphabet_tables.Table_name + " FROM " + tables.alphabet_tables + " ORDER BY " + alphabet_tables.Table_name, null);
		dataAdapter.clear();
		if (cursor.moveToFirst()) {
			do {
   			   dataAdapter.add(cursor.getString(0));				
			} while (cursor.moveToNext());
		} else {
			results.setText("nothing found");
		}
		cursor = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT DISTINCT " + alphabet_tables.Category + " FROM "
						+ tables.alphabet_tables + " ORDER BY "
						+ alphabet_tables.Category, null);
		categoriesAdapter.clear();
		System.out.println("# Categories=" + cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				categoriesAdapter.add(cursor.getString(cursor
						.getColumnIndex(alphabet_tables.Category)));
			} while (cursor.moveToNext());
			cursor.close();
		}
		select_category.setAdapter(categoriesAdapter);
		getTables(select_category.getSelectedItem().toString());
	}

	public void getAlphabets(String adjective, String letter, String opt) {

		int c_ct = 1;
		if (opt.equals("")) {
			index = 0;
		}
		show_number_insertions.setText("");
		c = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT Entry FROM " + tables.alphabet + " WHERE " + alphabet.Table_name + "='" + adjective + "' AND " + alphabet.Letter + "='" + letter
						+ "' ORDER BY " + alphabet_tables._id, null);
		if (c.moveToFirst()) {
			while (c.moveToNext()) {
				if (c.getString(0).equals("")) {
					break;
				}
				c_ct++;
			}
			c.moveToFirst();
			if (opt.equals("get_next")) {
				if (index < (c_ct - 1)) {
					index++;
					c.moveToPosition(index);
				} else {
					show_number_insertions.setText("SHOWING ENTRY "
							+ (index + 1) + " OF " + c_ct
							+ " TOTAL. NO NEXT ENRTY.");
					results.setText("RESULTS: doesn't exist.");
					return;
				}
			}
			if (opt.equals("get_last")) {
				if (index > 0) {
					index--;
					c.moveToPosition(index);
				} else {
					show_number_insertions.setText("SHOWING ENTRY "
							+ (index + 1) + " OF " + c_ct
							+ " TOTAL. NO LAST ENRTY.");
					results.setText("RESULTS: doesn't exist.");
					return;
				}

			}
			alphabet_text.setText(c.getString(0));
			results.setText("RESULTS: got alliterations of letter=" + letter
					+ " for adjective=" + adjective);
			show_number_insertions.setText("SHOWING ENTRY " + (index + 1)
					+ " OF " + c_ct + " TOTAL.");
		} else {
			results.setText("RESULTS: NO RESULTS FOR letter=" + letter
					+ " , adjective=" + adjective);
			alphabet_text.setText("");
		}
		c.close();
		showComplete();
	}

	public void showComplete() {
		String alp_complete_text = "";
		for (int i = 0; i < 26; i++) {
			c2 = MainLfqActivity.getMiscDb().rawQuery(
					"SELECT " + alphabet.Entry + " FROM " + tables.alphabet + " WHERE " + alphabet.Table_name + "='" + adjective + "' AND " + alphabet.Letter + "='" + alp.charAt(i) + "'", null);
			if (c2.getCount() == 0) {
				alp_complete_text += alp.charAt(i);
			}
			c2.close();
		}
		if (alp_complete_text.equals("")) {
			show_insertions.setText("ALL COMPLETE.");
		} else {
			show_insertions.setText(alp_complete_text + " ARE INCOMPLETE.");
		}

		c.close();
	}

	public void deleteTable(String table_name, String category) {
		MainLfqActivity.getMiscDb().execSQL(
				"DROP TABLE IF EXISTS '" + table_name + "'");
		values.clear();
		values.put(category, "");
		if (MainLfqActivity.getMiscDb().delete(tables.alphabet_tables,
				category + "=?", new String[] { table_name }) != 0) {
			text = "DELETED TABLE " + table_name + ". ";
		} else {
			text = "TABLE " + table_name + " DOESN'T EXIST. ";
		}
		sql = "UPDATE " + Helpers.db_prefix + "dictionary.alphabettables SET "
				+ category + "='' WHERE " + category + "='" + table_name + "'";
		sql2 = "DROP TABLE IF EXISTS " + Helpers.db_prefix + "alphabetlists."
				+ table_name;
		autosync_text = "";
		// autoSync(sql, db, action, table, name, bool is_image, byte[] image)
		autosync_text += Synchronize.autoSync(sql, "misc_db",
				"update alphabettables delete", table_name, "", false, null);
		autosync_text += "<br />"
				+ Synchronize.autoSync(sql2, "alp_db", "drop table",
						table_name, "", false, null);
		results.setText(Html.fromHtml(text + "<br />" + autosync_text));
		loadSelectAdjectives();
	}

	public void insertTable(String table_name, String category) {
		values.clear();
		values.put(alphabet_tables.Category, category);
		values.put(alphabet_tables.Table_name, table_name);
		long sql_ins = MainLfqActivity.getMiscDb().insert(
				tables.alphabet_tables, null, values);
		sql = "INSERT INTO " + Helpers.db_prefix + "misc."
				+ tables.alphabet_tables + "(" + alphabet_tables.Category + ","
				+ alphabet_tables.Table_name + ") VALUES ('" + category + "','"
				+ table_name + "')";
		if (sql_ins > 0) {
			text = "INSERTED NEW TABLE " + table_name + " INTO " + category
					+ ". ";
		}
		// SYNC TO LFQ:
		autosync_text = "";
		// autoSync(sql, db, action, table, name, bool is_image, byte[] image)
		autosync_text += "<br />"
				+ Synchronize.autoSync(sql, "misc_db", "insert_table",
						table_name, "", false, null);

		results.setText(Html.fromHtml(text + autosync_text));
		loadSelectAdjectives();

	}

	public void editAlphabet(String adjective, String letter, String alpent) {
		int id = index + 1;
		String[] selectionArgs = { Integer.toString(id) };
		MainLfqActivity.getMiscDb().execSQL(
				"UPDATE " + tables.alphabet + " SET " + alphabet.Entry + "='"
						+ alpent + "' WHERE _id=" + id);
		sql = "UPDATE " + Helpers.db_prefix + "misc." + tables.alphabet
				+ " SET " + alphabet.Entry + "='" + alpent + "' WHERE ID=" + id;
		// autoSync(sql, db, action, table, name, bool is_image, byte[] image)
		autosync_text = Synchronize.autoSync(sql, "misc_db", "update",
				adjective + "-" + letter, String.valueOf(id), false, null);
		c = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT " + alphabet.Entry + " FROM " + tables.alphabet
						+ " WHERE _id=?", selectionArgs);
		if (!c.moveToFirst()) {
			results.setText("RESULTS: doesn't exist." + autosync_text);
		} else {
			results.setText("RESULTS: Updated " + adjective + "'s letter,"
					+ letter + "." + autosync_text);
		}
		c.close();
	}

	public void insertAlphabet(String adjective, String letter, String alpent) {
		c = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT " + alphabet.Entry + " FROM " + tables.alphabet
						+ " WHERE " + alphabet.Entry + "='" + alpent + "'",
				null);
		if (c.moveToFirst()) {
			if (c.getString(0).equals(alpent)) {
				results.setText("ENTRY ALREADY EXISTS. NOT UPDATED.");
				return;
			}
		}
		c.close();
		values.clear();
		values.put(alphabet.Table_name, adjective);
		values.put(alphabet.Entry, alpent);
		values.put(alphabet.Letter, letter);
		MainLfqActivity.getMiscDb().insert(tables.alphabet, null, values);
		sql = "INSERT INTO " + Helpers.db_prefix + "misc." + tables.alphabet
				+ " ('" + alphabet.Table_name + "','" + alphabet.Letter + ","
				+ alphabet.Entry + "') VALUES('" + adjective + "','" + letter
				+ "','" + alpent + "')";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "misc_db", "insert",
				adjective + "-" + letter, alpent, false, null);

		c = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT " + alphabet.Entry + " FROM " + tables.alphabet
						+ " WHERE " + alphabet.Entry + "='" + alpent + "'",
				null);
		if (c.moveToFirst()) {
			results.setText("RESULTS: inserted into adjective's, " + adjective
					+ ", letter, " + letter + ". entry:" + c.getString(0) + "."
					+ autosync_text);

		} else {
			results.setText("NOT UPDATED." + autosync_text);
		}
		c.close();
	}

	public void deleteAlphabet(String adjective, String letter, String alpent) {
		// Cursor c_get_empty = MainLfqActivity.getMiscDb().rawQuery(
		// "SELECT _id FROM " + adjective + " WHERE " + letter
		// + "='' ORDER BY _id LIMIT 1", null);
		// if (c_get_empty.moveToFirst()) {
		// id = c_get_empty.getInt(0);
		// }
		// c_get_empty.close();
		MainLfqActivity.getMiscDb().delete(tables.alphabet,
				alphabet.Entry + "=?", new String[] { alpent });
		sql = "DELETE FROM " + Helpers.db_prefix + "misc." + tables.alphabet
				+ " WHERE " + alphabet.Entry + "='" + alpent + "'";
		// autoSync(sql, db, action, table, name, bool is_image, byte[] image)
		autosync_text = Synchronize.autoSync(sql, "misc_db", "delete",
				adjective + "-" + letter, alpent, false, null);
		results.setText("RESULTS: Deleted from : " + adjective
				+ " where letter is " + letter + autosync_text);
	}

	public void getTables(String category) {
		c = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT DISTINCT " + alphabet.Table_name + " FROM "
						+ tables.alphabet_tables + " WHERE "
						+ alphabet_tables.Category + "='" + category
						+ "' ORDER BY " + alphabet.Table_name, null);
		tablesAdapter.clear();
		if (c.moveToFirst()) {
			do {
				tablesAdapter.add(c.getString(0));
			} while (c.moveToNext());
		}
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_alphabet);
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

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void onPostExecute(String file_url) {
			setListeners();
			loadSelectAdjectives();
			results.setText("");
			check_dont_show.setChecked(sharedPref.getBoolean(
					"EDIT ALPHABET CHECK DON'T SHOW", false));
			((RadioButton) findViewById(sharedPref.getInt(
					"EDIT ALPHABET CHOICE", insert_alphabet.getId())))
					.setChecked(true);
			insert_alphabet.setChecked(sharedPref.getBoolean(
					"EDIT ALPHABET CHECK INSERT", false));
			delete_alphabet.setChecked(sharedPref.getBoolean(
					"EDIT ALPHABET CHECK DELETE", false));
			edit_alphabet.setChecked(sharedPref.getBoolean(
					"EDIT ALPHABET CHECK EDIT", false));
			alphabet_text.setText(sharedPref.getString("EDIT ALPHABET INPUT",
					""));
			edit_table_name.setText(sharedPref.getString(
					"EDIT ALPHABET INPUT TABLE NAME", ""));
			select_adjective.setSelection(((ArrayAdapter) select_adjective
					.getAdapter()).getPosition(sharedPref.getString(
					"EDIT ALPHABET SELECT ADJECTIVE", select_adjective
							.getItemAtPosition(0).toString())));
			select_letter.setSelection(((ArrayAdapter) select_letter
					.getAdapter()).getPosition(sharedPref.getString(
					"EDIT ALPHABET SELECT LETTER", select_letter
							.getItemAtPosition(0).toString())));
			select_category.setSelection(((ArrayAdapter) select_category
					.getAdapter()).getPosition(sharedPref.getString(
					"EDIT ALPHABET SELECT CATEGORY", select_category
							.getItemAtPosition(0).toString())));
		}
	}
}