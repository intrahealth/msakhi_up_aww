package org.intrahealth.mnewborncare.awc;

import java.io.File;
import java.util.ArrayList;

import org.intrahealth.mnewborncare.awc.R;
import org.intrahealth.mnewborncare.awc.FoodPregFragment.PregAdap;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FoodMotherFrag extends Fragment {

	
	Cursor cursor;

	DBAdapter obj_Db;
	ArrayList<PregInfo> prg_list;
	Context con;
	LayoutInflater lay_infla;
	ListView lv_preglist;
	PregAdap obj_Adapter;
	Button btn_save;
	FoodMotherFrag obj;
	//TextView tv_Cat;
	RelativeLayout relay_container;
	Context mContext = getActivity();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
	
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	View v=inflater.inflate(R.layout.lay_food_list, null);
	init(v);
	getAllPregList();
	setAdapter();	
	saveListener();
   return v;
	}
	private void saveListener() {
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	          for(PregInfo preg:prg_list)
	          {
	        	 
	        	  obj_Db.updateFoodRecord(preg.getId(),preg.getFood(),preg.getWeight_food(),preg.getWeight_size());
	        	  JSONObject json=new JSONObject();
	        	  try {
	        
	        		Log.d("id","222"+preg.getId());
					json.put("server_id",preg.getServer_id());
					json.put("food",preg.getFood());
					json.put("weight_food",preg.getWeight_food());
					json.put("weight_size",preg.getWeight_size());
					json.put("id",preg.getId());
					Log.d("datajson","jsonObject"+json.toString());
					obj_Db.sendGPRS(AppVariables.API(con)+"save.php", json.toString(),0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	  Toast.makeText(con, "जानकारी सेव हो गयी",100).show();
	        	  getActivity().finish();
	          }
				
			}
		});
		
	}
	private void setAdapter() {
		lv_preglist.setAdapter(obj_Adapter);
		
		
	}
	private void getAllPregList() {
		
		//cursor=obj_Db.getAllMotherFoodRecordList();
	   
	    if(cursor!=null)
	    {
	    	if(cursor.moveToFirst())
	    	{
	    		do{
	    			PregInfo obj_info=new PregInfo();
	    			obj_info.setId(cursor.getInt(cursor.getColumnIndex("_id")));
	    			obj_info.setName(cursor.getString(cursor.getColumnIndex("name")));
	    			obj_info.setEdd(cursor.getString(cursor.getColumnIndex("edd")));
	    			obj_info.setDob(cursor.getString(cursor.getColumnIndex("dob")));
	    			obj_info.setMoblile_No(cursor.getString(cursor.getColumnIndex("mobile")));
	    			obj_info.setMonth(cursor.getString(cursor.getColumnIndex("mnth")));
	    			int food=cursor.getInt(cursor.getColumnIndex("food"));
	    			int weight_food=cursor.getInt(cursor.getColumnIndex("weight_food"));
	    		    float weight_size=cursor.getFloat(cursor.getColumnIndex("weight_size"));
	    		    obj_info.setServer_id(cursor.getInt(cursor.getColumnIndex("server_id")));
                    obj_info.setFood(food);
                    obj_info.setWeight_food(weight_food);
                    obj_info.setWeight_size(weight_size);
	    			prg_list.add(obj_info);
	    	   }while(cursor.moveToNext());
	    	}
	    }
		
		
	}
	private void init(View v) {
		con=getActivity();
		obj=this;
		//tv_Cat=(TextView) v.findViewById(R.id.tv_cate);
	//	tv_Cat.setText("माता");
		lay_infla=getActivity().getLayoutInflater();
		prg_list=new ArrayList<PregInfo>();
		obj_Adapter=new PregAdap();
		lv_preglist=(ListView)v.findViewById(R.id.list);
		lv_preglist.setItemsCanFocus(true);
		btn_save=(Button)v.findViewById(R.id.btnNewBirth);
	    obj_Db=DBAdapter.getInstance(con);
	 
		
	}
   class PregAdap extends BaseAdapter
   {

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return prg_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	         if(convertView==null)
	         {
	        	 convertView=lay_infla.inflate(R.layout.food_row, null);
	        	 
	         }
	        // final CheckBox cb_food=(CheckBox) convertView.findViewById(R.id.checkBox1);
		    // CheckBox cb_weight=(CheckBox) convertView.findViewById(R.id.checkBox2);
		     final EditText edit=(EditText) convertView.findViewById(R.id.editText1);
	         TextView tv=(TextView) convertView.findViewById(R.id.name_entry);
	         TextView tvEdd = (TextView) convertView.findViewById(R.id.edd_entry);
	 		 TextView tvDob = (TextView) convertView.findViewById(R.id.dob_entry);
	 		 final TextView tv_unit=(TextView) convertView.findViewById(R.id.tv_unit);
	 		 TextView tv_month = (TextView) convertView.findViewById(R.id.slno);
	 		 ImageLoader Loader=new ImageLoader(con);
	 		 TextView tvSlno = (TextView) convertView.findViewById(R.id.slno);
	 		 TextView tvMobie = (TextView) convertView.findViewById(R.id.mobile_no);
	 		 tvDob.setVisibility(View.GONE);
	 		 tv_unit.setVisibility(View.GONE);
	 		 tv_month.setText(""+prg_list.get(position).getMonth());
	 		 final ImageView thumb_image = (ImageView) convertView.findViewById(R.id.list_image);
	 		 tvEdd.setText("सम्भावित: "+prg_list.get(position).getEdd());
	 		// tvDob.setText("जन�?म की तारीख: " +prg_list.get(position).getDob());
	 		 tvMobie.setText("मोबाइल नंबर : " +prg_list.get(position).getMoblile_No());
 		     edit.setVisibility(View.GONE);
 		     edit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
 		     edit.setSelectAllOnFocus(true);
             tv.setText(prg_list.get(position).getName());
             Log.d("getview","1212  ****getView()Called*****"+prg_list.get(position).getName());
//             if(prg_list.get(position).getFood()==1)
//             {
//            	 Log.d("food","1212"+prg_list.get(position).getName());
//            	 cb_food.setChecked(true);
//             }
//             else
//             {
//            	 cb_food.setChecked(false);
//             }
//             if(prg_list.get(position).getWeight_food()==1)
//             {
//            	 Log.d("Weight_food","1212"+prg_list.get(position).getName());
//            	 cb_weight.setChecked(true);
//            	 edit.setVisibility(View.VISIBLE);
//            	 tv_unit.setVisibility(View.VISIBLE);
//            	 edit.setText(prg_list.get(position).getWeight_size()+"");
//            	 
//             }
//             else
//             {
//            	 cb_weight.setChecked(false);
//             }
//
//	       cb_food.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(cb_food.isChecked())
//				{
//					    Log.d("cb_food","1212"+prg_list.get(position).getName());
//			        	prg_list.get(position).setFood(1);
//				}
//				else
//				{
//					    prg_list.get(position).setFood(0);
//				}
//				
//			}
//		});
//
//	  cb_weight.setOnClickListener(new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//           if(((CheckBox)v).isChecked())
//           {
//        	   Log.d("cb_weight","1212"+prg_list.get(position).getName());
//	        	edit.setVisibility(View.VISIBLE);
//	        	tv_unit.setVisibility(View.VISIBLE);
//	         	prg_list.get(position).setWeight_food(1);
//	         	prg_list.get(position).setWeight_size(0);
//	         	edit.setText(prg_list.get(position).getWeight_size()+""); 
//           }
//           else
//           {
//             	edit.setVisibility(View.GONE);
//             	tv_unit.setVisibility(View.GONE);
//	        	prg_list.get(position).setWeight_food(0);
//	        	prg_list.get(position).setWeight_size(0); 
//           }
//		}
//	});
	      edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			//	Toast.makeText(getApplicationContext(),"ontextchange"+s, 1000).show();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
//				Toast.makeText(getApplicationContext(), "before"+s, 1000).show();
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
				if(s!=null && !s.toString().isEmpty() )
				{
					Log.d("s","121"+s);
					float i;
					try
					{
				      i=Float.parseFloat(s.toString());
					}
					catch(NumberFormatException e)
					{
						i=0;
						edit.setText("0.0");
					}
				prg_list.get(position).setWeight_size(i);
				}
			}
		});
	       
	      String imgfile = null;
	        if(prg_list.get(position).getServer_id()>0)
	        {
	           imgfile =Environment.getExternalStorageDirectory()+ File.separator +Workflow.bfolder +"/pdata/"+
	        		prg_list.get(position).getServer_id() +".jpg";
	        }
	        else
	        {
	        	imgfile =Environment.getExternalStorageDirectory()+ File.separator +Workflow.bfolder +"/pdata/"+
	        			prg_list.get(position).getId() +".jpg";
	        }
	      Loader.DisplayThumbnail(imgfile, thumb_image);
//	         imgfile = Environment.getExternalStorageDirectory()
//	 				+ File.separator + Workflow.bfolder + "/pdata/"
//	 				+ c.getString(slCol) + ".jpg";
		return convertView;
	}
}
}
