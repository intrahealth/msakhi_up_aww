package org.intrahealth.mnewborncare.awc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.util.Log;

public class DownloadDataAsync extends AsyncTask<Void, Void, Void> {

	int test1 = 0, test2 = 0;
	DBAdapter db;
	ArrayList<Integer> server_Array, db_Array;

	Context mainactivity;
	ProgressDialog mProgressDialog;
	SharedPreferences prefsdata;
	String last_Month;
	boolean check_MonthChange = false;
	SharedPreferences.Editor edit;
	String month;
	String current_month;
	static ArrayList<Integer> serverid;

	public DownloadDataAsync(Context main) {
		mainactivity = main;
	}

	protected void onPreExecute() {

		super.onPreExecute();

		mProgressDialog = new ProgressDialog(mainactivity);
		mProgressDialog.setTitle("LOGIN");
		mProgressDialog.setMessage("Loading server data...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setCanceledOnTouchOutside(false);

		mProgressDialog.show();
		prefsdata = PreferenceManager.getDefaultSharedPreferences(mainactivity);
		edit = prefsdata.edit();
		serverid = new ArrayList<Integer>();

	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);

	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		Intent i = new Intent(mainactivity, Workflow.class);
		mainactivity.startActivity(i);
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			server_Array = new ArrayList<Integer>();
			db_Array = new ArrayList<Integer>();
			ArrayList<String> server_iddata = new ArrayList<String>();
			String data = POST(AppVariables.API(mainactivity)
					+ "preg_reg_api.php", "data", null);
			Log.e("download_data", data);
			db = DBAdapter.getInstance(mainactivity.getApplicationContext());
			// downloadBitmap("http://192.168.1.4/msakhi/download.php");
			if (data != null) {
				// db.deleteAllData();
				Log.i("maskhi", "data_deleted");
				db.deleteUnknownData();
				try {
					JSONArray ja = new JSONArray(data);
					Log.e("count", ja.length() + " length");
					for (int i = 0; i < ja.length(); i++) {
						JSONObject jo = ja.getJSONObject(i);

						int asha_id = jo.getInt("asha_id");
						int _id = jo.getInt("_id");
						String name = jo.getString("name");
						String lmp = jo.getString("lmp");
						String edd = jo.getString("edd");
						String m_stat = jo.getString("m_stat");
						String c_stat = jo.getString("c_stat");
						String dob = jo.getString("dob");
						Log.d("name", jo.getString("name"));
						Log.d("dob", jo.getString("dob"));
						String pob = jo.getString("pob");
						String weight = jo.getString("weight");
						String hv_str = jo.getString("hv_str");
						String last_visit = jo.getString("last_visit");
						String m_death = jo.getString("m_death");
						String c_death = jo.getString("c_death");
						String sex = jo.getString("sex");
						String dor = jo.getString("dor");
						String hname = jo.getString("hname");
						String mobile = jo.getString("mobile");
						try {
							month = jo.getString("reg_month");
							current_month = jo.getString("current_month");
						} catch (Exception e) {
							month = "null";
							current_month = "null";
							e.printStackTrace();
						}
						Log.e("_id", _id + " value");
						Log.e("month", month);
						Log.e("currentMonth", current_month);

						if (!month.equals(null) && !month.equals("null")) {
							last_Month = prefsdata.getString("month", "month");
							edit.putString("month", month).commit();
						}
						int caste;
						if (jo.getString("caste").equals(null)
								|| jo.getString("caste").equals("null")) {
							caste = 0;
						} else {
							caste = jo.getInt("caste");
						}

						int religion;
						if (jo.getString("religion").equals(null)
								|| jo.getString("religion").equals("null")) {
							religion = 0;
						} else {
							religion = jo.getInt("religion");
						}
						String dtime = jo.getString("dtime");
						int awc_id = jo.getInt("awc_id");
						int server_id = jo.getInt("server_id");
						int image_flag = jo.getInt("image_flag");
						int anm_flag = jo.getInt("anm_flag");
						serverid.add(server_id);
						server_Array.add(server_id);

						int food = 0;
						try {
							food = jo.getInt("food");

						} catch (Exception e) {
							food = 2;
						}
						int weight_food = 0;
						try {

							weight_food = jo.getInt("weight_food");
							if (dob.equals(null) || dob.equals("null")) {
								weight_food = 0;
							}

						} catch (Exception e) {
							weight_food = 2;
							if (dob.equals(null) || dob.equals("null")) {
								weight_food = 0;
							}
						}
						String weight_size = "0";
						try {
							weight_size = jo.getString("weight_size");
						} catch (Exception e) {
							weight_size = "0";
						}
						System.out.println(server_id);
						System.out.println(db.getserver_id(server_id));

						if (db.getserver_id(server_id)) {
							// Log.d("calledupdate",name+"called"+dob);
							try {//Add Hero
								test1++;
								db.updatePreg(_id, name, lmp, hname, caste,
										religion, mobile, awc_id, asha_id, 1,
										server_id, food, weight_food,
										weight_size, true, dob);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							test2++;
							Log.d("calling", "calling");
							db.insertPreg1(name, lmp, hname, caste, religion,
									mobile, true, asha_id, awc_id, 1,
									server_id, dob, sex, weight, pob, m_stat,
									c_stat, m_death, c_death, last_visit, edd,
									food, weight_food, weight_size);
						}
						if (anm_flag == 1) {
							server_iddata.add(server_id + "");

						}

					}

					Log.e("count", test1 + " length of update");
					Log.e("count", test2 + " length of insert");

					Log.d("calling", "calling");

					db_Array = db.selectserver_id();
					for (int count = 0; count < db_Array.size(); count++) {
						System.out.print(server_Array);
						if (!(server_Array.contains(db_Array.get(count)))) {
							Log.d("server_id", "" + db_Array.get(count));

							db.deleteLocalData(db_Array.get(count));
						}
					}
					for (int i = 0; i < server_iddata.size(); i++) {
						String server_id;
						server_id = server_iddata.get(i);
						System.out.println(server_id);
						try {
							downlaodfile(AppVariables.API(mainactivity)
									+ "download.php", server_id + "");
							POST(AppVariables.API(mainactivity)
									+ "download_success.php", "response",
									server_id);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (db.checkforfoodvalue()
							&& !month.equalsIgnoreCase(current_month)) {
						Log.e("month change inside download awc", "change");
						edit.putString("month", current_month).commit();
						edit.putBoolean("changesdone", true).commit();
					} else if (month.equalsIgnoreCase("null")
							&& current_month.equalsIgnoreCase("null")) {
						// edit.putString("month", current_month).commit();
						edit.putBoolean("changesdone", true).commit();
					} else {
						edit.putBoolean("changesdone", false).commit();
					}
					downloadFoodHistoryData();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception");
		}

		return null;
	}

	private void downloadFoodHistoryData() throws IOException {
		StringBuffer result = new StringBuffer();
		InputStream inputStream = null;
		Log.e("download food called", "food");
		String data = AppVariables.API(mainactivity) + "resturl/foodh";
		URL url = new URL(data);
		inputStream = url.openConnection().getInputStream();
		InputStream in = new BufferedInputStream(inputStream);

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line;
		while ((line = reader.readLine()) != null) {
			Log.d("Line", "1221" + line);
			result.append(line);
		}
		Log.e("food data", result.toString());
		ParseFoodHistoryData(result.toString());

	}

	private void ParseFoodHistoryData(String data) {

		try {
			JSONArray arr = new JSONArray(data);
			db.deletAllAncData("food_history");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject jobj = arr.getJSONObject(i);
				int server_id = jobj.getInt("server_id");

				int food = jobj.getInt("food");
				int weight_food = jobj.getInt("weight_food");
				String weight_size = jobj.getString("weight_size");
				String time = jobj.getString("reg_date");
				db.insertFoodHistory(server_id, food, weight_food, weight_size,
						time);
				// if(prefsdata.getBoolean("monthchange", false))
				// {
				// db.insertFoodHistory(server_id,food,weight_food,weight_size,time);
				// }
			}

		} catch (Exception e) {

		}

	}

	public String POST(String url, String type, String server_id)
			throws Exception {
		InputStream inputStream = null;
		String result = "";

		JSONObject jsonObject = null;
		// 1. create HttpClient
		HttpClient httpclient = new DefaultHttpClient();

		// 2. make POST request to the given URL
		HttpPost httpPost = new HttpPost(url);

		String json = "";

		if (type.equals("data")) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mainactivity);
			String uid = prefs.getString("id", "1");
			// 3. build jsonObject

			jsonObject = new JSONObject();
			jsonObject.put("awc_id", Integer.parseInt(uid));
			json = jsonObject.toString();

		}
		if (type.equals("response")) {
			json = server_id;
		}
		// 4. convert JSONObject to JSON to String

		System.out.println("jsondataget" + json);
		// ** Alternative way to convert Person object to JSON string usin
		// Jackson Lib
		// ObjectMapper mapper = new ObjectMapper();
		// json = mapper.writeValueAsString(person);

		// 5. set json to StringEntity
		StringEntity se = new StringEntity(json);

		// 6. set httpPost Entity
		httpPost.setEntity(se);

		// 7. Set some headers to inform server about the type of the content
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		// 8. Execute POST request to the given URL
		HttpResponse httpResponse = httpclient.execute(httpPost);

		// 9. receive response as inputStream
		inputStream = httpResponse.getEntity().getContent();

		// 10. convert inputstream to string
		if (inputStream != null)
			result = convertInputStreamToString(inputStream);
		else
			result = "Did not work!";

		// 11. return result
		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public String downlaodfile(String FileDownloadPath, String fileName)
			throws Exception {

		Log.e("downloadfile", fileName);
		File root = android.os.Environment.getExternalStorageDirectory();
		File dir = new File(Environment.getExternalStorageDirectory() + "/"
				+ Workflow.bfolder + "/pdata/");
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		File file = new File(dir, fileName + ".jpg");
		HttpClient httpclient = new DefaultHttpClient();

		// 2. make POST request to the given URL
		HttpPost httpPost = new HttpPost(FileDownloadPath);

		String json = "";

		// 3. build jsonObject

		// 4. convert JSONObject to JSON to String

		long startTime = System.currentTimeMillis();
		System.out.println(json);
		// ** Alternative way to convert Person object to JSON string usin
		// Jackson Lib
		// ObjectMapper mapper = new ObjectMapper();
		// json = mapper.writeValueAsString(person);

		// 5. set json to StringEntity
		StringEntity se = new StringEntity(fileName);

		// 6. set httpPost Entity
		httpPost.setEntity(se);

		// 7. Set some headers to inform server about the type of the content
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		// 8. Execute POST request to the given URL
		HttpResponse httpResponse = httpclient.execute(httpPost);

		// 9. receive response as inputStream
		InputStream inputStream = httpResponse.getEntity().getContent();

		BufferedInputStream bufferinstream = new BufferedInputStream(
				inputStream);
		Log.e("bufferinput", inputStream.toString());
		ByteArrayBuffer baf = new ByteArrayBuffer(5000);
		int current = 0;
		while ((current = bufferinstream.read()) != -1) {
			Log.e("bufferbyte", current + "");
			baf.append((byte) current);
		}

		FileOutputStream fos = new FileOutputStream(file);
		fos.write(baf.toByteArray());
		fos.flush();
		fos.close();

		Log.d("DownloadManager",
				"download ready in"
						+ ((System.currentTimeMillis() - startTime) / 1000)
						+ "sec");
		int dotindex = fileName.lastIndexOf('.');
		if (dotindex >= 0) {
			fileName = fileName.substring(0, dotindex);

		}

		return fileName;

	}
}
