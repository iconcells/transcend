package com.philips.hsdp.model;

import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.Contact;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.StringType;

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
 * HSDPPatient.java
 * Extends from HSDPObject, keeps patient info
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public class HSDPPatient implements HSDPObject
{
    Patient mPatient;
    String mUrlLink;
    String mTitle;
    String mHumanReadableDateUpdated;

    public HSDPPatient(Patient inPatient)
    {
        mPatient = inPatient;
    }


    public void updatePatientInfo (Patient inPatient)
    {
        mPatient = null;
        mPatient = inPatient;
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

    private String getNameFromIndex (int i)
    {
        StringBuilder name = new StringBuilder("");
        HumanName nm = mPatient.getName().get(i);
        if (nm.getGiven().size() > 0 || nm.getFamily().size() > 0)
        {
            if (nm.getGiven() != null)
               for (StringType given: nm.getGiven())
               {
                   if (given.getValue() != null)
                   {
                      name.append (given.getValue());
                      name.append (" ");
                   }
               }

            if (nm.getFamily() != null)
               for (StringType family: nm.getFamily())
               {
                   if (family.getValue() != null)
                   {
                      name.append (family.getValue());
                      name.append (" ");
                   }
               }
        }
        return name.toString().replaceAll(" +", " ");
    }

    // name
    public String getName()
    {
        String name = getNameFromIndex(0);
        return name;
    }

    // names
    public List<String> getNames()
    {
        List<String> names = new ArrayList<String>();

        for (int i=0; i<mPatient.getName().size(); i++ )
        {
            String name = getNameFromIndex(i);
            names.add(name.toString());
        }

        return names;
    }

    // gender
    public String getGender()
    {
        if (mPatient.getGender().getCoding().get(0).getDisplaySimple() != null)
            return mPatient.getGender().getCoding().get(0).getDisplaySimple();
        else
        {
            if (mPatient.getGender().getCoding().get(0).getCodeSimple().equals("M"))
                return "Male";
            else if (mPatient.getGender().getCoding().get(0).getCodeSimple().equals("F"))
                return "Female";
        }
        return "";
    }

    // gender
    public String getGenderShortForm()
    {
        return mPatient.getGender().getCoding().get(0).getCodeSimple();
    }

    // address
    public List<String> getAddresses()
    {
        List<String> addresses = new ArrayList<String>();

        for (Address address: mPatient.getAddress())
        {
            StringBuilder builder = new StringBuilder("");

            for (StringType st: address.getLine())
            {
                builder.append(st.getValue());
                builder.append(" ");
            }
            builder.append(address.getCitySimple());
            builder.append(" ");
            builder.append(address.getStateSimple());
            builder.append(" ");
            builder.append(address.getZipSimple());
            builder.append(" ");
            builder.append(address.getCountrySimple()==null ? "" : address.getCountrySimple());
            builder.append(" ");
            builder.append("(" + address.getUseSimple() + ")");

            addresses.add(builder.toString().replaceAll(" +", " "));
        }

        return addresses;
    }

    // phone
    public List<String> getPhones()
    {
        List<String> phones = new ArrayList<String>();

        for (Contact contact: mPatient.getTelecom())
        {
            if (contact.getValue() != null)
            {
                StringBuilder builder = new StringBuilder("");

                builder.append(contact.getValueSimple());
                builder.append(" (" + contact.getUseSimple() + ")"); // + contact.getSystemSimple() + " - "

                phones.add(builder.toString().replaceAll(" +", ""));
            }
        }

        return phones;
    }

    // birthdate
    public String getBirthdate()
    {
        DateAndTime dateAndTime = mPatient.getBirthDateSimple();
        return (dateAndTime == null ? "" : dateAndTime.toHumanDisplay());
    }

    // active
    public String getStatus()
    {
        return mPatient.getActiveSimple() ? "Active" : "Not Active";
    }


    // identifier
    // managing organization
    // organization
}
