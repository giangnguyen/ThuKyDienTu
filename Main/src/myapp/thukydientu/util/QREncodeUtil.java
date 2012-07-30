package myapp.thukydientu.util;

import android.content.Context;
import android.content.Intent;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;

public class QREncodeUtil {
	private static final String USER_ID = "USER_ID";
	private static final String TABLE = "TABLE";
	private static final String DATE_START = "DATE_START";
	private static final String DATE_END = "DATE_END";
	private static final String PARAM_SEP = "/";
	private static final String VALUE_SEP = ":";
	
	private static final int USER_ID_INDEX = 0;
	private static final int TABLE_INDEX = USER_ID_INDEX + 1;
	private static final int DATE_START_INDEX = TABLE_INDEX + 1;
	private static final int DATE_END_INDEX = DATE_START_INDEX + 1;
	
	public static void textEncode(Context context, String text) {
		Intent intent = new Intent(Intents.Encode.ACTION);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, text);
		intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
		context.startActivity(intent);
	}
	
	public static String convertTodo2QRText(int userId, String table, String dateStart,String dateEnd) {
		return USER_ID + VALUE_SEP + userId + PARAM_SEP + TABLE + VALUE_SEP + table + PARAM_SEP + DATE_START + VALUE_SEP + dateStart + PARAM_SEP + DATE_END + VALUE_SEP + dateEnd;
	}
	
	public static int getUserIdFromQRText(String QRText) {
		String[] Params = QRText.split(PARAM_SEP);
		return Integer.parseInt(Params[USER_ID_INDEX].split(VALUE_SEP)[1]);
	}
	
	public static String getTableFromQRText(String QRText) {
		String[] Params = QRText.split(PARAM_SEP);
		return Params[TABLE_INDEX].split(VALUE_SEP)[1];
	}
	
	public static String getDateStartFromQRText(String QRText) {
		String[] Params = QRText.split(PARAM_SEP);
		return Params[DATE_START_INDEX].split(VALUE_SEP)[1];
	}
	
	public static String getDateEndFromQRText(String QRText) {
		String[] Params = QRText.split(PARAM_SEP);
		return Params[DATE_END_INDEX].split(VALUE_SEP)[1];
	}
}
