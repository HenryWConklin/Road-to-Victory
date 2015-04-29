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
	AIPlayer p3;
	AIPlayer p4;

	final Game parent;

	public GameScreen(final Game parent, boolean p2, boolean p3, boolean p4, int p2Diff, int p3Diff, int p4Diff) {
		this.parent = parent;
		
		batch = new SpriteBatch();
		
		// Initialize Grid
		grid = new Grid(100, 100);
		
		//Initialize players
		this.p1 = new HumanPlayer(grid, 1);
		if (p2)
			this.p2 = new AIPlayer(grid,2, 2f - p2Diff*.15f);
		if (p3)
			this.p3 = new AIPlayer(grid,3, 2f - p3Diff*.15f);
		if (p4)
			this.p4 = new AIPlayer(grid,4, 2f - p4Diff*.15f);
		
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
		if (p2 != null)
			p2.update(timePassed);
		if (p3 != null)
			p3.update(timePassed);
		if (p4 != null)
			p4.update(timePassed);
		grid.update(timePassed);
		
		int winTeam = grid.getWinner();
		if (winTeam != -1) {
			parent.setScreen(new WinScreen(parent, winTeam));
			dispose();
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
	}
}
