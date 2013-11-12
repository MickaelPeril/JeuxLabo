package com.example.jeuxlabo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button button1;//boutton start
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        button1 = (Button) findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() { //click sur le button start
        	
        	public void onClick(View v) {    		
        		//lancement de la nouvelle page ( class Start)
        		Intent intent = new Intent(MainActivity.this, Start.class);
        		startActivity(intent);
        	}
        });
					
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void optionOnClick(View view)
	{
		
		
		Intent intent;
		intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	

}
