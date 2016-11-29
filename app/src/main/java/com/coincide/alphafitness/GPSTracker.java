package com.coincide.alphafitness;

//import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public final class GPSTracker implements LocationListener {

	private Context mContext;

	private static String TAG = GPSTracker.class.getSimpleName();

	// flag for GPS status
	public boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	public boolean canGetLocation = false;

	Location location; // location
	public static double latitude; // latitude
	public static double longitude; // longitude
	// Geocoder geocoder;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 10 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	/**
	 * Function to get the user's current location
	 * 
	 * @return
	 */
	public Location getLocation() {
		Location location1 = null;
		try {
			/*locationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			location = locationManager.getLastKnownLocation(provider);
			
			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			Log.d("LMS","isGPSEnabled "+isGPSEnabled);
			Log.d("LMS","isNetworkEnabled "+isNetworkEnabled);
			if (isGPSEnabled == false && isNetworkEnabled == false) {
				showSettingsAlert("NETWORK");
				//turnGPSOn();
			} else {
				this.canGetLocation = true;
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
								Log.d("LMS","Lat:Long from GPS is "+latitude+":"+longitude);
							}
						}
					}
				}
				else if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							Log.d("LMS","Lat:Long from Network is "+latitude+":"+longitude);
						}
					}
				}
			}*/
		
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		location1 = locationManager.getLastKnownLocation(provider);
		
		
		// getting GPS status
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		double lat=0.0;
		double lng=0.0;
		if (isGPSEnabled == false && isNetworkEnabled == false) {
			showSettingsAlert("Location Services");
			//turnGPSOn();
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		Log.d("LMS","Location Listener registered");
		if (isNetworkEnabled) {
			this.canGetLocation = true;
//			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
			Log.d("LMS", "Network Enabled");
			if (locationManager != null) {
				location1 = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location1 != null) {
					lat = location1.getLatitude();
					lng = location1.getLongitude();
					if(lat!=0)
						latitude = lat;
					if(lng!=0)
						longitude = lng;
					Log.d("LMS","Lat : Long - "+ String.valueOf(lat)+" : "+ String.valueOf(lng)); // +" add - "+getMyLocationAddress(lat, lng));
//					textView.setText("Lat : Long - "+String.valueOf(lat)+" : "+String.valueOf(lng)+" NETWORK address is - "+getMyLocationAddress(lat, lng));
				}
			}
		}
		// if GPS Enabled get lat/long using GPS Services
		else if (isGPSEnabled) {
			this.canGetLocation = true;
			if (location1 == null) {
//				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				Log.d("LMS", "GPS Enabled");
				if (locationManager != null) {
					location1 = locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location1 != null) {
						lat = location1.getLatitude();
						lng = location1.getLongitude();
						if(lat!=0)
							latitude = lat;
						if(lng!=0)
							longitude = lng;
						Log.d("LMS","Lat : Long - "+ String.valueOf(lat)+" : "+ String.valueOf(lng)); //+" add - "+getMyLocationAddress(lat, lng));
						
					}
//					onLocationChanged(location);
					else{
//						textView.setText("GPS location not available");
						Log.d("LMS", "GPS location not available");
					}
				}
				
			}
		}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		location = location1;
		return location1;
	}

	public void showSettingsAlert(String provider) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		alertDialog.setTitle(provider + " Disabled");

		alertDialog.setMessage(provider
				+ " is not enabled! Turn on "+provider+" ?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
//		if (location != null) {
//			latitude = location.getLatitude();
//		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
//		if (location != null) {
//			longitude = location.getLongitude();
//		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("Location settings");

		// Setting Dialog Message
		alertDialog
		.setMessage("Location settings is not enabled. Turn on Location Services?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("LMS", "***** onLocationChanged with location " + location.toString());
		Log.d("LMS", "***** onLocationChanged with lat:long " + location.getLatitude()+" : "+location.getLongitude());
		if(location.getLatitude()!=0)
			latitude = location.getLatitude();
		if(location.getLongitude()!=0)
			longitude = location.getLongitude();
		Log.d("LMS", "***** onLocationChanged end with lat:long " + latitude+" : "+longitude);
	}

	public String location() {
		String add = null;
		try {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			/*	List<Address> addresses = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 10);*/ // <10>
			/*for (Address address : addresses) {
				// this.locationText.append("\n" + address.getAddressLine(0));
				// Toast.makeText(mContext, "address" +
				// address.getAddressLine(0),
				// 1000).show();
			}*/
			List<Address> address=null;
			try
			{
				address= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			}catch(Exception w)
			{
				
			}
					
			String CityName = "",StateName = "",CountryName = "";
			//if(address.get(0).getAddressLine(0) != null || address.get(0).getAddressLine(1) != null || address.get(0).getAddressLine(2) != null)
			if(address != null){
			if(address.size()!=0)
			{
				if(address.get(0).getAddressLine(0) != null ){
					CityName= address.get(0).getAddressLine(0);
				}
				else
				{
					CityName="An Error Occured ";
				}
				if(address.get(0).getAddressLine(1) != null ){
					StateName = address.get(0).getAddressLine(1);
				}
				else
				{					
					StateName="while ";
				}
				if(address.get(0).getAddressLine(2) != null ){
					CountryName = address.get(0).getAddressLine(2);
				}
				else
				{	
					CountryName="Fetching";
				}
				Log.d("LMS","Address obtained is "+CityName+", "+StateName+", "+CountryName);
			}
			else
			{
				CityName="An Error Occured ";
				StateName="while ";
				CountryName="Fetching";
			}
		}
			// int latitude = (int)(location.getLatitude() * 1000000);
			// int longitude = (int)(location.getLongitude() * 1000000);
			Log.e("Location Address", CityName + StateName + CountryName);
			// Toast.makeText(mContext,
			// "address" + CityName + StateName + CountryName, 1000)
			// .show();
			/*
			 * Double d = location.getLatitude(); Integer l = d.intValue();
			 * Double d1 = location.getLongitude(); Integer l1 = d1.intValue();
			 */
			// GeoPoint point = new GeoPoint(l,l1);
			// mapController.animateTo(point); //<11>
			// ConvertPointToLocation(point) ;
			// Log.e("Address", ConvertPointToLocation(point));
			add = CityName + StateName + CountryName;

		} catch (Exception e) {
			Log.e("LMS", "Could not get Geocoder data", e);
		}
		return add;
	}
//++++++++++++++++++++START++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public String locationBasedOnLatlng(LatLng latLng) {
		String add = null;
		try {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			/*	List<Address> addresses = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 10);*/ // <10>
			/*for (Address address : addresses) {
				// this.locationText.append("\n" + address.getAddressLine(0));
				// Toast.makeText(mContext, "address" +
				// address.getAddressLine(0),
				// 1000).show();
			}*/
			List<Address> address=null;
			try
			{
//				address= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				address= geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
			}catch(Exception w)
			{
				
			}
					
			String CityName = "",StateName = "",CountryName = "";
			//if(address.get(0).getAddressLine(0) != null || address.get(0).getAddressLine(1) != null || address.get(0).getAddressLine(2) != null)
			if(address != null){
			if(address.size()!=0)
			{
				if(address.get(0).getAddressLine(0) != null ){
					CityName= address.get(0).getAddressLine(0);
				}
				else
				{
					CityName="An Error Occured ";
				}
				if(address.get(0).getAddressLine(1) != null ){
					StateName = address.get(0).getAddressLine(1);
				}
				else
				{					
					StateName="while ";
				}
				if(address.get(0).getAddressLine(2) != null ){
					CountryName = address.get(0).getAddressLine(2);
				}
				else
				{	
					CountryName="Fetching";
				}
				Log.d("LMS","Address obtained is "+CityName+", "+StateName+", "+CountryName);
			}
			else
			{
				CityName="An Error Occured ";
				StateName="while ";
				CountryName="Fetching";
			}
		}
			// int latitude = (int)(location.getLatitude() * 1000000);
			// int longitude = (int)(location.getLongitude() * 1000000);
			Log.e("Location Address", CityName + StateName + CountryName);
			// Toast.makeText(mContext,
			// "address" + CityName + StateName + CountryName, 1000)
			// .show();
			/*
			 * Double d = location.getLatitude(); Integer l = d.intValue();
			 * Double d1 = location.getLongitude(); Integer l1 = d1.intValue();
			 */
			// GeoPoint point = new GeoPoint(l,l1);
			// mapController.animateTo(point); //<11>
			// ConvertPointToLocation(point) ;
			// Log.e("Address", ConvertPointToLocation(point));
			add = CityName + StateName + CountryName;

		} catch (Exception e) {
			Log.e("LMS", "Could not get Geocoder data", e);
		}
		return add;
	}
	
	
	
//++++++++++++++++++++END++++++++++++++++++++++++++++++++++++++++++++++++++
	private void turnGPSOn() {
		///Intent intent = new Intent("android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS");//Settings.ACTION_LOCATION_SOURCE_SETTINGS
		//intent.putExtra("enabled", true);
		// startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		// this.mContext.sendBroadcast(intent);

		String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(!provider.contains("gps")){ //if gps is disabled
			final Intent poke = new Intent();
			//  poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.mContext.sendBroadcast(poke);
		}

		/*	String provider = android.provider.Settings.Secure.getString(
				mContext.getContentResolver(),
				android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			mContext.sendBroadcast(poke);
			Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", true);
			this.mContext.sendBroadcast(intent);
			 Intent callGPSSettingIntent = new Intent(
                     android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
             mContextstartActivity(callGPSSettingIntent);

		}*/

	}

	/*
	 * private void turnGPSOff(){ String provider =
	 * Settings.Secure.getString(getContentResolver(),
	 * Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	 * 
	 * if(provider.contains("gps")){ //if gps is enabled final Intent poke = new
	 * Intent(); poke.setClassName("com.android.settings",
	 * "com.android.settings.widget.SettingsAppWidgetProvider");
	 * poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	 * poke.setData(Uri.parse("3")); sendBroadcast(poke);
	 * 
	 * 
	 * } }
	 */

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
