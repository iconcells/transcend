package com.philips.hsdp.feed;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Observable;

/**
 * Copyright (c) 2014-2015 Koninklijke Philips N.V.
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * LoginManager.java
 * Responsible for login/logout calls and keeps token information.
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public class LoginManager extends Observable
{
    public interface LoginManagerListener
    {
    }

    public interface LoginListener extends LoginManagerListener
    {
        public void loginManagerReceivedToken (boolean inSuccessful);
        public void loginManagerUserAuthenticated (boolean inSuccessful);
    }

    public interface LogoutListener extends LoginManagerListener
    {
        public void logoutCompleted (boolean inSuccessful);
    }

    private static LoginManager sLoginManager = null;
    private boolean mAuthenticated = false;
    private String mUsername, mPassword;
    private String mAccessToken = null, mPatientUrlId = null, mOrganizationUrlId = null;

    private LoginManager()
    {
    }

    public static LoginManager getInstance()
    {
        if (sLoginManager == null)
        {
            sLoginManager = new LoginManager();
        }
        return sLoginManager;
    }

    public boolean isAuthenticated()
    {
        return mAuthenticated;
    }

    public void authenticate (String inUsername, String inPassword, LoginManagerListener inListener)
    {
        mUsername = inUsername;
        mPassword = inPassword;

        new LogInOutAsyncTask(true, inListener).execute();
        //Log.i("HS", "authenticate");
    }

    public void logout (LoginManagerListener inListener)
    {
        new LogInOutAsyncTask(false, inListener).execute();
    }



    public String getAccessToken()
    {
        return mAccessToken;
    }


    public String getPatientUrlIdString()
    {
        return mPatientUrlId;
    }


    public String getOrganizationUrlIdString()
    {
        return mOrganizationUrlId;
    }


    public String getAuthorizationBearerHttpHeader ()
    {
        return "Bearer " + mAccessToken;
    }


    public String getHttpEntity()
    {
        JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("username", mUsername);
            jsonObject.addProperty("password", mPassword);
        return  jsonObject.toString();
    }


    public String getStringFromHttpResponse (HttpResponse inResponse)
    {
        String responseString = null;
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            inResponse.getEntity().writeTo(out);
            out.close();
            responseString = out.toString();

            //Log.i("HS", responseString);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return responseString;
    }


    class LogInOutAsyncTask extends AsyncTask<Void, Void, Void>
    {
        boolean mOption;
        LoginManagerListener mListner = null;

        public LogInOutAsyncTask (boolean inLogin, LoginManagerListener inListener)
        {
            mOption = inLogin;
            mListner = inListener;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

            HttpClient httpClient = new DefaultHttpClient();

            if (mOption)
                authenticateApp(httpClient, parser);
            else
                logoutApp(httpClient, parser);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            setChanged();
            notifyObservers();
        }

        private void logoutApp (HttpClient httpClient, com.google.gson.JsonParser parser)
        {
            HttpDelete httpPostToken = new HttpDelete(Constants.BASE_URL_LOGOUT);
                httpPostToken.setHeader("Authorization", getAuthorizationBearerHttpHeader());

            boolean logoutCompleted = false;
            try
            {
                HttpResponse responseToken = httpClient.execute(httpPostToken);
                StatusLine statusLine = responseToken.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK)
                    logoutCompleted = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // Logout Pressed so cleaning the vars
            mUsername = null;
            mPassword = null;
            mOrganizationUrlId = null;
            mPatientUrlId = null;
            mAuthenticated = false;
            mAccessToken = null;

            if (mListner != null)
                ((LogoutListener)mListner).logoutCompleted(logoutCompleted);
        }


        private void authenticateApp (HttpClient httpClient, com.google.gson.JsonParser parser)
        {
            String authString = Constants.CLIENT_ID + ":" + Constants.CLIENT_SECRET;
            String base64AuthString = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

            HttpPost httpPostToken = new HttpPost(Constants.BASE_URL_TOKEN);
//                httpPostToken.setHeader("Content-type", "application/json");
                httpPostToken.setHeader("Authorization", base64AuthString);

            boolean accessTokenReceived = false;
            try
            {
                HttpResponse responseToken = httpClient.execute(httpPostToken);
                StatusLine statusLine = responseToken.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK)
                {
                    JsonObject jsonComplete = parser.parse(getStringFromHttpResponse(responseToken)).getAsJsonObject();
                    String accessToken = jsonComplete.get("access_token").getAsString();
                    mAccessToken = accessToken;
//                    int expiresInMs = jsonComplete.get("expiresIn").getAsInt();
//                    String uuid = jsonComplete.get("user").getAsJsonObject().get("uuid").getAsString();

                    accessTokenReceived = true;
                    if (mListner != null)
                        ((LoginListener)mListner).loginManagerReceivedToken(accessTokenReceived);

                    authenticateUser(httpClient, parser);
                }
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (mListner != null)
                ((LoginListener)mListner).loginManagerReceivedToken(accessTokenReceived);
        }


        private void authenticateUser (HttpClient httpClient, com.google.gson.JsonParser parser)
        {
            try
            {
                HttpPost httpPostLogin = new HttpPost(Constants.BASE_URL_LOGIN);
                    httpPostLogin.setHeader("Content-type", "application/json");
                    httpPostLogin.setHeader("Authorization", getAuthorizationBearerHttpHeader());
                    httpPostLogin.setEntity(new StringEntity(getHttpEntity()));

                HttpResponse responseLogin = httpClient.execute(httpPostLogin);
                StatusLine statusLine = responseLogin.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK)
                {
                    JsonObject jsonComplete = parser.parse(getStringFromHttpResponse(responseLogin)).getAsJsonObject();

                    // This is to prevent the application from crashing when the info returned
                    // doesn't contain patient or organization id
                    if (jsonComplete.get("user") != null
                            && jsonComplete.get("user").getAsJsonObject().get("fhir_patient_id") != null
                            && jsonComplete.get("user").getAsJsonObject().get("fhir_organization_id") != null )
                    {
                        mPatientUrlId = jsonComplete.get("user").getAsJsonObject().get("fhir_patient_id").getAsString();
                        mOrganizationUrlId = jsonComplete.get("user").getAsJsonObject().get("fhir_organization_id").getAsString();
                        String pictureUrlString = jsonComplete.get("user").getAsJsonObject().get("picture").getAsString();

                        mAuthenticated = true;
                    }
                    else
                    {
                        mAuthenticated = false;
                    }

                }
                else
                {
                    mAuthenticated = false;
                }
            } catch (IOException e)
            {
                mAuthenticated = false;
                e.printStackTrace();
            }
            if (mListner != null)
                ((LoginListener)mListner).loginManagerUserAuthenticated(mAuthenticated);
        }
    }
}
