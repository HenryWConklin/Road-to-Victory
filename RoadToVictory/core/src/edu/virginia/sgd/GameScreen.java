package edu.virginia.sgd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
	SpriteBatch batch;
	Texture img;
	Grid grid;
	HumanPlayer p1;
	AIPlayer p2;

	Game parent;

	public GameScreen(Game parent) {
		this.parent = parent;
		
		batch = new SpriteBatch();
		
		// Initialize Grid
		grid = new Grid(100, 100);
		
		//Initialize players
		p1 = new HumanPlayer(grid, 1);
		p2 = new AIPlayer(grid,2, .25f);
		
	}

	@Override
	public void render(float delta) {
		
		update(delta);
		
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
		
		int winTeam = grid.getWinner();
		if (winTeam != 0) {
			
		}
	}

	@Override
	public void show() {
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
