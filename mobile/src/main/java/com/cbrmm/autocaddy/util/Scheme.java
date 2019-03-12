package com.cbrmm.autocaddy.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;


@Deprecated
public class Scheme {
	
	private String title;
	private Set<String> keys;
	
	@NonNull
	private HashMap<String, Object> settings = new HashMap<>();
	
	/**
	 * Constructs a new Panel from a mapping of settings.
	 *
	 * @param title The title of the new panel.
	 * @param settings A map linking settings by a String key to whatever object is required.
	 *
	 * @throws IllegalArgumentException Thrown when a setting is tied to a null key.
	 */
	public Scheme(@NonNull String title, @NonNull HashMap<String, Object> settings) throws IllegalArgumentException {
		if(settings.keySet().contains(null)) {
			throw new IllegalArgumentException("Settings cannot contain null key");
		}
		
		this.title = title.trim().replaceAll("\\n", "").replaceAll("\\r", "");
		for(String s : settings.keySet()) { //partial deep copy
			this.settings.put(s, settings.get(s));
		}
		this.keys = this.settings.keySet();
	}
	
	/**
	 * Gets the calling panel's title.
	 *
	 * @return The title of the panel.
	 */
	@NonNull
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Get's the calling panel's keys.
	 *
	 * @return A Set containing the keys from this panel's settings map.
	 */
	@NonNull
	public Set<String> getKeys() {
		return this.keys;
	}
	
	/**
	 * Gets an individual setting from the calling panel.
	 *
	 * @param key The string used to access the setting.
	 * @return The object used to hold the setting.
	 */
	@NonNull
	public Object getSetting(String key) {
		if(!this.settings.containsKey(key)) {
			throw new IllegalArgumentException(title + " does not have the key: " + key);
		}
		return this.settings.get(key);
	}
	
	/**
	 * Modifies the settings map of the calling panel.
	 *
	 * @param key The new key to be added.
	 * @param value The new settings object to be added.
	 */
	public void modPanel(@NonNull String key, @Nullable Object value) {
		this.settings.put(key, value);
	}
	
	@NonNull
	@Override
	public String toString() {
		return title + "\n" + settings.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equal;
		if(!(obj instanceof Scheme)) {
			return false;
		}
		
		equal = Objects.equals(this.title, ((Scheme) obj).title);
		equal &= this.keys.size() == ((Scheme) obj).keys.size();
		for(String key : this.keys) {
			equal &= ((Scheme) obj).keys.contains(key);
			equal &= this.settings.get(key).hashCode() == ((Scheme) obj).getSetting(key).hashCode();
		}
		
		return equal;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		
		hash = 11 * hash + title.hashCode();
		for(String key : keys) {
			hash = 11 * hash + settings.get(key).hashCode();
		}
		
		return hash;
	}

}
