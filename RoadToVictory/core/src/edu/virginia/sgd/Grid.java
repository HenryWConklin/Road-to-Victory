package edu.virginia.sgd;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Grid {
	public static final int TILE_DIMESION = 50;
	
	private int[][] board;
	//Maps ID to texture
	private HashMap<Integer, Texture> tex;
	private Point offset;
	
	public Grid(int xdim, int ydim, Texture tex){
		this.board = new int[xdim][ydim];
		this.tex = new HashMap<Integer, Texture>();
		this.tex.put(0, tex);
		this.offset = new Point();
	}
	
	public void update(long timePassed){}
	
	public void render(SpriteBatch sb){
		// Assumes sp.begin() has already been called
		for (int i = 0; i < this.board.length; i++){
			for (int j = 0; j < this.board[i].length; j++){
				int xpos = TILE_DIMESION * i + this.offset.x;
				int ypos = TILE_DIMESION * j + this.offset.y;
				sb.draw(this.tex.get(board[i][j]), xpos, ypos, TILE_DIMESION, TILE_DIMESION);
			}
		}
	}
	
	public Point getOffset() {
		return offset;
	}
	
	public void setOffset(int x, int y) {
		offset.setLocation(x, y);
	}
	
	public void translateOffset(int dx, int dy) {
		offset.translate(dx, dy);		
	}
	
	public Point screenToGridCoords(Point screen) {
		int gridX = (screen.x - this.offset.x) / TILE_DIMESION;
		int gridY = (screen.y - this.offset.y) / TILE_DIMESION;
		return new Point(gridX,gridY);
	}
}
