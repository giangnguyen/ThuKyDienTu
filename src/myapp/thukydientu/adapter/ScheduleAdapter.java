package myapp.thukydientu.adapter;

import java.util.Calendar;

import myapp.thukydientu.R;
import myapp.thukydientu.database.DayOfWeekTable;
import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.provider.TKDTProvider;
import myapp.thukydientu.util.ScheduleUtils;
import myapp.thukydientu.util.TimeUtils;
import myapp.thukydientu.view.ScheduleAddActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends CursorTreeAdapter {

	private Context mContext;

	private static final int TOKEN_GROUP = 0;
	private static final int TOKEN_CHILD = 1;
	
	private static final class QueryHandler extends AsyncQueryHandler {

		private CursorTreeAdapter mAdapter;

		public QueryHandler(Context context, CursorTreeAdapter adapter) {
			
			super(context.getContentResolver());
			this.mAdapter = adapter;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

			switch (token) {
			case TOKEN_GROUP:
				mAdapter.setGroupCursor(cursor);
				break;

			case TOKEN_CHILD:
				int groupPosition = (Integer) cookie;
				mAdapter.setChildrenCursor(groupPosition, cursor);
				break;
			}
		}
	}

	private QueryHandler mQueryHandler;
	
	public ScheduleAdapter(Context context) {

		super(null, context);
		mContext = context;
		
		mQueryHandler = new QueryHandler(mContext, this);

		// Query for day of week
		mQueryHandler.startQuery(TOKEN_GROUP, null,
				TKDTProvider.DAY_OF_WEEK_CONTENT_URI, 
				DayOfWeekTable.PROJECTION,
				null, null, null);
	}

	class GroupHolder {

		TextView dateName;
		TextView count;
		TextView add_schedule;
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
			holder.add_schedule = (TextView) view
					.findViewById(R.id.add_schedule);
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
		return holder;
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {

		// Given the group, we return a cursor for all the children within
		// that group
		// Return a cursor that points to schedules of this day

		final int dateName = groupCursor
				.getInt(ScheduleTable.DATE_NAME_COLUMN_INDEX);
		mQueryHandler.startQuery(TOKEN_CHILD, groupCursor.getPosition(),
				TKDTProvider.SCHEDULE_CONTENT_URI, ScheduleTable.PROJECTION,
				ScheduleTable.DATE_NAME + "=" + dateName + " AND "
						+ ScheduleTable.DELETED + "=0", null, null);

		return null;
	}

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor,
			boolean arg3) {

		ChildHolder holder = (ChildHolder) view.getTag();

		final long Id = cursor.getLong(ScheduleTable.ID_COLUMN_INDEX);

		final String time = cursor
				.getString(ScheduleTable.TIME_COLUMN_INDEX);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, TimeUtils.getHour(time));
		cal.set(Calendar.MINUTE, TimeUtils.getMinute(time));
		cal.set(Calendar.SECOND, 0);

		holder.id = cursor.getLong(ScheduleTable.ID_COLUMN_INDEX);
		holder.date.setText(getPeriodOfDay(cal));
		holder.time.setText(TimeUtils.getTimeLable(context,
				cal.getTimeInMillis()));
		holder.school.setText(cursor
				.getString(ScheduleTable.SCHOOL_COLUMN_INDEX));
		holder.subject.setText(cursor
				.getString(ScheduleTable.SUBJECT_COLUMN_INDEX));
		holder.className.setText(cursor
				.getString(ScheduleTable.CLASS_COLUMN_INDEX));

		holder.edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(mContext,
						ScheduleAddActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(IConstants._ID, Id);
				intent.putExtras(bundle);
				((Activity) mContext).startActivityForResult(intent, 1);
			}
		});
		holder.delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						mContext);
				builder.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("Xác nhận!")
						.setMessage("Xóa thời khóa biểu?")
						.setPositiveButton("Đồng ý",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {

										ScheduleUtils.delete(
												(Activity) mContext, Id);
									}
								})
						.setNegativeButton("Hủy",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {

									}
								});
				builder.create().show();
			}
		});
	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor,
			boolean arg3) {

		final GroupHolder holder = getGroupHolder(view);

		final int dayOfWeek = cursor
				.getInt(ScheduleTable.DATE_NAME_COLUMN_INDEX);

		holder.dateName.setText(TimeUtils.getDateName(dayOfWeek));

		holder.add_schedule.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext,
						ScheduleAddActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(ScheduleTable.DATE_NAME, dayOfWeek);
				intent.putExtras(bundle);
				((Activity) mContext).startActivityForResult(intent, 1);
			}
		});
		holder.add_schedule.bringToFront();
		Cursor childCursor = context.getContentResolver().query(
				TKDTProvider.SCHEDULE_CONTENT_URI,
				ScheduleTable.PROJECTION,
				ScheduleTable.DATE_NAME + "=" + dayOfWeek + " AND "
						+ ScheduleTable.DELETED + "=0", null, null);
		final int count = childCursor.getCount();
		if (count > 0)
			holder.count.setText("(" + count + ")");
		else
			holder.count.setText("");
		childCursor.close();
	}

	@Override
	protected View newChildView(Context context, Cursor cursor,
			boolean arg2, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.schedule_item, null);

		ChildHolder holder = getChildHolder(view);

		view.setTag(holder);

		return view;
	}

	@Override
	protected View newGroupView(Context context, Cursor cursor,
			boolean arg2, ViewGroup parent) {

		final LayoutInflater inflater = LayoutInflater.from(context);

		final View view = inflater.inflate(R.layout.schedule_list_item,
				null);

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

}