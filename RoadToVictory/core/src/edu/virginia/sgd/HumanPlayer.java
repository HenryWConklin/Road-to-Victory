package edu.virginia.sgd;

import java.awt.Point;
import java.util.HashSet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class HumanPlayer extends Player {
	
	private static final int SCROLL_SPEED = 1000;
	private static final int EDGE_WIDTH = 50;
	
	private static final int STATE_SELECT = 0;
	private static final int STATE_BUILD = 1;
	
	// Control state
	private int state;
	
	// Last location that a mouse button was pressed
	private Point mouseDownPos;
	
	// Current mouse button that is down (0=none, 1=left, 2=right)
	private int buttonPressed;
	
	private HashSet<Unit> selection;
	
	private OrthographicCamera camera;

	public HumanPlayer(Grid grid, int team) {
		super(grid, team);
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		camera.position.x = 0;
		camera.position.y = 0;
		state = STATE_SELECT;
		selection = new HashSet<Unit>();
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
//		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
//			grid.build(grid.worldToGridCoords(mouseWorld));
//		}
//		else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
//			grid.destroy(grid.worldToGridCoords(mouseWorld));
//		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.B)) {
			state = STATE_BUILD;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			state = STATE_SELECT;
		}
		
		//// Setup for mouse drags / on mouse down
		if (buttonPressed == 0) {
			if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
				mouseDownPos = mouseWorld;
				buttonPressed = 1;
				
				if (state == STATE_BUILD) {
					grid.build(grid.worldToGridCoords(mouseWorld), team);
					if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
						state = STATE_SELECT;
					}
					// Stop a drag event
					buttonPressed = 0;
				}
			}
			else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				mouseDownPos = mouseWorld;
				buttonPressed = 2;
				
				if (state == STATE_SELECT) {
					moveSelection(grid.worldToGridCoords(mouseWorld));
				}
				else if (state == STATE_BUILD) {
					grid.destroy(grid.worldToGridCoords(mouseWorld), this.team);
					if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
						state = STATE_SELECT;
					}
					buttonPressed = 0;
				}
				
			}
		}
		//// Mouse released
		else {
			if (buttonPressed == 1 && !Gdx.input.isButtonPressed(Buttons.LEFT)) {
				
				buttonPressed = 0;
				if (state == STATE_SELECT) {
					// Select units in box
					Point gridDown = grid.worldToGridCoords(mouseDownPos);
					Point gridUp = grid.worldToGridCoords(mouseWorld);
					
					int minX,minY, maxX,maxY;
					
					if (gridDown.x < gridUp.x) {
						minX = gridDown.x;
						maxX = gridUp.x;
					}
					else {
						minX = gridUp.x;
						maxX = gridDown.x;
					}
					if (gridDown.y < gridUp.y) {
						minY = gridDown.y;
						maxY = gridUp.y;
					}
					else {
						minY = gridUp.y;
						maxY = gridDown.y;
					}
					
					// If not holding shift, clear selection before refilling
					if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
						selection.clear();
					}
											
					Unit[][] units = grid.getUnits();
					for (int x = minX; x <= maxX; x++) {
						for (int y = minY; y <= maxY; y++) {
							if (units[x][y] != null && units[x][y].getTeam() == this.team) {
								selection.add(units[x][y]);
							}
						}
					}
				}
				
			}
			else if (buttonPressed == 2 && !Gdx.input.isButtonPressed(Buttons.RIGHT)){
				buttonPressed = 0;
			}
		}
	}
	
	private void moveSelection(Point p) {
		boolean queue = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
		for (Unit u : selection) {
			if (queue) {
				u.queueMove(p);
			}
			else {
				u.move(p);
			}
		}
		
	}

	public Camera getCamera() {
		return camera;
	}

}
