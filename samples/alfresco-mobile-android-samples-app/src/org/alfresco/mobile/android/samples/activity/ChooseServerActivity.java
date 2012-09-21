package org.alfresco.mobile.android.samples.activity;

import org.alfresco.mobile.android.samples.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Let the user choose between OnPremise Server or Cloud Server.
 * 
 * @author Jean Marie Pascal
 */
public class ChooseServerActivity extends Activity
{

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdkapp_choose_server);
        setTitle(R.string.choose_server_title);
    }

    /**
     * Display OAuth screen
     * 
     * @param v : Button associated to the action.
     */
    public void onCloudServer(View v)
    {
        startActivity(new Intent(this, OAuthActivity.class));
    }

    /**
     * Display OnPremise LoginScreen
     * 
     * @param v : Button associated to the action.
     */
    public void onPremiseServer(View v)
    {
        startActivity(new Intent(this, LoginScreenActivity.class));
    }
}
