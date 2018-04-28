package me.thesquadmc.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JSONUtils {

	public static JSONObject safeGetObject(JSONObject obj, String key){
		try {
			return obj.getJSONObject(key);
		} catch (JSONException e){
			return null;
		}
	}

	public static JSONObject safeGetObject(JSONArray obj, int key){
		try {
			return obj.getJSONObject(key);
		} catch (JSONException e){
			return null;
		}
	}

	public static JSONArray safeGetArray(JSONObject obj, String key){
		if (obj==null){
			return null;
		}
		try {
			return obj.getJSONArray(key);
		} catch (JSONException e){
			return null;
		}
	}

	public static JSONArray safeGetArray(JSONArray obj, int key) {
		try {
			return obj.getJSONArray(key);
		}
		catch(JSONException e) {
			return null;
		}
	}

	public static String safeGetString(JSONObject obj, String key){
		return safeGetString(obj, key, true);
	}

	public static String safeGetString(JSONObject obj, String key, boolean empty) {
		try {
			return obj.getString(key);
		}
		catch(JSONException e) {
			return empty ? "" : null;
		}
	}

	public static Integer safeGetInt(JSONObject obj, String key){
		return safeGetInt(obj, key, true);
	}

	public static Integer safeGetInt(JSONObject obj, String key, boolean zero){
		try {
			return obj.getInt(key);
		} catch (JSONException e){
			return zero ? 0 : null;
		}
	}

	public static String safeGetString(JSONArray obj, int key) {
		try {
			return obj.getString(key);
		}
		catch(JSONException e) {
			return null;
		}
	}

	public static Boolean safeGetBoolean(JSONObject obj, String key){
		return safeGetBoolean(obj, key, true);
	}

	public static Boolean safeGetBoolean(JSONObject obj, String key, boolean f){
		try {
			return obj.getBoolean(key);
		} catch (JSONException e){
			return f ? false : null;
		}
	}

}
