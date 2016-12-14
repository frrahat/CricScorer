package com.frrahat.cricscorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.frrahat.cricscorer.BowlEvent.EventType;
import com.frrahat.cricscorer.FileChooserDialog.OnFileChosenListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScorerActivity extends Activity {
	
	ScoreCard scoreCard;
	
	TextView txtViewScore;
	TextView txtViewBat1;
	TextView txtViewBat2;
	TextView txtViewBowl;
	TextView txtViewBall;
	
	Button buttons[];
	String buttonStrings[]={"0","1","2","3","4","5","6","wd","nb","b","","","W","W"};
	Button btnViewFullScorecard;
	
	private final int nextBatsmanReqCode=100;
	private final int nextBowlerReqCode=300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scorer);
		
		Intent intent = getIntent();
		scoreCard=new ScoreCard(intent.getStringExtra("bat1"),
				intent.getStringExtra("bat2"), intent.getStringExtra("bow"));
		
		txtViewScore=(TextView) findViewById(R.id.textViewScore);
		txtViewBat1=(TextView) findViewById(R.id.textViewBat1);
		txtViewBat2=(TextView) findViewById(R.id.textViewBat2);
		txtViewBowl=(TextView) findViewById(R.id.textViewBowl);
		txtViewBall=(TextView) findViewById(R.id.textViewBall);
		
		
		initializeButtons();
		updateTextViews();
	}

	private void initializeButtons() {
		
		btnViewFullScorecard=(Button) findViewById(R.id.buttonFullScoreCard);
		btnViewFullScorecard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ScorerActivity.this, FullScorecardActivity.class);
				intent.putExtra("scorecard", scoreCard.toString());
				startActivity(intent);
			}
		});
		
		buttons=new Button[14];
		
		buttons[0] = (Button) findViewById(R.id.button0);
		buttons[1] = (Button) findViewById(R.id.button1);
		buttons[2] = (Button) findViewById(R.id.button2);
		buttons[3] = (Button) findViewById(R.id.button3);
		buttons[4] = (Button) findViewById(R.id.button4);
		buttons[5] = (Button) findViewById(R.id.button5);
		buttons[6] = (Button) findViewById(R.id.button6);
		buttons[7] = (Button) findViewById(R.id.buttonWide);
		buttons[8] = (Button) findViewById(R.id.buttonNB);
		buttons[9] = (Button) findViewById(R.id.buttonBy);
		buttons[10] = (Button) findViewById(R.id.buttonPlus);
		buttons[11] = (Button) findViewById(R.id.buttonDel);
		buttons[12] = (Button) findViewById(R.id.buttonWicket);
		buttons[13] = (Button) findViewById(R.id.buttonRunout);
		
		for(int i=0;i<7;i++){
			final int p[]={i,0};
			buttons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(scoreCard.isNewOverNext){
						onNewOver();
					}
					BowlEvent bowlEvent=new BowlEvent(EventType.Run, p[0], buttonStrings[p[0]]);
					scoreCard.addBowlEvent(bowlEvent);
					
					updateTextViews();
				}
			});
		}
		
		for(int i=7;i<=9;i++){
			final int q[]={i,0};
			buttons[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(scoreCard.isNewOverNext){
						onNewOver();
					}
					BowlEvent bowlEvent=new BowlEvent(EventType.Extra, 1, buttonStrings[q[0]]);
					scoreCard.addBowlEvent(bowlEvent);
					
					updateTextViews();
				}
			});
		}
		
		buttons[10].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Not implemented",
						Toast.LENGTH_SHORT).show();
				/*if(scoreCard.isNewOverNext){
					onNewOver();
				}
				BowlEvent bowlEvent=new BowlEvent(EventType.Plus, 0, buttonStrings[10]);
				scoreCard.addBowlEvent(bowlEvent);
				updateTextViews();*/
			}
		});

		buttons[11].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Not implemented",
						Toast.LENGTH_SHORT).show();
				/*if(scoreCard.isNewOverNext){
					onNewOver();
				}
				BowlEvent bowlEvent=new BowlEvent(EventType.DeletePrev, 0, buttonStrings[11]);
				scoreCard.addBowlEvent(bowlEvent);
				
				updateTextViews();*/
			}
		});

		buttons[12].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(scoreCard.isNewOverNext){
					onNewOver();
				}
				//setNextBatsman
				Intent intent=new Intent(ScorerActivity.this, PlayerNameInputActivity.class);
				intent.putExtra("hint", "Batsman Name Next to Come");
				startActivityForResult(intent, nextBatsmanReqCode);
			}
		});

		buttons[13].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Not implemented",
						Toast.LENGTH_SHORT).show();
				/*if(scoreCard.isNewOverNext){
					onNewOver();
				}
				Intent intent=new Intent(ScorerActivity.this, PlayerNameInputActivity.class);
				intent.putExtra("hint", "Batsman Name Next to Come");
				startActivityForResult(intent, nextBatsmanReqCode);
				//BowlEvent bowlEvent=new BowlEvent(EventType.RunOut, 0, buttonStrings[13]);
				//scoreCard.addBowlEvent(bowlEvent);
				
				updateTextViews();*/
			}
		});
	}


	private void onNewOver() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Set Bowler Name");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        String bowlerName = input.getText().toString();
		        scoreCard.setCurrentBowler(bowlerName);
		        
		        updateTextViews();
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}

	private void updateTextViews() {
		txtViewScore.setText(scoreCard.toScoreString());
		txtViewBat1.setText(scoreCard.getStrikingBatsman().toString());
		txtViewBat2.setText(scoreCard.getNonStrikingBatsman().toString());
		txtViewBowl.setText(scoreCard.getCurrentBowler().toString());
		txtViewBall.setText(scoreCard.getCurrentOverstring());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scorer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.actionSaveToFile){
			onSave();
			return true;
		}
		if(id == R.id.actionLoadFromFile){
			onLoad();
			return true;
		}
		if(id == R.id.actionSwitchBatsman){
			scoreCard.switcthBatsMan();
			updateTextViews();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	private void onLoad() {
		FileChooserDialog fileChooserDialog=new FileChooserDialog(new String[]{".scorecard"});
		fileChooserDialog.setOnFileChosenListener(new OnFileChosenListener() {
			
			@Override
			public void onFileChosen(File file) {
				FileInputStream inStream;
				ObjectInputStream objectInStream;
				try {
					inStream = new FileInputStream(file);
					objectInStream = new ObjectInputStream(inStream);
					scoreCard = (ScoreCard) objectInStream.readObject();
					objectInStream.close();
					
					updateTextViews();

				} catch (IOException | ClassCastException | ClassNotFoundException e) {
					e.printStackTrace();
				} 
			}
		});
		fileChooserDialog.show(getFragmentManager(), "fileChooser");
	}

	private void onSave(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter File Name");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        String fileName = input.getText().toString();
		        saveToFile(getApplicationContext(), fileName+".scorecard");
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	private void saveToFile(Context context, String fileName) {
		File storageDir;
		String state=Environment.getExternalStorageState();
		// has writable external  storage
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			storageDir = new File(Environment.getExternalStorageDirectory(),
					CricScorerMainActivity.storageFolderName);
		} else {
			ContextWrapper contextWrapper = new ContextWrapper(
					context.getApplicationContext());
			storageDir = contextWrapper.getDir(CricScorerMainActivity.storageFolderName,
					Context.MODE_PRIVATE);
		}


		if (!storageDir.exists()) {
			if (storageDir.mkdirs()) {
				Log.i("success", "new folder added");
			} else {
				Log.i("failure", "folder addition failure");
			}
		}

		File dataStorageFile = new File(storageDir, fileName);
		
		
		FileOutputStream outStream;
		ObjectOutputStream objectOutStream;
		try {
			outStream = new FileOutputStream(dataStorageFile);
			objectOutStream = new ObjectOutputStream(outStream);
	
			objectOutStream.writeObject(scoreCard);
			// objectOutStream.flush();
			objectOutStream.close();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub		
		if(requestCode==nextBatsmanReqCode){
			scoreCard.setNextBatsman(data.getStringExtra("name"));
			BowlEvent bowlEvent=new BowlEvent(EventType.Wicket, 0, buttonStrings[12]);
			scoreCard.addBowlEvent(bowlEvent);	
			updateTextViews();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
