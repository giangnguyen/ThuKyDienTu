package myapp.thukydientu.view;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import myapp.thukydientu.R;
import myapp.thukydientu.adapter.FileAdapter;
import myapp.thukydientu.controller.Connection;
import myapp.thukydientu.model.BackToMainActivity;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.MyFile;
import myapp.thukydientu.util.FileUtils;
import myapp.thukydientu.util.WebservicesUtils;
import myapp.thukydientu.util.XMLUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileManagerActivity extends BackToMainActivity {

	private ProgressDialog mLoading;
	
	private final int UPLOAD_SUCCESS_DIALOG = 1;
	private final int DIALOG_EXIT_CONFIRM = 2;
	private final int DIALOG_ERROR_NETWORK = 3;

	private int userId;

	// Constants for target
	public static final String ONLINE = "online";
	public static final String LOCAL = "local";
	public static String Target = LOCAL;
	
	// Constants for state
	public static final int MAIN = 1;
	public static final int LIST = 2;
	public static int State = MAIN;

	private final String ROOT = Environment.getExternalStorageDirectory() + "";
	private File path = new File(ROOT);

	public void setLoading(Context context, boolean loading) {
		if (loading) {
			if (context == null || mLoading != null)
				return;
			mLoading = ProgressDialog.show(context, null,
					context.getString(R.string.loading), true, true);
		} else if (mLoading != null) {
			mLoading.dismiss();
			mLoading = null;
		}
	}

	class ListFileHandler extends AsyncTask<String, Void, List<MyFile>> {

		private FileAdapter mAdapter;
		private Context mContext;

		public ListFileHandler(Context context, FileAdapter adapter) {
			mAdapter = adapter;
			mContext = context;
		}

		@Override
		protected List<MyFile> doInBackground(String... params) {
			final String target = params[0];
			final String filePath = params[1];
			List<MyFile> listFile = new ArrayList<MyFile>();
			if (target.equals(LOCAL)) { // file on sdCard
				path = new File(path, filePath);
				String list[] = path.list(filter);

				for (int i = 0; i < list.length; i++) {
					final String fileName = list[i];
					final File file = new File(path, fileName);
					MyFile myFile = new MyFile();

					if (file.isDirectory()) {
						myFile.setFormat(FileUtils.DIRECTORY);

					} else {
						myFile.setFormat(FileUtils.getFileFormat(fileName));
					}
					myFile.setLink(file.getPath());
					myFile.setFileName(fileName);
					myFile.setSize(file.length());
					listFile.add(myFile);
				}
			} else { // online
				final String serverReturn = WebservicesUtils.getFile(userId, -1);
				listFile = XMLUtils.getFileResult(serverReturn);
			}
			if (listFile != null)
				Collections.sort(listFile, comparator);
			
			return listFile;
		}

		private FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				File sel = new File(dir, filename);
				// Filters based on whether the file is hidden or not
				return (sel.isFile() || sel.isDirectory()) && !sel.isHidden();
			}
		};
		
		private Comparator<MyFile> comparator = new Comparator<MyFile>() {

			@Override
			public int compare(MyFile lhs, MyFile rhs) {

				final String lFormat = lhs.getFormat();
				final String rFormat = rhs.getFormat();
				final String lname = lhs.getFileName();
				final String rname = rhs.getFileName();

				if (lFormat != null && lFormat.equals(FileUtils.DIRECTORY)) {
					if (rFormat != null && rFormat.equals(FileUtils.DIRECTORY))
						return lname.compareToIgnoreCase(rname);
					else
						return -1;
				} else {
					if (rFormat != null && rFormat.equals(FileUtils.DIRECTORY))
						return 1;
					else
						return lname.compareToIgnoreCase(rname);
				}
			}
		};

		@Override
		protected void onPostExecute(List<MyFile> result) {
			setLoading(mContext, false);
			mAdapter.setListFile(result);
		}

		@Override
		protected void onPreExecute() {
			setLoading(mContext, true);
		}

	}

	private FileAdapter mFileAdapter;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_manager);

		SharedPreferences prefs = getSharedPreferences(IConstants.PREF_NAME,
				MODE_PRIVATE);
		userId = prefs.getInt(IConstants.User.ID, 0);

		mFileAdapter = new FileAdapter(this);
		ListView list = (ListView) findViewById(R.id.file_list);
		list.setAdapter(mFileAdapter);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final MyFile file = (MyFile) parent.getItemAtPosition(position);
				if (Target.equals(LOCAL)) {
					final String format = file.getFormat();
					if (!TextUtils.isEmpty(format)) {
						if (format.equals(FileUtils.DIRECTORY)) {
							exploreDirectory(Target, file.getFileName());
						} else
							FileUtils.openFile(mContext, file.getLink());
					}
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("Xác Nhận!")
							.setMessage("Tải file: " + file.getFileName())
							.setPositiveButton("Đồng Ý", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new myapp.thukydientu.service.DownloadService(mContext, file);
								}
							})
							.setNegativeButton("Hủy", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							})
							.create()
							.show();
				}
			}
			
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long Id) {
				final MyFile file = (MyFile) parent.getItemAtPosition(position);
				if (!TextUtils.isEmpty(file.getFormat()) && file.getFormat().equals(FileUtils.DIRECTORY))
					return false;
				if (Target.equals(LOCAL)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext);
					builder.setTitle("Xác Nhận!");
					builder.setMessage("Tải lên file: " + file.getFileName());
					builder.setPositiveButton("Đồng Ý", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							new UploadFileTask().execute(file);
						}
					});
					builder.setCancelable(false);
					builder.create().show();
				}
				return true;
			}
		});
		
		View online = findViewById(R.id.online);
		online.setOnClickListener(mainItemClickListener);
		View offline = findViewById(R.id.offline);
		offline.setOnClickListener(mainItemClickListener);
		
	}
	
	private View.OnClickListener mainItemClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.online) {
				if (Connection.isInternetConnected(mContext)) {
					Target = ONLINE;
					switchState(LIST);
				} else 
					showDialog(DIALOG_ERROR_NETWORK);
			}
			else { 
				Target = LOCAL;
				switchState(LIST);
			}
		}
	};
	
	public void showStateList() {
		
		ListFileHandler handler = new ListFileHandler(this, mFileAdapter);
		handler.execute(Target, "");
		
		// hide main 
		final View main = findViewById(R.id.main);
		main.setVisibility(View.GONE);
		
		// show list file
		final View listView = findViewById(R.id.list);
		final TextView header = (TextView) findViewById(R.id.header);
		if (Target.equals(LOCAL)) 
			header.setText("Tài liệu trên thẻ nhớ");
		else 
			header.setText("Tài liệu trực tuyến");
		listView.setVisibility(View.VISIBLE);
		
	}
	
	public void showStateMain() {
		
		// hide main 
		final View main = findViewById(R.id.main);
		main.setVisibility(View.VISIBLE);
		
		// show list file
		final View listView = findViewById(R.id.list);
		listView.setVisibility(View.GONE);
		mFileAdapter.setListFile(null);
		
	}
	
	public void switchState(int state) {
		switch (state) {
		
		case MAIN: {
			State = MAIN;
			showStateMain();
			break;
		}
		
		case LIST: {
			State = LIST;
			showStateList();
			break;
		}
		
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (State == MAIN)
			return super.onKeyDown(keyCode, event);
		else {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if (path.getPath().equals(ROOT)) {
					switchState(MAIN);
				} else {
					final String curentPath = path.getPath();
					final int index = curentPath.lastIndexOf("/");
					path = new File(curentPath.substring(0, index));
					exploreDirectory(Target, "");
				}
				return true;
			}
		}
		return false;
	}

	public void exploreDirectory(String location, String directory) {
		ListFileHandler handler = new ListFileHandler(mContext,
				mFileAdapter);
		handler.execute(location, directory);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Upload file");
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case UPLOAD_SUCCESS_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Tải Lên!");
			builder.setMessage("Thành Công!");
			builder.setPositiveButton("Đồng Ý", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setCancelable(false);
			return builder.create();
		}
		
		case DIALOG_EXIT_CONFIRM: {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Xác Nhận!");
			builder.setMessage("Thoát Khỏi Ứng Dụng?");
			builder.setPositiveButton("Đồng Ý", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.setNegativeButton("Hủy", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.setCancelable(false);
			return builder.create();
		}
		
		case DIALOG_ERROR_NETWORK: {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Lỗi Kết Nối!");
			builder.setMessage("Vui lòng kiểm tra lại kết nối internet trước khi sử dụng chức năng này?");
			builder.setNeutralButton("Đồng Ý", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setCancelable(false);
			return builder.create();
		}

		}
		return null;
	}

	private class UploadFileTask extends AsyncTask<MyFile, Void, Void> {

		@Override
		protected Void doInBackground(MyFile... params) {
			try {
				WebservicesUtils.addFile(userId, params[0]);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(mContext, "Upload Success!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mFileAdapter = null;
	}

}
