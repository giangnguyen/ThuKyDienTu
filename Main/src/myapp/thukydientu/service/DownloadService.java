package myapp.thukydientu.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import myapp.thukydientu.R;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.MyFile;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class DownloadService extends Service {

	private static final int BUFFER_SIZE = 1024;
	private final String ROOT = Environment.getExternalStorageDirectory() + "";
	private final File SAVE_DIR = new File(ROOT + "/ThuKyDienTu/Documents");
	private final String INCOMPLETED = "_incompleted";

	private int fileLength;

	private String mFileName;
	private static String mLastModifed = "";

	private ProgressBar mProgressBar;
	private LocalBroadcastManager mLocalBroadcastManager;
	private DownloadTask mDownloadTask;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public DownloadService(Context context, MyFile file) {
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
		mFileName = file.getFileName();

		createSaveFolder();
		mProgressBar = (ProgressBar) ((Activity) context)
				.findViewById(R.id.status);

		mDownloadTask = new DownloadTask();
		mDownloadTask.execute(file.getLink());
	}

	public void createSaveFolder() {
		try {
			SAVE_DIR.mkdirs();
		} catch (SecurityException e) {
			Log.e(SAVE_DIR + "", "unable to write on the sd card ");
		}
	}

	private long checkIncompleted() {
		File from = new File(SAVE_DIR, mFileName + INCOMPLETED);
		if (from.exists()) {
			Log.d("status", "download is incomplete, filesize:" + from.length());
			return from.length();
		}
		return 0;
	}

	public void cancel() {
	}

	public class DownloadTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... Url) {
			final String mDownloadURL = Url[0].replace(" ", "%20");
			InputStream inputStream = null;
			FileOutputStream fileOutputStream = null;

			try {
				final URL url = new URL(mDownloadURL);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection.setRequestMethod("GET");
				long downloaded = checkIncompleted();
				httpURLConnection.setRequestProperty("Range", "bytes="
						+ downloaded + "-");
				httpURLConnection.setRequestProperty("If-Range", mLastModifed);
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				httpURLConnection.connect();
				mLastModifed = httpURLConnection
						.getHeaderField("Last-Modified");

				inputStream = httpURLConnection.getInputStream();
				fileLength = httpURLConnection.getContentLength();
				fileOutputStream = new FileOutputStream(new File(SAVE_DIR,
						mFileName + INCOMPLETED));

				byte[] buffer = new byte[BUFFER_SIZE]; // 1kb
				int len = 0;

				while ((len = inputStream.read(buffer)) > 0) {
					downloaded += len;
					fileOutputStream.write(buffer, 0, len);
					publishProgress((int) downloaded * 100 / fileLength);
					if (isCancelled())
						break;
				}

				if (downloaded >= fileLength)
					return IConstants.Results.RESULT_OK;

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fileOutputStream != null) {
					try {
						fileOutputStream.flush();
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return IConstants.Results.RESULT_FAIL;
		}

		@Override
		protected void onPreExecute() {
			mLocalBroadcastManager.sendBroadcast(new Intent(
					IConstants.Service.DOWNLOAD_ACTION_STARTED));
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (mProgressBar != null) {
				mProgressBar.setProgress(progress[0]);
			} else {
				Log.w("status", "mProgressBar is null, please supply one!");
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			File from = new File(SAVE_DIR, mFileName + INCOMPLETED);
			File to = new File(SAVE_DIR, mFileName);
			from.renameTo(to);
			Intent intent = new Intent(IConstants.Service.DOWNLOAD_ACTION_FINISHED);
			intent.putExtra(IConstants.Service.DOWNLOADED_FILE_PATH, to.getPath());
			
			mProgressBar.setVisibility(View.GONE);
			mLocalBroadcastManager.sendBroadcast(intent);
			
			stopSelf();
		}

		@Override
		protected void onCancelled() {
			mLocalBroadcastManager.sendBroadcast(new Intent(IConstants.Service.DOWNLOAD_ACTION_CANCELLED));
		}
	}

}
