package com.newage.letstalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.StrictMode;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Deprecated
public class Contacts2 extends AppCompatActivity {

    String urladdress = "http://usccredits.com/clements/listview/profile.php";
    String[] id;
    String[] name;
    String[] email;
    String[] imagepath;
    ListView listView;
    BufferedInputStream is;
    String line = null;
    String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts2);

        listView = (ListView) findViewById(R.id.lview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {

//                Toast.makeText(MainActivity.this,""+vn ,Toast.LENGTH_SHORT).show();
            }
        });

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        collectData();
        CustomListView customListView = new CustomListView(this, name, email, imagepath);
        listView.setAdapter(customListView);
    }

    public void collectData() {
        try {
            URL url = new URL(urladdress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Content

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
//JSON
        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;
            id = new String[ja.length()];
            name = new String[ja.length()];
            email = new String[ja.length()];
            imagepath = new String[ja.length()];

            for (int i = 0; i <= ja.length(); i++) {
                jo = ja.getJSONObject(i);
                id[i] = jo.getString("id");
                name[i] = jo.getString("name");
                email[i] = jo.getString("email");
                imagepath[i] = jo.getString("photo");
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}