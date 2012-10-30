package com.example;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyActivity extends Activity implements OnClickListener {

    Button ok,back,exit;
    private TextView result;

    private static final String DEBUG_TAG = "HttpExample";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Login button clicked
        ok = (Button)findViewById(R.id.btn_login);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               postLoginData();
            }
        });

        result = (TextView)findViewById(R.id.lbl_result);
    }

    private void postLoginData() {

        String stringUrl = "http://powerful-river-1698.herokuapp.com/user/checkin";
        Log.d("testing","test me THE URL IS : " + stringUrl);

        try	{
            String rs = "Processing...";

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.d("postLoginData", "NETWORK CONNECTED - URL TO USE: " + stringUrl);
                new DownloadWebpageText().execute(stringUrl);
            } else {
                rs = "No network connection available.";
            }
            result.setText("Checked in, thank you");
        }
        catch (Exception e)	{
            Log.e("luchinator", "exception", e);
            result.setText("err: " + e.getStackTrace());
        }
    }

    @Override
    public void onClick(View view) {
        if(view == ok){
            postLoginData();
        }
    }


    /*another class*/

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageText extends AsyncTask {

        @Override
        protected Object doInBackground(Object... objects) {
            Log.d("background", "should do something here");
            Log.d("background",objects[0].toString());
            try {
                String checkinResponse = downloadUrl(objects[0].toString());
                Log.d("doInBackground","Response from download: " + checkinResponse);

                if(checkinResponse.equals("200")){
                    Log.d("doinBackground","checkedin");
                }
                else {
                    Log.d("doinBackground","no check in");
                }

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return true;
        }
        // onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(String resultResponse) {
            result.setText(resultResponse);
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                Log.d("DownloadUrl", myurl);
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return String.valueOf(response);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }


    }
}



