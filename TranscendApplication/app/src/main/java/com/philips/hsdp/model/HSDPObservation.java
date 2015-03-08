package com.philips.hsdp.model;

import org.hl7.fhir.instance.model.DateTimeType;
import org.hl7.fhir.instance.model.Observation;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.Quantity;
import org.hl7.fhir.instance.model.Type;

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
 * HSDPObservation.java
 * Extends from HSDPObject, keeps observation info
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public class HSDPObservation implements HSDPObject
{
    Observation mObservation;
    String mUrlLink;
    String mTitle;
    String mHumanReadableDateUpdated;

    public HSDPObservation(Observation inObservation)
    {
        mObservation = null;
        mObservation = inObservation;
    }
    public String getUrlLink()
    {
        return mUrlLink;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getHumanReadableDateUpdated()
    {
        return mHumanReadableDateUpdated;
    }

    public void setExtraInfo (String inUrlLink, String inTitle, String inHumanReadableDateUpdated)
    {
        mUrlLink = inUrlLink;
        mTitle = inTitle;
        mHumanReadableDateUpdated = inHumanReadableDateUpdated;
    }

    public String getObservationName()
    {
        return mObservation.getName().getCoding().get(1) != null
                ? mObservation.getName().getCoding().get(1).getDisplaySimple()
                : mObservation.getName().getCoding().get(0).getDisplaySimple();
    }

    public String getObservationValue()
    {
        if (mObservation.getValue() == null)
            return null;

        String returnVal = ((Quantity) mObservation.getValue()).getValue().getStringValue() + " " + ((Quantity) mObservation.getValue()).getUnitsSimple();

        return returnVal;
    }

    public String getAppliedDateTimeHumanReadableString()
    {
        if (mObservation.getApplies() == null)
            return null;

        Type tp  = mObservation.getApplies();

        if (tp instanceof Period)
        {
            Period period= (Period)tp;
            return period.getStartSimple() + " to " + period.getEndSimple();
        }
        else
           return ((DateTimeType) mObservation.getApplies()).asStringValue();
    }

    public String getStatus()
    {
        return mObservation.getStatusSimple().toCode();
    }

    public String getReliability()
    {
        return mObservation.getReliabilitySimple().toCode();
    }
}
