package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.lfq.learnfactsquick.Constants.cols.global_numbers;
import com.lfq.learnfactsquick.Constants.cols.user_numbers;
import com.lfq.learnfactsquick.Constants.tables;
import com.lfq.learnfactsquick.MainLfqActivity.doSyncConflicts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditNumbers extends Activity {
	private LinearLayout entries_header_layout;
	private LinearLayout entries_layout;
	private List<LinearLayout> entries;
	private LinearLayout top_layout;
	private TextView results, login_status, prompt_total_number_tv,
			total_number_tv, prompt_input_number, prompt_input_mnemonic,
			prompt_input_number_info;
	private EditText username_input, password_input,
			input_number_numbers_entries, input_number, input_mnemonic,
			input_number_info, input_number_title;
	private RadioButton check_edit_shared_numbers, check_edit_user_numbers;
	private Button do_login, do_logout, do_edit_numbers, do_backup, add_after;
	private RadioButton check_update_numbers, check_delete_numbers,
			check_insert_numbers;
	private Spinner select_number_title;
	private ArrayAdapter titlesAdapter;

	private LinearLayout.LayoutParams params;
	private LinearLayout.LayoutParams button_params;

	private String text, numbers_table, username, password;
	private String[] textspl;
	private int num_entries;
	private List<TextView> prompt_num_ent, prompt_mne_ent, prompt_mne_inf;
	private List<EditText> num_ent, mne_ent, inf_ent;
	// private TextView[] prompt_num_ent, prompt_mne_ent, prompt_mne_inf;
	// private EditText[] num_ent, mne_ent, inf_ent;
	private List<Button> delete_entries, insert_above_entries;
	private int id, sav_id;
	private HashMap<String, String> text_list;
	private Boolean logged_in;
	private Helpers h;
	AlertDialog dialog;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private String autosync_text, sql;
	private ContentValues cv;

	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private static Boolean is_database_load;
	private List<String> total_numbers_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		autosync_text = "";
		sql = "";
		cv = new ContentValues();
		h = new Helpers(this_act);
		text_list = new HashMap<String, String>();
		logged_in = false;
		entries = new ArrayList<LinearLayout>();
		total_numbers_list = new ArrayList<String>();
		prompt_num_ent = new ArrayList<TextView>();
		prompt_mne_ent = new ArrayList<TextView>();
		prompt_mne_inf = new ArrayList<TextView>();
		num_ent = new ArrayList<EditText>();
		mne_ent = new ArrayList<EditText>();
		inf_ent = new ArrayList<EditText>();
		delete_entries = new ArrayList<Button>();
		insert_above_entries = new ArrayList<Button>();

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
		if (check_update_numbers.isChecked()
				|| (check_insert_numbers.isChecked() && input_number_numbers_entries
						.getText().toString().length() > 0)) {
			for (int i = 0; i < num_ent.size(); i++) {
				if (num_ent.get(i) != null) {
					editor.putString("EDIT NUMBERS NUMBER ENTRY " + i, num_ent
							.get(i).getText().toString());
				}
			}
			for (int i = 0; i < mne_ent.size(); i++) {
				if (mne_ent.get(i) != null) {
					editor.putString("EDIT NUMBERS MNEMONIC ENTRY " + i,
							mne_ent.get(i).getText().toString());
				}
			}
		}
		editor.putString("EDIT NUMBERS NUMBER NUMBERS",
				input_number_numbers_entries.getText().toString());
		editor.putBoolean("EDIT NUMBERS CHECK USER NUMBERS",
				check_edit_user_numbers.isChecked());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	@SuppressWarnings("static-access")
	public void setViews() {
		// LAYOUT:
		setTitle("EDIT NUMBERS");
		entries_header_layout = (LinearLayout) findViewById(R.id.numbers_mnemonic_entries_header_layout);
		entries_layout = (LinearLayout) findViewById(R.id.numbers_mnemonic_entries_layout);
		top_layout = (LinearLayout) findViewById(R.id.edit_numbers_top_layout);

		// BUTTONS:
		do_login = (Button) findViewById(R.id.do_login_edit_numbers);
		do_logout = (Button) findViewById(R.id.do_logout_edit_numbers);
		do_edit_numbers = (Button) findViewById(R.id.do_edit_numbers_table);
		do_backup = (Button) findViewById(R.id.do_edit_numbers_backup);
		add_after = (Button) findViewById(R.id.edit_numbers_add_after);

		// EDITTEXTS:
		username_input = (EditText) findViewById(R.id.edit_numbers_username);
		password_input = (EditText) findViewById(R.id.edit_numbers_password);
		input_number_title = (EditText) findViewById(R.id.input_numbers_table);
		input_number_numbers_entries = (EditText) findViewById(R.id.input_number_numbers_entries);

		// TEXTVIEWS:
		login_status = (TextView) findViewById(R.id.edit_numbers_login_status);
		results = (TextView) findViewById(R.id.edit_numbers_results);

		if (h.getLoginStatus() == true) {
			username = Helpers.getUsername();
			password = Helpers.getPassword();
			username_input.setText(username);
			logged_in = true;
			login_status.setText("WELCOME " + username + ".");
		}

		// RADIOBUTTONS:
		check_edit_shared_numbers = (RadioButton) findViewById(R.id.check_edit_shared_numbers);
		check_edit_shared_numbers.setChecked(true);
		check_edit_user_numbers = (RadioButton) findViewById(R.id.check_edit_user_numbers);
		check_update_numbers = (RadioButton) findViewById(R.id.check_update_numbers);
		check_delete_numbers = (RadioButton) findViewById(R.id.check_delete_numbers);
		check_insert_numbers = (RadioButton) findViewById(R.id.check_insert_numbers);

		// SPINNERS:
		select_number_title = (Spinner) findViewById(R.id.select_edit_numbers_title);
		titlesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		titlesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_number_title.setAdapter(titlesAdapter);
	}

	public void loadButtons() {
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_edit_numbers.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		add_after.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		do_backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				top_layout.setVisibility(View.VISIBLE);
				do_backup.setVisibility(View.GONE);
			}
		});

		do_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				password = password_input.getText().toString();
				username = username_input.getText().toString();
				text = Helpers.login(username, password);
				textspl = text.split("@@@");
				if (textspl[0].equals("true")) {
					logged_in = true;
				}
				login_status.setText(textspl[1]);
			}
		});

		do_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logged_in = false;
				password_input.setText("");
				login_status.setText("LOGGED OUT. BYE BYE " + username);
			}
		});

		check_edit_shared_numbers
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						new doGetTitles(false).execute(tables.global_numbers);
					}
				});
		check_edit_user_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Helpers.getLoginStatus() == true) {
					new doGetTitles(false).execute(tables.user_numbers);
				} else {
					check_edit_user_numbers.setChecked(false);
					check_edit_shared_numbers.setChecked(true);
					dialog = new AlertDialog.Builder(this_act).create();
					dialog.setTitle("Not Logged In");
					dialog.setMessage("Need to be logged in to se your numbers.");
					dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
				}
				dialog.show();
			}
		});

		check_update_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
				do_begin_update_numbers();
			}
		});
		check_insert_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
				startInsert();
			}
		});
		check_delete_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
			}
		});

		select_number_title
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (check_update_numbers.isChecked()) {
							do_begin_update_numbers();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						if (check_update_numbers.isChecked()) {
							do_begin_update_numbers();
						}
					}

				});

		do_edit_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				results.setText("");
				if (check_delete_numbers.isChecked()) {
					deleteNumber();
				} else if (check_insert_numbers.isChecked()) {
					insertNumber();
				} else if (check_update_numbers.isChecked()) {
					updateTable();
				}
			}
		});
		add_after.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addRemoveNumber(true, entries.size() + 1, true);
			}
		});
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_numbers);
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
			input_number_numbers_entries.setText(sharedPref.getString(
					"EDIT NUMBERS NUMBER NUMBERS", ""));
			boolean isUser = false;
			if(Helpers.getLoginStatus() == true){
				isUser = sharedPref.getBoolean(
						"EDIT NUMBERS CHECK USER NUMBERS", false);
			   if(isUser==true){
			      check_edit_user_numbers.setChecked(true);
			   }
			}
			if(isUser==true){
				new doGetTitles(true).execute(tables.user_numbers);
			}else{
				new doGetTitles(true).execute(tables.global_numbers);
			}
		}

	}

	public class doGetTitles extends AsyncTask<String, String, String> {
		public boolean isStartUp;
		public doGetTitles(Boolean isStartUp){
			this.isStartUp = isStartUp;
		}
		@Override
		protected void onPreExecute() {
			titlesAdapter.clear();
		}

		@Override
		protected String doInBackground(String... strings) {
			String table = strings[0];
			System.out.println("getTitles called");
			Cursor c_tits = null;
			if (table.equals(tables.global_numbers)) {
				c_tits = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT DISTINCT " + global_numbers.Title + " FROM "
								+ table + " ORDER BY " + global_numbers.Title,
						null);
			} else {
				c_tits = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT DISTINCT " + user_numbers.Title + " FROM "
								+ table + " WHERE " + user_numbers.Username
								+ "=? ORDER BY " + user_numbers.Title,
						new String[] { username });
			}
			if (c_tits.moveToFirst()) {
				do {
					publishProgress(c_tits.getString(0));
				} while (c_tits.moveToNext());
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... strings) {
			titlesAdapter.add(strings[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(isStartUp==true){
				check_update_numbers.setChecked(sharedPref.getBoolean(
						"EDIT NUMBERS CHECK UPDATE NUMBERS", false));
				check_insert_numbers.setChecked(sharedPref.getBoolean(
						"EDIT NUMBERS CHECK INSERT NUMBERS", false));
				if (check_update_numbers.isChecked()
						|| check_insert_numbers.isChecked()) {
					if (check_insert_numbers.isChecked()) {
						startInsert();
					}
					if (check_update_numbers.isChecked()) {
						do_begin_update_numbers();
					}
					for (int i = 0; i < num_ent.size(); i++) {
						if (num_ent.get(i) != null) {
							num_ent.get(i).setText(
									sharedPref.getString(
											"EDIT NUMBERS NUMBER ENTRY " + i, ""));
						}
					}
					for (int i = 0; i < mne_ent.size(); i++) {
						if (mne_ent.get(i) != null) {
							mne_ent.get(i)
									.setText(
											sharedPref.getString(
													"EDIT NUMBERS MNEMONIC ENTRY "
															+ i, ""));
						}
					}
				}
				do_backup.setVisibility(View.GONE);
				setVisibilities();	
			}			
		}		
	}

	public void resetEntries() {
		entries_layout.removeAllViews();
		System.out.println("Reset entries called. size()=" + entries.size());
		for (int i = 0; i < entries.size(); i++) {
			// ADD THE RELATIVE LAYOUT:
			params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			((TextView) ((LinearLayout) entries.get(i).getChildAt(0))
					.getChildAt(0)).setText((i + 1) + ") NUMBER:");
			entries_layout.addView(entries.get(i));
		}
	}
	
	public String getTotalNumberPrompt(){
		String ret="";
		for(int i=0;i<total_numbers_list.size();i++){
			if(!total_numbers_list.get(i).trim().equals("")){
				ret += total_numbers_list.get(i) + "-"; 
			}
		}
		if (ret.length()>0){
			ret = ret.substring(0,ret.length()-1);
		}
		return ret;
	}

	public void addRemoveNumber(boolean is_add, int index, boolean is_after) {
		System.out.println("is_add = " + is_add);
		// ADD PROMPT INPUT NUMBER
		if (is_add == false) {
			System.out.println("total number string = "
					+ total_number_tv.getText().toString() + ", index = "
					+ index);
			total_numbers_list.remove(index);			
			total_number_tv.setText(getTotalNumberPrompt());
			entries.remove(index);
			resetEntries();
		} else {
			if (total_numbers_list.size() == 0 || is_after == true) {
				total_numbers_list.add("");
			} else {
				total_numbers_list.add(index, "");
			}
			if (index == 0) {
				System.out.println("INDEX IS 0!!!");
				index++;
			}
			LinearLayout llh;
			LinearLayout llv = new LinearLayout(this_act);
			llv.setOrientation(LinearLayout.VERTICAL);
			// ADD INSERT/DELETE ENTRY
			// LAYOUT------------------------------------------:
			llh = new LinearLayout(this_act);
			llh.setOrientation(LinearLayout.HORIZONTAL);
			prompt_input_number = new TextView(this_act);
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 0.3f;
			llh.addView(prompt_input_number, params);
			// ADD INPUT NUMBER
			input_number = new EditText(this_act);
			input_number.setMaxLines(1);
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 0.3f;
			input_number.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			input_number.setBackgroundResource(R.drawable.rounded_edittext_red);
			input_number.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable arg0) {
					String total_number_txt = total_number_tv.getText()
							.toString();

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
			llh.addView(input_number, params);
			// ADD INSERT ABOVE ENTRY BUTTON:
			Button insert_above = new Button(this_act);
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 0.2f;
			insert_above.setText("+");
			insert_above.setTextSize(24);
			insert_above.setBackgroundResource(sharedPref.getInt("BG Button",
					R.drawable.button));
			insert_above.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					LinearLayout myllv = (LinearLayout) v.getParent()
							.getParent();
					addRemoveNumber(true, ((LinearLayout) myllv.getParent())
							.indexOfChild(myllv), false);
				}
			});
			llh.addView(insert_above, params);
			// ADD DELETE ENTRY BUTTON:
			Button delete_entry = new Button(this_act);
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 0.2f;
			delete_entry.setText("-");
			delete_entry.setTextSize(24);
			delete_entry.setBackgroundResource(sharedPref.getInt("BG Button",
					R.drawable.button));
			delete_entry.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					LinearLayout myllv = (LinearLayout) v.getParent()
							.getParent();
					addRemoveNumber(false, ((LinearLayout) myllv.getParent())
							.indexOfChild(myllv), false);
				}
			});
			llh.addView(delete_entry, params);
			llv.addView(llh);
			// ----------------------------------------------------------------
			// ADD PROMPT INPUT MNEMONIC
			llh = new LinearLayout(this_act);
			llh.setOrientation(LinearLayout.HORIZONTAL);
			prompt_input_mnemonic = new TextView(this_act);
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			prompt_input_mnemonic.setText("MNEMONIC");
			llh.addView(prompt_input_mnemonic, params);
			// ADD INPUT MNEMONIC
			input_mnemonic = new EditText(this_act);
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			input_mnemonic
					.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
			input_mnemonic.setMaxLines(3);
			input_mnemonic.setGravity(Gravity.TOP);
			input_mnemonic
					.setBackgroundResource(R.drawable.rounded_edittext_red);
			llh.addView(input_mnemonic, params);
			llv.addView(llh);
			// ADD PROMPT NUMBER INFO
			llh = new LinearLayout(this_act);
			llh.setOrientation(LinearLayout.HORIZONTAL);
			prompt_input_number_info = new TextView(this_act);
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			prompt_input_number_info.setText("INFO:");
			llh.addView(prompt_input_number_info, params);
			// ADD INPUT INFO
			input_number_info = new EditText(this_act);
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			input_number_info
					.setBackgroundResource(R.drawable.rounded_edittext_red);
			input_number_info
					.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
			input_number_info.setMaxLines(3);
			input_number_info.setGravity(Gravity.TOP);
			llh.addView(input_number_info, params);
			llv.addView(llh);
			entries.add((index - 1), llv);
			resetEntries();
			setVisibilities();
		}
	}

	public void do_begin_update_numbers() {
        if(select_number_title.getSelectedItem() == null){
        	return;
        }
		total_numbers_list.clear();
		// SHOW FULL SCREEN:
		top_layout.setVisibility(View.GONE);
		do_backup.setVisibility(View.VISIBLE);
		// ----------------------------------------
		numbers_table = "";
		username = username_input.getText().toString();
		boolean is_date = false;
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
			is_date = true;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
		}
		text = "";
		entries_header_layout.removeAllViews();
		entries_layout.removeAllViews();

		// ADD PROMPT INPUT NUMBER
		prompt_total_number_tv = new TextView(this_act);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		prompt_total_number_tv.setText("TOTAL NUMBER:");
		entries_header_layout.addView(prompt_total_number_tv, params);
		total_number_tv = new TextView(this_act);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// total_number_tv.setBackgroundResource(R.drawable.rounded_edittext_red);
		entries_header_layout.addView(total_number_tv, params);
		String total_number = "";
		Cursor c_get1 = MainLfqActivity.getMiscDb()
				.rawQuery(
						"SELECT * FROM " + numbers_table + " WHERE "
								+ global_numbers.Title + "=?",
						new String[] { select_number_title.getSelectedItem()
								.toString() });
		if (c_get1.moveToFirst()) {
			results.setText("BEGIN UPDATE FOR "
					+ c_get1.getString(c_get1
							.getColumnIndex(global_numbers.Title)) + ".");
			num_entries = c_get1.getCount();
			System.out.println("num_entries=" + num_entries);
			input_number_numbers_entries.setText(String.valueOf(num_entries));
			startInsert();
			int i = 0;
			if (numbers_table.equals(tables.user_numbers)
					&& c_get1.getString(
							c_get1.getColumnIndex(user_numbers.Type)).equals(
							"HISTORICAL_NUMBERS")) {
				is_date = true;
			}
			String[] total_numbers = new String[c_get1.getCount()];
			do {
				total_numbers[i] = String.valueOf(c_get1.getString(c_get1
						.getColumnIndex(user_numbers.Entry)));
				num_ent.get(i).setText(
						c_get1.getString(c_get1
								.getColumnIndex(user_numbers.Entry)));
				total_numbers_list.add(c_get1.getString(c_get1
						.getColumnIndex(user_numbers.Entry)));
				System.out.println("SETTING mne_ent["
						+ i
						+ "] TO="
						+ c_get1.getString(c_get1
								.getColumnIndex(user_numbers.Entry_Mnemonic)));
				mne_ent.get(i).setText(
						c_get1.getString(c_get1
								.getColumnIndex(user_numbers.Entry_Mnemonic)));
				inf_ent.get(i).setText(
						c_get1.getString(c_get1
								.getColumnIndex(user_numbers.Entry_Info)));
				i++;
			} while (c_get1.moveToNext());
			// if (is_date == true && total_number.length() == 8) {
			// total_number = total_number.substring(0, 4) + "/"
			// + total_number.substring(4, 6) + "/"
			// + total_number.substring(6, 8);
			// }
			total_number_tv.setText(TextUtils.join("-", total_numbers));
		}
		setVisibilities();
	}

	public void startInsert() {
		if (input_number_numbers_entries.getText().toString().equals("")) {
			results.setText("MUST ENTER HOW MANY MNEMONIC ENTRIES.");
			return;
		}
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		System.out.println("startInsert num_entries=" + num_entries);
		entries.clear();
		prompt_num_ent.clear();
		prompt_mne_ent.clear();
		prompt_mne_inf.clear();
		num_ent.clear();
		mne_ent.clear();
		inf_ent.clear();
		delete_entries.clear();
		insert_above_entries.clear();

		final int num_eles = 8;
		LinearLayout llh, llv;
		for (int i = 0; i < num_entries; i++) {
			llv = new LinearLayout(this_act);
			llv.setOrientation(LinearLayout.VERTICAL);
			llh = new LinearLayout(this_act);
			llh.setOrientation(LinearLayout.HORIZONTAL);
			// ADD PROMPT NUMBER:
			prompt_num_ent.add(new TextView(this_act));
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 0.3f;
			prompt_num_ent.get(i).setText((i + 1) + ") NUMBER:");
			llh.addView(prompt_num_ent.get(i), params);
			// ADD NUMBER INPUT:
			num_ent.add(new EditText(this_act));
			num_ent.get(i).setRawInputType(InputType.TYPE_CLASS_NUMBER);
			num_ent.get(i).setMaxLines(1);
			params = new LinearLayout.LayoutParams(0,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.weight = 0.3f;
			num_ent.get(i).setBackgroundResource(
					R.drawable.rounded_edittext_red);
			llh.addView(num_ent.get(i), params);
			// ADD INSERT ABOVE ENTRY BUTTON:
			insert_above_entries.add(new Button(this_act));
			params = new LinearLayout.LayoutParams(0,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.weight = 0.2f;
			insert_above_entries.get(i).setText("+");
			insert_above_entries.get(i).setTextSize(24);
			insert_above_entries.get(i).setBackgroundResource(
					sharedPref.getInt("BG Button", R.drawable.button));
			insert_above_entries.get(i).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							LinearLayout myllv = (LinearLayout) v.getParent()
									.getParent();
							addRemoveNumber(true, ((LinearLayout) myllv
									.getParent()).indexOfChild(myllv), false);
						}
					});
			llh.addView(insert_above_entries.get(i), params);
			// ADD DELETE ENTRY BUTTON:
			delete_entries.add(new Button(this_act));
			params = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 0.2f;
			delete_entries.get(i).setText("-");
			delete_entries.get(i).setTextSize(24);
			// delete_entry[i].setBackgroundResource(R.drawable.minus);
			delete_entries.get(i).setBackgroundResource(
					sharedPref.getInt("BG Button", R.drawable.button));
			delete_entries.get(i).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							LinearLayout myllv = (LinearLayout) v.getParent()
									.getParent();
							addRemoveNumber(false, ((LinearLayout) myllv
									.getParent()).indexOfChild(myllv), false);
						}
					});
			llh.addView(delete_entries.get(i), params);
			llv.addView(llh);
			// CREATE NEW HORIZONTAL LINEAR LAYOUT:
			llh = new LinearLayout(this_act);
			llh.setOrientation(LinearLayout.HORIZONTAL);
			// ADD PROMPT MNEMONIC:
			prompt_mne_ent.add(new TextView(this_act));
			prompt_mne_ent.get(i).setText("MNEMONIC:");
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			llh.addView(prompt_mne_ent.get(i), params);
			// ADD MNEMONIC INPUT:
			System.out.println("Creating new mne_ent!!!!!!!!!!!11 IN LOOP");
			mne_ent.add(new EditText(this_act));
			mne_ent.get(i).setMaxLines(2);
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mne_ent.get(i).setBackgroundResource(
					R.drawable.rounded_edittext_red);
			llh.addView(mne_ent.get(i), params);
			llv.addView(llh);
			// ADD PROMPT MNEMONIC INFORMATION:
			llh = new LinearLayout(this_act);
			llh.setOrientation(LinearLayout.HORIZONTAL);
			prompt_mne_inf.add(new TextView(this_act));
			prompt_mne_inf.get(i).setText("INFO:");
			params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			llh.addView(prompt_mne_inf.get(i), params);
			// ADD MNEMONIC INFORMATION:
			inf_ent.add(new EditText(this_act));
			inf_ent.get(i).setMaxLines(2);
			params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			inf_ent.get(i).setBackgroundResource(
					R.drawable.rounded_edittext_red);
			llh.addView(inf_ent.get(i), params);
			llv.addView(llh);
			// ADD THE RELATIVE LAYOUT:
			params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			entries_layout.addView(llv, params);
			entries.add(llv);
		}
	}

	public void updateTable() {
		numbers_table = "";
		username = username_input.getText().toString();
		cv.clear();
		String where_username = "";
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
			username = "";
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
			cv.put(user_numbers.Username, username);
			where_username = " AND " + user_numbers.Username + "='" + username
					+ "'";
		}
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		cv.put(user_numbers.Title, input_number.getText().toString());
		cv.put(user_numbers.Entry_Number, input_number_info.getText()
				.toString());
		text = "";
		for (int i = 0; i < num_entries; i++) {
			cv.put(global_numbers.Entry, num_ent.get(i).getText().toString());
			cv.put(global_numbers.Entry_Mnemonic, mne_ent.get(i).getText()
					.toString());
			cv.put(global_numbers.Entry_Index, (i + 1));
			MainLfqActivity.getMiscDb().update(
					numbers_table,
					cv,
					global_numbers.Title + "=? AND "
							+ global_numbers.Entry_Index + "=?",
					new String[] {
							select_number_title.getSelectedItem().toString(),
							String.valueOf((i + 1)) });
			sql = "UPDATE " + Helpers.db_prefix + "misc." + numbers_table
					+ " SET " + user_numbers.Title + "='"
					+ input_number.getText().toString() + ", "
					+ user_numbers.Entry + "='"
					+ num_ent.get(i).getText().toString()
					+ user_numbers.Entry_Info + "='"
					+ input_number_info.getText().toString() + "', "
					+ user_numbers.Entry_Mnemonic + "='"
					+ mne_ent.get(i).getText().toString() + "' WHERE "
					+ global_numbers.Title + "='"
					+ select_number_title.getSelectedItem().toString()
					+ "' AND " + global_numbers.Entry_Index + "='" + (i + 1)
					+ "'" + where_username;
			// autoSync(sql, db, action, table, name, bool is_image, byte[]
			// image)
			autosync_text += Synchronize.autoSync(sql, "misc_db", "update",
					numbers_table, select_number_title.getSelectedItem()
							.toString(), false, null);
		}
		results.setText("UPDATED NUMBER " + input_number.getText().toString()
				+ ".");
	}

	public void insertNumber() {
		text = "";
		if (input_number_numbers_entries.getText().toString().equals("")) {
			results.setText("MUST ENTER NUMBER OF ENTRIES.");
			return;
		}
		numbers_table = "";
		username = username_input.getText().toString();
		String username_value = "", username_column = "";
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
			cv.put(user_numbers.Username, username);
			String type = "";
			cv.put(user_numbers.Type, type);
			username_value = username + "','";
			username_column = "," + user_numbers.Username;
		}
		text = "";
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		// GET Entry_Number:---------------------------------------
		int entry_number = 0;
		Cursor c_get_ent_num = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT MAX(" + global_numbers.Entry_Number + ") FROM "
						+ numbers_table, null);
		if (c_get_ent_num.moveToFirst()) {
			entry_number = c_get_ent_num.getInt(0) + 1;
		}
		cv.clear();
		cv.put(global_numbers.Title, input_number.getText().toString());
		cv.put(global_numbers.Entry_Number, entry_number);

		for (int i = 0; i < num_entries; i++) {
			cv.put(global_numbers.Entry, num_ent.get(i).getText().toString());
			cv.put(global_numbers.Entry_Mnemonic, mne_ent.get(i).getText()
					.toString());
			cv.put(global_numbers.Entry_Index, (i + 1));
			MainLfqActivity.getMiscDb().insert(numbers_table, null, cv);
			sql = "INSERT INTO  " + Helpers.db_prefix + "misc." + numbers_table
					+ "(" + username_column + global_numbers.Title + ","
					+ global_numbers.Entry_Number + ","
					+ global_numbers.Entry_Index + "," + global_numbers.Entry
					+ "," + global_numbers.Entry_Mnemonic + ") VALUES('"
					+ username_value + input_number.getText().toString()
					+ "','" + entry_number + "','" + (i + 1) + "','"
					+ num_ent.get(i).getText().toString() + "','"
					+ mne_ent.get(i).getText().toString() + "')";
			// autoSync(sql, db, action, table, name, bool is_image, byte[]
			// image)
			autosync_text += Synchronize.autoSync(sql, "misc_db", "insert",
					numbers_table, String.valueOf(sav_id), false, null);
		}
		results.setText("INSERTED NUMBER " + input_number.getText().toString()
				+ "." + autosync_text);

	}

	public void deleteNumber() {
		numbers_table = "";
		username = username_input.getText().toString();
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
		}
		Cursor c_get1 = MainLfqActivity.getMiscDb()
				.rawQuery(
						"SELECT * FROM " + numbers_table + " WHERE "
								+ global_numbers.Title + "=?",
						new String[] { select_number_title.getSelectedItem()
								.toString() });
		if (c_get1.moveToFirst()) {
			text = c_get1
					.getString(c_get1.getColumnIndex(global_numbers.Title));
		}
		MainLfqActivity.getMiscDb().execSQL(
				"DELETE FROM " + numbers_table + " WHERE "
						+ global_numbers.Title + "='"
						+ select_number_title.getSelectedItem().toString()
						+ "'");
		sql = "DELETE FROM " + Helpers.db_prefix + "misc." + numbers_table
				+ " WHERE " + global_numbers.Title + "='"
				+ select_number_title.getSelectedItem().toString() + "'";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "misc_db", "delete",
				numbers_table, String.valueOf(sav_id), false, null);
		results.setText("DELETED " + text + "." + autosync_text);
	}

	public void setVisibilities() {
		if (check_insert_numbers.isChecked()) {
			input_number_title.setVisibility(View.VISIBLE);
			select_number_title.setVisibility(View.GONE);
		} else {
			input_number_title.setVisibility(View.GONE);
			select_number_title.setVisibility(View.VISIBLE);
		}
		if (!check_delete_numbers.isChecked()) {
			if (num_entries > 0) {
				add_after.setVisibility(View.VISIBLE);
			} else {
				add_after.setVisibility(View.GONE);
			}
		}
	}

}
