/*
 *  AndroidUtil.java
 *  ali Library, android version, java part
 *
 *  Copyright 2010 Acrobits, s.r.o. All rights reserved.
 *
 */


package myapp.thukydientu.util;

import java.lang.reflect.Method;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * The main class of java part Android version of Ali library.
 * 
 * @author Jan Nemec
 *
 */
public class AndroidUtil {
	private static Context context;
	
	
	static Context getContext()
	{
		return context;
	}

	private static native void checkAllNativeClassesImplemented();
	
	public static String getLocation()
	{
		return getLocation(2);
	}

	public static String getLocation(int back)
	{
		try {
			// Use Throwable to obtain stack trace, using Thread.currentThread().getStackTrace is expensive because of thread-safety */
			StackTraceElement e = (new Throwable()).getStackTrace()[back];
			if (e.isNativeMethod()) {
				return " [<native>]";
			}
			return " [" + e.getClassName().split("\\$")[0].replace('.', '/') + ".java:" + e.getLineNumber() + "]";
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (SecurityException e) {
		}
		return " [<unknown>]";
	}

	// Exception-safe version of Class.forName
	public static Class<?> loadClass(String name)
	{
		try {
			return Class.forName(name, false, AndroidUtil.class.getClassLoader());
		} catch (Exception e) {
			log("AndroidUtil", "Failed to load class " + name + ": " + e);
			return null;
		}
	}
	
	// Toast
	public static void toast(String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT);
	}
	
	// Standard logcat facility for Java
	public static void log(String region, String message)
	{
		Log.d("Softphone", region + ": " + message + getLocation(2));
	}
	
	public static void init(Context ctxt)
	{
		context = ctxt;
		checkAllNativeClassesImplemented();
	}
	
	public static String sProgramVersionName = "";
	
	private static Handler sHandler;
	
	static {
		sHandler = new Handler();
	}

	public static String getProp(String prop) {
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method m = c.getMethod("get", String.class);
			return (String)m.invoke(null, prop);
		} catch (Throwable e)
		{
			return "";
		}	
	}
	
	public static String getProgramVersion() {
		return sProgramVersionName;
	}
	
	public static String getResourceString(int resId) {
		return context.getResources().getString(resId);
	}

}
