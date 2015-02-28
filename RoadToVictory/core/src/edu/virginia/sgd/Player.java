package edu.virginia.sgd;

import java.util.ArrayList;
import java.util.List;


// Abstract base class for human and AI players
public abstract class Player {
	
	//Reference to global game object list
	protected List<GameObject> gameObjs;
	//Reference to global grid
	protected Grid grid;
	//Team ID
	protected int team;
	
	public Player(List<GameObject> gameObjs, Grid grid, int team) {
		this.gameObjs = gameObjs;
		this.grid = grid;
		this.team = team;
	}
	
	public abstract void update(float timePassed);
	
	public List<GameObject> getOwnedGameObjects() {
		ArrayList<GameObject> onTeam = new ArrayList<GameObject>();
		for (GameObject g: gameObjs) {
			if (g.getTeam() == this.team)
				onTeam.add(g);
		}
		return onTeam;
	}
}
