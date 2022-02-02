package com.sp.bookshare;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email=view.findViewById(R.id.about_email);
        image=view.findViewById(R.id.about_image);

    }

    class fetchData extends Thread {

        String data = "";

        @Override
        public void run(){

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    /*progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();*/

                }
            });

            try {
                Log.d("LOG", "trying json");
                //https://www.npoint.io/docs/cbb709d068check583b916068
                URL url = new URL("https://api.npoint.io/cbb709d068583b916068");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while((line = bufferedReader.readLine()) != null){
                    data = data + line;
                }

                if (!data.isEmpty()){
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray users = jsonObject.getJSONArray("Healthtips");
                    Log.d("LOG", String.valueOf(users));
                    userList.clear();
                    for(int  i =0;i< users.length();i++){
                        JSONObject names = users.getJSONObject(i);
                        JSONObject images = users.getJSONObject(i);
                        String name = names.getString("healthtext");
                        String image = names.getString("healthimage");
                        userList.add(name);
                        imagelist.add(image);
                        Log.d("LOG", String.valueOf(name));

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

                   /* if (progressDialog.isShowing())
                        progressDialog.dismiss();*/

                    customadapter = new Customadapter(getContext(),userList,imagelist);
                    healthview.setAdapter(customadapter);
                    healthview.setLayoutManager(new LinearLayoutManager(getContext()));


                    customadapter.notifyDataSetChanged();
                }
            });
        }
    }
}