package edu.orangecoastcollege.cs273.flagquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MainActivity is the main Controller for version 2 of our Flag Quiz app.
 *
 * There are constants to represent:
 *  - the number of flags to guess in one play-through of the game
 *  - the identifying keys for the two ListPreferences in preferences.xml
 *
 * There are arrays of:
 *  - Buttons, this is initialized to 8.
 *  - LinearLayouts, this is initialized to 4, but these will change according to user preference.
 *
 *  There are lists of Countries to represent:
 *  - All 230-odd countries from the JSON file
 *  - The 10 countries being used in the user's instance of the game.
 *  - The filtered list of countries depending on the chosen region.
 *
 *  There are member variables to represent:
 *     - the correct Country
 *     - total number of guesses
 *     - number of correct guesses
 *     - a random number generator
 *     - a handler for delaying being each flag when a correct flag is guessed
 *     - how many buttons, or how many flag names the user wishes to display each round, 2,4,6,8
 *     - which region the user wants to study, i.e. Europe or Africa
 *
 *   There are Views :
 *      - TextViews for the current question number and whether a guess was right/wrong
 *      - ImageView to display the flag
 *
 *   The methods of this class include:
 *      - onCreate, this sets the content view, registers the SharedPreferencesChangeListener
 *              which listens for when the user updates the settings.  This method also loads the
 *              countries from the JSON file, initializes the country lists, the random number
 *              generator and the handler.  All the View widgets are wired up and text set,
 *              Buttons and LinearLayouts are connected.  The selection from the settings menu
 *              is captured and the number of buttons and regions are updated via their respective
 *              method calls.  Finally the quiz is reset.
 *
 *      - resetQuiz, resets guess variables, clears the country list from a previous game,
 *              then a while loop is used to add countries to the newly cleared list. Once
 *              ten unique entries from the correct region(s) are added to the list,
 *              the game is started by calling loadNextFlag.
 *
 *      - loadNextFlag, removes a country from the list of 10 countries, the answer text view
 *              is set to a blank string.  The current question out of 10 is displayed.
 *              The AssetManager is used in conjunction with a try/catch to load the next flag
 *              as a Drawable which will be set to the ImageView. A do while loop is used to
 *              shuffle the names and orders of the Country names to be used on the Buttons
 *              for guessing. A for loop is used to enable the buttons visibility and set
 *              their text.  Finally, one of the buttons is selected at random and the name
 *              of the correct country is set.
 *
 *      - makeGuess(View v), takes a View parameter, this represents the button the user tapped on.
 *              The View is downcast to a Button.  The text of the chosen button is retrieved
 *              as well as the text of the correct answer.  The total guesses is incremented.
 *              If the guess is correct, the correct guesses is incremented and the name of the flag
 *              is display in green text.  All the buttons are disabled.  If this is correct guess
 *              10 out of 10, we use an AlertDialog builder to display the user's score.
 *              Using the AlertDialog builder, a PositiveButton is set which enables the user
 *              to retry the quiz.  If there are still more flags left to guess, then loadNextFlag
 *              is called after a 2 second delay by the handler.  If the user makes an incorrect
 *              guess, then the name of the incorrect guess is display in red text and that
 *              country's button is disabled.
 *
 *      - onCreateOptionsMenu, is used to inflate the settings menu.
 *
 *      - onOptionsItemSelected, when the User taps on the settings wheel icon,
 *              this method creates an Intent to take the user from MainActivity
 *              to SettingsActivity.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FLAGS_IN_QUIZ = 10;

    private Button[] mButtons = new Button[8];
    private LinearLayout[] mLayouts = new LinearLayout[4];
    private List<Country> mAllCountriesList; // all the countries loaded from JSON
    private List<Country> mQuizCountriesList; // countries in current quiz
    private List<Country> mFilteredCountriesList; // countries filtered by selected region
    private Country mCorrectCountry; // correct country for the current flag
    private int mTotalGuesses; // number of guesses made
    private int mCorrectGuesses; // number of correct guesses
    private SecureRandom rng; // used to randomize the quiz
    private Handler handler; // used to delay loading next flag

    private TextView questionNumberTextView; // shows current question #
    private ImageView flagImageView; // displays a flag
    private TextView answerTextView; // displays correct answer

    private int mChoices; // stores how many choices (buttons) selected
    private String mRegion; // stores which region is selected

    // Keys used in preferences.xml
    private static final String CHOICES = "pref_numberOfChoices";
    private static final String REGIONS = "pref_regions";

    /**
     * - onCreate, this sets the content view, registers the SharedPreferencesChangeListener
     *              which listens for when the user updates the settings.  This method also loads the
     *              countries from the JSON file, initializes the country lists, the random number
     *              generator and the handler.  All the View widgets are wired up and text set,
     *              Buttons and LinearLayouts are connected.  The selection from the settings menu
     *              is captured and the number of buttons and regions are updated via their respective
     *              method calls.  Finally the quiz is reset.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Let's register the OnSharedPreferencesChangeListener
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);

        try {
            mAllCountriesList = JSONLoader.loadJSONFromAsset(this);
        } catch (IOException e) {
            Log.e(TAG, "Error loading JSON file", e);
        }

        mQuizCountriesList = new ArrayList<>();
        rng = new SecureRandom();
        handler = new Handler();

        // get references to GUI components
        questionNumberTextView =
                (TextView) findViewById(R.id.questionNumberTextView);
        flagImageView = (ImageView) findViewById(R.id.flagImageView);

        mButtons[0] = (Button) findViewById(R.id.button);
        mButtons[1] = (Button) findViewById(R.id.button2);
        mButtons[2] = (Button) findViewById(R.id.button3);
        mButtons[3] = (Button) findViewById(R.id.button4);
        mButtons[4] = (Button) findViewById(R.id.button5);
        mButtons[5] = (Button) findViewById(R.id.button6);
        mButtons[6] = (Button) findViewById(R.id.button7);
        mButtons[7] = (Button) findViewById(R.id.button8);

        mLayouts[0] = (LinearLayout) findViewById(R.id.row1LinearLayout);
        mLayouts[1] = (LinearLayout) findViewById(R.id.row2LinearLayout);
        mLayouts[2] = (LinearLayout) findViewById(R.id.row3LinearLayout);
        mLayouts[3] = (LinearLayout) findViewById(R.id.row4LinearLayout);

        answerTextView = (TextView) findViewById(R.id.answerTextView);

        // set questionNumberTextView's text
        questionNumberTextView.setText(
                getString(R.string.question, 1, FLAGS_IN_QUIZ));

        mRegion = preferences.getString(REGIONS, "All");
        mChoices = Integer.parseInt(preferences.getString(CHOICES, "4"));
        updateChoices();
        updateRegion();

        resetQuiz();
    }

    /**
     * - resetQuiz, resets guess variables, clears the country list from a previous game,
     *              then a while loop is used to add countries to the newly cleared list. Once
     *              ten unique entries from the correct region(s) are added to the list,
     *              the game is started by calling loadNextFlag.
     */
    // set up and start the next quiz
    public void resetQuiz() {

        mCorrectGuesses = 0; // reset the number of correct guesses made
        mTotalGuesses = 0; // reset the total number of guesses the user made
        mQuizCountriesList.clear(); // clear prior list of quiz countries

        int flagCounter = 1;
        int numberOfFlags = mFilteredCountriesList.size();

        // add FLAGS_IN_QUIZ rng file names to the mQuizCountriesList
        while (flagCounter <= FLAGS_IN_QUIZ) {
            int randomIndex = rng.nextInt(numberOfFlags);

            // get the rng file name
            Country randomCountry = mFilteredCountriesList.get(randomIndex);

            // if the rng country hasn't already been added
            if (!mQuizCountriesList.contains(randomCountry)) {
                mQuizCountriesList.add(randomCountry); // add the rng country to the list
                ++flagCounter;
            }
        }

        loadNextFlag(); // start the quiz by loading the first flag
    }

    // after the user guesses a correct flag, load the next flag
    private void loadNextFlag() {
        // Get file name of the next flag and remove it from the list
        mCorrectCountry = mQuizCountriesList.remove(0); // update the correct answer
        answerTextView.setText(""); // clear answerTextView

        // Display current question number
        questionNumberTextView.setText(getString(
                R.string.question, (mCorrectGuesses + 1), FLAGS_IN_QUIZ));

        // Use AssetManager to load next image from assets folder
        AssetManager am = getAssets();

        // COMPLETED: Get an InputStream to the asset representing the next flag
        // COMPLETED: and try to use the InputStream
        try {
            InputStream stream =
                    am.open(mCorrectCountry.getFileName());

            // load the asset as a Drawable and display on the flagImageView
            Drawable flag = Drawable.createFromStream(stream, mCorrectCountry.getName());
            flagImageView.setImageDrawable(flag);
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading " + mCorrectCountry.getFileName(), exception);
        }

        // COMPLETED: Shuffle the order of all the countries (use Collections.shuffle)
        do {
            Collections.shuffle(mFilteredCountriesList);
        } while (mFilteredCountriesList.subList(0, mChoices).contains(mCorrectCountry));

        // COMPLETED: Loop through all 4 buttons, enable them all and set them to the first 4 countries
        // COMPLETED: in the all countries list
        for (int i = 0; i < mChoices; i++)
        {
            mButtons[i].setEnabled(true);
            mButtons[i].setText(mFilteredCountriesList.get(i).getName());
        }
        // COMPLETED: After the loop, randomly replace one of the 4 buttons with the name of the correct country
        mButtons[rng.nextInt(mChoices)].setText(mCorrectCountry.getName());


    }

    /**
     * - makeGuess(View v), takes a View parameter, this represents the button the user tapped on.
     *              The View is downcast to a Button.  The text of the chosen button is retrieved
     *              as well as the text of the correct answer.  The total guesses is incremented.
     *              If the guess is correct, the correct guesses is incremented and the name of the flag
     *              is display in green text.  All the buttons are disabled.  If this is correct guess
     *              10 out of 10, we use an AlertDialog builder to display the user's score.
     *              Using the AlertDialog builder, a PositiveButton is set which enables the user
     *              to retry the quiz.  If there are still more flags left to guess, then loadNextFlag
     *              is called after a 2 second delay by the handler.  If the user makes an incorrect
     *              guess, then the name of the incorrect guess is display in red text and that
     *              country's button is disabled.
     *
     * @param v is a View object, the Button that the user tapped on.
     */
    public void makeGuess(View v) {

        Button guessButton = (Button) v;
        String guess = guessButton.getText().toString();
        String answer = mCorrectCountry.getName();
        ++mTotalGuesses; // increment number of guesses the user has made

        if (guess.equals(answer)) { // if the guess is correct
            ++mCorrectGuesses; // increment the number of correct answers

            // display correct answer in green text
            answerTextView.setText(answer + "!");
            answerTextView.setTextColor(ContextCompat.getColor(this, R.color.correct_answer));


            disableButtons(); // disable all guess Buttons


            // if the user has correctly identified FLAGS_IN_QUIZ flags
            if (mCorrectGuesses == FLAGS_IN_QUIZ) {
                // DialogFragment to display quiz stats and start new quiz
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(
                        getString(R.string.results,
                                mTotalGuesses,
                                (1000 / (double) mTotalGuesses)));
                // "Reset Quiz" Button
                builder.setPositiveButton(R.string.reset_quiz,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                resetQuiz();
                            }
                        }
                );
                builder.setCancelable(false);
                builder.create();
                builder.show();

            }
            else { // answer is correct but quiz is not over
                // load the next flag after a 2-second delay
                handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                loadNextFlag();
                            }
                        }, 2000); // 2000 milliseconds for 2-second delay
            }
        }
        else { // answer was incorrect

            // display "Incorrect!" in red
            answerTextView.setText(R.string.incorrect_answer);
            answerTextView.setTextColor(ContextCompat.getColor(this, R.color.incorrect_answer));
            guessButton.setEnabled(false); // disable incorrect answer
        }


    }

    // utility method that disables all Buttons
    private void disableButtons() {
        for (Button b : mButtons)
            b.setEnabled(false);
    }

    /**
     * - onCreateOptionsMenu, is used to inflate the settings menu.
     * @param menu the menu to inflate.
     * @return
     */
    // Override onCreateOptionsMenu to inflate the settings menu within MainActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * - onOptionsItemSelected, when the User taps on the settings wheel icon,
     *              this method creates an Intent to take the user from MainActivity
     *              to SettingsActivity.
     * @param item
     * @return
     */
    // Responds to the user clicking the Settings menu icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Make a new Intent going to SettingsActivity
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);

        return super.onOptionsItemSelected(item);
    }

    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Let's figure out which key changed
            switch (key)
            {
                case CHOICES:
                    // Read the number of choices from shared preferences
                    mChoices = Integer.parseInt(sharedPreferences.getString(CHOICES, "4"));
                    // Call method to update choices (visually)
                    updateChoices();
                    break;
                case REGIONS:
                    mRegion = sharedPreferences.getString(REGIONS, "All");
                    updateRegion();
                    break;
            }

            resetQuiz();
            // Notify the user that the quiz will restart
            Toast.makeText(MainActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
        }
    };

    private void updateChoices() {
        // Enable/Show all the linear layouts < mChoices / 2
        // Disable/Hide all the others
        // Let's loop through all linear layouts
        for ( int i = 0; i < mLayouts.length ; ++i )
        {
            if (i < mChoices / 2)
            {
                mLayouts[i].setEnabled(true);
                mLayouts[i].setVisibility(View.VISIBLE);
            }
            else
            {
                mLayouts[i].setEnabled(false);
                mLayouts[i].setVisibility(View.GONE);
            }
        }
    }

    private void updateRegion() {
        // Make a decision:
        // If region is "All", filtered list is same as all
        if (mRegion.equals("All"))
            mFilteredCountriesList = new ArrayList<>(mAllCountriesList);
        else
        {
            mFilteredCountriesList = new ArrayList<>();
            // Loop through all countries
            for (Country c: mAllCountriesList)
                if (c.getRegion().equals(mRegion))
                    mFilteredCountriesList.add(c);
        }
    }
}
