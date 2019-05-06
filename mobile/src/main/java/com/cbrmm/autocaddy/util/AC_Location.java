package com.cbrmm.autocaddy.util;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;


public class AC_Location {
	
	private final static String TAG = "AC_Location";
	private final int MAX_PAST_LOCATIONS = 5;
	
	public final static int ONE_MINUTE = 1000 * 60;
	
	public final static int PERMISSIONS_OK = 1;
	public final static int DEFAULT_TIME = 500;
	public final static int DEFAULT_DIST = 1/3; // time is ms, dist is meters
	
	public final static String MESSAGE_GPS_ON_SIGNAL = "GON";
	public final static String MESSAGE_NET_ON_SIGNAL = "NON";
	public final static String MESSAGE_GPS_OFF_SIGNAL = "GOFF";
	public final static String MESSAGE_NET_OFF_SIGNAL = "NOFF";
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location currLocation = null;
	
	private ArrayList<Location> pastBestNetLocations = new ArrayList<>();;
	private ArrayList<Location> pastBestGPSLocations = new ArrayList<>();;
	
	private Location calcAvgNetLocation;
	private Location calcAvgGPSLocation;
	
	private String locationProviderNet, locationProviderGPS;
	private float updateMinDistance = -1;
	private int updateMinTime = -1;
	
	private long timeDelta;
	private boolean isPermission;
	
	public AC_Location(LocationManager manager, LocationListener listener, boolean permission) {
		this.locationManager = manager;
		this.locationListener = listener;
		
		this.isPermission = permission;
		this.locationProviderNet = LocationManager.NETWORK_PROVIDER;
		this.locationProviderGPS = LocationManager.GPS_PROVIDER;
		
		this.calcAvgNetLocation = new Location(locationProviderNet);
		this.calcAvgGPSLocation = new Location(locationProviderGPS);
		
		enable(true);
	}
	
	private void listenForLocationUpdates(boolean enable) {
		if(enable && isPermission) {
			if(updateMinTime < 0) updateMinTime = DEFAULT_TIME;
			if(updateMinDistance < 0) updateMinDistance = DEFAULT_DIST;
			
			enableNetUpdates();
			enableGPSUpdates();
		} else if(!isPermission) {
			Log.w(TAG, "Permissions for location updates not given.");
		} else {
			removeLocationUpdates();
		}
	}
	
	public Location updateLocation(Location newLocation) {
		try {
			if(isBetterLocation(newLocation, currLocation)) {
				currLocation = newLocation;
				
				if(Objects.equals(newLocation.getProvider(), locationProviderNet)) {
					if(pastBestNetLocations.size() == MAX_PAST_LOCATIONS) {
						pastBestNetLocations.remove(0);
					}
					pastBestNetLocations.add(newLocation);
				} else if(Objects.equals(newLocation.getProvider(), locationProviderGPS)) {
					if(pastBestGPSLocations.size() == MAX_PAST_LOCATIONS) {
						pastBestGPSLocations.remove(0);
					}
					pastBestGPSLocations.add(newLocation);
				}
				calcAvgLocation();
			}
		} catch(SecurityException e) {
			Log.e(TAG, "Failed to update location.", e);
		}
		
		return currLocation;
	}
	
	/**
	 * Determines whether one Location reading is better than the current Location fix
	 *
	 * @param location1 The new Location that you want to evaluate
	 * @param location2 The current Location fix, to which you want to compare the new one
	 */
	private boolean isBetterLocation(Location location1, Location location2) {
		if(location2 == null) {
			// A new location is always better than no location
			return true;
		}
		
		// Check whether the new location fix is newer or older
		this.timeDelta = location1.getTime() - location2.getTime();
		boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
		boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
		boolean isNewer = timeDelta > 0;
		
		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if(isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if(isSignificantlyOlder) {
			return false;
		}
		
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location1.getAccuracy() - location2.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location1.getProvider(),
				location2.getProvider());
		
		// Determine location quality using a combination of timeliness and accuracy
		return isMoreAccurate
				|| isNewer && !isLessAccurate
				|| isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
	}
	
	/**
	 * Checks whether two providers are the same
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	
	private void calcAvgLocation() {
		double lat = 0, log = 0, acc = 0, delta = 0;
		
		if(pastBestNetLocations.size() > 0) {
			for(Location l : pastBestNetLocations) {
				lat += l.getLatitude();
				log += l.getLongitude();
				acc += l.getAccuracy();
				delta += l.getTime();
			}
			calcAvgNetLocation.setAccuracy((float) (acc / pastBestNetLocations.size()));
			calcAvgNetLocation.setLatitude(lat / pastBestNetLocations.size());
			calcAvgNetLocation.setLongitude(log / pastBestNetLocations.size());
			calcAvgNetLocation.setTime((long) (delta / pastBestNetLocations.size()));
		}
		
		lat = log = acc = delta = 0;
		if(pastBestGPSLocations.size() > 0) {
			for(Location l : pastBestGPSLocations) {
				lat += l.getLatitude();
				log += l.getLongitude();
				acc += l.getAccuracy();
				delta += l.getTime();
			}
			calcAvgGPSLocation.setAccuracy((float) (acc / pastBestGPSLocations.size()));
			calcAvgGPSLocation.setLatitude(lat / pastBestGPSLocations.size());
			calcAvgGPSLocation.setLongitude(log / pastBestGPSLocations.size());
			calcAvgGPSLocation.setTime((long) (delta / pastBestGPSLocations.size()));
		}
	}
	
	private void removeLocationUpdates() {
		locationManager.removeUpdates(locationListener);
	}
	
	public void enable(boolean enableLocation) {
		if(enableLocation && (locationProviderNet != null || locationProviderGPS != null)) {
			enableNetUpdates();
			enableGPSUpdates();
		} else {
			removeLocationUpdates();
			enable(true);
		}
	}
	
	private void enableNetUpdates() {
		if(locationProviderNet != null) {
			try {
				locationManager.requestLocationUpdates(locationProviderNet, updateMinTime, updateMinDistance, locationListener);
			} catch(SecurityException e) {
				Log.w(TAG, "Permission to enable network location updates denied.", e);
			}
		}
	}
	
	private void enableGPSUpdates() {
		if(locationProviderGPS != null) {
			try {
				locationManager.requestLocationUpdates(locationProviderGPS, updateMinTime, updateMinDistance, locationListener);
			} catch(SecurityException e) {
				Log.w(TAG, "Permission to enable GPS location updates denied.", e);
			}
		}
	}
	
	private void updateMinTimeDistUpdateFrequency() {
		removeLocationUpdates();
		listenForLocationUpdates(true);
	}
	
	public void setUpdateMinTime(int minTime) {
		if(minTime != updateMinTime) {
			this.updateMinTime = minTime;
			updateMinTimeDistUpdateFrequency();
		}
	}
	
	public void setUpdateMinDistance(int minDistance) {
		if(minDistance != updateMinDistance) {
			this.updateMinDistance = minDistance;
			updateMinTimeDistUpdateFrequency();
		}
	}
	
	public void setPermission(boolean permission) {
		isPermission = permission;
	}
	
	public void setNetLocations(boolean enable) {
		this.locationProviderNet = enable ? LocationManager.NETWORK_PROVIDER : null;
		enable(true);
	}
	
	public void setGPSLocations(boolean enable) {
		this.locationProviderGPS = enable ? LocationManager.GPS_PROVIDER : null;
		enable(true);
	}
	
	public long getTimeDelta() {
		return timeDelta;
	}
	
	public float getMinDistance() {
		return updateMinDistance;
	}
	
	public Location getCalcAvgLocation() {
		return isBetterLocation(calcAvgGPSLocation, calcAvgNetLocation)
				? calcAvgGPSLocation:calcAvgNetLocation;
	}
	
	public void destroy() {
		listenForLocationUpdates(false);
		locationManager = null;
		locationListener = null;
	}
}
