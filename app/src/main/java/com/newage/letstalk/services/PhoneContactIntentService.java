package com.newage.letstalk.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;


public class PhoneContactIntentService extends IntentService {
    private static final String TAG = PhoneContactIntentService.class.getSimpleName();
    private Context context;
    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String PHONE_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;

    public PhoneContactIntentService() {
        // Used to name the worker thread, important only for debugging.
        super("contact-intent-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        context = getApplicationContext(); // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Contact> contacts = getAll(context);

        /*
        * Continue what you want to do
        */
    }

    public List<Contact> getAll(Context context) {
        List<Contact> contactLists = new ArrayList<>(0);

        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        //Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null , null , null,null); //

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(CONTACT_ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{id}, null);

                    if (pCur != null && pCur.moveToFirst()) {
                        String tempContact = pCur.getString(pCur.getColumnIndex(PHONE_NUMBER));
                        String contactName = pCur.getString(pCur.getColumnIndex(PHONE_NAME));

                        if (tempContact != null) {
                            String contactNumber = tempContact.replaceAll("[ \\-]*", "");
                            if (Patterns.PHONE.matcher(contactNumber).matches()) {
                                contactLists.add(new Contact(contactName, contactNumber));
                            }
                        }

                        pCur.close();
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contactLists;
    }


    public class Contact {
        @Nullable
        private final String name;

        @NonNull
        private final String phoneNumber;

        public Contact(@Nullable String name, @NonNull String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        @Nullable
        public String getName() {
            return name;
        }

        @NonNull
        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}