package com.hellowidget.datecalc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

public class widgetService extends Service {
	//preferences
	public final static String PREFS_NAME = "UserDateInputs";
	private SharedPreferences prefs;
	//User dates
	int userDate = 0;
	int userMonth = 0;
	int userYear = 0;
	int calculatedValue = 0;
	int userDayOfYear = 0;
    int userWeekOfYear = 0;
    int numOfDays = 0;
    int userInput = 0;
    int displayFormat;
	//Current dates
    SimpleDateFormat month = new SimpleDateFormat("MM", Locale.getDefault());
	SimpleDateFormat date = new SimpleDateFormat("d", Locale.getDefault());
	SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.getDefault());
	SimpleDateFormat weekOfYear = new SimpleDateFormat("w", Locale.getDefault());
	SimpleDateFormat dayOfYear = new SimpleDateFormat("D", Locale.getDefault());
	SimpleDateFormat hour = new SimpleDateFormat("hh", Locale.getDefault());
	SimpleDateFormat minute = new SimpleDateFormat("mm", Locale.getDefault());
	SimpleDateFormat second = new SimpleDateFormat("ss", Locale.getDefault());
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("serviceTag", "Widget update service called");
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
		ComponentName widgetComponent = new ComponentName(this, Main.class);
		int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
		String daysText;
		
		//Update display for each active widget
		for(int i=0; i<widgetIds.length; i++) {
			int widgetId = widgetIds[i];
			RemoteViews view = new RemoteViews(getPackageName(), R.layout.main);
			getUserInput(this.getApplicationContext(), widgetId);
			switch(displayFormat) {
				case 1:
					//Case combination of weeks and days
					view.setViewVisibility(R.id.bigCount, View.VISIBLE);
					view.setViewVisibility(R.id.bigLabel, View.VISIBLE);
					view.setViewVisibility(R.id.smallLabel, View.VISIBLE);
					//Set textView for bigCount
					if((numOfDays/7) > 99) {
						if((numOfDays/7) > 999)
							view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 15);
						else
							view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 18);
					}
					else {
						view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 22);
					}
					view.setTextViewText(R.id.bigCount, String.valueOf(numOfDays/7));				
					//Set textView for bigLabel
					view.setTextViewText(R.id.bigLabel, "W");
					//Set textView for smallLabel
					if (numOfDays == 1) { 
						view.setTextViewText(R.id.smallLabel, numOfDays + " day");
					}
					else {
						if(numOfDays > 999) {
							view.setTextViewTextSize(R.id.smallLabel, TypedValue.COMPLEX_UNIT_SP, 10);
						}
						else {
							view.setTextViewTextSize(R.id.smallLabel, TypedValue.COMPLEX_UNIT_SP, 12);
						}
						view.setTextViewText(R.id.smallLabel, numOfDays + " days");
					}
					break;
				case 2:
					//Case days
					view.setViewVisibility(R.id.bigCount, View.VISIBLE);
					view.setViewVisibility(R.id.bigLabel, View.INVISIBLE);
					view.setViewVisibility(R.id.smallLabel, View.VISIBLE);
					//Set textView for bigCount
					if((numOfDays/7) > 999)
						view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 18);
					else
						view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 22);
					view.setTextViewText(R.id.bigCount, String.valueOf(numOfDays));				
					//Set textView for smallLabel
					if (numOfDays == 1) 
						view.setTextViewText(R.id.smallLabel, "day");
					else
						view.setTextViewText(R.id.smallLabel, "days");
					break;
				case 3:
					//Case weeks
					view.setViewVisibility(R.id.bigCount, View.VISIBLE);
					view.setViewVisibility(R.id.bigLabel, View.VISIBLE);
					view.setViewVisibility(R.id.smallLabel, View.VISIBLE);
					//Set textView for bigCount
					if((numOfDays/7) > 99) {
						if((numOfDays/7) > 999)
							view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 15);
						else
							view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 18);
					}
					else {
						view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 22);
					}
					view.setTextViewText(R.id.bigCount, String.valueOf(numOfDays/7));				
					//Set textView for bigLabel
					view.setTextViewText(R.id.bigLabel, "W");
					//Set textView for smallLabel
					switch(numOfDays%7) {
						default:
							daysText = "zero days";
							break;
						case 1:
							daysText = "one day";
							break;
						case 2:
							daysText = "two days";
							break;
						case 3:
							daysText = "three days";
							break;
						case 4:
							daysText = "four days";
							break;
						case 5:
							daysText = "five days";
							break;
						case 6:
							daysText = "six days";
							break;
					}				
					view.setTextViewTextSize(R.id.bigCount, TypedValue.COMPLEX_UNIT_SP, 12);
					view.setTextViewText(R.id.smallLabel, daysText);
					break;
				default:
					//Case error
					view.setViewVisibility(R.id.bigCount, View.INVISIBLE);
					view.setViewVisibility(R.id.bigLabel, View.INVISIBLE);
					view.setViewVisibility(R.id.smallLabel, View.VISIBLE);
					view.setTextViewText(R.id.smallLabel, "Error!");
					break;
			}
			//Push update for widget to home screen
			Log.d("serviceTag", "Pushing update to home screen");
//			ComponentName thisWidget = new ComponentName(this, Main.class);
//			AppWidgetManager manager = AppWidgetManager.getInstance(this);
//			manager.updateAppWidget(thisWidget, view);
			widgetManager.updateAppWidget(widgetId, view);
			
		}		
		
		//Stop service
		Log.d("serviceTag", "Stopping widget update service");
		stopSelf();
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * Function for getting user inputs
	 */
	public void getUserInput(Context context, int widgetID) {
		/** Get user inputs from Shared Preferences **/ 
		prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		userDate = prefs.getInt("inputDate" + widgetID, 01);
		userMonth = prefs.getInt("inputMonth" + widgetID, 01);
		userYear = prefs.getInt("inputYear" + widgetID, 2000);
		userDayOfYear = calculateDays(0, 1, userDate, userMonth, isLeapYear(userYear));
		userWeekOfYear = userDayOfYear/7;
		displayFormat = prefs.getInt("displaySettings" + widgetID, 1);
		getNumOfDays(context);
	}
	
	/* 
	 * Function used to get the value for Number of Days
	 */
	public void getNumOfDays(Context context) {
		Log.d("serviceTag", "Getting number of days");
		if( userYear == Integer.valueOf(year.format(new Date())) ) {
			numOfDays = calculateDays(userDate, userMonth, Integer.valueOf(date.format(new Date())), Integer.valueOf(month.format(new Date())), isLeapYear(userYear));
		}
		else {
			for(int i = userYear; i < Integer.valueOf(year.format(new Date())); i++) {
	            if(i == userYear) {
	            	numOfDays += calculateDays(userDate, userMonth, 31, 12, isLeapYear(i));
	            }
	            else {
	            	if(isLeapYear(i) == true) {
	            		numOfDays += 366;
	            	}
	            	else {
	            		numOfDays += 365;
	            	}
	            }
	        }
			numOfDays += calculateDays(0, 1, Integer.valueOf(date.format(new Date())), Integer.valueOf(month.format(new Date())), isLeapYear(Integer.valueOf(year.format(new Date()))));			
		}
		
	}
	
	/*
	 * Function for determining if year is a leap year
	 */
	private boolean isLeapYear(int targetYear) {
		if((targetYear%4) == 0) {
			if((targetYear%100) == 0) {
				if((targetYear%400) == 0) {
					return true;
				}
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Function for calculating the difference between two dates within the same year
	 */ 
	private int calculateDays(int refDay, int  refMonth, int endDay, int endMonth, boolean leapYear) {
	    int refDOY = refDay;
	    int endDOY = endDay;
	    //Calculate the Day of the Year of the Reference Day
	    for(int i=0; i<refMonth-1; i++) {
	        switch(i) {
	            case 0:
	                refDOY += 31;
	                break;
	            case 1:
	            	if(leapYear) {
	            		refDOY += 29;
	            	}
	            	else {
	            		refDOY += 28;
	            	}
	                break;
	            case 2:
	                refDOY += 31;
	                break;
	            case 3:
	                refDOY += 30;
	                break;
	            case 4:
	                refDOY += 31;
	                break;
	            case 5:
	                refDOY += 30;
	                break;
	            case 6:
	                refDOY += 31;
	                break;
	            case 7:
	                refDOY += 31;
	                break;
	            case 8:
	                refDOY += 30;
	                break;
	            case 9:
	                refDOY += 31;
	                break;
	            case 10:
	                refDOY += 30;
	                break;
	            case 11:
	                refDOY += 31;
	                break;
	        }
	    }
	    //Calculate the Day of the Year of the End Date
	    for(int i=0; i<endMonth-1; i++) {
	        switch(i) {
	            case 0:
	                endDOY += 31;
	                break;
	            case 1:
	            	if(leapYear) {
	            		endDOY += 29;
	            	}
	            	else {
	            		endDOY += 28;
	            	}
	                break;
	            case 2:
	                endDOY += 31;
	                break;
	            case 3:
	                endDOY += 30;
	                break;
	            case 4:
	                endDOY += 31;
	                break;
	            case 5:
	                endDOY += 30;
	                break;
	            case 6:
	                endDOY += 31;
	                break;
	            case 7:
	                endDOY += 31;
	                break;
	            case 8:
	                endDOY += 30;
	                break;
	            case 9:
	                endDOY += 31;
	                break;
	            case 10:
	                endDOY += 30;
	                break;
	            case 11:
	                endDOY += 31;
	                break;
	        }
	    }
	    return endDOY - refDOY;
	}

}
