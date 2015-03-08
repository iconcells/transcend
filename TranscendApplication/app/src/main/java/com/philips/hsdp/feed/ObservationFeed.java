package com.philips.hsdp.feed;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.philips.hsdp.model.HSDPObservation;

import org.hl7.fhir.instance.formats.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
 * ObservationFeed.java
 * Extends from FeedManager. Keeps observation records
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public class ObservationFeed extends FeedManager
{
    private List<HSDPObservation> mObservations;
    static ObservationFeed sObservationFeed;

    private ObservationFeed()
    {
        super();
        mObservations = new ArrayList<HSDPObservation>();
    }

    public static ObservationFeed getNewInstance ()
    {
        return new ObservationFeed();
    }

    public static ObservationFeed getInstance ()
    {
        if (sObservationFeed == null)
            sObservationFeed = new ObservationFeed();
        return sObservationFeed;
    }

    public static String generateObservationUrl (String inPatientId, String inWhatObservations, String inDate1, String inDate2)
    {
        String url = null;
        try
        {
            url = Constants.BASE_URL_OBSERVATION + "?subject._id=" + inPatientId; //Patient/46";

            if (inWhatObservations != null)
                url += "&name=" + URLEncoder.encode(inWhatObservations, "UTF-8"); // http://loinc.org|55284-4,http://loinc.org|8462-4,http://loinc.org|8480-6";

            if (inDate1 != null)
                url += "&date=" + URLEncoder.encode(inDate1, "UTF-8");

            if (inDate2 != null)
                url += "&date=" + URLEncoder.encode(inDate2, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        //Log.i("HS", url);

        return url;
    }

    public List<HSDPObservation> getObservationList()
    {
        return mObservations;
    }

    @Override
    public boolean handleResponse (JsonObject inJsonComplete)
    {
        //Log.i("HS", "--- Received Observation Json Data");
        mObservations.clear();

        JsonParser fhirJsonParser = new JsonParser();
        if (inJsonComplete.has("entry"))
        {
            JsonArray jsonList = inJsonComplete.getAsJsonArray(("entry"));

            for (int i=0; i<jsonList.size(); i++)
            {
                // Data comes with double quotes "SomeTitle", so removing double quotes
                String title = jsonList.get(i).getAsJsonObject().get("title").toString().replaceAll("^\"|\"$", "");
                String id = jsonList.get(i).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");;
                String dateUpdated = jsonList.get(i).getAsJsonObject().get("updated").toString().replaceAll("^\"|\"$", "");

                JsonObject observationJson = jsonList.get(i).getAsJsonObject().getAsJsonObject("content");

                HSDPObservation phpsObservation = (HSDPObservation) getPHPSObjectFromJson(PHPS_OBJECT.Observation, fhirJsonParser, observationJson);
                if (phpsObservation == null)
                    return false;

                phpsObservation.setExtraInfo(id, title, dateUpdated);
                mObservations.add(phpsObservation);
            }
        }
        else
        {
            HSDPObservation phpsObservation = (HSDPObservation) getPHPSObjectFromJson(PHPS_OBJECT.Observation, fhirJsonParser, inJsonComplete);
            if (phpsObservation == null)
                return false;
            mObservations.add(phpsObservation);
        }

        return true;
    }
}
