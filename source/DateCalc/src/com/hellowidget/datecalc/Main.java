package com.hellowidget.datecalc;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class Main extends AppWidgetProvider {
	//Miscellaneous variables
	public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	private PendingIntent service = null;  
	
	@Override
	public void onDisabled(Context context) {
		Log.d("widgetTag", "Starting onDisabled");
		super.onDisabled(context);
		if (service != null) {
			AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			m.cancel(service);
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d("widgetTag", "Starting onDeleted");
		super.onDeleted(context, appWidgetIds);
		if (service != null) {
			AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			m.cancel(service);
		}
	}	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d("widgetTag", "Starting onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		//Setup alarm
		AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Calendar TIME = Calendar.getInstance();
//				TIME.clear();
		TIME.setTimeInMillis(System.currentTimeMillis());
		TIME.set(Calendar.HOUR_OF_DAY, 0);
		TIME.set(Calendar.MINUTE, 0);
		TIME.set(Calendar.SECOND, 0);
		TIME.set(Calendar.MILLISECOND, 0);
		
		final Intent alarmIntent = new Intent(context, widgetService.class);
//		alarmIntent.putExtra(ACTION_WIDGET_CONFIGURE, appWidgetId);
		alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, getAbortBroadcast());
		if (service == null) {
			Log.d("widgetTag", "Service is equal to null");
			service = PendingIntent.getService(context,  0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			Log.d("widgetTag", "Service is now equal to: " + service);
		}
		m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), AlarmManager.INTERVAL_DAY, service);
		
		//Set up widget(s)
		RemoteViews remoteViews = null;
		Intent configIntent = null;
		for(int i=0; i<appWidgetIds.length; i++) {
			int appWidgetId = appWidgetIds[i];
			
			//Update widget
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
			configIntent = new Intent(context, Configure.class);
			configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			Log.d("widgetTag", "ConfigIntent: " + configIntent.getExtras());
			
			PendingIntent configPendingIntent = PendingIntent.getActivity(context, i, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			Log.d("widgetTag", "PendingIntent: " + configPendingIntent);

			remoteViews.setOnClickPendingIntent(R.id.widget, configPendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);				
		}
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("widgetTag", "Starting onReceive");
		/*final String action = intent.getAction();
		if(AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}*/
		super.onReceive(context, intent);
		
		//Call onUpdate
		Log.d("widgetTag", "onReceive calling onUpdate");
		Bundle extras = intent.getExtras();
		if (extras != null) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), Main.class.getName());
			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
			onUpdate(context, appWidgetManager, appWidgetIds);
		}
		Log.d("widgetTag", "Ending onReceive");
	}
	
}
