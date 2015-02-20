package edu.virginia.sgd;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Grid {
	private int[][] board;
	private int tile_dimension = 50;
	private Texture tex;
	
	
	public Grid(int xdim, int ydim, Texture tex){
		this.board = new int[xdim][ydim];
		this.tex = tex;
	}
	
	public void update(long timePassed){}
	
	public void render(int offX, int offY, SpriteBatch sb){
		// Assumes sp.begin() has already been called
		for (int i = 0; i < this.board.length; i++){
			for (int j = 0; j < this.board[i].length; j++){
				int xpos = this.tile_dimension * i + offX;
				int ypos = this.tile_dimension * j + offY;
				sb.draw(this.tex, xpos, ypos, this.tile_dimension, this.tile_dimension);
			}
		}
	}
}
