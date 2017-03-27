package org.intrahealth.mnewborncare.awc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;

import org.intrahealth.mnewborncare.awc.R;

public class LineChartActivity2 extends Activity implements // extends DemoBase
															// implements
		OnChartValueSelectedListener {

	private LineChart mChart;
	ArrayList<graphBean> mred_array;
	ArrayList<graphBean> myellow_array;
	ArrayList<graphBean> mgreen_array;
	DBAdapter db;
	int server_id;
	String gender;
	Cursor c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//setContentView(R.layout.activity_linechart); //HeroComment
		setContentView(R.layout.testing_mw);
		Bundle intent = getIntent().getExtras();
		Log.e("server_id", intent.getInt("server_id") + "");
		server_id = intent.getInt("server_id");
		gender = intent.getString("gender");
		Log.e("name", intent.getString("name") + "");
		init();
		getCursorData();

		mChart = (LineChart) findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);

		// no description text
		mChart.setDescription("");
		mChart.setNoDataTextDescription("You need to provide data for the chart.");

		// enable value highlighting
		mChart.setHighlightEnabled(true);

		// enable touch gestures
		mChart.setTouchEnabled(true);

		mChart.setDragDecelerationFrictionCoef(0.9f);

		// enable scaling and dragging
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);
		mChart.setDrawGridBackground(false);
		mChart.setHighlightPerDragEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		mChart.setPinchZoom(true);

		// set an alternative background color
		mChart.setBackgroundColor(Color.LTGRAY);

		// add data
		Log.e("count", c.getCount() + "");
		setData(c.getCount() + 1, 3, 10, 13);

		mChart.animateX(2500);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"OpenSans-Regular.ttf");

		// get the legend (only possible after setting data)
		Legend l = mChart.getLegend();

		// modify the legend ...
		// l.setPosition(LegendPosition.LEFT_OF_CHART);
		l.setForm(LegendForm.LINE);
		l.setTypeface(tf);
		l.setTextSize(11f);
		l.setTextColor(Color.WHITE);
		l.setPosition(LegendPosition.BELOW_CHART_LEFT);
		// l.setYOffset(11f);

		XAxis xAxis = mChart.getXAxis();
		xAxis.setTypeface(tf);
		xAxis.setTextSize(12f);
		xAxis.setTextColor(Color.WHITE);
		xAxis.setDrawGridLines(false);
		xAxis.setDrawAxisLine(false);
		xAxis.setSpaceBetweenLabels(1);

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setTypeface(tf);
		leftAxis.setTextColor(ColorTemplate.getHoloBlue());
		leftAxis.setAxisMaxValue(30f);
		leftAxis.setDrawGridLines(true);

		YAxis rightAxis = mChart.getAxisRight();
		rightAxis.setTypeface(tf);
		rightAxis.setTextColor(Color.RED);
		rightAxis.setAxisMaxValue(30);
		rightAxis.setAxisMinValue(0);
		rightAxis.setStartAtZero(false);
		// rightAxis.setAxisMinValue(-200);
		rightAxis.setDrawGridLines(false);

		for (DataSet<?> set : mChart.getData().getDataSets())
			set.setDrawValues(!set.isDrawValuesEnabled());

		ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
				.getDataSets();

		for (LineDataSet set : sets) {
			if (set.isDrawCirclesEnabled())
				set.setDrawCircles(false);
			else
				set.setDrawCircles(true);
		}

		for (LineDataSet set : sets) {
			if (set.isDrawCubicEnabled())
				set.setDrawCubic(false);
			else
				set.setDrawCubic(true);
		}

		for (LineDataSet set : sets) {
			if (set.isDrawFilledEnabled())
				set.setDrawFilled(false);
			else
				set.setDrawFilled(true);
		}
		mChart.invalidate();
	}

	private void init() {
		db = new DBAdapter(this);
		mred_array = new ArrayList<graphBean>();
		myellow_array = new ArrayList<graphBean>();
		mgreen_array = new ArrayList<graphBean>();

	}

	private void getCursorData() {
		try {

			c = db.getFoodDataForServer_id(server_id);
			mgreen_array.add(new graphBean(0, 0));
			myellow_array.add(new graphBean(0, 0));
			mred_array.add(new graphBean(0, 0));
			if (c.moveToFirst()) {
				do {
					boolean count = false;
					boolean myello = false;
					boolean mgreen = false;
					Log.e("server_id", server_id + "");
					Log.e("position", c.getPosition() + "");
					Log.e("server_id", gender + "");
					Log.e("food", c.getString(c.getColumnIndex("food_size")));
					String color = db.checkdataforColor(server_id,
							c.getString(c.getColumnIndex("food_size")),
							c.getString(c.getColumnIndex("timestamp")),
							c.getPosition(), gender);
					Log.e("color", color);
					if (color.equalsIgnoreCase("r")) {
						mred_array.add(new graphBean(Float.parseFloat(c
								.getString(c.getColumnIndex("food_size"))), c
								.getPosition() + 1));
						count = true;
					} else if (color.equalsIgnoreCase("g")) {
						mgreen_array.add(new graphBean(Float.parseFloat(c
								.getString(c.getColumnIndex("food_size"))), c
								.getPosition() + 1));
						mgreen = true;
					} else {
						myellow_array.add(new graphBean(Float.parseFloat(c
								.getString(c.getColumnIndex("food_size"))), c
								.getPosition() + 1));
						myello = true;
					}
					if (count == true) {
						mgreen_array.add(new graphBean(0, c.getPosition() + 1));
						myellow_array
								.add(new graphBean(0, c.getPosition() + 1));
						count = false;
					} else if (mgreen == true) {
						myellow_array
								.add(new graphBean(0, c.getPosition() + 1));
						mred_array.add(new graphBean(0, c.getPosition() + 1));
						mgreen = false;
					} else if (myello == true) {
						mred_array.add(new graphBean(0, c.getPosition() + 1));
						mgreen_array.add(new graphBean(0, c.getPosition() + 1));
						myello = false;
					}
				} while (c.moveToNext());
				Log.e("cursor", c.getCount() + "");
				Log.e("mred Size", mred_array.size() + "");
				Log.e("myello Size", mred_array.size() + "");
				Log.e("mgreen_Size", mgreen_array.size() + "");

			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	private void setData(int count, int red, int blue, int green) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xVals.add((i) + "");
		}

		ArrayList<Entry> yVals1 = new ArrayList<Entry>();

		for (int i = 0; i < myellow_array.size(); i++) {
			float mult = blue / 2f;
			float val = myellow_array.get(i).value;

			Log.e("blue", val + "");
			yVals1.add(new Entry(val, myellow_array.get(i).position));
		}

		// create a dataset and give it a type
		LineDataSet set1 = new LineDataSet(yVals1, "15th");
		set1.setAxisDependency(AxisDependency.LEFT);
		set1.setColor(Color.YELLOW);
		set1.setCircleColor(Color.WHITE);
		set1.setLineWidth(2f);
		set1.setCircleSize(3f);
		set1.setFillAlpha(65);
		set1.setFillColor(Color.YELLOW);
		set1.setFillAlpha(120);
		// set1.setFillColor(ColorTemplate.getHoloBlue());
		set1.setHighLightColor(Color.rgb(244, 117, 117));
		set1.setDrawCircleHole(false);
		// set1.setVisible(false);
		// set1.setCircleHoleColor(Color.WHITE);

		ArrayList<Entry> yVals2 = new ArrayList<Entry>();

		for (int i = 0; i < mred_array.size(); i++) {
			float mult = red;
			//
			float val = mred_array.get(i).value;

			Log.e("red", val + "");
			yVals2.add(new Entry(val, mred_array.get(i).position));
		}

		// create a dataset and give it a type
		LineDataSet set2 = new LineDataSet(yVals2, "3rd");
		set2.setAxisDependency(AxisDependency.RIGHT);
		set2.setColor(Color.RED);
		set2.setCircleColor(Color.WHITE);
		set2.setLineWidth(2f);
		set2.setCircleSize(3f);
		set2.setFillAlpha(65);
		set2.setFillColor(Color.RED);
		set2.setFillAlpha(120);
		set2.setDrawCircleHole(false);
		set2.setHighLightColor(Color.rgb(244, 117, 117));

		ArrayList<Entry> yVals3 = new ArrayList<Entry>();

		for (int i = 0; i < mgreen_array.size(); i++) {
			float mult = green;
			float val = mgreen_array.get(i).value;// + (float)
													// ((mult *
			Log.e("green", val + ""); // 0.1) / 10);
			yVals3.add(new Entry(val, mgreen_array.get(i).position));
		}

		// create a dataset and give it a type
		LineDataSet set3 = new LineDataSet(yVals3, "Median");
		set3.setAxisDependency(AxisDependency.RIGHT);
		set3.setColor(Color.GREEN);
		set3.setCircleColor(Color.WHITE);
		set3.setLineWidth(2f);
		set3.setCircleSize(3f);
		set3.setFillAlpha(65);
		set3.setFillAlpha(120);
		set3.setFillColor(Color.GREEN);
		set3.setDrawCircleHole(false);
		set3.setHighLightColor(Color.rgb(244, 117, 117));
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		if (mred_array.size() > 0) {
			dataSets.add(set2);
		}
		if (myellow_array.size() > 0) {
			dataSets.add(set1);
		}
		if (mgreen_array.size() > 0) {
			dataSets.add(set3);// add the datasets
		}
		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);
		data.setValueTextColor(Color.WHITE);
		data.setValueTextSize(9f);

		// set data
		mChart.setData(data);
	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		Log.i("Entry selected", e.toString());
	}

	@Override
	public void onNothingSelected() {
		Log.i("Nothing selected", "Nothing selected.");
	}

}
