package edu.virginia.sgd;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Unit {
	
	private static final float MOVE_TIME = .5f;

	private static TileSet tileSet;
	private static int nextID;
	
	private int team;
	private Point pos;
	private int id;
	
	private Queue<Point> moveQueue; 
	private Grid grid;
	
	private float moveTimer;
	
	static {
		tileSet = new TileSet("units.png", 150, 150);
		nextID = 0;
	}

	public Unit(int x, int y, int team, Grid grid) {
		this.team = team;
		pos = new Point(x,y);
		moveQueue = new ArrayDeque<Point>();
		this.grid = grid;
		moveTimer=0;
		id = nextID++;
	}
	
	public Unit(Point p, int team, Grid grid) {
		pos = p;
		this.team = team;
		moveQueue = new ArrayDeque<Point>();
		this.grid = grid;
		moveTimer=0;
		id = nextID++;
	}

	public void update(float timePassed) {
		// If at destination 
		if (pos.equals(moveQueue.peek())) {
			moveQueue.poll();			
		}
		
		// Timer to delay movement
		moveTimer -= timePassed;
		if (moveTimer < 0) {
			moveTimer = 0;
		}
		
		if (moveTimer <= 0 && moveQueue.peek()!=null) {
			Point dest = moveQueue.peek();
			int currWeight = pathWeight(pos, dest);
			
			int minWeight=currWeight;
			Point minPoint = pos;
			
			// Iterate over four directions
			Point dir = new Point(0,1);
			for (int i = 1; i <=4; i++) {
				Point curr = new Point(pos.x + dir.x, pos.y + dir.y);
				int weight = pathWeight(curr, dest);
				if (weight < minWeight) {
					minWeight = weight;
					minPoint = curr;
				}
				dir = new Point(dir.y, -dir.x); 
			}
			
			// If a point closer to the destination was found
			if (minPoint!=pos) {
				// If the destination point is unoccupied
				Unit[][] units = grid.getUnits();
				if (units[minPoint.x][minPoint.y] == null) {
					// Move to point
					units[minPoint.x][minPoint.y] = this;
					units[pos.x][pos.y] = null;
					pos = minPoint;
					moveTimer = MOVE_TIME;
				}
			}
			
		}
		
	}
	
	private int pathWeight(Point p, Point dest) {
		if (!grid.isRoad(p.x,p.y) && !(p.equals(dest) && grid.getTile(p.x, p.y) == 11 + this.team)) {
			return Integer.MAX_VALUE;
		}
		return Math.abs(p.x-dest.x) + Math.abs(p.y-dest.y);
	}

	public void render(SpriteBatch sb) {
		sb.draw(tileSet.getTexture(getTeam()), pos.x * Grid.TILE_DIMENSION, pos.y * Grid.TILE_DIMENSION, Grid.TILE_DIMENSION, Grid.TILE_DIMENSION);
	}
	
	public int getTeam() {
		return team;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Unit) {
			Unit u = (Unit) o;
			return u.id == this.id;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	public void queueMove(Point p) {
		moveQueue.add(p);
	}
	
	public void move(Point p) {
		moveQueue.clear();
		moveQueue.add(p);
	}

}
