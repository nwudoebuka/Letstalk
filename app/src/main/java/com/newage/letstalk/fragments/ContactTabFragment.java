package com.newage.letstalk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

import com.newage.letstalk.CustomAdapter;
import com.newage.letstalk.R;

/**
 * Created by Newage_android on 5/3/2018.
 */

public class ContactTabFragment extends Fragment {
    ListView lv;
    Context context;

    public static int[] prgmImages = new int[4];
    public static String[] prgmNameList = new String[4];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_contact, container, false);

        context=getActivity().getApplicationContext();
        for(int i=0;i<=3;i++){
            prgmImages[i] = R.drawable.group;
            if(i == 0) {
                prgmNameList[i] = "Religion Group";

            }
            if(i == 1) {
                prgmNameList[i] = "Family Group";

            }
            if(i == 2) {
                prgmNameList[i] = "Friends Group";

            }

            if(i == 3) {
                prgmNameList[i] = "Business Group";

            }
        }

        lv=(ListView) view.findViewById(R.id.list_view);
        lv.setAdapter(new CustomAdapter(getActivity(), prgmNameList,prgmImages));
        return view;
    }
}
