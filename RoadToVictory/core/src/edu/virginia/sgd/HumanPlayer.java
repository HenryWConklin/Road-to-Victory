package edu.virginia.sgd;

import java.awt.Point;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;

public class HumanPlayer extends Player {
	
	private static final int SCROLL_SPEED = 1000;
	private static final int EDGE_WIDTH = 50;
	
	// Last location that a mouse button was pressed
	private Point mouseDownPos;
	
	// Current mouse button that is down (0=none, 1=left, 2=right)
	private int buttonPressed;

	public HumanPlayer(List<GameObject> gameObjs, Grid grid, int team) {
		super(gameObjs, grid, team);

	}

	@Override
	public void update(float timePassed) {
		Point mousePos = new Point(Gdx.input.getX(), Gdx.input.getY());
		
		// Do edge scrolling
		if (mousePos.x <= EDGE_WIDTH) {
			grid.translateOffset((int)(SCROLL_SPEED * timePassed), 0);
		}
		else if (mousePos.x >= Gdx.graphics.getWidth()-EDGE_WIDTH) {
			grid.translateOffset((int)(-SCROLL_SPEED * timePassed), 0);
		}
		
		if (mousePos.y <= EDGE_WIDTH) {
			grid.translateOffset(0, (int)(-SCROLL_SPEED * timePassed));
		}
		else if (mousePos.y >= Gdx.graphics.getHeight()-EDGE_WIDTH) {
			grid.translateOffset(0, (int)(SCROLL_SPEED * timePassed));
		}
		
		//// Mouse button down
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			grid.build(grid.screenToGridCoords(mousePos));
		}
		else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			grid.destroy(grid.screenToGridCoords(mousePos));
		}
		
		//// Setup for mouse drags
		if (buttonPressed == 0) {
			if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
				mouseDownPos = mousePos;
				buttonPressed = 1;
			}
			else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				mouseDownPos = mousePos;
				buttonPressed = 2;
			}
		}
		//// Mouse released
		else {
			if (buttonPressed == 1 && !Gdx.input.isButtonPressed(Buttons.LEFT)) {
				// TODO Left click released 
			}
			else if (buttonPressed == 2 && !Gdx.input.isButtonPressed(Buttons.RIGHT)){
				// TODO Right click released
			}
		}
	}

}
