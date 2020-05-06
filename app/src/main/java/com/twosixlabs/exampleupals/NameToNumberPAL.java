/*
 * This work was authored by Two Six Labs, LLC and is sponsored by a
 * subcontract agreement with Raytheon BBN Technologies Corp. under Prime
 * Contract No. FA8750-16-C-0006 with the Air Force Research Laboratory (AFRL).

 * The Government has unlimited rights to use, modify, reproduce, release,
 * perform, display, or disclose computer software or computer software
 * documentation marked with this legend. Any reproduction of technical data,
 * computer software, or portions thereof marked with this legend must also
 * reproduce this marking.
 *
 * (C) 2020 Two Six Labs, LLC.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twosixlabs.exampleupals;

import android.os.Bundle;
import android.privatedata.DataRequest;
import android.pal.item.ListItem;
import android.pal.item.communication.ContactItem;
import android.privatedata.MicroPALProviderService;
import android.util.Log;

import java.util.ArrayList;


public class NameToNumberPAL extends MicroPALProviderService<ListItem<ContactItem>> {
    private final String TAG = NameToNumberPAL.class.getSimpleName();
    private final String DESCRIPTION = "Returns the phone number for a specified Contact";

    public static final String NAME_KEY = "name";
    public static final String PHONE_KEY = "phone";

    public NameToNumberPAL() {
        super(DataRequest.DataType.CONTACTS);
    }

    @Override
    public Bundle onReceive(ListItem<ContactItem> messagesList, Bundle bundle) {
        String name = bundle.getString(NAME_KEY);
        Log.w(TAG, "Looking up phone number for the name: " + name);

        if(name != null) {
            name = name.toLowerCase().trim();

            // Loop through the list of contacts looking for one with the right name
            for (ContactItem i : messagesList.getStoredItems()) {
                String contactName = i.getName();
                String originalName = contactName;
                if (contactName != null) {
                    contactName = contactName.toLowerCase().trim();
                }

                // If the name of the contact equals the target name
                if (name.equals(contactName)) {
                    Log.w(TAG, "Found matching contact: " + i);

                    // Get the list of phone numbers for the contact
                    ArrayList<String> phoneNumbers = i.getPhoneNumbers();
                    Log.w(TAG, "Matching contact has phone numbers: " + phoneNumbers);

                    // If the contact has no phone number, error
                    // If the contact has 1 or more number, return the first one
                    if (!phoneNumbers.isEmpty()) {
                        Bundle returnBundle = new Bundle();
                        returnBundle.putString(PHONE_KEY, phoneNumbers.get(0));
                        returnBundle.putString(NAME_KEY, originalName);
                        return returnBundle;
                    } else {
                        Log.w(TAG, "Contact contained ZERO phone numbers");
                        return null;
                    }
                }
            }
        } else {
            Log.w(TAG, "null input name provided");
        }

        Log.w(TAG, "Failed to find the name " + name + " in the list of contacts");
        return null;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
