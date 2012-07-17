package myapp.thukydientu.adapter;

import java.util.ArrayList;
import java.util.List;

import myapp.thukydientu.R;
import myapp.thukydientu.model.FileItem;
import myapp.thukydientu.model.MyFile;
import myapp.thukydientu.util.FileUtils;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {

	private List<MyFile> mListFile = new ArrayList<MyFile>();
	Context mContext;

	public FileAdapter(Context context) {
		mContext = context;
	}

	public FileItem getFileHolder(View view) {
		FileItem itemHolder = (FileItem) view.getTag();
		if (itemHolder == null) {
			itemHolder = new FileItem();
			itemHolder.icon = (ImageView) view.findViewById(R.id.icon);
			itemHolder.name = (TextView) view.findViewById(R.id.name);
			itemHolder.size = (TextView) view.findViewById(R.id.size);
		}
		return itemHolder;
	}

	@Override
	public int getCount() {
		return mListFile.size();
	}

	@Override
	public Object getItem(int possition) {
		return mListFile.get(possition);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = newView();
		bindView(convertView, position);
		return convertView;
	}

	public View newView() {
		View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.file_item, null);
		FileItem item = getFileHolder(view);
		view.setTag(item);
		return view;
	}

	public void bindView(View convertView, int position) {
		FileItem item = getFileHolder(convertView);
		final MyFile file = mListFile.get(position);
		item.icon.setImageDrawable(getIconForFile(file));
		item.name.setText(file.getFileName());
		final String format = file.getFormat();
		if (!TextUtils.isEmpty(format) && format.equals(FileUtils.DIRECTORY))
			item.size.setText("");
		else
			item.size.setText(FileUtils.byteCountToDisplaySize((long) (file
					.getSize())));
	}

	public List<MyFile> getListFile() {
		return mListFile;
	}

	public void setListFile(List<MyFile> listFile) {
		if (listFile == null)
			this.mListFile.clear();
		else 
			this.mListFile = listFile;
		notifyDataSetChanged();
	}

	public Drawable getIconForFile(MyFile file) {

		final Resources res = mContext.getResources();
		final String format = file.getFormat();

		if (!TextUtils.isEmpty(format)) {
			if (format.equals(FileUtils.DIRECTORY))
				return res.getDrawable(R.drawable.folder);
			if (format.contains("image")) {
				final String filePath = file.getLink();
				Bitmap image = BitmapFactory.decodeFile(filePath);
				return new BitmapDrawable(image);
			}
			if (format.contains("video")) {
				return res.getDrawable(R.drawable.myfiles_icon_video_thumb);
			}
			if (format.contains("html")) {
				return res.getDrawable(R.drawable.file_html);
			}
			if (format.contains("pdf")) {
				return res.getDrawable(R.drawable.file_pdf);
			}
			if (format.contains("ppt")) {
				return res.getDrawable(R.drawable.file_ppt);
			}
			if (format.contains("doc")) {
				return res.getDrawable(R.drawable.file_doc);
			}
			if (format.contains("xls")) {
				return res.getDrawable(R.drawable.file_xls);
			}
			if (format.contains("audio")) {
				return res.getDrawable(R.drawable.file_mp3);
			}
			if (format.contains("text")) {
				return res.getDrawable(R.drawable.file_txt);
			}

		}

		final String fileName = file.getFileName();
		final int index = fileName.lastIndexOf(".");
		if (index == -1)
			return res.getDrawable(R.drawable.file_etc);

		final String extension = fileName.substring(index + 1);
		if (extension.equalsIgnoreCase("APK"))
			return res.getDrawable(R.drawable.file_apk);
		if (extension.equalsIgnoreCase("jad")
				|| extension.equalsIgnoreCase("jar"))
			return res.getDrawable(R.drawable.file_java);

		return res.getDrawable(R.drawable.file_etc);

	}

}
