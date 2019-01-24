package com.brandon.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.brandon.Helper.AssetLoader;
import com.brandon.Helper.Background;
import com.brandon.Helper.GameButton;
import com.brandon.Helper.GameStateManager;

public class LevelSelect extends GameState {

    private Background bg;
	private GameButton[][] buttons;
    private GameButton backButton;

    private BitmapFont font;

	public LevelSelect(GameStateManager gsm) {
		super(gsm);
        cam.position.set(cam.viewportWidth / 2,cam.viewportHeight / 2,0);

        // Background
        bg = new Background(new TextureRegion(AssetLoader.backgroundMain), cam, 1f);

        // Level buttons
		TextureRegion buttonReg = new TextureRegion(AssetLoader.levelButton, 0, 0, 32, 32);
		buttons = new GameButton[4][5];
		
		for(int row = 0; row < buttons.length; row++) {
			for(int col = 0; col < buttons[0].length; col++) {
				buttons[row][col] = new GameButton(buttonReg, 70 + col * 40, 300 - row * 40, cam);
				buttons[row][col].setText(row * buttons[0].length + col + 1 + "");
			}
		}

        // Back button
        backButton = new GameButton(new TextureRegion(AssetLoader.GUI, 0, 130, 135 / 2, 116 / 2), 25, 25, cam);

		// Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Chunq.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.BLACK;
        parameter.size = 35;
        font = generator.generateFont(parameter);
	}

	public void update(float dt) {
        bg.update(dt);

		for(int row = 0; row < buttons.length; row++) {
			for(int col = 0; col < buttons[0].length; col++) {
				buttons[row][col].update(dt);

				// Button has been pressed
				if(Gdx.input.justTouched()) {
				    if(buttons[row][col].isClicked()) {
                        AssetLoader.buttonSound.play(Play.volume);
					    Play.level = row * buttons[0].length + col + 1;
					    gsm.setState(GameStateManager.PLAY);
				    }
				}
			}
		}

        if(backButton.isClicked()) {
            AssetLoader.buttonSound.play(Play.volume);
            gsm.setState(GameStateManager.MENU);
        }

        backButton.update(dt);
	}

	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.position.x = cam.viewportWidth / 2;
        cam.position.y = cam.viewportHeight / 2;
        cam.zoom = 1;
        cam.update();

        bg.render(sb);
        backButton.render(sb, 40, 40);

        sb.begin();
        font.draw(sb, "Level Select", 50, cam.viewportHeight - 50);
        sb.end();

		for(int row = 0; row < buttons.length; row++) {
			for(int col = 0; col < buttons[0].length; col++) {
				buttons[row][col].render(sb, 32, 32);
			}
		}
	}

    public void resize(int width, int height) {
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
    }

	public void dispose() {}
}
