package com.example.quickchat;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:
                RequestFragment requestFragment = new RequestFragment();
                return  requestFragment;

            case 2:
                GroupFragement groupFragement = new GroupFragement();
                return groupFragement;
            case 3:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Chat";

            case 1:
                return "REQUEST";

            case 2:
                return "Group";

            case 3:
                return "contact";
        }

        return super.getPageTitle(position);
    }
}
