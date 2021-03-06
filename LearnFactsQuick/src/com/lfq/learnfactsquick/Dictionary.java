package com.lfq.learnfactsquick;

import java.util.Locale;

import com.lfq.learnfactsquick.Constants.cols.dictionarya;
import com.lfq.learnfactsquick.Constants.tables;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Dictionary extends Activity {
	private TextView prompt_input, results, load_results, prompt_and,
			prompt_or, prompt_nor;
	private EditText dictionary_input_and, dictionary_input_or,
			dictionary_input_nor;
	private CheckBox check_one_word;
	private Button find_words;		
	private String input_and, input_or, input_nor, text;
	private String[] and_spl, or_spl, nor_spl;
	private int and_l, or_l, nor_l;
	private Boolean is_one_word;	
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();		
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
		MenuItem autosync = menu.findItem(R.id.autosync);
		autosync.setVisible(false);
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
		return true;
	}

	public void saveChanges() {
		editor.putString("DICTIONARY INPUT AND", dictionary_input_and.getText()
				.toString());
		editor.putString("DICTIONARY INPUT OR", dictionary_input_or.getText()
				.toString());
		editor.putString("DICTIONARY INPUT NOR", dictionary_input_nor.getText()
				.toString());
		editor.putBoolean("DICTIONARY CHECK ONE WORD",
				check_one_word.isChecked());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		setTitle("SHOW DICTIONARY!");

		// BUTTONS:
		find_words = (Button) findViewById(R.id.find_words);

		// CHECKBOXES:
		check_one_word = (CheckBox) findViewById(R.id.check_find_one_word);

		// EDITTEXTS:
		dictionary_input_and = (EditText) findViewById(R.id.dictionary_input_and);
		dictionary_input_or = (EditText) findViewById(R.id.dictionary_input_or);
		dictionary_input_nor = (EditText) findViewById(R.id.dictionary_input_nor);

		// TEXTVIEWS:
		load_results = (TextView) findViewById(R.id.show_dictionary_load_results);
		results = (TextView) findViewById(R.id.show_dictionary_results);
		prompt_input = (TextView) findViewById(R.id.prompt_word_input);
		prompt_and = (TextView) findViewById(R.id.prompt_dictionary_and);
		prompt_or = (TextView) findViewById(R.id.prompt_dictionary_or);
		prompt_nor = (TextView) findViewById(R.id.prompt_dictionary_nor);
	}

	public void loadButtons() {
		find_words.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		find_words.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new doLoadWords().execute();
			}// onClick
		});// set onClick listener for find_words button

		check_one_word.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setCheckOneWordVisibilities();

			}
		});

	}// end setListeners function

	public void setCheckOneWordVisibilities() {
		if (check_one_word.isChecked()) {
			prompt_and.setVisibility(View.GONE);
			prompt_or.setVisibility(View.GONE);
			prompt_nor.setVisibility(View.GONE);
			dictionary_input_or.setVisibility(View.GONE);
			dictionary_input_nor.setVisibility(View.GONE);
			prompt_input
					.setText(Html
							.fromHtml("<b>FIND WORDS<br />Input a word, '_' can select one character and '%' can select 1 or more characters:</b>"));
		} else {
			prompt_and.setVisibility(View.VISIBLE);
			prompt_or.setVisibility(View.VISIBLE);
			prompt_nor.setVisibility(View.VISIBLE);
			dictionary_input_or.setVisibility(View.VISIBLE);
			dictionary_input_nor.setVisibility(View.VISIBLE);
			prompt_input
					.setText(Html
							.fromHtml("<b>FIND KEYWORDS<br />Separate words by spaces. 'AND' means find all words, 'OR' means find 1 of the words, and 'NOR' means find none of the words.</b>"));
		}
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.show_dictionary);
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
			dictionary_input_and.setText(sharedPref.getString(
					"DICTIONARY INPUT AND", ""));
			dictionary_input_or.setText(sharedPref.getString(
					"DICTIONARY INPUT OR", ""));
			dictionary_input_nor.setText(sharedPref.getString(
					"DICTIONARY INPUT NOR", ""));
			check_one_word.setChecked(sharedPref.getBoolean(
					"DICTIONARY CHECK ONE WORD", false));
			results.setText("");
			if (!dictionary_input_and.equals("")
					&& !dictionary_input_or.equals("")
					&& !dictionary_input_nor.equals("")) {
				find_words.performClick();
			}
			setCheckOneWordVisibilities();
		}

	}

	class doLoadWords extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			input_and = dictionary_input_and.getText().toString();
			input_or = dictionary_input_or.getText().toString();
			input_nor = dictionary_input_nor.getText().toString();
			and_spl = input_and.split("\\s+");
			or_spl = input_or.split("\\s+");
			nor_spl = input_nor.split("\\s+");
			and_l = 0;
			or_l = 0;
			nor_l = 0;
			if (!input_and.equals("")) {
				and_l = and_spl.length;
			}
			if (!input_or.equals("")) {
				or_l = or_spl.length;
			}
			if (!input_nor.equals("")) {
				nor_l = nor_spl.length;
			}
			is_one_word = check_one_word.isChecked();
			if ((and_l < 2 && and_spl[0].length() < 2)
					&& (or_l < 2 && or_spl[0].length() < 2)
					&& (nor_l < 2 && nor_spl[0].length() < 2)) {
				publishProgress(
						"MUST ENTER AT LEAST 1 KEYWORD THAT HAS MORE THAN 1 LETTER.",
						"");
				return null;
			}
			text = "RESULTS:<br />";
			int ct_now = 0;
			String load_text = "";
			if (is_one_word) {
				String[] selectionArgs = { input_and };
				Cursor c = MainLfqActivity.getMiscDb()
						.rawQuery(
								"SELECT " + dictionarya.Word + "," + dictionarya.PartSpeech + "," + dictionarya.Definition + " FROM " + tables.dictionarya + " WHERE " + dictionarya.Word + " LIKE ? ORDER BY " + dictionarya.Word + " LIMIT 1000",
								selectionArgs);
				load_text = "Found " + c.getCount() + " words.";
				if (c.moveToFirst()) {
					do {
						ct_now++;
						text += ct_now + ") "
								+ c.getString(c.getColumnIndex(dictionarya.Word)) + " - "
								+ c.getString(c.getColumnIndex(dictionarya.PartSpeech))
								+ " "
								+ c.getString(c.getColumnIndex(dictionarya.Definition))
								+ "<br />";
						if (ct_now > 500) {
							break;
						}
					} while (c.moveToNext());
				} else {
					load_text = "Sorry no records found.";
				}
				publishProgress(load_text, text);
			}
			if (!is_one_word) {

				String def_expression = "";
				for (int i = 0; i < and_l; i++) {
					def_expression += " " + dictionarya.Definition + " LIKE '% " + and_spl[i]
							+ " %'";
					if (i < (and_l - 1)) {
						def_expression += " AND";
					}

				}

				for (int i = 0; i < or_l; i++) {
					if (i == 0) {
						def_expression += " OR";
					}
					def_expression += " " + dictionarya.Definition + " LIKE '% " + or_spl[i]
							+ " %'";
					if (i < (or_l - 1)) {
						def_expression += " OR";
					}

				}

				for (int i = 0; i < nor_l; i++) {
					if (i == 0) {
						def_expression += " AND";
					}
					def_expression += " " + dictionarya.Definition + " NOT LIKE '% " + nor_spl[i]
							+ " %'";
					if (i < (nor_l - 1)) {
						def_expression += " AND";
					}

				}

				String query = "SELECT " + dictionarya.Word + "," + dictionarya.PartSpeech + "," + dictionarya.Definition + " FROM " + tables.dictionarya + " WHERE"
						+ def_expression
						+ " ORDER BY " + dictionarya.Word + " COLLATE NOCASE LIMIT 300";

				Cursor c_seadic = MainLfqActivity.getMiscDb().rawQuery(query, null);

				System.out.println(query);

				if (c_seadic.moveToFirst()) {
					char beg_let = 0;
					char sav_let = 0;
					int ct_nums = 0;
					do {
						beg_let = c_seadic
								.getString(c_seadic.getColumnIndex(dictionarya.Word))
								.toUpperCase(Locale.US).charAt(0);
						if (ct_now == 0) {
							sav_let = beg_let;
							text += sav_let + ":<br />";
						}
						ct_now++;
						ct_nums++;
						if (sav_let != beg_let) {
							sav_let = beg_let;
							text += "<br /><br />" + sav_let + ":<br />";
						}

						text += ct_now
								+ ")"
								+ c_seadic.getString(c_seadic
										.getColumnIndex(dictionarya.Word))
								+ " - "
								+ c_seadic.getString(c_seadic
										.getColumnIndex(dictionarya.PartSpeech))
								+ " "
								+ c_seadic.getString(c_seadic
										.getColumnIndex(dictionarya.Definition))
								+ "<br />";
						if (ct_nums > 50) {
							ct_nums = 0;
							publishProgress("Loaded " + ct_now + " words.",
									text);
						}
					} while (c_seadic.moveToNext());
				}// if cursor move to first
				else {
					publishProgress("Sorry no records found.", "");
				}
				publishProgress("Loaded " + ct_now + " words.", text);
				c_seadic.close();
			}// if !is_one_word
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			load_results.setText(Html.fromHtml("<b>" + values[0] + "</b>"));
			results.setText(Html.fromHtml("<b>" + values[1] + "</b>"));
		}

		@Override
		protected void onPostExecute(String file_url) {
		}
	}
}