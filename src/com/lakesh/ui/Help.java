package com.lakesh.ui;

import com.lakesh.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;

public class Help extends Activity {
	
	private TextView help;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.help);
		help = (TextView) findViewById(R.id.help);	
		help.setText("This is the help page");
    }   
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.game_menu, menu);
    }
    
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.removeItem(R.id.help);
    	menu.removeItem(R.id.about);
    	menu.removeItem(R.id.high_score);    	
    	return true;    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    	
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {        
	        case R.id.back:	        	
	        	finish();            
	            return true;
	        case R.id.exit:	        	
	        	finish();            
	            return true;	        
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
}