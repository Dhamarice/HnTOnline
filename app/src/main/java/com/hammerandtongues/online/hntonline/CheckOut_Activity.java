package com.hammerandtongues.online.hntonline;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by NgonidzaIshe on 15/6/2016.
 */
public class CheckOut_Activity extends AppCompatActivity {

@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        System.gc();

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs02);
        tabLayout.addTab(tabLayout.newTab().setText("Checkout"));
        tabLayout.addTab(tabLayout.newTab().setText("Delivery/Pickup"));
        tabLayout.addTab(tabLayout.newTab().setText("Payment"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);




        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager02);
        final PagerAdapter adapter = new PagerAdapter02 (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }
}