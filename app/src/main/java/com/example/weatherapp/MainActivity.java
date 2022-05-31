package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button guessButton;
    EditText cityEditText;
    TextView resulTextView;

    public void guessWeeather(View view){
        try {
            DownloadTask task = new DownloadTask();
            String input = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + input + "&appid=be7cc4d203b4853a2e4a86237fbafa3d");

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(resulTextView.getWindowToken(), 0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char curr = (char) data;
                    result += curr;
                    data = reader.read();
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                   String main = jsonPart.getString("main");
                   String desc = jsonPart.getString("description");

                   if(!main.equals("") && !desc.equals("")) {
                       message += main + " : " + desc;
                   }
                }

                if(!message.equals("")) {
                    resulTextView.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(), "City not found", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "City not found", Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guessButton = (Button) findViewById(R.id.guessButton);
        cityEditText = (EditText) findViewById(R.id.cityEditText);
        resulTextView = (TextView) findViewById(R.id.resultTextView);

   }
}