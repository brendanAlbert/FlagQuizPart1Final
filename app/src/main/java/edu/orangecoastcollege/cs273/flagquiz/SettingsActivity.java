package edu.orangecoastcollege.cs273.flagquiz;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * SettingsActivity is a helper Controller.
 *
 * This class contains only one method to allow the settings menu to function.
 *
 * onCreate, gets the support action bar and sets the display home as up method to true.
 *      This allows the user to tap a back arrow when they are done with the settings and wish
 *      to return to the guessing area, MainActivity.  We needed to tell SettingsActivity in the
 *      AndroidManifest.xml file that MainActivity is the parent of SettingsActivity, this is so
 *      it knows where to go back to when the arrow is tapped.
 *
 *      The other method chain being performed is the FragmentManager.  The FragmentManager fills
 *      out the content of the settings menu which is retrieved from android.R.id.content.
 *      In this case, the number of buttons the user wishes to have, or the region they wish to
 *      study.
 *
 *  This class also has an inner class, SettingsActivityFragment.
 *  Because it is an inner class, it is static.  Because it is a Fragment,
 *  it extends PreferenceFragment.
 *
 *  This inner class also has only one method, onCreate.
 *  Inside this onCreate, addPreferencesFromResource is called with the preferences.xml used
 *  to populate the Fragment.
 *
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * onCreate, gets the support action bar and sets the display home as up method to true.
     *      This allows the user to tap a back arrow when they are done with the settings and wish
     *      to return to the guessing area, MainActivity.  We needed to tell SettingsActivity in the
     *      AndroidManifest.xml file that MainActivity is the parent of SettingsActivity, this is so
     *      it knows where to go back to when the arrow is tapped.
     *
     *      The other method chain being performed is the FragmentManager.  The FragmentManager fills
     *      out the content of the settings menu which is retrieved from android.R.id.content.
     *      In this case, the number of buttons the user wishes to have, or the region they wish to
     *      study.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the tool bar to the one in activity_settings
        // Enable home button (not enabled by default)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Use our fragment to fill out the content
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsActivityFragment())
                .commit();
    }

    /**
     *  SettingsActivityFragment is an inner class of SettingsActivity.
     *  Because it is an inner class, it is static.  Because it is a Fragment,
     *  it extends PreferenceFragment.
     *
     *  This inner class also has only one method, onCreate.
     *  Inside this onCreate, addPreferencesFromResource is called with the preferences.xml used
     *  to populate the Fragment.
     */
    public static class SettingsActivityFragment extends PreferenceFragment {

        /**
         *  Inside this onCreate, addPreferencesFromResource is called with the preferences.xml used
         *  to populate the Fragment.
         * @param savedInstanceState
         */
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
