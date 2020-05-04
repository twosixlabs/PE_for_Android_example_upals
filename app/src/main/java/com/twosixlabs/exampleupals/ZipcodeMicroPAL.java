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
