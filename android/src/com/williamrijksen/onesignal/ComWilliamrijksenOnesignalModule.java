package com.williamrijksen.onesignal;

import android.content.Context;

import com.onesignal.OneSignal;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;

import java.util.HashMap;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONObject;

@Kroll.module(name="ComWilliamrijksenOnesignal", id="com.williamrijksen.onesignal")
public class ComWilliamrijksenOnesignalModule extends KrollModule
{
	private static final String LCAT = "ComWilliamrijksenOnesignalModule";
	private static final boolean DBG = TiConfig.LOGD;

	public ComWilliamrijksenOnesignalModule()
	{
		super();
		TiApplication appContext = TiApplication.getInstance();
		OneSignal
		.startInit(appContext)
		.setNotificationReceivedHandler(new NotificationReceivedHandler())
		.setNotificationOpenedHandler(new NotificationOpenedHandler())
		.inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
		.init();
	}
	//TODO inFocusDisplaying should be configurable from Titanium App module initialization
	
	//variable to store the received call back function for the getTags method call
	private KrollFunction getTagsCallback = null;
	private KrollFunction idsAvailableCallback = null;

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(LCAT, "inside onAppCreate");
	}

	@Kroll.method
	public void sendTag(Object tag)
	{
		HashMap <String, Object> dict = (HashMap <String, Object>) tag;
		String key = TiConvert.toString(dict, "key");
		String value = TiConvert.toString(dict, "value");
		OneSignal.sendTag(key, value);
	}

	@Kroll.method
	public void deleteTag(Object tag)
	{
		HashMap <String, Object> dict = (HashMap <String, Object>) tag;
		String key = TiConvert.toString(dict, "key");
		OneSignal.deleteTag(key);
	}
	
	@Kroll.method
	public void getTags(KrollFunction handler)
	{
		getTagsCallback = handler;
		OneSignal.getTags(new GetTagsHandler());
	}
	
	@Kroll.method
	public void idsAvailable(KrollFunction handler)
	{
		idsAvailableCallback = handler;
		OneSignal.idsAvailable(new IdsAvailableHandler());
	}
	
	@Kroll.method
	public void setSubscription(Boolean subscription)
	{
		OneSignal.setSubscription(subscription);
	}
	
	private class IdsAvailableHandler implements OneSignal.IdsAvailableHandler {
		@Override
		public void idsAvailable(String userId, String registrationId) {
			HashMap dict = new HashMap();
			if(userId != null){
				dict.put("userId", userId);
			}
			if (registrationId != null){
			  dict.put("registrationId", registrationId);
			}
			idsAvailableCallback.call(getKrollObject(), dict);
		}
	}
	
	private class GetTagsHandler implements OneSignal.GetTagsHandler {
		@Override
		public void tagsAvailable(JSONObject tags) {
			HashMap dict = new HashMap();
			try {
				dict.put("success", true);
				dict.put("error", false);
				dict.put("results", tags.toString());
			} catch (Exception e) {
				dict.put("success", false);
				dict.put("error", true);
				e.printStackTrace();
				Log.d("error:", e.toString());
			}

			getTagsCallback.call(getKrollObject(), dict);
		}
	}

	private class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
		// This fires when a notification is opened by tapping on it.
		@Override
		public void notificationOpened(OSNotificationOpenResult result) {
			String title = result.notification.payload.title;
			String body = result.notification.payload.body;
			JSONObject additionalData = result.notification.payload.additionalData;

			HashMap kd = new HashMap();
			if(title != null){
				kd.put("title", title);
			}

			if(body != null){
				kd.put("body", body);
			}

			if(additionalData != null){
				String payload = additionalData.toString();
				kd.put("additionalData", payload);
			}
			fireEvent("notificationOpened", kd);
		}
	}

	private class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
		@Override
		public void notificationReceived(OSNotification notification) {
			JSONObject additionalData = notification.payload.additionalData;
			if(additionalData != null){
				String payload = additionalData.toString();
				HashMap kd = new HashMap();
				kd.put("additionalData", payload);
				fireEvent("notificationReceived", kd);
			}else{
				Log.d(LCAT, "No additionalData on notification payload =/");
			}
		}
	}
}