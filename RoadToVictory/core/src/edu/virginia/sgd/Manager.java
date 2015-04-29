package edu.virginia.sgd;

import com.badlogic.gdx.Game;

public class Manager extends Game{

	public Manager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void create() {
		this.setScreen(new GameScreen());
		
	}

}
