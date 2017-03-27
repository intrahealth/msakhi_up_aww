package org.intrahealth.mnewborncare.awc;

//import com.intrahealth.mnewborncare.control.BadgeView;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;

//import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class Report_Anc_VisitList extends Activity {

	Intent i;
	int rep_ind = 0;
	private DBAdapter mydb;
	String c, c1, c2, c3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.report_anc_visitlist);
		TextView btn1 = (TextView) findViewById(R.id.btn1);
		TextView btn2 = (TextView) findViewById(R.id.btn2);
		TextView btn3 = (TextView) findViewById(R.id.btn3);
		TextView btn4 = (TextView) findViewById(R.id.btn4);
		TextView txt1 = (TextView) findViewById(R.id.txt1);
		TextView txt2 = (TextView) findViewById(R.id.txt2);
		TextView txt3 = (TextView) findViewById(R.id.txt3);
		TextView txt4 = (TextView) findViewById(R.id.txt4);
		TableRow tbl4 = (TableRow) findViewById(R.id.tbl4);
		// Button btn5 = (Button) findViewById(R.id.btn5);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			rep_ind = extras.getInt("rep_type");
			mydb = DBAdapter.getInstance(getApplicationContext());
			if (rep_ind == 1) {

				c = mydb.getdueListcount(1);
				c1 = mydb.getdueListcount(2);
				c2 = mydb.getdueListcount(3);
				btn1.setText("बी. सी. जी.");
				txt1.setText(c);
				btn2.setText("पोलियो");
				txt2.setText(c1);
				btn3.setText("डी. पी. टी-1 ,ओ. पी. वी-1, हेपेटाइटिस बी-1");
				txt3.setText(c2);
				tbl4.setVisibility(View.GONE);
			}
			if (rep_ind == 0) {

				c = mydb.getdueListanc1(0);
				c1 = mydb.getdueListanc1(1);
				c2 = mydb.getdueListanc1(2);
				c3 = mydb.getdueListanc1(3);

				txt1.setText(c);

				txt2.setText(c1);

				txt3.setText(c2);
				txt4.setText(c3);

			}

		}

		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report_AncVisit.class);
				if (rep_ind == 0) {
					intent.putExtra("rep_type", 0);
					intent.putExtra("flag", 0);
					startActivity(intent);
				} else if (rep_ind == 1) {
					intent.putExtra("rep_type", 1);
					intent.putExtra("flag", 0);
					startActivity(intent);
				}
			}
		});

		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report_AncVisit.class);
				if (rep_ind == 0) {
					intent.putExtra("rep_type", 0);
					intent.putExtra("flag", 1);
					startActivity(intent);
				} else if (rep_ind == 1) {
					intent.putExtra("rep_type", 1);
					intent.putExtra("flag", 1);
					startActivity(intent);
				}
			}
		});

		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report_AncVisit.class);
				if (rep_ind == 0) {
					intent.putExtra("rep_type", 0);
					intent.putExtra("flag", 2);
					startActivity(intent);
				} else if (rep_ind == 1) {
					intent.putExtra("rep_type", 1);
					intent.putExtra("flag", 2);
					startActivity(intent);
				}
			}
		});
		btn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report_AncVisit.class);
				intent.putExtra("rep_type", 0);
				intent.putExtra("flag", 3);
				startActivity(intent);
			}
		});

	}

}
