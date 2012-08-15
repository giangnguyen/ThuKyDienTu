package myapp.thukydientu.service;

import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.IConstants.DataType;
import myapp.thukydientu.util.ScheduleUtils;
import myapp.thukydientu.util.TodoUtils;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class SyncService extends IntentService {

	private LocalBroadcastManager mLocalBroadcastManager;
	public SyncService() {
		super("SyncService");
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int userId = intent.getIntExtra(IConstants.User.ID, -1);
		int dataType = intent.getIntExtra(IConstants.DataType.DATA_TYPE, IConstants.DataType.SCHEDULE);
		
		if (dataType == DataType.ALL) {
			Sync(userId, DataType.SCHEDULE);
			Sync(userId, DataType.TODO);
		} else 
			Sync(userId, dataType);
	}
	
	private void Sync(int userId, int dataType) {

		Intent intent = new Intent();
		intent.putExtra(IConstants.DataType.DATA_TYPE, dataType);
		
		intent.setAction(IConstants.Service.SYNC_ACTION_STARTED);
		mLocalBroadcastManager.sendBroadcast(intent);
		
		switch(dataType) {
		case IConstants.DataType.SCHEDULE:
			ScheduleUtils.sync(userId, getBaseContext());
			break;
		case IConstants.DataType.TODO:
			TodoUtils.sync(userId, getBaseContext());
		}
		
		intent.setAction(IConstants.Service.SYNC_ACTION_FINISHED);
		mLocalBroadcastManager.sendBroadcast(intent);
	}
	
}
