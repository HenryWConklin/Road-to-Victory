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
	long lastTime;
	ArrayList<GameObject> gameobjlist = new ArrayList<GameObject>();

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		lastTime = TimeUtils.millis();
	}

	@Override
	public void render() {

		long currentTime = TimeUtils.millis();
		update(currentTime - lastTime);
		lastTime = currentTime;

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		for (GameObject a : gameobjlist)
			a.render(batch);
	}

	private void update(long timePassed) {
		for (GameObject a : gameobjlist)
			a.update(timePassed);
	}
}
