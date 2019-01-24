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

public class SettingMenu extends GameState {

    private GameButton volumeButton, musicButton, backButton;
    private BitmapFont font;
    private Background bg;

	public SettingMenu(GameStateManager gsm) {
        super(gsm);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Chunq.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        bg = new Background(new TextureRegion(AssetLoader.backgroundMain), cam, 1f);
        parameter.size = 50;
        parameter.color = Color.BLACK;
        font = generator.generateFont(parameter);

        Texture tex = AssetLoader.GUI;
        volumeButton = new GameButton(new TextureRegion(tex, 156, 1, 309 / 2, 141 / 2), 25,225, cam);
        musicButton = new GameButton(new TextureRegion(tex, 156, 72, 309 / 2, 141 / 2), 25,150, cam);
        backButton = new GameButton(new TextureRegion(tex, 0, 130, 135 / 2, 116 / 2), 25,25, cam);
	}

	@Override
	public void update(float dt) {
        volumeButton.update(dt);
        musicButton.update(dt);
        backButton.update(dt);
        bg.update(dt);

        if(backButton.isClicked()) {
            AssetLoader.buttonSound.play(Play.volume);
            gsm.setState(GameStateManager.MENU);
        }

        if(musicButton.isClicked()) {
            AssetLoader.buttonSound.play(Play.volume);
            Play.musicOn = !Play.musicOn;
        }

        if(volumeButton.isClicked()) {
            AssetLoader.buttonSound.play(Play.volume);
            Play.volume += 0.2f;
            if(Play.volume > 1f) {
                Play.volume = 0f;
            }
        }

        AssetLoader.gameMusic.setLooping(true);
        if(Play.musicOn){
            AssetLoader.gameMusic.setVolume(Play.volume);
        }
        else {
            AssetLoader.gameMusic.setVolume(0);
        }
        AssetLoader.gameMusic.play();
	}

	@Override
	public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(cam.combined);
        bg.render(sb);

        //Change Size
        musicButton.render(sb, 307 / 2, 138 / 2);
        volumeButton.render(sb, 307 / 2, 138 / 2);
        backButton.render(sb, 40, 40);

        sb.begin();
        font.draw(sb, "   "+Play.volume, 180, 280);

        if(Play.musicOn) {
            font.draw(sb, "   On", 180, 200);
        }else
            font.draw(sb, "   Off", 180, 200);
        sb.end();
	}

    public void resize(int width, int height) {}

	public void dispose() {}
}
