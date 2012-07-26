package myapp.thukydientu.util;

import android.content.Context;
import android.content.Intent;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;

public class QREncodeUtil {
	public static void textEncode(Context context, String text) {
		Intent intent = new Intent(Intents.Encode.ACTION);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, text);
		intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
		context.startActivity(intent);
	}
}
