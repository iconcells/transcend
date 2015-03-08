package com.philips.hsdp.feed;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.philips.hsdp.model.HSDPObject;
import com.philips.hsdp.model.HSDPObservation;
import com.philips.hsdp.model.HSDPOrganization;
import com.philips.hsdp.model.HSDPPatient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.model.Observation;
import org.hl7.fhir.instance.model.Organization;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Resource;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
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
 * FeedManager.java
 * Base class is responsible for interacting with the server, has following subclasses
 * - PatientFeed
 * - ObservationFeed
 * - OrganizationFeed
 * It keeps the information related to the feed like pagination links, total records and
 * passes the json to the respective subclass to parse the objects.
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public abstract class FeedManager extends Observable
{
    public static enum FeedStatus {Done, Processing, Failed};
    public static enum PHPS_OBJECT {Patient, Observation, Organization};

    abstract public boolean handleResponse (JsonObject inResponseJsonObject);

    private HashMap<String, String> mReturnedLinks = null;
    private int mTotalRecordsOnServer = 0;
    private int mCurrentPageNo = 1;
    private int mRecordsPerPage = 0;
    private int mTotalNoPages = 1;

    protected Date mLastUpdated;
    protected String mError;
    protected int mErrorCode;
    protected FeedStatus mStatus = FeedStatus.Done;
    private String mFeedUrl;
    private boolean mUrlSet = false;


    protected FeedManager()
    {
    }

    final public Date getLastUpdated ()
    {
        return mLastUpdated;
    }

    final public void setReturnedLinks (JsonArray inReturnedLinks)
    {
        HashMap<String, String> linksMap = new HashMap<String, String>();
        for (int i=0; i<inReturnedLinks.size(); i++)
        {
            if (inReturnedLinks.get(i) != null)
            {
                String key = inReturnedLinks.get(i).getAsJsonObject().get("rel").getAsString();
                String value = inReturnedLinks.get(i).getAsJsonObject().get("href").getAsString();

                linksMap.put(key, value);
            }
        }
        mReturnedLinks = linksMap;
    }

    final public String getNextPageLinkUrl ()
    {
        if (mReturnedLinks != null)
        {
            if (mReturnedLinks.containsKey("next"))
                return mReturnedLinks.get("next");
        }
        return null;
    }

    final public String getPreviousPageLinkUrl ()
    {
        if (mReturnedLinks != null)
        {
            if (mReturnedLinks.containsKey("previous"))
                return mReturnedLinks.get("previous");
        }
        return null;
    }

    final public int getTotalRecordsOnServer ()
    {
        return mTotalRecordsOnServer;
    }

    final public int getCurrentPageNo () { return mCurrentPageNo; }
    final public int getTotalNoPages () { return mTotalNoPages; }


    final public void setFeedUrl (String inUrl)
    {
        mFeedUrl = inUrl;
        mUrlSet = true;
    }

    final public void setFeedUrl (String inUrl, HashMap<String, String> inParameters)
    {
        mFeedUrl = inUrl;
//        // add parameters to the request
//        for (String key: inParameters.keySet())
//        {
//
//        }
        mUrlSet = true;
    }

    final public String getLastError ()
    {
        return mError;
    }


    final public void refresh()
    {
        if (mUrlSet && mStatus != FeedStatus.Processing)
            new GetData().execute();
        else
            Log.i("HS", "Already Processing");
    }


    class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mStatus = FeedStatus.Processing;
            mError = null;
            mErrorCode = 0;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            String responseString = null;
//            if (isOnline)
            {
                boolean succesful = false;

                HttpClient httpclient = new DefaultHttpClient();
                try
                {
                    HttpGet httpGet = new HttpGet(mFeedUrl);
                        httpGet.setHeader("Accept", "application/json");
//                        httpGet.setHeader("Content-type", "application/json");
                        httpGet.setHeader("Authorization", "Bearer " + LoginManager.getInstance().getAccessToken());
                    //Log.i("HS", "FeedUrl " + mFeedUrl);
                    //Log.i("HS", "Bearer " + LoginManager.getInstance().getAccessToken());

//                    httpGet.setEntity(new StringEntity(LoginManager.getInstance().getHttpEntity()));

                    String pageOffsetStr = null;
                    int pageOffset = 0;
                    int indexToStartFrom = mFeedUrl.indexOf("?");
                    if (indexToStartFrom >= 0)
                    {
                        pageOffsetStr = mFeedUrl.substring(indexToStartFrom);
                        String paramsWithValues[] = pageOffsetStr.split("&");
                        for (String paramWithValue: paramsWithValues)
                        {
                            String keyValue[] = paramWithValue.split("=");
                            if (keyValue[0].equals("_getpagesoffset"))
                                pageOffset = Integer.parseInt(keyValue[1]);
                            if (keyValue[0].equals("_count"))
                                mRecordsPerPage = Integer.parseInt(keyValue[1]);
                        }
                    }

                    if (mRecordsPerPage != 0)
                        mCurrentPageNo = (pageOffset / mRecordsPerPage) + 1;
                    else
                        mRecordsPerPage = 10;

                    HttpResponse response = httpclient.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();

                    //Log.i("HS", responseString);

                    if (statusLine.getStatusCode() == HttpStatus.SC_OK)
                    {
                        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                        JsonObject jsonComplete = parser.parse(responseString).getAsJsonObject();

                        if (jsonComplete.has("link"))
                            setReturnedLinks(jsonComplete.get("link").getAsJsonArray());

                        if (jsonComplete.has("totalResults"))
                            mTotalRecordsOnServer = jsonComplete.get("totalResults").getAsInt();

                        //Log.i("HS", "PageNo: " + mCurrentPageNo);

                        succesful = handleResponse(jsonComplete);
                    }
                    else
                    {
//                        response.getEntity().getContent().close();
//                        throw new IOException(statusLine.getReasonPhrase());
                        mError = statusLine.getReasonPhrase();
                        mErrorCode = statusLine.getStatusCode();
                    }

                    mTotalNoPages = (mTotalRecordsOnServer/mRecordsPerPage)
                                    + (mTotalRecordsOnServer%mRecordsPerPage == 0
                                        ? 0
                                        : 1);
                } catch (Exception e)
                {
                    //Log.e("HS", "Error: ");
                    e.printStackTrace();
                }

                if (succesful)
                    mStatus = FeedStatus.Done;
                else
                    mStatus = FeedStatus.Failed;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            setChanged();
            notifyObservers();
        }
    }


    // factory method for generating PHPSObject
    protected HSDPObject getPHPSObjectFromJson (PHPS_OBJECT inWhat, JsonParser fhirJsonParser, JsonObject jsonObject)
    {
        try
        {
            Resource resource = fhirJsonParser.parse(jsonObject);

            switch (inWhat)
            {
                case Patient:
                    Patient patientObj = (Patient) resource;
                    HSDPPatient phpsPatientObject = new HSDPPatient(patientObj);
                    return phpsPatientObject;
                case Observation:
                    Observation observationObj = (Observation) resource;
                    HSDPObservation phpsObservationObject = new HSDPObservation(observationObj);
                    return phpsObservationObject;
                case Organization:
                    Organization organizationObj = (Organization) resource;
                    HSDPOrganization phpsOrganizationObject = new HSDPOrganization(organizationObj);
                    return phpsOrganizationObject;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
