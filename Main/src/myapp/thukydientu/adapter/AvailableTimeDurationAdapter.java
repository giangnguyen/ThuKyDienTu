package myapp.thukydientu.adapter;

import java.util.Calendar;
import java.util.List;

import myapp.thukydientu.model.TimeDuration;
import myapp.thukydientu.util.TimeUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class AvailableTimeDurationAdapter extends BaseExpandableListAdapter {
	
	private List<Integer> days;
	private List<TimeDuration> availableTimeDurations;
	private Context mContext;
	
	class GroupViewHolder {
		TextView dayOfMonth;
	}
	
	private GroupViewHolder getGroupViewHolder(View view) {
		GroupViewHolder holder = (GroupViewHolder) view.getTag();
		if (holder == null) {
			holder = new GroupViewHolder();
			holder.dayOfMonth = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		}
		return holder;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return 0;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		return null;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return availableTimeDurations.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return days.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return days.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_expandable_list_item_1, null);
		}
		
		GroupViewHolder holder = getGroupViewHolder(convertView);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, days.get(groupPosition));
		holder.dayOfMonth.setText(TimeUtils.getDateLable(mContext, calendar.getTimeInMillis()));
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return false;
	}

}