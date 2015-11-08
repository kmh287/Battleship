package com.mygdx.battleship;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.oracle.tools.packager.Log;

public class Battleship implements Screen {

	private final GameCanvas canvas;

	public Battleship(GameCanvas canvas){
		this.canvas = canvas;
	}

	@Override
	public void show () {

	}

	@Override
	public void render (float delta) {
		update(delta);
		draw (delta);
		Log.debug("foo");
	}

	private void update(float delta){

	}

	private void draw(float delta){

	}

	@Override
	public void resize (int width, int height) {

	}

	@Override
	public void pause () {

	}

	@Override
	public void resume () {

	}

	@Override
	public void hide () {

	}

	@Override
	public void dispose () {

	}
}
