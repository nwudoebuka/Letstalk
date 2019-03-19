package com.newage.letstalk;

/**
 * Created by Newage_android on 5/3/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.newage.letstalk.fragments.FriendListTabFragment;
import com.newage.letstalk.fragments.ContactTabFragment;
import com.newage.letstalk.fragments.ProfileTabFragment;

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
                FriendListTabFragment tab1 = new FriendListTabFragment();
                return tab1;
            case 1:
                ProfileTabFragment tab2 = new ProfileTabFragment();
                return tab2;
            case 2:
                ContactTabFragment tab3 = new ContactTabFragment();
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
