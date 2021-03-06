package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.lfq.learnfactsquick.Constants.cols.mnemonics;
import com.lfq.learnfactsquick.Constants.tables;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditMnemonics extends Activity {
	private LinearLayout scroll, add_remove_layout, edit_mnemonics_top_layout,
			edit_mnemonics_insert_layout, edit_mnemonics_number_entries_layout;
	private Spinner select_mnemonic_table, select_mnemonic_title;
	private TextView results, prompt_mnemonic_title,
			prompt_insert_mnemonic_type;
	private EditText input_mnemonic_table, input_mnemonic_title,
			input_number_mnemonic_entries;
	private CheckBox check_insert_mnemonic_line_breaks,
			check_new_mnemonic_table, check_new_mnemonic_title;
	private RadioButton check_insert_mnemonics_type,
			check_insert_number_mnemonics, check_insert_mnemonic_anagrams,
			check_insert_mnemonic_peglist;
	private RadioButton check_update_mnemonics, check_insert_mnemonics,
			check_delete_mnemonics, check_delete_mnemonic_table;
	private Button do_backup, do_fullscreen, do_edit_mnemonic_table;
	private Button add_mnemonic, remove_mnemonic;
	private ArrayAdapter<String> tablesAdapter, titlesAdapter;
	private int num_entries;
	private String prompt_mnemonic, prompt_word, prompt_info;
	private String[] peglist;
	private LinearLayout.LayoutParams params;
	private String cat_sel, text;
	private HashMap<String, String> text_list;
	private ContentValues cv;
	private List<TextView> tv_mnemonic;
	private List<TextView> tv_word;
	private List<TextView> tv_info;
	private List<EditText> mnemonic;
	private List<EditText> word;
	private List<EditText> info;
	private List<String> mnespl, wordspl, infospl;
	private List<String> titles_entry_numbers;
	private EditText anagram;
	String sav_ent_num;
	private int ent_num;
	private static String status;
	private String sql, sql2;
	private ContentValues values;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	private Helpers h;
	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private String autosync_text;
	private String title, type, linebreak;
	private static Boolean is_database_load;
	private String TAG = EditMnemonics.class.getSimpleName();
	private String saved_table, saved_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = true;
		cv = new ContentValues();
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		status = "before insert";
		saved_table = "";
		saved_title = "";
		num_entries = 0;
		prompt_mnemonic = "";
		prompt_word = "";
		prompt_info = "";
		sql = "";
		sql2 = "";
		type = "";
		titles_entry_numbers = new ArrayList<String>();
		tv_mnemonic = new ArrayList<TextView>();
		tv_word = new ArrayList<TextView>();
		tv_info = new ArrayList<TextView>();
		mnemonic = new ArrayList<EditText>();
		word = new ArrayList<EditText>();
		info = new ArrayList<EditText>();

		mnespl = new ArrayList<String>();
		wordspl = new ArrayList<String>();
		infospl = new ArrayList<String>();

		values = new ContentValues();
		text_list = new HashMap<String, String>();
		peglist = new String[] { "Tea", "New", "Me", "Ear", "Owl", "Gay",
				"Cow", "UFO", "Bee", "Dash", "Dead", "Tuna", "Atom", "Deer",
				"Tale", "Dog", "Duke", "TV", "Tuba", "NASA", "Ant", "Noon",
				"Enemy", "Honor", "Noel", "Wing", "Ink", "Navy", "Newbie",
				"Mouse", "Myth", "Moon", "Memo", "Humor", "Email", "Image",
				"Macho", "Movie", "Amoeba", "Horse", "Rat", "Rain", "Arm",
				"Arrow", "Rail", "Rage", "Rich", "Review", "Robe", "Loose",
				"Old", "Lion", "Lama", "Liar", "Hello", "Leg", "Lake", "Wolf",
				"Loop", "Goose", "Goat", "Gun", "Game", "Gray", "Galaxy",
				"Egg", "Joke", "Goofy", "Jeep", "Cheese", "Cat", "Knee",
				"Coma", "Car", "Cola", "Cage", "Cake", "Cafe", "Chip", "Fish",
				"Fat", "Fun", "Fame", "Fairy", "Fly", "Fog", "Fake", "FIFO",
				"FBI", "Bus", "Bat", "PIN", "Beam", "Bear", "Pool", "Pig",
				"Bike", "Beef", "Babe", "Disease", "Test", "Disney", "Autism",
				"Tzar", "Diesel", "White sage", "Disc", "Satisfy", "Hat shop",
				"Odds", "Daddy", "Titan", "Stadium", "Dexter", "Total",
				"Hot dog", "Attic", "HDTV", "HTTP", "Adonis", "Stunt",
				"Estonian", "Autonomy", "Diner", "Denial", "Stone Age",
				"Dance", "TNF", "Danube", "Times", "Time-out", "Domine",
				"Dummy", "Tumor", "HTML", "Damage", "Stomach", "TMV", "Thumb",
				"Tears", "Druid", "Darwin", "Storm", "Adorer", "Australia",
				"Storage", "Dark", "Dwarf", "Trophy", "Atlas", "Athlete",
				"Italian", "Soda lime", "Hitler", "Dolly", "Dialogue",
				"Italic", "Tea leaf", "Toolbox", "Doghouse", "Widget",
				"Shotgun", "Dogma", "Tiger", "Stagily", "Hedgehog", "Dog hook",
				"Deja vu", "Doughboy", "Steakhouse", "Woodcut", "Technique",
				"Sitcom", "Teacher", "Stokehole", "The Cage", "Duck",
				"Deceive", "Teacup", "TV show", "DVD", "Divine", "The Fame",
				"Stover", "Devil", "Defog", "Device", "Day off", "The F.B.I.",
				"Oedipus", "Tibet", "Headphone", "Tie beam", "Sidebar",
				"Duplex", "Debug", "Topic", "Top-heavy", "Tippy", "News show" };
		new doLoadDatabases().execute();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause called.");
		super.onPause();
		if (is_database_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy called.");
		super.onDestroy();
		if (is_database_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume called.");
		super.onResume();
		if (is_database_load == false) {
			new doLoadDatabases().execute();
		}
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed called.");
		if (is_database_load == false) {
			saveChanges();
			super.onBackPressed();
		}
	}

	@SuppressWarnings("static-access")
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
				&& h.isNetworkAvailable()) {
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

	@SuppressWarnings("static-access")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.autosync_on:
			if (h.isNetworkAvailable()) {
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
		Log.i(TAG, "saveChanges called.");
		editor.putString("EDIT MNEMONICS STATUS", status);
		if (status.equals("begin input")) {
			for (int i = 0; i < mnemonic.size(); i++) {
				if (mnemonic.get(i) != null) {
					editor.putString("EDIT MNEMONICS MNEMONIC " + i, mnemonic
							.get(i).getText().toString());
				}
			}
			for (int i = 0; i < word.size(); i++) {
				editor.putString("EDIT MNEMONICS WORD " + i, word.get(i)
						.getText().toString());
			}
			for (int i = 0; i < info.size(); i++) {
				editor.putString("EDIT MNEMONICS INFO " + i, info.get(i)
						.getText().toString());
			}
			editor.putString("EDIT MNEMONICS ANAGRAM", anagram.getText()
					.toString());
		}
		editor.putBoolean("EDIT MNEMONICS CHECK INSERT LINEBREAKS",
				check_insert_mnemonic_line_breaks.isChecked());
		editor.putBoolean("EDIT MNEMONICS CHECK NEW TABLE",
				check_new_mnemonic_table.isChecked());
		editor.putBoolean("EDIT MNEMONICS CHECK NEW TITLE",
				check_new_mnemonic_title.isChecked());
		editor.putString("EDIT MNEMONICS TITLE INPUT", input_mnemonic_title
				.getText().toString());
		editor.putString("EDIT MNEMONICS NUMBER ENTRIES INPUT",
				input_number_mnemonic_entries.getText().toString());
		editor.putString("EDIT MNEMONICS TABLE INPUT", input_mnemonic_table
				.getText().toString());
		editor.putString("EDIT MNEMONICS SELECT TABLE", select_mnemonic_table
				.getSelectedItem().toString());
		// FOR HOW EDITING:
		editor.putBoolean("EDIT MNEMONICS CHECK UPDATE MNEMONICS",
				check_update_mnemonics.isChecked());
		editor.putBoolean("EDIT MNEMONICS CHECK INSERT MNEMONICS",
				check_insert_mnemonics.isChecked());
		editor.putBoolean("EDIT MNEMONICS CHECK DELETE MNEMONICS",
				check_delete_mnemonics.isChecked());
		// FOR MNEMONIC TYPES:
		editor.putBoolean("EDIT MNEMONICS CHECK INSERT MNEMONICS TYPE",
				check_insert_mnemonics.isChecked());
		editor.putBoolean("EDIT MNEMONICS CHECK INSERT NUMBER MNEMONICS",
				check_insert_number_mnemonics.isChecked());
		editor.putBoolean("EDIT MNEMONICS CHECK INSERT MNEMONIC ANAGRAMS",
				check_insert_mnemonic_anagrams.isChecked());
		editor.putBoolean("EDIT MNEMONICS CHECK INSERT MNEMONIC PEGLIST",
				check_insert_mnemonic_peglist.isChecked());

		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		Log.i(TAG, "setViews called.");
		// LAYOUTS:
		scroll = (LinearLayout) findViewById(R.id.mnemonics_scroll_layout);
		add_remove_layout = (LinearLayout) findViewById(R.id.add_remove_mnemonic_layout);
		add_remove_layout.setVisibility(View.GONE);
		edit_mnemonics_top_layout = (LinearLayout) findViewById(R.id.edit_mnemonics_top_layout);
		edit_mnemonics_insert_layout = (LinearLayout) findViewById(R.id.edit_mnemonics_insert_layout);
		edit_mnemonics_number_entries_layout = (LinearLayout) findViewById(R.id.edit_mnemonics_number_entries_layout);

		// BUTTONS:
		do_edit_mnemonic_table = (Button) findViewById(R.id.do_edit_mnemonic_table);
		add_mnemonic = (Button) findViewById(R.id.add_mnemonic);
		remove_mnemonic = (Button) findViewById(R.id.remove_mnemonic);
		do_fullscreen = (Button) findViewById(R.id.do_edit_mnemonic_fullscreen);
		do_backup = (Button) findViewById(R.id.do_edit_mnemonic_backup);
		do_backup.setVisibility(View.GONE);

		// CHECKBOXES:
		check_insert_mnemonic_line_breaks = (CheckBox) findViewById(R.id.check_insert_mnemonic_line_breaks);
		check_new_mnemonic_table = (CheckBox) findViewById(R.id.check_new_mnemonic_table);
		check_new_mnemonic_title = (CheckBox) findViewById(R.id.check_new_mnemonic_title);

		// EDITTEXTS:
		input_mnemonic_title = (EditText) findViewById(R.id.input_mnemonic_title);
		input_number_mnemonic_entries = (EditText) findViewById(R.id.input_number_mnemonic_entries);
		input_mnemonic_table = (EditText) findViewById(R.id.input_mnemonic_table);

		// SPINNERS:
		select_mnemonic_table = (Spinner) findViewById(R.id.select_mnemonic_table);
		tablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		tablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_mnemonic_table.setAdapter(tablesAdapter);
		select_mnemonic_title = (Spinner) findViewById(R.id.select_mnemonic_title);
		titlesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		titlesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_mnemonic_title.setAdapter(titlesAdapter);

		// RADIOBUTTONS:
		check_update_mnemonics = (RadioButton) findViewById(R.id.check_update_mnemonics);
		check_insert_mnemonics = (RadioButton) findViewById(R.id.check_insert_mnemonics);
		check_delete_mnemonics = (RadioButton) findViewById(R.id.check_delete_mnemonics);
		check_delete_mnemonic_table = (RadioButton) findViewById(R.id.check_delete_mnemonic_table);

		check_insert_mnemonics_type = (RadioButton) findViewById(R.id.check_insert_mnemonics_type);
		check_insert_number_mnemonics = (RadioButton) findViewById(R.id.check_insert_number_mnemonics);
		check_insert_mnemonic_anagrams = (RadioButton) findViewById(R.id.check_insert_mnemonic_anagrams);
		check_insert_mnemonic_peglist = (RadioButton) findViewById(R.id.check_insert_mnemonic_peglist);

		// RADIOGROUPS:

		// SCROLLVIEWS:

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.edit_mnemonics_results);
		prompt_mnemonic_title = (TextView) findViewById(R.id.prompt_mnemonic_title);
	}

	public void loadButtons() {
		do_edit_mnemonic_table.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		do_fullscreen.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		add_mnemonic.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		remove_mnemonic.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		Log.i(TAG, "setListeners called.");
		input_number_mnemonic_entries.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				if (input_number_mnemonic_entries.getText().toString().length() > 0) {
					startInsert();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

		});
		do_fullscreen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				edit_mnemonics_top_layout.setVisibility(View.GONE);
				do_backup.setVisibility(View.VISIBLE);
				do_fullscreen.setVisibility(View.GONE);
			}
		});
		do_backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				edit_mnemonics_top_layout.setVisibility(View.VISIBLE);
				do_backup.setVisibility(View.GONE);
				do_fullscreen.setVisibility(View.VISIBLE);
			}
		});
		check_new_mnemonic_table.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setTableVisibility();
			}
		});
		check_new_mnemonic_title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setTitleVisibility();
			}
		});
		check_update_mnemonics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
				getEntry();
			}
		});
		check_insert_mnemonics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
				startInsert();
			}
		});
		check_delete_mnemonics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
			}
		});
		check_delete_mnemonic_table
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						results.setText("");
						setVisibilities();
					}
				});
		add_mnemonic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				num_entries++;
				if (!type.equals("anagram")) {
					mnespl.add("");
				}
				wordspl.add("");
				infospl.add("");
				doAddEntries(type, word.size(), word.size() + 1);
			}
		});
		select_mnemonic_table
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						getTitles();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						getTitles();
					}

				});
		select_mnemonic_title
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (check_update_mnemonics.isChecked()) {
							Log.i(TAG, "getting Entry!");
							getEntry();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						if (check_update_mnemonics.isChecked()) {
							Log.i(TAG, "getting Entry!");
							getEntry();
						}
					}

				});

		remove_mnemonic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("infospl size=" + infospl.size());
				System.out.println("wordspl size=" + wordspl.size());
				System.out.println("mnespl size=" + mnespl.size());
				if (num_entries > 0) {
					num_entries--;
				}
				if (type != "anagram") {
					scroll.removeView(tv_mnemonic.get(tv_mnemonic.size() - 1));
					tv_mnemonic.remove(tv_mnemonic.size() - 1);
					scroll.removeView(mnemonic.get(mnemonic.size() - 1));
					mnemonic.remove(mnemonic.size() - 1);
				}
				scroll.removeView(tv_word.get(tv_word.size() - 1));
				tv_word.remove(tv_word.size() - 1);
				scroll.removeView(word.get(word.size() - 1));
				word.remove(word.size() - 1);
				scroll.removeView(tv_info.get(tv_info.size() - 1));
				tv_info.remove(tv_info.size() - 1);
				scroll.removeView(info.get(info.size() - 1));
				info.remove(info.size() - 1);
				if (!type.equals("anagram")) {
					if (mnespl.size() > 0) {
						mnespl.remove(mnespl.size() - 1);
					}
				}
				if (wordspl.size() > 0) {
					wordspl.remove(wordspl.size() - 1);
				}
				if (infospl.size() > 0) {
					infospl.remove(infospl.size() - 1);
				}
			}
		});

		do_edit_mnemonic_table.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				results.setText("");
				if (check_delete_mnemonics.isChecked()) {
					deleteMnemonic();
				} else if (check_insert_mnemonics.isChecked()) {
					insertMnemonic();
				} else if (check_delete_mnemonic_table.isChecked()) {
					deleteTable();
				} else if (check_update_mnemonics.isChecked()) {
					updateTable();
				}
			}
		});

	}

	public void doAddEntries(String type, int start, int end) {
		Log.i(TAG, "doAddEntries called!");
		for (int i = start; i < end; i++) {
			int j = i + 1;
			System.out.println("mnemonic size=" + mnemonic.size()
					+ " mnespl size=" + mnespl.size());
			System.out.println("word size=" + word.size() + " wordspl size="
					+ wordspl.size());
			System.out.println("info size=" + info.size() + " infospl size="
					+ infospl.size());
			if (type.equals("mnemonic") || type.equals("number_mnemonic")) {
				tv_mnemonic.add(new TextView(this_act));
				tv_mnemonic.get(i).setText(j + ") " + prompt_mnemonic);
				scroll.addView(tv_mnemonic.get(i));
			}
			if (type.equals("peglist")) {
				tv_mnemonic.add(new TextView(this_act));
				tv_mnemonic.get(i).setText(
						j + ") Pegword Mnemonics(" + peglist[i] + "#" + (i + 1)
								+ ")");
				scroll.addView(tv_mnemonic.get(i));
			}
			if (!type.equals("anagram")) {
				mnemonic.add(new EditText(this_act));
				mnemonic.get(i).setText(mnespl.get(i));
				mnemonic.get(i).setBackgroundResource(
						R.drawable.rounded_edittext_red);
				scroll.addView(mnemonic.get(i), params);
			}
			tv_word.add(new TextView(this_act));

			if (!type.equals("anagram")) {
				tv_word.get(i).setText(prompt_word);
			}
			if (type.equals("anagram")) {
				tv_word.get(i).setText(j + ") " + prompt_word);
			}
			scroll.addView(tv_word.get(i));
			word.add(new EditText(this_act));
			word.get(i).setText(wordspl.get(i));
			word.get(i).setBackgroundResource(R.drawable.rounded_edittext_red);
			word.get(i).setId((4 * num_entries) + i);
			scroll.addView(word.get(i), params);
			tv_info.add(new TextView(this_act));
			tv_info.get(i).setText(prompt_info);
			scroll.addView(tv_info.get(i));
			info.add(new EditText(this_act));
			info.get(i).setText(infospl.get(i));
			info.get(i).setBackgroundResource(R.drawable.rounded_edittext_red);
			scroll.addView(info.get(i), params);
		}// end for loop
	}

	public void getTables() {
		Log.i(TAG, "getTables called.");
		Cursor c_mne_tabs = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT DISTINCT " + mnemonics.Category + " FROM "
						+ tables.mnemonics + " ORDER BY " + mnemonics.Category,
				null);
		tablesAdapter.clear();
		if (c_mne_tabs.moveToFirst()) {
			do {
				tablesAdapter.add(c_mne_tabs.getString(0));
			} while (c_mne_tabs.moveToNext());
		}

	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_mnemonics);
			setTitle("MNEMONICS!");
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
			setListeners();
			getTables();
			check_insert_mnemonic_line_breaks.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK INSERT LINEBREAKS", false));
			input_mnemonic_title.setText(sharedPref.getString(
					"EDIT MNEMONICS TITLE INPUT", ""));
			input_number_mnemonic_entries.setText(sharedPref.getString(
					"EDIT MNEMONICS NUMBER ENTRIES INPUT", ""));
			input_mnemonic_table.setText(sharedPref.getString(
					"EDIT MNEMONICS TABLE INPUT", ""));
			select_mnemonic_table
					.setSelection(((ArrayAdapter) select_mnemonic_table
							.getAdapter()).getPosition(sharedPref.getString(
							"EDIT MNEMONICS SELECT TABLE",
							select_mnemonic_table.getItemAtPosition(0)
									.toString())));
			// SET UPDATE RADIOS:
			check_update_mnemonics.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK UPDATE MNEMONICS", false));
			check_insert_mnemonics.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK INSERT MNEMONICS", false));
			check_delete_mnemonics.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK DELETE MNEMONICS", false));
			// -----------------------------------------------------------

			check_insert_mnemonics_type.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK UPDATE MNEMONICS TYPE", false));
			check_insert_number_mnemonics.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK INSERT NUMBER MNEMONICS", false));
			check_insert_mnemonic_anagrams.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK INSERT MNEMONIC ANAGRAMS", false));
			check_insert_mnemonic_peglist.setChecked(sharedPref.getBoolean(
					"EDIT MNEMONICS CHECK INSERT MNEMONIC PEGLIST", false));
			/*
			 * if (sharedPref.getString("EDIT MNEMONICS STATUS", "").equals(
			 * "begin input")) { startInsert(); for (int i = 0; i <
			 * mnemonic.size(); i++) { if (mnemonic.get(i) != null) {
			 * mnemonic.get(i).setText(
			 * sharedPref.getString("EDIT MNEMONICS MNEMONIC " + i, "")); } }
			 * for (int i = 0; i < word.size(); i++) { word.get(i).setText(
			 * sharedPref .getString("EDIT MNEMONICS WORD " + i, "")); } for
			 * (int i = 0; i < info.size(); i++) { info.get(i).setText(
			 * sharedPref .getString("EDIT MNEMONICS INFO " + i, "")); }
			 * anagram.setText(sharedPref.getString("EDIT MNEMONICS ANAGRAM",
			 * "")); }
			 */
			setVisibilities();
			is_database_load = false;
		}

	}

	public void startInsert() {
		Log.i(TAG, "startInsert called.");
		add_remove_layout.setVisibility(View.GONE);
		scroll.removeAllViews();
		tv_mnemonic.clear();
		tv_word.clear();
		tv_info.clear();
		mnemonic.clear();
		word.clear();
		info.clear();
		mnespl.clear();
		wordspl.clear();
		infospl.clear();
		if (input_number_mnemonic_entries.getText().toString().equals("")) {
			results.setText("NEED TO ENTER 'Number Of Entries'\n");
		} else {
			num_entries = Integer.parseInt(input_number_mnemonic_entries
					.getText().toString());
		}
		if (input_mnemonic_title.getText().toString().equals("")) {
			results.setText("NEED TO ENTER 'Title'");
		}
		if (input_number_mnemonic_entries.getText().toString().equals("")
				|| input_mnemonic_title.getText().toString().equals("")) {
			return;
		}
		if (!check_insert_mnemonics.isChecked()
				&& !check_insert_number_mnemonics.isChecked()
				&& !check_insert_mnemonic_anagrams.isChecked()
				&& !check_insert_mnemonic_peglist.isChecked()) {
			results.setText("NEED TO SELECT WHICH KIND OF MNEMONICS");
			return;
		}
		status = "begin input";
		// BEGIN
		prompt_mnemonic = "";
		prompt_word = "Word to learn:";
		prompt_info = "Word to learn information:";
		anagram = new EditText(this_act);

		if (check_insert_mnemonics.isChecked()) {
			prompt_mnemonic = "Mnemonic(Word with first letters matching word to learn):";
		}
		if (check_insert_number_mnemonics.isChecked()) {
			prompt_mnemonic = "Mnemonic Number(Number to remember):";
			prompt_word = "Word that represents number(use major system*):";
		}
		if (check_insert_mnemonic_anagrams.isChecked()) {
			TextView prompt_anagram = new TextView(this_act);
			prompt_anagram
					.setText("Anagram Word(s) **Captilize letters that are first letters of your words, otherwise lower-case them):");
			// ADD VIEW 0
			scroll.addView(prompt_anagram, params);
			//
			anagram.setBackgroundResource(R.drawable.rounded_edittext_red);

			// ADD VIEW 1
			scroll.addView(anagram, params);
			//
			prompt_word = "Word(from a letter of the *above anagram to learn):";
		}

		for (int i = 0; i < num_entries; i++) {
			int j = i + 1;
			if (check_insert_mnemonics.isChecked()
					|| check_insert_number_mnemonics.isChecked()) {
				tv_mnemonic.add(new TextView(this_act));
				tv_mnemonic.get(i).setText(j + ") " + prompt_mnemonic);
				tv_mnemonic.get(i).setId(num_entries + i);
				scroll.addView(tv_mnemonic.get(i));
			}
			if (check_insert_mnemonic_peglist.isChecked()) {
				tv_mnemonic.add(new TextView(this_act));
				tv_mnemonic.get(i).setText(
						j + ") Pegword Mnemonics(" + peglist[i] + ")#"
								+ (i + 1));
				tv_mnemonic.get(i).setId(num_entries + i);
				scroll.addView(tv_mnemonic.get(i), params);
			}
			if (!check_insert_mnemonic_anagrams.isChecked()) {
				mnemonic.add(new EditText(this_act));

				mnemonic.get(i).setBackgroundResource(
						R.drawable.rounded_edittext_red);
				scroll.addView(mnemonic.get(i), params);
			}
			tv_word.add(new TextView(this_act));

			if (!check_insert_mnemonic_anagrams.isChecked()) {
				tv_word.get(i).setText(prompt_word);
			}
			if (check_insert_mnemonic_anagrams.isChecked()) {
				tv_word.get(i).setText(j + ") " + prompt_word);
			}
			scroll.addView(tv_word.get(i), params);
			word.add(new EditText(this_act));
			word.get(i).setBackgroundResource(R.drawable.rounded_edittext_red);
			scroll.addView(word.get(i), params);
			tv_info.add(new TextView(this_act));
			tv_info.get(i).setText(prompt_info);
			scroll.addView(tv_info.get(i));
			info.add(new EditText(this_act));
			info.get(i).setBackgroundResource(R.drawable.rounded_edittext_red);
			scroll.addView(info.get(i), params);
		}
	}

	public void getTitles() {
		Log.i(TAG, "getTitles called.");
		status = "get titles";
		cat_sel = select_mnemonic_table.getSelectedItem().toString();
		Cursor c_get = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT * FROM " + tables.mnemonics + " WHERE "
						+ mnemonics.Category + "='" + cat_sel + "' GROUP BY "
						+ mnemonics.Entry_Number + " ORDER BY "
						+ mnemonics.Entry_Number, null);
		System.out.println("NUMBER OF TITLES=" + c_get.getCount());
		if (c_get.moveToFirst()) {
			System.out.println("c_get moved to first!!!");
			System.out.println("cat_sel=" + cat_sel);
			titlesAdapter.clear();
			titles_entry_numbers.clear();
			do {
				// type =
				// c_get.getString(c_get.getColumnIndex("Mnemonic_Type"));
				// System.out.println("Mnemonic_Type=" + type);
				title = c_get.getString(c_get.getColumnIndex(mnemonics.Title));
				titles_entry_numbers.add(c_get.getString(c_get
						.getColumnIndex(mnemonics.Entry_Number)));
				System.out.println("Title=" + title);
				titlesAdapter.add(title);

			} while (c_get.moveToNext());
			c_get.close();
			results.setText("Got entries for " + cat_sel);
		}// END MOVE TO FIRST
	}

	public void getEntry() {
		Log.i(TAG, "getEntry called!");
		sav_ent_num = titles_entry_numbers.get(select_mnemonic_title
				.getSelectedItemPosition());
		status = "begin update";
		cat_sel = select_mnemonic_table.getSelectedItem().toString();
		Cursor c_get1 = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT * FROM " + tables.mnemonics + " WHERE "
						+ mnemonics.Category + "='" + cat_sel + "' AND "
						+ mnemonics.Entry_Number + "='" + sav_ent_num + "'",
				null);
		Log.i(TAG, "cget1=" + "SELECT * FROM " + tables.mnemonics + " WHERE "
				+ mnemonics.Category + "='" + cat_sel + "' AND "
				+ mnemonics.Entry_Number + "='" + sav_ent_num + "'");
		if (c_get1.moveToFirst()) {
			// 0=TITLE, 1=TYPE, 2=LINEBREAK, 3=MNEMONICS,
			// 4=WORDS, 5=INFO
			scroll.removeAllViews();
			num_entries = c_get1.getCount();
			Log.i(TAG, "num_entries=" + num_entries);
			title = c_get1.getString(c_get1.getColumnIndex(mnemonics.Title));
			input_mnemonic_title.setText(title);
			type = c_get1.getString(c_get1
					.getColumnIndex(mnemonics.Mnemonic_Type));
			String linebreak = c_get1.getString(c_get1
					.getColumnIndex(mnemonics.Is_Linebreak));
			prompt_mnemonic = "";
			prompt_word = "Word to learn:";
			prompt_info = "Word to learn information:";
			anagram = new EditText(this_act);
			if (linebreak.equals("1")) {
				check_insert_mnemonic_line_breaks.setChecked(true);
			} else {
				check_insert_mnemonic_line_breaks.setChecked(false);
			}
			if (type.equals("mnemonic")) {
				prompt_mnemonic = "Mnemonic(Word with first letters matching word to learn):";
			}
			if (type.equals("number_mnemonic")) {
				prompt_mnemonic = "Mnemonic Number(Number to remember):";
				prompt_word = "Word that represents number(use major system*):";
			}
			if (type.equals("anagram")) {
				TextView prompt_anagram = new TextView(this_act);
				prompt_anagram
						.setText("Anagram Word(s) **Captilize letters that are first letters of your words, otherwise lower-case them):");
				// ADD VIEW 0
				scroll.addView(prompt_anagram, params);
				//
				anagram.setBackgroundResource(R.drawable.rounded_edittext_red);
				anagram.setText(c_get1.getString(c_get1
						.getColumnIndex(mnemonics.Entry_Mnemonic)));
				// ADD VIEW 1
				scroll.addView(anagram, params);
				//
				prompt_word = "Word(from a letter of the *above anagram to learn):";
			}
			wordspl.clear();
			mnespl.clear();
			infospl.clear();
			do {
				wordspl.add(c_get1.getString(c_get1.getColumnIndex("Entry")));
				mnespl.add(c_get1.getString(c_get1
						.getColumnIndex(mnemonics.Entry_Mnemonic)));
				infospl.add(c_get1.getString(c_get1
						.getColumnIndex(mnemonics.Entry_Info)));
			} while (c_get1.moveToNext());
			tv_mnemonic.clear();
			tv_word.clear();
			tv_info.clear();
			mnemonic.clear();
			word.clear();
			info.clear();
			doAddEntries(type, 0, num_entries);
			add_remove_layout.setVisibility(View.VISIBLE);
		}// end cursor go to first
		c_get1.close();
	}

	public void deleteMnemonic() {
		Log.i(TAG, "deleteMnemonic called.");
		add_remove_layout.setVisibility(View.GONE);
		cat_sel = select_mnemonic_table.getSelectedItem().toString();
		sav_ent_num = titles_entry_numbers.get(select_mnemonic_title
				.getSelectedItemPosition());
		MainLfqActivity.getMiscDb().delete(tables.mnemonics,
				mnemonics.Category + "=? AND " + mnemonics.Entry_Number + "=?",
				new String[] { cat_sel, String.valueOf(sav_ent_num) });
		results.setText("DELETED from " + cat_sel + ".");
		sql = "DELETE FROM " + Helpers.db_prefix + "dictionary."
				+ tables.mnemonics + " WHERE " + mnemonics.Category + "='"
				+ cat_sel + "' AND " + mnemonics.Entry_Number + "='"
				+ sav_ent_num + "'";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text = Synchronize.autoSync(sql, "mne_db", "delete", cat_sel,
				String.valueOf(sav_ent_num), false, null);
		getTables();
		getTitles();
	}

	public void insertMnemonic() {
		Log.i(TAG, "insertMnemonic called.");
		// ENTER TITLE:
		add_remove_layout.setVisibility(View.GONE);
		title = input_mnemonic_title.getText().toString();
		String type = "";
		if (check_insert_mnemonics.isChecked()) {
			type = "mnemonic";
		}
		if (check_insert_number_mnemonics.isChecked()) {
			type = "number_mnemonic";
		}
		if (check_insert_mnemonic_anagrams.isChecked()) {
			type = "anagram";
		}
		if (check_insert_mnemonic_peglist.isChecked()) {
			type = "peglist";
		}
		linebreak = "0";
		if (check_insert_mnemonic_line_breaks.isChecked()) {
			linebreak = "1";
		}
		if (check_new_mnemonic_table.isChecked()) {
			cat_sel = input_mnemonic_table.getText().toString();
		} else {
			cat_sel = select_mnemonic_table.getSelectedItem().toString();
		}
		// GET MAX Entry_Number, THEN INCREMENT:
		Cursor c_max = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT MAX(" + mnemonics.Entry_Number + ") AS MAX_NUM FROM "
						+ tables.mnemonics + " WHERE " + mnemonics.Category
						+ "=?", new String[] { cat_sel });
		int ent_num_max = 0;
		if (c_max.moveToFirst()) {
			ent_num_max = c_max.getInt(c_max.getColumnIndex("MAX_NUM"));
		}
		ent_num_max++;
		cv.clear();
		cv.put(mnemonics.Title, title);
		cv.put(mnemonics.Mnemonic_Type, type);
		cv.put(mnemonics.Category, cat_sel);
		cv.put(mnemonics.Entry_Number, ent_num_max);
		if (check_insert_mnemonic_anagrams.isChecked()) {
			cv.put(mnemonics.Entry_Mnemonic, anagram.getText().toString());
		}
		String ent = "", ent_mne = "", ent_info = "";
		int j = 0;
		autosync_text = "";
		long num_app_ins = 0;
		for (int i = 0; i < num_entries; i++) {
			j = i + 1;
			ent = word.get(i).getText().toString();
			ent_mne = mnemonic.get(i).getText().toString();
			ent_info = info.get(i).getText().toString();
			cv.put(mnemonics.Entry, ent);
			cv.put(mnemonics.Entry_Mnemonic, ent_mne);
			cv.put(mnemonics.Entry_Info, ent_info);
			cv.put(mnemonics.Entry_Index, j);
			num_app_ins = MainLfqActivity.getMiscDb().insert(tables.mnemonics,
					null, cv);
			if (num_app_ins == 0) {
				Log.e(TAG, "INSERT MNEMONIC ERROR");
			}
			// SYNC TO LFQ DATABASE:
			sql = "INSERT INTO " + Helpers.db_prefix + "dictionary."
					+ tables.mnemonics + "(" + mnemonics.Title + ","
					+ mnemonics.Mnemonic_Type + "," + mnemonics.Is_Linebreak
					+ "," + mnemonics.Entry + "," + mnemonics.Entry_Mnemonic
					+ "," + mnemonics.Entry_Info + "," + mnemonics.Entry_Number
					+ "," + mnemonics.Entry_Index + ")" + " VALUES('" + title
					+ "','" + type + "','" + linebreak + "','" + ent + "','"
					+ ent_mne + "','" + ent_info + "','" + ent_num_max + "','"
					+ j + "')";
			// autoSync(sql, db, action, table, name, bool is_image,
			// byte[]
			// image)
			autosync_text += Synchronize.autoSync(sql, "mne_db", "insert",
					cat_sel, "", false, null);
		}
		getTables();
		getTitles();
		results.setText("INSERTED " + title + ".");
		System.out.println("INSERT MNEMONIC autosync_text=" + autosync_text);
	}

	public void deleteTable() {
		Log.i(TAG, "deleteTable called.");
		add_remove_layout.setVisibility(View.GONE);
		cat_sel = select_mnemonic_table.getSelectedItem().toString();
		long num_app_del = 0;
		num_app_del = MainLfqActivity.getMiscDb().delete(tables.mnemonics,
				mnemonics.Category + "=?", new String[] { cat_sel });
		if (num_app_del == 0) {
			Log.e(TAG, "ERROR DELETE MNEMNONIC TABLE!");
		}
		sql = "DELETE FROM " + Helpers.db_prefix + "dictionary."
				+ tables.mnemonics + " WHERE " + mnemonics.Category + "='"
				+ cat_sel + "'";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "mne_db", "delete_category",
				cat_sel, "", false, null);
		results.setText("DELETED table " + cat_sel + "." + autosync_text);
		getTables();
	}

	public void updateTable() {
		// mnemonics:ID, Category, Entry_Number, Entry_Index, Entry,
		// Entry_Mnemonic, Entry_Info, Mnemonic_Type, Title,
		// Is_Linebreak
		Log.i(TAG, "updateTable called");
		String old_cat_sel = select_mnemonic_table.getSelectedItem().toString();
		String old_title = select_mnemonic_title.getSelectedItem().toString();
		sav_ent_num = titles_entry_numbers.get(select_mnemonic_title
				.getSelectedItemPosition());
		if (check_new_mnemonic_table.isChecked()) {
			cat_sel = input_mnemonic_table.getText().toString();
		} else {
			cat_sel = select_mnemonic_table.getSelectedItem().toString();
		}
		if (check_new_mnemonic_title.isChecked()) {
			title = input_mnemonic_title.getText().toString();
		} else {
			title = select_mnemonic_title.getSelectedItem().toString();
		}
		Cursor c_get1 = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT * FROM " + tables.mnemonics + " WHERE "
						+ mnemonics.Category + "='" + old_cat_sel + "' AND "
						+ mnemonics.Entry_Number + "='" + sav_ent_num + "'",
				null);
		cv.clear();
		if (c_get1.moveToFirst()) {
			type = c_get1.getString(c_get1
					.getColumnIndex(mnemonics.Mnemonic_Type));
			if (check_insert_mnemonic_line_breaks.isChecked()) {
				linebreak = c_get1.getString(c_get1
						.getColumnIndex(mnemonics.Is_Linebreak));
			}
			cv.put(mnemonics.Category, cat_sel);
			cv.put(mnemonics.Title, title);
			cv.put(mnemonics.Mnemonic_Type, type);
			cv.put(mnemonics.Is_Linebreak, linebreak);
			// mnemonic,word,info(List<String>):...
			String ent = "", ent_mne = "", ent_info = "";
			int i = 0, j = 1;
			autosync_text = "";
			int num_app_upds = 0;
			do {
				ent = word.get(i).getText().toString();
				ent_mne = mnemonic.get(i).getText().toString();
				ent_info = info.get(i).getText().toString();
				cv.put(mnemonics.Entry, ent);
				cv.put(mnemonics.Entry_Mnemonic, ent_mne);
				cv.put(mnemonics.Entry_Info, ent_info);
				num_app_upds = MainLfqActivity.getMiscDb().update(
						tables.mnemonics,
						cv,
						mnemonics.Category + "=? AND " + mnemonics.Entry_Number
								+ "=? AND " + mnemonics.Entry_Index + "=?",
						new String[] { cat_sel, String.valueOf(sav_ent_num),
								String.valueOf(j) });
				if (num_app_upds == 0) {
					Log.e(TAG, "ERROR UPDATING MNEMONICS TABLE!");
				}
				sql = "UPDATE " + Helpers.db_prefix + "dictionary."
						+ tables.mnemonics + " SET " + mnemonics.Category
						+ "='" + cat_sel + "', " + mnemonics.Title + "='"
						+ title + "', " + mnemonics.Entry + "='" + ent + "', "
						+ mnemonics.Entry_Mnemonic + "='" + ent_mne + "', "
						+ mnemonics.Entry_Info + "='" + ent_info + "' WHERE "
						+ mnemonics.Category + "='" + old_cat_sel + "' AND "
						+ mnemonics.Entry_Number + "='" + sav_ent_num
						+ "' AND " + mnemonics.Entry_Index + "='" + j + "'";
				// autoSync(sql, db, action, table, name, bool is_image,
				// byte[]
				// image)
				autosync_text += Synchronize.autoSync(sql, "mne_db", "update",
						cat_sel, String.valueOf(sav_ent_num), false, null)
						+ ". ";
				i++;
				j++;
			} while (c_get1.moveToNext());
		}
		// SYNC TO LFQ DATABASE:
		// mnemonics:ID, Category, Entry_Number, Entry_Index, Entry,
		// Entry_Mnemonic, Entry_Info, Mnemonic_Type, Title,
		// Is_Linebreak
		getTables();
		getTitles();
		System.out.println("autosync_text=" + autosync_text);
		results.setText("UPDATED " + title + ".");

	}

	public void setTableVisibility() {
		Log.i(TAG, "setTableVisibility called.");
		if (check_new_mnemonic_table.isChecked()) {
			saved_table = select_mnemonic_table.getSelectedItem().toString();
			select_mnemonic_table.setVisibility(View.GONE);
			input_mnemonic_table.setVisibility(View.VISIBLE);
			if (check_update_mnemonics.isChecked()) {
				input_mnemonic_table.setText(select_mnemonic_table
						.getSelectedItem().toString());
			} else {// FOR INSERT:
				input_mnemonic_table.setText("");
			}

		} else {
			select_mnemonic_table.setVisibility(View.VISIBLE);
			input_mnemonic_table.setVisibility(View.GONE);
		}
	}

	public void setTitleVisibility() {
		Log.i(TAG, "setTitleVisibility called.");
		if (check_new_mnemonic_title.isChecked()) {
			saved_table = select_mnemonic_title.getSelectedItem().toString();
			select_mnemonic_title.setVisibility(View.GONE);
			input_mnemonic_title.setVisibility(View.VISIBLE);
			if (check_update_mnemonics.isChecked()) {
				input_mnemonic_title.setText(select_mnemonic_title
						.getSelectedItem().toString());
			} else {// FOR INSERT:
				input_mnemonic_title.setText("");
			}
		} else {
			select_mnemonic_title.setVisibility(View.VISIBLE);
			input_mnemonic_title.setVisibility(View.GONE);
		}
	}

	public void setVisibilities() {
		if (check_insert_mnemonics.isChecked()
				|| check_update_mnemonics.isChecked()) {
			check_insert_mnemonic_line_breaks.setVisibility(View.VISIBLE);
			add_remove_layout.setVisibility(View.VISIBLE);
			if (check_insert_mnemonics.isChecked()) {
				edit_mnemonics_insert_layout.setVisibility(View.VISIBLE);
				scroll.removeAllViews();
				edit_mnemonics_number_entries_layout
						.setVisibility(View.VISIBLE);
				select_mnemonic_title.setVisibility(View.GONE);
				input_mnemonic_title.setVisibility(View.VISIBLE);
				input_mnemonic_title.setText("");
				check_new_mnemonic_title.setVisibility(View.GONE);
			} else {
				edit_mnemonics_insert_layout.setVisibility(View.GONE);
				edit_mnemonics_number_entries_layout.setVisibility(View.GONE);
				check_new_mnemonic_title.setVisibility(View.VISIBLE);
				setTitleVisibility();
			}
			check_new_mnemonic_table.setVisibility(View.VISIBLE);
			prompt_mnemonic_title.setVisibility(View.VISIBLE);
			setTableVisibility();
		} else {// FOR delete or delete table
			add_remove_layout.setVisibility(View.GONE);
			scroll.removeAllViews();
			edit_mnemonics_insert_layout.setVisibility(View.GONE);
			check_insert_mnemonic_line_breaks.setVisibility(View.GONE);
			edit_mnemonics_number_entries_layout.setVisibility(View.GONE);
			select_mnemonic_table.setVisibility(View.VISIBLE);
			if (check_delete_mnemonic_table.isChecked()) {
				select_mnemonic_title.setVisibility(View.GONE);
				prompt_mnemonic_title.setVisibility(View.GONE);
			} else {
				select_mnemonic_title.setVisibility(View.VISIBLE);
				prompt_mnemonic_title.setVisibility(View.VISIBLE);
			}
			input_mnemonic_table.setVisibility(View.GONE);
			input_mnemonic_title.setVisibility(View.GONE);
			check_new_mnemonic_table.setVisibility(View.GONE);
			check_new_mnemonic_title.setVisibility(View.GONE);
		}
	}
}
