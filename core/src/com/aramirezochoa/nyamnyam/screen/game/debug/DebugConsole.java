package com.aramirezochoa.nyamnyam.screen.game.debug;

import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public final class DebugConsole extends InputAdapter {
    private static final int MAX_LOG_LINES = 8;
    private static final int MAX_HISTORY_LINES = 24;
    private static final String FONT = "data/game/font-14.fnt";

    private final Viewport viewport;
    private final DebugCommandRegistry commands = new DebugCommandRegistry();
    private final List<String> log = new ArrayList<String>();
    private final List<String> history = new ArrayList<String>();
    private final StringBuilder input = new StringBuilder();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private int historyIndex;
    private boolean active;
    private boolean toggledByKeyDown;
    private boolean suppressMenuInput;

    public DebugConsole(MediaManager mediaManager, Viewport viewport) {
        this.viewport = viewport;
        font = mediaManager.get(FONT);
        registerBuiltInCommands();
        addLog("Debug console ready. Type help for commands.");
    }

    private void registerBuiltInCommands() {
        commands.register(new DebugCommand() {
            @Override
            public String name() {
                return "help";
            }

            @Override
            public String description() {
                return "List available commands";
            }

            @Override
            public String execute(String[] arguments) {
                StringBuilder output = new StringBuilder();
                for (DebugCommand command : commands.commands()) {
                    if (output.length() > 0) output.append("\n");
                    output.append(command.name()).append(" - ").append(command.description());
                }
                return output.toString();
            }
        });
        commands.register(new DebugCommand() {
            @Override
            public String name() {
                return "clear";
            }

            @Override
            public String description() {
                return "Clear the console output";
            }

            @Override
            public String execute(String[] arguments) {
                log.clear();
                return "";
            }
        });
    }

    public void registerCommand(DebugCommand command) {
        commands.register(command);
    }

    @Override
    public boolean keyDown(int keycode) {
        toggledByKeyDown = false;
        if (keycode == Input.Keys.GRAVE) {
            if (!canActivate()) return false;
            toggle();
            toggledByKeyDown = true;
            suppressMenuInput = true;
            return true;
        }
        if (!active) return false;
        suppressMenuInput = true;
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            close();
        } else if (keycode == Input.Keys.ENTER) {
            submit();
        } else if (keycode == Input.Keys.BACKSPACE && input.length() > 0) {
            input.setLength(input.length() - 1);
        } else if (keycode == Input.Keys.UP) {
            previousHistory();
        } else if (keycode == Input.Keys.DOWN) {
            nextHistory();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '`' || character == '~') {
            if (toggledByKeyDown) {
                toggledByKeyDown = false;
                return true;
            }
            if (!canActivate()) return false;
            toggle();
            suppressMenuInput = true;
            return true;
        }
        if (!active) return false;
        if (character >= 0x20 && character != 0x7F) input.append(character);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return active;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return active;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return active;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return active;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return active;
    }

    private void toggle() {
        if (active) {
            close();
            return;
        }
        active = true;
    }

    private boolean canActivate() {
        return Gdx.app.getType() == ApplicationType.Desktop;
    }

    private void close() {
        active = false;
    }

    private void submit() {
        String commandLine = input.toString().trim();
        input.setLength(0);
        historyIndex = history.size();
        if (commandLine.isEmpty()) return;
        addLog("> " + commandLine);
        if (history.isEmpty() || !history.get(history.size() - 1).equals(commandLine)) {
            history.add(commandLine);
            if (history.size() > MAX_HISTORY_LINES) history.remove(0);
        }
        String output = commands.execute(commandLine);
        addLog(output);
    }

    private void previousHistory() {
        if (historyIndex > 0) input.setLength(0);
        if (historyIndex > 0) input.append(history.get(--historyIndex));
    }

    private void nextHistory() {
        if (historyIndex < history.size() - 1) {
            input.setLength(0);
            input.append(history.get(++historyIndex));
        } else if (historyIndex < history.size()) {
            historyIndex = history.size();
            input.setLength(0);
        }
    }

    private void addLog(String message) {
        if (message == null || message.isEmpty()) return;
        for (String line : message.split("\\n")) log.add(line);
        while (log.size() > MAX_LOG_LINES) log.remove(0);
    }

    public void render() {
        if (!active) return;
        viewport.apply();
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        float panelHeight = Math.min(height * 0.42f, 180f);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.02f, 0.02f, 0.02f, 0.58f);
        shapeRenderer.rect(0, 0, width, panelHeight);
        shapeRenderer.setColor(0.25f, 0.8f, 0.35f, 0.55f);
        shapeRenderer.rect(0, panelHeight - 2f, width, 2f);
        shapeRenderer.end();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        Color previousColor = new Color(font.getColor());
        font.setColor(0.45f, 1f, 0.5f, 1f);
        float lineHeight = font.getLineHeight() + 3f;
        float y = lineHeight * 2f + 8f;
        for (int index = log.size() - 1; index >= 0 && y < panelHeight - lineHeight; index--) {
            font.draw(batch, log.get(index), 12f, y);
            y += lineHeight;
        }
        font.draw(batch, "> " + input + "_", 12f, lineHeight + 5f);
        font.setColor(previousColor);
        batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
    }

    public boolean consumeMenuInput() {
        boolean suppress = suppressMenuInput;
        suppressMenuInput = false;
        return suppress;
    }
}
