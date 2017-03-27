package org.intrahealth.mnewborncare.awc;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Report_AncVisit extends ListActivity {
	private DBAdapter mydb;
	// TextView txtCount;
	// Typeface face;
	int id = 0, seq = 0, pid, pcaste, preligion, dstat = 0;
	Long pmobile;
	String tv_from, tv_to;
	private int year;
	private int month;
	private int day, rep_ind = -1;
	static final int DATE_DIALOG_ID = 999;
	Button btnDate;
	ListView lst;
	String alertMsg;
	String mydt, asha_id, phname;
	Cursor c;
	int aa = 0, flag = 0;

	public String hv_str = "", mname = "";
	boolean learn = false, death = false, fvisit = false, current = true;
	Button btnNewTest;
	Button btnModBirth, btn;

	// static int lastSel = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.report_ancvisit);

		TextView tvHead = (TextView) findViewById(R.id.tvHead);
		TextView txtC3 = (TextView) findViewById(R.id.tvHeadC3);
		// TextView txtC4 = (TextView) findViewById(R.id.tvHeadC4);
		TextView tvHeadC2 = (TextView) findViewById(R.id.tvHeadC2);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			rep_ind = extras.getInt("rep_type");
			if (rep_ind == 0 )
				flag = extras.getInt("flag");

		}

		mydb = DBAdapter.getInstance(getApplicationContext());

		switch (rep_ind) {
		case 0:
			aa = rep_ind;
			c = mydb.getdueList(flag);
			tvHeadC2.setText("गर्भवती/पति का नाम");
			tvHead.setText("गर्भवती महिलाएं");
			txtC3.setText("ई.डी.डी. (महिना)");
			break;
		case 1:
			aa = rep_ind;
			c = mydb.getdueListimmu(flag);
			tvHeadC2.setText("बच्चे का नाम/मां का नाम");
			tvHead.setText("बच्चे के टीकाकरण लंबित सूची");
			txtC3.setText("जन्म तिथि");
			break;
		case 2:
			aa = rep_ind;
			c = mydb.getListHRP();
			tvHeadC2.setText("गर्भवती/पति का नाम");
			tvHead.setText("एचआरपी महिलाओं की सूची");
			txtC3.setText("ई.डी.डी. (महिना)");
			break;
//		case 3:
//			aa = rep_ind;
//			c = mydb.getListvisit48day();
//			tvHeadC2.setText("गर्भवती/पति का नाम");
//
//			tvHead.setText("गर्भवती महिलाएं");
//			txtC3.setText("ई.डी.डी. (महिना)");
//			break;

		default:
			c = mydb.getdueList(0);
			tvHead.setText("बच्चे का नाम/मां का नाम");
			txtC3.setText("गर्भवती (संख्या)");
			break;
		}
		startManagingCursor(c);
		// the desired columns to be bound
		String[] from = new String[] { "_id", "name", "edd" };
		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.slno, R.id.name_entry, R.id.edd_entry };

		LazyCursorAdapterAncVisit ca = new LazyCursorAdapterAncVisit(this,
				R.layout.ancvisit_row, c, from, to, aa);
		setListAdapter(ca);
		ListView lst = (ListView) findViewById(android.R.id.list);
		lst.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View v,
					int position, long arg3) {
				Cursor cursor = (Cursor) adapterView
						.getItemAtPosition(position);
				Intent intent;
				switch (rep_ind) {
				case 0:

					mname = cursor.getString(cursor.getColumnIndex("name"));
					pid = cursor.getInt(cursor.getColumnIndex("_id"));
					phname = cursor.getString(cursor.getColumnIndex("hname"));

					Toast.makeText(getApplicationContext(),
							"" + mname + " " + pid + " " + phname,
							Toast.LENGTH_SHORT).show();

					break;
				case 1:

					mname = cursor.getString(cursor.getColumnIndex("name"));
					pid = cursor.getInt(cursor.getColumnIndex("_id"));
					phname = cursor.getString(cursor.getColumnIndex("hname"));

					Toast.makeText(getApplicationContext(),
							"" + mname + " " + pid + " " + phname,
							Toast.LENGTH_SHORT).show();

					break;
				case 2:

					mname = cursor.getString(cursor.getColumnIndex("name"));
					pid = cursor.getInt(cursor.getColumnIndex("_id"));
					phname = cursor.getString(cursor.getColumnIndex("hname"));

					Toast.makeText(getApplicationContext(),
							"" + mname + " " + pid + " " + phname,
							Toast.LENGTH_SHORT).show();

					break;
				case 3:

					mname = cursor.getString(cursor.getColumnIndex("name"));
					pid = cursor.getInt(cursor.getColumnIndex("_id"));
					phname = cursor.getString(cursor.getColumnIndex("hname"));

					Toast.makeText(getApplicationContext(),
							"" + mname + " " + pid + " " + phname,
							Toast.LENGTH_SHORT).show();

					break;

				default:
					intent = new Intent(getApplicationContext(),
							Report_AncVisit.class);
					break;
				}

			}
		});// addListenerOnButton();
	}

	public void showAlert(String msg) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(alertMsg).setMessage(msg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// Some stuff to do when ok got clicked
					}
				})

				.show();
	}

}
