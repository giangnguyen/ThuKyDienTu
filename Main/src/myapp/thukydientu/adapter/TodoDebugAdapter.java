package myapp.thukydientu.adapter;

import myapp.thukydientu.R;
import myapp.thukydientu.database.TodoTable;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.provider.TKDTProvider;
import myapp.thukydientu.util.TodoUtils;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TodoDebugAdapter extends CursorAdapter {
	
	private static final class QueryHandler extends AsyncQueryHandler {
	    private TodoDebugAdapter mAdapter;

	    public QueryHandler(Context context, TodoDebugAdapter adapter) {
	        super(context.getContentResolver());
	        this.mAdapter = adapter;
	    }

	    @Override
	    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	    	mAdapter.changeCursor(cursor);
	    	mAdapter.notifyDataSetChanged();
	    }
	}
	
	private class ViewHolder {
		TextView id;
		TextView userid;
		TextView datestart;
		TextView dateend;
		TextView timefrom;
		TextView timeuntil;
		TextView title;
		TextView work;
		TextView alarm;
		TextView dateset;
		TextView modified;
		TextView changed;
		TextView deleted;
	}
	
	public TodoDebugAdapter(Context context, int userId) {
		super(context, null);
		
		QueryHandler queryHandler = new QueryHandler(context, this);
		queryHandler.startQuery(
				userId, 
				null, 
				TKDTProvider.TODO_CONTENT_URI, 
				TodoTable.PROJECTION, 
				null,
				null, 
				null);
	}
	
	public ViewHolder getViewHolder(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.alarm = (TextView) view.findViewById(R.id.alarm);
			holder.changed = (TextView) view.findViewById(R.id.changed);
			holder.dateend = (TextView) view.findViewById(R.id.dateend);
			holder.dateset = (TextView) view.findViewById(R.id.dateset);
			holder.datestart = (TextView) view.findViewById(R.id.datestart);
			holder.deleted = (TextView) view.findViewById(R.id.deleted);
			holder.id = (TextView) view.findViewById(R.id._id);
			holder.modified = (TextView) view.findViewById(R.id.modified);
			holder.timefrom = (TextView) view.findViewById(R.id.timefrom);
			holder.timeuntil = (TextView) view.findViewById(R.id.timeuntil);
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.userid = (TextView) view.findViewById(R.id.userid);
			holder.work = (TextView) view.findViewById(R.id.work);
		}
		return holder;
	}
	
	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		
		final Todo todo = new Todo();
		TodoUtils.bindTodoData(todo, cursor);
		
		holder.alarm.setText(todo.getAlarm() + "");
		holder.changed.setText(todo.getChanged() + "");
		holder.dateend.setText(todo.getDateEnd());
		holder.dateset.setText(todo.getDateSet());
		holder.datestart.setText(todo.getDateStart());
		holder.deleted.setText(todo.getDeleted() + "");
		holder.id.setText(todo.getId() + "");
		holder.modified.setText(todo.getModified());
		holder.timefrom.setText(todo.getTimeFrom());
		holder.timeuntil.setText(todo.getTimeUntil());
		holder.title.setText(todo.getTitle());
		holder.userid.setText(todo.getUserId() + "");
		holder.work.setText(todo.getWork());
		
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.todo_debug_item, null);
		
		ViewHolder holder = getViewHolder(view);
		view.setTag(holder);
		
		return view;
	}

}
