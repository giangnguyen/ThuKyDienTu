package myapp.thukydientu.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class FileUtils {

	public static final String DIRECTORY = "directory";
	
	public static void openFile(Context context, String filePath) {
		Intent viewDoc = new Intent(Intent.ACTION_VIEW);
		
		final File file = new File(filePath);
		final String format = getFileFormat(filePath);
		
		viewDoc.setDataAndType(Uri.fromFile(file), format);

		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> apps = 
		    pm.queryIntentActivities(viewDoc, PackageManager.MATCH_DEFAULT_ONLY);

		if (apps.size() > 0)
		    context.startActivity(viewDoc);

	}
	
	public static String getFileFormat(String fileName) {
		String type = null;
		final int index = fileName.lastIndexOf(".");
		if (index == -1)
			return null;
		
		fileName = fileName.substring(index);
	    String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
	    if (extension != null) {
	        MimeTypeMap mime = MimeTypeMap.getSingleton();
	        type = mime.getMimeTypeFromExtension(extension);
	    }
	    return type;
	}
	
	public static String byteCountToDisplaySize(long sizeBytes) {
		final String BYTES = "Bytes";
		final String KILOBYTES = "KB";
		final String MEGABYTES = "MB";
		final String GIGABYTES = "GB";
		final long KILO = 1024;
		final long MEGA = KILO * 1024;
		final long GIGA = MEGA * 1024;
		DecimalFormat df = null;
		if (sizeBytes < KILO) {
			return sizeBytes + BYTES;
		} else if (sizeBytes < MEGA) {
			df = new DecimalFormat("#.###");
			return df.format((float) (0.5 + (sizeBytes / (double) KILO))) + KILOBYTES;
		} else if (sizeBytes < GIGA) {
			df = new DecimalFormat("#.##");
			return df.format((float) (0.5 + (sizeBytes / (double) MEGA))) + MEGABYTES;
		} else {
			df = new DecimalFormat("#.##");
			return df.format((float) (0.5 + (sizeBytes / (double) GIGA))) + GIGABYTES;
		}
	}
	
}
