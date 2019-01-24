package com.brandon.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.brandon.Helper.GameStateManager;
import com.brandon.ski.SkiGame;

public abstract class GameState {

	protected GameStateManager gsm;
	protected SkiGame game;

	protected SpriteBatch sb;
	protected OrthographicCamera cam;
	protected Viewport viewport;
	
	protected GameState(GameStateManager gsm) {
		this.gsm = gsm;
		game = gsm.game();
		sb = game.getSpriteBatch();
		cam = game.getCamera();
		viewport = game.getViewport();
	}
	
	public abstract void update(float dt);
	public abstract void render();
	public abstract void dispose();
    public abstract void resize(int width, int height);
}
