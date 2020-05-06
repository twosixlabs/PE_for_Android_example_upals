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

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.privatedata.DataRequest;
import android.pal.item.location.LocationItem;
import android.privatedata.MicroPALProviderService;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ZipcodeMicroPAL extends MicroPALProviderService<LocationItem> {
    private static final String TAG = ZipcodeMicroPAL.class.getName();
    private static final String DESCRIPTION = "Return the US zip code for the given zip code, if available";
    private static final int MAX_ADDRESSES = 1;
    private static final String DEFAULT_ZIP_CODE = "unavailable";

    public static final String RESULT_KEY_ZIP = "zip_code";
    public static final String RESULT_KEY_SUCCESS = "success";

    public ZipcodeMicroPAL() {
        super(DataRequest.DataType.LOCATION);
    }

    @Override
    public Bundle onReceive(LocationItem locationItem, Bundle bundle) {

        double lat = locationItem.getLatitude();
        double lon = locationItem.getLongitude();

        String zipCode = DEFAULT_ZIP_CODE;
        boolean success = false;

        try {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = gc.getFromLocation(lat, lon, MAX_ADDRESSES);

            if(addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                String addrZip = addr.getPostalCode();

                if(addrZip != null) {
                    Log.i(TAG, "Got valid zip code " + addrZip);
                    zipCode = addrZip;
                    success = true;
                } else {
                    Log.w(TAG, "Failed to get zip code");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle result = new Bundle();
        result.putString(RESULT_KEY_ZIP, zipCode);
        result.putBoolean(RESULT_KEY_SUCCESS, success);

        return result;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
