package edu.virginia.sgd;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class AIPlayer extends Player {

	private float moveTimer;
	private final float MOVE_TIME;	
	
	// Corner of current expansion target
	private Point expPoint;
	private List<Unit> units;

	private boolean spawnOverride;
	private Stack<Point> attackPath;
	
	public AIPlayer(Grid grid, int team, float time) {
		super(grid, team);
		MOVE_TIME = time;
		moveTimer = MOVE_TIME;	
		expPoint = null;
		units = null;
		spawnOverride = false;
		attackPath = new Stack<Point>();
	}
	
	// Array of points, represents the 4x4 border around an expansion location
	private static int[][] EXP_ROADS = {
		{-1,-1},
		{0,-1},
		{1,-1},
		{2,-1},
		{2,0},
		{2,1},
		{2,2},
		{1,2},
		{0,2},
		{-1,2},
		{-1,1},
		{-1,0}
	};

	@Override
	public void update(float timePassed) {
		
		
		moveTimer -= timePassed;
		if (moveTimer <= 0) {
			moveTimer = MOVE_TIME;
			
			units = getUnitsNotInHouses();
			
			// Avoid divide-by-zero, but game should end before this
			if (units.size() == 0)
				return;
			
			int eTerr = enemyTerritory();
			int eUnits = enemyUnits();
			int fTerr = friendlyTerritory();
			int fUnits = units.size();

			
			// If more enemies than friendlies, expand
			if (eUnits + 10 >= fUnits || fUnits < 10) {
				
				boolean build = eTerr > fTerr || spawnOverride;
				
				if (build) {
					if (expPoint == null) {
						findExpansion();
					}
				}
				
				// findExpansion will return null if there is no more available space, or at least no available space
				// near claimed roads. Expand if don't have "too much" territory, and aren't behind on units. Otherwise build units
				if (build && expPoint != null) {
					boolean allBuilt = true;
					int claimedInd = -1;
					for (int i = 0; i < EXP_ROADS.length; i++) {
						Point p = new Point(expPoint.x + EXP_ROADS[i][0], expPoint.y + EXP_ROADS[i][1]);
						
						if (allBuilt && grid.getTeam(p.x,p.y) != this.team) {
							allBuilt = false;
						}
						
						if (grid.getTeam(p.x, p.y) == this.team) {				
							claimedInd = i;
						}
						// Break at the end of the first chain of built, claimed roads
						else if (claimedInd!=-1) {
							break;
						}
					}
					
					// If no claimed roads, expansion is no longer valid
					if (claimedInd == -1) {
						expPoint = null;
					}
					// expansion is still valid
					else {
						int nextInd = (claimedInd+1)%EXP_ROADS.length;
						Point nextPt = new Point(expPoint.x+EXP_ROADS[nextInd][0], expPoint.y+EXP_ROADS[nextInd][1]);
						if (allBuilt) {
							expPoint = null;
						}
				
						// If next road is built, but not claimed, 
						else if (grid.isRoad(nextPt.x, nextPt.y)) {						
							Unit u = findClosestUnit(nextPt);
							u.move(nextPt);
						}
						// Otherwise, build road
						else {
							grid.build(nextPt, this.team);
						}
					}
					
					spawnOverride = false;
					
				}
				// Else build units
				else {
					List<Point> buildings = getFriendlyBuildings();
					if (buildings.size() > 0) {
						// Choose a random building
						int i = (int)(buildings.size()*Math.random());
						Point b = buildings.get(i);
						// If the house isn't currently filled, move a unit to it
						if (grid.getUnits()[b.x][b.y] == null)
							findClosestUnit(b).move(b);
						// Assume most houses are filled and do something else
						else 
							spawnOverride = true;
					}
					// If no houses, don't try to spawn units
					else {
						spawnOverride = true;
					}
				}
			}
			// If more friendlies than enemies, attack
			else {
				// If no current attack path, path to closest enemy
				if (attackPath.isEmpty()) {
					int i = (int)(units.size()*Math.random());
					Point pos = units.get(i).getPos();
				
					double minDist = Float.POSITIVE_INFINITY;
					Point closestEnemy = null;
					for (int r = 0; r < grid.getRows(); r++) {
						for (int c = 0; c < grid.getColumns(); c++) {
							if (grid.getUnits()[r][c] != null && grid.getUnits()[r][c].getTeam() != this.team) {
								double dist = grid.getUnits()[r][c].getPos().distance(pos);
								if (dist < minDist) {
									minDist = dist;
									closestEnemy = grid.getUnits()[r][c].getPos();
								}
							}
						}
					}
						
					findPath(pos, closestEnemy, false);
					while (attackPath.size() > 1)
						attackPath.pop();
					Point p = pos;
					if (attackPath.size() != 0)
						p = attackPath.pop();
					
					// If can path without building roads
					if (p.equals(closestEnemy)) {
						attackPath.push(closestEnemy);
					}
					else {
						findPath(p, closestEnemy, true);
					}
				}
				
				
				if (attackPath.size() == 1 || !grid.build(attackPath.peek(), this.team)) {
					for(Unit u : units) {
						u.move(attackPath.peek());
					}
					if (attackPath.size() == 1) {
						attackPath.pop();
					}
				}
				else if (attackPath.size() > 0){
					attackPath.pop();
				}
				
			}
		}
	}
	
	private void findPath(Point pos, Point dest, boolean direct) {
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
				if (!visited.contains(p) && (grid.isRoad(p.x,p.y) || direct)) {
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
		attackPath = new Stack<Point>();
		while (!curr.equals(pos)) {
			attackPath.add(curr);
			curr = parent.get(curr);
		}
		
	}
	
	private int pathWeight(Point p, Point dest) {
		if (!grid.isRoad(p.x,p.y) && !(p.equals(dest) && grid.getTile(p.x, p.y) == 11 + this.team)) {
			return Integer.MAX_VALUE;
		}
		return Math.abs(p.x-dest.x) + Math.abs(p.y-dest.y);
	}
	
	private static final int[][][] EXP_LOCS = {
		// Right top
		{
			{1,-1},{2,-1},{1,0},{2,0}
		},
		// Right bottom
		{
			{1,0},{2,0},{1,1},{2,1}
		},
		// Bottom right
		{
			{0,1},{1,1},{0,2},{1,2}
		},
		// Bottom Left
		{
			{-1,1},{0,1},{-1,2},{0,2}
		},
		// Left top
		{
			{-2,1},{-1,1},{-2,0},{-1,0}
		},
		// Left bottom
		{
			{-2,0},{-1,0},{-2,-1},{-1,-1}
		},
		// Top left
		{
			{-1, 2}, {0,2},{-1,1},{0,1}
		},
		// Top right
		{
			{0,2},{1,2},{0,1},{1,1}
		}
	};
	
	private void findExpansion() {
		// Want to find an empty 2x2 area next to an owned road, closest to friendly units
		float minAvgDist = Float.POSITIVE_INFINITY;
		Point bestExp = null;
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getTeam(r, c) == this.team) {
					// EXP_LOCS is an array of arrays of points, each second level array is a set of points to check as empty
					// The first element in each second level array is the top left corner of the set of points
					for (int i = 0; i < EXP_LOCS.length; i++) {
						boolean valid = true;
						for (int j = 0; j < EXP_LOCS[i].length; j++) {
							if (grid.getTile(r+EXP_LOCS[i][j][0], c+EXP_LOCS[i][j][1]) != 0) {
								valid = false;
								break;
							}
						}
						if (valid) {
							float avgDist = averageUnitDistance(new Point(r+EXP_LOCS[i][0][0], c+EXP_LOCS[i][0][1]));
							if (avgDist < minAvgDist) {
								minAvgDist = avgDist;
								bestExp = new Point(r+EXP_LOCS[i][0][0], c+EXP_LOCS[i][0][1]);
							}
						}
					}
				}
			}
			expPoint = bestExp;
		}		
	}
	
	private float averageUnitDistance(Point p) {
		double total = 0;
		for (Unit u : units) {
			total += u.getPos().distance(p);
		}
		return (float)(total/units.size());
	}
	
	private Unit findClosestUnit(Point p) {
		double minDist = units.get(0).getPos().distance(p);
		Unit minUnit = units.get(0);
		for (int i = 1; i < units.size(); i++) {
			double dist = units.get(i).getPos().distance(p);
			if (dist < minDist) {
				minDist = dist;
				minUnit = units.get(i);
			}
		}
		
		return minUnit;
	}

	private int enemyTerritory() {
		int count = 0;
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getTeam(r, c) != 0 && grid.getTeam(r,c) != this.team) {
					count++;
				}
			}
		}
		return count;
	}
	
	private int enemyUnits() {
		int count = 0;
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getUnits()[r][c] != null && grid.getUnits()[r][c].getTeam() != this.team) {
					count++;
				}
			}
		}
		return count;
	}
	
	private int friendlyTerritory() {
		int count = 0;
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getTeam(r, c) != -1 && grid.getTeam(r,c) == this.team) {
					count++;
				}
			}
		}
		return count;
	}
	
	private List<Unit> getUnitsNotInHouses() {
		ArrayList<Unit> u = new ArrayList<Unit>();
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getUnits()[r][c] != null && grid.getUnits()[r][c].getTeam() == this.team && grid.getTile(r, c) != 11+this.team) {
					u.add(grid.getUnits()[r][c]);
				}
			}
		}
		return u;
	}
	
	/*
	private int friendlyUnits() {
		int count = 0;
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getUnits()[r][c] != null && grid.getUnits()[r][c].getTeam() == this.team) {
					count++;
				}
			}
		}
		return count;
	}*/
}
