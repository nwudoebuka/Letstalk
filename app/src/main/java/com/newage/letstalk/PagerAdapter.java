package com.newage.letstalk;

/**
 * Created by Newage_android on 5/3/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragmentchat tab1 = new TabFragmentchat();
                return tab1;
            case 1:
                TabFragmentprofile tab2 = new TabFragmentprofile();
                return tab2;
            case 2:
                TabFragmentcontact tab3 = new TabFragmentcontact();
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
