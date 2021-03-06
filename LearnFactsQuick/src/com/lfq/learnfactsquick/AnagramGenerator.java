package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.lfq.learnfactsquick.Constants.tables;
import com.lfq.learnfactsquick.Constants.cols.dictionarya;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnagramGenerator extends Activity {
	private RelativeLayout above_layout;
	private TextView results, load_results;
	private EditText anagram_input, input_number_letters;
	private CheckBox check_all_combos, check_like_vowels,
			check_limited_letters;
	private Button make_anagrams, backup;	
	ArrayAdapter<String> dataAdapter;	

	private String input;
	private String[] worspl;
	private Boolean is_all_combos, is_like_vowels, is_limited_letters;
	private int number_letters, k;
	private String text;
	private List<String> words_list, defs_list;	
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
		words_list = new ArrayList<String>();
		defs_list = new ArrayList<String>();
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
		editor.putString("ANAGRAM GENERATOR INPUT", anagram_input.getText()
				.toString());
		editor.putBoolean("ANAGRAM GENERATOR CHECK ALL COMBOS",
				check_all_combos.isChecked());
		editor.putBoolean("ANAGRAM GENERATOR CHECK LIKE VOWELS",
				check_like_vowels.isChecked());
		editor.putBoolean("ANAGRAM GENERATOR CHECK LIMITED LETTERS",
				check_limited_letters.isChecked());
		editor.putString("ANAGRAM GENERATOR INPUT NUMBER LETTERS",
				input_number_letters.getText().toString());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		above_layout = (RelativeLayout) findViewById(R.id.anagrams_above_layout);
		setTitle("MAKE ANAGRAMS!");
		load_results = (TextView) findViewById(R.id.anagrams_load_results);
		anagram_input = (EditText) findViewById(R.id.anagram_input);
		make_anagrams = (Button) findViewById(R.id.make_anagrams);
		check_all_combos = (CheckBox) findViewById(R.id.check_all_combos);
		check_like_vowels = (CheckBox) findViewById(R.id.check_like_vowels);
		check_limited_letters = (CheckBox) findViewById(R.id.check_limited_letters);
		input_number_letters = (EditText) findViewById(R.id.input_number_letters);
		results = (TextView) findViewById(R.id.show_anagram_results);
		backup = (Button) findViewById(R.id.anagrams_backup);
		backup.setVisibility(View.GONE);
	}

	public void loadButtons() {
		make_anagrams.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		make_anagrams.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				results.setText("");

				new makeAnagrams().execute();

			}
		});

		backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backup.setVisibility(View.GONE);
				above_layout.setVisibility(View.VISIBLE);
			}
		});

	}

	String[] spliceArray(String[] arr, int ind) {
		String[] makarr = new String[arr.length - 1];
		int ct = 0;
		for (int i = 0; i < arr.length; i++) {
			if (i != ind) {
				makarr[ct] = arr[i];
				ct++;
			}
		}

		return makarr;
	}

	String[] explode(String str) {
		String[] arr = new String[str.length()];
		for (int i = 0; i < str.length(); i++) {
			arr[i] = String.valueOf(str.charAt(i));
		}
		return arr;
	}

	String arrToString(String[] arr) {
		String arrstr = "";
		for (int i = 0; i < arr.length; i++) {
			arrstr += arr[i];
		}
		return arrstr;
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.show_anagrams);
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
			anagram_input.setText(sharedPref.getString(
					"ANAGRAM GENERATOR INPUT", ""));
			check_all_combos.setChecked(sharedPref.getBoolean(
					"ANAGRAM GENERATOR CHECK ALL COMBOS", false));
			check_like_vowels.setChecked(sharedPref.getBoolean(
					"ANAGRAM GENERATOR CHECK LIKE VOWELS", false));
			check_limited_letters.setChecked(sharedPref.getBoolean(
					"ANAGRAM GENERATOR CHECK LIMITED LETTERS", false));
			input_number_letters.setText(sharedPref.getString(
					"ANAGRAM GENERATOR INPUT NUMBER LETTERS", ""));
			setListeners();
			results.setText("");
		}
	}

	class makeAnagrams extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			backup.setVisibility(View.VISIBLE);
			above_layout.setVisibility(View.GONE);
			load_results.setText((Html.fromHtml("</b>NOW LOADING...!</b>")));
		}

		@Override
		protected String doInBackground(String... params) {
			input = anagram_input.getText().toString();
			is_all_combos = check_all_combos.isChecked();
			is_like_vowels = check_like_vowels.isChecked();
			is_limited_letters = check_limited_letters.isChecked();
			if (is_limited_letters) {
				number_letters = Integer.parseInt(input_number_letters
						.getText().toString());
			}

			String[] acrspl = explode(input);
			int al = input.length();
			if (is_limited_letters) {
				al = number_letters;
			}
			String vowexp = ".*[aeiou].*";
			boolean matvow = false;
			String newtex = "";
			input = input.toLowerCase(Locale.US);
			if (input.matches(vowexp)) {
				matvow = true;
			}
			String[] input_split = explode(input);
			String sql_search = "";
			String sql_input = input.toUpperCase(Locale.US);
			for (int i = 0; i < input_split.length; i++) {
				sql_input = input.replaceFirst(input_split[i],
						input_split[i].toLowerCase(Locale.US));
			}
			sql_input.replaceAll("[A-Z]", "");
			String[] sql_input_split = explode(sql_input);
			for (int i = 0; i < sql_input_split.length; i++) {
				sql_search += " AND Word LIKE '%" + input_split[i] + "%'";
			}
			words_list.clear();
			defs_list.clear();
			Cursor c_sel = MainLfqActivity.getMiscDb().rawQuery(
					"SELECT " + dictionarya.Word + "," + dictionarya.Definition + " FROM " + tables.dictionarya + " WHERE " + dictionarya.Word + " NOT LIKE '%[.,;-_]%'"
							+ sql_search + " ORDER BY Word", null);
			if (c_sel.moveToFirst()) {
				do {
					words_list
							.add(c_sel.getString(c_sel.getColumnIndex("Word")));
					defs_list.add(c_sel.getString(c_sel
							.getColumnIndex("Definition")));
				} while (c_sel.moveToNext());
			}
			c_sel.close();
			boolean done = false;
			String my_word;
			String acronym2, spcwor;
			String[] acr2spl;
			int word_count = 0, vowct, acrind, ct, wl, start;
			if (matvow || is_like_vowels) {

				for (int inc = 1; inc < 6; inc++) {
					for (int w_ct = 0; w_ct < words_list.size(); w_ct++) {
						my_word = words_list.get(w_ct);
						worspl = explode(my_word.toLowerCase(Locale.US));
						wl = my_word.length();
						vowct = 0;
						ct = 0;
						acrind = 0;
						spcwor = "";
						acronym2 = input;
						for (k = 0; k < wl; k++) {
							if (k != vowct || ct == al) {
								spcwor = spcwor
										+ worspl[k].toLowerCase(Locale.US);
							}
							if (is_all_combos) {
								if (acronym2.contains(worspl[vowct]) == false
										&& k == vowct && ct != al) {
									break;
								}
								if (acronym2.contains(worspl[vowct]) != false
										&& k == vowct && ct != al) {
									acr2spl = explode(acronym2);
									start = acronym2.indexOf(worspl[vowct]);

									acr2spl = spliceArray(acr2spl, start);
									acronym2 = arrToString(acr2spl);
									if ((vowct + inc) < wl) {
										vowct = vowct + inc;
									}
									ct++;
									spcwor = spcwor
											+ worspl[k].toUpperCase(Locale.US);
								}
							}
							if (is_all_combos == false) {
								if (!worspl[vowct].equals(acrspl[acrind])
										&& k == vowct && ct != al) {
									break;
								}
								if (worspl[vowct].equals(acrspl[acrind])
										&& k == vowct && ct != al) {
									if ((vowct + inc) < wl) {
										vowct = vowct + inc;
									}
									if ((acrind + 1) < al) {
										acrind++;
									}
									ct++;
									spcwor = spcwor
											+ worspl[k].toUpperCase(Locale.US);
								}
							}
						}
						if (ct == al) {
							done = true;
							word_count++;
							newtex += word_count + ")" + spcwor;
							if (!defs_list.get(w_ct).equals("")) {
								newtex += "(" + defs_list.get(w_ct) + ")<br />";
							}
							if (defs_list.get(w_ct).equals("")) {
								newtex += "<br />";
							}
							publishProgress(newtex);

						}

					}// end for each word
				}
				if (!done) {
					publishProgress("RESULTS: SORRY TRY AGAIN");
				}

			}

			if (matvow == false && is_like_vowels == false) {

				for (int w_ct = 0; w_ct < words_list.size(); w_ct++) {
					my_word = words_list.get(w_ct).toLowerCase(Locale.US);
					worspl = explode(my_word);
					wl = my_word.length();
					if (wl >= al) {
						ct = 0;
						acrind = 0;
						spcwor = "";
						acronym2 = input;
						for (k = 0; k < wl; k++) {
							if (worspl[k].matches(vowexp) != false || ct == al) {
								spcwor = spcwor
										+ worspl[k].toLowerCase(Locale.US);
							}
							if (is_all_combos) {
								if (acronym2.contains(worspl[k]) != false) {
									ct++;
									acr2spl = explode(acronym2);
									start = acronym2.indexOf(worspl[k]);
									acr2spl = spliceArray(acr2spl, start);
									acronym2 = arrToString(acr2spl);
									spcwor = spcwor
											+ worspl[k].toUpperCase(Locale.US);
									continue;
								}
								if (acronym2.contains(worspl[k]) == false
										&& worspl[k].matches(vowexp) == false
										&& ct != al) {
									break;
								}
							}
							if (is_all_combos == false) {
								if (!worspl[k].equals(acrspl[acrind])
										&& worspl[k].matches(vowexp) == false
										&& ct != al) {
									break;
								}
								if (worspl[k].equals(acrspl[acrind])
										&& ct != al) {
									ct++;
									if ((acrind + 1) < al) {
										acrind++;
									}
									spcwor = spcwor
											+ worspl[k].toUpperCase(Locale.US);
								}
							}
						}
						if (ct == al) {
							done = true;
							word_count++;
							newtex += word_count + ")" + spcwor;
							if (!defs_list.get(w_ct).equals("")) {
								newtex = newtex + "(" + defs_list.get(w_ct)
										+ ")<br />";
							}
							if (defs_list.get(w_ct).equals("")) {
								newtex = newtex + "<br />";
							}
							publishProgress(newtex);

						}

					}
				}// while for each word

				if (!done) {
					publishProgress("RESULTS: SORRY TRY AGAIN", "");
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			results.setText(Html.fromHtml("<b>" + values[0] + "</b>"));
		}

		@Override
		protected void onPostExecute(String file_url) {
			load_results.setText((Html.fromHtml("</b>ALL LOADED!</b>")));

		}

	}

}