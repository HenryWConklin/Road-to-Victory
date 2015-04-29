package edu.virginia.sgd;

import java.awt.Point;
import java.util.List;

public class AIPlayer extends Player {
	
	// Safe attack threshold, below this ratio of enemy to friendly resources, AI will attack
	private final float EXP_THRESH = .75f;
	
	// Threshold ratio to enemy territory to limit expansion
	private final float BUILD_THRESH = 2f;
	// Danger threshold on enemy unit count
	private final float SPAWN_THRESH = .75f;
	

	private float moveTimer;
	private final float MOVE_TIME;	
	
	// Corner of current expansion target
	private Point expPoint;
	private List<Unit> units;

	private boolean spawnOverride;
	
	public AIPlayer(Grid grid, int team, float time) {
		super(grid, team);
		MOVE_TIME = time;
		moveTimer = MOVE_TIME;	
		expPoint = null;
		units = null;
		spawnOverride = false;
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
			
			units = getFriendlyUnits();
			
			// Avoid divide-by-zero, but game should end before this
			if (units.size() == 0)
				return;
			
			int eTerr = enemyTerritory();
			int eUnits = enemyUnits();
			int fTerr = friendlyTerritory();
			int fUnits = units.size();

			
			// If more enemies than friendlies, expand
			if (eUnits >= fUnits) {
				
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
				System.out.println("GRRR");
			}
		}
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
