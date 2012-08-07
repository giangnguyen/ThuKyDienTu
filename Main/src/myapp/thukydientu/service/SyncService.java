package myapp.thukydientu.service;

import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.util.ScheduleUtils;
import myapp.thukydientu.util.TodoUtils;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class SyncService extends Service {
	
	private int userId;
	private Activity mActivity;
	
	private LocalBroadcastManager mLocalBroadcastManager;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public SyncService(Context context, int userId, int dataType){
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
		
		this.userId = userId;
		mActivity = (Activity) context;
		
		new DownloadTask().execute(dataType);
	}
	
	public class DownloadTask extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... dataType) {
			switch(dataType[0]) {
			case IConstants.DataType.SCHEDULE:
				ScheduleUtils.sync(userId, mActivity);
				break;
			case IConstants.DataType.TODO:
				TodoUtils.sync(userId, mActivity);
			}
			return Activity.RESULT_OK;
		}

		@Override
		protected void onPreExecute() {
			mLocalBroadcastManager.sendBroadcast(new Intent(IConstants.Service.SYNC_ACTION_STARTED));
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result == Activity.RESULT_OK) {
				mLocalBroadcastManager.sendBroadcast(new Intent(IConstants.Service.SYNC_ACTION_FINISHED));
				stopSelf();
			}
		}
		
		@Override
        protected void onCancelled(){
            mLocalBroadcastManager.sendBroadcast(new Intent(IConstants.Service.SYNC_ACTION_CANCELLED));
        }
	}
}
