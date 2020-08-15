package com.lev_agency.socialmanager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.lev_agency.socialmanager.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME = "com.lev_agency.socialmanager.username";

    private ProfileTracker mProfileTracker;
    private TextView mLoginTest;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // try to login to facebook
        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.lev_agency.socialmanager", PackageManager.GET_SIGNATURES
            );
            for(Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);

        CallbackManager callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        // If using Fragment
//        loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                    }
                };
                mProfileTracker.startTracking();
                AccessToken token = loginResult.getAccessToken();
                Profile userFacebookProfile = Profile.getCurrentProfile();

                // set User Information from Facebook Profile
                mUser.setFirstName(userFacebookProfile.getFirstName());
                mUser.setName(userFacebookProfile.getName());

                intent.putExtra(USER_NAME, mUser.getName());
                Toast.makeText(MainActivity.this, "Connected "+ mUser.getName(), Toast.LENGTH_LONG).show();
                mLoginTest = (TextView) findViewById(R.id.loginTest);
                mLoginTest.setText(mUser.getName());
                startActivity(intent);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
}