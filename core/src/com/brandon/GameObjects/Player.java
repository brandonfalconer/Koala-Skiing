package com.brandon.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brandon.Helper.AssetLoader;

public class Player {
	public Vector2 position;
	public Vector2 acceleration;

	public float rotation;
	public int width = 56;
	public int height = 64;

	//private float turnSpeedDecrease = 75;
    public static Texture player;
	public float turnSpeed = 1;

	public boolean isAlive = true;
    public boolean roundStart = false;
	
	public Sprite playerSprite;
    private Animation skiing;
    private TextureRegion mainFrame;
    private static final int FRAME_ROWS = 1;
    private static final int FRAME_COLS = 4;
    private float stateTime = 0f;
	
	public Player() {
		position = new Vector2(0, 0);
		acceleration = new Vector2(0, -10);

        player = AssetLoader.player;
        playerSprite = new Sprite(player, 56, 0, width, height);
        TextureRegion[][] tmp = TextureRegion.split(player, player.getWidth() / FRAME_COLS, player.getHeight() / FRAME_ROWS);
        TextureRegion[] skiFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        mainFrame = new TextureRegion(player, 0, 0, width, height);

        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                skiFrames[index++] = tmp[i][j];
            }
        }

        //Pole movement
        skiing = new Animation(0.175f,skiFrames);
        skiing.setPlayMode(Animation.PlayMode.LOOP);
    }

	public void update(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = skiing.getKeyFrame(stateTime);

        final float maxSpeed = -700;
        float turnSpeedIncrease;
        float turnSpeedMax = 0;

        // Rotate to point straight when no input
		if (rotation > 1 && rotation < 180 && rotation != 1 && (!Gdx.input.isButtonPressed(Buttons.LEFT) || !isAlive)){
			rotation -= 2;
		}else if(rotation > 180 && rotation < 360 && rotation != 1 && (!Gdx.input.isButtonPressed(Buttons.LEFT) || !isAlive)){
			rotation += 2;
		}

		if (isAlive && roundStart) {
            if(acceleration.y > -300) {
                playerSprite.setRegion(currentFrame);
            }else {
                playerSprite.setRegion(mainFrame);
            }

			//Acceleration downwards
			acceleration.y -= 80 * delta;

			if (acceleration.y < maxSpeed) {
				acceleration.y = maxSpeed;
			}
			
			if (Gdx.input.getY() < 100) {
				rotation = 0;
			}
			
			//Slow down on rotation
			if (rotation < 30 || rotation > 330) {
				//Fast
				turnSpeedIncrease = 100;
				turnSpeedMax = 500;
				
				if(turnSpeed < turnSpeedMax)
					turnSpeed += turnSpeedIncrease * delta;
				//else if(turnSpeed > turnSpeedMax) {
					//turnSpeed -= turnSpeedDecrease * delta;
				//}

                if (acceleration.y > -300)
                    playerSprite.setRegion(currentFrame);
                else
                    playerSprite.setRegion(mainFrame);
				
			}else if(rotation > 50 && rotation < 90 || rotation > 270 && rotation < 315) {
				//Slow
				turnSpeedIncrease = 50;
				turnSpeedMax = 250;
				
				if(turnSpeed < turnSpeedMax)
					turnSpeed += turnSpeedIncrease * delta;

                playerSprite.setRegion(currentFrame);
				//else if(turnSpeed > turnSpeedMax) {
					//turnSpeed -= turnSpeedDecrease * delta;
				//}
				
			}else if (rotation > 30 && rotation < 50 || rotation < 330 && rotation > 315) {
				//Medium
				turnSpeedIncrease = 80;
				turnSpeedMax = 350;
				
				if(turnSpeed < turnSpeedMax)
					turnSpeed += turnSpeedIncrease * delta;
				//else if(turnSpeed > turnSpeedMax) {
					//turnSpeed -= turnSpeedDecrease * delta;
				//}

                playerSprite.setRegion(currentFrame);
			}
			
			if (turnSpeed > turnSpeedMax) {
				turnSpeed = turnSpeedMax;
			}

		}else if (!isAlive) {
            playerSprite.setRegion(AssetLoader.deadKoala);
			position.add(acceleration.cpy().scl(delta));
			
			if(acceleration.y < 0)
				acceleration.y += 7;
			else
				acceleration.y = 0;
		}

		playerSprite.setRotation(rotation);
		playerSprite.setX(position.x);
		playerSprite.setY(position.y);
		playerSprite.setOrigin(width / 2, height / 2);
	}

	public float getX() {
		return position.x;
	}
	public float getY() {
		return position.y;
	}
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public float getRotation() {
		return rotation;
	}
	public float getTurnSpeed() {
		return turnSpeed;
	}
	public Sprite getPlayerSprite() {
		return playerSprite;
	}
	public float getAcceleration() {
		return acceleration.y;
	}
	public void setAcceleration(float acc) {
		acceleration.y = acc;
	}
}