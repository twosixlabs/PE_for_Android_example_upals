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
