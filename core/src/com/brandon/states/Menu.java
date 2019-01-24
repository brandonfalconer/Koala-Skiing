package com.brandon.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.brandon.Helper.AssetLoader;
import com.brandon.Helper.Background;
import com.brandon.Helper.GameButton;
import com.brandon.Helper.GameStateManager;

public class Menu extends GameState {
	
	private GameButton playButton, exitButton, settingButton;
    private Background bg;
    private BitmapFont font;

    public static int money = 0;

	public Menu(GameStateManager gsm) {
		super(gsm);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Chunq.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.BLACK;
        font = generator.generateFont(parameter);

        bg = new Background(new TextureRegion(AssetLoader.backgroundMain), cam, 1f);

		Texture tex = AssetLoader.GUI;
		exitButton = new GameButton(new TextureRegion(tex, 155, 258, 309 / 2, 141 / 2), 85, 75, cam);
		playButton = new GameButton(new TextureRegion(tex, 0, 329, 309 / 2, 138 / 2), 85, 225, cam);
		settingButton = new GameButton(new TextureRegion (tex, 0, 119 / 2, 309 / 2, 141 / 2), 85, 150 , cam);
	}

	public void update(float dt) {
		if(playButton.isClicked()) {
            AssetLoader.buttonSound.play(Play.volume);
			gsm.setState(GameStateManager.LEVEL_SELECT);
		}

        if(exitButton.isClicked()){
			Gdx.app.exit();
		}
		
		if(settingButton.isClicked()) {
			gsm.setState(GameStateManager.SETTING_MENU);
		}
		
		playButton.update(dt);
		exitButton.update(dt);
		settingButton.update(dt);
        bg.update(dt);
	}

	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		sb.setProjectionMatrix(cam.combined);

        bg.render(sb);
		playButton.render(sb, 307 / 2, 130 / 2);
		exitButton.render(sb, 307 / 2, 133 / 2);
		settingButton.render(sb, 307 / 2, 133 / 2);

        sb.begin();
        font.draw(sb,"Koala Skiing", 30, 400);
        sb.end();
	}

	public void dispose() {}

    public void resize(int width, int height) {}
}