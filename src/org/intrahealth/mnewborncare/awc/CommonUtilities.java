package org.intrahealth.mnewborncare.awc;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

	// give your server registration url here
	static final String SERVER_URL = "http://192.168.1.82/TestASHA/register.php";
	static final String TAG = "Insert";
	static String EXTRA_MESSAGE = "message";
	 static void displayMessage(Context context, String message) {
	       EXTRA_MESSAGE=message;
	 }
}