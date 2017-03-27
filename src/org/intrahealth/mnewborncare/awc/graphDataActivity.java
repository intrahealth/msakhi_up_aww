package org.intrahealth.mnewborncare.awc;

import java.io.File;
import java.util.ArrayList;

import org.intrahealth.mnewborncare.awc.R;
import org.intrahealth.mnewborncare.awc.Preg_reg_list.SpinShow;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class graphDataActivity extends Activity {

	String[] filterlist = { "बच्चे (0-6 माह)", "बच्चे (6 माह-3 वर्ष)",
			"बच्चे (3 वर्ष-6 वर्ष)" };

	DBAdapter obj_Db;
	ArrayList<PregInfo> prg_list;
	Context con;
	LayoutInflater lay_infla;
	ListView lv_preglist;
	PregAdap obj_Adapter;

	graphDataActivity obj;

	Context mContext = this;

	private Spinner filter_spin;
	private SpinShow filter_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lay_graph_list);
		init();
		// getAllPregList(obj_Db.getAllPregFoodRecordList());

		saveListener();
	}

	private void saveListener() {

		filter_spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Cursor c;

				if (filterlist[arg2].equals("बच्चे (6 माह-3 वर्ष)")) {
					c = obj_Db.getAllMotherFoodRecordList(6, 36);
					Log.e("cursor", "बच्चे (6 माह-3 वर्ष)");
				} else if (filterlist[arg2].equals("बच्चे (3 वर्ष-6 वर्ष)")) {
					c = obj_Db.getAllMotherFoodRecordList(36, 72);
					Log.e("cursor", "बच्चे (3 वर्ष-6 वर्ष)");
				} else {
					c = obj_Db.getAllMotherFoodRecordList(0, 6);
					Log.e("cursor", "बच्चे (0-6 माह)");

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
				int server_id = prg_list.get(position).getServer_id();
				Intent intent = new Intent(con,
						Linegraaph1.class);
				intent.putExtra("server_id", server_id);
				intent.putExtra("name", prg_list.get(position).getName());
				intent.putExtra("gender", prg_list.get(position).getgender());
				intent.putExtra("dob", prg_list.get(position).getDob());
				startActivity(intent);

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
					obj_info.setgender(cursor.getString(cursor
							.getColumnIndex("sex")));
					obj_info.setMoblile_No(cursor.getString(cursor
							.getColumnIndex("mobile")));
					obj_info.setMonth(cursor.getString(cursor
							.getColumnIndex("mnth")));
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
		lay_infla = getLayoutInflater();
		filter_adapter = new SpinShow();

		filter_spin = (Spinner) findViewById(R.id.spin_filter);
		filter_spin.setAdapter(filter_adapter);
		prg_list = new ArrayList<PregInfo>();
		obj_Adapter = new PregAdap();
		lv_preglist = (ListView) findViewById(R.id.list);
		lv_preglist.setItemsCanFocus(true);

		obj_Db = DBAdapter.getInstance(con);

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
				convertView = lay_infla.inflate(R.layout.graphrow, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.name_entry);
			TextView tvEdd = (TextView) convertView
					.findViewById(R.id.edd_entry);
			TextView tvDob = (TextView) convertView
					.findViewById(R.id.dob_entry);

			TextView tv_month = (TextView) convertView.findViewById(R.id.slno);
			ImageLoader Loader = new ImageLoader(con);
			TextView tvSlno = (TextView) convertView.findViewById(R.id.slno);
			TextView tvMobie = (TextView) convertView
					.findViewById(R.id.mobile_no);
			tvDob.setVisibility(View.GONE);

			tv_month.setText("" + prg_list.get(position).getMonth());
			final ImageView thumb_image = (ImageView) convertView
					.findViewById(R.id.list_image);
			tvEdd.setText("सम्भावित: " + prg_list.get(position).getEdd());

			tvMobie.setText("मोबाइल नंबर : "
					+ prg_list.get(position).getMoblile_No());

			tv.setText(prg_list.get(position).getName());

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

			return convertView;
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

}
