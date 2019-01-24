package com.brandon.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.brandon.Helper.GameStateManager;

public class Pause extends GameState{

	public Pause(GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void update(float dt) {
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			gsm.setState(GameStateManager.PLAY);
		}
	}

	@Override
	public void render() {
	}

	@Override
	public void dispose() {
	}

    public void resize(int width, int height) {
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
    }
}