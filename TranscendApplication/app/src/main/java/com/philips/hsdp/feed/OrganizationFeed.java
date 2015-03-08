package com.philips.hsdp.feed;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.philips.hsdp.model.HSDPOrganization;

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
 * OrganizationFeed.java
 * Extends from FeedManager. Keeps organization records
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public class OrganizationFeed extends FeedManager
{
    List<HSDPOrganization> mOrganizations;
    static OrganizationFeed sOrganizationFeed;

    private OrganizationFeed ()
    {
        mOrganizations = new ArrayList<HSDPOrganization>();
    }

    public static OrganizationFeed getNewInstance()
    {
        return new OrganizationFeed();
    }

    public static OrganizationFeed getInstance()
    {
        if (sOrganizationFeed == null)
            sOrganizationFeed = new OrganizationFeed();
        return sOrganizationFeed;
    }

    public static String generateOrganizationFeedUrl (String inOrganizationid)
    {
        String url = Constants.BASE_FHIR_INFO_URL + inOrganizationid ; //+ "?_format=json";

        return url;
    }

    public List<HSDPOrganization> getOrganizationList()
    {
        return mOrganizations;
    }

    @Override
    public boolean handleResponse (JsonObject inJsonComplete)
    {
        //Log.i("HS", "--- Received Organization Json Data");
        mOrganizations.clear();

        JsonParser fhirJsonParser = new JsonParser();
        if (inJsonComplete.has("entry"))
        {
            JsonArray jsonListPatients = inJsonComplete.getAsJsonArray(("entry"));

            for (int i = 0; i < jsonListPatients.size(); i++)
            {
                String title = jsonListPatients.get(i).getAsJsonObject().get("title").toString().replaceAll("^\"|\"$", "");
                String id = jsonListPatients.get(i).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");
                String dateUpdated = jsonListPatients.get(i).getAsJsonObject().get("updated").toString().replaceAll("^\"|\"$", "");
                JsonObject organizationJson = jsonListPatients.get(i).getAsJsonObject().getAsJsonObject("content");

                HSDPOrganization phpsOrganization = (HSDPOrganization) getPHPSObjectFromJson(PHPS_OBJECT.Organization, fhirJsonParser, organizationJson);
                if (phpsOrganization == null)
                    return false;

                phpsOrganization.setExtraInfo(id, title, dateUpdated);
                mOrganizations.add(phpsOrganization);
            }
        }
        else
        {
            HSDPOrganization phpsOrganization = (HSDPOrganization) getPHPSObjectFromJson(PHPS_OBJECT.Organization, fhirJsonParser, inJsonComplete);
            if (phpsOrganization == null)
                return false;
            mOrganizations.add(phpsOrganization);
        }

        return true;
    }
}
