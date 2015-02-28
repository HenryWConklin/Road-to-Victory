package edu.virginia.sgd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Obstruction extends GameObject{
	
	//how far the obstruction extends horizontally
	int xRange;
	int xLowerBound; 
	int xUpperBound;
	//how far the obstruction extends vertically
	int yRange;
	int yLowerBound;
	int yUpperBound;
	boolean destroyable; //if the obstruction can be destroyed (like a wall)
	
	public Obstruction(int x, int y, int xRange, int yRange, boolean destroyable){
		super(x, y);
		(this).xRange = xRange;
		(this).yRange = yRange;
		(this).destroyable = destroyable;
		
	}
//	public Obstruction(int x, int y, int xLowerBound, int yLowerBound, int xUpperBound, int yUpperBound, boolean destroyable){
//		super(x, y);
//		(this).xLowerBound = xLowerBound;
//		(this).yLowerBound = yLowerBound;
//		(this).xUpperBound = xUpperBound;
//		(this).yUpperBound = yUpperBound;
//		(this).destroyable = destroyable;
//		
//	}

	@Override
	public void update(float timePassed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(SpriteBatch sb) {
		// TODO Auto-generated method stub
		
	}
	
	public void moveTo() {
		
	}
	
	// Remove part of the obstruction
	// either split into two obstructions, or trim off one edge
	public void breakPart(int xCoord, int yCoord) {
		if(destroyable) {
			//if the coordinate is not in the range, do nothing
			if ( !(xCoord <= (pos.x + xRange)&& pos.y <= (pos.y + yRange))) {
				System.out.println("nothing to delete");
			}
			//otherwise check cases to see what to do
			else {
				//if its a horizontal wall
				if( yRange == 1) {
					//if at beginning or end, just trim it
					if(yCoord == pos.y) {
						pos.y+=1;
						yRange--;
					}
					if(yCoord == pos.y + yRange) {
						yRange--;
					}
					//Otherwise split it into two objects
					else {
					Obstruction o1 = new Obstruction(pos.x, pos.y, xRange - xCoord, yRange, true);
					Obstruction o2 = new Obstruction(xCoord+1, pos.y, xRange - pos.x+1, yRange, true);
					//draw these?
					//delete original
					}
				}
				//if its a vertical line
				if( xRange == 1 ) {
					//if at beginning or end, just trim it
					if(xCoord == pos.x) {
						pos.x+=1;
						xRange--;
					}
					if(xCoord == pos.x + xRange) {
						xRange--;
					}
					else {
						Obstruction o1 = new Obstruction(pos.x, pos.y, xRange, yRange-yCoord, true);
						Obstruction o2 = new Obstruction(pos.x, yCoord+1, xRange, yRange - pos.x+1, true);
					}
					
				}
				
			}
			
			
			
		}
		if (yRange == 0 || xRange == 0) {
			//destruct?
		}
	}
}
