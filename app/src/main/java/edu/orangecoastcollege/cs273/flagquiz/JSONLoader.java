package edu.orangecoastcollege.cs273.flagquiz;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class loads Country data from a formatted JSON (JavaScript Object Notation) file.
 * Populates data model (Country) with data.
 */

public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */
    public static List<Country> loadJSONFromAsset(Context context) throws IOException {
        List<Country> allCountries = new ArrayList<>();
        String json = null;
            InputStream is = context.getAssets().open("Countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allCountriesJSON = jsonRootObject.getJSONArray("Countries");
            int numberOfEvents = allCountriesJSON.length();

            for (int i = 0; i < numberOfEvents; i++) {
                JSONObject CountryJSON = allCountriesJSON.getJSONObject(i);

                String name = CountryJSON.getString("Name");
                String region = CountryJSON.getString("Region");
                Country country = new Country(name, region);

            allCountries.add(country);
        }
        }
        catch (JSONException e)
        {
            Log.e("Flag Quiz", e.getMessage());
        }

        return allCountries;
    }
}
