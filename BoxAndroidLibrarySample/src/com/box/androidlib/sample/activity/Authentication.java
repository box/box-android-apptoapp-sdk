package com.box.androidlib.sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.box.androidlib.activities.BoxAuthentication;
import com.box.androidlib.sample.Constants;

/**
 * Demonstrates how to use com.box.androidlib.actitivies.BoxAuthentication
 * activity to authenticate users against Box, and get an auth token.
 *
 * @author developers@box.net
 */
public class Authentication extends Activity {

    private static final int AUTH_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For convenience, the Box Android library includes an activity which
        // you can launch to handle authentication. This saves you from having
        // to implement the login webviews yourself.
        // If you would prefer to create the login webviews yourself, see the
        // source code in com.box.androidlib.activities.BoxAuthentication for
        // guidance, as well as the API docs for authentication:
        // http://developers.box.net/w/page/12923915/ApiAuthentication
        Intent intent = new Intent(this, BoxAuthentication.class);
        intent.putExtra("API_KEY", Constants.API_KEY); // API_KEY is required
        startActivityForResult(intent, AUTH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTH_REQUEST_CODE) {
            if (resultCode == BoxAuthentication.AUTH_RESULT_SUCCESS) {
                // Store auth key in shared preferences.
                // BoxAuthentication activity will set the auth key into the
                // resulting intent extras, keyed as AUTH_TOKEN
                final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.PREFS_KEY_AUTH_TOKEN, data.getStringExtra("AUTH_TOKEN"));
                editor.commit();
                finish();
            } else if (resultCode == BoxAuthentication.AUTH_RESULT_FAIL) {
                Toast.makeText(getApplicationContext(), "Unable to log into Box", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                finish();
            }
        }
    }
}
