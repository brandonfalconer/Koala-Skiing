package com.brandon.Helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class AssetLoader {
	
	public static Texture GUI, HUD, levelButton, backgroundMain, coin, deadKoala, player;
    public static Sound buttonSound, hitSound;
    public static Music gameMusic, skiingSound;

	public static void load() {
        backgroundMain = new Texture(Gdx.files.internal("Sprites/BackgroundMain.png"));
		GUI = new Texture(Gdx.files.internal("Sprites/GUI.png"));
		HUD = new Texture(Gdx.files.internal("Sprites/HUD.png"));
		levelButton = new Texture(Gdx.files.internal("Sprites/LevelButton.png"));
        coin = new Texture(Gdx.files.internal("Sprites/Coin.png"));
        deadKoala = new Texture(Gdx.files.internal("Sprites/DeadKoala.png"));
        player = new Texture(Gdx.files.internal("Sprites/KoalaSkier.png"));

        buttonSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/button.ogg"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/hit.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Ski Music.wav"));
        skiingSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Skiing.wav"));
	}

	public static void playLoad() {
        hitSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/hit.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Ski Music.wav"));
        skiingSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Skiing.wav"));
    }

	public static void dispose() {
        hitSound.dispose();
        buttonSound.dispose();
        skiingSound.dispose();
        gameMusic.dispose();
	}
}