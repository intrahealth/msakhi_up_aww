package org.intrahealth.mnewborncare.awc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
//import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class LazyCursorAdapterAncVisit extends SimpleCursorAdapter {

	// private Context context;
	private int layout;
	private LayoutInflater inflater = null;
	int nameCol, sv_dtCol, diffCol, slCol, slHvInd, pid, mstInd, cstInd,
			rsltCol, hnameCol;
	int mdtCol, cdtCol, mst, cst;
	boolean mdth = false, cdth = false;
	int child = 0;

	public LazyCursorAdapterAncVisit(Context context, int layout, Cursor c,
			String[] from, int[] to, int aa) {
		super(context, layout, c, from, to);
		// this.context = context;
		this.layout = layout;
		this.child = aa;
		inflater = LayoutInflater.from(context);
		nameCol = c.getColumnIndex("name");
		hnameCol = c.getColumnIndex("hname");
		sv_dtCol = c.getColumnIndex("edd");
		slCol = c.getColumnIndex("_id");

		// TODO Auto-generated constructor stub
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View v = inflater.inflate(layout, parent, false);
		Cursor c = getCursor();

		TextView tvName = (TextView) v.findViewById(R.id.name_entry);
		TextView tvEdd = (TextView) v.findViewById(R.id.edd_entry);
		TextView tvSlno = (TextView) v.findViewById(R.id.slno);
		TextView tvRslt = (TextView) v.findViewById(R.id.rslt);
		// tvRslt.setText(c.getString(rsltCol));
		if (child == 0) {
			tvName.setText(c.getString(nameCol) + " W/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		} else if (child == 1) {
			tvName.setText(c.getString(nameCol) + " C/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		} else if (child == 2) {
			tvName.setText(c.getString(nameCol) + " W/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		} else if (child == 3) {
			tvName.setText(c.getString(nameCol) + " W/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		}
		return v;
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {

		Cursor c = getCursor();

		TextView tvName = (TextView) v.findViewById(R.id.name_entry);
		TextView tvEdd = (TextView) v.findViewById(R.id.edd_entry);
		TextView tvSlno = (TextView) v.findViewById(R.id.slno);
		TextView tvRslt = (TextView) v.findViewById(R.id.rslt);
		// tvRslt.setText(c.getString(rsltCol));

		if (child == 0) {
			tvName.setText(c.getString(nameCol) + " W/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		} else if (child == 1) {
			tvName.setText(c.getString(nameCol) + " C/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		} else if (child == 2) {
			tvName.setText(c.getString(nameCol) + " W/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		} else if (child == 3) {
			tvName.setText(c.getString(nameCol) + " W/O "
					+ c.getString(hnameCol));
			tvEdd.setText(DBAdapter.strtodate(c.getString(sv_dtCol)));

			tvSlno.setText("" + (c.getPosition() + 1));
		}

	}
	/*
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { View currView = super.getView(position, convertView, parent);
	 * Object currItem = getItem(position); if (currItem.isItemSelected) { / *
	 * currView.setBackgroundColor(Color.RED); } else {
	 * currView.setBackgroundColor(Color.BLACK); } return currView; }
	 */

}
