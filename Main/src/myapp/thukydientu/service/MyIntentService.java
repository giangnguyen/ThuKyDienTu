package myapp.thukydientu.service;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class MyIntentService extends IntentService{

	public MyIntentService() {
		super("MyIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Toast.makeText(getBaseContext(), "onHandle Service", Toast.LENGTH_LONG).show();
	}

}
