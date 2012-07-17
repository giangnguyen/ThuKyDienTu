package myapp.thukydientu.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {
	public static boolean isInternetConnected(Context context) {
		boolean connected = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm != null) {
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();

			for (NetworkInfo ni : netInfo) {
				if ((ni.getTypeName().equalsIgnoreCase("WIFI") 
					|| ni.getTypeName().equalsIgnoreCase("MOBILE")
					) && ni.isConnected() 
					&& ni.isAvailable()) {
					connected = true;
				}

			}
		}

		return connected;
	}
}
