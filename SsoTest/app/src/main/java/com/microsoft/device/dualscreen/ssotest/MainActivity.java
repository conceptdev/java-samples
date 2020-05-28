package com.microsoft.device.dualscreen.ssotest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    /* Azure AD Variables */
    private IMultipleAccountPublicClientApplication mMultipleAccountApp;
    private List<IAccount> accountList;
    TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logTextView = findViewById(R.id.txt_log);

        // Creates a PublicClientApplication object with res/raw/auth_config_single_account.json
        PublicClientApplication.createMultipleAccountPublicClientApplication(this,
                R.raw.auth_config_multiple_account,
                new IPublicClientApplication.IMultipleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(IMultipleAccountPublicClientApplication application) {
                        mMultipleAccountApp = application;
                        loadAccounts();
                    }

                    @Override
                    public void onError(MsalException exception) {
                        displayError(exception);
//                        removeAccountButton.setEnabled(false);
//                        callGraphApiInteractiveButton.setEnabled(false);
//                        callGraphApiSilentButton.setEnabled(false);
                    }
                });

    }


    /**
     * Load currently signed-in accounts, if there's any.
     */
    private void loadAccounts() {
        if (mMultipleAccountApp == null) {
            return;
        }

        mMultipleAccountApp.getAccounts(new IPublicClientApplication.LoadAccountsCallback() {
            @Override
            public void onTaskCompleted(final List<IAccount> result) {
                // You can use the account data to update your UI or your app database.
                accountList = result;
                //displayText(accountList.get(0).toString());
                //updateUI(accountList);
            }

            @Override
            public void onError(MsalException exception) {
                displayError(exception);
            }
        });
    }


    /**
     * Display the graph response
     */
    private void displayText(@NonNull final String text) {
        logTextView.setText(text);
    }
    /**
     * Display the graph response
     */
    private void displayGraphResult(@NonNull final JSONObject graphResponse) {
        logTextView.setText(graphResponse.toString());
    }

    /**
     * Display the error message
     */
    private void displayError(@NonNull final Exception exception) {
        logTextView.setText(exception.toString());
    }
}
