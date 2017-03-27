package org.intrahealth.mnewborncare.awc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

class Anc_answr {
	int optind;
	int qnty, qtype;
	long dte;
	long dtm;
}

public class DBAdapter extends SQLiteOpenHelper {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LMP = "lmp";
	// private static final String TAG = "DBAdapter";
	private static String dbPath = "/data/data/org.intrahealth.mnewborncare.awc/databases/";
	public static final String DATABASE_NAME = "mnewborncare.db";
	private static final String DB_PREG_TABLE = "preg_reg";
	// private static final int DATABASE_VERSION = 1;
	/*
	 * 0 25 1 12 37 2 15 52 3 8 60 4 16 76 sishu all yes 7-8 negative response
	 */
	public static final int qc[] = { 25, 37, 52, 60, 76 };
	public static final String mstr[] = { "JAN", "FEB", "MAR", "APR", "MAY",
			"JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	public static final String hmstr[] = { "JAN", "FEB", "MAR", "APR", "MAY",
			"JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	private static DBAdapter mInstance = null;
	public static boolean send_sms = true;
	// public static String resturl="http://msakhi.org/resturl";
	public static String resturl = "http://192.168.1.46/webbackup06jun15/resturl";

	int pqarr[], qarr[], anc_qcnt, anc_acnt = 0, pnc_qcnt = 0, pnc_acnt = 0;
	Dictionary<Integer, Anc_answr> tansdata, pansdata;

	private Context context;
	// private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	String seq_str;

	public DBAdapter(Context ctx) {
		super(ctx, DATABASE_NAME, null, 1);
		this.context = ctx;
	}

	public static Calendar getCalInstance() {
		Calendar def = Calendar.getInstance();
		def.set(Calendar.HOUR_OF_DAY, 0);
		def.set(Calendar.MINUTE, 0);
		def.set(Calendar.SECOND, 0);
		def.set(Calendar.MILLISECOND, 0);
		return def;
	}

	public static DBAdapter getInstance(Context ctx) {
		/**
		 * use the application context as suggested by CommonsWare. this will
		 * ensure that you dont accidentally leak an Activitys context (see this
		 * article for more information:
		 * http://developer.android.com/resources/articles
		 * /avoiding-memory-leaks.html)
		 */
		if (mInstance == null) {
			mInstance = new DBAdapter(ctx.getApplicationContext());
			try {
				mInstance.createDataBase();
				mInstance.openDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mInstance;
	}

	private boolean checkDataBase() {
		File dbFile = new File(dbPath + DATABASE_NAME);
		return dbFile.exists();
	}

	private void copyDataBase() throws IOException {
		try {

			InputStream input = context.getAssets().open(DATABASE_NAME);
			String outPutFileName = dbPath + DATABASE_NAME;
			OutputStream output = new FileOutputStream(outPutFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			output.flush();
			output.close();
			input.close();
		} catch (IOException e) {
			Log.v("error", e.toString());
		}
	}

	/*
	 * private static class DatabaseHelper extends SQLiteOpenHelper {
	 * 
	 * DatabaseHelper(Context context) { super(context, DATABASE_NAME, null,
	 * DATABASE_VERSION); }
	 * 
	 * @Override public void onCreate(SQLiteDatabase db) { /* try { Log.d(TAG,
	 * "Creating database"); String[] queries = DATABASE_CREATE.split(";");
	 * for(String query : queries){ db.execSQL(query); }
	 * //db.execSQL(DATABASE_CREATE); } catch (SQLException e) {
	 * e.printStackTrace(); } }
	 * 
	 * @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int
	 * newVersion) { Log.w(TAG, "Upgrading database from version " + oldVersion
	 * + " to " + newVersion + ", which will destroy all old data");
	 * 
	 * db.execSQL("DROP TABLE IF EXISTS [preg_reg]");
	 * db.execSQL("DROP TABLE IF EXISTS [sch_visits]");
	 * db.execSQL("Drop Trigger If Exists [trg_insert]");
	 * db.execSQL("Drop Trigger If Exists [trg_update]"); onCreate(db); } }
	 */
	// ---opens the database---
	/*
	 * public DBAdapter open() throws SQLException { db =
	 * DBHelper.getWritableDatabase(); return this; }
	 */

	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	public void openDataBase() throws SQLException, IOException {
		String fullDbPath = dbPath + DATABASE_NAME;
		db = SQLiteDatabase.openDatabase(fullDbPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	public SQLiteDatabase getDB() {
		try {
			openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}

	// ---closes the database---
	@Override
	public synchronized void close() {
		// if( db!=null) db.close();
		// super.close();
	}

	public synchronized void myclose() {

		if (db != null)
			db.close();
		super.close();
		mInstance = null;
		// DBHelper.close();
	}

	// --New registration Pregnancy--
	public synchronized long insertPreg(String name, String lmp, String hname,
			int caste, int rel, String mobile, boolean dbreg, long aid,
			long awid, int media_flag, int server_id, int food,
			int weight_food, float weight_size) {
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_LMP, lmp);
		initialValues.put("m_stat", 0);
		initialValues.put("c_stat", 0);
		initialValues.put("hname", hname);
		initialValues.put("caste", caste);
		initialValues.put("religion", rel);
		initialValues.put("mobile", mobile);
		initialValues.put("dbreg", dbreg);
		initialValues.put("aid", awid);
		initialValues.put("awid", aid);
		initialValues.put("puid", String.format("%03d%03d", awid, getMaxt()));
		initialValues.put("media_flag", media_flag);
		initialValues.put("server_id", server_id);
		initialValues.put("food", food);
		initialValues.put("weight_food", weight_food);
		initialValues.put("weight_size", weight_size);
		Log.i("data_inserted", media_flag + "");
		return db.insert(DB_PREG_TABLE, null, initialValues);

	}

	int test3 = 0;

	public synchronized long insertPreg1(String name, String lmp, String hname,
			int caste, int rel, String mobile, boolean dbreg, long aid,
			long awid, int media_flag, int server_id, String dob, String sex,
			String weight, String pob, String m_stat, String c_stat,
			String m_death, String c_death, String last_visit, String edd,
			int food, int weight_food, String weight_size) {
		ContentValues initialValues = null;
		try {
			test3++;
			Log.e("count", test3 + " length of database");
			initialValues = new ContentValues();

			initialValues.put(KEY_NAME, name);
			initialValues.put(KEY_LMP, lmp);
			initialValues.put("m_stat", 0);
			initialValues.put("c_stat", 0);
			initialValues.put("hname", hname);
			initialValues.put("caste", caste);
			initialValues.put("religion", rel);
			initialValues.put("mobile", mobile);
			initialValues.put("dbreg", dbreg);
			initialValues.put("aid", aid);
			initialValues.put("awid", awid);
			initialValues
					.put("puid", String.format("%03d%03d", aid, getMaxt()));
			initialValues.put("media_flag", media_flag);
			initialValues.put("server_id", server_id);
			initialValues.put("dob", dob);
			initialValues.put("sex", sex);
			initialValues.put("weight", weight);
			initialValues.put("pob", pob);
			initialValues.put("m_stat", m_stat);
			initialValues.put("c_stat", c_stat);
			initialValues.put("m_death", m_death);
			initialValues.put("c_death", c_death);

			initialValues.put("last_visit", last_visit);
			initialValues.put("edd", edd);

			initialValues.put("food", food);
			initialValues.put("weight_food", weight_food);
			initialValues.put("weight_size", weight_size);
			Log.i("data_inserted", media_flag + "");

			Log.e("data_inserted", name + "");
			Log.e("data_inserted", caste + "");
		} catch (Exception e) {
			Log.e("Sqlite Exception", e.toString());
		}
		return db.insert(DB_PREG_TABLE, null, initialValues);
	}

	private int getMaxt() {
		try {
			openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int s = 0;
		String smax = null;
		Cursor cMax = db.rawQuery("select Max(_id) As _id from preg_reg", null);
		if (cMax.moveToFirst()) {
			s = cMax.getInt(cMax.getColumnIndex("_id"));
			smax = Integer.toString(s);
		}
		Log.d("***MAx******", "111111" + s);
		return s++;
	}

	public synchronized long logAct(String info) {
		Calendar c = Calendar.getInstance(Locale.getDefault());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		String dtime = df.format(c.getTime());
		ContentValues initialValues = new ContentValues();
		initialValues.put("info", info);
		initialValues.put("dtime", dtime);
		return db.insert("actlog", null, initialValues);
	}

	public synchronized Cursor getPendLog(String mydt) {
		String sql = "select _id,dtime,info from actlog where dtime>'" + mydt
				+ "' order by dtime";
		return db.rawQuery(sql, null);
	}

	public synchronized long insertSms(String msg) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("msg", msg);
		return db.insert("pend_sms", null, initialValues);
	}

	public synchronized void sendSMS(String phoneNumber, String msg) {
		long l = insertSms(msg);
		Log.d("info", msg + " " + l);
	}

	public synchronized boolean sendGPRS(String url, String msg, int reqtype) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("url", url);
		initialValues.put("msg", msg);
		initialValues.put("rtype", reqtype);

		Log.d("info", "121212" + url + "#" + msg);
		return db.insert("pend_web", null, initialValues) > 0;
	}

	public synchronized boolean updateSms(long rowId, int retry) {
		ContentValues args = new ContentValues();
		args.put("retry", retry);
		return db.update("pend_sms", args, "_id=" + rowId, null) > 0;
	}

	public synchronized boolean updatepreg_serverid(long rowId, int retry) {
		ContentValues args = new ContentValues();
		args.put("server_id", retry);
		return db.update("preg_reg", args, "_id=" + rowId, null) > 0;
	}

	public synchronized boolean updatepreg_mediaflag(long rowId, int flag) {
		ContentValues args = new ContentValues();
		args.put("media_flag", flag);
		return db.update("preg_reg", args, "server_id=" + rowId, null) > 0;
	}

	public synchronized boolean deleteSms(long rowId) {
		ContentValues args = new ContentValues();
		args.put("sent", 1);
		return db.update("pend_sms", args, "_id=" + rowId, null) > 0;
		// return db.delete("pend_sms", "_id=" + rowId, null) > 0;
	}

	public synchronized boolean deleteWeb(long rowId) {
		ContentValues args = new ContentValues();
		args.put("sent", 1);
		// return db.update("pend_web", args, "_id=" + rowId, null) > 0;
		return db.delete("pend_sms", "_id=" + rowId, null) > 0;
	}

	public synchronized boolean deleteWeb1(long rowId) {
		ContentValues args = new ContentValues();
		// args.put("sent", 1);
		// return db.update("pend_web", args, "_id=" + rowId, null) > 0;
		return db.delete("pend_web", "_id=" + rowId, null) > 0;
	}

	// ---deletes a particular contact---
	public synchronized boolean deletePreg(long rowId) {
		return db.delete(DB_PREG_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// ---retrieves all the contacts---
	public synchronized Cursor getAllPreg() {
		// return
		// db.rawQuery("select * from preg_reg where dob is null order by edd",
		// null);
		// String sql =
		// "select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(lmp))/30) mnth,dbreg,server_id,hname,child_name from preg_reg where server_id >"
		// + 0
		// +
		// " And last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by _id ASC ";
		String sql = "select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(lmp))/30) mnth,round((julianday('NOW')-julianday(dob))/30) mnth1,dbreg,server_id,hname,child_name from preg_reg where server_id>"
				+ 0
				+ " And last_visit=0 and ((m_stat<>1) and (c_stat<>1)) and ((mnth1>=0 and mnth1<=72) or (dob is null OR dob='null')) order by _id ASC ";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getANCList() {
		// and (round((julianday('NOW')-julianday(lmp))/90)-last_anc_visit)>0
		String sql = "select p._id,p.name,p.edd,p.dob,((julianday('NOW')-julianday(p.lmp))/90) dy,ifnull(v.tri,'0') last_anc_visit,lmp"
				+ ",round((julianday(date('NOW'))-julianday(p.lmp))/90)-ifnull(lastv,0) diff,p.server_id"
				+ " from preg_reg p left join ((select pid,group_concat(distinct tri) tri,max(tri) lastv from ans_anc group by pid)) v on p.server_id=v.pid where server_id>"
				+ 0
				+ " And (dob is null OR dob='null') "
				+ " order by abs(diff) desc,lmp";
		return db.rawQuery(sql, null);
	}

	public synchronized ArrayList<Integer> getANCdata() {
		// and (round((julianday('NOW')-julianday(lmp))/90)-last_anc_visit)>0
		String sql = "select pid,group_concat(distinct tri) tri,max(tri) lastv from ans_anc group by pid";
		ArrayList<Integer> data = new ArrayList<Integer>();
		Cursor c = db.rawQuery(sql, null);

		if (c.moveToFirst()) {
			do {
				data.add(c.getInt(c.getColumnIndex("pid")));
			} while (c.moveToNext());

		}
		return data;
	}

	public synchronized boolean UpdateQlist(int seq) {
		int ind = 0, lbl_qcount = 0;
		boolean res = true;
		String sql = "select count(_id) cnt from qb_anc where tri like '%"
				+ String.valueOf(seq) + "%'";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		anc_qcnt = c.getInt(0);
		// Log.d("info","Debug message"+String.valueOf(cnt)+" "+String.valueOf(seq));
		qarr = new int[anc_qcnt];
		tansdata = new Hashtable<Integer, Anc_answr>();
		sql = "select _id,q_type from qb_anc where tri like '%"
				+ String.valueOf(seq) + "%'";
		c = db.rawQuery(sql, null);
		if (c.moveToFirst())
			while (!c.isAfterLast()) {
				qarr[ind++] = c.getInt(0);
				if (c.getInt(1) == 3)
					lbl_qcount++;
				/*
				 * else { anc_ans tmp_ans=new anc_ans();
				 * //tansdata.put(c.getInt(0), new Anc_ans());
				 * //tmp_ans.qid=c.getInt(0); ansdata.add(tmp_ans); }
				 */
				c.moveToNext();
			}
		else
			res = false;
		anc_acnt = anc_qcnt - lbl_qcount;
		return res;
	}

	public synchronized boolean UpdateQlistpnc(int seq) {
		int ind = 0, lbl_qcount = 0;
		boolean res = true;
		String sql = "select count(_id) cnt from q_bank";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		pnc_qcnt = c.getInt(0);
		// Log.d("info","Debug message"+String.valueOf(cnt)+" "+String.valueOf(seq));
		pqarr = new int[pnc_qcnt + 1];
		pansdata = new Hashtable<Integer, Anc_answr>();
		sql = "select _id,q_type from q_bank order by dispseq";
		c = db.rawQuery(sql, null);
		if (c.moveToFirst())
			while (!c.isAfterLast()) {
				pqarr[ind++] = c.getInt(0);
				if (c.getInt(1) == 3)
					lbl_qcount++;
				/*
				 * else { anc_ans tmp_ans=new anc_ans();
				 * tmp_ans.qid=c.getInt(0); pansdata.add(tmp_ans); }
				 */
				c.moveToNext();
			}
		else
			res = false;
		pnc_acnt = pnc_qcnt - lbl_qcount;
		return res;
	}

	public synchronized int getQind(int qid) {
		boolean found = false;
		int i = 0, pos = -1;
		while (!found && i < anc_qcnt)
			if (qarr[i++] == qid) {
				found = true;
				pos = i - 1;
			}
		return pos;
	}

	public synchronized Cursor getHVCompList() {
		// return
		// db.rawQuery("select * from preg_reg where dob is null order by edd",
		// null);
		String sql = "select p.*,avd,(julianday(date(\"now\"))-julianday(svd)) diff from preg_reg p "
				+ "left join sch_visits s on p._id=s.pid and s.seq=7 "
				+ "where (julianday(date(\"now\"))-julianday(svd))>15 or last_visit=7 ";
		// String
		// sql="select * from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getDthList(String rep_type) {
		// return
		// db.rawQuery("select * from preg_reg where dob is null order by edd",
		// null);

		String sql = "select _id,name,edd,dob,m_stat,c_stat,m_death,c_death, "
				+ "round((julianday(c_death)-julianday(dob))/30) mnth from preg_reg "
				+ "where (m_stat=1 and m_death is not null) or (c_stat=1 and c_death is not null)";
		if (rep_type.equals("abrep"))
			sql = "select _id,name,dob,edd,m_stat,c_stat,m_death,c_death, "
					+ "round((julianday(dob)-julianday(lmp))/30) mnth "
					+ "from preg_reg where c_stat=1 and c_death is null";
		// String
		// sql="select * from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getAllPregList() {
		// Add hero
		String sql = "select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(lmp))/30) mnth,server_id,hname from preg_reg where dob='null' or dob IS NULL order by _id ASC";
		return db.rawQuery(sql, null);
		// String
		// sql="select * from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";1
		// return db.rawQuery(sql, null);
	}

	public synchronized boolean getserver_id(int server_id) {
		Cursor r = db.rawQuery("select server_id from preg_reg", null);
		if (r != null) {
			if (r.moveToFirst()) {
				do {
					Log.i(r.getInt(0) + "", server_id + "");
					if (r.getInt(0) == server_id)
						return true;

				} while (r.moveToNext());

			}
		}
		return false;

		// String
		// sql="select * from preg_reg where last_visit=0 and ((m_stat<>1) and (c_stat<>1)) order by edd ";
		// return db.rawQuery(sql, null);
	}

	// jitendra
	public synchronized Cursor getdueList(int flag) {
		String sql = "";
		if (flag == 0) {
			sql = "select _id,name,hname,edd from preg_reg where (julianday('NOW')-julianday(lmp))>56 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid) from ans_anc)";
		} else if (flag == 1) {
			sql = "select _id,name,hname,edd from preg_reg where (julianday('NOW')-julianday(lmp))>168 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid)from ans_anc where tri>2)";
		} else if (flag == 2) {
			sql = "select _id,name,hname,edd from preg_reg where (julianday('NOW')-julianday(lmp))>244 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid)from ans_anc where tri>3)";
		} else if (flag == 3) {
			sql = "select _id,name,hname,edd from preg_reg where (julianday('NOW')-julianday(lmp))>252 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid)from ans_anc where tri>4)";
		}
		return db.rawQuery(sql, null);
	}

	// jitendra
	public synchronized String getdueListcount(int flag) {
		String sql = "";

		sql = "select count(_id) as cnt from preg_reg where  (preg_reg.dob  is not null  and  preg_reg.dob!='null') and preg_reg._id not in(select distinct(pid) from imm_det where im_id="
				+ flag + ")";

		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		return String.valueOf(c.getInt(0));
	}

	// jitendra
	public synchronized String getdueListanc1(int flag) {
		String sql = "";
		if (flag == 0) {
			sql = "select count(_id) as cnt from preg_reg where (julianday('NOW')-julianday(lmp))>56 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid) from ans_anc)";
		} else if (flag == 1) {
			sql = "select count(_id) as cnt from preg_reg where (julianday('NOW')-julianday(lmp))>168 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid)from ans_anc where tri>2)";
		} else if (flag == 2) {
			sql = "select count(_id) as cnt from preg_reg where (julianday('NOW')-julianday(lmp))>244 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid)from ans_anc where tri>3)";
		} else if (flag == 3) {
			sql = "select count(_id) as cnt from preg_reg where (julianday('NOW')-julianday(lmp))>252 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid)from ans_anc where tri>4)";
		}

		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		return String.valueOf(c.getInt(0));
	}

	// jitendra
	public synchronized Cursor getdueListimmu(int flag) {
		String sql = "";
		if (flag == 0) {
			sql = "select  _id,child_name as name,name as hname,dob as edd from preg_reg where  (preg_reg.dob  is not null  and  preg_reg.dob!='null') and preg_reg._id not in(select distinct(pid) from imm_det where im_id=1)";
		} else if (flag == 1) {
			sql = "select  _id,child_name as name,name as hname,dob as edd from preg_reg where  (preg_reg.dob  is not null  and  preg_reg.dob!='null') and preg_reg._id not in(select distinct(pid) from imm_det where im_id=2)";
		} else if (flag == 2) {
			sql = "select  _id,child_name as name,name as hname,dob as edd from preg_reg where  (preg_reg.dob  is not null  and  preg_reg.dob!='null') and preg_reg._id not in(select distinct(pid) from imm_det where im_id=3)";
		}
		return db.rawQuery(sql, null);
	}

	// jitendra
	public synchronized Cursor getListHRP() {
		String sql = "";

		sql = "select _id,name,hname,edd from preg_reg where (julianday('NOW')-julianday(lmp))>56 and (preg_reg.dob='null' or preg_reg.dob='') and preg_reg._id not in(select distinct(pid) from ans_anc)";

		return db.rawQuery(sql, null);
	}

	// jitendra
	public synchronized Cursor getgraphlist(int flag) {
		String sql = "";
		if (flag == 0) {
			sql = "select Gender,month,sd1,sd2,sd3 from tbllchildmonthvalue where Gender='1' limit 37";
		} else if (flag == 1) {
			sql = "select Gender,month,sd1,sd2,sd3 from tbllchildmonthvalue where Gender='2' limit 37";
		}
		return db.rawQuery(sql, null);
	}

	public void deleteAllData() {
		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
			sqLiteDatabase.delete("preg_reg", null, null);

			Log.i("all data", "deleted");
			return;
		} catch (Exception localException) {
			Log.e("DB ERROR", localException.toString());
			localException.printStackTrace();
		}
	}

	public void deleteUnknownData() {
		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String awc_id = prefs.getString("id", "700").trim();
			int awc_idint = Integer.parseInt(awc_id);
			Log.e("data", "11111" + awc_idint);

			sqLiteDatabase.execSQL("DELETE FROM preg_reg WHERE awid <> "
					+ awc_idint);

			Log.i("all data", "deleted");
			return;
		} catch (Exception localException) {
			Log.e("DB ERROR", localException.toString());
			localException.printStackTrace();
		}
	}

	public synchronized void custom_qry(String sql) {
		db.execSQL(sql);
	}

	public synchronized Cursor getImmunList() {
		return db.rawQuery("SELECT _id, descr FROM immun", null);
	}

	public synchronized String getImmun(long rowid) {
		String res = "";
		Cursor c = db.rawQuery("select im_id,immun.descr,imdt from imm_det "
				+ "inner join immun on imm_det.im_id=immun._id " + "where pid="
				+ String.valueOf(rowid) + " order by imdt", null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			res = res + "|" + c.getString(0) + ":" + c.getString(1) + ":"
					+ strtodate(c.getString(2));
			c.moveToNext();
		}
		return res;
	}

	public synchronized Cursor getAwlist() {
		int area_asha = 0;
		int anm_area_id = 0;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String awc_id = prefs.getString("id", "101").trim();
		int awc_idint = Integer.parseInt(awc_id);
		Cursor cursor_area = db.rawQuery(
				"select area_id from awc_dets where awc_id=" + awc_idint + "",
				null);
		if (cursor_area != null) {
			if (cursor_area.moveToFirst()) {
				area_asha = cursor_area.getInt(0);

			}
		}

		// Cursor
		// canm_Area=db.rawQuery("select area_id from anm_dets where area_id="+area_asha+"",null);
		// if(canm_Area!=null)
		// {
		// if(canm_Area.moveToFirst())
		// {
		// anm_area_id=canm_Area.getInt(0);
		//
		// }
		// }
		// return db.rawQuery("select _id,name from asha_dets where area_id="
		// + area_asha + " order by name asc", null);
		// Add Hero
		String sql = "select * from (select 0 as _id,'Not Any' as descr union select _id,name from asha_dets where area_id='"
				+ area_asha + "' order by name asc)order by _id";
		return db.rawQuery(sql, null);

	}

	public synchronized Cursor getPregVisits(String mydt) {
		/*
		 * String sql=
		 * "select v._id,v.seq,v.pid,p.name,min(svd) sv_dt,(julianday(svd)-julianday(date("
		 * +mydt+"))) diff,p.hv_str,p.m_stat,p.c_stat from sch_visits v "
		 * +"inner join preg_reg p on p._id=v.pid " +"inner join ( "
		 * +"select pid,min(abs(julianday(svd)-julianday(date("
		 * +mydt+")))) mday "
		 * +" from sch_visits v inner join preg_reg p on p._id=v.pid "
		 * +"where seq>p.last_visit and avd is null "
		 * +"group by pid) m on v.pid=m.pid " +
		 * "where (m_stat<>1 or c_stat<>1) and avd is null and abs(julianday(svd)-julianday(date("
		 * +mydt+")))=mday " +"group by v.pid " +"order by sv_dt";
		 */
		String sql = "select sc._id,seq,sc.pid,p.name,svd sv_dt,(julianday(svd)-julianday(date("
				+ mydt
				+ "))) diff,p.hv_str,p.m_stat,p.c_stat "
				+ "from sch_visits sc inner join preg_reg p on p._id=sc.pid "
				+ "inner join (select pid,0 ind,max(svd) svdt from sch_visits v "
				+ "inner join preg_reg p on p._id=v.pid "
				+ "where avd is null	and seq>p.last_visit "
				+ " and svd between date("
				+ mydt
				+ ",'-15 days') and date("
				+ mydt
				+ ") "
				+ "group by pid "
				+ "UNION "
				+ "select pid,1 ind,min(svd) svdt from sch_visits where pid not in "
				+ "(select distinct pid from sch_visits v "
				+ "inner join preg_reg p on p._id=v.pid "
				+ "where avd is null	and seq>p.last_visit "
				+ " and svd between date("
				+ mydt
				+ ",'-15 days') and date("
				+ mydt
				+ ")) "
				+ "and svd>date("
				+ mydt
				+ ") "
				+ "group by pid) tmp on sc.pid=tmp.pid and sc.svd=tmp.svdt "
				+ "where (m_stat<>1 or c_stat<>1) and avd is null "
				+ "order by sv_dt";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getQuest(int grp) {
		String sql = "select * from q_bank where grp=" + String.valueOf(grp)
				+ " order by _id";
		return db.rawQuery(sql, null);
	}

	public synchronized int getPregNo() {
		// Cursor
		// mCursor=db.rawQuery("Select seq From sqlite_sequence where name='preg_reg'",
		// null);
		Cursor mCursor = db.rawQuery("select max(_id) from preg_reg", null);
		if (mCursor.moveToFirst())
			return mCursor.getInt(0);
		else
			return 0;
	}

	public synchronized Cursor getNextQuest(int qid, boolean branch, int dstat) {
		String sql = "select * from q_bank where _id=" + String.valueOf(qid);
		if (!branch)
			sql = sql + " and grp<2";
		if (dstat == 1)
			sql = sql + " and grp<>1 and grp<>4";
		else if (dstat == 2)
			sql = sql + " and grp=1";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getNextQuestAnc(int qid, boolean branch, int seq) {
		String sql = "select * from qb_anc where _id=" + String.valueOf(qid);
		// if (!branch) sql=sql+" and grp<2";
		// if (dstat==1) sql=sql+" and grp<>1 and grp<>4";
		// else if(dstat==2) sql=sql+" and grp=1";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getCatgr(int cid) {
		String sql = "select * from catgr where _id=" + String.valueOf(cid);
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getCouncInfo(int mnth, int gid) {
		String sql;
		if (mnth != 0)
			sql = "SELECT i._id _id,prompt,i.msg ctext,ms.msg grphead,help from infolist i "
					+ "inner join msglist ms on i.msg_id=ms._id "
					+ "inner join mnth_disp_order mo on ms._id=mo.msg_id and mo.mnth="
					+ String.valueOf(mnth) + " order by mo.disp_order,i._id";
		else
			sql = "SELECT i._id,prompt,i.msg ctext,ms.msg grphead,help from infolist i "
					+ "inner join msglist ms on i.msg_id=ms._id and ms._id="
					+ String.valueOf(gid) + " order by i._id";
		Log.d("MyDebug", sql);
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getModList() {
		String sql = "select _id,module from modlist";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getMsgList(int mod_id) {
		String sql = "select _id,msg from msglist where mod_id="
				+ String.valueOf(mod_id);
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getAllRem(int cid) {
		String sql = "select * from remedy where cid=" + String.valueOf(cid);
		return db.rawQuery(sql, null);
	}

	public synchronized String getpans(int pid, int qid) {
		// int qc[]={26,13,13,8,18,6};
		int pos = qid;
		String res = "", col = "answrg5";

		if (qid <= qc[0])
			col = "answrg0";
		else if (qid <= qc[1]) {
			col = "answrg1";
			pos = pos - qc[0] - 1;
		} else if (qid <= qc[2]) {
			col = "answrg2";
			pos = pos - qc[1] - 1;
		} else if (qid <= qc[3]) {
			col = "answrg3";
			pos = pos - qc[2] - 1;
		} else if (qid <= qc[4]) {
			col = "answrg4";
			pos = pos - qc[3] - 1;
		}
		String sql = "select " + col + " answr,seq from sch_visits where pid="
				+ String.valueOf(pid) + " and avd is not null order by seq";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		seq_str = "";
		while (!c.isAfterLast()) {
			String mstr = c.getString(0);
			String mcharr[] = mstr.split("\\ ");
			res = res + " " + mcharr[pos];
			seq_str = seq_str + " " + c.getInt(1);
			c.moveToNext();
		}
		return res;
	}

	public synchronized boolean UpdateQc() {
		/*
		 * 0 25 1 12 37 2 15 52 3 8 60 4 16 76
		 */

		int qs = 0, ind = 0;
		boolean res = true;
		String sql = "select grp,count(*) cnt from q_bank group by grp";
		Cursor c = db.rawQuery(sql, null);
		if (c.moveToFirst())
			while (!c.isAfterLast()) {
				qs = qs + c.getInt(1);
				ind = c.getInt(0);
				qc[ind] = qs;
				c.moveToNext();
			}
		else
			res = false;
		return res;
	}

	public synchronized Cursor getVisitSummary(int pid, boolean fvisit) {
		// select avd,gsumm from sch_visits where pid=1 and seq=1
		String tblName = "sch_visits";
		if (fvisit)
			tblName = "opt_visits";
		String sql = "select seq,avd,gsumm from " + tblName
				+ " where avd is not null and pid=" + String.valueOf(pid);// +" and seq="+String.valueOf(pid);
		sql = sql + " order by avd desc limit 1";
		return db.rawQuery(sql, null);
	}

	public synchronized Cursor getAVisitSummary(int pid, int seq) {
		// select avd,gsumm from sch_visits where pid=1 and seq=1
		seq = seq + 1;
		if (seq < 1)
			seq = 1;
		if (seq > 3)
			seq = 3;
		String sql = "select t" + String.valueOf(seq)
				+ "dt vdt,last_anc_visit from preg_reg where _id="
				+ String.valueOf(pid);
		// sql=sql+" order by avd desc limit 1";
		return db.rawQuery(sql, null);
	}

	public synchronized boolean regChild(long rowId, String dob, int pob,
			float weight, int m_stat, int c_stat, String sex, String childname,
			String childreg_flag) {
		ContentValues args = new ContentValues();
		args.put("dob", dob);
		args.put("weight", weight);
		args.put("c_stat", c_stat);
		args.put("m_stat", m_stat);
		args.put("pob", pob);
		args.put("sex", sex);
		// Add Hero

		args.put("child_name", childname);
		args.put("childreg_flag", childreg_flag);
		boolean valuerturn;
		// String sql = "";// add hero
		// try {
		// sql = "Update preg_reg set dob='" + dob + "',weight='" + weight
		// + "',c_stat='" + c_stat + "',m_stat='" + m_stat + "',pob='"
		// + pob + "',sex='" + sex + "'  where _id='" + rowId + "'";
		// valuerturn = true;
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// valuerturn = false;
		// }
		// return valuerturn;
		return db.update(DB_PREG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public synchronized boolean regDeath(long rowId, String doDth, int dstat) {
		ContentValues args = new ContentValues();

		switch (dstat) {
		case 0:
			args.put("c_death", doDth);
			args.put("c_stat", 1);
			break;
		case 1:
			args.put("m_death", doDth);
			args.put("m_stat", 1);
			break;
		case 2:
			args.put("m_death", doDth);
			args.put("c_death", doDth);
			args.put("m_stat", 1);
			args.put("c_stat", 1);
			break;
		case 3:
			args.put("m_stat", 1);
			args.put("c_stat", 1);
			break;
		}
		return db.update(DB_PREG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// ---retrieves a particular contact---
	public synchronized Cursor getPreg(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DB_PREG_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_LMP, "EDD", "dob", "pob", "sex",
				"weight", "m_stat", "c_stat", "hname", "caste", "religion",
				"mobile", "aid", "server_id", "child_name", "childreg_flag" },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public synchronized Cursor getPregInfo(long rowId) throws SQLException {
		String sql = "select p._id,name,lmp,edd,dob,m_stat,c_stat,date(v.avd) avd,last_visit from preg_reg p "
				+ "left join sch_visits v on v.pid=p._id and v.seq=p.last_visit"
				+ " where p._id=" + String.valueOf(rowId);
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public synchronized Cursor getAnsAnc(int pid, int qid, String filter) {
		if (filter.trim().isEmpty())
			filter = " 1)";
		String sql = "select qid,ans,qnty,dtval from ans_anc " + " where pid="
				+ String.valueOf(pid) + " and (qid=" + String.valueOf(qid)
				+ " and " + filter + " order by dtm desc limit 1";
		Cursor mCursor = db.rawQuery(sql, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	// ---updates a contact---
	public synchronized boolean updatePreg(int slno, String name, String lmp,
			String hname, int caste, int rel, String mobile, long aid,
			long awid, int media, int server_id, int f, int w_f,
			String weight_size, boolean b, String dob) {
		ContentValues args = null;
		try {
			args = new ContentValues();
			args.put(KEY_NAME, name);
			args.put(KEY_LMP, lmp);
			args.put("hname", hname);
			args.put("caste", caste);
			args.put("religion", rel);
			args.put("mobile", mobile);
			args.put("aid", awid);
			args.put("awid", aid);
			if (b) {
				if (!dob.equalsIgnoreCase("null") && !dob.equals(null)
						&& !dob.isEmpty()) {
					args.put("dob", dob);
				} else {
					args.put("dob", "null");
				}
				args.put("food", f);
				args.put("weight_food", w_f);
				args.put("weight_size", weight_size);
			}
			args.put("media_flag", media);

			Log.e("data_inserted", name + "");
			Log.e("data_inserted", server_id + "");
			if (server_id > 0) {
				return db.update(DB_PREG_TABLE, args, "server_id" + "="
						+ server_id + " And Server_id>" + 0, null) > 0;
			} else {
				return db.update(DB_PREG_TABLE, args, "_id=" + slno, null) > 0;
			}
		} catch (Exception e) {
			Log.e("Sqlite Exception-----------------------", e.toString());
			return false;
		}

	}

	public synchronized boolean updatePregVisitStr(long rowId, String hv_str) {
		ContentValues args = new ContentValues();
		args.put("hv_str", hv_str);
		return db.update(DB_PREG_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public synchronized boolean updateVisit(long rowId, String answrg0,
			String answrg1, String answrg2, String answrg3, String answrg4,
			String gsumm) {
		ContentValues args = new ContentValues();
		Calendar c = Calendar.getInstance(Locale.getDefault());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		String avd = df.format(c.getTime());
		args.put("avd", avd);
		args.put("answrg0", answrg0);
		args.put("answrg1", answrg1);
		args.put("answrg2", answrg2);
		args.put("answrg3", answrg3);
		args.put("answrg4", answrg4);
		// args.put("answrg5", answrg5);
		args.put("gsumm", gsumm);
		return db.update("sch_visits", args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public synchronized boolean optVisit(long pid, String answrg0,
			String answrg1, String answrg2, String answrg3, String answrg4,
			String gsumm) {
		ContentValues args = new ContentValues();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		String avd = df.format(c.getTime());
		args.put("pid", pid);
		args.put("svd", avd);
		args.put("seq", 0);
		args.put("avd", avd);
		args.put("answrg0", answrg0);
		args.put("answrg1", answrg1);
		args.put("answrg2", answrg2);
		args.put("answrg3", answrg3);
		args.put("answrg4", answrg4);
		// args.put("answrg5", answrg5);
		args.put("gsumm", gsumm);
		// return db.update("sch_visits", args, KEY_ROWID + "=" + rowId, null) >
		// 0;
		return db.insert("opt_visits", null, args) > 0;
	}

	public synchronized Cursor getSmsList() {
		return db.rawQuery("select _id,msg,retry from pend_sms where sent=0",
				null);
	}

	public synchronized Cursor getALLpreg(int id) {
		return db.rawQuery("select * from preg_reg where _id=" + id, null);
	}

	public synchronized Cursor getPdataList() {
		return db
				.rawQuery(
						"select _id,msg,url,retry,rtype,recive_flag from pend_web where sent=0",
						null);
	}

	public synchronized Cursor getServer_id(int _id) {
		return db.rawQuery("select server_id from preg_reg where _id=" + _id,
				null);
	}

	public synchronized int getServer_id_value(int _id) {
		Cursor c = db.rawQuery("select server_id from preg_reg where _id="
				+ _id, null);

		if (c.moveToFirst()) {
			return c.getInt(c.getColumnIndex("server_id"));
		}
		return 0;
	}

	public synchronized int getasha_id_value(int _id) {
		Cursor c = db.rawQuery("select aid from preg_reg where _id=" + _id,
				null);

		if (c.moveToFirst()) {
			return c.getInt(c.getColumnIndex("aid"));
		}
		return 0;
	}

	public synchronized Cursor getPregdata_photo() {
		return db.rawQuery(
				"select _id,server_id from preg_reg where media_flag=0", null);
	}

	public synchronized Cursor setPdataList(String id, String sentValue) {
		return db.rawQuery("UPDATE pend_web SET sent = " + sentValue
				+ " WHERE _id = " + id, null);
	}

	public Cursor rawQuery(String sql) {
		return db.rawQuery(sql, null);
	}

	@Override
	public synchronized void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// TODO Auto-generated method stub

	}

	public void beginTransaction() {
		db.beginTransaction();
	}

	public void endTransaction() {
		db.endTransaction();
	}

	public void setTransactionSuccessful() {
		db.setTransactionSuccessful();
	}

	public SQLiteStatement compileStatement(String sql) {
		return db.compileStatement(sql);
	}

	public synchronized String imm_txt(int immid) {
		Cursor c = db.rawQuery(
				"select descr from immun where _id=" + String.valueOf(immid),
				null);
		c.moveToFirst();
		return c.getString(0);
	}

	public synchronized int getOptAnc(int pid, int seq) {
		String str = "select optflag from ans_anc where pid="
				+ String.valueOf(pid) + " and tri=" + String.valueOf(seq)
				+ " order by dtm desc limit 1";
		Cursor c = db.rawQuery(str, null);
		if (c.moveToFirst())
			if (!c.isNull(0))
				return c.getInt(0) + 1;
			else
				return 0;
		else
			return 0;
	}

	public synchronized int getOptPnc(int pid, int seq) {
		String str = "select optflag from ans_pnc where pid="
				+ String.valueOf(pid) + " and hvid=" + String.valueOf(seq)
				+ " order by dtm desc limit 1";
		Cursor c = db.rawQuery(str, null);
		if (c.moveToFirst())
			if (!c.isNull(0))
				return c.getInt(0) + 1;
			else
				return 0;
		else
			return 0;
	}

	public synchronized boolean isAncReg(int pid) {
		String str = "select ans from ans_anc where pid=" + String.valueOf(pid)
				+ " and qid=1 and ans=1";
		Cursor c = db.rawQuery(str, null);
		if (c.moveToFirst())
			return !c.isNull(0);
		else
			return false;
	}

	public Cursor searchPregQuery(String query) {
		String sql = "select asha_id,_id,name,lmp,edd,m_stat,c_stat,dob,pob,weight,hv_str,last_visit,"
				+ "m_death,c_death,sex,dor,hname,mobile,caste,religion,dtime,awc_id from preg_reg_list";
		return db.rawQuery(sql, null);
	}

	public static String strtodate(String dstr) {
		String tmp = "-";
		if (dstr != null) {
			String dt_str[] = dstr.split("\\-");
			if (dt_str.length > 2)
				tmp = dt_str[2] + "-" + hmstr[Integer.parseInt(dt_str[1]) - 1]
						+ "-" + dt_str[0];
			else
				tmp = dstr;
		}
		return tmp;
	}

	static boolean isAirplaneModeOn(Context context) {

		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;

	}

	static Calendar removeTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	static int strToint(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}

	public synchronized Cursor getAllFilterdPreg(int first, int last) {

		return db
				.rawQuery(
						"select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(dob))/30) mnth,dbreg,server_id,hname,last_anc_visit,child_name from preg_reg WHERE server_id >"
								+ 0
								+ " And mnth>="
								+ first
								+ " And mnth<="
								+ last + " And last_visit=0 order by _id ASC",
						null);

	}

	public synchronized Cursor getAllFilterdPregNonDelivered() {

		/*
		 * return db .rawQuery(
		 * "SELECT _id,name,edd,dob,mobile,dbreg,server_id,hname,last_anc_visit   FROM preg_reg "
		 * + "WHERE server_id>0 And dob='null'" + " ORDER BY _id ASC", null);
		 */

		return db
				.rawQuery(
						"select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(dob))/30) mnth,dbreg,server_id,hname,last_anc_visit,child_name from preg_reg WHERE server_id >"
								+ 0
								+ " And (dob is null OR dob='null') And last_visit=0 order by _id ASC",
						null);

	}

	public synchronized Cursor getAllFilterdPregPnc(int first, int last) {
		Cursor c = db
				.rawQuery(
						"select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(dob))/30) mnth,dbreg,server_id,hname,last_anc_visit from preg_reg WHERE server_id >"
								+ 0
								+ " And mnth>="
								+ first
								+ " And mnth<"
								+ last + " order by _id ASC", null);
		if (c.moveToFirst()) {
			do {

				Cursor cur = null;
				int age_flag = 0;
				if (last == 1) {
					age_flag = 1;
					cur = db.rawQuery("select * from pnc_data where server_id="
							+ c.getInt(c.getColumnIndex("server_id"))
							+ " And age_flag=" + 1, null);
				} else if (last == 6) {
					age_flag = 2;
					cur = db.rawQuery("select * from pnc_data where server_id="
							+ c.getInt(c.getColumnIndex("server_id"))
							+ " And age_flag=" + 2, null);
				} else if (last == 13) {

					age_flag = 3;
					cur = db.rawQuery("select * from pnc_data where server_id="
							+ c.getInt(c.getColumnIndex("server_id"))
							+ " And age_flag=" + 3, null);
				}
				if (cur.getCount() == 0) {
					ContentValues values = new ContentValues();
					values.put("server_id",
							c.getInt(c.getColumnIndex("server_id")));
					values.put("month", c.getInt(c.getColumnIndex("mnth")));
					values.put("age_flag", age_flag);
					values.put("visit_value", "0");
					Log.e("ValueInsertedfor",
							c.getString(c.getColumnIndex("name")));
					db.insert("pnc_data", null, values);
				}

			} while (c.moveToNext());
		}

		return c;

	}

	public synchronized Cursor getAllFilterdPregList(int first, int last) {
		int fir = first;
		int second = ++first;
		int third = ++first;
		// Add Hero
		String sql = "SELECT _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(lmp))/30) mnth,server_id,hname FROM preg_reg WHERE (mnth="
				+ fir
				+ " or mnth="
				+ second
				+ " or mnth="
				+ third
				+ " or mnth="
				+ last
				+ ") AND (dob='null' or dob IS NULL) order by _id ASC";
		return db.rawQuery(sql, null);

	}

	public synchronized Cursor getAllPregFoodRecordList() {

		return db
				.rawQuery(
						"select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(lmp))/30) mnth,food,weight_food,weight_size,server_id,sex from preg_reg where server_id >"
								+ 0 + " AND (dob='null' or dob IS NULL)", null);

	}

	public synchronized Cursor getAllMotherFoodRecordList(int s_Month,
			int l_Month) {

		return db
				.rawQuery(
						"select _id,name,edd,dob,mobile,round((julianday('NOW')-julianday(dob))/30) mnth,food,weight_food,weight_size,server_id,sex from preg_reg where server_id >"
								+ 0
								+ " And (dob!=null or dob!='null') And mnth>="
								+ s_Month + " And mnth<=" + l_Month, null);

	}

	public void updateFoodRecord(int id, int food, int weight_food, float f) {
		ContentValues values = new ContentValues();
		values.put("food", food);
		values.put("weight_food", weight_food);
		values.put("weight_size", f);

		db.update("preg_reg", values, "_id=" + id, null);
		Log.d("done", "122  updation");

	}

	public synchronized ArrayList<Integer> selectserver_id() {
		// return
		// db.rawQuery("select * from preg_reg where dob is null order by edd",
		// null);
		String sql = "select server_id from preg_reg";
		ArrayList<Integer> ar = new ArrayList<Integer>();
		Cursor c = db.rawQuery(sql, null);

		if (c.moveToFirst()) {
			do {
				ar.add(c.getInt(c.getColumnIndex("server_id")));
			} while (c.moveToNext());
		}
		return ar;
	}

	public void deleteLocalData(int server_id) {
		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
			// SharedPreferences
			// prefs=PreferenceManager.getDefaultSharedPreferences(context);
			// String awc_id=prefs.getString("id", "700").trim();
			// int awc_idint=Integer.parseInt(awc_id);
			// Log.e("data","11111"+awc_idint);

			sqLiteDatabase.execSQL("DELETE FROM preg_reg WHERE server_id = "
					+ server_id);

			Log.i("all data", "deleted");
			return;
		} catch (Exception localException) {
			Log.e("DB ERROR", localException.toString());
			localException.printStackTrace();
		}
	}

	public int getID(String string) {
		int a = Integer.parseInt(string);
		Cursor c = db.rawQuery("select _id from preg_reg where server_id=" + a,
				null);
		int value = 0;
		if (c.moveToFirst()) {
			value = c.getInt(0);
		}
		Log.d("vale ", value + "");
		return value;

	}

	public void deletAllAncData(String string) {
		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
			sqLiteDatabase.delete(string, null, null);

			Log.i("all data", "deleted");
			return;
		} catch (Exception localException) {
			Log.e("DB ERROR", localException.toString());
			localException.printStackTrace();
		}

	}

	public void insertAns_Anc(int pid, int qid, int tri, int optflag, int ans,
			int qnty, int dtval, int dtm) {
		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("pid", pid);
			values.put("qid", qid);
			values.put("tri", tri);
			values.put("optflag", optflag);
			values.put("ans", ans);
			values.put("qnty", qnty);
			values.put("dtval", dtval);
			values.put("dtm", dtm);
			sqLiteDatabase.insert("ans_anc", null, values);
			return;
		} catch (Exception localException) {
			Log.e("DB ERROR", localException.toString());
			localException.printStackTrace();
		}

	}

	public void insertFoodHistory(int server_id, int food, int weight_food,
			String weight_size, String time) {
		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("server_id", server_id);
			values.put("food_flag", food);
			values.put("food_weight_flag", weight_food);
			values.put("food_size", weight_size);
			values.put("timestamp", time);

			sqLiteDatabase.insert("food_history", null, values);
			return;
		} catch (Exception localException) {
			Log.e("DB ERROR", localException.toString());
			localException.printStackTrace();
		}

	}

	public Cursor getFoodDataForServer_id(int server_id) {

		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

			return sqLiteDatabase.rawQuery(
					"select food_size,timestamp from food_history where server_id="
							+ server_id + " order by date(timestamp) ASC ",
					null);
		} catch (NullPointerException e) {
			return null;
		}

	}

	public Cursor getFoodDataForServer_id1(int server_id) {

		try {
			SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
			String sql = "select food_size,ABS(round((julianday(date(timestamp))-julianday(date(dob)))/30+0.5 ))  mnth from food_history inner join  preg_reg on preg_reg.server_id=food_history.server_id where preg_reg.server_id="
					+ server_id + " order by date(timestamp) ASC";
			return sqLiteDatabase.rawQuery(sql, null);
		} catch (NullPointerException e) {
			return null;
		}

	}

	public String checkdataforColor(int server_id, String string,
			String timestamp, int count, String gender) {
		// String gender=getGenderforserverid(server_id);
		if (gender.equalsIgnoreCase("b")) {
			return getdatafromfoodlist(count, string, "boys_weight_list");
		} else {
			return getdatafromfoodlist(count, string, "girls_weight_list");
		}

	}

	private String getdatafromfoodlist(int count, String string,
			String tablename) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor c = sqLiteDatabase.rawQuery("select  * from " + tablename
				+ " where months=" + count, null);
		String color = "r";
		if (c.moveToFirst()) {
			// float
			// th=Float.parseFloat(c.getString(c.getColumnIndex("third")));
			// float
			// fift=Float.parseFloat(c.getString(c.getColumnIndex("fifteenth")));
			// float
			// median=Float.parseFloat(c.getString(c.getColumnIndex("median")));
			// float
			// eightyfive=Float.parseFloat(c.getString(c.getColumnIndex("eightyfive")));
			// float
			// nintyseven=Float.parseFloat(c.getString(c.getColumnIndex("nintyseven")));
			float normal = Float.parseFloat(c.getString(c
					.getColumnIndex("normal")));

			float from_mol = Float.parseFloat(c.getString(c
					.getColumnIndex("malnour from")));
			float to_malnour = Float.parseFloat(c.getString(c
					.getColumnIndex("malnour to")));
			Log.e("Values", normal + "  " + from_mol + "  " + to_malnour);
			float serve_mol = Float.parseFloat(c.getString(c
					.getColumnIndex("min")));
			float value = Float.parseFloat(string);

			if (value < serve_mol) {
				color = "r";
			} else if (value >= from_mol && to_malnour >= value) {
				color = "y";
			} else if (value > normal) {
				color = "g";
			}
		}

		return color;

	}

	private String getGenderforserverid(int server_id) {
		Cursor c = db.rawQuery("select sex from preg_reg where server_id="
				+ server_id, null);
		return c.getString(c.getColumnIndex("sex"));

	}

	public synchronized int getmonth(String now, int server_id) {

		Cursor c = db.rawQuery("select round((julianday('" + now
				+ "')-julianday(dob))/30) mnth from preg_reg where server_id ="
				+ server_id, null);
		return c.getInt(c.getColumnIndex("mnth"));

	}

	public boolean checkforfoodvalue() {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor child = sqLiteDatabase
				.rawQuery(
						"select * from preg_reg where (dob!=null or dob!='null') And (food=2 or weight_food=2) and server_id>" + 0,
						null);
		Cursor preg = sqLiteDatabase
				.rawQuery(
						"select * from preg_reg where (dob='null' or dob IS NULL) And food=2 and server_id>" + 0,
						null);
		if (child.getCount() > 0) {

			return false;
		} else if (preg.getCount() > 0) {
			return false;
		} else {
			return true;
		}
	}

	public Cursor getAllServerId() {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor c = sqLiteDatabase
				.rawQuery(
						"select server_id,_id from preg_reg where server_id>" + 0,
						null);
		return c;
	}

	public void updateFoodData(int server_id) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Log.e("server_id inside update", server_id + "");
		try {
			ContentValues values = new ContentValues();
			values.put("server_id", server_id);
			values.put("food", 2);
			if (getPregDob(server_id)) {
				values.put("weight_food", 0);
			} else {
				values.put("weight_food", 2);
			}

			values.put("weight_size", 0);
			sqLiteDatabase.update("preg_reg", values, "server_id=" + server_id,
					null);
			return;
		} catch (Exception localException) {
			Log.e("DB ERROR", localException.toString());
			localException.printStackTrace();
		}

	}

	// hero addd method
	public synchronized Cursor monthreturn() {
		// Correct query
		// String
		// sql="select  case strftime('%m', timestamp) when '01' then 'January' when '02' then 'Febuary' when '03' then 'March' when '04' then 'April' when '05' then 'May' when '06' then 'June' when '07' then 'July' when '08' then 'August' when '09' then 'September' when '10' then 'October' when '11' then 'November' when '12' then 'December' else '' end as month from food_history a inner join preg_reg b on a.server_id=b.server_id  order by a.timestamp desc limit 1";
		String sql = "select  case strftime('%m', timestamp) when '01' then 'Febuary' when '02' then 'March' when '03' then 'April' when '04' then 'May' when '05' then 'June' when '06' then 'July' when '07' then 'August' when '08' then 'September' when '09' then 'October' when '10' then 'November' when '11' then 'December' when '12' then 'January' else '' end as month from food_history a inner join preg_reg b on a.server_id=b.server_id  order by a.timestamp desc limit 1";

		return db.rawQuery(sql, null);

	}

	// hero add
	public String getvalue(String Sql) {
		String iIntegerValue = "";
		Cursor cursor = null;
		try {
			getDB();
			cursor = db.rawQuery(Sql, null);
			if (cursor != null) {
				cursor.moveToFirst();
				while (cursor.isAfterLast() == false) {
					iIntegerValue = cursor.getString(0);
					cursor.moveToNext();
				}
				cursor.close();
			}
		} catch (Exception exception) {
			Log.e("DataProvider",
					"Error in getMaxRecord :: " + exception.getMessage());
		}
		return iIntegerValue;

	}

	private boolean getPregDob(int server_id) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor c = db.rawQuery("select *from preg_reg where server_id="
				+ server_id, null);
		c.moveToFirst();
		String dob = c.getString(c.getColumnIndex("dob"));
		if (dob.equals(null) || dob.equalsIgnoreCase("null")) {
			return true;
		} else {
			return false;
		}
	}

	public String getDob(int server_id) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		String dob = null;
		Log.e("server_id", server_id + "");

		Cursor c = db.rawQuery("select dob from preg_reg where server_id="
				+ server_id, null);
		if (c.moveToFirst()) {
			dob = c.getString(c.getColumnIndex("dob"));
		}
		return dob;
	}

	public Cursor getMsgData(int i) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		Cursor c = sqLiteDatabase.rawQuery(
				"select * from infolist where msg_id=" + i, null);

		return c;
	}

	public Cursor getMsgDiffID(int i, int j, int k, int l) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		Cursor c = sqLiteDatabase.rawQuery(
				"select * from infolist where msg_id=" + i + " OR msg_id=" + j
						+ " OR _id=" + k + " OR _id=" + l, null);

		return c;
	}

	public Cursor getMsgDataSecond(int i, int j, int k, int l) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		Cursor c = sqLiteDatabase.rawQuery("select * from infolist where _id="
				+ i + " OR _id=" + j + " OR _id=" + k + " OR _id=" + l, null);

		return c;
	}

	public Cursor getMsgDataSecondMsg_id(int i, int j, int k, int l, int m,
			int n, int o) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		Cursor c = sqLiteDatabase.rawQuery("select * from infolist where _id="
				+ i + " OR _id=" + j + " OR _id=" + k + " OR msg_id=" + l
				+ " OR msg_id=" + m + " OR msg_id=" + n + " OR msg_id=" + o,
				null);

		return c;
	}

	public Cursor getMsgDataforThird(int i, int j, int k) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		Cursor c = sqLiteDatabase.rawQuery(
				"select * from infolist where msg_id=" + i + " OR msg_id=" + j
						+ " OR msg_id=" + k, null);

		return c;
	}

	public Cursor getMsgDataforThirdId(int i, int j) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		Cursor c = sqLiteDatabase.rawQuery("select * from infolist where _id="
				+ i + " OR _id=" + j, null);

		return c;
	}

	public String getPncValue(int age_flag, int server_id) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		Cursor c = sqLiteDatabase.rawQuery(
				"select visit_value from pnc_data where server_id=" + server_id
						+ " And age_flag=" + age_flag, null);
		c.moveToFirst();
		return c.getString(0);
	}

	public void updatePnc_Visit(int i, String a, int server_id, int flag,
			String str) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		// if(a.contains("0") || a.contains(""))
		// {
		// values.put("visit_value", i);
		// }
		// else
		// {

		values.put("visit_value", str);

		// }

		sqLiteDatabase.update("pnc_data", values, "server_id=" + server_id
				+ " And age_flag=" + flag, null);

	}

}