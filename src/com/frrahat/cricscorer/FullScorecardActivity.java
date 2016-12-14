package com.frrahat.cricscorer;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class FullScorecardActivity extends Activity {
	
	EditText editTextscorecard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_scorecard);
		
		editTextscorecard=(EditText) findViewById(R.id.editTextScorecard);
		
		editTextscorecard.setText(getIntent().getStringExtra("scorecard"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_scorecard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.actionCopyToClipboard) {
			copyToClipboard();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void copyToClipboard() {
		ClipData clip = ClipData.newPlainText("text",editTextscorecard.getText());
		((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clip);
		Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
	}
}
