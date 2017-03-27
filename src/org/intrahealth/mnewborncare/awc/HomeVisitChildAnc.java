package org.intrahealth.mnewborncare.awc;


import org.intrahealth.mnewborncare.awc.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class HomeVisitChildAnc extends ListActivity {
	private DBAdapter mydb;
	TextView txtCount;
	LayoutInflater inflater;
	int id=0;
	String mname;
	 SimpleAdapter adapter;
	 ListView lst;
	 Button bt_Save;
	Cursor c;
	String[] items;
	String[] first={"शिशु की गर्माहट","खतरे के लक्षण - शिशु","खतरे के लक्षण - माँ","शिशु का वजन","केवल स्तनपान","साफ़ सूखी नाल","नवजात टीकाकरण"};
	String[] second={"केवल स्तनपान","टीकाकरण","गर्भ निरोधक तरीके"," आंगनवाडी सुविधाएं -1"};
	String[] Third={"पूरक पोषाहार","टीकाकरण","गर्भ निरोधक तरीके","आंगनवाडी सुविधाएं-2"};
	int[] keys;
	Intent intent;
	int flag;
	int server_id;
	 

	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.counc_modlist);
	    intent=getIntent();
	    flag=intent.getIntExtra("flag_Value", 5);
	    server_id=intent.getIntExtra("server_Id", 0);
	    Log.e("ServerId+Flag", flag+" + "+server_id);
	    init();
	    
	    setAdapter();
	    
	    lst.setOnItemClickListener(new OnItemClickListener()
	    {

			public void onItemClick(AdapterView<?> adapterView,  View v,int position, long arg3) {
			Intent intent = new Intent(getApplicationContext(),MessageShowAct.class);
			intent.putExtra("flag_Value", flag);
			intent.putExtra("position", position);
			startActivity(intent);

			}

	    });
	    bt_Save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    String a=mydb.getPncValue(flag,server_id);
		    Log.e("Value  Of Vist_value", a);
		    for(int i=1;i<5;i++)
		    {
		    	if(!a.contains(String.valueOf(i)))
		    	{
		    		String str=a+","+i;
		    		Log.e("i+a+si+f+str",i+"   "+a+"   "+server_id+"   "+flag+"  "+str);
		    		mydb.updatePnc_Visit(i,a,server_id,flag,str);
		    		break;
		    	}
		    }
		    finish();
			}
			
		});
	  }
	 private void setAdapter() {
		 lst.setAdapter(adapter);
		
	}
	private void init() {
		inflater=getLayoutInflater();
		adapter=new SimpleAdapter(this,R.layout.list_sel_row);
		lst=(ListView)findViewById(android.R.id.list);
		bt_Save=(Button) findViewById(R.id.save_hv);
		bt_Save.setVisibility(View.VISIBLE);
		bt_Save.setEnabled(true);
		mydb=new DBAdapter(this);
		
	}
	class SimpleAdapter extends ArrayAdapter
	 {

		public SimpleAdapter(Context context, int resource) {
			super(context, resource);
			// TODO Auto-generated constructor stub
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(flag==1)
			{
				return first.length;
			}
			else if(flag==2)
			{
				return second.length;
			}
			else
			{
				return Third.length;
			}
			
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return super.getItemId(position);
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return super.getItem(position);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null)
			{
				convertView=inflater.inflate(R.layout.list_sel_row,null);
			}
			TextView tv=(TextView) convertView.findViewById(R.id.tvItem);
			if(flag==1)
			{
			  tv.setText(first[position]);
			}
			else if(flag==2)
			{
				tv.setText(second[position]);
			}
			else
			{
				tv.setText(Third[position]);
			}
			return convertView;
		}
		 
	 }
	
}