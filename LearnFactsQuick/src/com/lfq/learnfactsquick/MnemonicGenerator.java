package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.lfq.learnfactsquick.Constants.cols.alphabet_tables;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MnemonicGenerator extends Activity {
	private RelativeLayout mne_gen_combos, above_layout;
	private TextView prompt_mne_gen_table, results;
	private Spinner select_mne_gen_adj, select_mne_gen_theme;
	private TableLayout table, mne_gen_header_table;
	private TableRow mne_gen_word_header_row;
	private EditText mne_input;
	private CheckBox check_mne_gen_table;
	private Button do_mne_gen, backup;
	
	private String input, theme, adj_type, tab;
	private List<List<String[]>> comarr;
	private int num_com;
	private Cursor c = null;
	private Cursor c2 = null;
	private String[] alp = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z" };

	private android.widget.RelativeLayout.LayoutParams rlparams;
	private android.widget.TableLayout.LayoutParams tableParams;
	private android.widget.TableRow.LayoutParams rowParams, cellParams;

	private String text;
	private ArrayAdapter<String> themesAdapter, adjsAdapter;
	private int len;
	private String[] inputspl;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		setTitle("MNEMONIC GENERATOR");
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
		editor.putBoolean("MNEMONIC GENERATOR CHECK TABLE",
				check_mne_gen_table.isChecked());
		editor.putString("MNEMONIC GENERATOR INPUT", mne_input.getText()
				.toString());
		editor.putString("MNEMONIC GENERATOR SELECT ADJECTIVE",
				select_mne_gen_adj.getSelectedItem().toString());
		editor.putString("MNEMONIC GENERATOR SELECT THEME",
				select_mne_gen_theme.getSelectedItem().toString());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		// LAYOUTS:
		mne_gen_combos = (RelativeLayout) findViewById(R.id.mne_gen_combos);
		above_layout = (RelativeLayout) findViewById(R.id.mne_gen_above_layout);

		// BUTTONS:
		do_mne_gen = (Button) findViewById(R.id.do_mne_gen);
		backup = (Button) findViewById(R.id.mne_gen_backup);
		backup.setVisibility(View.GONE);

		// CHECKBOXES:
		check_mne_gen_table = (CheckBox) findViewById(R.id.check_mne_gen_table);

		// EDITTEXTS:
		mne_input = (EditText) findViewById(R.id.mne_gen_input);

		// TABLES:
		table = (TableLayout) findViewById(R.id.mne_gen_table);
		mne_gen_header_table = (TableLayout) findViewById(R.id.mne_gen_header_table);

		// TABLEROWS:
		mne_gen_word_header_row = (TableRow) findViewById(R.id.mne_gen_word_header_row);

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.mne_gen_results);
		prompt_mne_gen_table = (TextView) findViewById(R.id.prompt_mne_gen_table);

		// SPINNERS:
		select_mne_gen_adj = (Spinner) findViewById(R.id.select_mne_gen_adj);
		adjsAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		adjsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_mne_gen_adj.setAdapter(adjsAdapter);
		select_mne_gen_theme = (Spinner) findViewById(R.id.select_mne_gen_theme);
		themesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		themesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_mne_gen_theme.setAdapter(themesAdapter);
	}

	public void loadButtons() {
		do_mne_gen.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {

		backup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				above_layout.setVisibility(View.VISIBLE);
				backup.setVisibility(View.GONE);
			}
		});

		do_mne_gen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new doStartLoadMnemonics().execute();
			}

		});

	}

	public void addComboLists() {
		comarr = new ArrayList<List<String[]>>();// DECLARE THE COMBINATION
													// ARRAY...
		List<String[]> com3 = new ArrayList<String[]>();
		com3.add(new String[] { theme, "verb", "noun" });
		comarr.add(com3);
		List<String[]> com4 = new ArrayList<String[]>();
		com4.add(new String[] { adj_type, theme, "verb", "noun" });
		com4.add(new String[] { theme, "verb", "preposition", "noun" });
		com4.add(new String[] { theme, "noun", "adverb", theme });
		comarr.add(com4);
		List<String[]> com5 = new ArrayList<String[]>();
		com5.add(new String[] { theme, "verb", "preposition", adj_type, "noun" });
		comarr.add(com5);
		List<String[]> com6 = new ArrayList<String[]>();
		com6.add(new String[] { theme, "verb", "adverb", "preposition",
				adj_type, "noun" });
		com6.add(new String[] { theme, "verb", "spc,as", adj_type, "spc,as",
				"noun" });
		comarr.add(com6);
		List<String[]> com7 = new ArrayList<String[]>();
		com7.add(new String[] { adj_type, theme, "verb", "adverb",
				"preposition", adj_type, "noun" });
		comarr.add(com7);
		List<String[]> com8 = new ArrayList<String[]>();
		com8.add(new String[] { adj_type, theme, "verb", "adverb",
				"preposition", "adjectivenumber", adj_type, "noun" });
		comarr.add(com8);
		List<String[]> com9 = new ArrayList<String[]>();
		com9.add(new String[] { theme, "verb", "adverb", "preposition",
				"adjectivenumber", adj_type, "noun", "verb", "adverb" });
		comarr.add(com9);
		List<String[]> com10 = new ArrayList<String[]>();
		com10.add(new String[] { adj_type, theme, "verb", "adverb",
				"preposition", "adjectivenumber", adj_type, "noun", "verb",
				"adverb" });
		comarr.add(com10);
		List<String[]> com11 = new ArrayList<String[]>();
		com11.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "adjectivenumber", adj_type, "noun",
				"verb", "adverb" });
		comarr.add(com11);
		List<String[]> com12 = new ArrayList<String[]>();
		com12.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"preposition", "noun", "adverb", "preposition", adj_type,
				"noun", "verb", "adverb" });
		comarr.add(com12);
		List<String[]> com13 = new ArrayList<String[]>();
		com13.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "noun", "conjunction",
				"adjectivenumber", adj_type, "noun", "verb", "adverb" });
		comarr.add(com13);
		List<String[]> com14 = new ArrayList<String[]>();
		com14.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "adjectivenumber", adj_type, "noun",
				"conjunction", "verb", adj_type, "noun", "adverb" });
		comarr.add(com14);
		List<String[]> com15 = new ArrayList<String[]>();
		com15.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "adjectivenumber", adj_type, "noun",
				"conjunction", "verb", "adjectivenumber", adj_type, "noun",
				"adverb" });
		comarr.add(com15);
		List<String[]> com16 = new ArrayList<String[]>();
		com16.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "noun", "conjunction", "verb",
				"preposition", "adjectivenumber", adj_type, "noun", "adverb",
				"preposition", "noun" });
		comarr.add(com16);
		List<String[]> com17 = new ArrayList<String[]>();
		com17.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "adjectivenumber", "noun",
				"conjunction", "verb", "preposition", "adjectivenumber",
				adj_type, "noun", "adverb", "preposition", "noun" });
		comarr.add(com17);
		List<String[]> com18 = new ArrayList<String[]>();
		com18.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "adjectivenumber", adj_type, "noun",
				"conjunction", "verb", "preposition", "adjectivenumber",
				adj_type, "noun", "adverb", "preposition", "noun" });
		comarr.add(com18);
		List<String[]> com19 = new ArrayList<String[]>();
		com19.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "adjectivenumber", adj_type, "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", adj_type, "noun", "verb", "adjectivenumber",
				"noun" });
		comarr.add(com19);
		List<String[]> com20 = new ArrayList<String[]>();
		com20.add(new String[] { "adjectivenumber", adj_type, theme, "verb",
				"adverb", "preposition", "adjectivenumber", adj_type, "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", adj_type, "noun", "verb", "adjectivenumber",
				adj_type, "noun" });
		comarr.add(com20);
		List<String[]> com21 = new ArrayList<String[]>();
		com21.add(new String[] { "adjectivenumber", "adjectivesize", adj_type,
				theme, "verb", "adverb", "preposition", "adjectivenumber",
				adj_type, "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", adj_type, "noun", "verb",
				"adjectivenumber", adj_type, "noun" });
		comarr.add(com21);
		List<String[]> com22 = new ArrayList<String[]>();
		com22.add(new String[] { "adjectivenumber", "adjectivesize", adj_type,
				theme, "verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", adj_type, "noun", "conjunction", "verb",
				"adverb", "preposition", "adjectivenumber", adj_type, "noun",
				"verb", "adjectivenumber", adj_type, "noun" });
		comarr.add(com22);
		List<String[]> com23 = new ArrayList<String[]>();
		com23.add(new String[] { "adjectivenumber", "adjectivesize", adj_type,
				theme, "verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", adj_type, "noun", "conjunction", "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				adj_type, "noun", "verb", "adjectivenumber", adj_type, "noun" });
		comarr.add(com23);
		List<String[]> com24 = new ArrayList<String[]>();
		com24.add(new String[] { "adjectivenumber", "adjectivesize", adj_type,
				theme, "verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", adj_type, "noun", "conjunction", "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				adj_type, "noun", "verb", "adjectivenumber", "adjectivesize",
				adj_type, "noun" });
		comarr.add(com24);
		List<String[]> com25 = new ArrayList<String[]>();
		com25.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize", adj_type,
				"noun", "conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", adj_type, "noun", "verb",
				"adjectivenumber", "adjectivesize", adj_type, "noun" });
		comarr.add(com25);
		List<String[]> com26 = new ArrayList<String[]>();
		com26.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun", "conjunction", "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				adj_type, "noun", "verb", "adjectivenumber", "adjectivesize",
				adj_type, "noun" });
		comarr.add(com26);
		List<String[]> com27 = new ArrayList<String[]>();
		com27.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun", "conjunction", "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun", "verb", "adjectivenumber",
				"adjectivesize", adj_type, "noun" });
		comarr.add(com27);
		List<String[]> com28 = new ArrayList<String[]>();
		com28.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun", "conjunction", "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectivesize", adj_type, "noun" });
		comarr.add(com28);
		List<String[]> com29 = new ArrayList<String[]>();
		com29.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun", "conjunction", "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type, "noun" });
		comarr.add(com29);
		List<String[]> com30 = new ArrayList<String[]>();
		com30.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "noun" });
		comarr.add(com30);
		List<String[]> com31 = new ArrayList<String[]>();
		com31.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type, "noun" });
		comarr.add(com31);
		List<String[]> com32 = new ArrayList<String[]>();
		com32.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun" });
		comarr.add(com32);
		List<String[]> com33 = new ArrayList<String[]>();
		com33.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun" });
		comarr.add(com33);
		List<String[]> com34 = new ArrayList<String[]>();
		com34.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun" });
		comarr.add(com34);
		List<String[]> com35 = new ArrayList<String[]>();
		com35.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "noun" });
		comarr.add(com35);
		List<String[]> com36 = new ArrayList<String[]>();
		com36.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun",
				"conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun" });
		comarr.add(com36);
		List<String[]> com37 = new ArrayList<String[]>();
		com37.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun" });
		comarr.add(com37);
		List<String[]> com38 = new ArrayList<String[]>();
		com38.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "noun" });
		comarr.add(com38);
		List<String[]> com39 = new ArrayList<String[]>();
		com39.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "noun" });
		comarr.add(com39);
		List<String[]> com40 = new ArrayList<String[]>();
		com40.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "noun" });
		comarr.add(com40);
		List<String[]> com41 = new ArrayList<String[]>();
		com41.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape", theme,
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"noun", "conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "noun" });
		comarr.add(com41);
		List<String[]> com42 = new ArrayList<String[]>();
		com42.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape", theme,
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"noun" });
		comarr.add(com42);
		List<String[]> com43 = new ArrayList<String[]>();
		com43.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape", theme,
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape", "noun",
				"verb", "adjectivenumber", "adjectivesize", "adjectiveage",
				adj_type, "adjectiveintensity", "adjectivequality",
				"adjectivecolor", "noun" });
		comarr.add(com43);
		List<String[]> com44 = new ArrayList<String[]>();
		com44.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape", theme,
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape", "noun",
				"verb", "adjectivenumber", "adjectivesize", "adjectiveage",
				adj_type, "adjectiveintensity", "adjectivequality",
				"adjectivecolor", "adjectiveshape", "noun" });
		comarr.add(com44);
		List<String[]> com45 = new ArrayList<String[]>();
		com45.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape", "noun",
				"verb", "adjectivenumber", "adjectivesize", "adjectiveage",
				adj_type, "adjectiveintensity", "adjectivequality",
				"adjectivecolor", "adjectiveshape", "noun" });
		comarr.add(com45);
		List<String[]> com46 = new ArrayList<String[]>();
		com46.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "noun" });
		comarr.add(com46);
		List<String[]> com47 = new ArrayList<String[]>();
		com47.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "noun" });
		comarr.add(com47);
		List<String[]> com48 = new ArrayList<String[]>();
		com48.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", theme, "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun" });
		comarr.add(com48);
		List<String[]> com49 = new ArrayList<String[]>();
		com49.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "noun", "verb", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun" });
		comarr.add(com49);
		List<String[]> com50 = new ArrayList<String[]>();
		com50.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun", "verb",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "noun" });
		comarr.add(com50);
		List<String[]> com51 = new ArrayList<String[]>();
		com51.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "adjectivereligion",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "noun" });
		comarr.add(com51);
		List<String[]> com52 = new ArrayList<String[]>();
		com52.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", theme, "verb",
				"adverb", "preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "adjectivereligion",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun" });
		comarr.add(com52);
		List<String[]> com53 = new ArrayList<String[]>();
		com53.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion",
				"adjectivemadjectivetextureerial", theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun", "conjunction",
				"verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "adjectivereligion",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun" });
		comarr.add(com53);
		List<String[]> com54 = new ArrayList<String[]>();
		com54.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion",
				"adjectivemadjectivetextureerial", theme, "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "adjectivematerial",
				"noun", "conjunction", "verb", "adverb", "preposition",
				"adjectivenumber", "adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "adjectivereligion",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun" });
		comarr.add(com54);
		List<String[]> com55 = new ArrayList<String[]>();
		com55.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "adjectivematerial",
				theme, "verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "adjectivereligion",
				"adjectivematerial", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "adjectivematerial",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "noun" });
		comarr.add(com55);
		List<String[]> com56 = new ArrayList<String[]>();
		com56.add(new String[] { "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "adjectivematerial",
				theme, "verb", "adverb", "preposition", "adjectivenumber",
				"adjectivesize", "adjectiveage", adj_type,
				"adjectiveintensity", "adjectivequality", "adjectivecolor",
				"adjectiveshape", "adjectivetexture", "adjectivereligion",
				"adjectivematerial", "noun", "conjunction", "verb", "adverb",
				"preposition", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "adjectivematerial",
				"noun", "verb", "adjectivenumber", "adjectivesize",
				"adjectiveage", adj_type, "adjectiveintensity",
				"adjectivequality", "adjectivecolor", "adjectiveshape",
				"adjectivetexture", "adjectivereligion", "adjectivematerial",
				"noun" });
		comarr.add(com56);
	}

	public void initializeSpinners() {
		themesAdapter.clear();
		adjsAdapter.clear();
		c = MainLfqActivity.getMiscDb().rawQuery("SELECT DISTINCT " + alphabet_tables.Category + " FROM " + tables.alphabet_tables + " ORDER BY " + alphabet_tables.Category, null);
		String[] catNames = new String[c.getCount()];
		int ct_cats=0;
		if (c.moveToFirst()){
			do{
				catNames[ct_cats++]= c.getString(c.getColumnIndex(alphabet_tables.Category)); 
			}while(c.moveToNext());
		}
		List<String> adj_types = new ArrayList<String>();
		adj_types.addAll(Arrays.asList(new String[] { "Opposites", "Shapes",
				"Times" }));
		List<String> theme_types = new ArrayList<String>();
		theme_types.addAll(Arrays.asList(new String[] { "Colors", "Directions",
				"Materials", "Nationalities", "Number", "Religions" }));
		for (int i = 0; i < catNames.length; i++) {// Add to themes if new Type
													// Table:....
			if (!theme_types.contains(catNames[i])
					&& !adj_types.contains(catNames[i])) {
				if (!catNames[i].equals("_id")) {
					theme_types.add(catNames[i]);
				}
			}
		}
		c.close();
		Cursor c_alp_tabs = MainLfqActivity.getMiscDb().rawQuery(" SELECT name FROM sqlite_master "
				+ " WHERE type='table' ORDER BY name", null);
		List<String> tabs = new ArrayList<String>();
		if (c_alp_tabs.moveToFirst()) {
			do {
				tabs.add(c_alp_tabs.getString(0));

			} while (c_alp_tabs.moveToNext());
		}
		c_alp_tabs.close();
		for (int i = 0; i < theme_types.size(); i++) {
			c = MainLfqActivity.getMiscDb().rawQuery("SELECT " + theme_types.get(i)
					+ " FROM " + tables.alphabettables, null);
			if (c.moveToFirst()) {
				do {
					tab = c.getString(0);
					if (tabs.contains(tab)) {

						c2 = MainLfqActivity.getMiscDb().query(tab, null, null, null, null, null,
								null, "1");
						text = "true";
						if (c2.moveToFirst()) {
							for (int j = 0; j < 26; j++) {
								if (c2.getString(c2.getColumnIndex(alp[j])) == null) {
									text = "false";

								} else {
									if (c2.getString(c2.getColumnIndex(alp[j]))
											.equals("")) {
										text = "false";
									}
								}
							}
							if (text.equals("true")) {
								themesAdapter.add(tab);
							}
						}
						c2.close();
					}

				} while (c.moveToNext());
			}
			c.close();
		}
		for (int i = 0; i < adj_types.size(); i++) {
			c = MainLfqActivity.getMiscDb().rawQuery("SELECT " + adj_types.get(i)
					+ " FROM " + tables.alphabettables, null);
			if (c.moveToFirst()) {
				do {
					tab = c.getString(0);
					if (tabs.contains(tab)) {
						c2 = MainLfqActivity.getMiscDb().query(tab, null, null, null, null, null,
								null, "1");
						text = "true";
						if (c2.moveToFirst()) {
							for (int j = 0; j < 26; j++) {
								if (c2.getString(c2.getColumnIndex(alp[j])) == null) {
									text = "false";

								} else {
									if (c2.getString(c2.getColumnIndex(alp[j]))
											.equals("")) {
										text = "false";
									}
								}
							}
							if (text.equals("true")) {
								adjsAdapter.add(tab);
							}
						}
						c2.close();
					}
				} while (c.moveToNext());
			}
			c.close();
		}
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.mne_gen);
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
			results.setText("");
			setListeners();
			initializeSpinners();
			check_mne_gen_table.setChecked(sharedPref.getBoolean(
					"MNEMONIC GENERATOR CHECK TABLE", false));
			mne_input.setText(sharedPref.getString("MNEMONIC GENERATOR INPUT",
					""));
			select_mne_gen_adj
					.setSelection(adjsAdapter.getPosition(sharedPref.getString(
							"MNEMONIC GENERATOR SELECT ADJECTIVE",
							select_mne_gen_adj.getItemAtPosition(0).toString())));
			select_mne_gen_theme.setSelection(themesAdapter
					.getPosition(sharedPref.getString(
							"MNEMONIC GENERATOR SELECT THEME",
							select_mne_gen_theme.getItemAtPosition(0)
									.toString())));
		}
	}

	class doStartLoadMnemonics extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			above_layout.setVisibility(View.GONE);
			backup.setVisibility(View.VISIBLE);
			results.setText(Html.fromHtml("NOW LOADING...</b>"));
		}

		@Override
		protected String doInBackground(String... params) {
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

		@Override
		protected void onPostExecute(String file_url) {
			doLoadMnemonics();
		}
	}

	public void doLoadMnemonics() {
		input = mne_input.getText().toString();
		input = input.toUpperCase(Locale.US);
		inputspl = input.split(" ");
		len = inputspl.length;
		mne_gen_word_header_row.removeAllViews();
		table.removeAllViews();
		mne_gen_combos.removeAllViews();
		prompt_mne_gen_table
				.setText(Html
						.fromHtml("1ST LETTER <u><b>M</b></u>atches,2ND LETTER <u><b>MA</b></u>tches,3RD LETTER <big><u><b>MAT</b></u></big>ches,4TH LETTER <big><u><b>MATC</b></u>hes</big>,5TH LETTER(OR ALL) <big><u><b>MATCH</b></u>ES</big>"));
		for (int i = 0; i < len; i++) {
			int j = i + 1;
			TextView word_review = new TextView(this_act);
			cellParams = new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT);
			word_review.setText(j + ") " + inputspl[i]);
			mne_gen_word_header_row.addView(word_review, cellParams);
		}
		if (!check_mne_gen_table.isChecked()) {
			// Make mne_gen_combos, a RelativeLayout, visible:
			mne_gen_combos.setVisibility(View.VISIBLE);
			// Make table and Header Table(TableLayouts) invisible:
			table.setVisibility(View.GONE);
			mne_gen_header_table.setVisibility(View.GONE);
		}
		theme = select_mne_gen_theme.getSelectedItem().toString();
		adj_type = select_mne_gen_adj.getSelectedItem().toString();
		if (adj_type.equals("numbers")) {
			adj_type = "adjectivenumber";
		}
		if (adj_type.equals("colors")) {
			adj_type = "adjectivecolor";
		}

		String[] parspesplspc = null;
		String[] tabarr = { theme, "preposition", "conjunction", adj_type,
				"verb", "adv.", "noun", "adj." };
		int tl = tabarr.length;// TOTAL 'COLUMNS' OF TABLE
		String[] tabarrspestr = { "theme(noun)", "preposition", "conjunction",
				"chosen adjective" };
		int tassl = tabarrspestr.length;// NUMBER OF COLUMNS OF SPECIAL
										// THEME, ADJECTIVE, OR OTHER
										// PART OF SPEECH

		String parspe = "", selwor = "";
		adj_type = adj_type.toLowerCase(Locale.US);
		theme = theme.toLowerCase(Locale.US);

		if (!check_mne_gen_table.isChecked()) {
			if (len < 3) {
				return;
			}
			List<String> parspelistspl = null;
			List<String> recspc = new ArrayList<String>();
			int ctspc = 0, ctmat = 0, spcarrct = 0, spcarrl = 0;
			String able = "", spcmat = "", spcone = "";
			// SET IDS TO 0:
			int ct_com_ids = 0;
			int jlen = len - 3;
			addComboLists();
			List<String[]> coms = comarr.get(jlen);
			int cl = coms.size();
			// CREATE COMBINATION TABLES:
			TextView[] com_prompts = new TextView[cl];
			// CREATE COMBINATION TABLES:
			TableLayout[] com_tabs = new TableLayout[cl];
			// CREATE NOT FOUND TEXT VIEWS:
			TextView[] com_not_founds = new TextView[cl];

			// CREATE COMBINATION ROWS:
			TableRow[][] com_rows = new TableRow[cl][len];
			// CREATE COMBINATION INPUTS:
			TextView[][] com_inputs = new TextView[cl][len];
			// CREATE COMBINATION TYPES(PROMPTS):
			TextView[][] com_types = new TextView[cl][len];
			// CREATE COMBINATION WORDS:
			TextView[][] com_words = new TextView[cl][len];

			int psssl = 0;
			int psscl = 0;
			String fouwor = "";

			for (int i = 0; i < cl; i++) {// FOR EACH COMBO OF A COMBO
											// OF WORDS(A NUMBER OF
											// WORDS)
				num_com = i + 1;
				// CREATE AND ADD NEW COM PROMPT:
				com_prompts[i] = new TextView(this_act);
				com_prompts[i].setText(Html.fromHtml("<u><b>COMBINATION "
						+ num_com + "." + "</b></u>"));
				ct_com_ids++;
				com_prompts[i].setId(ct_com_ids);
				rlparams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.MATCH_PARENT);
				if (i != 0) {
					rlparams.addRule(RelativeLayout.BELOW,
							com_tabs[i - 1].getId());
				}
				mne_gen_combos.addView(com_prompts[i], rlparams);

				// CREATE AND ADD NEW COMS TABLE:
				com_tabs[i] = new TableLayout(this_act);
				rlparams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.MATCH_PARENT);
				rlparams.addRule(RelativeLayout.BELOW, com_prompts[i].getId());
				ct_com_ids++;
				com_tabs[i].setId(ct_com_ids);
				mne_gen_combos.addView(com_tabs[i], rlparams);
				String[] parspelist = coms.get(i);
				ctspc = 0;
				ctmat = 0;
				spcarrct = 0;
				able = "yes";
				spcmat = "no";
				// FOR EACH WORD IN INPUT CHECK IF SPC WORDS CAN WORK:
				for (int q = 0; q < len; q++) {
					parspelistspl = Arrays.asList(parspelist[q].split(","));
					if (parspelistspl.get(0).equals("spc")) {
						String[] inputsplspl = Helpers.explode(inputspl[q]);
						parspelistspl.remove(0);
						spcarrl = parspelistspl.size();
						ctspc++;
						spcmat = "yes";
						for (int m = 0; m < spcarrl; m++) {
							spcone = parspelistspl.get(m);
							String[] spcspl = Helpers.explode(spcone);
							if (spcspl[0].toLowerCase(Locale.US).equals(
									inputsplspl[0])) {
								ctmat++;
								spcarrct++;
								recspc.add(spcone);
								break;
							}
						}
					}
				}

				// CHECK IF A-Z IS AVAILABLE FOR EACH PART OF SPEECH:
				for (int l = 0; l < len - 1; l++) {
					parspe = parspelist[l];
					String[] inputsplspl = Helpers.explode(inputspl[l]);
					if (!parspe.equals("spc")) {
						c = MainLfqActivity.getMiscDb().rawQuery("SELECT " + inputsplspl[0]
								+ " FROM " + parspe, null);
						if (c.moveToFirst())
							if (c.getString(0).equals("")) {
								able = "no";
							}
					}
				}
				if (spcmat.equals("yes") && ctspc != ctmat) {
					able = "no";
				}
				if (able.equals("no")) {
					spcarrct = 0;

					for (int n = 0; n < len; n++) {
						int o = n + 1;
						// CREATE AND ADD ROW:
						rowParams = new TableRow.LayoutParams(
								TableRow.LayoutParams.WRAP_CONTENT,
								TableRow.LayoutParams.WRAP_CONTENT);
						com_rows[i][n] = new TableRow(this_act);
						com_tabs[i].addView(com_rows[i][n], rowParams);

						// CREATE AND ADD INPUT WORD TEXT VIEW:
						com_inputs[i][n] = new TextView(this_act);
						com_inputs[i][n].setText(o + ". " + inputspl[n]);
						com_inputs[i][n]
								.setBackgroundResource(R.drawable.cell_shape);
						com_rows[i][n].addView(com_inputs[i][n], rowParams);

						// CREATE AND ADD TYPES TEXT VIEW:
						com_types[i][n] = new TextView(this_act);
						if (!parspelist[n].equals(adj_type)
								&& !parspelist[n].equals(theme)) {
							com_types[i][n].setText(parspelist[n]
									.toUpperCase(Locale.US) + " ");
						}
						if (parspelist[n].equals(adj_type)) {
							com_types[i][n].setText("Chosen Adjective ("
									+ adj_type.toUpperCase(Locale.US) + ") ");
						}
						if (parspelist[n].equals(theme)) {
							com_types[i][n].setText("Chose Theme ("
									+ theme.toUpperCase(Locale.US) + ") ");
						}
						parspelistspl = Arrays.asList(parspelist[n].split(","));
						if (parspelistspl.get(0).equals("spc")) {
							parspelistspl.remove(0);
							spcarrl = parspelistspl.size();
							text = "";
							for (int s = 0; s < spcarrl; s++) {
								text += parspelistspl.get(s).toUpperCase(
										Locale.US)
										+ " ";
							}
							text += ")";
							com_types[i][n].setText(text);
						}
						com_types[i][n]
								.setBackgroundResource(R.drawable.cell_shape);
						com_rows[i][n].addView(com_types[i][n], rowParams);
						//
					}
					com_not_founds[i] = new TextView(this_act);
					rlparams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.MATCH_PARENT);
					rlparams.addRule(RelativeLayout.BELOW, com_tabs[i].getId());
					com_not_founds[i].setText("COMBINATION NOT FOUND");
					ct_com_ids++;
					com_not_founds[i].setId(ct_com_ids);
					mne_gen_combos.addView(com_not_founds[i], rlparams);
				}
				if (able.equals("yes")) {
					spcarrct = 0;
					int k;
					for (int j = 0; j < len; j++) {
						k = j + 1;
						String[] inputsplspl = Helpers.explode(inputspl[j]);
						parspe = parspelist[j];

						// ADD AND CREATE NEW COM ROW:
						com_rows[i][j] = new TableRow(this_act);
						rowParams = new TableRow.LayoutParams(
								TableRow.LayoutParams.WRAP_CONTENT,
								TableRow.LayoutParams.WRAP_CONTENT);
						com_tabs[i].addView(com_rows[i][j], rowParams);

						// ADD AND CREATE NEW INPUT TEXT VIEW:
						rowParams = new TableRow.LayoutParams(150,
								TableRow.LayoutParams.MATCH_PARENT);
						com_inputs[i][j] = new TextView(this_act);
						com_inputs[i][j].setText(k + "." + inputspl[j]);
						ct_com_ids++;
						com_inputs[i][j].setId(ct_com_ids);
						com_inputs[i][j]
								.setBackgroundResource(R.drawable.cell_shape);
						com_rows[i][j].addView(com_inputs[i][j], rowParams);

						// ADD AND CREATE NEW TYPES TEXT VIEW:
						com_types[i][j] = new TextView(this_act);
						if (!parspelist[j].equals(adj_type)
								&& !parspelist[j].equals(theme)) {
							com_types[i][j].setText(parspelist[j]
									.toUpperCase(Locale.US) + " ");
						}
						if (parspelist[j].equals(adj_type)) {
							com_types[i][j].setText("Chosen Adjective ("
									+ adj_type.toUpperCase(Locale.US) + ") ");
						}
						if (parspelist[j].equals(theme)) {
							com_types[i][j].setText("Chosen Theme ("
									+ theme.toUpperCase(Locale.US) + ") ");
						}
						rowParams = new TableRow.LayoutParams(150,
								TableRow.LayoutParams.MATCH_PARENT);
						com_types[i][j]
								.setBackgroundResource(R.drawable.cell_shape);
						com_rows[i][j].addView(com_types[i][j], rowParams);
						// END ADD TYPE TEXT VIEW

						parspelistspl = Arrays.asList(parspelist[j].split(","));
						text = "";
						if (parspelistspl.get(0).equals("spc")) {
							parspelistspl.remove(0);
							spcarrl = parspelistspl.size();
							text = "(";
							for (int s = 0; s < spcarrl; s++) {
								text += parspelistspl.get(s).toUpperCase(
										Locale.US)
										+ " ";
							}
							text += ")";
						}
						if (parspelistspl.get(0).equals("spc")) {
							spcone = recspc.get(spcarrct)
									.toUpperCase(Locale.US);
							String[] spcspl = Helpers.explode(spcone);
							if (spcspl.length > 1) {
								text += spcspl[1] + " ";
							}
							if (spcspl.length > 1 && inputsplspl.length > 1) {
								if (!spcspl[1].equals(inputsplspl[1])) {
									text += spcone + " ";
								}
								if (spcspl[1].equals(inputsplspl[1])) {
									if (spcspl.length > 2
											&& inputsplspl.length > 2) {
										if (!spcspl[2].equals(inputsplspl[2])) {
											text += "<b>" + spcone + "</b> ";
										}
										if (spcspl[2].equals(inputsplspl[2])) {
											if (spcspl.length > 3
													&& inputsplspl.length > 3) {
												if (!spcspl[3]
														.equals(inputsplspl[3])) {
													text += "<b><u>" + spcone
															+ "</b></u> ";
												}
												if (spcspl[3]
														.equals(inputsplspl[3])) {
													if (spcspl.length > 4
															&& inputsplspl.length > 4) {
														if (!spcspl[4]
																.equals(inputsplspl[4])) {
															text += "<big><b><u>"
																	+ spcone
																	+ "</b></u></big> ";
														}
														if (spcspl[4]
																.equals(inputsplspl[4])) {
															text += "<b><u><big>"
																	+ spcone
																	+ "</b></u></big> ";
														}
													}
												}
											}
										}
									}
								}
							}
							spcarrct++;
						}
						if (!parspe.equals("spc")) {
							String fousth = "no";
							if (inputspl[j].length() > 2) {
								c = MainLfqActivity.getMiscDb().rawQuery("SELECT " + inputsplspl[0]
										+ " FROM " + parspe + " WHERE "
										+ inputsplspl[0] + " LIKE '"
										+ inputspl[j].substring(0, 3)
										+ "%' LIMIT 20", null);
								if (c.moveToFirst()) {
									do {
										selwor = c.getString(0);
										if (selwor == null) {
											continue;
										}
										parspesplspc = selwor.split(" ");
										String[] parspesplcha = Helpers
												.explode(parspesplspc[0]);
										psssl = parspesplspc.length;
										psscl = parspesplcha.length;
										fouwor = "no";
										if (parspesplcha.length > 3
												&& inputsplspl.length > 3) {
											if (!parspesplcha[3]
													.toLowerCase(Locale.US)
													.equals(inputsplspl[3]
															.toLowerCase(Locale.US))) {
												text += "<big><b><u>"
														+ parspesplspc[0]
																.substring(0, 3)
																.toUpperCase(
																		Locale.US)
														+ "</u></b></big>";
												fouwor = "yes";
												for (int x = 3; x < psscl; x++) {
													text += parspesplcha[x]
															.toLowerCase(Locale.US);
												}
												text += " ";
											}
											if (parspesplcha[3]
													.toLowerCase(Locale.US)
													.equals(inputsplspl[3]
															.toLowerCase(Locale.US))) {
												if (parspesplcha.length > 4
														&& inputsplspl.length > 4) {
													if (!parspesplcha[4]
															.toLowerCase(
																	Locale.US)
															.equals(inputsplspl[4]
																	.toLowerCase(Locale.US))) {
														text += "<big><b><u>"
																+ parspesplspc[0]
																		.substring(
																				0,
																				4)
																		.toUpperCase(
																				Locale.US)
																+ "</u><b>";
														fouwor = "yes";
														for (int x = 4; x < psscl; x++) {
															text += parspesplcha[x]
																	.toLowerCase(Locale.US);
														}
														text += "</big> ";
													}
													if (parspesplcha[4]
															.toLowerCase(
																	Locale.US)
															.equals(inputsplspl[4]
																	.toLowerCase(Locale.US))) {
														text += "<big><b><u>"
																+ parspesplspc[0]
																		.substring(
																				0,
																				5)
																		.toUpperCase(
																				Locale.US)
																+ "</u></b>";
														fouwor = "yes";
														for (int x = 5; x < psscl; x++) {
															text += parspesplcha[x]
																	.toLowerCase(Locale.US);
														}
														text += "</big> ";
													}// END IF 5th
														// CHARACTER
														// MATCHES
												}
											}// END IF 4TH CHARACTER
												// MATCHES
										}// END IF FIRST 3 CHARACTERS
											// MATCH

										if (fouwor.equals("yes")) {
											fousth = "yes";
											for (int x = 1; x < psssl; x++) {
												text += parspesplspc[x] + " ";
											}
										}
									} while (c.moveToNext());
								}
							}// END IF inputspl[j].length()>2

							if (fousth.equals("no")) {
								if (inputspl[j].length() > 1) {
									c = MainLfqActivity.getMiscDb().rawQuery("SELECT "
											+ inputsplspl[0] + " FROM "
											+ parspe + " WHERE "
											+ inputsplspl[0] + " LIKE '"
											+ inputspl[j].substring(0, 2)
											+ "%' LIMIT 20", null);
									if (c.moveToFirst()) {
										do {
											selwor = c.getString(0);
											if (selwor == null) {
												continue;
											}
											parspesplspc = c.getString(0)
													.split(" ");
											psssl = parspesplspc.length;
											fouwor = "no";
											String[] parspesplcha = Helpers
													.explode(parspesplspc[0]);
											psscl = parspesplcha.length;
											text += "<u><b>"
													+ parspesplspc[0]
															.substring(0, 2)
															.toUpperCase(
																	Locale.US)
													+ "</b></u>";
											fouwor = "yes";
											for (int x = 2; x < psscl; x++) {
												text += parspesplcha[x]
														.toLowerCase(Locale.US);
											}
											text += " ";
											fousth = "yes";
											for (int x = 1; x < psssl; x++) {
												text += parspesplspc[x] + " ";
											}

										} while (c.moveToNext());
									}
								}// END IF inputspl[j].length()>1
							}
							if (fousth.equals("no")) {
								c = MainLfqActivity.getMiscDb()
										.rawQuery("SELECT " + inputsplspl[0]
												+ " FROM " + parspe
												+ " LIMIT 20", null);
								if (c.moveToFirst()) {
									do {
										if (c.getString(0) == null) {
											continue;
										}
										parspesplspc = c.getString(0)
												.split(" ");
										psssl = parspesplspc.length;
										String[] parspesplcha = Helpers
												.explode(parspesplspc[0]);
										psscl = parspesplcha.length;
										text += "<u><b>"
												+ parspesplcha[0]
														.toUpperCase(Locale.US)
												+ "</b></u>";
										for (int x = 1; x < psscl; x++) {
											text += parspesplcha[x]
													.toLowerCase(Locale.US);
										}
										text += " ";
										for (int x = 1; x < psssl; x++) {
											text += parspesplspc[x] + " ";
										}
									} while (c.moveToNext());
									c.close();
								}// END IF CURSOR MOVE TO FIRST
							}
						}// END IF PARSPE!="SPC"
							// CREATE AND ADD NEW COM_WORDS:
						rowParams = new TableRow.LayoutParams(400,
								TableRow.LayoutParams.MATCH_PARENT);
						com_words[i][j] = new TextView(this_act);
						com_words[i][j].setText(Html.fromHtml(text));
						com_words[i][j]
								.setBackgroundResource(R.drawable.cell_shape);
						com_rows[i][j].addView(com_words[i][j], rowParams);

					}// END FOR EACH INPUT WORD
				}// END IF ABLE=="YES"
			}// END FOR EACH COMBO
		}// END IF !CHECK_MNE_GEN_TABLE.ISCHECKED()

		if (check_mne_gen_table.isChecked()) {
			// Make mne_gen_combos, a RelativeLayout, invisible:
			mne_gen_combos.setVisibility(View.GONE);
			// Make table and header_table(TableLayouts) visible:
			table.setVisibility(View.VISIBLE);
			mne_gen_header_table.setVisibility(View.VISIBLE);

			int ip = 0;
			TableRow[] row = new TableRow[len];
			TextView[] word_cell = new TextView[len];
			TextView[][] other_cell = new TextView[len][4];

			// FOR SPECIAL 'OTHER'(FIRST) COLUMN:
			RelativeLayout[] fir_col_layout = new RelativeLayout[len];
			TableLayout[] spe_tab = new TableLayout[len];
			TableRow[][] prompt_spe_row = new TableRow[len][tassl];
			TableRow[][] spe_row = new TableRow[len][tassl];
			TextView[][] prompt_spe_cell = new TextView[len][tassl];
			TextView[][] spe_cell = new TextView[len][tassl];
			int main_cols_ind = 0;
			String fouwor = "";
			int psssl = 0;
			int psscl = 0;

			for (int i = 0; i < len; i++)// FOR EACH WORD IN INPUT
			{
				ip = i + 1;

				// CREATE AND ADD ROW:
				row[i] = new TableRow(this_act);
				rowParams = new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT);
				table.addView(row[i], rowParams);

				// CREATE AND ADD CELL:
				word_cell[i] = new TextView(this_act);
				word_cell[i].setText(Html
						.fromHtml("<u><span style=\"color:purple\"><big>#" + ip
								+ "<br /><br />" + inputspl[i]
								+ "</big></span></u>"));
				word_cell[i].setBackgroundResource(R.drawable.cell_shape);
				rowParams = new TableRow.LayoutParams(150,
						TableRow.LayoutParams.MATCH_PARENT);
				row[i].addView(word_cell[i], rowParams);

				String[] inputsplspl = Helpers.explode(inputspl[i]);
				for (int j = 0; j < tl; j++) {
					System.out.println("j=" + j);
					parspe = tabarr[j];
					if (j == 0) {
						// SET PARAMETERS:
						rlparams = new RelativeLayout.LayoutParams(400,
								RelativeLayout.LayoutParams.WRAP_CONTENT);
						tableParams = new TableLayout.LayoutParams(
								TableLayout.LayoutParams.MATCH_PARENT,
								TableLayout.LayoutParams.MATCH_PARENT);

						// CREATE & ADD RELATIVE LAYOUT:
						fir_col_layout[i] = new RelativeLayout(this_act);
						rowParams = new TableRow.LayoutParams(400,
								TableRow.LayoutParams.MATCH_PARENT);
						fir_col_layout[i]
								.setBackgroundResource(R.drawable.cell_shape_inside);
						row[i].addView(fir_col_layout[i], rowParams);

						// CREATE & ADD TABLE:
						spe_tab[i] = new TableLayout(this_act);
						fir_col_layout[i].addView(spe_tab[i], tableParams);

						// RESET rowParams:
						rowParams = new TableRow.LayoutParams(400,
								TableRow.LayoutParams.WRAP_CONTENT);

						// CREATE AND ADD ROW 1:
						prompt_spe_row[i][0] = new TableRow(this_act);
						spe_tab[i].addView(prompt_spe_row[i][0], rowParams);

						// CREATE AND ADD CELL 1 of ROW 1:
						prompt_spe_cell[i][0] = new TextView(this_act);
						prompt_spe_cell[i][0].setText(tabarrspestr[j]
								.toUpperCase(Locale.US));
						prompt_spe_cell[i][0]
								.setBackgroundResource(R.drawable.cell_shape);
						prompt_spe_row[i][0].addView(prompt_spe_cell[i][0],
								rowParams);

						// CREATE AND ADD ROW 2:
						spe_row[i][0] = new TableRow(this_act);
						spe_tab[i].addView(spe_row[i][0], rowParams);

						// CREATE AND ADD CELL 1 of ROW 1:
						spe_cell[i][0] = new TextView(this_act);
						spe_cell[i][0]
								.setBackgroundResource(R.drawable.cell_shape);
						spe_row[i][0].addView(spe_cell[i][0], rowParams);

					}
					if (j > 0 && j < tassl)// IF FOR J IN # OF
											// SPECIAL COLUMNS
					{
						// SET rowParams:
						rowParams = new TableRow.LayoutParams(
								TableRow.LayoutParams.MATCH_PARENT,
								TableRow.LayoutParams.WRAP_CONTENT);

						// CREATE AND ADD PROMPT ROW:
						prompt_spe_row[i][j] = new TableRow(this_act);
						spe_tab[i].addView(prompt_spe_row[i][j], rowParams);

						// CREATE AND ADD PROMPT CELL:
						prompt_spe_cell[i][j] = new TextView(this_act);
						prompt_spe_cell[i][j].setText(tabarrspestr[j]
								.toUpperCase(Locale.US));
						prompt_spe_cell[i][j]
								.setBackgroundResource(R.drawable.cell_shape);
						prompt_spe_row[i][j].addView(prompt_spe_cell[i][j],
								rowParams);

						// CREATE AND ADD CELL ROW:
						spe_row[i][j] = new TableRow(this_act);
						spe_tab[i].addView(spe_row[i][j], rowParams);

						// CREATE AND ADD CELL:
						spe_cell[i][j] = new TextView(this_act);
						spe_cell[i][j]
								.setBackgroundResource(R.drawable.cell_shape);
						spe_row[i][j].addView(spe_cell[i][j], rowParams);

					}
					if (j >= tassl) {

						// CREATE & ADD CELL
						main_cols_ind = j - tassl;
						other_cell[i][main_cols_ind] = new TextView(this_act);
						other_cell[i][main_cols_ind]
								.setBackgroundResource(R.drawable.cell_shape);
						rowParams = new TableRow.LayoutParams(400,
								TableRow.LayoutParams.MATCH_PARENT);
						row[i].addView(other_cell[i][main_cols_ind], rowParams);

					}
					String fousth = "no";
					text = "";
					if (inputspl[i].length() > 2) {
						if (parspe.equals("verb") || parspe.equals("adv.")
								|| parspe.equals("noun")
								|| parspe.equals("adj.")) {
							c = MainLfqActivity.getMiscDb().rawQuery(
									"SELECT * FROM " + tables.dictionarya + " WHERE " + dictionarya.Word + " LIKE '"
											+ inputspl[i].substring(0, 3)
											+ "%' AND " + dictionarya.PartSpeech + "='" + parspe
											+ "' ORDER BY Word LIMIT 20", null);
						}
						if (!parspe.equals("verb") && !parspe.equals("adv.")
								&& !parspe.equals("noun")
								&& !parspe.equals("adj.")) {
							c = MainLfqActivity.getMiscDb().rawQuery(
									"SELECT " + inputsplspl[0] + " FROM "
											+ parspe + " WHERE "
											+ inputsplspl[0] + " LIKE '"
											+ inputspl[i].substring(0, 3)
											+ "%'", null);
						}
						if (c.moveToFirst()) {

							do {

								if (parspe.equals("verb")
										|| parspe.equals("adv.")
										|| parspe.equals("noun")
										|| parspe.equals("adj.")) {
									selwor = c.getString(c
											.getColumnIndex("Word"));
								}
								if (!parspe.equals("verb")
										&& !parspe.equals("adv.")
										&& !parspe.equals("noun")
										&& !parspe.equals("adj.")) {
									selwor = c.getString(0);

								}
								if (selwor == null) {
									continue;
								}
								parspesplspc = selwor.split(" ");
								String[] parspesplcha = Helpers
										.explode(parspesplspc[0]);
								psssl = parspesplspc.length;
								psscl = parspesplcha.length;
								fouwor = "no";
								if (parspesplcha.length == 3) {
									text += "<big><u><b>"
											+ parspesplspc[0].substring(0, 3)
													.toUpperCase(Locale.US)
											+ "</b></u></big>";
									fouwor = "yes";
								}
								if (parspesplcha.length > 3) {
									if (!parspesplcha[3].toLowerCase(Locale.US)
											.equals(inputsplspl[3]
													.toLowerCase(Locale.US))) {
										text += "<big><u><b>"
												+ parspesplspc[0].substring(0,
														3).toUpperCase(
														Locale.US)
												+ "</b></u></big>";
										fouwor = "yes";
										for (int x = 3; x < psscl; x++) {
											text += parspesplcha[x]
													.toLowerCase(Locale.US);
										}
									}
									if (parspesplcha[3].toLowerCase(Locale.US)
											.equals(inputsplspl[3]
													.toLowerCase(Locale.US))) {
										if (parspesplcha.length > 4
												&& inputsplspl.length > 4) {
											if (!parspesplcha[4]
													.toLowerCase(Locale.US)
													.equals(inputsplspl[4]
															.toLowerCase(Locale.US))) {
												text += "<big><b><u>"
														+ parspesplspc[0]
																.substring(0, 4)
																.toUpperCase(
																		Locale.US)
														+ "</u></b>";
												fouwor = "yes";
												for (int x = 4; x < psscl; x++) {
													text += parspesplcha[x]
															.toLowerCase(Locale.US);
												}
												text += "</big>";
											}
											if (parspesplcha[4]
													.toLowerCase(Locale.US)
													.equals(inputsplspl[4]
															.toLowerCase(Locale.US))) {
												text += "<big><b><u>"
														+ parspesplspc[0]
																.substring(0, 5)
																.toUpperCase(
																		Locale.US)
														+ "</u></b>";
												fouwor = "yes";
												for (int x = 5; x < psscl; x++) {
													text += parspesplcha[x]
															.toLowerCase(Locale.US);
												}
												text += "</big>";
											}
										}
									}
									if (fouwor.equals("yes")) {
										fousth = "yes";
										if (parspe.equals("verb")
												|| parspe.equals("adv.")
												|| parspe.equals("noun")
												|| parspe.equals("adj.")) {
											text += " - " + parspe + " ";
											text += c
													.getString(c
															.getColumnIndex("Definition"))
													+ " ";
										}
										if (!parspe.equals("verb")
												&& !parspe.equals("adv.")
												&& !parspe.equals("noun")
												&& !parspe.equals("adj.")) {
											for (int x = 1; x < psssl; x++) {
												text += parspesplspc[x] + " ";
											}
											if (psssl == 1) {
												text += " ";
											}
										}
									}// end if fouwor is "yes"
								}

							} while (c.moveToNext());
						}
					}
					if (fousth.equals("no")) {
						if (inputspl[i].length() > 1) {
							if (parspe.equals("verb") || parspe.equals("adv.")
									|| parspe.equals("noun")
									|| parspe.equals("adj.")) {
								c = MainLfqActivity.getMiscDb().rawQuery(
										"SELECT * FROM dictionarya WHERE Word LIKE '"
												+ inputspl[i].substring(0, 2)
												+ "%' AND PartSpeech='"
												+ parspe
												+ "' ORDER BY Word LIMIT 20",
										null);
							}
							if (!parspe.equals("verb")
									&& !parspe.equals("adv.")
									&& !parspe.equals("noun")
									&& !parspe.equals("adj.")) {
								c = MainLfqActivity.getMiscDb().rawQuery("SELECT " + inputsplspl[0]
										+ " FROM " + parspe + " WHERE "
										+ inputsplspl[0] + " LIKE '"
										+ inputspl[i].substring(0, 2) + "%'",
										null);
							}
							if (c.moveToFirst()) {
								do {
									selwor = "";
									if (parspe.equals("verb")
											|| parspe.equals("adv.")
											|| parspe.equals("noun")
											|| parspe.equals("adj.")) {
										selwor = c.getString(c
												.getColumnIndex("Word"));
									}
									if (!parspe.equals("verb")
											&& !parspe.equals("adv.")
											&& !parspe.equals("noun")
											&& !parspe.equals("adj.")) {
										selwor = c.getString(0);
									}
									if (selwor == null) {
										continue;
									}
									parspesplspc = selwor.split(" ");
									fouwor = "no";
									String[] parspesplcha = Helpers
											.explode(parspesplspc[0]);
									psssl = parspesplspc.length;
									psscl = parspesplcha.length;
									text += "<u><b>"
											+ parspesplspc[0].substring(0, 2)
													.toUpperCase(Locale.US)
											+ "</b></u>";
									fouwor = "yes";
									for (int x = 2; x < psscl; x++) {
										text += parspesplcha[x]
												.toLowerCase(Locale.US);
									}

									if (fouwor.equals("yes")) {
										fousth = "yes";
										if (parspe.equals("verb")
												|| parspe.equals("adv.")
												|| parspe.equals("noun")
												|| parspe.equals("adj.")) {
											text += " - " + parspe + " ";
											text += c
													.getString(c
															.getColumnIndex("Definition"));
										}
										if (!parspe.equals("verb")
												&& !parspe.equals("adv.")
												&& !parspe.equals("noun")
												&& !parspe.equals("adj.")) {
											for (int x = 1; x < psssl; x++) {
												text += parspesplspc[x] + " ";
											}
											if (psssl == 1) {
												text += " ";
											}
										}
									}
								} while (c.moveToNext());
							}
						}
					}
					if (fousth.equals("no")) {
						if (inputspl[i].length() > 0) {

							if (parspe.equals("verb") || parspe.equals("adv.")
									|| parspe.equals("noun")
									|| parspe.equals("adj.")) {
								c = MainLfqActivity.getMiscDb().rawQuery(
										"SELECT * FROM " + tables.dictionarya + " WHERE " + dictionarya.Word + " LIKE '"
												+ inputsplspl[0]
												+ "%' AND " + dictionarya.PartSpeech + "='"
												+ parspe
												+ "' ORDER BY Word LIMIT 20",
										null);
							}
							if (!parspe.equals("verb")
									&& !parspe.equals("adv.")
									&& !parspe.equals("noun")
									&& !parspe.equals("adj.")) {
								c = MainLfqActivity.getMiscDb().rawQuery("SELECT " + inputsplspl[0]
										+ " FROM " + parspe, null);
							}
							if (c.moveToFirst()) {
								do {
									selwor = "";
									if (parspe.equals("verb")
											|| parspe.equals("adv.")
											|| parspe.equals("noun")
											|| parspe.equals("adj.")) {
										selwor = c.getString(c
												.getColumnIndex("Word"));
									}
									if (!parspe.equals("verb")
											&& !parspe.equals("adv.")
											&& !parspe.equals("noun")
											&& !parspe.equals("adj.")) {
										selwor = c.getString(0);
									}
									if (selwor == null) {
										continue;
									}
									parspesplspc = selwor.split(" ");
									String[] parspesplcha = Helpers
											.explode(parspesplspc[0]);
									psssl = parspesplspc.length;
									psscl = parspesplcha.length;
									if (parspesplspc[0].length() > 1) {
										text += "<u><b>"
												+ parspesplspc[0].substring(0,
														1).toUpperCase(
														Locale.US) + "</b></u>";
									}
									for (int x = 1; x < psscl; x++) {
										text += parspesplcha[x]
												.toLowerCase(Locale.US);
									}
									if (parspe.equals("verb")
											|| parspe.equals("adv.")
											|| parspe.equals("noun")
											|| parspe.equals("adj.")) {
										text += " - " + parspe + " ";
										text += c.getString(c
												.getColumnIndex(dictionarya.Definition));
									}
									if (!parspe.equals("verb")
											&& !parspe.equals("adv.")
											&& !parspe.equals("noun")
											&& !parspe.equals("adj.")) {
										for (int x = 1; x < psssl; x++) {
											text += parspesplspc[x] + " ";
										}
										if (psssl == 1) {
											text += " ";
										}
									}
								} while (c.moveToNext());
							}
						}
					}
					if (j < tassl) {
						spe_cell[i][j].setText(Html.fromHtml(text));
					} else {
						other_cell[i][main_cols_ind].setText(Html
								.fromHtml(text));
					}

				}// end for each parspe(j)

			}
		}// END IF check_mne_gen_table.isChecked()
		results.setText(Html.fromHtml("<b>ALL LOADED!</b>"));
	}// end function doLoadMnemonics()

}
