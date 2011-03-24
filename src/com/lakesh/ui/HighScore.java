package com.lakesh.ui;

import java.util.ArrayList;
import java.util.List;

import com.lakesh.R;
import com.lakesh.db.DataHelper;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HighScore extends Activity {
	
	private TextView help;
	private DataHelper dataHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.high_score);	
        populateData();
    }   
    
    
    private void populateData() {
    	TableLayout table = (TableLayout)findViewById(R.id.high_score_table);
    	dataHelper = new DataHelper(this);
    	List<List>highScore = new ArrayList<List>();
    	highScore = dataHelper.selectAll();
    	for(int i = 0; i < highScore.size(); i++) {
    		List<String> data = highScore.get(i);
    		TableRow row = new TableRow(this);
    		TextView  name = new TextView(this);
    		TextView  black_score = new TextView(this);
    		TextView  white_score = new TextView(this);
    		name.setText(data.get(0));
    		black_score.setText(data.get(1));
    		white_score.setText(data.get(2));    		
    		row.addView(name);
    		row.addView(black_score);
    		row.addView(white_score);
    		table.addView(row);
    	}
    	
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
	        	startActivity(new Intent(this, Main.class));
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