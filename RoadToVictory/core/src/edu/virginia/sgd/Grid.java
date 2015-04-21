package edu.virginia.sgd;

import java.awt.Point;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Grid {
	public static final int TILE_DIMENSION = 150;
	public static final float SPAWN_TIME = 5f;
	
	private int[][] board;
	private Unit[][] units;
	private int[][] team;
	//Maps ID to texture
	private TileSet tiles;
	
	private float spawnTimer;
	
	public Grid(int xdim, int ydim, Texture tex){
		if (xdim == 0 || ydim == 0)
			throw new IllegalArgumentException("Grid size cannot be 0.");
		this.board = new int[xdim][ydim];
		this.tiles = new TileSet("tileset.png", TILE_DIMENSION, TILE_DIMENSION);
		units = new Unit[xdim][ydim];
		team = new int[xdim][ydim];
		spawnTimer = SPAWN_TIME;
		
		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 5; c++) {
				team[r][c]=1;
			}
		}
		for (int r = 5; r < 10; r++) {
			for (int c = 0; c < 5; c++) {
				team[r][c]=2;
			}
		}
		for (int r = 10; r < 15; r++) {
			for (int c = 0; c < 5; c++) {
				team[r][c]=4;
			}
		}
	}
	
	private void buildHouses() {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (getTile(r,c) == 0 || getTile(r,c) >= 12) {
					// Count neighboring roads
					int team = 0;
					int closeRoads = 0;
					if (getTile(r+1,c) >= 1 && getTile(r+1,c) <= 11) {
						closeRoads++;
						team = getTeam(r+1,c);
					}
					if (getTile(r-1,c) >= 1 && getTile(r-1,c) <= 11) {
						closeRoads++;
						if (team==-1) {
							team = getTeam(r-1,c);
						}
						if (getTeam(r-1,c) != team) {
							team = 0;
						}
					}
					if (getTile(r,c+1) >= 1 && getTile(r,c+1) <= 11) {
						closeRoads++;
						if (team == -1) {
							team = getTeam(r,c+1);
						}
						if (getTeam(r,c+1) != team) {
							team = 0;
						}
					}
					if (getTile(r,c-1) >= 1 && getTile(r,c-1) <= 11) {
						closeRoads++;
						if (team == -1) {
							team = getTeam(r,c-1);
						}
						if (getTeam(r,c-1) != team) {
							team = 0;
						}
					}
					
					if (closeRoads >= 2 && team > 0) {
						board[r][c] = 11 + team;
					}
					else {
						board[r][c] = 0;
					}
				}
				
			}
		}
	}
	
	private void spawnUnits(float timePassed) {
		spawnTimer -= timePassed;
		
		
		if (spawnTimer <= 0) {
			for (int r = 0; r < board.length; r++) {
				for (int c = 0; c < board[r].length; c++) {
					if (getTile(r,c) >= 12 & getTile(r,c) <= 14) {
						if (units[r][c] == null) {
							units[r][c] = new Unit(r, c, getTile(r,c)-11, this);
						}
					}
				}
			}
			
			spawnTimer = SPAWN_TIME;
		}
	}
	
	private void updateUnits(float timePassed) {
		for (int r = 0; r < units.length; r++) {
			for (int c = 0; c < board[r].length; c++){
				if (units[r][c] != null) {
					// Change team first, because unit may move
					team[r][c] = units[r][c].getTeam();
					units[r][c].update(timePassed);
				}
			}
		}
	}
	
	public void update(float timePassed){
		buildHouses();
		spawnUnits(timePassed);
		updateUnits(timePassed);
	}
	
	public void render(SpriteBatch sb){
		// Assumes sp.begin() has already been called
		for (int i = 0; i < this.board.length; i++){
			for (int j = 0; j < this.board[i].length; j++){
				int xpos = TILE_DIMENSION * i;
				int ypos = TILE_DIMENSION * j;
				sb.draw(this.tiles.getTexture(board[i][j]), xpos, ypos, TILE_DIMENSION, TILE_DIMENSION);
			}
		}
		for (int i = 0; i < this.units.length; i++){
			for (int j = 0; j < this.units[i].length; j++){
				if (units[i][j] != null) {
					units[i][j].render(sb);
				}
			}
		}
	}
	
	// Returns width in world coordinates
	public int getWidth() {
			return board.length * TILE_DIMENSION;
	}
	
	// Returns height in world coordinates
	public int getHeight() {
		return board[0].length * TILE_DIMENSION;
	}
	
	
	public Point worldToGridCoords(Point screen) {
		
		int gridX = (screen.x) / TILE_DIMENSION;
		int gridY = (screen.y) / TILE_DIMENSION;
		
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
	
	public int getTeam(int x, int y) {
		if (x < 0 || x >= team.length || y < 0 || y >= team[0].length) 
			return -1;
		
		return team[x][y];
	}
	
	public boolean isRoad(int x, int y) {
		int tile = getTile(x,y);
		
		return tile > 0 && tile < 12;
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
	

	public Unit[][] getUnits() {
		return units;
	}
}
