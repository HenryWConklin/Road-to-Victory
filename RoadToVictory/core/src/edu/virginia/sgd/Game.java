package edu.virginia.sgd;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Grid grid;
	long lastTime;
	HumanPlayer p1;
	AIPlayer p2;


	@Override
	public void create() {
		batch = new SpriteBatch();
		lastTime = TimeUtils.millis();
		
		// Initialize Grid
		grid = new Grid(100, 100);
		
		//Initialize players
		p1 = new HumanPlayer(grid, 1);
		p2 = new AIPlayer(grid,2, 2);
	}

	@Override
	public void render() {

		long currentTime = TimeUtils.millis();
		update((currentTime - lastTime) / 1000.f);
		lastTime = currentTime;

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		p1.getCamera().update();
		
		batch.setTransformMatrix(p1.getCamera().view);
		batch.setProjectionMatrix(p1.getCamera().projection);
		
		batch.begin();
		
		//Render Grid
		grid.render(batch);
		
		batch.end();
	}

	private void update(float timePassed) {
		p1.update(timePassed);
		p2.update(timePassed);
		grid.update(timePassed);
	}
}
