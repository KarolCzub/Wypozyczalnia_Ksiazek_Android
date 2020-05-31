package com.example.karol.bookrental;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class JsonTaskPost extends AsyncTask<String, String, String> {

    public String odpowiedz;
    private ProgressDialog pd;
    private Context context;

    public JsonTaskPost(Context context) {
        this.context = context;
    }
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
    }

    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

/*            JSONObject jsonObject=new JSONObject();
            jsonObject.put("lid",lid);*/


            DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.write(params[1].getBytes("UTF-8"));

            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                JSONObject response =  new JSONObject(stringBuffer.toString());
                //   array = new JSONArray(stringBuffer.toString());

            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return "blabla";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pd.isShowing()){
            pd.dismiss();
        }
    }

    private HashMap<String, String> konwertujStringi(String[] param) {
        HashMap <String, String> mapa = new HashMap<>();
        if (param.length > 1) {
            for (int i=1; i<param.length; i+=2) {
                mapa.put(param[i], param[i+1]);
            }
        }
        return mapa;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
