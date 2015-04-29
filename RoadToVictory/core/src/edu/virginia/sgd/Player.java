package edu.virginia.sgd;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


// Abstract base class for human and AI players
public abstract class Player {
	
	//Reference to global grid
	protected Grid grid;
	//Team ID
	protected int team;
	
	public Player(Grid grid, int team) {
		this.grid = grid;
		this.team = team;
	}
	
	public abstract void update(float timePassed);
	
	public List<Unit> getFriendlyUnits() {
		ArrayList<Unit> units = new ArrayList<Unit>();
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getUnits()[r][c] != null && grid.getUnits()[r][c].getTeam() == this.team) {
					units.add(grid.getUnits()[r][c]);
				}
			}
		}
		return units;
	}
	
	public List<Point> getFriendlyBuildings() {
		ArrayList<Point> buildings = new ArrayList<Point>();
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getColumns(); c++) {
				if (grid.getTile(r, c) == 11+team) {
					buildings.add(new Point(r,c));
				}
			}
		}
		return buildings;
	}
	
}
