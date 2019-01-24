package com.brandon.Helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

/**
 * Simple image button.
 */
public class GameButton {
	
	// center at x, y
	private float x, y, width, height;
	
	private TextureRegion reg;
	
	private Vector3 vec;
	private OrthographicCamera cam;
	
	private boolean clicked;
	
	private String text;
	private TextureRegion[] font;
	
	public GameButton(TextureRegion reg, float x, float y, OrthographicCamera cam) {

		this.reg = reg;
		this.x = x;
		this.y = y;
		this.cam = cam;
		
		width = reg.getRegionWidth();
		height = reg.getRegionHeight();
		vec = new Vector3();
		
		Texture tex = AssetLoader.HUD;
		font = new TextureRegion[11];
		for(int i = 0; i < 6; i++) {
			font[i] = new TextureRegion(tex, 32 + i * 9, 16, 9, 9);
		}
		for(int i = 0; i < 5; i++) {
            font[i + 6] = new TextureRegion(tex, 32 + i * 9, 25, 9, 9);
        }
	}
	
	public boolean isClicked() { return clicked; }
	public void setText(String s) { text = s; }
	
	public void update(float dt) {
		
		vec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(vec);

        clicked = Gdx.input.justTouched() &&
                vec.x > x && vec.x < x + width &&
                vec.y > y && vec.y < y + height;
	}
	
	public void render(SpriteBatch sb, int width, int height) {
		
		sb.begin();
		
		sb.draw(reg, x, y, width, height);
		
		if(text != null) {
			drawString(sb, text, x, y);
		}
		
		sb.end();
	}
	
	private void drawString(SpriteBatch sb, String s, float x, float y) {
		int len = s.length();
		float xo = len * font[0].getRegionWidth() / 2 - 16;
		float yo = font[0].getRegionHeight() / 2 - 16;
		for(int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if(c == '/') c = 10;
			else if(c >= '0' && c <= '9') c -= '0';
			else continue;
			sb.draw(font[c], x + i * 9 - xo, y - yo);
		}
	}
}
