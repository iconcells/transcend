package com.philips.hsdp.feed;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.philips.hsdp.model.HSDPPatient;

import org.hl7.fhir.instance.formats.JsonParser;

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
 * PatientFeed.java
 * Extends from FeedManager. Keeps patient records
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public class PatientFeed extends FeedManager
{
    List<HSDPPatient> mPatients;
    static PatientFeed sPatientListFeed;

    private PatientFeed()
    {
        super();
        mPatients = new ArrayList<HSDPPatient>();
    }

    public static PatientFeed getNewInstance()
    {
        return new PatientFeed();
    }

    public static PatientFeed getInstance()
    {
        if (sPatientListFeed == null)
            sPatientListFeed = new PatientFeed();
        return sPatientListFeed;
    }

    public static String generatePatientFeedUrl (String inPatientId)
    {
        String url = Constants.BASE_FHIR_INFO_URL + inPatientId ; //+ "?_format=json";

        return url;
    }

    public List<HSDPPatient> getPatientList()
    {
        return mPatients;
    }

    @Override
    public boolean handleResponse (JsonObject inJsonComplete)
    {
        //Log.i("HS", "--- Received Patient Json Data");
        mPatients.clear();

        JsonParser fhirJsonParser = new JsonParser();
        if (inJsonComplete.has("entry"))
        {
            JsonArray jsonListPatients = inJsonComplete.getAsJsonArray(("entry"));

            for (int i = 0; i < jsonListPatients.size(); i++)
            {
                String title = jsonListPatients.get(i).getAsJsonObject().get("title").toString().replaceAll("^\"|\"$", "");
                String id = jsonListPatients.get(i).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");
                String dateUpdated = jsonListPatients.get(i).getAsJsonObject().get("updated").toString().replaceAll("^\"|\"$", "");
                JsonObject patientJson = jsonListPatients.get(i).getAsJsonObject().getAsJsonObject("content");

                HSDPPatient phpsPatient = (HSDPPatient) getPHPSObjectFromJson(PHPS_OBJECT.Patient, fhirJsonParser, patientJson);
                if (phpsPatient == null)
                    return false;

                phpsPatient.setExtraInfo(id, title, dateUpdated);
                mPatients.add(phpsPatient);
            }
        }
        else
        {
            HSDPPatient phpsPatient = (HSDPPatient) getPHPSObjectFromJson(PHPS_OBJECT.Patient, fhirJsonParser, inJsonComplete);
            if (phpsPatient == null)
                return false;
            mPatients.add(phpsPatient);
        }

        return true;
    }
}
