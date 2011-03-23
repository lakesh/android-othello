package com.lakesh.ui;

import com.lakesh.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;

public class About extends Activity {
	
    private TextView about;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("debug", "inside the create");
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.about);
        about = (TextView) findViewById(R.id.about);	
		about.setText("This is the about page");        
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    	
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.removeItem(R.id.about);
    	menu.removeItem(R.id.help);
    	menu.removeItem(R.id.high_score);    	
    	return true;    	
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.back:
        	Log.i("info", "back");
        	startActivity(new Intent(this, Main.class));
        	finish();            
            return true;   
        case R.id.exit:
        	Log.i("info", "exit");        	
        	finish();            
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
    
}