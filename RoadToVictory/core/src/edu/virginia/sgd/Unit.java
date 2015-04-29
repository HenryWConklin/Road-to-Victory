package edu.virginia.sgd;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Unit {
	
	private static final float MOVE_TIME = .5f;

	private static TileSet tileSet;
	private static int nextID;
	
	private int team;
	private Point pos;
	private int id;
	
	private Stack<Point> path;
	private Queue<Point> moveQueue; 
	private Grid grid;
	
	private float moveTimer;

	private boolean dead;
	
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
		dead = false;
		path = new Stack<Point>();
	}
	
	public Unit(Point p, int team, Grid grid) {
		pos = p;
		this.team = team;
		moveQueue = new ArrayDeque<Point>();
		this.grid = grid;
		moveTimer=0;
		id = nextID++;
		dead = false;
		path = new Stack<Point>();
	}

	public void update(float timePassed) {
		if (!dead) {
			
			moveTimer -= timePassed;
			
			if (moveTimer <= 0) {
				
				
				if (path.isEmpty()){
					if (!moveQueue.isEmpty()) {
						findPath(moveQueue.poll());
					}
				}
				
				if (!path.isEmpty()) {
					Point next = path.peek();
					if ((grid.isRoad(next) || (path.size()==1 && grid.getTile(next.x, next.y) == 11 + this.team)) 
							&& grid.getUnits()[next.x][next.y] == null) {
						grid.getUnits()[next.x][next.y] = this;
						grid.getUnits()[pos.x][pos.y] = null;
						this.pos = next;
						path.pop();
						moveTimer = MOVE_TIME;
					}
				}
				
			}
		}
		
	}
	
	private void findPath(Point dest) {
		Queue<Point> q = new ArrayDeque<Point>();
		Set<Point> visited = new HashSet<Point>();
		Map<Point,Point> parent = new HashMap<Point,Point>();
		q.add(pos);
		
		while (!q.isEmpty()) {
			Point curr = q.poll();
			if (visited.contains(curr)) 
				continue;
			visited.add(curr);
			
			if (curr.equals(dest))
				break;
			
			int[] vec = {1,0};
			for (int i = 0; i < 4; i++) {
				Point p = new Point(curr.x + vec[0], curr.y + vec[1]);
				if (!visited.contains(p) && (grid.isRoad(p) || (p.equals(dest) && grid.getTile(p.x, p.y) == 11 + this.team))) {
					q.add(p);
					parent.put(p, curr);
				}
				int temp = vec[0];
				vec[0] = -vec[1];
				vec[1] = temp;
			}
		}
		Point apDest = dest;
		// If the parent tree doesn't have the destination, find the closest in the tree
		if (!parent.containsKey(dest)) {
			int minWeight = pathWeight(pos, dest);
			apDest = pos;
			for (Point p : visited) {
				int weight = pathWeight(p, dest);
				if (weight < minWeight) {
					apDest = p;
					minWeight = weight;
				}
			}
		}
		
		Point curr = apDest;
		path = new Stack<Point>();
		while (!curr.equals(pos)) {
			path.add(curr);
			curr = parent.get(curr);
		}
		
	}
	
	private int pathWeight(Point p, Point dest) {
		if (!grid.isRoad(p.x,p.y) && !(p.equals(dest) && grid.getTile(p.x, p.y) == 11 + this.team)) {
			return Integer.MAX_VALUE;
		}
		return Math.abs(p.x-dest.x) + Math.abs(p.y-dest.y);
	}

	public void render(SpriteBatch sb) {
		sb.draw(tileSet.getTexture(getTeam()-1), pos.x * Grid.TILE_DIMENSION, pos.y * Grid.TILE_DIMENSION, Grid.TILE_DIMENSION, Grid.TILE_DIMENSION);
	}
	
	public int getTeam() {
		return team;
	}
	
	public Point getPos() {
		return pos;
	}
	
	public void kill() {
		dead = true;
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
		path.clear();
		moveQueue.add(p);
	}

}
