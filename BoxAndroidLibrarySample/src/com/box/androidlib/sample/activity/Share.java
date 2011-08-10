//  Copyright 2011 Box.net.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package com.box.androidlib.sample.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.box.androidlib.Box;
import com.box.androidlib.ResponseListeners.PublicShareListener;
import com.box.androidlib.sample.Constants;
import com.box.androidlib.sample.R;

public class Share extends Activity {

    private Bundle extras;
    private String authToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, null);
        if (authToken == null) {
            Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_SHORT)
                .show();
            finish();
            return;
        }

        setContentView(R.layout.share);
        extras = getIntent().getExtras();

        final String itemName = extras.getString("itemName");
        final String itemType = extras.getString("itemType");
        final long itemId = extras.getLong("itemId");

        final TextView itemNameText = (TextView) findViewById(R.id.itemNameText);
        final Button shareButton = (Button) findViewById(R.id.shareButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        final EditText emailsInput = (EditText) findViewById(R.id.emailsInput);
        final EditText messageInput = (EditText) findViewById(R.id.messageInput);

        itemNameText.setText(itemName);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailsInput.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(),
                        "Enter one or more valid e-mail addresses", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] emails = emailsInput.getText().toString().trim().split(",");
                if (emails.length == 0) {
                    Toast.makeText(getApplicationContext(),
                        "Enter one or more valid e-mail addresses", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Box box = Box.getInstance(Constants.API_KEY);
                box.publicShare(authToken, itemType, itemId, "", messageInput
                    .getText().toString(), emails, new PublicShareListener() {
                    @Override
                    public void onComplete(final String public_name, final String status) {
                        String toast = "";
                        if (status.equals(PublicShareListener.STATUS_SHARE_OK)) {
                            toast = "Shared.";
                        } else if (status.equals(PublicShareListener.STATUS_APPLICATION_RESTRICTED)) {
                            toast = "Check your API key";
                        } else if (status.equals(PublicShareListener.STATUS_WRONG_NODE)) {
                            toast = "Invalid item to share";
                        } else {
                            toast = "There was an error";
                        }
                        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT)
                            .show();
                        finish();
                    }

                    @Override
                    public void onIOException(IOException e) {
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
