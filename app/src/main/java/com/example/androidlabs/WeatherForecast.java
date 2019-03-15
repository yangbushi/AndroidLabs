package com.example.androidlabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView weatherIconView;
    private TextView curTempView;
    private TextView minTempView;
    private TextView maxTempView;
    private TextView uvView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        ForecastQuery forecastQueryThread = new ForecastQuery();
        forecastQueryThread.execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        weatherIconView = (ImageView) findViewById(R.id.lab7WeatherIcon);
        curTempView = (TextView) findViewById(R.id.lab7CurTemp);
        minTempView = (TextView) findViewById(R.id.lab7MinTemp);
        maxTempView = (TextView) findViewById(R.id.lab7MaxTemp);
        uvView = (TextView) findViewById(R.id.lab7UV);
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String>
    {
        private String windSpeed;
        private String minTemp;
        private String maxTemp;
        private String curTemp;
        private Bitmap weatherIcon;
        private double uv;

        @Override
        protected String doInBackground(String ... params) {
            try {
                String myURL = params[0];
                URL url = new URL(myURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Log.d("response: ", inputStream.toString());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(inputStream, "UTF-8");

                // get temperature, wind speed and weather icon
                while(xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if(xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                        String tagName = xmlPullParser.getName();
                        if(tagName.equals("temperature")) {
                            curTemp = xmlPullParser.getAttributeValue(null, "value");
                            publishProgress(25);

                            minTemp = xmlPullParser.getAttributeValue(null, "min");
                            publishProgress(50);

                            maxTemp = xmlPullParser.getAttributeValue(null, "max");
                            publishProgress(75);
                        } else if(tagName.equals("speed")) {
                            windSpeed = xmlPullParser.getAttributeValue(null, "value");
                        } else if(tagName.equals("weather")) {
                            String iconName = xmlPullParser.getAttributeValue(null, "icon");
                            String fileName = iconName + ".png";

                            if(!fileExistance(fileName)) { // the icon file does not exist, download and save
                                Log.d("weather icon: ", "icon not found, download it");
                                downloadAndSaveIcon(fileName);
                            } else {
                                Log.d("weather icon: ", "icon found");
                            }

                            // read the icon file from local storage
                            FileInputStream fis = null;
                            try {
                                fis = openFileInput(fileName);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            weatherIcon = BitmapFactory.decodeStream(fis);

                            publishProgress(100);
                        }
                    }
                    xmlPullParser.next();
                }

                // get uv rating

                //create the network connection:
                URL UVurl = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");
                HttpURLConnection UVConnection = (HttpURLConnection) UVurl.openConnection();
                inputStream = UVConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                //now a JSON table:
                JSONObject jObject = new JSONObject(result);
                uv = jObject.getDouble("value");

                Thread.sleep(2000);
            }
            catch (Exception e) {
                Log.e("Crash!!", e.getMessage() );
            }

            return "got weather";
        }

        @Override
        protected void onProgressUpdate(Integer ... values) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            weatherIconView.setImageBitmap(weatherIcon);
            curTempView.setText("Current temperature is: " + curTemp);
            minTempView.setText("Minimal temperature is: " + minTemp);
            maxTempView.setText("Maximal temperature is: " + maxTemp);
            uvView.setText("UV rating is: " + uv);

            progressBar.setVisibility(View.INVISIBLE);
        }

        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        public void downloadAndSaveIcon(String iconfile) {

            // dowload icon

            Bitmap image = null;
            URL url = null;

            try {
                url = new URL("http://openweathermap.org/img/w/" + iconfile);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int responseCode = 0;
                responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }




            // save icon to local storage

            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput( iconfile, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
