package org.intrahealth.mnewborncare.awc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class GuestDonationWebView extends Activity {

	private static final String TAG = "MainActivity";
	WebView webviewPayment;
	SharedPreferences prefs = null;
	Global g;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.guest_donation_webview);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.titlebar);
		// Button btnMenu = (Button) findViewById(R.id.BtnTitleMenu);
		// btnMenu.setVisibility(View.GONE);
		// Button btnCLose = (Button) findViewById(R.id.BtnBack);
		// btnCLose.setVisibility(View.VISIBLE);
		// btnCLose.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent in = new Intent(GuestDonationWebView.this,
		// MainActivity.class);
		// finish();
		// startActivity(in);
		//
		// }
		// });
		//
		g = (Global) getApplication();
		webviewPayment = (WebView) findViewById(R.id.webviewPayment);
		webviewPayment.getSettings().setJavaScriptEnabled(true);
		webviewPayment.getSettings().setDomStorageEnabled(true);
		webviewPayment.getSettings().setLoadWithOverviewMode(true);
		webviewPayment.getSettings().setUseWideViewPort(true);
		// webviewPayment.loadUrl("http://www.google.com");
		/*
		 * webviewPayment .loadUrl(
		 * "128.199.193.113/rakhi/payment/endpoint?order_id=aAbBcC45&amount=10"
		 * );
		 */

		// webviewPayment.loadUrl("http://timesofindia.com/");
		StringBuilder url_s = new StringBuilder();
		// http://merirakhi.com/processor/payment/endpoint?order_id=aAbBcC&amount=10&currency=USD
		// Hero Add new
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String url = prefs.getString("server_url", "192.168.1.34");
		String URL_http1 = "http://", URL_http2 = "/dash/call-service.php";
		url_s.append(URL_http1 + url + URL_http2);
		// url_s.append("http://www.msakhi.org/dash/index.php");

		Log.e(TAG, "call url " + url_s);

		// webviewPayment.loadUrl(url_s.toString());
		// String postData = "username=my_username&password=my_password";
		webviewPayment.postUrl(url_s.toString(),
				EncodingUtils.getBytes(getPostString(), "utf-8"));

		// webviewPayment.loadUrl("http://128.199.193.113/rakhi/payment/endpoint?order_id=aAbBcC45&amount=0.10&currency=USD");

		webviewPayment.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

			}

			@SuppressWarnings("unused")
			public void onReceivedSslError(WebView view) {

				Log.e("Error", "Exception caught!");
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

		});
	}

	@Override
	public void onBackPressed() {
		Intent in = new Intent(GuestDonationWebView.this, Workflow.class);
		finish();
		startActivity(in);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private String getPostString() {

		StringBuilder post = new StringBuilder();
		post.append("user=");
		post.append(g.getGlobaluserid());
		post.append("&");
		post.append("pass=");
		post.append(g.getGlobalpassword());

		StringBuilder checkSumStr = new StringBuilder();
		/*
		 * =sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4
		 * |udf5||||||salt)
		 */
		MessageDigest digest = null;
		String hash;
		try {
			digest = MessageDigest.getInstance("SHA-512");// MessageDigest.getInstance("SHA-256");

			checkSumStr.append(g.getGlobaluserid());
			checkSumStr.append("|");
			checkSumStr.append(g.getGlobalpassword());
			checkSumStr.append("|");

			digest.update(checkSumStr.toString().getBytes());

			hash = bytesToHexString(digest.digest());
			post.append("hash=");
			post.append(hash);
			post.append("&");
			Log.i(TAG, "SHA result is " + hash);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		post.append("service_provider=");
		post.append("payu_paisa");
		return post.toString();
	}

	// private JSONObject getProductInfo() {
	// try {
	// // create payment part object
	// JSONObject productInfo = new JSONObject();
	//
	// JSONObject jsonPaymentPart = new JSONObject();
	// jsonPaymentPart.put("name", "TapFood");
	// jsonPaymentPart.put("description", "Lunchcombo");
	// jsonPaymentPart.put("value", "500");
	// jsonPaymentPart.put("isRequired", "true");
	// jsonPaymentPart.put("settlementEvent", "EmailConfirmation");
	//
	// // create payment part array
	// JSONArray jsonPaymentPartsArr = new JSONArray();
	// jsonPaymentPartsArr.put(jsonPaymentPart);
	//
	// // paymentIdentifiers
	// JSONObject jsonPaymentIdent = new JSONObject();
	// jsonPaymentIdent.put("field", "CompletionDate");
	// jsonPaymentIdent.put("value", "31/10/2012");
	//
	// // create payment part array
	// JSONArray jsonPaymentIdentArr = new JSONArray();
	// jsonPaymentIdentArr.put(jsonPaymentIdent);
	//
	// productInfo.put("paymentParts", jsonPaymentPartsArr);
	// productInfo.put("paymentIdentifiers", jsonPaymentIdentArr);
	//
	// Log.e(TAG, "product Info = " + productInfo.toString());
	// return productInfo;
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	// }

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

}
