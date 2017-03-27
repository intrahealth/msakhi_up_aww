package org.intrahealth.mnewborncare.awc;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageShowAct extends Activity{
	int flag;
	int position;
	Button bt_quest;
	TextView tv_head;
	private Button btnNext;
	private Button btnBack;
	private DBAdapter db_Ins;
	ImageView img_View;
	Context mCon;
	String visit_Str;
	Cursor cur;
	MediaPlayer mp,beep_mp;
	String[] first={"शिशु की गर्माहट","खतरे के लक्षण - शिशु","खतरे के लक्षण - माँ","शिशु का वजन","केवल स्तनपान","साफ़ सूखी नाल","नवजात टीकाकरण"};
	String[] second={"केवल स्तनपान","टीकाकरण","गर्भ निरोधक तरीके"," आंगनवाडी सुविधाएं -1"};
	String[] Third={"पूरक पोषाहार","टीकाकरण","गर्भ निरोधक तरीके","आंगनवाडी सुविधाएं-2"};
	 AssetFileDescriptor afd;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counc_info);
		Intent intent=getIntent();
		
		   flag=intent.getIntExtra("flag_Value", 5);
		   position=intent.getIntExtra("position", 0);
		   beep_mp=MediaPlayer.create(getApplicationContext(), R.raw.beep7);
		   mp=MediaPlayer.create(getApplicationContext(), R.raw.beep7);
		   try {
			beep_mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
		
		   init();
		   getDBData();
		   addListener();
		   btnBack.setVisibility(View.INVISIBLE);
		   if(flag==1)
		   {
			   bt_quest.setText(first[position]);
			   visit_Str=first[position];
		   }
		   else if(flag==2)
		   {
			   bt_quest.setText(second[position]);
			   visit_Str=second[position];
		   }
		   else
		   {
			   bt_quest.setText(Third[position]);
			   visit_Str=Third[position];
		   }
		
	}

	private void getDBData() {
		if(flag==1)
		{
		if(flag==1 &&position<6)
		{
			if(position==0)
			{
				cur=db_Ins.getMsgData(8);
			}
			else if(position==1)
			{
				cur=db_Ins.getMsgData(17);
			}
			else if(position==2)
			{
				cur=db_Ins.getMsgData(16);
			}
			else if(position==3)
			{
				cur=db_Ins.getMsgData(10);
			}
			else if(position==4)
			{
				cur=db_Ins.getMsgData(13);
			}
			else if(position==5)
			{
				cur=db_Ins.getMsgData(11);
			}
		   
		}
		else if(flag==1 && position==6)
		{
			cur=db_Ins.getMsgDiffID(12,20,88,89);
		}
		}
		else if(flag==2)
		{
			if(position==0)
			{
				cur=db_Ins.getMsgData(13);
			}
			else if(position==1)
			{
				cur=db_Ins.getMsgDataSecond(87,90,91,92);
			}
			else if(position==2)
			{
				cur=db_Ins.getMsgDataSecondMsg_id(113,115,116,28,29,30,31);
			}
			else if(position==3)
			{
				Toast.makeText(mCon,"no item to show", 1000).show();
				cur=db_Ins.getMsgDataforThirdId(87,93);
			}
		}
		else
		{
			if(position==0)
			{
				cur=db_Ins.getMsgDataforThird(23,32,33);
			}
			else if(position==1)
			{
				cur=db_Ins.getMsgDataforThirdId(87,93);
			}
			else if(position==2)
			{
				cur=db_Ins.getMsgDataSecondMsg_id(113,115,116,28,29,30,31);
			}
			else if(position==3)
			{
				Toast.makeText(mCon,"no item to show", 1000).show();
				cur=db_Ins.getMsgDataforThirdId(87,93);
			}
		}
		
	}

	private void addListener() {
	btnNext.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			cur.moveToNext();
			if(!cur.isAfterLast())
			{  
				beep_mp.start();
				try {
					afd = getAssets().openFd("cosnd/"+String.valueOf(cur.getInt(cur.getColumnIndex("_id")))+"coa.3gp"); //Beginning counsel audio
					mp.reset();
					mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					mp.prepare();
					mp.start();	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				 
				bt_quest.setText(cur.getString(cur.getColumnIndex("msg")));
				tv_head.setText(cur.getString(cur.getColumnIndex("prompt")));
				String name=cur.getInt(cur.getColumnIndex("_id"))+"co";
				int id=cur.getInt(cur.getColumnIndex("_id"));
	
				showimageSource( id);

				btnBack.setVisibility(View.VISIBLE);
			}
			else{
				openDialog();
			}

		}

		
	});
	btnBack.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			cur.moveToPrevious();
			if(!cur.isBeforeFirst() && !cur.isAfterLast())
			{
				beep_mp.start();
				try {
					afd = getAssets().openFd("cosnd/"+String.valueOf(cur.getInt(cur.getColumnIndex("_id")))+"coa.3gp"); //Beginning counsel audio
					mp.reset();
					mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
					mp.prepare();
					mp.start();	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				bt_quest.setText(cur.getString(cur.getColumnIndex("msg")));
				tv_head.setText(cur.getString(cur.getColumnIndex("prompt")));
				int id=cur.getInt(cur.getColumnIndex("_id"));
				
				showimageSource( id);
				
				
			}
			else{
				
				btnBack.setVisibility(View.INVISIBLE);
			}
			
		}
	});
		
	}

	private void init() {
		db_Ins=new DBAdapter(this);
		bt_quest=(Button) findViewById(R.id.btnQuest);
		tv_head=(TextView) findViewById(R.id.tvHead);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnBack =(Button) findViewById(R.id.btnBack);
		img_View=(ImageView) findViewById(R.id.imgCounc);
		mCon=this;
		
		
	}
	public void openDialog()
	{
		AlertDialog.Builder customDialog 
	     = new AlertDialog.Builder(this);
		customDialog
         .setMessage(visit_Str+" समाप्त! आगे ?")
         .setCancelable(false)
         .setPositiveButton("हा", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
					btnNext.setVisibility(android.view.View.INVISIBLE);
					mp.reset();
					mp.release();
					beep_mp.release();
//					mydb.close();
					finish();
             }
         })
         .setNegativeButton("ना", null)
         .show();
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void showimageSource(int id) {
		
		if(id==43)
		{
			img_View.setImageResource(R.drawable.fourthree);
		}
		else if(id==44)
		{
			img_View.setImageResource(R.drawable.fourfour);
		}
		else if(id==45)
		{
			img_View.setImageResource(R.drawable.fourfive);
		}
		else if(id==72)
		{
			img_View.setImageResource(R.drawable.seventwo);
		}
		else if(id==73)
		{
			img_View.setImageResource(R.drawable.seventhree);
		}
		else if(id==74)
		{
			img_View.setImageResource(R.drawable.sevenfour);
		}
		else if(id==75)
		{
			img_View.setImageResource(R.drawable.sevenfive);
		}
		else if(id==76)
		{
			img_View.setImageResource(R.drawable.sevensix);
		}
		else if(id==77)
		{
			img_View.setImageResource(R.drawable.sevenseven);
		}
		else if(id==78)
		{
			img_View.setImageResource(R.drawable.seveneight);
		}
		else if(id==79)
		{
			img_View.setImageResource(R.drawable.sevennine);
		}
		else if(id==80)
		{
		
		}
		else if(id==81)
		{
			img_View.setImageResource(R.drawable.eightone);
		}
		else if(id==82)
		{
			img_View.setImageResource(R.drawable.eighttwo);
		}
		else if(id==83)
		{
			img_View.setImageResource(R.drawable.eightthree);
		}
		else if(id==64)
		{
			img_View.setImageResource(R.drawable.sixfour);
		}
		else if(id==65)
		{
			img_View.setImageResource(R.drawable.sixfive);
		}
		else if(id==66)
		{
			img_View.setImageResource(R.drawable.sixsix);
		}
		else if(id==67)
		{
			img_View.setImageResource(R.drawable.sixseven);
		}
		else if(id==68)
		{
			img_View.setImageResource(R.drawable.sixeight);
		}
		else if(id==69)
		{
			img_View.setImageResource(R.drawable.sixnine);
		}
		else if(id==70)
		{
			img_View.setImageResource(R.drawable.sevenzero);
		}
		else if(id==71)
		{
		
		}
		else if(id==50)
		{
			img_View.setImageResource(R.drawable.fivezero);
		}
		else if(id==51)
		{
			img_View.setImageResource(R.drawable.fiveone);
		}
		else if(id==52)
		{
			img_View.setImageResource(R.drawable.fivetwo);
		}
		else if(id==55)
		{
			img_View.setImageResource(R.drawable.fivefive);
		}
		else if(id==56)
		{
			img_View.setImageResource(R.drawable.fivesix);
		}
		else if(id==57)
		{
			img_View.setImageResource(R.drawable.fiveseven);
		}
		else if(id==58)
		{
			img_View.setImageResource(R.drawable.fiveeight);
		}
		else if(id==59)
		{
			img_View.setImageResource(R.drawable.fivenine);
		}
		else if(id==60)
		{
			img_View.setImageResource(R.drawable.sixzero);
		}
		else if(id==61)
		{
			img_View.setImageResource(R.drawable.sixone);
		}
		else if(id==53)
		{
			img_View.setImageResource(R.drawable.fivethreee);
		}
		else if(id==54)
		{
			img_View.setImageResource(R.drawable.fivefour);
		}
		else if(id==86)
		{
			img_View.setImageResource(R.drawable.eightsix);
		}
		else if(id==87)
		{
			img_View.setImageResource(R.drawable.eightseven);
		}
		else if(id==88)
		{
			img_View.setImageResource(R.drawable.eighteight);
		}
		else if(id==89)
		{
			img_View.setImageResource(R.drawable.eightnine);
		}
		else if(id==90)
		{
			img_View.setImageResource(R.drawable.ninezero);
		}
		else if(id==91)
		{
			img_View.setImageResource(R.drawable.nineone);
		}
		else if(id==92)
		{
			img_View.setImageResource(R.drawable.ninetwo);
		}
		else if(id==113)
		{
			img_View.setImageResource(R.drawable.eleventhree);
		}
		else if(id==115)
		{
			img_View.setImageResource(R.drawable.elevenfive);
		}
		else if(id==116)
		{
			img_View.setImageResource(R.drawable.elevensix);
		}
		else if(id==117)
		{
			img_View.setImageResource(R.drawable.elevenseven);
		}
		else if(id==118)
		{
			img_View.setImageResource(R.drawable.eleveneight);
		}
		else if(id==119)
		{
			img_View.setImageResource(R.drawable.elevennine);
		}
		else if(id==120)
		{
			img_View.setImageResource(R.drawable.ninetwo);
		}
		else if(id==121)
		{
			img_View.setImageResource(R.drawable.twelveone);
		}
		else if(id==122)
		{
			img_View.setImageResource(R.drawable.twelvetwo);
		}
		else if(id==123)
		{
			img_View.setImageResource(R.drawable.twelvethree);
		}
		else if(id==124)
		{
			img_View.setImageResource(R.drawable.twelvefour);
		}
		else if(id==125)
		{
			img_View.setImageResource(R.drawable.twelvefive);
		}
		else if(id==126)
		{
			img_View.setImageResource(R.drawable.twelvesix);
		}
		else if(id==127)
		{
			img_View.setImageResource(R.drawable.twelveseven);
		}
		else if(id==97)
		{
			img_View.setImageResource(R.drawable.nineseven);
		}
		else if(id==98)
		{
			img_View.setImageResource(R.drawable.nineeight);
		}
		else if(id==99)
		{
			img_View.setImageResource(R.drawable.ninenine);
		}
		else if(id==100)
		{
			img_View.setImageResource(R.drawable.tenzero);
		}
		else if(id==101)
		{
			img_View.setImageResource(R.drawable.tenone);
		}
		else if(id==102)
		{
			img_View.setImageResource(R.drawable.tentwo);
		}
		else if(id==103)
		{
			img_View.setImageResource(R.drawable.tenthree);
		}
		else if(id==93)
		{
			img_View.setImageResource(R.drawable.ninethree);
		}
		else
		{
			img_View.setImageResource(R.drawable.apply);
		}
		
	}
}
