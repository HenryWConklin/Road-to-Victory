package edu.virginia.sgd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CreateGameMenu implements Screen {

	private final Game parent;
	
	private Stage stage;
	private Table table;
	
	public CreateGameMenu(final Game parent) {
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
		
		table.row();
		table.add(new Label("Player 1: Human", skin));
		table.row();
		final CheckBox p2Check = new CheckBox("Player 2: Computer", skin);
		p2Check.setChecked(true);
		table.add(p2Check);
		table.row();
		table.add(new Label("Difficulty:", skin));
		final Slider p2Diff = new Slider(0, 10, 1, false, skin);
		p2Diff.setValue(5);
		table.add(p2Diff);
		table.row();
		final CheckBox p3Check = new CheckBox("Player 3: Computer", skin);
		table.add(p3Check);
		table.row();
		table.add(new Label("Difficulty:", skin));
		final Slider p3Diff = new Slider(0, 10, 1, false, skin);
		p3Diff.setValue(5);
		table.add(p3Diff);
		table.row();
		final CheckBox p4Check = new CheckBox("Player 4: Computer", skin);
		table.add(p4Check);
		table.row();
		table.add(new Label("Difficulty:", skin));
		final Slider p4Diff = new Slider(0, 10, 1, false, skin);
		p4Diff.setValue(5);
		table.add(p4Diff);
		table.row();
		TextButton startButton = new TextButton("Start", skin);
		startButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.setScreen(new GameScreen(parent, p2Check.isChecked(), p3Check.isChecked(), p4Check.isChecked(), 
						(int)p2Diff.getValue(), (int)p3Diff.getValue(), (int)p4Diff.getValue()));		
				dispose();
			}
		});
		table.add(startButton);
		TextButton backButton = new TextButton("Back", skin);
		backButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.setScreen(new MainMenu(parent));
				dispose();				
			}
		});
		table.add(backButton);
		
		
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
