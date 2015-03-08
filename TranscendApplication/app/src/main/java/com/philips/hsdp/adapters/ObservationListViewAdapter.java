package com.philips.hsdp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//import com.philips.hsdp.mobiledhp.R;
import com.philips.hsdp.model.HSDPObservation;

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
 * ObservationListViewAdapter.java
 * Used for list view on PatientInfoActivity
 *
 * Created by Himanshu Shrivastava (hshrivastava@gmail.com)
 */

public class ObservationListViewAdapter extends ArrayAdapter<HSDPObservation>
{
    public class RowViewHolder
    {
        TextView mObservationName, mObseravtionValue, mObservationDate, mObservationStatus, mObservationReliability;
    }

    LayoutInflater mLayoutInflater;
    int mResourceId;

    public ObservationListViewAdapter(Context inContext, int inResource, List<HSDPObservation> objects)
    {
        super(inContext, inResource, objects);
        mResourceId = inResource;
        mLayoutInflater = (LayoutInflater) inContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public View getView(int inPosition, View convertView, ViewGroup inParent)
    {
        RowViewHolder attachedRowViewHolder;
        if (convertView == null)
        {
            convertView = mLayoutInflater.inflate(mResourceId, inParent, false);

            attachedRowViewHolder = new RowViewHolder();
//            attachedRowViewHolder.mObservationName = (TextView) convertView.findViewById(R.id.observationName);
//            attachedRowViewHolder.mObseravtionValue = (TextView) convertView.findViewById(R.id.observationValue);
//            attachedRowViewHolder.mObservationDate = (TextView) convertView.findViewById(R.id.observationDate);
//            attachedRowViewHolder.mObservationStatus = (TextView) convertView.findViewById(R.id.observationStatus);
//            attachedRowViewHolder.mObservationReliability = (TextView) convertView.findViewById(R.id.observationReliability);
            convertView.setTag(attachedRowViewHolder);
        }
        else
        {
            attachedRowViewHolder = (RowViewHolder) convertView.getTag();
        }

        HSDPObservation observation = getItem(inPosition);
        attachedRowViewHolder.mObservationName.setText(observation.getObservationName());
        attachedRowViewHolder.mObseravtionValue.setText(observation.getObservationValue());
//        attachedRowViewHolder.mObservationDate.setText(observation.getHumanReadableDateUpdated());
        attachedRowViewHolder.mObservationDate.setText(observation.getAppliedDateTimeHumanReadableString());
        attachedRowViewHolder.mObservationStatus.setText(observation.getStatus());
        attachedRowViewHolder.mObservationReliability.setText(observation.getReliability());

        return convertView;
    }
}
