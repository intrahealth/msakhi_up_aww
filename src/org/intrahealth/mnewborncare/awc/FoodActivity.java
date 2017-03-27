package org.intrahealth.mnewborncare.awc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.intrahealth.mnewborncare.awc.R;
import org.intrahealth.mnewborncare.awc.Preg_reg_list.SpinShow;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FoodActivity extends Activity {

	String[] filterlist = { "गर्भवती महिलाएं", "बच्चे (0-6 माह)",
			"बच्चे (6 माह-3 वर्ष)", "बच्चे (3 वर्ष-6 वर्ष)" };
	String[] food = { "चुनें", "लिया ", "नहीं लिया " };
	String[] foodSecond = { "चुनें", "दिया ", "नहीं दिया " };
	DBAdapter obj_Db;
	ArrayList<PregInfo> prg_list;
	Context con;
	LayoutInflater lay_infla;
	ListView lv_preglist;
	PregAdap obj_Adapter;
	Button btn_save;
	FoodActivity obj;
	int i = 1;
	boolean showedit = false;;
	TextView month;
	Context mContext = this;
	SharedPreferences pref;
	private Spinner filter_spin;
	private SpinShow filter_adapter;
	Button bt_submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lay_food_list);

		init();
		if (pref.getBoolean("changesdone", false)) {
			try {
				startSendNewValue();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		saveListener();
	}

	private void startSendNewValue() throws IOException {
		Log.e("foodInsertSrver", "foodInsrtcalled");
		if (DownloadDataAsync.serverid != null
				&& DownloadDataAsync.serverid.size() > 0) {
			// ArrayList<Integer> serverid=new
			// ArrayList<Integer>(DownloadDataAsync.serverid);
			for (int server_id : DownloadDataAsync.serverid) {
				Log.e("calling", server_id + "");
				obj_Db.updateFoodData(server_id);

			}
			// obj_Db.sendGPRS(AppVariables.API(mContext)+"update_foodh.php","",0);
			URL url = new URL(AppVariables.API(mContext) + "update_foodh.php");
			InputStream input = url.openConnection().getInputStream();
			DownloadDataAsync.serverid.clear();
			pref.edit().putBoolean("changesdone", false).commit();
		}

	}

	private void saveListener() {
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count_benef = 0;
				for (PregInfo preg : prg_list) {
					if (preg.getFood() == 2 && preg.getWeight_food() == 2) {
						count_benef++;
					}
					obj_Db.updateFoodRecord(preg.getId(), preg.getFood(),
							preg.getWeight_food(), preg.getWeight_size());
					JSONObject json = new JSONObject();
					try {

						Log.d("id", "222" + preg.getId());
						json.put("server_id", preg.getServer_id());
						json.put("food", preg.getFood());
						json.put("weight_food", preg.getWeight_food());
						json.put("weight_size", preg.getWeight_size());
						json.put("id", preg.getId());
						json.put("child_flag", preg.getDob());
						Log.d("datajson", "jsonObject" + json.toString());
						obj_Db.sendGPRS(
								AppVariables.API(mContext) + "save.php",
								json.toString(), 0);
					} catch (JSONException e) {

						e.printStackTrace();
					}

				}
				if (count_benef > 0) {
					Toast.makeText(
							con,
							"data not Saved for " + count_benef
									+ " beneficiaries", 100).show();
				} else {
					Toast.makeText(con, "data Saved for the beneficiaries", 100)
							.show();
				}

			}
		});

		filter_spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Cursor c;
				if (filterlist[arg2].equals("बच्चे (0-6 माह)")) {
					c = obj_Db.getAllMotherFoodRecordList(0, 6);
					showedit = false;
					Log.e("cursor", "बच्चे (0-6 माह)");

				} else if (filterlist[arg2].equals("बच्चे (6 माह-3 वर्ष)")) {
					c = obj_Db.getAllMotherFoodRecordList(6, 36);
					showedit = false;
					Log.e("cursor", "बच्चे (6 माह-3 वर्ष)");
				} else if (filterlist[arg2].equals("बच्चे (3 वर्ष-6 वर्ष)")) {
					c = obj_Db.getAllMotherFoodRecordList(36, 72);
					showedit = false;
					Log.e("cursor", "बच्चे (3 वर्ष-6 वर्ष)");
				} else {
					c = obj_Db.getAllPregFoodRecordList();

					Log.e("cursor", "else");
					showedit = true;

				}
				//
				getAllPregList(c);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		lv_preglist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void setAdapter() {
		lv_preglist.setAdapter(obj_Adapter);

	}

	private void getAllPregList(Cursor c) {
		prg_list.clear();
		Cursor cursor = c;

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					PregInfo obj_info = new PregInfo();
					obj_info.setId(cursor.getInt(cursor.getColumnIndex("_id")));
					obj_info.setName(cursor.getString(cursor
							.getColumnIndex("name")));
					Log.e("name",
							cursor.getString(cursor.getColumnIndex("name")));
					obj_info.setEdd(cursor.getString(cursor
							.getColumnIndex("edd")));
					obj_info.setDob(cursor.getString(cursor
							.getColumnIndex("dob")));
					obj_info.setMoblile_No(cursor.getString(cursor
							.getColumnIndex("mobile")));
					obj_info.setMonth(cursor.getString(cursor
							.getColumnIndex("mnth")));
					obj_info.setgender(cursor.getString(c.getColumnIndex("sex")));
					int food = cursor.getInt(cursor.getColumnIndex("food"));
					int weight_food = cursor.getInt(cursor
							.getColumnIndex("weight_food"));
					float weight_size = cursor.getFloat(cursor
							.getColumnIndex("weight_size"));
					obj_info.setServer_id(cursor.getInt(cursor
							.getColumnIndex("server_id")));
					obj_info.setFood(food);
					obj_info.setWeight_food(weight_food);
					obj_info.setWeight_size(weight_size);
					prg_list.add(obj_info);
				} while (cursor.moveToNext());
			}
		}
		setAdapter();

	}

	private void init() {
		con = this;
		obj = this;
		month = (TextView) findViewById(R.id.month);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref.getString("month", "month");
		// month.setText(pref.getString("month", "month")); Hero comment
		// Add Hero
		obj_Db = new DBAdapter(getApplicationContext());
		String sql = "select case strftime('%m', timestamp) when '01' then 'Febuary' when '02' then 'March' when '03' then 'April' when '04' then 'May' when '05' then 'June' when '06' then 'July' when '07' then 'August' when '08' then 'September' when '09' then 'October' when '10' then 'November' when '11' then 'December' when '12' then 'January' else '' end as month from food_history a inner join preg_reg b on a.server_id=b.server_id  order by a.timestamp desc limit 1";
		String monthget = obj_Db.getvalue(sql);
		// Cursor monthvalue = obj_Db.monthreturn();
		// String monthget = monthvalue.getString(monthvalue
		// .getColumnIndex("month"));
		month.setText(monthget);
		lay_infla = getLayoutInflater();
		filter_adapter = new SpinShow();
		filter_spin = (Spinner) findViewById(R.id.spin_filter);
		filter_spin.setAdapter(filter_adapter);
		prg_list = new ArrayList<PregInfo>();
		obj_Adapter = new PregAdap();
		lv_preglist = (ListView) findViewById(R.id.list);
		lv_preglist.setItemsCanFocus(true);
		btn_save = (Button) findViewById(R.id.btnNewBirth);
		obj_Db = DBAdapter.getInstance(con);
		bt_submit = (Button) findViewById(R.id.submit);

	}

	class PregAdap extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return prg_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = lay_infla.inflate(R.layout.food_row, null);
			}
			Spinner spin_rasan = (Spinner) convertView
					.findViewById(R.id.spin_filter0);
			Spinner spin_weight = (Spinner) convertView
					.findViewById(R.id.spin_filter3);
			TextView tvEdd = (TextView) convertView
					.findViewById(R.id.edd_entry);
			TextView liya = (TextView) convertView.findViewById(R.id.liya);
			TextView age_Month = (TextView) convertView.findViewById(R.id.Age);
			SpinData adapt = new SpinData();
			SpinDataSecond adaptSecond = new SpinDataSecond();
			// ArrayAdapter<String> adapt = new ArrayAdapter<String>(con,
			// android.R.layout.simple_list_item_1, food);
			// adapt.setDropDownViewResource(R.layout.spinnerrow);
			spin_rasan.setAdapter(adaptSecond);
			spin_weight.setAdapter(adapt);

			final EditText edit = (EditText) convertView
					.findViewById(R.id.editText1);
			if (showedit) {
				spin_weight.setVisibility(View.GONE);
				edit.setVisibility(View.GONE);
				tvEdd.setVisibility(View.GONE);
				liya.setVisibility(View.GONE);

			}
			TextView tv = (TextView) convertView.findViewById(R.id.name_entry);

			TextView tvDob = (TextView) convertView
					.findViewById(R.id.dob_entry);
			final TextView tv_unit = (TextView) convertView
					.findViewById(R.id.tv_unit);
			TextView tv_month = (TextView) convertView.findViewById(R.id.slno);
			ImageLoader Loader = new ImageLoader(con);
			TextView tvSlno = (TextView) convertView.findViewById(R.id.slno);
			TextView tvMobie = (TextView) convertView
					.findViewById(R.id.mobile_no);
			tvDob.setVisibility(View.GONE);
			tv_unit.setVisibility(View.GONE);
			edit.setTag(position);
			// age_Month.setText("Age/Month :"+prg_list.get(position).getMonth());
			final ImageView thumb_image = (ImageView) convertView
					.findViewById(R.id.list_image);
			int mnth = Integer.parseInt(prg_list.get(position).getMonth());
			if (mnth < 12) {
				if (showedit) {
					age_Month.setText("गर्भावस्था के महीने :"
							+ prg_list.get(position).getMonth() + "M");
				} else {
					age_Month.setText("आयु :"
							+ prg_list.get(position).getMonth() + "M");
				}

			} else {
				if (showedit) {
					int y = mnth / 12;
					int month = mnth - (y * 12);
					age_Month.setText("गर्भावस्था के महीने :" + y + "Y "
							+ month + "M");
				} else {
					int y = mnth / 12;
					int month = mnth - (y * 12);
					age_Month.setText("आयु :" + y + "Y " + month + "M");
				}

			}

			tvEdd.setText("लिंग: " + prg_list.get(position).getgender());
			// age_Month.setText("Age/Month :"+5);
			tvMobie.setText("मोबाइल नंबर : "
					+ prg_list.get(position).getMoblile_No());
			edit.setVisibility(View.GONE);
			// edit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			edit.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			edit.setSelectAllOnFocus(true);
			tv.setText(prg_list.get(position).getName());

			if (prg_list.get(position).getFood() == 1) {
				Log.e("food flag", "1212" + prg_list.get(position).getName());
				spin_rasan.setSelection(1);
				prg_list.get(position)
						.setFood(prg_list.get(position).getFood());
			} else if (prg_list.get(position).getFood() == 0) {
				spin_rasan.setSelection(2);
				prg_list.get(position)
						.setFood(prg_list.get(position).getFood());
			} else {
				spin_rasan.setSelection(0);
				prg_list.get(position).setFood(2);
			}
			if (prg_list.get(position).getWeight_food() == 1) {
				Log.e("Weight_flag", "1212" + prg_list.get(position).getName()
						+ "   " + prg_list.get(position).getWeight_size());
				spin_weight.setSelection(1);
				edit.setVisibility(View.VISIBLE);
				tv_unit.setVisibility(View.VISIBLE);
				edit.setText(prg_list.get(position).getWeight_size() + "");
				prg_list.get(position).setWeight_food(
						prg_list.get(position).getWeight_food());
				prg_list.get(position).setWeight_size(
						prg_list.get(position).getWeight_size());

			} else if (prg_list.get(position).getWeight_food() == 0) {
				spin_weight.setSelection(2);
				prg_list.get(position).setWeight_food(0);
				prg_list.get(position).setWeight_size(0);
			} else {
				spin_weight.setSelection(0);
				prg_list.get(position).setWeight_food(2);
				prg_list.get(position).setWeight_size(0);
			}

			spin_rasan.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					if (arg2 == 1) {
						Log.e("args", "1212" + arg2);
						prg_list.get(position).setFood(1);
					} else if (arg2 == 2) {
						Log.e("args", "1212" + arg2);
						prg_list.get(position).setFood(0);
					} else {
						Log.e("args", "1212" + arg2);
						prg_list.get(position).setFood(2);
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			spin_weight.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					if (arg2 == 1) {
						Log.e("cb_weight", "1212"
								+ prg_list.get(position).getName());
						edit.setVisibility(View.VISIBLE);
						tv_unit.setVisibility(View.VISIBLE);
						prg_list.get(position).setWeight_food(1);
						prg_list.get(position).setWeight_size(
								prg_list.get(position).getWeight_size());
						edit.setText(prg_list.get(position).getWeight_size()
								+ "");
					} else if (arg2 == 2) {
						edit.setVisibility(View.GONE);
						tv_unit.setVisibility(View.GONE);
						prg_list.get(position).setWeight_food(0);
						prg_list.get(position).setWeight_size(0);

					} else {
						edit.setVisibility(View.GONE);
						tv_unit.setVisibility(View.GONE);
						prg_list.get(position).setWeight_food(2);
						prg_list.get(position).setWeight_size(0);

					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int tag = (Integer) v.getTag();
					Log.e("name", prg_list.get(tag).getName());
					dialogOpen(tag);

				}
			});

			String imgfile = null;
			if (prg_list.get(position).getServer_id() > 0) {
				imgfile = Environment.getExternalStorageDirectory()
						+ File.separator + Workflow.bfolder + "/pdata/"
						+ prg_list.get(position).getServer_id() + ".jpg";
			} else {
				imgfile = Environment.getExternalStorageDirectory()
						+ File.separator + Workflow.bfolder + "/pdata/"
						+ prg_list.get(position).getId() + ".jpg";
			}
			Loader.DisplayThumbnail(imgfile, thumb_image);
			i++;
			return convertView;
		}

		private void dialogOpen(final int tag) {
			AlertDialog.Builder alert = new AlertDialog.Builder(con);

			final EditText edittext = new EditText(con);
			// edittext.setBackground(getResources().getDrawable(R.drawable.edittextform));
			edittext.setTextSize(18);
			alert.setMessage("enter Weight :");

			alert.setTitle("Weight");
			alert.setView(edittext);
			edittext.setHint(prg_list.get(tag).getWeight_size() + "");
			edittext.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			// edittext.setInputType(InputType.TYPE_CLASS_NUMBER);

			alert.setPositiveButton("save", null);
			alert.setNegativeButton("cancel", null);
			final AlertDialog dialog = alert.create();
			dialog.show();

			Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Button neg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			pos.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (edittext.getText().toString() != null
							&& !edittext.getText().toString().isEmpty()) {
						float i;
						try {
							i = Float.parseFloat(edittext.getText().toString());
						} catch (NumberFormatException e) {
							i = 0;
							Log.e("numberFormat", "exception");
						}

						if (i <= 30 && i > 0) {

							prg_list.get(tag).setWeight_size(i);
							dialog.dismiss();

						} else {
							Toast.makeText(
									con,
									"Rasan value must be less than 30 and grater than 0",
									100).show();

						}
					} else {
						Toast.makeText(con, "plz insert value", 100).show();
					}

				}
			});
			neg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog.dismiss();

				}
			});

		}
	}

	class SpinShow extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return filterlist.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = lay_infla.inflate(R.layout.spinnerrow, null);
			}
			TextView tv_month = (TextView) convertView
					.findViewById(R.id.tv_months);
			tv_month.setText(filterlist[position]);
			return convertView;
		}

	}

	class SpinData extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return food.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = lay_infla.inflate(R.layout.spinnerrow, null);
			}
			TextView tv_month = (TextView) convertView
					.findViewById(R.id.tv_months);
			tv_month.setText(food[position]);
			return convertView;
		}

	}

	class SpinDataSecond extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return foodSecond.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = lay_infla.inflate(R.layout.spinnerrow, null);
			}
			TextView tv_month = (TextView) convertView
					.findViewById(R.id.tv_months);
			tv_month.setText(foodSecond[position]);
			return convertView;
		}

	}
}
