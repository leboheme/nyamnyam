package emu.com.badlogic.gdx.utils.async;

import emu.com.badlogic.gdx.assets.AssetLoadingTask;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

public class AsyncResult<T> implements TimerHandler {
    private AssetLoadingTask loadingTask;
    private RuntimeException failure;
    private boolean done;
    private int count;

    AsyncResult(AsyncTask<T> task) {
        loadingTask = (AssetLoadingTask)task;
        Window.setTimeout(this, 0);
    }

    public boolean isDone() {
        count++;
        if(count > 2)
            tick();
        return done;
    }

    public T get() {
        if(failure != null)
            throw failure;
        return null;
    }

    @Override
    public void onTimer() {
        tick();
    }

    private void tick() {
        // finishLoading() may run the task before the scheduled browser timer fires.
        if(done)
            return;

        try {
            loadingTask.call();
        } catch(Exception error) {
            failure = new RuntimeException(error);
        } finally {
            loadingTask = null;
            done = true;
        }
    }
}
