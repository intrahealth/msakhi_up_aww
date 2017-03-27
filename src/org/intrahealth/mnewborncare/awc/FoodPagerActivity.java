package org.intrahealth.mnewborncare.awc;

import org.intrahealth.mnewborncare.awc.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class FoodPagerActivity extends FragmentActivity{

	ViewPager view_page;
	PagerAdapter pager;
	private static final int num_col=2;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.lay_food_pager);
		init();
		
	}
	private void init() {
		view_page=(ViewPager) findViewById(R.id.pager);
		pager=new PagerAdapter(getSupportFragmentManager());
		view_page.setAdapter(pager);
		}
	class PagerAdapter extends FragmentPagerAdapter
	{

		public PagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			if(arg0==0)
			{
				return new FoodPregFragment();	
			}
			else{
				return new FoodMotherFrag();	
			}
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return num_col;
		}
	}
}
