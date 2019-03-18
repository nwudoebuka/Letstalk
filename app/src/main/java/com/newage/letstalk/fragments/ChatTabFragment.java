package com.newage.letstalk.fragments;

/**
 * Created by Newage_android on 5/3/2018.
 */

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import com.newage.letstalk.Chat2;
import com.newage.letstalk.Chat3;
import com.newage.letstalk.activity.Dashboard;
import com.newage.letstalk.HttpParse;
import com.newage.letstalk.R;
import com.newage.letstalk.SessionManager;
import com.newage.letstalk.adapter.FriendListAdapter;
import com.newage.letstalk.api.ApiInterface;
import com.newage.letstalk.api.RetrofitService;
import com.newage.letstalk.model.Friend;
import com.newage.letstalk.utils.Utility;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatTabFragment extends Fragment implements FriendListAdapter.FriendClickListener {
    FloatingActionButton chat;
    public int REQUESTCODE = 1;
    String finalResult;
    String HttpURL = "https://globeexservices.com/letstalk/check_contact.php";
    String HttpURLin = "https://globeexservices.com/letstalk/invite.php";
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    TextView pdt;
    LinearLayout ntf;
    TextView ntftxt;
    SessionManager session;
    ProgressBar progressBar;
    String phoneNumber;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pdt = (TextView) view.findViewById(R.id.pdt);
        ntftxt = (TextView) view.findViewById(R.id.ntftxt);
        ntf = (LinearLayout) view.findViewById(R.id.notify);
        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar1);
        chat = (FloatingActionButton) view.findViewById(R.id.fab);

        session = new SessionManager(getContext());
        phoneNumber = session.getPhoneNumber();

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUESTCODE);
                }
            }
        });

        final FriendListAdapter mAdapter = new FriendListAdapter(this);
        final RecyclerView recyclerView = view.findViewById(R.id.friends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ApiInterface api = RetrofitService.initializer();
        Call<List<Friend>> call = api.getFriendList(phoneNumber);
        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(@NonNull Call<List<Friend>> call, @NonNull Response<List<Friend>> response) {
                if (response.isSuccessful()) {
                    List<Friend> friends = response.body();
                    mAdapter.swapItems(friends);
                }
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onFriendClick(Friend friend) {
        String nam = friend.getName();
        String phone = friend.getPhone();
        String img = friend.getImageUrl();

//        Intent i = new Intent(getContext(), Chat2.class);
        Intent i = new Intent(getContext(), Chat3.class);
        i.putExtra("user", nam);
        i.putExtra("img", img);
        i.putExtra("phone", phone);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {
        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        if (ResultCode == Activity.RESULT_OK) {
            Uri uri = ResultIntent.getData();
            if (uri != null) {
                try (Cursor cursor1 = Objects.requireNonNull(getActivity()).getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor1 != null && cursor1.moveToFirst()) {
                        String TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                        String IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        int IDresultHolder = Integer.valueOf(IDresult);

                        if (IDresultHolder == 1) {
                            try (Cursor cursor2 = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null)) {
                                while (cursor2 != null && cursor2.moveToNext()) {
                                    String number = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    String name = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                    UserFriendCheckFunction2(number, name, phoneNumber);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void UserFriendCheckFunction2(final String friendPhone, final String friendName, final String userPhoneNumber) {
        if (!Utility.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
            Toast.makeText(getContext(), "No internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("nameec", friendName);
        map.put("phonec", friendPhone);
        map.put("username", userPhoneNumber);

        ApiInterface api = RetrofitService.initializer();
        Call<String> call = api.checkContact2(map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {

                    String resp = response.body();
                    Toast.makeText(getContext(), resp, Toast.LENGTH_LONG).show();

                    assert resp != null;

                    if (resp.equalsIgnoreCase("true")) {
//                        Intent myIntent = new Intent(getActivity(), Dashboard.class);
//                        getActivity().startActivity(myIntent);

                        //TODO

                        Toast.makeText(getActivity(), "Added", Toast.LENGTH_LONG).show();
                    } else if (resp.equalsIgnoreCase("he is not a user")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                        alert.setMessage("Not a user: would you invite " + friendName + " to letstalk?");
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        );
                        alert.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserInviteFriendFunction2(friendPhone, friendName, phoneNumber);
                                        dialog.dismiss();
                                    }
                                }
                        );
                        alert.show();
                    } else if (resp.equalsIgnoreCase("Contact is already added")) {
                        Toast.makeText(getContext(), resp, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    public void UserInviteFriendFunction2(final String friendPhone, final String friendName, final String userPhone) {
        if (!Utility.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
            Toast.makeText(getContext(), "No internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("phonein", friendPhone);
        map.put("nameofsender", userPhone);
        map.put("nameoff", friendName);

        ApiInterface api = RetrofitService.initializer();
        Call<String> call = api.inviteContact(map);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String resp = response.body();
                    assert resp != null;
                    Toast.makeText(getContext(), resp, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public boolean checkPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), p) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUESTCODE);
                }
            }
        }
    }








    @Deprecated
    public void UserFriendCheckFunction(final String friendPhone, final String friendName, final String UserPhoneNumber) {

        class UserRegisterFunctionClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdt.setText("fetching");
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pdt.setText("done!!!");
                Toast.makeText(getActivity().getApplicationContext(), httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                if (httpResponseMsg.toString().equalsIgnoreCase("true")) {

                    Intent myIntent = new Intent(getActivity(), Dashboard.class);

                    getActivity().startActivity(myIntent);
                    Toast.makeText(getActivity(), "Added", Toast.LENGTH_LONG).show();
                    //listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                } else if (httpResponseMsg.toString().equalsIgnoreCase("he is not a user")) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Not a user: would you invite " + friendName + " to letstalk?");
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    );
                    alert.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserRegisterFunctioninvite(friendPhone, phoneNumber, friendName);
                                }
                            }
                    );
                    alert.show();

                    //listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    ntf.setVisibility(View.GONE);
                    ntftxt.setText("not yet registered, would you like to invite" + friendName + " to letstalk?");

                } else if (httpResponseMsg.toString().equalsIgnoreCase("Contact is already added")) {
                    //listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity().getApplicationContext(), httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("phonec", params[0]);
                hashMap.put("nameec", params[1]);
                hashMap.put("username", params[2]);
                finalResult = httpParse.postRequest(hashMap, HttpURL);
                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();
        userRegisterFunctionClass.execute(friendPhone, friendName, UserPhoneNumber);

    }

    @Deprecated
    public void UserRegisterFunctioninvite(final String Phonein, final String Namef, final String Nameofsender) {

        class UserRegisterFunctionClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdt.setText("fetching");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                pdt.setText("done!!!");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("phonein", params[0]);
                hashMap.put("nameofsender", params[1]);
                hashMap.put("nameoff", params[2]);
                finalResult = httpParse.postRequest(hashMap, HttpURLin);
                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();
        userRegisterFunctionClass.execute(Phonein, Nameofsender, Namef);
    }

}