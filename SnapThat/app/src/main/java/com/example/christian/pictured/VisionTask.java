package com.example.christian.pictured;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This class handles the image-tagging service.
 */

public class VisionTask extends AsyncTask<InputStream, String, String>{

    // Setup api with key and endpoint.
    public VisionServiceClient visionServiceClient = new VisionServiceRestClient("364da92137ce4d0d99397ae2b2c5a29b", "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0");
    private PlayActivity parent;

    VisionTask(PlayActivity parent)
    {
        this.parent = parent;
    }

    @Override
    protected String doInBackground(InputStream... params) {
        // Show waiting dialog, and run the analyzer.
        try {
            publishProgress("Processing dat Snap...");
            String[] features = {"Description"};
            String[] details = {};

            AnalysisResult result = visionServiceClient.analyzeImage(params[0],features,details);

            String strResult = new Gson().toJson(result);
            return strResult;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String s) {
        // When returning the tags as a JSON, split them and save them.
        try {
            JSONObject info = new JSONObject(s);

            // Get wanted information.
            JSONArray tagArray = info.getJSONObject("description").getJSONArray("tags");
            String guessString = info.getJSONObject("description").getJSONArray("captions").getJSONObject(0).getString("text");

            // Get amount of tags.
            int amountOfTags = tagArray.length();

            // Convert taglist and description line to ArrayList.
            ArrayList<String> tagList = new ArrayList<String>();
            for(int i = 0; i < amountOfTags; i++) {
                tagList.add(tagArray.get(i).toString());
            }
            try {
                String[] splitArray = guessString.split("\\s+");
                tagList.addAll(Arrays.asList(splitArray));
            } catch (PatternSyntaxException ex) {
                // error
            }
            // Fire visionCheckDone event with the data retrieved from the image tagging service.
            parent.visionCheckDone(guessString , tagList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
          }
}
