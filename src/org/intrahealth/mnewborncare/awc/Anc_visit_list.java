package org.intrahealth.mnewborncare.awc;

import java.io.File;
import java.io.IOException;

import org.intrahealth.mnewborncare.awc.*;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Anc_visit_list extends ListActivity {
	private DBAdapter mydb;
	TextView txtCount;
	int id = 0, pid, seq;
	String mname, hv_str, lmp_str, edd;
	private Spinner filter_spin;
	Cursor c;
	static int lastSel = -1;
	MediaPlayer mediaPlayer = new MediaPlayer();
	Button btnModBirth, btn;
	ListView lst;
	HomeVisitAdapter adapt;
	int flag = 0;
	String[] filterlist = { "ए.एन.सी", "बच्चे (0-1 माह)", "बच्चे (1-5 माह)",
			"बच्चे (6-12माह)" };

	@Override
	public void onDestroy() {
		if (c != null)
			c.close();
		mydb.close();
		super.onDestroy();
	}

	public static int getCurrentSelectedItemId() {
		return lastSel;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.anc_preg_list);

		txtCount = (TextView) findViewById(R.id.txtCount);
		btn = (Button) findViewById(R.id.btnNewBirth);
		filter_spin = (Spinner) findViewById(R.id.spin_filter);
		lst = (ListView) findViewById(android.R.id.list);

		ArrayAdapter<String> adapter = new ArrayAdapter(this,
				android.R.layout.simple_list_item_1, filterlist);
		filter_spin.setAdapter(adapter);
		mydb = DBAdapter.getInstance(getApplicationContext());
		filter_spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				if (filter_spin.getSelectedItem().toString()
						.equalsIgnoreCase("बच्चे (0-1 माह)")) {
					c = mydb.getAllFilterdPregPnc(0, 1);
					flag = 1;
					showInfo();
				} else if (filter_spin.getSelectedItem().toString()
						.equalsIgnoreCase("बच्चे (1-5 माह)")) {
					c = mydb.getAllFilterdPregPnc(1, 6);
					flag = 2;
					showInfo();
				} else if (filter_spin.getSelectedItem().toString()
						.equalsIgnoreCase("बच्चे (6-12माह)")) {
					c = mydb.getAllFilterdPregPnc(6, 13);
					flag = 3;
					showInfo();
				} else {
					c = mydb.getANCList();
					flag = 0;
					showInfo();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		lst.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View v,
					int position, long arg3) {
				if (flag == 0) {
					v.setSelected(true);
					Cursor cursor = (Cursor) adapterView
							.getItemAtPosition(position);
					lastSel = position;
					id = cursor.getInt(cursor
							.getColumnIndex(DBAdapter.KEY_ROWID));
					// hv_str=cursor.getString(cursor.getColumnIndex("hv_str"));
					seq = (int) Math.ceil(cursor.getFloat(cursor
							.getColumnIndex("dy")));
					pid = id;
					lmp_str = cursor.getString(cursor.getColumnIndex("lmp"));
					edd = cursor.getString(cursor.getColumnIndex("edd"));
					// cursor.getInt(cursor.getColumnIndex("_id"));
					/*
					 * if (cursor.isNull(cursor.getColumnIndex("dob"))) {
					 * btnModBirth.setVisibility(android.view.View.INVISIBLE);
					 * btn.setVisibility(android.view.View.VISIBLE); } else {
					 * btnModBirth.setVisibility(android.view.View.VISIBLE);
					 * btn.setVisibility(android.view.View.INVISIBLE); }
					 */
					mname = cursor.getString(cursor.getColumnIndex("name"));
					// Toast.makeText(getApplicationContext(), "" + id,
					// Toast.LENGTH_SHORT).show();
					try {
						String audioFile;
						audioFile = Environment.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/mcare/pdata/"
								+ String.valueOf(id) + ".3gp";

						// mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/tmp/test.3gp");
						mediaPlayer.reset();
						mediaPlayer.setDataSource(audioFile);
						mediaPlayer.prepare();
						mediaPlayer.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Cursor cursor = (Cursor) adapterView
							.getItemAtPosition(position);

					id = cursor.getInt(cursor
							.getColumnIndex(DBAdapter.KEY_ROWID));
					v.setSelected(true);
					lastSel = position;
				}

				// return true;
				// cursor.close();
			}
		});

		lst.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapterView, View v,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Cursor cursor = (Cursor) adapterView
						.getItemAtPosition(position);
				int key_id = cursor.getInt(cursor
						.getColumnIndex(DBAdapter.KEY_ROWID));
				lastSel = position;
				ImageView img = new ImageView(getApplicationContext());
				String imgfile = Environment.getExternalStorageDirectory()
						+ File.separator + "/mcare/pdata/"
						+ String.valueOf(key_id) + ".jpg";
				Bitmap bmp = BitmapFactory.decodeFile(imgfile);
				img.setImageBitmap(bmp);
				loadPhoto(img, 100, 100);
				cursor.close();
				return false;
			}

		});

		addListenerOnButton();
	}

	private void addListenerOnButton() {
		// TODO Auto-generated method stub

		// btn.setTypeface(face);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if (flag == 0) {
					// TODO Auto-generated method stub
					if (id == 0)
						Toast.makeText(getApplicationContext(),
								"लाभार्थी चुने", Toast.LENGTH_LONG).show();
					else {
						Context ctx = arg0.getContext();
						Intent intent = new Intent(ctx, AVisit_summary.class);
						intent.putExtra("id", id);
						intent.putExtra("hv_str", hv_str);
						intent.putExtra("seq", seq);
						intent.putExtra("pid", pid);
						intent.putExtra("bflag", true);
						intent.putExtra("lmp", lmp_str);
						intent.putExtra("edd", edd);

						ctx.startActivity(intent);
					}
				}

				else {
					if (id == 0)
						Toast.makeText(getApplicationContext(),
								"लाभार्थी चुने", Toast.LENGTH_LONG).show();
					else {
						Intent intent = new Intent(getApplicationContext(),
								HomeVisitChildAnc.class);
						c.moveToPosition(lastSel);
						// intent.putExtra("mnth",c.getString(c.getColumnIndex("mnth")));
						intent.putExtra("flag_Value", flag);
						intent.putExtra("server_Id",
								c.getInt(c.getColumnIndex("server_id")));
						startActivity(intent);
					}
				}
			}

		});

	}

	@Override
	protected void onResume() {
		lastSel = -1;
		if (flag > 0) {
			adapt.notifyDataSetChanged();
		}
		super.onResume();
	}

	private void loadPhoto(ImageView imageView, int width, int height) {

		ImageView tempImageView = imageView;

		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
				(ViewGroup) findViewById(R.id.layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
		image.setImageDrawable(tempImageView.getDrawable());
		imageDialog.setView(layout);
		imageDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});

		imageDialog.create();
		imageDialog.show();
	}

	private void showInfo() {

		Log.i("ans_aid", "mee" + mydb.getANCdata() + "");
		txtCount.setText("कुल गर्भवती : "
				+ String.valueOf(c.getCount()));
		startManagingCursor(c);

		String[] from = new String[] { DBAdapter.KEY_NAME, "edd",
				DBAdapter.KEY_ROWID };

		int[] to = new int[] { R.id.name_entry, R.id.number_entry };
		if (flag == 0) {
			LazyCursorAdapterAnc ca = new LazyCursorAdapterAnc(this,
					R.layout.hvanc_list_row, c, from, to);
			setListAdapter(ca);
		} else {
			adapt = new HomeVisitAdapter(this, R.layout.hvanc_list_row, c,
					from, to, flag);
			setListAdapter(adapt);
		}

	}

}
