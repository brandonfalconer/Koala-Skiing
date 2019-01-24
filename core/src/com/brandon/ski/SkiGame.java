package com.brandon.ski;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.brandon.Helper.AssetLoader;
import com.brandon.Helper.GameStateManager;

public class SkiGame implements ApplicationListener {
	
	private SpriteBatch sb;
    private String filename = "saveFile";
	private GameStateManager gsm;

    private OrthographicCamera cam;
    private Viewport viewport;
	
	public void create() {
		AssetLoader.load();

        cam = new OrthographicCamera();
        //cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(320,480, cam);
        viewport.apply();

		sb = new SpriteBatch();
		gsm = new GameStateManager(this);
	}
	
	public void render() {
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();

        cam.position.x = cam.viewportWidth/2;
        cam.position.y = cam.viewportHeight/2;
        cam.zoom = 1;
        cam.update();
	}

    public void dispose() {
        AssetLoader.dispose();
    }
	
	public SpriteBatch getSpriteBatch() { return sb; }
	public OrthographicCamera getCamera() { return cam; }
    public Viewport getViewport() { return viewport; }

	public void resize(int width, int height) {
	    //gsm.resize(width, height);
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}
}