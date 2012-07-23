package myapp.thukydientu.view;

import myapp.thukydientu.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends Activity {

	public static final int DIALOG_INFORM_INPUT = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		View inform = findViewById(R.id.inform);
		inform.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_INFORM_INPUT);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		View informView = LayoutInflater.from(this).inflate(R.layout.inform, null);
		
		final EditText title = (EditText) informView.findViewById(R.id.title);
		final EditText content = (EditText) informView.findViewById(R.id.content);
		
		Button submit = (Button) informView.findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String titleString = title.getText().toString();
				final String contentString = content.getText().toString();
				Toast.makeText(HomeActivity.this, "title: " + titleString + " content: " + contentString, Toast.LENGTH_LONG).show();
			}
		});
		
		builder.setView(informView);
		
		return builder.create();
	}
	
	

}
