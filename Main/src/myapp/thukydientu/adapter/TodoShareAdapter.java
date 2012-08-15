package myapp.thukydientu.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myapp.thukydientu.R;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.util.TaleTimeUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TodoShareAdapter extends BaseAdapter {
	
	private List<Todo> mListTodoShare = new ArrayList<Todo>();
	
	private class ViewHolder {
		TextView date;
		TextView time;
		TextView event;
		TextView description;
		TextView end;
	}
	
	private Context mContext;
	
	public TodoShareAdapter(Context context) {
		mContext = context;
	}
	
	public ViewHolder getViewHolder(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.date = (TextView) view.findViewById(R.id.date);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.event = (TextView) view.findViewById(R.id.event);
			holder.description = (TextView) view.findViewById(R.id.description);
			holder.end = (TextView) view.findViewById(R.id.end);
		}
		view.setTag(holder);
		return holder;
	}
	
	public void bindView(View view, Todo todo) {
		ViewHolder holder = (ViewHolder) view.getTag();
		
		final Calendar calendarStartTime = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateStart(), todo.getTimeFrom());
		final long currentTime = System.currentTimeMillis();
		if (calendarStartTime.getTimeInMillis() < currentTime)
			view.setBackgroundResource(R.drawable.list_item_out_of_date);
		else 
			view.setBackgroundResource(R.drawable.list_item);
		
		holder.date.setText(TaleTimeUtils.getDateLable(mContext, calendarStartTime));
		holder.time.setText(TaleTimeUtils.getTimeLable(mContext, calendarStartTime));

		holder.event.setText(todo.getTitle());
		holder.description.setText(todo.getWork());
		
		final Calendar calendarEndTime = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateEnd(), todo.getTimeUntil());
		holder.end.setText(TaleTimeUtils.getTimeLable(mContext, calendarEndTime) + " " + TaleTimeUtils.getDateLable(mContext, calendarEndTime));
	}

	public View newView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.todo_item, null);
		
		ViewHolder holder = getViewHolder(view);
		view.setTag(holder);
		
		return view;
	}

	@Override
	public int getCount() {
		return mListTodoShare.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListTodoShare.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = newView();
		
		bindView(convertView, mListTodoShare.get(position));
		
		return convertView;
	}

	public void setListTodo(List<Todo> list) {
		this.mListTodoShare = list;
		notifyDataSetChanged();
	}
}
