package com.resoneuronance.stadic.main;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMUtil implements AndroidConstants {

	private static GoogleCloudMessaging gcm;
	private static String regId;

	public static String registerGCM(Activity context) {
		
		gcm = GoogleCloudMessaging.getInstance(context);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {
			registerInBackground(context);
			Log.d("RegisterActivity","registerGCM - successfully registered with GCM server - regId: "+ regId);
		} else {
			Toast.makeText(context,"RegId already available. RegId: " + regId,Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private static String getRegistrationId(Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		return registrationId;
	}


	private static void registerInBackground(final Activity context) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "+ regId);
					msg = "Device registered, registration ID=" + regId;
					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(context,"Registered with GCM Server." + msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null);
	}

	private static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.commit();
	}
	
}
