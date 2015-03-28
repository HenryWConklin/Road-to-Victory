package edu.virginia.sgd;

import java.awt.Point;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class HumanPlayer extends Player {
	
	private static final int SCROLL_SPEED = 1000;
	private static final int EDGE_WIDTH = 50;
	
	// Last location that a mouse button was pressed
	private Point mouseDownPos;
	
	// Current mouse button that is down (0=none, 1=left, 2=right)
	private int buttonPressed;
	
	private OrthographicCamera camera;

	public HumanPlayer(List<GameObject> gameObjs, Grid grid, int team) {
		super(gameObjs, grid, team);
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		camera.position.x = 0;
		camera.position.y = 0;
	}

	@Override
	public void update(float timePassed) {
		// Mouse screen coordinates
		Point mousePos = new Point(Gdx.input.getX(), Gdx.input.getY());
		
		// Mouse world coordinates
		Vector3 mouseWorldVec = camera.unproject(new Vector3(mousePos.x, mousePos.y, 0));
		Point mouseWorld = new Point((int) mouseWorldVec.x, (int)mouseWorldVec.y);
		
		// Do edge scrolling
		if (mousePos.x <= EDGE_WIDTH) {
			camera.translate(-SCROLL_SPEED * timePassed, 0);
		}
		else if (mousePos.x >= Gdx.graphics.getWidth()-EDGE_WIDTH) {
			camera.translate(SCROLL_SPEED * timePassed, 0);
		}
		
		// Clip x coord
		if (camera.position.x < Gdx.graphics.getWidth()/2) {
			camera.position.x = Gdx.graphics.getWidth()/2;
		}
		else if (camera.position.x > grid.getWidth()-Gdx.graphics.getWidth()/2) {
			camera.position.x = grid.getWidth()-Gdx.graphics.getWidth()/2;
		}
		
		if (mousePos.y <= EDGE_WIDTH) {
			camera.translate(0, SCROLL_SPEED * timePassed);
		}
		else if (mousePos.y >= Gdx.graphics.getHeight()-EDGE_WIDTH) {
			camera.translate(0, -SCROLL_SPEED * timePassed);
		}
		
		// Clip y coord
		if (camera.position.y < Gdx.graphics.getHeight()/2) {
			camera.position.y = Gdx.graphics.getHeight()/2;
		}
		else if (camera.position.y > grid.getHeight()-Gdx.graphics.getHeight()/2) {
			camera.position.y = grid.getHeight()-Gdx.graphics.getHeight()/2;
		}
		
		//// Mouse button down
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			grid.build(grid.worldToGridCoords(mouseWorld));
		}
		else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			grid.destroy(grid.worldToGridCoords(mouseWorld));
		}
		
		//// Setup for mouse drags
		if (buttonPressed == 0) {
			if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
				mouseDownPos = mouseWorld;
				buttonPressed = 1;
			}
			else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				mouseDownPos = mouseWorld;
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
	
	public Camera getCamera() {
		return camera;
	}

}
