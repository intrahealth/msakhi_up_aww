package org.intrahealth.mnewborncare.awc;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class Linegraaph1 extends Activity {

	private DBAdapter mydb;

	WebView wv;

	String sd1 = "", sd2 = "", sd3 = "";
	int width = 0, height = 0;
	Cursor c;
	String name, gender, dob;
	int server_id;
	TextView txt1, txt2;
	String[] weight = { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ",
			" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ",
			" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " " };

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.linegraph1);
		mydb = DBAdapter.getInstance(getApplicationContext());
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			name = extras.getString("name");
			gender = extras.getString("gender");
			server_id = extras.getInt("server_id");
			dob = extras.getString("dob");

		}
		txt1 = (TextView) findViewById(R.id.txt1);
		txt2 = (TextView) findViewById(R.id.txt2);
		if (name.contains("/")) {
			txt1.setText(name);
		} else {
			txt1.setText("/" + name);
		}
		dob = dob(dob);
		txt2.setText(dob + " (" + Weekcalculation(dob) + ")");
		wv = (WebView) findViewById(R.id.web);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setDisplayZoomControls(false);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.requestFocusFromTouch();
		wv.setWebViewClient(new WebViewClient());
		wv.setWebChromeClient(new WebChromeClient());

		wv.loadUrl("file:///android_asset/AggregatorGraph1.html");
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Download updategraph = new Download();
				updategraph.execute();
				return false;
			}
		});

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Download updategraph = new Download();
				updategraph.execute();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Download updategraph = new Download();
				updategraph.execute();
				return true;
				// return super.shouldOverrideUrlLoading(view, url);
			}

		});

	}

	public void stringdata() {

	}

	public String returnUrl() {

		String Url = "";
		String arr = "";
		getScreenResolution();
		value();
		weight();
		for (int i = 0; i < weight.length; i++) {
			if (weight[i].equalsIgnoreCase("")) {
				arr = arr + ",";
			} else {
				arr = arr + weight[i] + ",";
			}

		}

		String sd11 = sd1;
		String sd22 = sd2;
		String sd33 = sd3;
		// sd33.replace("", "\r\n");
		// sd33.replace("\n", "");
		int h = height - 150;
		int w = width - 15;
		return Url = "javascript:newfun(\"" + h + "\",\"" + w + "\",\"" + sd11
				+ "\",\"" + sd22 + "\",\"" + sd33 + "\",\"" + arr + "\")";

	}

	public String dob(String dob) {
		String dob1 = "";
		String[] ss = dob.split("-");
		if (ss.length > 2) {
			dob1 = ss[2] + "-" + ss[1] + "-" + ss[0];

		}
		return dob1;

	}

	private int Weekcalculation(String dob) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		int diffInDays = 0;
		Date CurrDate, DOB;
		String dob1 = "";

		dob1 = dob;
		try {
			Date dateobj = new Date();
			CurrDate = sdf.parse(sdf.format(dateobj));
			DOB = sdf.parse(dob1);

			diffInDays = (int) ((CurrDate.getTime() - DOB.getTime()) / (1000 * 60 * 60 * 24));
			diffInDays = (int) Math.round(diffInDays / 30.0 + .5);

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return diffInDays;
	}

	public void weight() {
		c = null;
		int prev = 0;
		int curr = 0;
		int next = 0;
		try {

			c = mydb.getFoodDataForServer_id1(server_id);

			c.moveToFirst();
			for (int i = 0; i < c.getCount(); i++) {
				int isd1 = c.getColumnIndex("food_size");
				int isd2 = c.getColumnIndex("mnth");
				next = Integer.parseInt(c.getString(isd2));
				if (curr < weight.length) {
					if (curr < next) {
						if (curr == 0) {
							for (int j = curr; j < next; j++) {
								if (curr == 0 && next != 0 && j == 0) {
									if (gender.equalsIgnoreCase("b")) {
										weight[j] = "2.1";
									} else if (gender.equalsIgnoreCase("g")) {
										weight[j] = "2";
									}
								} else {
									weight[j] = ""
											+ (Math.round(Double
													.valueOf(weight[j - 1]) * 100.0) / 100.0 + .25);
								}
							}
						} else {
							for (int j = curr + 1; j < next; j++) {
								Double value = (Math.round(Double
										.valueOf(weight[j - 1]) * 100.0) / 100.0 + .25);
								if (value > 2.1) {

									weight[j] = ""
											+ (Math.round(Double
													.valueOf(weight[j - 1]) * 100.0) / 100.0 + .25);
								}else{
									weight[j]="2.5";
								}
							}
						}
					}

				}
				if (i == (c.getCount() - 1)) {
					weight[Integer.parseInt(c.getString(isd2))] = ""
							+ (Math.round(Double.valueOf(c.getString(isd1)) * 100.0) / 100.0);

				} else {
					weight[Integer.parseInt(c.getString(isd2))] = ""
							+ (Math.round(Double.valueOf(c.getString(isd1)) * 100.0) / 100.0);

				}

				curr = next;
				c.moveToNext();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void value() {
		// c = null;
		// if (gender.equalsIgnoreCase("b")) {
		// c = mydb.getgraphlist(0);
		// } else if (gender.equalsIgnoreCase("g")) {
		// c = mydb.getgraphlist(1);
		//
		// }
		// c.moveToFirst();
		// for (int i = 0; i < c.getCount(); i++) {
		// int isd1 = c.getColumnIndex("sd1");
		// int isd2 = c.getColumnIndex("sd2");
		// int isd3 = c.getColumnIndex("sd3");
		// if (i == (c.getCount() - 1)) {
		// sd1 = sd1 + Float.parseFloat(c.getString(isd1));
		// sd2 = sd2 + c.getString(isd2);
		// sd3 = sd3 + c.getString(isd3);
		//
		// } else {
		// sd1 = sd1 + c.getString(isd1) + ",";
		// sd2 = sd2 + c.getString(isd2) + ",";
		// sd3 = sd3 + c.getString(isd3) + ",";
		// }
		// c.moveToNext();
		//
		// }
		if (gender.equalsIgnoreCase("b")) {
			sd1 = "2.1,2.9,3.8,4.4,4.9,5.3,5.7,5.9,6.2,6.4,6.6,6.8,6.9,7.1,7.2,7.4,7.5,7.7,7.8,8,8.1,8.2,8.4,8.5,8.6,8.8,8.9,9,9.1,9.2,9.4,9.5,9.6,9.7,9.8,9.9,10.0";
			sd2 = "0.4,0.5,0.5,0.6,0.7,0.7,0.7,0.8,0.7,0.7,0.8,0.8,0.8,0.8,0.9,0.9,0.9,0.9,1,0.9,1,1,1,1,1.1,1,1.1,1.1,1.1,1.2,1.1,1.2,1.2,1.2,1.2,1.3,1.3";
			sd3 = "0.4,0.5,0.6,0.7,0.6,0.7,0.7,0.7,0.8,0.9,0.8,0.8,0.9,0.9,0.9,0.9,1,1,1,1.1,1,1.1,1.1,1.2,1.1,1.2,1.2,1.2,1.3,1.3,1.3,1.3,1.3,1.4,1.4,1.4,1.4";
		} else if (gender.equalsIgnoreCase("g")) {
			sd1 = "2,2.7,3.4,4,4.4,4.8,5.1,5.3,5.6,5.8,5.9,6.1,6.3,6.4,6.6,6.7,6.9,7,7.2,7.3,7.5,7.6,7.8,7.9,8.1,8.2,8.4,8.5,8.6,8.8,8.9,9,9.1,9.3,9.4,9.5,9.6";
			sd2 = "0.4,0.5,0.5,0.5,0.6,0.6,0.6,0.7,0.7,0.7,0.8,0.8,0.7,0.8,0.8,0.9,0.8,0.9,0.9,0.9,0.9,1,0.9,1,0.9,1,1,1,1.1,1,1.1,1.1,1.2,1.1,1.1,1.2,1.2";
			sd3 = "0.4,0.4,0.6,0.7,0.7,0.7,0.8,0.8,0.7,0.8,0.8,0.8,0.9,0.9,0.9,0.9,1,1,1,1,1,1,1.1,1.1,1.2,1.1,1.1,1.2,1.2,1.3,1.2,1.3,1.3,1.3,1.4,1.3,1.4";

		}
	}

	public void getScreenResolution() {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		int ht = 0;
		int wd = 0;
		double screenInches = Math.sqrt(x + y);
		if (screenInches < 4.5) {
			ht = 480;
			wd = 300;
		} else if (screenInches >= 4.5 && screenInches < 6.5) {
			ht = 640;
			wd = 360;
		} else if (screenInches >= 6.5 && screenInches < 7.5) {
			ht = 720;
			wd = 480;
		} else if (screenInches >= 7.5) {
			ht = 1280;
			wd = 720;
		}

		// Display display = wm.getDefaultDisplay();
		// DisplayMetrics metrics = new DisplayMetrics();
		// display.getMetrics(metrics);
		width = wd;
		height = ht;

	}

	class Download extends AsyncTask<Integer, Integer, Void> {

		ProgressDialog dialog = new ProgressDialog(Linegraaph1.this);
		String URL = "";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog.show();
			dialog.setMessage("Loading..");

			dialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			URL = returnUrl();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			wv.removeAllViews();
			wv.loadUrl(returnUrl());
			dialog.dismiss();

		}

	}
}
