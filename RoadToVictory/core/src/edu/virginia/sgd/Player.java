package edu.virginia.sgd;


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
	
}
