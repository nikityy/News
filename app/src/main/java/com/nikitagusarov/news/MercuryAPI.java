package com.nikitagusarov.news;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mac on 18/12/2016.
 */
public class MercuryAPI {

    private static final String MERCURY_API = "https://mercury.postlight.com/parser?url=%s";
    private static final String API_KEY = "eA4FzvYeiPLZ2L61Gso7j9v1wKppFNAO8avmtrqs";

    public static JSONObject getJSON(String pageUrl) {
        try {
            URL url = new URL(String.format(MERCURY_API, pageUrl));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("x-api-key", API_KEY);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);

            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;

        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
