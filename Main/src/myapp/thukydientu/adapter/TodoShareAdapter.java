package myapp.thukydientu.adapter;

import java.util.ArrayList;
import java.util.List;

import myapp.thukydientu.R;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.util.TimeUtils;
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
	
	public TodoShareAdapter(Context context, List<Todo> listTodo) {
		mContext = context;
		mListTodoShare = listTodo;
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
		
		final long startTime = TimeUtils.toTimeInMilisecond(todo.getDateStart(), todo.getTimeFrom());
		final long currentTime = System.currentTimeMillis();
		if (startTime < currentTime)
			view.setBackgroundResource(R.drawable.list_item_out_of_date);
		else 
			view.setBackgroundResource(R.drawable.list_item);
		
		holder.date.setText(TimeUtils.getDateLable(mContext, startTime));
		holder.time.setText(TimeUtils.getTimeLable(mContext, startTime));

		holder.event.setText(todo.getTitle());
		holder.description.setText(todo.getWork());
		
		final long endTime = Long.parseLong(todo.getDateEnd());
		holder.end.setText(TimeUtils.getTimeLable(mContext, endTime) + " " + TimeUtils.getDateLable(mContext, endTime));
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

}
