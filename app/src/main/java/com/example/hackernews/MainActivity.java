package com.example.hackernews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {




    ListView list;
    ArrayList<NumberView> news = new ArrayList<>();
    ArrayList<String> urlcontent = new ArrayList<>();
    SQLiteDatabase articleDB;
    Numberviewadapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        articleDB = this.openOrCreateDatabase("ARTICLES",MODE_PRIVATE,null);
        articleDB.execSQL("CREATE TABLE IF NOT EXISTS Articles (id INTEGER PRIMARY KEY , articleid VARCHAR , title VARCHAR(500), url VARCHAR(3000))");



        DownloadTask task = new DownloadTask();
        task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");


        list = findViewById(R.id.list);
        arrayAdapter = new Numberviewadapter(getApplicationContext(),news);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                intent.putExtra("urlps",urlcontent.get(i));
                startActivity(intent);
            }
        });
        updatelistView();



        }

        public void updatelistView(){
            Cursor c = articleDB.rawQuery("SELECT * FROM ARTICLES",null);
            int titleindex= c.getColumnIndex("title");
            int urlindex = c.getColumnIndex("url");
            if(c.moveToFirst()){
                news.clear();
                urlcontent.clear();
                do{
                    news.add(new NumberView(R.drawable.geeks_logo,c.getString(titleindex)));
                    urlcontent.add(c.getString(urlindex));
                }while(c.moveToNext());
                arrayAdapter.notifyDataSetChanged();


            }
        }


    public class DownloadTask extends AsyncTask<String, Void , String>{
        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url=new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data =inputStreamReader.read();
                while(data!=-1){
                    char current = (char) data;
                    result+=current;
                    data=inputStreamReader.read();
                }
                JSONArray jsonArray = new JSONArray(result);
                int numberofitems = 20;
                if(jsonArray.length()<20){
                    numberofitems= jsonArray.length();
                }
                articleDB.execSQL("DELETE FROM Articles");
                for(int i=0;i<numberofitems;i++){
                    String articleid = jsonArray.getString(i);
                    url=new URL("https://hacker-news.firebaseio.com/v0/item/"+articleid+".json?print=pretty");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    inputStream = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);
                    data =inputStreamReader.read();
                    String articleinfo ="";
                    while(data!=-1){
                        char current = (char) data;
                        articleinfo+=current;
                        data=inputStreamReader.read();
                    }
                    JSONObject jsonObject = new JSONObject(articleinfo);
                    if(!jsonObject.isNull("title") && !jsonObject.isNull("url")){
                        String articletitle = jsonObject.getString("title");
                        String articleUrl   = jsonObject.getString("url");


                    String sql = "INSERT INTO Articles(articleid,title,url) VALUES(?, ?, ?)";
                    SQLiteStatement sqLiteStatement = articleDB.compileStatement(sql);
                    sqLiteStatement.bindString(1,articleid);
                    sqLiteStatement.bindString(2,articletitle);
                    sqLiteStatement.bindString(3,articleUrl);
                    sqLiteStatement.execute();
                    }



                }


                Log.i("INFO",result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            updatelistView();
        }
    }
}