package myapp.thukydientu.adapter;

import myapp.thukydientu.R;
import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.provider.TKDTProvider;
import myapp.thukydientu.util.ScheduleUtils;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ScheduleDebugAdapter extends CursorAdapter {
	
	private static final class QueryHandler extends AsyncQueryHandler {
	    private ScheduleDebugAdapter mAdapter;

	    public QueryHandler(Context context, ScheduleDebugAdapter adapter) {
	        super(context.getContentResolver());
	        this.mAdapter = adapter;
	    }

	    @Override
	    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	    	mAdapter.changeCursor(cursor);
	    }
	}
	
	private class ViewHolder {
		TextView id;
		TextView userid;
		TextView dateset;
		TextView datename;
		TextView time;
		TextView subject;
		TextView className;
		TextView school;
		TextView modified;
		TextView changed;
		TextView deleted;
	}
	
	public ScheduleDebugAdapter(Context context, int userId) {
		super(context, null);
		
		QueryHandler queryHandler = new QueryHandler(context, this);
		queryHandler.startQuery(
				userId, 
				null, 
				TKDTProvider.SCHEDULE_CONTENT_URI, 
				ScheduleTable.PROJECTION, 
				null,
				null, 
				null);
	}
	
	public ViewHolder getViewHolder(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.changed = (TextView) view.findViewById(R.id.changed);
			holder.className = (TextView) view.findViewById(R.id.classname);
			holder.datename = (TextView) view.findViewById(R.id.datename);
			holder.dateset = (TextView) view.findViewById(R.id.dateset);
			holder.deleted = (TextView) view.findViewById(R.id.deleted);
			holder.id = (TextView) view.findViewById(R.id._id);
			holder.modified = (TextView) view.findViewById(R.id.modified);
			holder.school = (TextView) view.findViewById(R.id.school);
			holder.subject = (TextView) view.findViewById(R.id.subject);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.userid = (TextView) view.findViewById(R.id.userid);
		}
		return holder;
	}
	
	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		
		final Schedule schedule = new Schedule();
		ScheduleUtils.bindScheduleData(schedule, cursor);
		
		holder.changed.setText(schedule.getChanged() + "");
		holder.className.setText(schedule.getClassName());
		holder.datename.setText(schedule.getDayName() + "");
		holder.dateset.setText(schedule.getDateSet());
		holder.deleted.setText(schedule.getDeleted() + "");
		holder.id.setText(schedule.getId() + "");
		holder.modified.setText(schedule.getModified());
		holder.school.setText(schedule.getSchoolName());
		holder.subject.setText(schedule.getSubject());
		holder.time.setText(schedule.getTime());
		holder.userid.setText(schedule.getUserId() + "");
		
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.schedule_debug_item, null);
		
		ViewHolder holder = getViewHolder(view);
		view.setTag(holder);
		
		return view;
	}

}
