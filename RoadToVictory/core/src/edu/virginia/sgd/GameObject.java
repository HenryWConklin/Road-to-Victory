package edu.virginia.sgd;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameObject {
	protected Point pos;
	
	public GameObject(int x, int y) {
		pos = new Point(x,y);
	}
	
	public GameObject(Point p) {
		// Copy the point rather than the reference
		pos = new Point(p);
	}
	
	public abstract void update(float timePassed);
	public abstract void render(SpriteBatch sb);
}
