package com.hammerandtongues.online.hntonline;

/**
 * Created by NgonidzaIshe on 16/5/2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter033 extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter033(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Login tab1 = new Login();
                return tab1;
            case 1:
                Register tab2 = new Register();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
