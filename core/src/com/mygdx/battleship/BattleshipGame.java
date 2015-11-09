package com.mygdx.battleship;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.oracle.tools.packager.Log;

public class BattleshipGame implements Screen {

	private final GameCanvas canvas;
	private final BattleshipMap map;

	public BattleshipGame (GameCanvas canvas){
		this.canvas = canvas;
		this.map = new BattleshipMap(true, new AssetManager(), new String[0][0], new String[0][0]);
	}

	@Override
	public void render (float delta) {
		update(delta);
		draw(delta);
		Log.debug("foo");
	}

	private void update(float delta){

	}

	private void draw(float delta){
		canvas.begin();
		map.draw(canvas);
		canvas.end();
	}

/** Unused overrides **/

	@Override
	public void show () {

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
