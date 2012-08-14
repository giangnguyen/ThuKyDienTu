package myapp.thukydientu.service;

import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.util.ScheduleUtils;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SyncService extends IntentService {

	private LocalBroadcastManager mLocalBroadcastManager;
	public SyncService() {
		super("SyncService");
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int userId = intent.getIntExtra(IConstants.User.ID, 12);
		int dataType = intent.getIntExtra(IConstants.DataType.DATA_TYPE, IConstants.DataType.SCHEDULE);
		
		// Start sync 
		intent.setAction(IConstants.Service.SYNC_ACTION_STARTED);
		mLocalBroadcastManager.sendBroadcast(intent);
		Log.d("onHandleIntent", "Start syn");
		
		switch(dataType) {
		case IConstants.DataType.SCHEDULE:
			ScheduleUtils.sync(userId, getBaseContext());
			break;
//		case IConstants.DataType.TODO:
//			TodoUtils.sync(userId, mActivity);
		}
		
		// Sync finished
		intent.setAction(IConstants.Service.SYNC_ACTION_FINISHED);
		mLocalBroadcastManager.sendBroadcast(intent);
	}

//	private int userId;
//	
//	private LocalBroadcastManager mLocalBroadcastManager;
//	
//	@Override
//	public IBinder onBind(Intent arg0) {
//		return null;
//	}
//	
//	public SyncService(Context context, int userId, int dataType){
//		mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
//		
//		this.userId = userId;
//		
//		new DownloadTask().execute(dataType);
//	}
//	
//	public class DownloadTask extends AsyncTask<Integer, Void, Integer> {
//
//		@Override
//		protected Integer doInBackground(Integer... dataType) {
//			switch(dataType[0]) {
//			case IConstants.DataType.SCHEDULE:
//				ScheduleUtils.sync(userId, getBaseContext());
//				break;
////			case IConstants.DataType.TODO:
////				TodoUtils.sync(userId, mActivity);
//			}
//			return Activity.RESULT_OK;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			mLocalBroadcastManager.sendBroadcast(new Intent(IConstants.Service.SYNC_ACTION_STARTED));
//		}
//		
//		@Override
//		protected void onPostExecute(Integer result) {
//			if (result == Activity.RESULT_OK) {
//				mLocalBroadcastManager.sendBroadcast(new Intent(IConstants.Service.SYNC_ACTION_FINISHED));
//				stopSelf();
//			}
//		}
//		
//		@Override
//        protected void onCancelled(){
//            mLocalBroadcastManager.sendBroadcast(new Intent(IConstants.Service.SYNC_ACTION_CANCELLED));
//        }
//	}
}
