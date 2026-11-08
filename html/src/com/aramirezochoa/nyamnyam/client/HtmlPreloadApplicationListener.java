package com.aramirezochoa.nyamnyam.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetInstance;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoader;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoaderListener;

public final class HtmlPreloadApplicationListener extends ApplicationAdapter {
    private AssetLoader assetLoader;
    private boolean manifestQueued;

    @Override
    public void create() {
        assetLoader = AssetInstance.getLoaderInstance();
        assetLoader.preload(new AssetLoaderListener<Void>() {
            @Override
            public void onSuccess(String url, Void result) {
                manifestQueued = true;
            }

            @Override
            public void onFailure(String url) {
                WebApplication.get().setPreloadReady();
            }
        });
    }

    @Override
    public void render() {
        if (manifestQueued && assetLoader.getQueue() == 0 && !assetLoader.isDownloading()) {
            manifestQueued = false;
            WebApplication.get().setPreloadReady();
        }
    }
}
