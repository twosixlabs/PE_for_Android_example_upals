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

public class NumbersOnlyPAL extends MicroPALProviderService<ListItem<ContactItem>> {
    private static final String TAG = NumbersOnlyPAL.class.getSimpleName();
    private static final String DESCRIPTION = "Return a list of all phone numbers";

    public static final String NUMBERS_KEY = "numbers";

    public NumbersOnlyPAL() {
        super(DataRequest.DataType.CONTACTS);
    }

    @Override
    public Bundle onReceive(ListItem<ContactItem> contactItemList, Bundle bundle) {
        ArrayList<String> allNumbers = new ArrayList<>();

        for(ContactItem contact : contactItemList.getStoredItems()) {
            ArrayList<String> numbers = contact.getPhoneNumbers();
            allNumbers.addAll(numbers);
        }

        Bundle result = new Bundle();
        result.putStringArrayList(NUMBERS_KEY, allNumbers);

        Log.i(TAG, String.format("Found %d contacts and %d numbers",
                                  contactItemList.getStoredItems().size(), allNumbers.size()));

        return result;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
