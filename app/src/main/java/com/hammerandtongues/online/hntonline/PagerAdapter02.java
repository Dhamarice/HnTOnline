package com.hammerandtongues.online.hntonline;

/**
 * Created by NgonidzaIshe on 16/5/2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter02 extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter02(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CheckoutFragment tab1 = new CheckoutFragment();
                return tab1;
            case 1:
                UserDetails tab2 = new UserDetails();
                return tab2;
            case 2:
                Payment tab3 = new Payment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
