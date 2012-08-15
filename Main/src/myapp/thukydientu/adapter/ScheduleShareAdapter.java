package myapp.thukydientu.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myapp.thukydientu.R;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.util.ScheduleUtils;
import myapp.thukydientu.util.TaleTimeUtils;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleShareAdapter extends BaseExpandableListAdapter {
	static final int CONFIRM_DIALOG = 1;
	static final int INSERT_ERROR_DIALOG = 3;
	static final int INSERT_SUCCESS_DIALOG = 5;
	static final int REQUEST_TO_UPDATE = 6;
	private Context mContext;

	private int[] dayOfWeeks = new int[] {1, 2, 3, 4, 5, 6, 7};
	private List<Schedule> mListSchedule = new ArrayList<Schedule>();
	
	public ScheduleShareAdapter(Context context) {
		mContext = context;
	}

	public void setListSchedule(List<Schedule> listSchedule) {
		mListSchedule = listSchedule;
		notifyDataSetChanged();
	}
	
	class GroupHolder {
		TextView dateName;
		TextView count;
	}

	class ChildHolder {

		long id;
		Button edit;
		Button delete;
		TextView date;
		TextView time;
		TextView school;
		TextView className;
		TextView subject;
	}

	public GroupHolder getGroupHolder(View view) {

		GroupHolder holder = (GroupHolder) view.getTag();
		if (holder == null) {
			holder = new GroupHolder();
			holder.dateName = (TextView) view.findViewById(R.id.datename);
			holder.count = (TextView) view.findViewById(R.id.count);
		}
		return holder;
	}

	public ChildHolder getChildHolder(View view) {

		ChildHolder holder = (ChildHolder) view.getTag();
		if (holder == null) {
			holder = new ChildHolder();
			holder.edit = (Button) view.findViewById(R.id.edit);
			holder.delete = (Button) view.findViewById(R.id.delete);
			holder.date = (TextView) view.findViewById(R.id.date);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.school = (TextView) view.findViewById(R.id.school);
			holder.subject = (TextView) view.findViewById(R.id.subject);
			holder.className = (TextView) view.findViewById(R.id.classname);
		}
		view.setTag(holder);
		return holder;
	}

	protected void bindChildView(View view, final Schedule schedule) {

		ChildHolder holder = (ChildHolder) view.getTag();

		final String timeString = schedule.getTime();
		
		Calendar calendar = TaleTimeUtils.createCalendarByTimeString(timeString);

		holder.date.setText(getPeriodOfDay(calendar));
		holder.delete.setBackgroundResource(R.drawable.list_item_add_selector);
		holder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int result = ScheduleUtils.insert((Activity) mContext, schedule);
				if (result == ScheduleUtils.FAIL)
					Toast.makeText(mContext, "Thêm thất bại!", Toast.LENGTH_LONG).show();
				if (result == ScheduleUtils.REQUEST_TO_UPDATE) {
					final long Id = ScheduleUtils.getId((Activity) mContext, schedule.getDayName(), schedule.getTime());
					ScheduleUtils.update((Activity) mContext, schedule, Id);
					Toast.makeText(mContext, "Cập nhật thành công!", Toast.LENGTH_LONG).show();
				} else 
					Toast.makeText(mContext, "Thêm thành công", Toast.LENGTH_LONG).show();
			}
		});
		
		holder.time.setText(TaleTimeUtils.getTimeLable(mContext, calendar));
		holder.school.setText(schedule.getSchoolName());
		holder.subject.setText(schedule.getSubject());
		holder.className.setText(schedule.getClassName());

	}

	protected void bindGroupView(View view, int groupPosition) {

		final GroupHolder holder = getGroupHolder(view);

		holder.dateName.setText(TaleTimeUtils.getDayOfWeekString(dayOfWeeks[groupPosition]));

		final int count = getChildrenCount(groupPosition);
		if (count > 0)
			holder.count.setText("(" + count + ")");
		else
			holder.count.setText("");
	}

	protected View newChildView() {

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.schedule_item, null);

		ChildHolder holder = getChildHolder(view);

		view.setTag(holder);

		return view;
	}

	protected View newGroupView() {

		final LayoutInflater inflater = LayoutInflater.from(mContext);

		final View view = inflater.inflate(R.layout.schedule_list_item, null);

		final GroupHolder holder = getGroupHolder(view);

		view.setTag(holder);

		return view;
	}

	public String getPeriodOfDay(Calendar cal) {

		final int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		if (hourOfDay < 12) {
			return "Sáng";
		} else if (hourOfDay >= 12 || hourOfDay < 6) {
			return "Chiều";
		} else
			return "Tối";
	}

	public List<Schedule> getListChild(int groupPosition) {
		List<Schedule> listChild = new ArrayList<Schedule>();
		for (Schedule schedule : mListSchedule) {
			if (schedule.getDayName() == dayOfWeeks[groupPosition])
				listChild.add(schedule);
		}
		return listChild;
	}
	@Override
	public Object getChild (int groupPosition, int childPosition) {
		return getListChild(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId (int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = newChildView();
		
		bindChildView(convertView, (Schedule) getChild(groupPosition, childPosition));
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 0;
		for (Schedule schedule : mListSchedule) {
			if (schedule.getDayName() == dayOfWeeks[groupPosition])
				count++;
		}
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return dayOfWeeks[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return dayOfWeeks.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = newGroupView();
		
		bindGroupView(convertView, groupPosition);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}