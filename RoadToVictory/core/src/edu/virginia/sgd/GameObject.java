package edu.virginia.sgd;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameObject {
	protected Point pos;
	protected int team;
	
	public GameObject(int x, int y, int team) {
		this.pos = new Point(x, y);
		this.team = team;
	}

	public GameObject(Point p, int team) {
		// Copy the point rather than the reference
		this.pos = new Point(p);
		this.team = team;
	}

	public abstract void update(float timePassed);

	public abstract void render(SpriteBatch sb);

	public Point getPos() {
		return pos;
	}
	
	public int getTeam() {
		return team;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public void setPos(int x, int y) {
		this.pos.setLocation(x, y);
	}

}
