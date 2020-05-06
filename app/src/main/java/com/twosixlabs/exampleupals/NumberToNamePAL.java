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
import android.pal.item.ListItem;
import android.pal.item.communication.ContactItem;
import android.privatedata.DataRequest;
import android.privatedata.MicroPALProviderService;
import android.util.Log;

import java.util.ArrayList;

public class NumberToNamePAL extends MicroPALProviderService<ListItem<ContactItem>> {
    private static final String TAG = NumberToNamePAL.class.getSimpleName();
    private static final String DESCRIPTION = "Get the matching name from the contact list given a known phone number";

    public static final String PHONE_KEY = "phone";
    public static final String COUNTRY_KEY = "country";
    public static final String NAME_KEY = "name";

    public NumberToNamePAL() {
        super(DataRequest.DataType.CONTACTS);
    }

    @Override
    public Bundle onReceive(ListItem<ContactItem> contactItemList, Bundle bundle) {
        if(bundle.containsKey(PHONE_KEY)) {
            String inputPhoneNumber = normalizePhoneNumber(bundle.getString(PHONE_KEY));
            String countryCode = bundle.getString(COUNTRY_KEY);
            String fullPhoneNumber = "";
            if (countryCode != null) {
                fullPhoneNumber = normalizePhoneNumber(countryCode) + inputPhoneNumber;
            }
            Log.i(TAG, "Checking for phone numbers " + inputPhoneNumber + ", " + fullPhoneNumber);

            for(ContactItem contact : contactItemList.getStoredItems()) {
                // Contacts could have more than one phone number
                ArrayList<String> phoneNumbers = contact.getPhoneNumbers();
                for(String phoneNumber : phoneNumbers) {
                    String contactPhoneNumber = normalizePhoneNumber(phoneNumber);

                    if(inputPhoneNumber.equals(contactPhoneNumber)) {
                        String name = contact.getName();
                        Log.i(TAG, "Found name " + name + " corresponding to phone number " + inputPhoneNumber);

                        Bundle result = new Bundle();
                        result.putString(PHONE_KEY, inputPhoneNumber);
                        result.putString(NAME_KEY, name);

                        return result;
                    }
                    else if((countryCode != null) && (fullPhoneNumber.equals(contactPhoneNumber))) {
                        String name = contact.getName();
                        Log.i(TAG, "Found name " + name + " corresponding to phone number " + fullPhoneNumber);

                        Bundle result = new Bundle();
                        result.putString(PHONE_KEY, fullPhoneNumber);
                        result.putString(NAME_KEY, name);

                        return result;
                    }
                    else {
                        Log.i(TAG, "Contact phone number " + contactPhoneNumber);
                    }
                }
            }

            Log.w(TAG, "No contact name found for " + inputPhoneNumber);

        } else {
            Log.e(TAG, String.format("Need to provide an input bundle with key '%s' corresponding to the phone number", PHONE_KEY));
        }

        return null;
    }

    /**
     *
     * @param phoneNumber
     * @return The phone number only containing numerics; all other symbols are stripped out
     */
    private String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("\\D+", "");
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
