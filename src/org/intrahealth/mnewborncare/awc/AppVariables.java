package org.intrahealth.mnewborncare.awc;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppVariables {

	public static String API(Context c)
	{
		SharedPreferences prefs;
		prefs=PreferenceManager
				.getDefaultSharedPreferences(c);
		String url=prefs.getString("server_url", "localhost");
		String api="http://"+url+"/";
		
		return api;
		
	}
	public static String AP(Context c)
	{
		SharedPreferences prefs;
		prefs=PreferenceManager
				.getDefaultSharedPreferences(c);
		String url=prefs.getString("server_url", "localhost");
		return url;
	}

	public static String API_INDEX(Context c)
	{
		SharedPreferences prefs;
		prefs=PreferenceManager
				.getDefaultSharedPreferences(c);
		String url=prefs.getString("server_url", "localhost");
		
		String api="http://"+url+"/resturl/index.php/";
		return api;
		
	}
	
	public static String getUpdate_API_INDEX(Context c)
	{
		SharedPreferences prefs;
		prefs=PreferenceManager
				.getDefaultSharedPreferences(c);
		String url=prefs.getString("server_url", "localhost");
		SharedPreferences prf=PreferenceManager
				.getDefaultSharedPreferences(c);
		 DBAdapter mydb=new DBAdapter(c);
		 mydb.deletAllAncData("ans_anc");
		 String asha_id=prefs.getString("id", "700").trim();
	    	String api="http://"+url+"/resturl/awc_details/"+asha_id;
		return api;
		
	}
	
	
}
