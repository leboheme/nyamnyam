package com.aramirezochoa.nyamnyam.screen.game;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.input.InputManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.AbstractScreen;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.ScreenType;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.debug.DebugConsole;
import com.aramirezochoa.nyamnyam.screen.game.debug.StageCommand;
import com.aramirezochoa.nyamnyam.screen.game.gui.GameUserInterface;
import com.aramirezochoa.nyamnyam.screen.game.input.GamePad;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomLoader;
import com.aramirezochoa.nyamnyam.screen.game.run.RunRuntime;
import com.aramirezochoa.nyamnyam.screen.game.run.RunFactory;
import com.aramirezochoa.nyamnyam.screen.game.run.RunState;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.aramirezochoa.nyamnyam.screen.menu.MenuAction;
import com.aramirezochoa.nyamnyam.store.StoreManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Created by boheme on 13/01/15.
 */
public class GameScreen extends AbstractScreen {

    private GameWrapper gameWrapper;
    private GameUserInterface gameUserInterface;
    private GamePad gamePad;
    private DebugConsole debugConsole;
    private ExtendViewport hudViewport;
    private RunState runState;
    private GameStatus gameStatus;
    private final RunFactory runFactory = new RunFactory(new RoomLoader());
    private RunRuntime runRuntime;
    private boolean hasJsonRoom;
    private boolean useLegacyFlow;
    private int loadedLevel = -1;

    public GameScreen() {
        super(MediaManager.GAME);
    }

    @Override
    public void show() {
        gameStatus = DataManager.INSTANCE.getGameStatus();

        hudViewport = new ExtendViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        hudViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        runState = new RunState();
        gameUserInterface = new GameUserInterface(getMediaManager(), hudViewport);
        gameStatus.addListener(gameUserInterface);

        gameWrapper = new GameWrapper(getMediaManager(), gameStatus, gameUserInterface, runState);
        gameStatus.addListener(gameWrapper);

        gamePad = new GamePad(getMediaManager(), new GamePad.PlayerActions() {
            private com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar player() {
                return runRuntime == null ? gameWrapper.getEntityWorld().getMainAvatar()
                        : runRuntime.getMainAvatar();
            }

            @Override public void left() { if (player() != null) player().left(); }
            @Override public void right() { if (player() != null) player().right(); }
            @Override public void fire() {
                if (player() != null) player().fire();
            }
            @Override public void jump() { if (player() != null) player().jump(); }
        }, hudViewport);
        gamePad.layoutControls();
        debugConsole = new DebugConsole(getMediaManager(), hudViewport);
        debugConsole.registerCommand(new StageCommand(this::loadStage));

        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(debugConsole);
        im.addProcessor(new InputAdapter());
        im.addProcessor(gamePad);
        im.addProcessor(gameUserInterface.getStage());
        Gdx.input.setInputProcessor(im);

        StoreManager.INSTANCE.checkExtraItems();

        init(gameStatus);
    }

    private void init(GameStatus gameStatus) {
        if (loadedLevel != gameStatus.getLevel()) {
            useLegacyFlow = false;
            loadedLevel = gameStatus.getLevel();
        }
        gameUserInterface.init(gameStatus.getLevel(), gameStatus.getLives(), gameStatus.getScore());
        hasJsonRoom = runFactory.populate(runState,
                "data/rooms/" + gameStatus.getLevel() + ".json");
        if (runRuntime != null) {
            runRuntime.dispose();
            runRuntime = null;
        }
        if (hasJsonRoom && !useLegacyFlow) {
            gameWrapper.unloadLegacyStage();
            runRuntime = new RunRuntime(getMediaManager(), runState, gameStatus);
        } else {
            gameWrapper.init(getMediaManager(), gameStatus);
        }
        gameStatus.setGameState(GameState.IN_GAME);
    }

    private void loadStage(int stageNumber) {
        gameStatus.setLevel(stageNumber);
        init(gameStatus);
    }

    @Override
    public void render(float delta) {
        // check if deltaTime isn't very high to avoid passing walls and so
        if (delta > Constant.GAME_MAX_LAG) {
            delta = Constant.GAME_MAX_LAG;
        }

        if (hasJsonRoom && Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            useLegacyFlow = !useLegacyFlow;
            init(gameStatus);
        }

        // Update
        if (!gameStatus.isPaused()) {
            if (runRuntime != null) {
                runRuntime.update(delta);
                gamePad.update(delta);
            } else {
                gameWrapper.update(delta);
                gamePad.update(delta);
            }
        }
        gameUserInterface.update(delta);

        // Render
        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (runRuntime != null) {
            runRuntime.render(delta);
        } else {
            gameWrapper.render(delta);
        }
        gameUserInterface.render(delta);
        if (!gameStatus.isPaused()) {
            gamePad.render(delta);
        }
        debugConsole.render();

        if (DataManager.INSTANCE.getGameStatus().getLevel() > Constant.GAME_LEVELS) {
            ActivityManager.INSTANCE.showBannerAd(false);
            ScreenManager.INSTANCE.changeTo(ScreenType.END);
        } else {
            if (!debugConsole.consumeMenuInput()) {
                MenuAction menuAction = InputManager.INSTANCE.getMenuAction();
                switch (menuAction) {
                    case PAUSE:
                        gameStatus.setGameState(GameState.PAUSE);
                        break;
                    case CONTINUE:
                        gameStatus.setGameState(GameState.IN_GAME);
                        break;
                    case NEXT:
                        getMediaManager().stopAllThemes();
                        int level = DataManager.INSTANCE.getGameStatus().getLevel();
                        if (level <= Constant.GAME_LEVELS) {
                            init(DataManager.INSTANCE.getGameStatus());
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
                            if (GameState.PAUSE.equals(gameStatus.getGameState())) {
                                gameStatus.setGameState(GameState.IN_GAME);
                            } else if (GameState.IN_GAME.equals(gameStatus.getGameState())) {
                                gameStatus.setGameState(GameState.PAUSE);
                            }
                        }
                }
            }
        }
    }

    @Override
    public void pause() {
        if (GameState.IN_GAME.equals(gameStatus.getGameState())) {
            gameStatus.setGameState(GameState.PAUSE);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (gameWrapper != null) gameWrapper.resize(width, height);
        if (runRuntime != null) runRuntime.resize(width, height);
        if (hudViewport != null) hudViewport.update(width, height, true);
        if (gameUserInterface != null) gameUserInterface.resize();
        if (gamePad != null) gamePad.layoutControls();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameWrapper.dispose();
        if (runRuntime != null) runRuntime.dispose();
        gameUserInterface.dispose();
        gamePad.dispose();
        debugConsole.dispose();
    }

    public interface GameScreenComponent {
        void update(float deltaTime);

        void render(float deltaTime);

        void dispose();
    }

}
