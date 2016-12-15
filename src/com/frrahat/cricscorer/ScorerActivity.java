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

public class ScorerActivity extends Activity {
	
	ScoreCard scoreCard;
	
	TextView txtViewScore;
	TextView txtViewBat;
	TextView txtViewBowl;
	TextView txtViewBall;
	
	Button buttons[];
	String buttonStrings[]={"0","1","2","3","4","-4","6","wd","nb","","","W"};
	Button btnViewFullScorecard;
	
	private final int nextBatsmanNameReqCode=100;
	private final int curBowlerNameReqCode=300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scorer);
		
		Intent intent = getIntent();
		scoreCard=new ScoreCard(intent.getStringExtra("bat"), intent.getStringExtra("bow"));
		
		txtViewScore=(TextView) findViewById(R.id.textViewScore);
		txtViewBat=(TextView) findViewById(R.id.textViewBat1);
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
		
		buttons=new Button[12];
		
		buttons[0] = (Button) findViewById(R.id.button0);
		buttons[1] = (Button) findViewById(R.id.button1);
		buttons[2] = (Button) findViewById(R.id.button2);
		buttons[3] = (Button) findViewById(R.id.button3);
		buttons[4] = (Button) findViewById(R.id.button4);
		buttons[5] = (Button) findViewById(R.id.buttonNeg4);
		buttons[6] = (Button) findViewById(R.id.button6);
		buttons[7] = (Button) findViewById(R.id.buttonWide);
		buttons[8] = (Button) findViewById(R.id.buttonNB);
		buttons[9] = (Button) findViewById(R.id.buttonPlus);
		buttons[10] = (Button) findViewById(R.id.buttonDel);
		buttons[11] = (Button) findViewById(R.id.buttonWicket);
		
		for(int i=0;i<7;i++){
			final int p[]={i,0};
			buttons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					safeSave();
					if(scoreCard.isNewOverNext){
						onNewOver();
					}
					String bs=buttonStrings[p[0]];
					BowlEvent bowlEvent=new BowlEvent(EventType.Run, Integer.parseInt(bs), bs);
					scoreCard.addBowlEvent(bowlEvent);
					
					updateTextViews();
				}
			});
		}
		
		for(int i=7;i<=8;i++){
			final int q[]={i,0};
			buttons[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					safeSave();
					if(scoreCard.isNewOverNext){
						onNewOver();
					}
					BowlEvent bowlEvent=new BowlEvent(EventType.Extra, 2, buttonStrings[q[0]]);
					scoreCard.addBowlEvent(bowlEvent);
					
					updateTextViews();
				}
			});
		}
		
		buttons[9].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				safeSave();
				String parts[]=scoreCard.getCurrentOverstring().split(" ");
				if(parts.length==0)
					return;
				
				String lastBallString=parts[parts.length-1];
				if(lastBallString.endsWith("wd") ||
						lastBallString.endsWith("nb")){
					onPlusExtraRuns();
					updateTextViews();
				}
			}
		});

		buttons[10].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				safeSave();
				BowlEvent bowlEvent=new BowlEvent(EventType.DeletePrev, 0, buttonStrings[10]);
				scoreCard.addBowlEvent(bowlEvent);
				
				updateTextViews();
			}
		});

		buttons[11].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				safeSave();
				if(scoreCard.isNewOverNext){
					onNewOver();
				}
				//setNextBatsman
				Intent intent=new Intent(ScorerActivity.this, PlayerNameInputActivity.class);
				intent.putExtra("hint", "Batsman Name Next to Come");
				startActivityForResult(intent, nextBatsmanNameReqCode);
			}
		});
	}

	/*@Override
	public void onBackPressed() {
		safeSave();
		super.onBackPressed();
	}*/
	
	private void safeSave() {
		saveToFile(getApplicationContext(), "backup.scorecard");
	}

	private void onPlusExtraRuns() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Additional Runs : ");

		// Set up the input
		final EditText editTextInput = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		editTextInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(editTextInput);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	String input=editTextInput.getText().toString();
		    	if(input==null || input.length()==0)
		    		return; 
		        scoreCard.plusAdditionalRuns(Integer.parseInt(input));
		        updateTextViews();
		    }
		});
		builder.setNeutralButton("-4",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						scoreCard.plusAdditionalRuns(-4);
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
		txtViewBat.setText(scoreCard.getCurrentBatsman().toString());
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
		if(id == R.id.action_setCurBatsman){
			Intent intent=new Intent(ScorerActivity.this, PlayerNameInputActivity.class);
			intent.putExtra("hint", "Current Batsman Name");
			startActivityForResult(intent, nextBatsmanNameReqCode);
			return true;
		}
		if(id == R.id.action_setCurBowler){
			Intent intent=new Intent(ScorerActivity.this, PlayerNameInputActivity.class);
			intent.putExtra("hint", "Current Bowler Name");
			startActivityForResult(intent, curBowlerNameReqCode);
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
		if(requestCode==nextBatsmanNameReqCode){
			scoreCard.setNextBatsman(data.getStringExtra("name"));
			BowlEvent bowlEvent=new BowlEvent(EventType.Wicket, 0, buttonStrings[11]);
			scoreCard.addBowlEvent(bowlEvent);	
			updateTextViews();
		}
		else if(requestCode==curBowlerNameReqCode){
			scoreCard.setCurrentBowler(data.getStringExtra("name"));
			updateTextViews();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
