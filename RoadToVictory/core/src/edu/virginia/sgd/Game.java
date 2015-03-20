package edu.virginia.sgd;

import java.util.ArrayList;

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
	ArrayList<GameObject> gameobjlist = new ArrayList<GameObject>();
	Player p1;


	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		lastTime = TimeUtils.millis();
		
		// Initialize Grid
		grid = new Grid(20, 20, img);
		
		//Initialize players
		p1 = new HumanPlayer(gameobjlist, grid, 1);
	}

	@Override
	public void render() {

		long currentTime = TimeUtils.millis();
		update((currentTime - lastTime) / 1000.f);
		lastTime = currentTime;

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		//Render Grid
		grid.render(batch);
		for (GameObject a : gameobjlist)
			a.render(batch);
		
		batch.end();
	}

	private void update(float timePassed) {
		for (GameObject a : gameobjlist)
			a.update(timePassed);
		p1.update(timePassed);
		grid.update(timePassed);
	}
}
