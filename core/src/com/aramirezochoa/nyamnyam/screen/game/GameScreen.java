package com.aramirezochoa.nyamnyam.screen.game;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.activity.ActivityTransaction;
import com.aramirezochoa.nyamnyam.input.InputManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.AbstractScreen;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.ScreenType;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.gui.GameUserInterface;
import com.aramirezochoa.nyamnyam.screen.game.input.GamePad;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.aramirezochoa.nyamnyam.screen.menu.MenuAction;
import com.aramirezochoa.nyamnyam.store.StoreManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by boheme on 13/01/15.
 */
public class GameScreen extends AbstractScreen {

    private GameWrapper gameWrapper;
    private GameUserInterface gameUserInterface;
    private GamePad gamePad;

    public GameScreen() {
        super(MediaManager.GAME);
    }

    @Override
    public void show() {
        GameStatus gameStatus = DataManager.INSTANCE.getGameStatus();

        gameUserInterface = new GameUserInterface(getMediaManager());
        gameStatus.addListener(gameUserInterface);

        gameWrapper = new GameWrapper(getMediaManager(), gameStatus, gameUserInterface);
        gameStatus.addListener(gameWrapper);

        gamePad = new GamePad(getMediaManager(), gameWrapper);

        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(new InputAdapter());
        im.addProcessor(gamePad);
        im.addProcessor(gameUserInterface.getStage());
        Gdx.input.setInputProcessor(im);

        StoreManager.INSTANCE.checkExtraItems();

        init(gameStatus);
    }

    private void init(GameStatus gameStatus) {
        gameUserInterface.init(gameStatus.getLevel(), gameStatus.getLives(), gameStatus.getScore());
        gameWrapper.init(getMediaManager(), gameStatus);

        gameWrapper.getGameStatus().setGameState(GameState.IN_GAME);
    }

    @Override
    public void render(float delta) {
        // check if deltaTime isn't very high to avoid passing walls and so
        if (delta > Constant.GAME_MAX_LAG) {
            delta = Constant.GAME_MAX_LAG;
        }

        // Update
        if (!gameWrapper.getGameStatus().isPaused()) {
            gameWrapper.update(delta);
            gamePad.update(delta);
        }
        gameUserInterface.update(delta);

        // Render
        Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        gameWrapper.render(delta);
        gameUserInterface.render(delta);
        if (!gameWrapper.getGameStatus().isPaused()) {
            gamePad.render(delta);
        }

        if (DataManager.INSTANCE.getGameStatus().getLevel() > Constant.GAME_LEVELS) {
            ActivityManager.INSTANCE.showBannerAd(false);
            ScreenManager.INSTANCE.changeTo(ScreenType.END);
        } else {
            MenuAction menuAction = InputManager.INSTANCE.getMenuAction();
            switch (menuAction) {
                case PAUSE:
                    gameWrapper.getGameStatus().setGameState(GameState.PAUSE);
                    break;
                case CONTINUE:
                    gameWrapper.getGameStatus().setGameState(GameState.IN_GAME);
                    break;
                case NEXT:
                    getMediaManager().stopAllThemes();
                    int level = DataManager.INSTANCE.getGameStatus().getLevel();
                    if (level <= Constant.GAME_LEVELS) {
                        if (level % 2 == 0) {
                            ActivityManager.INSTANCE.showInterstitial(new ActivityTransaction() {
                                @Override
                                public void done(boolean result) {
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            init(DataManager.INSTANCE.getGameStatus());
                                        }
                                    });
                                }
                            });
                        } else {
                            init(DataManager.INSTANCE.getGameStatus());
                        }
                    }
                    break;
                case EXIT:
                    gameUserInterface.showConfirmExit();
                    break;
                case CONFIRM:
                    getMediaManager().stopAllThemes();
                    ActivityManager.INSTANCE.showBannerAd(false);
                    ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
                    break;
                default:
                    if (isBackJustPressed()) {
                        if (GameState.PAUSE.equals(gameWrapper.getGameStatus().getGameState())) {
                            gameWrapper.getGameStatus().setGameState(GameState.IN_GAME);
                        } else if (GameState.IN_GAME.equals(gameWrapper.getGameStatus().getGameState())) {
                            gameWrapper.getGameStatus().setGameState(GameState.PAUSE);
                        }
                    }
            }
        }
    }

    @Override
    public void pause() {
        if (GameState.IN_GAME.equals(gameWrapper.getGameStatus().getGameState())) {
            gameWrapper.getGameStatus().setGameState(GameState.PAUSE);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        gameWrapper.dispose();
        gameUserInterface.dispose();
        gamePad.dispose();
    }

    public interface GameScreenComponent {
        void update(float deltaTime);

        void render(float deltaTime);

        void dispose();
    }

}
