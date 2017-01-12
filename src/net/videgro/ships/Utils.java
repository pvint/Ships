package net.videgro.ships;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.videgro.ships.activities.MainActivity;
import net.videgro.ships.dialogs.ImagePopup;
import net.videgro.ships.dialogs.ImagePopup.ImagePopupListener;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public final class Utils {
	private static final String TAG = "Utils";
	
	private static final SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("[HH:mm:ss] ", Locale.getDefault());
	
	private Utils(){
		// Utility class, no public constructor
	}
	
	public static boolean haveNetworkConnection(Context context) {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI")){
	            if (ni.isConnected()){
	                haveConnectedWifi = true;
	            }
	        }
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE")){
	            if (ni.isConnected()){
	                haveConnectedMobile = true;
	            }
	        }
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}

	public static void loadAd(final View view){
		final Builder builder = new AdRequest.Builder();		
		builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR); // Emulator
		
		// Add test devices
		final String[] testDevices = view.getContext().getString(R.string.testDevices).split(",");		
	    for (String testDevice:testDevices){
	    	builder.addTestDevice(testDevice);
	    	
	    }
	    
	    final AdView adView = (AdView) view.findViewById(R.id.adView);
	    adView.loadAd(builder.build());
	}
	
	public static void sendNotification(final Context context,final String postfix,final String message){
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(context.getText(R.string.app_name)+" "+postfix)
		        .setContentText(message);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		int mId=0;
		mNotificationManager.notify(mId, mBuilder.build());
		
		Analytics.logEvent(context,TAG,"sendNotification","message");
	}	
	
	public static void showPopup(final int id,final Activity activity,final ImagePopupListener listener,final String title,final String message,final int imageResource){
		final String txt="<h1>"+title+"</h1><p style='text-decoration: none'>"+message+"</p>";
		if (activity!=null){
			activity.runOnUiThread(new Runnable() {
		        @Override
		        public void run() {		        	
		        	new ImagePopup(id,activity,listener,txt,imageResource).show();
		        }
			});
		} else {
			Log.e(TAG,"showPopup - activity is null.");
		}
	}
	
	public static void logStatus(final Activity activity,final TextView textView,final String status) {
		final String tag="logStatus - ";
		Log.d(TAG,tag+status);
		if (activity!=null){
			if (SettingsUtils.parseFromPreferencesLoggingVerbose(activity)){
				final String text = LOG_TIME_FORMAT.format(new Date()) + status;
				updateText(activity,textView,text);
			}
		} else {
			Log.e(TAG,tag+"Huh? No activity set. ("+status+")");
		}
	}

	private static void updateText(final Activity activity,final TextView textView,final String text) {
		final String tag="updateText - ";
		if (activity!=null){
			activity.runOnUiThread(new Runnable() {
				public void run() {					
					textView.setText(text+"\n"+textView.getText());
				}
			});
		} else {
			Log.e(TAG,tag+"Huh? No activity set. ("+text+")");
		}
	}
}
