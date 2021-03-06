package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.controller.Connection;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.service.SyncService;
import myapp.thukydientu.util.AndroidUtil;
import myapp.thukydientu.util.FileUtils;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity {

	public static final String TAB_HOME_ID = "TAB_HOME";
	public static final String TAB_SCHEDULE_ID = "TAB_SCHEDULE";
	public static final String TAB_TODO_ID = "TAB_TODO";
	public static final String TAB_FILE_MANAGER_ID = "TAB_FILE_MANAGER";

	public static MainActivity sInstance;

	public ImageView scheduleSync;
	public ImageView todoSync;

	private LocalBroadcastManager mLocalBroadcastManager;

	public static int sUserId;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			int dataType = intent.getIntExtra(IConstants.DataType.DATA_TYPE, IConstants.DataType.SCHEDULE);
			
			if (intent.getAction().equals(IConstants.Service.DOWNLOAD_ACTION_STARTED)) {
				Toast.makeText(context, "Download started!", Toast.LENGTH_SHORT).show();
				AndroidUtil.log("OnReceive", "Download started!");
			}

			if (intent.getAction().equals(IConstants.Service.DOWNLOAD_ACTION_FINISHED)) {
				final String filePath = intent.getStringExtra(IConstants.Service.DOWNLOADED_FILE_PATH);
				Toast.makeText(context, "Download finished!", Toast.LENGTH_SHORT).show();

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				
				builder.setTitle("Thành Công!")
						.setMessage("Đã tải thành công tài liệu: " + filePath)
						.setNeutralButton("Mở",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,	int which) {
										FileUtils.openFile(MainActivity.this, filePath);
									}
								})
						.create()
						.show();
			}

			if (intent.getAction().equals(IConstants.Service.DOWNLOAD_ACTION_CANCELLED)) {
				Toast.makeText(context, "Download cancelled!", Toast.LENGTH_SHORT).show();
				AndroidUtil.log("OnReceive", "Download canceled!");
			}

			Animation syncAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.sync);
			
			if (intent.getAction().equals(IConstants.Service.SYNC_ACTION_STARTED)) {
				switch (dataType) {
				case IConstants.DataType.SCHEDULE:
					scheduleSync.setVisibility(View.VISIBLE);
					scheduleSync.startAnimation(syncAnimation);
					break;
				case IConstants.DataType.TODO:
					todoSync.setVisibility(View.VISIBLE);
					todoSync.startAnimation(syncAnimation);
					break;
				}
				AndroidUtil.log("OnReceive", "Sync Started!");
			}

			if (intent.getAction().equals(IConstants.Service.SYNC_ACTION_FINISHED)) {
				switch (dataType) {
				case IConstants.DataType.SCHEDULE:
					scheduleSync.setVisibility(View.INVISIBLE);
					scheduleSync.clearAnimation();
					break;
				case IConstants.DataType.TODO:
					todoSync.setVisibility(View.INVISIBLE);
					todoSync.clearAnimation();
					break;
				}
				AndroidUtil.log("OnReceive", "Sync Finished!");
			}

			if (intent.getAction().equals(IConstants.Service.SYNC_ACTION_CANCELLED)) {
				Toast.makeText(context, "Đồng Bộ Thất Bại!", Toast.LENGTH_SHORT).show();
				AndroidUtil.log("OnReceive", "Sync Canceled!");
			}
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		sInstance = this;

		SharedPreferences prefs = getSharedPreferences(IConstants.PREF_NAME, MODE_PRIVATE);
		sUserId = prefs.getInt(IConstants.User.ID, 0);

		createTabLayout();

		/*******************************************/
		/* Broadcast Receiver */
		/*******************************************/
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(IConstants.Service.DOWNLOAD_ACTION_STARTED);
		filter.addAction(IConstants.Service.DOWNLOAD_ACTION_FINISHED);
		filter.addAction(IConstants.Service.DOWNLOAD_ACTION_CANCELLED);
		filter.addAction(IConstants.Service.SYNC_ACTION_STARTED);
		filter.addAction(IConstants.Service.SYNC_ACTION_FINISHED);
		filter.addAction(IConstants.Service.SYNC_ACTION_CANCELLED);
		
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);

		ImageButton share = (ImageButton) findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.HOUR_OF_DAY, 8);
//				long startTime = cal.getTimeInMillis();
//				cal.set(Calendar.HOUR_OF_DAY, 17);
//				long endTime = cal.getTimeInMillis();
//
//				HintTimeManager hintTimeManager = new HintTimeManager(
//						startTime, endTime, 15, 30, 45 * 60 * 1000);
//				List<TimeDuration> listHint = hintTimeManager.getHintTimeByDay(MainActivity.this, 20);
//				for (TimeDuration timeDuration : listHint) {
//					Log.d("MainActivity + time",
//							"startTime: "
//									+ TaleTimeUtils.getTimeLable(MainActivity.this,
//											timeDuration.getStartTime())
//									+ " endTime: "
//									+ TaleTimeUtils.getTimeLable(MainActivity.this,
//											timeDuration.getEndTime()));
//				}
			}
		});
	
		if (Connection.isInternetConnected(MainActivity.this)) {
			Intent intent = new Intent(MainActivity.this, SyncService.class);
			intent.putExtra(IConstants.User.ID, sUserId);
			intent.putExtra(IConstants.DataType.DATA_TYPE, IConstants.DataType.ALL);
			startService(intent);
		}
	}

	public void createTabLayout() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)

		View home = getLayoutInflater().inflate(R.layout.tab_home, null);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, HomeActivity.class);
		spec = tabHost.newTabSpec(TAB_HOME_ID).setIndicator(home)
				.setContent(intent);
		tabHost.addTab(spec);

		View schedule = getLayoutInflater()
				.inflate(R.layout.tab_schedule, null);
		scheduleSync = (ImageView) schedule.findViewById(R.id.sync);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, ScheduleListActivity.class);
		spec = tabHost.newTabSpec(TAB_SCHEDULE_ID).setIndicator(schedule)
				.setContent(intent);
		tabHost.addTab(spec);

		View todo = getLayoutInflater().inflate(R.layout.tab_todo, null);
		todoSync = (ImageView) todo.findViewById(R.id.sync);

		intent = new Intent().setClass(this, TodoListActivity.class);
		spec = tabHost.newTabSpec(TAB_TODO_ID).setIndicator(todo)
				.setContent(intent);
		tabHost.addTab(spec);

		View file = getLayoutInflater()
				.inflate(R.layout.tab_file_manager, null);
		intent = new Intent().setClass(this, FileManagerActivity.class);
		spec = tabHost.newTabSpec(TAB_FILE_MANAGER_ID).setIndicator(file)
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTabByTag(TAB_HOME_ID);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				TextView title = (TextView) findViewById(R.id.title);

				if (tabId.equals(TAB_SCHEDULE_ID))
					title.setText(R.string.schedule);

				if (tabId.equals(TAB_TODO_ID))
					title.setText(R.string.todo);

				if (tabId.equals(TAB_FILE_MANAGER_ID))
					title.setText(R.string.file_manager);

				if (tabId.equals(TAB_HOME_ID))
					title.setText(R.string.home);

			}
		});
	}

}