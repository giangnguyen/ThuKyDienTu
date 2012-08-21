package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.adapter.ScheduleDebugAdapter;
import android.app.ListActivity;
import android.os.Bundle;

public class ScheduleDebugListActivity extends ListActivity {

	private ScheduleDebugAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);

		mAdapter = new ScheduleDebugAdapter(this, MainActivity.sUserId);
		setListAdapter(mAdapter);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAdapter.changeCursor(null);
		mAdapter = null;
	}

}
