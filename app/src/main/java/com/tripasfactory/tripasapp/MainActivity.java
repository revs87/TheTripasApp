package com.tripasfactory.tripasapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.pokegoapi.auth.GoogleAuthTokenJson;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.examples.CatchPokemonAtAreaExample;
import com.pokegoapi.examples.ExampleLoginDetails;
import com.pokegoapi.examples.GoogleUserInteractionExample;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.Log;
import com.pokegoapi.util.Time;
import com.squareup.moshi.Moshi;
import com.tripasfactory.tripasapp.map.TTGoogleMapsFragmentActivity;

import java.io.IOException;
import java.util.Scanner;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends FragmentActivity {

    private String key = "4/9nLXoE0hoZjd0uxbfsPzl3-TS2LbntHW_Tv71F8TxB8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        GoogleUserInteractionExample googleUserInteraction = new GoogleUserInteractionExample();
        LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
        loginAsyncTask.execute();

//        ExampleLoginDetails loginDetails = new ExampleLoginDetails();


        /**
         * GMaps activity
         * */
//        Intent intent = new Intent(this, TTGoogleMapsFragmentActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
    }

    class LoginAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient http = new OkHttpClient();

            try {
                GoogleUserCredentialProvider2 e = new GoogleUserCredentialProvider2(http);
                System.out.println("Please go to " + "https://accounts.google.com/o/oauth2/auth?client_id=848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&response_type=code&scope=openid%20email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
                System.out.println("Enter authorisation code:");
//                            Scanner sc = new Scanner(System.in);
//                            String access = sc.nextLine();
                String access = key;
                e.login(access);
                System.out.println("Refresh token:" + e.getRefreshToken());
                Log.e("GoogleLogin", "Refresh token:" + e.getRefreshToken());
            } catch (RemoteServerException | LoginFailedException var5) {
                var5.printStackTrace();
            }

            return null;
        }
    }


    class GoogleUserCredentialProvider2 extends GoogleUserCredentialProvider {

        public GoogleUserCredentialProvider2(OkHttpClient client, String refreshToken, Time time) throws LoginFailedException, RemoteServerException {
            super(client, refreshToken, time);
        }

        public GoogleUserCredentialProvider2(OkHttpClient client, String refreshToken) throws LoginFailedException, RemoteServerException {
            super(client, refreshToken);
        }

        public GoogleUserCredentialProvider2(OkHttpClient client, Time time) throws LoginFailedException, RemoteServerException {
            super(client, time);
        }

        public GoogleUserCredentialProvider2(OkHttpClient client) throws LoginFailedException, RemoteServerException {
            super(client);
        }

        @Override
        public void login(String authcode) throws LoginFailedException, RemoteServerException {
            HttpUrl url = HttpUrl.parse("https://www.googleapis.com/oauth2/v4/token").newBuilder().addQueryParameter("code", authcode).addQueryParameter("client_id", "848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com").addQueryParameter("client_secret", "NCjF1TLi2CcY6t5mt0ZveuL7").addQueryParameter("grant_type", "authorization_code").addQueryParameter("scope", "openid email https://www.googleapis.com/auth/userinfo.email").addQueryParameter("redirect_uri", "urn:ietf:wg:oauth:2.0:oob").build();
            RequestBody reqBody = RequestBody.create((MediaType)null, new byte[0]);
            Request request = (new okhttp3.Request.Builder()).url(url).method("POST", reqBody).build();
            Response response = null;

            try {
                response = this.client.newCall(request).execute();
            } catch (IOException var10) {
                throw new RemoteServerException("Network Request failed to fetch tokenId", var10);
            }

            Moshi moshi = (new com.squareup.moshi.Moshi.Builder()).build();
            GoogleAuthTokenJson googleAuth = null;

            try {
                googleAuth = (GoogleAuthTokenJson)moshi.adapter(GoogleAuthTokenJson.class).fromJson(response.body().string());
                Log.d("GoogleUserCredentialProvider2", "" + googleAuth.getExpiresIn());
            } catch (IOException var9) {
                throw new RemoteServerException("Failed to unmarshell the Json response to fetch tokenId", var9);
            }

            Log.d("GoogleUserCredentialProvider2", "Got token: " + googleAuth.getAccessToken());
            this.expiresTimestamp = this.time.currentTimeMillis() + ((long)(googleAuth.getExpiresIn() * 1000) - 300000L);
            this.tokenId = googleAuth.getIdToken();
            this.refreshToken = googleAuth.getRefreshToken();
        }
    }
}
