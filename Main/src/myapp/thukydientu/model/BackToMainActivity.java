package myapp.thukydientu.model;

import myapp.thukydientu.view.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

public class BackToMainActivity extends Activity {
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	startActivity(new Intent(this, MainActivity.class));
	    	finish();
	        return true;
	    }
	    return false;
	}
}
