package com.lakesh.ui;

import com.lakesh.R;
import com.lakesh.db.DataHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;



public class Main extends Activity {
	
    GameCanvas gameCanvas;
    DataHelper dataHelper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCanvas = new GameCanvas(this);
        setContentView(gameCanvas);
        dataHelper = new DataHelper(this);        
        this.registerForContextMenu(gameCanvas);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.game_keys, menu);
    	super.onCreateContextMenu(menu, v, menuInfo);
     }
    
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    	
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
      case R.id.pass:
    	Log.i("debug", "pass selected");
    	gameCanvas.passMove();
        return true;
      case R.id.new_game:
      	Log.i("debug", "new game");
      	newGame();
        return true;   
      default:
        return super.onContextItemSelected(item);
      }
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.removeItem(R.id.back);    	    	
    	return true;    	
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.exit:
        	Log.i("info", "Exit");
            finish();   
            return true;
        case R.id.help:
        	Log.i("info", "Help");
        	startActivity(new Intent(this, Help.class));
        	finish();            
            return true;
        case R.id.about:
        	Log.i("info", "About");
        	startActivity(new Intent(this, About.class));
        	finish();            
            return true;
        case R.id.high_score:
        	Log.i("info", "High Score");
        	startActivity(new Intent(this, HighScore.class));
        	finish();        	
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
    
    private void newGame() {
    	gameCanvas.reset();   	    	
    }
    
    public void saveScore(String name, int black_score, int white_score) {
    	dataHelper.insert(name, black_score, white_score);
    }
   
}
