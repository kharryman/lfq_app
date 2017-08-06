package com.lfq.learnfactsquick;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("NewApi")
public class EditAcrostics extends Activity {
	private static LinearLayout mne_layout, peg_layout, input_types_layout;// ,
																			// input_word_layout;
	private static ScrollView edit_acrostics_scroll;
	private static EditText input_acrostic_name, enter_acrostic_name;
	// private AutoCompleteTextView enter_acrostic_name;
	// private static ListView select_acrostic_names;
	private static Spinner select_acrostic_tables, select_acrostic_names;
	private static TextView prompt_new_name, results, prompt_inf, prompt_acr,
			prompt_mne, prompt_peg, prompt_img, prompt_name_exists;
	private static ImageView img;
	private static EditText input_new_name;
	// private static EditText input_acrostic_name;
	private static EditText acrostic_text, information_text, mnemonic_text,
			peglist_text;
	private static CheckBox get_next_acrostic, get_last_acrostic,
			check_change_name, check_use_dictionary, check_use_all_acrostics;
	private static RadioButton insert_acrostics, delete_acrostics,
			edit_acrostics;
	private static Button get_acrostic, edit_acrostic, vocabulary_switch,
			do_upload_image, go_left, go_right;

	private static String sql, info, acrostic, mnemonic, peglist, table_name,
			name, opt;

	public static Context context;
	private android.widget.LinearLayout.LayoutParams linear_params;
	private ArrayAdapter<String> tablesAdapter;
	private CustomAdapter namesAdapter;
	private int last_id;
	private List<EditText> inputs;
	private static ContentValues values;
	private String text;
	private static final int SELECT_PHOTO = 100;

	// private SelectNameListener select_name_listener;
	private SelectTableListener select_table_listener;

	static SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private static String autosync_text;
	private byte[] image_data;
	private static Bitmap selected_image;
	private List<Boolean> input_fields;
	private int input_fields_index;

	private static Cursor c, c2;
	private static List<String> col_list;
	private static Boolean is_database_load;
	private static Boolean is_image_load;
	private static Boolean is_first_load;
	private int sav_ent_acr_len;
	private String sav_ent_acr_tex;
	private List<String> list_names;
	private List<Boolean> is_exists;
	private Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list_names = new ArrayList<String>();
		is_exists = new ArrayList<Boolean>();

		context = this;
		act = this;
		setContentView(R.layout.edit_acrostics);
		setViews();
		sav_ent_acr_len = 0;
		sav_ent_acr_tex = "";
		is_database_load = false;
		is_image_load = false;
		is_first_load = true;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		col_list = new ArrayList<String>();
		last_id = 0;
		image_data = null;
		input_fields = new ArrayList<Boolean>();
		input_fields.add(true);
		input_fields.add(true);
		input_fields.add(false);
		input_fields.add(false);
		input_fields.add(false);
		input_fields_index = 0;
		sql = "";
		text = "";
		name = "";
		values = new ContentValues();
		// select_name_listener = new SelectNameListener();
		select_table_listener = new SelectTableListener();
		new doLoadDatabases().execute();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (is_database_load == false || is_image_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (is_database_load == false || is_image_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (is_database_load == false || is_image_load == false) {
			// new doLoadDatabases().execute();
		}
	}

	@Override
	public void onBackPressed() {
		if (is_database_load == false || is_image_load == false) {
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
		name = input_acrostic_name.getText().toString();
		table_name = select_acrostic_tables.getSelectedItem().toString();
		editor.putString("Edit Acrostic Name", name);
		editor.putString("Edit Acrostic Table", table_name);

		editor.putString("Edit Information Text", information_text.getText()
				.toString());
		editor.putString("Edit Acrostic Text", acrostic_text.getText()
				.toString());
		editor.putString("Edit Peglist Text", peglist_text.getText().toString());

		editor.putBoolean("GET NEXT ACROSTIC", get_next_acrostic.isChecked());
		editor.putBoolean("GET LAST ACROSTIC", get_last_acrostic.isChecked());
		editor.putBoolean("USE DICTIONARY", check_use_dictionary.isChecked());
		editor.putBoolean("USE ALL ACROSTICS",
				check_use_all_acrostics.isChecked());

		editor.putBoolean("EDIT ACROSTIC CHECK INSERT",
				insert_acrostics.isChecked());
		editor.putBoolean("EDIT ACROSTIC CHECK DELETE",
				delete_acrostics.isChecked());
		editor.putBoolean("EDIT ACROSTIC CHECK EDIT",
				edit_acrostics.isChecked());
		editor.commit();
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

	@SuppressWarnings("deprecation")
	public void setViews() {
		// LAYOUTS:
		setTitle("EDIT ACROSTICS");
		input_types_layout = (LinearLayout) findViewById(R.id.edit_acrostics_horizontal_type_input_layout);
		mne_layout = (LinearLayout) findViewById(R.id.edit_acrostics_mne_layout);
		peg_layout = (LinearLayout) findViewById(R.id.edit_acrostics_peg_layout);

		// BUTTONS:
		go_left = (Button) findViewById(R.id.edit_acrostics_left);
		go_right = (Button) findViewById(R.id.edit_acrostics_right);
		get_acrostic = (Button) findViewById(R.id.get_acrostic);
		edit_acrostic = (Button) findViewById(R.id.edit_acrostic);
		vocabulary_switch = (Button) findViewById(R.id.edit_acr_vocabulary_switch);
		do_upload_image = (Button) findViewById(R.id.edit_acrostic_upload_image);

		// CHECKBOXES:
		check_use_dictionary = (CheckBox) findViewById(R.id.check_use_dictionary);
		check_use_all_acrostics = (CheckBox) findViewById(R.id.check_use_all_acrostics);
		get_next_acrostic = (CheckBox) findViewById(R.id.get_next_acrostic);
		get_last_acrostic = (CheckBox) findViewById(R.id.get_last_acrostic);
		check_change_name = (CheckBox) findViewById(R.id.change_acrostic_name);

		// EDITTEXTS:
		// input_acrostic_name = (EditText)
		// findViewById(R.id.input_acrostic_name);
		input_new_name = (EditText) findViewById(R.id.input_new_acrostic_name);
		information_text = (EditText) findViewById(R.id.edit_acrostic_information_text);
		acrostic_text = (EditText) findViewById(R.id.edit_acrostic_acrostic_text);
		mnemonic_text = (EditText) findViewById(R.id.edit_acrostic_mnemonic_text);
		peglist_text = (EditText) findViewById(R.id.edit_acrostic_peglist_text);
		// input_word_layout = (LinearLayout)
		// findViewById(R.id.edit_acr_input_word_layout);
		// enter_acrostic_name = (AutoCompleteTextView)
		// findViewById(R.id.enter_acrostic_name);
		enter_acrostic_name = (EditText) findViewById(R.id.enter_acrostic_name);
		// input_word_layout.addView(enter_acrostic_name);

		// enter_acrostic_name.setDropDownAnchor(R.id.input_acrostic_name);
		input_acrostic_name = (EditText) findViewById(R.id.input_acrostic_name);
		// enter_acrostic_name.setThreshold(2);

		// IMAGEVIEWS:
		img = (ImageView) findViewById(R.id.edit_acrostic_image);

		// ListView:
		// select_acrostic_names = (ListView)
		// findViewById(R.id.select_acrostic_names);

		// SCROLLVIEW:
		edit_acrostics_scroll = (ScrollView) findViewById(R.id.edit_acrostics_scroll);

		// SPINNERS:
		select_acrostic_tables = (Spinner) findViewById(R.id.select_acrostic_tables);
		select_acrostic_names = (Spinner) findViewById(R.id.select_acrostic_names);
		tablesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		tablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_acrostic_tables.setAdapter(tablesAdapter);

		// AUTOCOMPLETETEXTVIEWS:
		// namesAdapter = new ArrayAdapter<String>(this,R.layout.dropdown_item,
		// new ArrayList<String>());
		// namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// namesAdapter = new
		// ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, new
		// ArrayList<String>());
		// namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		namesAdapter = new CustomAdapter(act, list_names, is_exists);
		select_acrostic_names.setAdapter(namesAdapter);

		// RADIOBUTTONS:
		insert_acrostics = (RadioButton) findViewById(R.id.insert_acrostics);
		delete_acrostics = (RadioButton) findViewById(R.id.delete_acrostics);
		edit_acrostics = (RadioButton) findViewById(R.id.edit_acrostics);

		// TEXTVIEWS:
		prompt_new_name = (TextView) findViewById(R.id.prompt_new_acrostic_name);
		prompt_inf = (TextView) findViewById(R.id.prompt_edit_acrostic_information);
		prompt_acr = (TextView) findViewById(R.id.prompt_edit_acrostic_acrostic);
		prompt_mne = (TextView) findViewById(R.id.prompt_edit_acrostic_mnemonic);
		prompt_peg = (TextView) findViewById(R.id.prompt_edit_acrostic_peglist);
		prompt_img = (TextView) findViewById(R.id.prompt_edit_acrostic_image);
		prompt_name_exists = (TextView) findViewById(R.id.prompt_name_exists);
		results = (TextView) findViewById(R.id.show_acrostic_results);

		@SuppressWarnings("unused")
		int measuredWidth = 0;
		int measuredHeight = 0;
		WindowManager w = getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			measuredWidth = size.x;
			measuredHeight = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			measuredWidth = d.getWidth();
			measuredHeight = d.getHeight();
		}
		linear_params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, measuredHeight / 2);
		information_text.setLayoutParams(linear_params);
		acrostic_text.setLayoutParams(linear_params);
		mnemonic_text.setLayoutParams(linear_params);
		peglist_text.setLayoutParams(linear_params);
		img.setLayoutParams(linear_params);

		information_text.setVisibility(View.VISIBLE);
		prompt_inf.setVisibility(View.VISIBLE);
		acrostic_text.setVisibility(View.GONE);
		prompt_acr.setVisibility(View.GONE);
		mnemonic_text.setVisibility(View.GONE);
		prompt_mne.setVisibility(View.GONE);
		peglist_text.setVisibility(View.GONE);
		prompt_peg.setVisibility(View.GONE);
		img.setVisibility(View.GONE);
		prompt_img.setVisibility(View.GONE);
	}

	public void loadButtons() {
		get_acrostic.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		edit_acrostic.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		vocabulary_switch.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_upload_image.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		go_left.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		go_right.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setListeners() {
		/*
		 * enter_acrostic_name.setOnItemClickListener(new OnItemClickListener(){
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View arg1,
		 * int pos, long id) {
		 * 
		 * } });
		 */
		select_acrostic_names
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						System.out.println("ACTV CLICKED! ="
								+ list_names.get(arg2).toString());
						// enter_acrostic_name.dismissDropDown();
						// enter_acrostic_name.setAdapter(null);
						// enter_acrostic_name.setText(sav_ent_acr_tex);
						name = list_names.get(arg2).toString();
						if (check_use_all_acrostics.isChecked()) {
							String[] namespl = name.split("--");
							name = namespl[0];
							select_acrostic_tables.setSelection(tablesAdapter
									.getPosition(namespl[1]));
						}
						input_acrostic_name.setText(name);
						/*
						 * for (int
						 * i=0;i<enter_acrostic_name.getAdapter().getCount
						 * ();i++){
						 * 
						 * }
						 */
						// enter_acrostic_name.setAdapter(namesAdapter);
						get_acrostic.performClick();

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		/*
		 * enter_acrostic_name.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { if
		 * (!enter_acrostic_name.isPopupShowing()) {
		 * enter_acrostic_name.showDropDown(); } sav_ent_acr_tex =
		 * enter_acrostic_name.getText().toString(); } });
		 */

		check_use_dictionary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (check_use_dictionary.isChecked()) {
					prompt_name_exists.setVisibility(View.VISIBLE);
					check_use_all_acrostics.setChecked(false);
				} else {
					prompt_name_exists.setVisibility(View.GONE);
				}
				refreshTables();
			}
		});
		check_use_all_acrostics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				check_use_dictionary.setChecked(false);
			}
		});

		go_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (input_fields_index == 0) {
					return;
				} else {
					input_fields_index--;
					while (input_fields.get(input_fields_index) == false
							&& input_fields_index > 0) {
						input_fields_index--;
					}
					System.out.println("input_fields_index="
							+ input_fields_index);
					switch (input_fields_index) {
					case 0:// INF
						information_text.setVisibility(View.VISIBLE);
						prompt_inf.setVisibility(View.VISIBLE);
						acrostic_text.setVisibility(View.GONE);
						prompt_acr.setVisibility(View.GONE);
						mnemonic_text.setVisibility(View.GONE);
						prompt_mne.setVisibility(View.GONE);
						peglist_text.setVisibility(View.GONE);
						prompt_peg.setVisibility(View.GONE);
						img.setVisibility(View.GONE);
						prompt_img.setVisibility(View.GONE);
						break;
					case 1:// ACR
						information_text.setVisibility(View.GONE);
						prompt_inf.setVisibility(View.GONE);
						acrostic_text.setVisibility(View.VISIBLE);
						prompt_acr.setVisibility(View.VISIBLE);
						mnemonic_text.setVisibility(View.GONE);
						prompt_mne.setVisibility(View.GONE);
						peglist_text.setVisibility(View.GONE);
						prompt_peg.setVisibility(View.GONE);
						img.setVisibility(View.GONE);
						prompt_img.setVisibility(View.GONE);
						break;
					case 2:// MNE
						information_text.setVisibility(View.GONE);
						prompt_inf.setVisibility(View.GONE);
						acrostic_text.setVisibility(View.GONE);
						prompt_acr.setVisibility(View.GONE);
						mnemonic_text.setVisibility(View.VISIBLE);
						prompt_mne.setVisibility(View.VISIBLE);
						peglist_text.setVisibility(View.GONE);
						prompt_peg.setVisibility(View.GONE);
						img.setVisibility(View.GONE);
						prompt_img.setVisibility(View.GONE);
						break;
					case 3:// PEG
						information_text.setVisibility(View.GONE);
						prompt_inf.setVisibility(View.GONE);
						acrostic_text.setVisibility(View.GONE);
						prompt_acr.setVisibility(View.GONE);
						mnemonic_text.setVisibility(View.GONE);
						prompt_mne.setVisibility(View.GONE);
						peglist_text.setVisibility(View.VISIBLE);
						prompt_peg.setVisibility(View.VISIBLE);
						img.setVisibility(View.GONE);
						prompt_img.setVisibility(View.GONE);
						break;
					}

				}
			}
		});
		go_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (input_fields_index == 4) {
					return;
				} else {
					input_fields_index++;
					while (input_fields.get(input_fields_index) == false
							&& input_fields_index < 4) {
						input_fields_index++;
					}
					System.out.println("input_fields_index="
							+ input_fields_index);
					switch (input_fields_index) {
					case 1:// ACR
						information_text.setVisibility(View.GONE);
						prompt_inf.setVisibility(View.GONE);
						acrostic_text.setVisibility(View.VISIBLE);
						prompt_acr.setVisibility(View.VISIBLE);
						mnemonic_text.setVisibility(View.GONE);
						prompt_mne.setVisibility(View.GONE);
						peglist_text.setVisibility(View.GONE);
						prompt_peg.setVisibility(View.GONE);
						img.setVisibility(View.GONE);
						prompt_img.setVisibility(View.GONE);
						break;
					case 2:// MNE
						information_text.setVisibility(View.GONE);
						prompt_inf.setVisibility(View.GONE);
						acrostic_text.setVisibility(View.GONE);
						prompt_acr.setVisibility(View.GONE);
						mnemonic_text.setVisibility(View.VISIBLE);
						prompt_mne.setVisibility(View.VISIBLE);
						peglist_text.setVisibility(View.GONE);
						prompt_peg.setVisibility(View.GONE);
						img.setVisibility(View.GONE);
						prompt_img.setVisibility(View.GONE);
						break;
					case 3:// PEG
						information_text.setVisibility(View.GONE);
						prompt_inf.setVisibility(View.GONE);
						acrostic_text.setVisibility(View.GONE);
						prompt_acr.setVisibility(View.GONE);
						mnemonic_text.setVisibility(View.GONE);
						prompt_mne.setVisibility(View.GONE);
						peglist_text.setVisibility(View.VISIBLE);
						prompt_peg.setVisibility(View.VISIBLE);
						img.setVisibility(View.GONE);
						prompt_img.setVisibility(View.GONE);
						break;
					case 4:// IMG
						img.setVisibility(View.VISIBLE);
						prompt_img.setVisibility(View.VISIBLE);
						information_text.setVisibility(View.GONE);
						prompt_inf.setVisibility(View.GONE);
						acrostic_text.setVisibility(View.GONE);
						prompt_acr.setVisibility(View.GONE);
						mnemonic_text.setVisibility(View.GONE);
						prompt_mne.setVisibility(View.GONE);
						peglist_text.setVisibility(View.GONE);
						prompt_peg.setVisibility(View.GONE);
						break;
					}

				}

			}
		});

		edit_acrostics_scroll.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				information_text.getParent()
						.requestDisallowInterceptTouchEvent(false);
				acrostic_text.getParent().requestDisallowInterceptTouchEvent(
						false);
				mnemonic_text.getParent().requestDisallowInterceptTouchEvent(
						false);
				peglist_text.getParent().requestDisallowInterceptTouchEvent(
						false);
				return false;
			}
		});
		information_text.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		acrostic_text.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		mnemonic_text.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		peglist_text.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		do_upload_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new doLoadImage().execute();
			}
		});

		edit_acrostic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				table_name = select_acrostic_tables.getSelectedItem()
						.toString();
				name = input_acrostic_name.getText().toString();
				info = information_text.getText().toString();
				info = info.replace("'", "`");
				info = info.replace("\"", "`");
				acrostic = acrostic_text.getText().toString();
				acrostic = acrostic.replace("'", "`");
				acrostic = acrostic.replace("\"", "`");
				boolean exists = false;
				List<String> col_list = getColumnsNotImage();
				String col_list_str = Helpers.joinList(col_list, ",");
				if (col_list.size() == 0) {
					results.setText("NAME NOT FOUND!");
				}
				c = MainLfqActivity.getAcrosticsDb().rawQuery(
						"SELECT " + col_list_str + " FROM " + table_name
								+ " WHERE Name='" + name + "'", null);
				if (c.moveToFirst()) {
					exists = true;
					if (c.getColumnIndex("Mnemonics") != -1) {
						mnemonic = mnemonic_text.getText().toString();
						mnemonic = mnemonic.replace("'", "`");
						mnemonic = mnemonic.replace("\"", "`");
					}
					if (c.getColumnIndex("Peglist") != -1) {
						peglist = peglist_text.getText().toString();
						peglist = peglist.replace("'", "`");
						peglist = peglist.replace("\"", "`");
					}
				}
				if (insert_acrostics.isChecked()) {
					if (exists) {
						results.setText("ALREADY EXISTS.");
						return;
					}
					sql = "";
					if (c.moveToFirst()) {
						do {
							if (c.getString(c.getColumnIndex("Name")) != null) {
								if (c.getString(c.getColumnIndex("Name"))
										.equals(name)) {
									results.setText("ENTRY ALREADY EXISTS. NOT INSERTED.");
									return;
								}
							}
						} while (c.moveToNext());
					}
					values.clear();
					values.put("Name", name);
					values.put("Information", info);
					values.put("Acrostics", acrostic);
					sql = "INSERT INTO " + Helpers.db_prefix + "acrostics."
							+ table_name + " (Name, Information, Acrostics";
					String sql_end = ") VALUES('" + name + "','" + info + "','"
							+ acrostic + "'";
					if (c.getColumnIndex("Mnemonics") != -1) {
						values.put("Mnemonics", mnemonic);
						sql += ", Mnemonics";
						sql_end += ",'" + mnemonic + "'";
					}
					if (c.getColumnIndex("Peglist") != -1) {
						values.put("Peglist", peglist);
						sql += ", Peglist";
						sql_end += ",'" + peglist + "'";
					}
					String input = "";
					col_list.clear();
					col_list = getColumnTypes();
					for (int i = 0; i < col_list.size(); i++) {
						values.put(col_list.get(i), inputs.get(i).getText()
								.toString());
						sql += ", " + col_list.get(i) + "";
						input = inputs.get(i).getText().toString();
						input = input.replace("'", "`");
						input = input.replace("\"", "`");
						sql_end += ",'" + input + "'";
					}

					MainLfqActivity.getAcrosticsDb().insert(table_name, null,
							values);
					sql += sql_end + ")";
					autosync_text = "";
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[] image)
					autosync_text = Synchronize.autoSync(sql, "acr_db",
							"insert", table_name, name, false, null);

					c.close();
					results.setText("RESULTS: inserted into " + table_name
							+ ", " + name + "." + autosync_text);
					if (check_use_dictionary.isChecked()) {
						if (isExist(name)) {
							if (list_names.indexOf("name") != -1) {
								is_exists.set(list_names.indexOf("name"), true);
								namesAdapter = new CustomAdapter(act,
										list_names, is_exists);
								select_acrostic_names.setAdapter(namesAdapter);
							}
						}
					}
				}
				if (edit_acrostics.isChecked()) {
					// TEST FIRST IF name EXISTS
					if (!exists) {
						results.setText("RESULTS: " + name + " does not exist.");
						return;
					} else {
						values.clear();
						String new_name = name;
						if (check_change_name.isChecked()) {
							new_name = input_new_name.getText().toString();
							new_name = new_name.replace("'", "`");
							new_name = new_name.replace("\"", "`");
						}
						values.put("Name", new_name);
						values.put("Information", info);
						values.put("Acrostics", acrostic);
						sql = "UPDATE " + Helpers.db_prefix + "acrostics."
								+ table_name + " SET Name='" + new_name
								+ "', Information='" + info + "', Acrostics='"
								+ acrostic + "'";
						if (c.getColumnIndex("Mnemonics") != -1) {
							values.put("Mnemonics", mnemonic);
							sql += ", Mnemonics='" + mnemonic + "'";
						}
						if (c.getColumnIndex("Peglist") != -1) {
							values.put("Peglist", peglist);
							sql += ", Peglist='" + peglist + "'";
						}
						col_list.clear();
						col_list = getColumnTypes();
						for (int i = 0; i < col_list.size(); i++) {
							values.put(col_list.get(i), inputs.get(i).getText()
									.toString());
							sql += ", " + col_list.get(i) + "='"
									+ inputs.get(i).getText().toString() + "'";
						}
						MainLfqActivity.getAcrosticsDb().update(table_name,
								values, "Name=?", new String[] { name });
						sql += " WHERE Name='" + name + "'";
						// autoSync(sql, db, action, table, name, bool is_image,
						// byte[] image)
						autosync_text = Synchronize.autoSync(sql, "acr_db",
								"update", table_name, name, false, null);
						results.setText("RESULTS: Updated " + table_name + ", "
								+ name + "." + autosync_text);

					}
					c.close();
				}
				if (delete_acrostics.isChecked()) {
					if (c.moveToFirst()) {
						MainLfqActivity.getAcrosticsDb().execSQL(
								"DELETE FROM " + table_name + " WHERE Name='"
										+ name + "'");
						sql = "DELETE FROM `" + Helpers.db_prefix
								+ "acrostics`.`" + table_name
								+ "` WHERE Name='" + name + "'";
						// autoSync(sql, db, action, table, name, bool is_image,
						// byte[] image)
						autosync_text = Synchronize.autoSync(sql, "acr_db",
								"delete", table_name, name, false, null);
						results.setText("RESULTS: Deleted from : " + table_name
								+ ", " + name + ". " + autosync_text);
					} else {
						results.setText("NOT DELETED. ENTRY DOESN'T EXIST IN US ER DATABASE.");
					}
					if (list_names.indexOf("name") != -1) {
						is_exists.remove(list_names.indexOf("name"));
						list_names.remove(list_names.indexOf("name"));
						namesAdapter = new CustomAdapter(act, list_names,
								is_exists);
						select_acrostic_names.setAdapter(namesAdapter);
					}

				}
				if (check_use_dictionary.isChecked()) {
					doShowExists(name);
				}
			}
		});

		get_acrostic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				table_name = select_acrostic_tables.getSelectedItem()
						.toString();
				name = input_acrostic_name.getText().toString();
				if (check_use_dictionary.isChecked()) {
					getDictionaryWord();
					return;
				}
				opt = "none";
				if (get_next_acrostic.isChecked()) {
					opt = "get_next";
				}
				if (get_last_acrostic.isChecked()) {
					opt = "get_last";
				}
				col_list.clear();
				col_list = getColumnsNotImage();
				String col_list_str = Helpers.joinList(col_list, ",");
				System.out.println("col_list_str=" + col_list_str);
				// c = null;
				if (opt.equals("none")) {
					c = MainLfqActivity.getAcrosticsDb().rawQuery(
							"SELECT " + col_list_str + " FROM " + table_name
									+ " WHERE Name='" + name + "'", null);

				}
				if (opt.equals("get_next")) {
					c = MainLfqActivity.getAcrosticsDb().rawQuery(
							"SELECT " + col_list_str + " FROM " + table_name
									+ " WHERE LOWER(Name)>'"
									+ name.toLowerCase(Locale.US)
									+ "' ORDER BY LOWER(Name) LIMIT 1", null);
					System.out.println("get next " + c.getColumnCount());
				}
				if (opt.equals("get_last")) {
					c = MainLfqActivity.getAcrosticsDb().rawQuery(
							"SELECT " + col_list_str + " FROM " + table_name
									+ " WHERE LOWER(Name)<'"
									+ name.toLowerCase(Locale.US)
									+ "' ORDER BY LOWER(Name) DESC LIMIT 1",
							null);
					System.out.println("get last " + c.getColumnCount());
				}
				if (c.moveToFirst()) {
					if (opt.equals("get_last") || opt.equals("get_next")) {
						name = c.getString(c.getColumnIndex("Name"));
						input_acrostic_name.setText(name);
					}
					System.out.println("move 1st " + c.getColumnCount());

					results.setText("Found " + name);
					information_text.setText(c.getString(c
							.getColumnIndex("Information")));
					acrostic_text.setText(c.getString(c
							.getColumnIndex("Acrostics")));
					if (c.getColumnIndex("Mnemonics") != -1) {
						mnemonic_text.setText(c.getString(c
								.getColumnIndex("Mnemonics")));
					}
					if (c.getColumnIndex("Peglist") != -1) {
						peglist_text.setText(c.getString(c
								.getColumnIndex("Peglist")));
					}
					col_list.clear();
					col_list = getColumnTypes();
					for (int i = 0; i < col_list.size(); i++) {
						inputs.get(i).setText(
								c.getString(c.getColumnIndex(col_list.get(i))));
					}
					getImage();

				} else {
					results.setText("RESULTS: doesn't exist.");
				}
				c.close();
			}
		});

		get_last_acrostic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (get_last_acrostic.isChecked()) {
					get_next_acrostic.setChecked(false);
				}
			}
		});
		get_next_acrostic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (get_next_acrostic.isChecked()) {
					get_last_acrostic.setChecked(false);
				}
			}
		});

		vocabulary_switch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int now_id = select_acrostic_tables.getSelectedItemPosition();
				int voc_id = tablesAdapter.getPosition("vocabulary");
				if (now_id != voc_id) {
					last_id = select_acrostic_tables.getSelectedItemPosition();
					select_acrostic_tables.setSelection(voc_id);
				} else {
					select_acrostic_tables.setSelection(last_id);
				}
				refreshTables();
			}
		});

		enter_acrostic_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// namesAdapter = new CustomAdapter(act, list_names, is_exists);
				// enter_acrostic_name.setAdapter(namesAdapter);
				/*
				 * if (namesAdapter != null && enter_acrostic_name != null) { if
				 * (namesAdapter.getCount() > 0) { if
				 * (!enter_acrostic_name.isPopupShowing()) {
				 * enter_acrostic_name.showDropDown(); } } }
				 */

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				sav_ent_acr_tex = enter_acrostic_name.getText().toString();
				if (enter_acrostic_name.getText().length() < 2
						|| enter_acrostic_name.getText().length() < sav_ent_acr_len) {
					if (enter_acrostic_name.getText().length() < sav_ent_acr_len) {
						list_names.clear();
						is_exists.clear();
						namesAdapter = new CustomAdapter(act, list_names,
								is_exists);
						select_acrostic_names.setAdapter(namesAdapter);
					}
					sav_ent_acr_len = enter_acrostic_name.getText().length();
					System.out.println("RETURNING!");
					return;
				}
				sav_ent_acr_len = enter_acrostic_name.getText().length();
				new doLoadWords().execute();

			}
		});

		check_change_name.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_change_name.isChecked()) {
					prompt_new_name.setVisibility(View.VISIBLE);
					input_new_name.setVisibility(View.VISIBLE);
				} else {
					prompt_new_name.setVisibility(View.GONE);
					input_new_name.setVisibility(View.GONE);
				}
			}
		});

	}

	public void getDictionaryWord() {
		if (namesAdapter.getCount() == 0) {
			return;
		}
		opt = "none";
		if (get_next_acrostic.isChecked()) {
			opt = "get_next";
		}
		if (get_last_acrostic.isChecked()) {
			opt = "get_last";
		}
		if (opt.equals("none")) {
			c = MainLfqActivity.getDictionaryDb()
					.rawQuery(
							"SELECT * FROM dictionarya WHERE Word='" + name
									+ "'", null);
		}
		if (opt.equals("get_next")) {
			c = MainLfqActivity.getDictionaryDb().rawQuery(
					"SELECT * FROM dictionarya WHERE LOWER(Word)>'"
							+ name.toLowerCase(Locale.US)
							+ "' ORDER BY LOWER(Word) LIMIT 1", null);
		}
		if (opt.equals("get_last")) {
			c = MainLfqActivity.getDictionaryDb().rawQuery(
					"SELECT * FROM dictionarya WHERE LOWER(Word)<'"
							+ name.toLowerCase(Locale.US)
							+ "' ORDER BY LOWER(Word) DESC LIMIT 1", null);
		}
		if (c.moveToFirst()) {
			if (opt.equals("get_last") || opt.equals("get_next")) {
				name = c.getString(c.getColumnIndex("Word"));
				input_acrostic_name.setText(name);
			}
			doShowExists(name);

			results.setText("Found " + name);
			information_text
					.setText(c.getString(c.getColumnIndex("Definition")));
			acrostic_text.setText("");
		} else {
			results.setText("RESULTS: doesn't exist.");
		}
		c.close();
	}

	public void doShowExists(String myName) {
		name = myName;
		c2 = MainLfqActivity.getAcrosticsDb()
				.rawQuery(
						"SELECT Name FROM " + table_name + " WHERE Name='"
								+ name + "'", null);
		String prompt_name = name;
		if (prompt_name.length() > 10) {
			prompt_name = name.substring(0, 10) + "..";
		}
		if (c2.getCount() > 0) {
			prompt_name_exists.setTextSize(12);
			prompt_name_exists.setText(Html.fromHtml("<b>" + prompt_name
					+ " EXISTS</b>"));
		} else {
			prompt_name_exists.setTextSize(11);
			prompt_name_exists.setText(Html.fromHtml("<b>" + prompt_name
					+ " NOT EXISTS</b>"));
		}
		c2.close();
	}

	public boolean isExist(String myName) {
		Cursor my_c = MainLfqActivity.getAcrosticsDb().rawQuery(
				"SELECT Name FROM " + table_name + " WHERE Name='" + myName
						+ "'", null);
		boolean ret = false;
		if (my_c.getCount() > 0) {
			ret = true;
		}
		my_c.close();
		return ret;
	}

	public void getImage() {
		// GET IMAGE:
		boolean is_found_image = false;
		c = MainLfqActivity.getAcrosticsDb().rawQuery(
				"SELECT Image FROM " + table_name + " WHERE Name='" + name
						+ "'", null);
		System.out.println("SELECT Image FROM " + table_name + " WHERE Name='"
				+ name + "'");
		if (c.getCount() > 0) {
			try {
				if (c.moveToFirst()) {
					if (c.getBlob(0) != null) {
						if (c.getBlob(0).length > 0) {
							is_found_image = true;
							byte[] imageByteArray = c.getBlob(0);
							selected_image = BitmapFactory.decodeByteArray(
									imageByteArray, 0, imageByteArray.length);
							img.setImageBitmap(selected_image);
							input_fields.set(4, true);
						}
					}
				}
			} catch (Exception e) {
				c.close();
				System.out.println(e.getMessage());
			}
		}
		if (is_found_image == false) {
			img.setImageDrawable(null);
			results.setText(Html.fromHtml("<b>" + name.toUpperCase(Locale.US)
					+ ":IMAGE NOT FOUND</b>"));
			input_fields.set(4, false);
		}
		c.close();
	}

	public List<String> getColumnsNotImage() {
		col_list.clear();
		c2 = MainLfqActivity.getAcrosticsDb().rawQuery(
				"SELECT * FROM " + table_name + " LIMIT 1", null);
		String[] col_names = c2.getColumnNames();
		c2.close();
		for (int i = 0; i < col_names.length; i++) {
			if (!col_names[i].equals("android_metadata")
					&& !col_names[i].equals("sqlite_sequence")
					&& !col_names[i].equals("Image")
					&& !col_names[i].equals("Has_Image")) {
				col_list.add(col_names[i]);
			}
		}
		return col_list;
	}

	public List<String> getColumnTypes() {
		col_list.clear();
		c2 = MainLfqActivity.getAcrosticsDb().rawQuery(
				"SELECT * FROM " + table_name + " WHERE Name='" + name + "'",
				null);
		String cols[] = c2.getColumnNames();
		c2.close();
		for (int i = 0; i < cols.length; i++) {
			if (!cols[i].equals("android_metadata")
					&& !cols[i].equals("sqlite_sequence")
					&& !cols[i].equals("_id") && !cols[i].equals("Name")
					&& !cols[i].equals("Information")
					&& !cols[i].equals("Acrostics") && !cols[i].equals("Image")
					&& !cols[i].equals("Has_Image")
					&& !cols[i].equals("Mnemonics")
					&& !cols[i].equals("Peglist")) {
				col_list.add(cols[i]);
			}
		}
		return col_list;
	}

	public void refreshTables() {
		System.out.println("REFRESH TABLES CALLED");
		input_types_layout.removeAllViews();
		table_name = select_acrostic_tables.getSelectedItem().toString();
		if (check_use_dictionary.isChecked()) {
			if (list_names.size() > 0) {
				name = input_acrostic_name.getText().toString();
			}
			doShowExists(name);
		}
		col_list.clear();
		col_list = getColumnsNotImage();
		if (col_list.size() > 0) {
			mne_layout.setVisibility(View.GONE);
			peg_layout.setVisibility(View.GONE);
			inputs = new ArrayList<EditText>();
			input_fields.set(2, false);
			input_fields.set(3, false);
			for (int i = 0; i < col_list.size(); i++) {
				if (col_list.get(i).equals("Mnemonics")) {
					mne_layout.setVisibility(View.VISIBLE);
					input_fields.set(2, true);
				}
				if (col_list.get(i).equals("Peglist")) {
					peg_layout.setVisibility(View.VISIBLE);
					input_fields.set(3, true);
				}
				if (!col_list.get(i).equals("_id")
						&& !col_list.get(i).equals("Name")
						&& !col_list.get(i).equals("Information")
						&& !col_list.get(i).equals("Acrostics")
						&& !col_list.get(i).equals("Mnemonics")
						&& !col_list.get(i).equals("Peglist")
						&& !col_list.get(i).equals("Has_Image")) {
					TextView prompt_tv = new TextView(this);
					prompt_tv.setText(col_list.get(i) + ":");
					input_types_layout.addView(prompt_tv);
					EditText inp_et = new EditText(this);
					inp_et.setWidth(200);
					inp_et.setMaxLines(2);
					inp_et.setBackgroundResource(R.drawable.rounded_edittext_red);
					inputs.add(inp_et);
					input_types_layout.addView(inp_et);
				}
			}
		}
		// setWords();

	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
			c = MainLfqActivity.getAcrosticsDb().rawQuery(
					"SELECT name FROM sqlite_master "
							+ " WHERE type='table' ORDER BY name", null);
			tablesAdapter.clear();
			if (c.moveToFirst()) {
				do {
					if (!c.getString(0).equals("android_metadata")
							&& !c.getString(0).equals("sqlite_sequence")) {
						tablesAdapter.add(c.getString(0));
					}
				} while (c.moveToNext());
			} else {
				results.setText("nothing found");
			}
			c.close();
			table_name = sharedPref.getString("Edit Acrostic Table",
					tablesAdapter.getItem(0));
			select_acrostic_tables.setSelection(tablesAdapter
					.getPosition(table_name));
			check_use_dictionary.setChecked(sharedPref.getBoolean(
					"USE DICTIONARY", false));
			check_use_all_acrostics.setChecked(sharedPref.getBoolean(
					"USE ALL ACROSTICS", false));
			name = sharedPref.getString("Edit Acrostic Name", "");
			refreshTables();
			// enter_acrostic_name.setOnTouchListener(select_name_listener);
			// enter_acrostic_name.setOnItemSelectedListener(select_name_listener);
			select_acrostic_tables.setOnTouchListener(select_table_listener);
			select_acrostic_tables
					.setOnItemSelectedListener(select_table_listener);
			setListeners();
			results.setText("");
			input_acrostic_name.setText(name);
			get_next_acrostic.setChecked(sharedPref.getBoolean(
					"GET NEXT ACROSTIC", false));
			get_last_acrostic.setChecked(sharedPref.getBoolean(
					"GET LAST ACROSTIC", false));
			insert_acrostics.setChecked(sharedPref.getBoolean(
					"EDIT ACROSTIC CHECK INSERT", false));
			delete_acrostics.setChecked(sharedPref.getBoolean(
					"EDIT ACROSTIC CHECK DELETE", false));
			edit_acrostics.setChecked(sharedPref.getBoolean(
					"EDIT ACROSTIC CHECK EDIT", false));
			information_text.setText(sharedPref.getString(
					"Edit Information Text", ""));
			acrostic_text.setText(sharedPref
					.getString("Edit Acrostic Text", ""));
			peglist_text.setText(sharedPref.getString("Edit Peglist Text", ""));

		}
	}

	public class SelectTableListener implements
			AdapterView.OnItemSelectedListener, View.OnTouchListener {
		public Boolean userSelectTable = false;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("ON TOUCH TABLE CALLED?...");
			userSelectTable = true;
			refreshTables();
			if (tablesAdapter.getCount() == 1) {
				doSelected();
			}
			if (tablesAdapter.getCount() > 0) {
				if (select_acrostic_tables
						.getItemAtPosition(0)
						.toString()
						.equals(select_acrostic_tables.getSelectedItem()
								.toString())) {
					doSelected();
				}
			}
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			refreshTables();
			doSelected();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

		public void doSelected() {
			if (!userSelectTable) {
				System.out.println("doSelected, userSelect=false");
			}
			if (tablesAdapter.getCount() == 0) {
				return;
			}
			if (userSelectTable) {
				System.out.println("doSelected, userSelectTable=true");
				userSelectTable = false;
			}
		}
	}

	class doLoadImage extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			results.setText(Html.fromHtml("<b>LOADING IMAGE FOR "
					+ input_acrostic_name.getText().toString() + "...</b>"));
		}

		@Override
		protected String doInBackground(String... params) {
			values.clear();
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

		@Override
		protected void onPostExecute(String file_url) {
			is_image_load = true;
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		switch (requestCode) {
		case SELECT_PHOTO:
			image_data = null;
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				new doBeginImageLoad().execute(selectedImage);
			}
			break;
		}
	}

	class doBeginImageLoad extends AsyncTask<Uri, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Uri... params) {
			InputStream is = null;
			InputStream is2 = null;
			try {
				is = getContentResolver().openInputStream(params[0]);
				is2 = getContentResolver().openInputStream(params[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			selected_image = BitmapFactory.decodeStream(is);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			// selected_image.compress(CompressFormat.JPEG, 0, os);
			int nRead;
			byte[] data = new byte[16384];
			try {
				while ((nRead = is2.read(data, 0, data.length)) != -1) {
					os.write(data, 0, nRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			image_data = os.toByteArray();
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

		@Override
		protected void onPostExecute(String file_url) {
			try {
				img.setImageBitmap(selected_image);
				insertImage(image_data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void insertImage(byte[] myImage) throws IOException {
		if (myImage != null) {
			autosync_text = "";
			values.clear();
			values.put("Image", myImage);
			values.put("Has_Image", "yes");
			table_name = select_acrostic_tables.getSelectedItem().toString();
			MainLfqActivity.getAcrosticsDb().update(table_name, values,
					"Name='" + input_acrostic_name.getText().toString() + "'",
					null);
			// SYNC TO LFQ DATABASE:
			sql = "UPDATE " + Helpers.db_prefix + "acrostics."
					+ select_acrostic_tables.getSelectedItem().toString()
					+ " SET Has_Image='yes' WHERE Name='"
					+ input_acrostic_name.getText().toString() + "'";
			// autoSync(sql, db, action, table, name, bool is_image, byte[]
			// image)
			autosync_text += Synchronize.autoSync(sql, "acr_db",
					"upload_image", table_name, input_acrostic_name.getText()
							.toString(), true, myImage);

			results.setText(Html.fromHtml("<b>UPLOADED IMAGE FOR "
					+ input_acrostic_name.getText().toString() + ". "
					+ autosync_text + "!</b>"));
		} else {
			results.setText(Html.fromHtml("<b>IMAGE "
					+ input_acrostic_name.getText().toString()
					+ " FAILED TO LOAD."));
		}
		is_image_load = false;
	}

	class doLoadWords extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			list_names.clear();
			is_exists.clear();
			select_acrostic_names.setEnabled(false);
			enter_acrostic_name.setEnabled(false);
			// is_add_names = false;
			// namesAdapter = new CustomAdapter(act, list_names, is_exists);
			// select_acrostic_names.setAdapter(namesAdapter);
		}

		@Override
		protected String doInBackground(String... params) {
			// System.out.println("s=" + s + ", start=" + start + ", count="+
			// after);
			int count = enter_acrostic_name.getText().toString().length();
			sav_ent_acr_tex = enter_acrostic_name.getText().toString();
			sav_ent_acr_len = count;
			is_first_load = false;
			table_name = select_acrostic_tables.getSelectedItem().toString();
			name = enter_acrostic_name.getText().toString();
			System.out.println("name=" + name);
			String[] selectionArgs = { name + "%" };
			if (check_use_dictionary.isChecked()) {
				c2 = MainLfqActivity
						.getDictionaryDb()
						.rawQuery(
								"SELECT DISTINCT Word FROM dictionarya WHERE Word LIKE ? ORDER BY Word COLLATE NOCASE",
								selectionArgs);
			} else if (check_use_all_acrostics.isChecked()) {
				Cursor c_tables = MainLfqActivity.getAcrosticsDb().rawQuery(
						"SELECT name FROM sqlite_master "
								+ " WHERE type='table' ORDER BY name", null);
				// is_exists.clear();
				if (c_tables.moveToFirst()) {
					do {
						if (!c_tables.getString(0).equals("android_metadata")
								&& !c_tables.getString(0).equals(
										"sqlite_sequence")) {
							Cursor c_1_table = MainLfqActivity.getAcrosticsDb()
									.rawQuery(
											"SELECT Name FROM "
													+ c_tables.getString(0)
													+ " WHERE Name LIKE '"
													+ name + "%'", null);
							if (c_1_table.moveToFirst()) {
								do {
									publishProgress(c_1_table.getString(0)
											+ "--" + c_tables.getString(0));
									is_exists.add(false);
								} while (c_1_table.moveToNext());
							}
							c_1_table.close();
						}
					} while (c_tables.moveToNext());
					c_tables.close();
				}
				/*
				 * Comparator<String> comparator = new Comparator<String>() {
				 * 
				 * @SuppressLint("DefaultLocale")
				 * 
				 * @Override public int compare(String str1, String str2) {
				 * return str1.toUpperCase(Locale.US).compareTo(
				 * str2.toUpperCase()); } }; list_names.sort(comparator);
				 */
			} else {
				c2 = MainLfqActivity
						.getAcrosticsDb()
						.rawQuery(
								"SELECT Name FROM "
										+ table_name
										+ " WHERE Name LIKE ? ORDER BY Name COLLATE NOCASE",
								selectionArgs);
			}
			if (!check_use_all_acrostics.isChecked()
					&& !check_use_dictionary.isChecked()) {
				// is_exists.clear();
				if (c2.moveToFirst()) {
					while (!c2.isAfterLast()) {
						publishProgress(c2.getString(0));
						is_exists.add(false);
						// System.out.println("ADDED NAME...");
						c2.moveToNext();
					}
					System.out.println("ADDED " + c2.getCount() + " NAMES.");
				}
				c2.close();
			}
			if (check_use_dictionary.isChecked()) {
				// list_names.clear();
				// is_exists.clear();
				if (c2.moveToFirst()) {
					while (!c2.isAfterLast()) {
						publishProgress(c2.getString(0));
						// System.out.println("ADDED NAME...");
						is_exists.add(isExist(c2.getString(0)));
						c2.moveToNext();
					}
					System.out.println("ADDED " + c2.getCount() + " NAMES.");
				}
				c2.close();
			}
			// if (!enter_acrostic_name.isPopupShowing()){
			// enter_acrostic_name.showDropDown();
			// }
			// enter_acrostic_name.showDropDown();
			// enter_acrostic_name.showDropDown();
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			list_names.add(values[0]);
		}

		@Override
		protected void onPostExecute(String file_url) {
			results.setText("");
			System.out.println("list_names size=" + list_names.size()
					+ ", is_exists size=" + is_exists.size());
			namesAdapter = new CustomAdapter(act, list_names, is_exists);
			select_acrostic_names.setAdapter(namesAdapter);
			select_acrostic_names.setEnabled(true);
			enter_acrostic_name.setEnabled(true);
			enter_acrostic_name.setFocusableInTouchMode(true);
		}

	}

}
