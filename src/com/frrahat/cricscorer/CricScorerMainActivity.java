package com.frrahat.cricscorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * @author Rahat
 * @since Dec 13, 2016
 */
public class CricScorerMainActivity extends Activity {
	
	Button startButton;
	EditText batsman1Edt;
	EditText batsman2Edt;
	EditText bowlerEdt;
	
	public static final String storageFolderName="CricScorer";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cric_scorer_main);
		
		startButton=(Button) findViewById(R.id.buttonStart);
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(CricScorerMainActivity.this, ScorerActivity.class);
				intent.putExtra("bat1", batsman1Edt.getText().toString());
				intent.putExtra("bat2", batsman2Edt.getText().toString());
				intent.putExtra("bow", bowlerEdt.getText().toString());
				startActivity(intent);
				
			}
		});
		
		batsman1Edt=(EditText) findViewById(R.id.editTextBat1);
		batsman2Edt=(EditText) findViewById(R.id.editTextBat2);
		bowlerEdt=(EditText) findViewById(R.id.editTextBow);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cric_scorer_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_getSource) {
			onGetSource();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void onGetSource(){
		final TextView message = new TextView(this);
		// i.e.: R.string.dialog_message =>
		// "Test this dialog following the link to dtmilano.blogspot.com"
		final SpannableString s = new SpannableString("https://github.com/frrahat/CricScorer");
		Linkify.addLinks(s, Linkify.WEB_URLS);
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());

		new AlertDialog.Builder(this).setTitle("Source link")
				.setCancelable(true).setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("OK", null)
				.setView(message).show();
	}
}
