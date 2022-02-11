package com.sp.bookshare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AboutFragment extends Fragment {

    private TextView email;
    private ImageView image;
    String name;

    Handler mainHandler =  new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LOG", "ONCREATE");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v =  inflater.inflate(R.layout.fragment_about, container, false);
        email=v.findViewById(R.id.about_email1);
        image=v.findViewById(R.id.about_image);
       return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new fetchData().start();

    }

    class fetchData extends Thread {

        String data = "";

        @Override
        public void run(){

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });

            try {
                Log.d("LOG", "trying json");
                //https://www.npoint.io/docs/181b576a0742fd2ad6c7
                URL url = new URL("https://api.npoint.io/181b576a0742fd2ad6c7");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while((line = bufferedReader.readLine()) != null){
                    data = data + line;
                }

                if (!data.isEmpty()){
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray users = jsonObject.getJSONArray("BookShare");
                    Log.d("LOG", String.valueOf(users));
                    for(int  i =0;i< users.length();i++){
                        JSONObject names = users.getJSONObject(i);
                        //JSONObject images = users.getJSONObject(i);
                         name = names.getString("email");
                        //String image = names.getString("image");

                        Log.d("TEST2", name);

                    }
                }
            } catch (MalformedURLException e) {
                Log.d("LOG", "MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("LOG", "IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d("LOG", "JSONException");
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    email.setText(name);

                }
            });
        }
    }
}