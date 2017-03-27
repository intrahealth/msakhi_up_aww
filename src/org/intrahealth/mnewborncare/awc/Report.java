package org.intrahealth.mnewborncare.awc;

import java.io.File;
import java.io.IOException;

import org.intrahealth.mnewborncare.awc.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
//import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Report extends ListActivity {
	private DBAdapter mydb;
	TextView txtCount;
	int id=0;
	String mname,rep_type;
	MediaPlayer mediaPlayer = new MediaPlayer();
	

	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.report);
	    TextView tvHead=(TextView)findViewById(R.id.tvHead);
	    tvHead.setText("Death list");
	    txtCount=(TextView)findViewById(R.id.txtCount);

		  //btnModBirth.setTypeface(face);
	    mydb=DBAdapter.getInstance(getApplicationContext());
	    /*
	    mydb = new DBAdapter(this);
	    try {
	    	mydb.createDataBase();
			mydb.openDataBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    */
	    Bundle extras = getIntent().getExtras();
        if (extras!= null) {
        	//id = extras.getInt("id");
        	rep_type=extras.getString("rep_type");
        }
	    
	    if (rep_type.equals("abrep")) tvHead.setText("Abortion list");
	    Cursor c=mydb.getDthList(rep_type);
	    txtCount.setText("Total :"+String.valueOf(c.getCount()));
	    startManagingCursor(c);
        // the desired columns to be bound
	            String[] from = new String[] { DBAdapter.KEY_NAME, "edd",DBAdapter.KEY_ROWID };
	            // the XML defined views which the data will be bound to
	            int[] to = new int[] { R.id.name_entry, R.id.number_entry };	
		
		LazyCursorAdapterdth ca=new LazyCursorAdapterdth(this,R.layout.list_row,c,from,to);
	    setListAdapter(ca);
	    ListView lst=(ListView)findViewById(android.R.id.list);
	    lst.setOnItemClickListener(new OnItemClickListener()
	    {
	        public void onItemClick(AdapterView<?> adapterView, View v,int position, long arg3)
	        { 
	        	 Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                 id = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                 mname=cursor.getString(cursor.getColumnIndex("name"));
	        	Toast.makeText(getApplicationContext(), "" + id, Toast.LENGTH_SHORT).show();
	        	try {
		    		String audioFile;
		    		audioFile=Environment.getExternalStorageDirectory().getAbsolutePath()
		    				+ "/mcare/pdata/"+String.valueOf(id)+".3gp";
	        		
		    		//mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/tmp/test.3gp");
		    		mediaPlayer.reset();
		    		mediaPlayer.setDataSource(audioFile);
		    		mediaPlayer.prepare();
		    		mediaPlayer.start();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    			}

	        	//return true;
	        }
	    });
	    
	    lst.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?>  adapterView, View v,int position, long arg3) {
				// TODO Auto-generated method stub
	        	 Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                 int key_id = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                 ImageView img=new ImageView(getApplicationContext());
                 String imgfile=Environment.getExternalStorageDirectory()+ File.separator + "/mcare/pdata/"+
                 String.valueOf(key_id) +".jpg";
                 Bitmap bmp = BitmapFactory.decodeFile(imgfile);
                 img.setImageBitmap(bmp);
				loadPhoto(img, 100, 100);
				return false;
			}
		
	    });
	   
	  }
	

	  
	  private void loadPhoto(ImageView imageView, int width, int height) {

	        ImageView tempImageView = imageView;


	        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
	        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

	        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
	                (ViewGroup) findViewById(R.id.layout_root));
	        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
	        image.setImageDrawable(tempImageView.getDrawable());
	        imageDialog.setView(layout);
	        imageDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }

	        });


	        imageDialog.create();
	        imageDialog.show();     
	    }

}
