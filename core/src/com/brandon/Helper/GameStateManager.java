package com.brandon.Helper;

import java.util.Stack;

import com.brandon.ski.SkiGame;
import com.brandon.states.GameState;
import com.brandon.states.LevelSelect;
import com.brandon.states.Menu;
import com.brandon.states.Pause;
import com.brandon.states.Play;
import com.brandon.states.SettingMenu;

public class GameStateManager {
	
	private SkiGame game;
	private Stack<GameState> gameStates;
	
	public static final int MENU = 1;
	public static final int PLAY = 2;
	public static final int LEVEL_SELECT = 3;
	public static final int SETTING_MENU = 4;
	public static final int PAUSE = 5;
	
	public GameStateManager(SkiGame game) {
		this.game = game;
		gameStates = new Stack<GameState>();
		pushState(MENU);
	}
	
	public void update(float dt) {
		gameStates.peek().update(dt);
	}
	public void render() {
		gameStates.peek().render();
	}
	public void resize(int width, int height) { gameStates.peek().resize(width,height);}
	public SkiGame game() { return game; }
	
	public GameState getState(int state) {
		if(state == MENU) return new Menu(this);
		if(state == PLAY) return new Play(this);
		if(state == PAUSE) return new Pause(this);
		if(state == SETTING_MENU) return new SettingMenu(this);
		if(state == LEVEL_SELECT) return new LevelSelect(this);
		return null;
	}

	
	public void setState(int state) {
		popState();
		pushState(state);
	}

	private void pushState(int state) {
		gameStates.push(getState(state));
	}
	
	private void popState() {
		GameState g = gameStates.pop();
		g.dispose();
	}
}
