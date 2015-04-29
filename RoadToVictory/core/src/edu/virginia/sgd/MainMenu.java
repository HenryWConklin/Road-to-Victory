package edu.virginia.sgd;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenu implements Screen {

	private Stage stage;
	private Table table;
	
	private final Game parent;
	
	public MainMenu(final Game parent) {
		this.parent = parent;
		stage = new Stage();
		ScreenViewport viewport = new ScreenViewport();
		viewport.setUnitsPerPixel(.25f);
		stage.setViewport(viewport);
		Gdx.input.setInputProcessor(stage);
		
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		
		Label label = new Label("Road to Victory", skin);
		table.add(label);
		table.row();
		TextButton startButton = new TextButton("Start", skin);
		startButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.setScreen(new GameScreen(parent));
				dispose();
			}
		});
		table.add(startButton);
		table.row();
		TextButton quitButton = new TextButton("Quit", skin);
		quitButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();				
			}
		});
		table.add(quitButton);
		
	}	

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

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
		stage.dispose();
	}

}
