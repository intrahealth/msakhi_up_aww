package org.intrahealth.mnewborncare.awc;


import java.io.File;

import org.intrahealth.mnewborncare.awc.*;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class HomeVisitAdapter extends SimpleCursorAdapter  {

	//private Context context;
	private int layout;
	public ImageLoader imageLoader;
	private LayoutInflater inflater=null;
	int nameCol,eddCol,slCol,dobCol,mnCol,seqCol,triCol;
	Context con;
	Typeface face;
	private int server_idcol;
	DBAdapter dba;
	int age_flag;

	
	public HomeVisitAdapter(Context context, int layout, Cursor c,String[] from, int[] to, int flag) {
		super(context, layout, c, from, to);
		//this.context = context;
		this.layout = layout;
		inflater = LayoutInflater.from(context);
		imageLoader=new ImageLoader(context);
		dba=DBAdapter.getInstance(context);
        nameCol = c.getColumnIndex("name");
        eddCol = c.getColumnIndex("edd");
        slCol = c.getColumnIndex("_id");
        mnCol = c.getColumnIndex("mnth");
        triCol = c.getColumnIndex("last_anc_visit");
        server_idcol=c.getColumnIndex("server_id");
        face=Typeface.createFromAsset(context.getAssets(),"monospace821.ttf");
        con=context;
        age_flag=flag;
      
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        View v = inflater.inflate(layout, parent, false);
        Cursor c = getCursor();
        int  cpos=(int)Math.ceil(c.getInt(mnCol));

        TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry);
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        TextView tvHvInd = (TextView)v.findViewById(R.id.home_visit_ind);
        TextView tvHvLeg= (TextView)v.findViewById(R.id.home_visit_leg);
        TextView tvmobile_no= (TextView)v.findViewById(R.id.mobile_no);
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        tvName.setText(c.getString(nameCol));
//   	String dt_str[]=plmp.split("\\-");
//		dtPicker.init(Integer.parseInt(dt_str[0]),Integer.parseInt(dt_str[1])-1, Integer.parseInt(dt_str[2]), new OnDateChangedListener() {
        tvHvInd.setTypeface(face);
        tvHvLeg.setTypeface(face);
        tvHvLeg.setText("| 1 | 2 | 3 | 4 |");
        Log.e("ageFlag", age_flag+"");
        String value=dba.getPncValue(age_flag,c.getInt(server_idcol));
        Log.e("LastAncValue",value);
        tvHvInd.setText(str_fmt_visit(dba.getPncValue(age_flag,c.getInt(server_idcol)),cpos));
        tvEdd.setText("समभावित: "+DBAdapter.strtodate(c.getString(eddCol))+" ("+cpos+")");
        tvSlno.setText(dba.getID(c.getString(server_idcol))+"");
        tvHvInd.setTextColor(getTextColor(str_fmt_visit(dba.getPncValue(age_flag,c.getInt(server_idcol)),cpos), tvSlno,cpos));
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + Workflow.bfolder+"/pdata/"+
        		c.getString(server_idcol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);
        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

 
        //int position= c.getPosition();
    	int cpos=(int)Math.ceil(c.getFloat(mnCol));
//        cpos=cpos/30;
    	TextView tvName = (TextView)v.findViewById(R.id.name_entry); 
        TextView tvEdd = (TextView)v.findViewById(R.id.edd_entry); 
        TextView tvSlno = (TextView)v.findViewById(R.id.slno); 
        TextView tvHvInd = (TextView)v.findViewById(R.id.home_visit_ind);
        TextView tvHvLeg= (TextView)v.findViewById(R.id.home_visit_leg);
        ImageView thumb_image=(ImageView)v.findViewById(R.id.list_image);
        tvHvInd.setTypeface(face);
        tvHvLeg.setTypeface(face);
        tvHvLeg.setText("| 1 | 2 | 3 | 4 |");
        tvHvInd.setText(str_fmt_visit(dba.getPncValue(age_flag,c.getInt(server_idcol)),cpos));
        tvName.setText(c.getString(nameCol));
        tvEdd.setText("समभावित: "+DBAdapter.strtodate(c.getString(eddCol))+" ("+cpos+")");
        tvSlno.setText(dba.getID(c.getString(server_idcol))+"");
        tvHvInd.setTextColor(getTextColor(str_fmt_visit(dba.getPncValue(age_flag,c.getInt(server_idcol)),cpos), tvSlno,cpos));
   
        String imgfile=Environment.getExternalStorageDirectory()+ File.separator + Workflow.bfolder+"/pdata/"+
        		c.getString(server_idcol) +".jpg";
        //imageLoader.DisplayImage(imgfile, thumb_image);
        imageLoader.DisplayThumbnail(imgfile, thumb_image);

    }
 private int getTextColor(String string, TextView tvSlno,int c) {
	   char  []str=string.toCharArray();
	   String firsttoken=Character.toString(str[2]);
	   String secondtoken=Character.toString(str[6]);
	   String thirdtoken=Character.toString(str[10]);
	   if(c==1)
	   {
		  if(firsttoken.equals("√"))
		   {
			   return Color.BLACK;
		   }
		   else
		   {
			   return con.getResources().getColor(R.color.green); 
		   }
	   }
	   else if(c==2)
	   {
		 if(secondtoken.equals("√"))
		   {
			   return Color.BLACK;
		   }
	   else if(firsttoken.equals("√"))
		   {
			   return con.getResources().getColor(R.color.green); 
		   }
		   else if(firsttoken.equals("√") && secondtoken.equals("√"))
		   {
			   return Color.BLACK;
		   }
		   else if(firsttoken.equals("x") && secondtoken.equals("*"))
		   {
			   return Color.RED;
		   }
		   else
		   {
			   return con.getResources().getColor(R.color.green);  
		   }
	   }
	   else if(c==3)
	   {
		  
	    if(thirdtoken.equals("√"))
		   {
			   return Color.BLACK;
		   }
	     else if(secondtoken.equals("√"))
		   {
			   return con.getResources().getColor(R.color.green); 
		   }
		   else if(secondtoken.equals("√") && thirdtoken.equals("√"))
		   {
			   return Color.BLACK;
		   }
		   else if(secondtoken.equals("x") && thirdtoken.equals("*"))
		   {
			   return Color.RED;
		   }
		   else
		   {
			   return con.getResources().getColor(R.color.green);  
		   }
	   }
	   else
	   {
		   return con.getResources().getColor(R.color.green);  
	   }
	    
	}



    private String str_fmt_visit(String instr,int cpos)
	  {
    	Log.e("Value Str_fmt", instr);
		  StringBuilder tmp=new StringBuilder("| * | * | * | * |");
		  char tick='√';
		  for(int i=1;i<5;i++)
		  {
			if (instr.contains(String.valueOf(i)))  
			{
			  tmp.setCharAt(i*4-2, tick);
			}
//			else if (i<cpos) 
//				{
//				tmp.setCharAt(i*4-2, 'x');
//				
//				}
		  }
		return tmp.toString();
	  }
    
}