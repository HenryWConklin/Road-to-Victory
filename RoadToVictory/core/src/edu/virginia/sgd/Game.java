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
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		lastTime = TimeUtils.millis();
		
		// Initialize Grid
		grid = new Grid(200, 200, img);
	}

	@Override
	public void render () {
		
		long currentTime = TimeUtils.millis();
		update(currentTime - lastTime);
		lastTime = currentTime;
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		//Render Grid
		grid.render(0, 0, batch);
		batch.end();
	}
	
	private void update(long timePassed) {
		
	}
}
