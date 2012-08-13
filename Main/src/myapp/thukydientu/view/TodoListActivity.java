package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.adapter.TodoAdapter;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.service.SyncService;
import myapp.thukydientu.util.TodoUtils;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class TodoListActivity extends ListActivity {

	private TodoAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);

		mAdapter = new TodoAdapter(this, MainActivity.sUserId);
		setListAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		createMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0: {
			Intent todoAdd = new Intent("myapp.thukygiangvien.TODO_ADD_ACTIVITY");
			startActivity(todoAdd);
			break;
		}
		case 1: {
			TodoUtils.deleteAll(TodoListActivity.this);
			break;
		}
		case 2:
			new SyncService(this, MainActivity.sUserId, IConstants.DataType.TODO);
			break;
		}
		return true;

	}

	private void createMenu(Menu menu) {

		MenuItem addItem = menu.add(0, 0, 0, "Thêm");
		addItem.setIcon(R.drawable.ic_menu_add_picture);
		MenuItem deleteItem = menu.add(0, 1, 1, "Xóa tất cả");
		deleteItem.setIcon(R.drawable.ic_menu_clear_list);
		MenuItem sync = menu.add(0, 2, 2, "Đồng Bộ");
		sync.setIcon(R.drawable.sync);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Null out the group cursor. This will cause the group cursor and all
		// of the child cursors
		// to be closed.
		mAdapter.changeCursor(null);
		mAdapter = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	startActivity(new Intent(this, MainActivity.class));
	    	finish();
	        return true;
	    }
	    return false;
	}
}
