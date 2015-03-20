package edu.virginia.sgd;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Grid {
	public static final int TILE_DIMENSION = 150;
	
	private int[][] board;
	//Maps ID to texture
	private TileSet tiles;
	private Point offset;
	
	public Grid(int xdim, int ydim, Texture tex){
		if (xdim == 0 || ydim == 0)
			throw new IllegalArgumentException("Grid size cannot be 0.");
		this.board = new int[xdim][ydim];
		this.tiles = new TileSet("tileset.png", TILE_DIMENSION, TILE_DIMENSION);
		this.offset = new Point();
	}
	
	public void update(float timePassed){
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (getTile(r,c) == 0 || getTile(r,c) >= 12) {
					// Count neighboring roads
					int closeRoads = 0;
					if (getTile(r+1,c) >= 1 && getTile(r+1,c) <= 11)
						closeRoads++;
					if (getTile(r-1,c) >= 1 && getTile(r-1,c) <= 11)
						closeRoads++;
					if (getTile(r,c+1) >= 1 && getTile(r,c+1) <= 11)
						closeRoads++;
					if (getTile(r,c-1) >= 1 && getTile(r,c-1) <= 11)
						closeRoads++;
					
					if (closeRoads >= 2) {
						board[r][c] = 12;
					}
					else {
						board[r][c] = 0;
					}
				}
			}
		}
	}
	
	public void render(SpriteBatch sb){
		// Assumes sp.begin() has already been called
		for (int i = 0; i < this.board.length; i++){
			for (int j = 0; j < this.board[i].length; j++){
				int xpos = TILE_DIMENSION * i + this.offset.x;
				int ypos = TILE_DIMENSION * j + this.offset.y;
				sb.draw(this.tiles.getTexture(board[i][j]), xpos, ypos, TILE_DIMENSION, TILE_DIMENSION);
			}
		}
	}
	
	public Point getOffset() {
		return offset;
	}
	
	public void setOffset(int x, int y) {
		offset.setLocation(x, y);
		clipOffset();
	}
	
	public void translateOffset(int dx, int dy) {
		offset.translate(dx, dy);
		clipOffset();
	}
	
	private void clipOffset() {
		if (offset.x > 0) {
			offset.x = 0;
		}
		else if (offset.x < -board.length * TILE_DIMENSION+ Gdx.graphics.getWidth()) {
			offset.x = -board.length * TILE_DIMENSION+ Gdx.graphics.getWidth();
		}
		
		if (offset.y > 0) {
			offset.y = 0;
		}
		else if (offset.y < -board[0].length * TILE_DIMENSION + Gdx.graphics.getHeight()) {
			offset.y = -board[0].length * TILE_DIMENSION+ Gdx.graphics.getHeight();
			
		}
	}
	
	public Point screenToGridCoords(Point screen) {
		screen.y = Gdx.graphics.getHeight() - screen.y;
		int gridX = (screen.x - this.offset.x) / TILE_DIMENSION;
		int gridY = (screen.y - this.offset.y) / TILE_DIMENSION;
		
		return new Point(gridX,gridY);
	}
	
	public boolean build(Point p) {
		return build(p.x, p.y);
	}
	
	public boolean build(int x, int y) {
		if (getTile(x,y) != 0 && getTile(x,y) < 12) return false;
		board[x][y] = 1;
		
		fixTile(x,y);
		
		return true;
	}
	
	private void fixTile(int x, int y) {
		fixTile(x,y,true);
	}
	
	// Fixes road directions
	private void fixTile(int x, int y, boolean fixNeighbors) {
		if (getTile(x,y) < 1 || getTile(x,y) > 11)
			return;
		int n = getTile(x, y+1);
		int e = getTile(x+1, y);
		int s = getTile(x, y-1);
		int w = getTile(x-1, y);
		
		boolean nRoad = (n > 0 && n < 12);
		boolean eRoad = (e > 0 && e < 12);
		boolean sRoad = (s > 0 && s < 12);
		boolean wRoad = (w > 0 && w < 12);
		
		// East-west road
		if (!nRoad && !sRoad && (eRoad || wRoad)) {
			board[x][y] = 1;
		}
		// North-south road
		else if ((nRoad || sRoad) && !eRoad && !wRoad) {
			board[x][y] = 2;
		}
		// 4-way
		else if (nRoad && sRoad && eRoad && wRoad) {
			board[x][y] = 3;
		}
		// East-west-south
		else if (!nRoad && sRoad && eRoad && wRoad) {
			board[x][y] = 4;
		}
		// North-west-south
		else if (nRoad && sRoad && !eRoad && wRoad) {
			board[x][y] = 5;
		}
		// North-east-west
		else if (nRoad && !sRoad && eRoad && wRoad) {
			board[x][y] = 6;
		}
		// North-south-east
		else if (nRoad && sRoad && eRoad && !wRoad) {
			board[x][y] = 7;
		}
		// South-west
		else if (!nRoad && sRoad && !eRoad && wRoad) {
			board[x][y] = 8;
		}
		// North-west
		else if (nRoad && !sRoad && !eRoad && wRoad) {
			board[x][y] = 9;
		}
		// North-east
		else if (nRoad && !sRoad && eRoad && !wRoad) {
			board[x][y] = 10;
		}
		// South-east
		else if (!nRoad && sRoad && eRoad && !wRoad) {
			board[x][y] = 11;
		}
		
		if (fixNeighbors) {
			fixTile(x-1, y, false);
			fixTile(x+1, y, false);
			fixTile(x, y+1, false);
			fixTile(x, y-1, false);
		}
	}
	
	public int getTile(int x, int y) {
		if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) 
			return -1;
		else
			return board[x][y];
	}
	
	public boolean destroy(Point p) {
		return destroy(p.x,p.y);
	}
	
	public boolean destroy(int x, int y) { 
		if (getTile(x,y) == -1)
			return false;
		
		board[x][y] = 0;
		fixTile(x-1,y,false);
		fixTile(x+1,y,false);
		fixTile(x,y-1,false);
		fixTile(x,y+1,false);
		return true;
	}
}
