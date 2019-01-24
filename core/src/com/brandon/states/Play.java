package com.brandon.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.brandon.GameObjects.Player;
import com.brandon.Helper.AssetLoader;
import com.brandon.Helper.GameButton;
import com.brandon.Helper.GameStateManager;
import com.brandon.Helper.OrthogonalTiledMapRendererWithSprites;

import java.util.Timer;
import java.util.TimerTask;

public class Play extends GameState {

    private Vector2 direction;

    private Player player;
    private SpriteBatch batch;
    private ShapeRenderer sr;

    private int tileMapHeight, tileMapWidth;
    private float spawnPosX, spawnPosY;

    private TiledMap tiledMap;
    private TiledMapTileLayer mainLayer;
    private OrthogonalTiledMapRendererWithSprites tiledMapRenderer;

    private MapObjects objects, objectSnowman, coin, finish, spawn, ramp;
    private Rectangle playerRect;

    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Chunq.ttf"));
    private FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    private BitmapFont font;

    private ParticleEffect snowCloud, snowExplode, snowTrail;
    private float runTime;

    public static float volume = 0.2f;
    public static boolean musicOn = true;
    private boolean hit = true;
    private boolean finishLine, isJumping = false;

    public static int level;
    private GameButton backButton;

    private Timer timer;

    public Play(GameStateManager gsm) {
        super(gsm);

        try {
            tiledMap = new TmxMapLoader().load("Maps/Level"+level+".tmx");

        } catch (Exception e) {
            System.out.println("Failed to load level " + level);
            Gdx.app.exit();
        }

        mainLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        tileMapWidth = mainLayer.getWidth() * 32;
        tileMapHeight = mainLayer.getHeight() * 32;

        batch = new SpriteBatch();
        sr = new ShapeRenderer();

        direction = new Vector2(0, 0);

        timer = new Timer();

        player = new Player();
        player.position.x = tileMapWidth / 2;
        player.position.y = tileMapHeight - 150;
        playerRect = new Rectangle();

        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);
        tiledMapRenderer.addSprite(player.getPlayerSprite());

        parameter.color = Color.BLACK;
        parameter.size = 40;
        font = generator.generateFont(parameter);

        objectSetup();

        snowTrail = new ParticleEffect();
        snowTrail.load(Gdx.files.internal("Effects/Ski Trail 2"), Gdx.files.internal("Effects"));

        snowCloud = new ParticleEffect();
        snowCloud.load(Gdx.files.internal("Effects/SkiTrail.p"), Gdx.files.internal("Effects"));
        snowCloud.start();

        snowExplode = new ParticleEffect();
        snowExplode.load(Gdx.files.internal("Effects/SnowExplode"), Gdx.files.internal("Effects"));

        // In-game music
        //AssetLoader.playLoad();
        AssetLoader.gameMusic.setLooping(true);
        AssetLoader.gameMusic.setVolume(volume);

        AssetLoader.skiingSound.setLooping(true);
        AssetLoader.skiingSound.setVolume(volume / 4f);

        Texture tex = AssetLoader.GUI;
        backButton = new GameButton(new TextureRegion(tex, 0, 130, 135 / 2, 116 / 2), 25, 25, cam);
    }

    public void update(final float dt) {
        runTime = dt;

        //System.out.println("Turn Speed: "+player.turnSpeed);
        //System.out.println("Acceleration y: "+player.acceleration.y);
        //System.out.println("Rotation: "+player.rotation);
        //System.out.println("jumping: "+isJumping);

        // Set camera position
        if (!isJumping) {
            cam.position.x = player.getX() + 32;
            cam.position.y = player.getY() - 170;
            cam.zoom = 1.4f;

            cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth / 2, tileMapWidth - cam.viewportWidth / 2);
            cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight / 2, tileMapHeight + 500 - cam.viewportHeight / 2);
            cam.update();
        }

        snowExplode.update(dt);
        snowExplode.setPosition(player.position.x, player.position.y);

        if(musicOn){
            AssetLoader.gameMusic.play();
        } else {
            AssetLoader.gameMusic.stop();
        }

        player.update(dt);
        playerRect.set(player.position.x + 10, player.position.y + 10, 30, 35);

        if(!player.roundStart && Gdx.input.justTouched()) { player.roundStart = true; }

        if(player.roundStart) {
            backButton.update(dt);
            updateCollision();

            if (player.isAlive) {

                if (finishLine) {
                    if (player.acceleration.y < 0) {
                        player.acceleration.y += 20;
                    } else {
                        nextLevel();
                    }
                }

                // Skiing physics
                if (!isJumping) {
                    // Snow cloud behind player
                    if (player.acceleration.y < -200 || player.getTurnSpeed() > 200) {
                        snowCloud.setPosition(player.position.x + 20, player.position.y + 60);
                        snowCloud.update(dt);
                        AssetLoader.skiingSound.play();
                    }

                    // Player points towards mouse
                    if (Gdx.input.getY() > (Gdx.graphics.getHeight() / 3) && Gdx.input.isTouched()) {
                        Vector3 worldCoords;
                        worldCoords = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

                        float mouseX = worldCoords.x;
                        float mouseY = worldCoords.y;

                        float originX = player.getWidth() / 2 + player.position.x;
                        float originY = player.getHeight() / 2 + player.position.y;

                        double angle = Math.atan2(mouseY - originY, mouseX - originX) * (180 / Math.PI) + 90;
                        if (angle < 0)
                            angle += 360;

                        player.rotation = (float) angle;

                        direction.x = mouseX - player.position.x;
                        direction.y = mouseY - player.position.y;

                        double hyp = Math.sqrt(direction.x * direction.x + direction.y * direction.y);
                        direction.x /= hyp;
                        direction.y /= hyp;

                        // Player movement
                        player.position.x += direction.x * player.getTurnSpeed() * dt;
                        player.position.y += direction.y * player.getTurnSpeed() * dt;
                    }
                    // Increase speed when not touching or turning
                    else if (!Gdx.input.isTouched() || Gdx.input.getY() < 250 || isJumping) {
                        player.position.add(player.acceleration.cpy().scl(dt));

                    }
                } else {
                    // Player is jumping
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (player.rotation > 1 && player.rotation < 180 && player.rotation != 1 && (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) || !player.isAlive)) {
                                player.rotation -= 4;
                            } else if (player.rotation > 180 && player.rotation < 360 && player.rotation != 1 && (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) || !player.isAlive)) {
                                player.rotation += 4;
                            }

                            player.position.add(player.acceleration.cpy().scl(dt));
                            //player.playerSprite.setSize(64, 72);
                            cam.zoom = 0.4f;
                            cam.update();
                        }
                    }, 2);

                    isJumping = false;
                    cam.zoom = 1.4f;
                    cam.update();
                    player.playerSprite.setSize(56, 64);
                }
            } else {
                // Player has died
                AssetLoader.skiingSound.stop();

                // Back button was clicked
                if (Gdx.input.justTouched() && Gdx.input.getX() < (Gdx.graphics.getWidth() / 5) && Gdx.input.getY() > (Gdx.graphics.getHeight() / 1.2)) {
                    AssetLoader.buttonSound.play(Play.volume);
                    gsm.setState(GameStateManager.LEVEL_SELECT);
                    AssetLoader.gameMusic.stop();

                // Reset Player
                } else if (Gdx.input.justTouched()) {
                    resetPlayer(player);
                    hit = true;
                }
            }
        }
    }

    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!player.isAlive) { backButton.render(sb, 40, 40); }

        Player.player.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        AssetLoader.GUI.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        AssetLoader.HUD.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(cam.combined);
        batch.enableBlending();
        batch.begin();

        snowCloud.draw(batch);
        snowExplode.draw(batch);
        snowTrail.draw(batch);

        if (!player.roundStart) {
            parameter.size = 50;
            parameter.color = Color.CYAN;
            font.draw(batch, "      Level: "+level+"\n Hold to move", cam.position.x - 150, cam.position.y + 320);
        }

        if (!player.isAlive) {
            parameter.size = 50;
            parameter.color = Color.CYAN;
            font.draw(batch, "Tap to restart", cam.position.x - 150, cam.position.y + 300);
        }

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeType.Filled);
        sr.setColor(new Color(1, 1, 1, 0f));
        sr.rect(playerRect.x, playerRect.y, 30, 40);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void restartMap() {
        if (hit) {
            AssetLoader.hitSound.play((Play.volume + 0.3f));
            hit = false;
        }
        player.isAlive = false;
        snowCloud.reset();
    }

    private void resetPlayer(Player player) {
        player.position.x = spawnPosX;
        player.position.y = spawnPosY;
        player.acceleration.y = -60;
        player.turnSpeed = 0;
        player.isAlive = true;
    }

    private void nextLevel() {
        player.roundStart = false;
        finishLine = false;

        tiledMapRenderer.getMap().dispose();
        tiledMapRenderer.dispose();

        level += 1;
        tiledMap = new TmxMapLoader().load("Maps/Level" + level + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);
        tiledMapRenderer.addSprite(player.getPlayerSprite());
        tiledMapRenderer.setMap(tiledMap);

        mainLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        tileMapWidth = mainLayer.getWidth() * 32;
        tileMapHeight = mainLayer.getHeight() * 32;

        objectSetup();

        for (RectangleMapObject rectangleObject : spawn.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            spawnPosX = rectangle.getX();
            spawnPosY = rectangle.getY();
        }

        resetPlayer(player);
    }

    private void objectSetup() {
        MapLayer collisionObjectLayer;
        MapLayer snowmanCollision;
        //MapLayer rampCollision;
        MapLayer coinCollision;
        MapLayer finishCollision;
        MapLayer spawnCollision;

        collisionObjectLayer = tiledMap.getLayers().get("Collision");
        snowmanCollision = tiledMap.getLayers().get("SnowmanCollision");
        //rampCollision = tiledMap.getLayers().get("RampCollision");
        coinCollision = tiledMap.getLayers().get("Coin");
        finishCollision = tiledMap.getLayers().get("Finish");
        spawnCollision = tiledMap.getLayers().get("Spawn");

        objectSnowman = snowmanCollision.getObjects();
        objects = collisionObjectLayer.getObjects();
        //ramp = rampCollision.getObjects();
        coin = coinCollision.getObjects();
        finish = finishCollision.getObjects();
        spawn = spawnCollision.getObjects();
    }

    private void updateCollision() {
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, playerRect) && !isJumping) {
                restartMap();
            }
        }

        for (RectangleMapObject rectangleObject : objectSnowman.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, playerRect) && !isJumping) {
                //player.acceleration.y += 30;

                snowExplode.setPosition(rectangle.getX() + 20, rectangle.getY());
                snowExplode.reset();
                snowExplode.update(runTime);
            }
        }

        for (RectangleMapObject rectangleObject : coin.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, playerRect) && !isJumping) {
                Menu.money += 1;
            }
        }

        for (RectangleMapObject rectangleObject : finish.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, playerRect)) {
                if(level < 20 && player.isAlive) {
                    finishLine = true;
                } else {
                    gsm.setState(GameStateManager.LEVEL_SELECT);
                    AssetLoader.gameMusic.stop();
                }
            }
        }

        for (RectangleMapObject rectangleObject : spawn.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            spawnPosX = rectangle.getX();
            spawnPosY = rectangle.getY();
        }

        /*
        for (RectangleMapObject rectangleObject : ramp.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, playerRect)) {
                isJumping = true;
            }
        }
        */
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
    }

    public void dispose() {
        //AssetLoader.dispose();
        batch.dispose();
        generator.dispose();
        sr.dispose();
        tiledMapRenderer.dispose();
        font.dispose();
        snowCloud.dispose();
        snowExplode.dispose();
        snowTrail.dispose();
    }
}