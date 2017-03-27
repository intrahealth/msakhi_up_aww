package org.intrahealth.mnewborncare.awc;

//import com.intrahealth.mnewborncare.control.BadgeView;

import org.intrahealth.mnewborncare.awc.R;

import android.app.Activity;

import android.content.Intent;

//import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.widget.Button;

public class Report_list extends Activity {

	Intent i;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.report_list);
		Button btnHVList = (Button) findViewById(R.id.btnHVList);
		Button btnDList = (Button) findViewById(R.id.btnDlist);
		Button btnAbortRep = (Button) findViewById(R.id.btnAbortRep);

		Button btnDueList_Anc = (Button) findViewById(R.id.btnDueList_Anc);
		Button btnDueList_ASHA = (Button) findViewById(R.id.btnDueList_ASHA);
		Button btnDueList_HRP = (Button) findViewById(R.id.btnDueList_HRP);

		btnHVList.setVisibility(View.GONE);
		btnDList.setVisibility(View.GONE);
		btnAbortRep.setVisibility(View.GONE);
		btnHVList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Reporthv.class);
				intent.putExtra("rep_type", "hvlist");
				startActivity(intent);
			}
		});

		btnDList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report.class);
				intent.putExtra("rep_type", "drep");
				startActivity(intent);
			}
		});

		btnAbortRep.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report.class);
				intent.putExtra("rep_type", "abrep");
				startActivity(intent);
			}
		});

		btnDueList_Anc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report_Anc_VisitList.class);
				intent.putExtra("rep_type", 0);
				startActivity(intent);
			}
		});
		btnDueList_ASHA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report_AncVisit.class);
				intent.putExtra("rep_type", 1);
				startActivity(intent);
			}
		});
		btnDueList_HRP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						Report_AncVisit.class);
				intent.putExtra("rep_type", 2);
				startActivity(intent);
			}
		});

	}

}
