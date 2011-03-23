package com.lakesh.ui;

import com.lakesh.R;

import com.lakesh.constants.Coin;
import com.lakesh.constants.Engine;
import com.lakesh.core.Cell;
import com.lakesh.core.Coordinate;
import com.lakesh.core.GameEngine;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;


public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	
	private Coordinate boardSquareCoordinates[][];
	private boolean moveMade = false;
	private int turn;
	private GameEngine reversi;
	private Main main;
	private Thread gameThread;
	private Bitmap white, black;
	private int canvasWidth = 0;
	private int canvasHeight = 0;
	private int scoreBoardHeight = 120;
	private boolean gameEnded = false;
	private String winner = "";
	private Dialog saveScore;
	private StatusHandler statusHandler;
	private android.view.View.OnClickListener clickListener;

	public GameCanvas(Context context) {
		super(context);		
		getHolder().addCallback(this);
		this.main = (Main)context;
	}

	private void initializeComponents() {
		reversi = new GameEngine();		
		initializeBoard();		
		black = BitmapFactory.decodeResource(getResources(), R.drawable.black);
		white = BitmapFactory.decodeResource(getResources(), R.drawable.white);
		statusHandler = new StatusHandler(this);
		gameThread = new Thread(this);
		gameThread.setName("Othello");
		turn = Coin.WHITE;
		gameThread.start();		
	}

	private void initializeBoard() {
		boardSquareCoordinates = new Coordinate[8][8];
		int horizontal_incr = canvasWidth / 8;
		int vertical_incr = canvasHeight / 8;
		int horizontal_start = 0;
		int vertical_start = 0;
		int i, j = 0;

		for (i = 0; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				Point a = new Point(horizontal_start, vertical_start);
				Point b = new Point(horizontal_start + horizontal_incr,
						vertical_start);
				Point c = new Point(horizontal_start + horizontal_incr,
						vertical_start + vertical_incr);
				Point d = new Point(horizontal_start, vertical_start
						+ vertical_incr);
				try {
					boardSquareCoordinates[i][j] = new Coordinate(a, b, c, d);
				} catch (Exception e) {
					Log.i("error", e.getMessage());
				}
				horizontal_start += horizontal_incr;
			}
			horizontal_start = 0;
			vertical_start += vertical_incr;

		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Canvas canvas = null;
		Log.i("debug", "Drawing the surface");

		try {

			canvas = this.getHolder().lockCanvas(null);
			synchronized (this.getHolder()) {
				this.canvasWidth = canvas.getWidth();
				this.canvasHeight = canvas.getHeight() - scoreBoardHeight;
				initializeComponents();
				doDraw(canvas);
				updateScores(canvas);
				drawContent(canvas);				
			}
		} finally {

			if (canvas != null) {
				this.getHolder().unlockCanvasAndPost(canvas);

			}
		}

	}

	private void doDraw(Canvas canvas) {		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLUE);
		canvas.drawPaint(paint);

		int width = this.canvasWidth;
		int height = this.canvasHeight;
		Log.i("debug", "Dodraw" + canvasWidth);
		Log.i("debug", "Dodraw" + canvasHeight);

		int xpos = width / 8;
		int ypos = height / 8;
		
		paint.setColor(Color.WHITE);
		for (int i = 0; i < 7; i++) {			
			canvas.drawLine(xpos + (xpos * i), 0, xpos + (xpos * i), height,
					paint);		
		}
		
		for (int i = 0; i < 7; i++) {		
			canvas.drawLine(0, (ypos * i) + ypos, width, (ypos * i) + ypos,
					paint);

		}
	}

	private Cell findposition(int x, int y) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (checkmatch(boardSquareCoordinates[i][j], x, y) == true) {
					return new Cell(i, j);
				}
			}
		}
		return null;
	}

	private boolean checkmatch(Coordinate position, int x, int y) {
		if ((x > position.getPoint(0).x && y > position.getPoint(0).y)
				&& (x < position.getPoint(2).x && y < position.getPoint(2).y)) {
			return true;
		} else {
			return false;
		}

	}

	public void setmovemade() {
		moveMade = true;
	}
	
	public void passMove() {
		if(isMovePassAllowed()) {
			setmovemade();
		}
	}

	public boolean isMovePassAllowed() {
		return reversi.ispassallowed();
	}

	public void reset() {
		reversi.resetBoard();
		turn = Coin.WHITE;
		gameEnded = false;
		winner = "";
		drawBoard();
		if(!gameThread.isAlive()) {
			gameThread.start();
		}
	}

	public void flipCells(int x, int y, int player) {
		int i, j;
		i = x;
		j = y;
		if (reversi.board[i][j + 1] != player) {

			j++;
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				j++;
			}
			if (reversi.board[i][j] == player) {
				while (j != y) {
					j--;
					reversi.board[i][j] = player;
				}
			}
		}
		i = x;
		j = y;
		if (reversi.board[i][j - 1] != player) {
			j--;
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				j--;
			}

			if (reversi.board[i][j] == player) {
				while (j != y) {
					j++;
					reversi.board[i][j] = player;
				}
			}
		}

		i = x;
		j = y;
		if (reversi.board[i + 1][j - 1] != player) {

			i++;
			j--;
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				i++;
				j--;
			}
			if (reversi.board[i][j] == player) {
				while (i != x && j != y) {
					j++;
					i--;
					reversi.board[i][j] = player;
				}
			}
		}
		i = x;
		j = y;
		if (reversi.board[i - 1][j - 1] != player) {

			i--;
			j--;
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				i--;
				j--;
			}
			if (reversi.board[i][j] == player) {
				while (i != x && j != y) {
					j++;
					i++;
					reversi.board[i][j] = player;
				}
			}

		}
		i = x;
		j = y;
		if (reversi.board[i + 1][j + 1] != player) {

			i++;
			j++;
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				i++;
				j++;
			}
			if (reversi.board[i][j] == player) {
				while (i != x && j != y) {
					j--;
					i--;
					reversi.board[i][j] = player;
				}
			}

		}
		i = x;
		j = y;
		if (reversi.board[i - 1][j + 1] != player) {

			i--;
			j++;
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				i--;
				j++;
			}
			if (reversi.board[i][j] == player) {
				while (i != x && j != y) {
					j--;
					i++;
					reversi.board[i][j] = player;
				}
			}
		}
		i = x;
		j = y;
		if (reversi.board[i - 1][j] != player) {

			i--;
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				i--;
			}
			if (reversi.board[i][j] == player) {
				while (i != x) {
					i++;
					reversi.board[i][j] = player;
				}
			}
		}

		i = x;
		j = y;
		if (reversi.board[i + 1][j] != player) {
			i++;			
			while (reversi.board[i][j] != -1 && reversi.board[i][j] != player) {
				if (reversi.board[i][j] == 0) {
					break;
				}
				i++;
			}
			if (reversi.board[i][j] == player) {
				while (i != x) {
					i--;
					reversi.board[i][j] = player;
				}
			}
		}
	}

	public void run() {
		initializeBoard();
		while (true) {
			if (reversi.checkDraw() == true) {
				if (reversi.getWinner() == Coin.BLACK) {
					gameEnded = true;
					winner = "Winner is BLACK";					
				} else if (reversi.getWinner() == Coin.WHITE) {
					gameEnded = true;
					winner = "Winner is WHITE";					
				} else {
					gameEnded = true;
					winner = "It's a draw";					
				}
				drawBoard();
				statusHandler.sendEmptyMessage(0);
				
				break;
			} else {
				if (turn == Coin.WHITE) {
					
					while (moveMade == false) {
						
					}
					reversi.display();					
					turn = Coin.BLACK;					
					moveMade = false;
					drawBoard();

				} else if (turn == Coin.BLACK) {					
					Cell move = reversi.computerMove();
					if (move == null) {
						
						turn = Coin.WHITE;
					} else {
						reversi.board[move.getx()][move.gety()] = Coin.BLACK;
											
						flipCells(move.getx(), move.gety(),
										Coin.BLACK);
												
						reversi.increaseNoOfMoves();
						turn = Coin.WHITE;						
						reversi.display();
					}
					drawBoard();
				}

			}

		}
	}

	private void drawContent(Canvas canvas) {
		Log.i("debug", "Inside draw content");
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (reversi.board[i][j] == Coin.BLACK) {
					Log.i("debug", "BLACK");
					drawCross(canvas, new Cell(i - 1, j - 1));
				} else if (reversi.board[i][j] == Coin.WHITE) {
					Log.i("debug", "WHITE");
					drawCircle(canvas, new Cell(i - 1, j - 1));
				}
			}
		}

	}

	private void drawCircle(Canvas canvas, Cell cell) {		
		Paint paint = new Paint();
		int xOffset = (int) (((canvasWidth) / 8 - Coin.DIMENSION) / 2);
		int yOffset = (int) (((canvasHeight) / 8 - Coin.DIMENSION) / 2);
		Point a = boardSquareCoordinates[cell.getx()][cell.gety()].getPoint(0);
		canvas.drawBitmap(white, a.x + xOffset, a.y + yOffset, paint);
		Log.i("debug", String.valueOf(a.x) + String.valueOf(a.y));
	}

	private void drawCross(Canvas canvas, Cell cell) {
		Paint paint = new Paint();
		int xOffset = (int) (((canvasWidth) / 8 - Coin.DIMENSION) / 2);
		int yOffset = (int) (((canvasHeight) / 8 - Coin.DIMENSION) / 2);
		Point a = boardSquareCoordinates[cell.getx()][cell.gety()].getPoint(0);	
		canvas.drawBitmap(black, a.x + xOffset, a.y + yOffset, paint);
		Log.i("debug", String.valueOf(a.x) + String.valueOf(a.y));
	}

	private void updateScores(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);		    
		paint.setColor(Color.WHITE);	
		paint.setTextSize(20);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
				 Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		Rect rect = new Rect(1, canvasHeight, canvasWidth, canvasHeight + scoreBoardHeight);		
		canvas.drawRect(rect, paint);
		paint.setColor(Color.BLACK);		
		canvas.drawText("White : " + reversi.countWhite(), 15, canvasHeight + 15, paint);
		canvas.drawText("Black : " + reversi.countBlack(), 115,
				canvasHeight + 15, paint);
		if(gameEnded == true) {
			canvas.drawText(winner, 15, canvasHeight + 40, paint);
		} else {
			if(turn == Coin.BLACK) {
				canvas.drawText(" I am thinking !!!!", 15, canvasHeight + 40, paint);
			} else {
				canvas.drawText(" Your turn !!!!", 15, canvasHeight + 40, paint);
			}
		}
	}
	
	private void updateTurn(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);		    
		paint.setColor(Color.WHITE);	
		paint.setTextSize(10);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
				 Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));				
		
		paint.setColor(Color.BLACK);
		
	}
	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void MouseResponse(int x, int y) {
		if (turn == Coin.WHITE) {
			Cell position = findposition(x, y);
			if (position != null) {
				// logger.info(position.getx() + " " + position.gety());
				if (reversi.checkmovevalidity(reversi.board,
						position.getx() + 1, position.gety() + 1, Coin.WHITE) == true) {
					position.setx(position.getx() + 1);
					position.sety(position.gety() + 1);
					reversi.board[position.getx()][position.gety()] = Coin.WHITE;
					flipCells(position.getx(), position.gety(),
							Coin.WHITE);
					drawBoard();
					reversi.increaseNoOfMoves();
					// logger.info("Setting move made to true");
					moveMade = true;
					// logger.info("The movemade value is " + movemade);
				} else {
					Log.i("debug", "Invalid move");

				}
			}
		}

	}	
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("debug", "Inside mouse motion");
		MouseResponse((int) event.getX(), (int) event.getY());
		return super.onTouchEvent(event);
	}
	
	private void drawBoard() {
		Canvas canvas = null;
		try {

			canvas = this.getHolder().lockCanvas(null);
			synchronized (this.getHolder()) {
				doDraw(canvas);				
				drawContent(canvas);
				updateScores(canvas);				
			}
		} finally {

			if (canvas != null) {
				this.getHolder().unlockCanvasAndPost(canvas);

			}
		}
	}
	
	private void initializeSaveScoreDialog() {
		saveScore = new Dialog(this.getContext());
		saveScore.setTitle("Save your score");
		saveScore.setContentView(R.layout.save);
		final Button save = (Button)saveScore.findViewById(R.id.save);
		final Button cancel = (Button)saveScore.findViewById(R.id.cancel);
		final EditText name = (EditText)saveScore.findViewById(R.id.name);
		clickListener = new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if( save.getId() == ((Button)v).getId() ){
					Log.i("debug", "Inside the save");
					main.saveScore(name.getText().toString(), reversi.countBlack(),reversi.countBlack());					
					saveScore.dismiss();					
			     } else if( cancel.getId() == ((Button)v).getId() ){
			    	 saveScore.dismiss();
			     }
				// 
				
			}
			
		};
		save.setOnClickListener(clickListener);
		cancel.setOnClickListener(clickListener);
		saveScore.show();
	}
	
	private static class StatusHandler extends Handler {
		
		private GameCanvas gameCanvas;
		
		public StatusHandler(GameCanvas gameCanvas) {
			this.gameCanvas = gameCanvas;
		}
		
		public void handleMessage(Message msg) {
			gameCanvas.initializeSaveScoreDialog();
			Log.i("debug","inside handler");		
			super.handleMessage(msg);			
			
		}
		
	}

	
	
	
}
