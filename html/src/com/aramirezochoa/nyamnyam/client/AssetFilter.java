package com.aramirezochoa.nyamnyam.client;

import com.badlogic.gdx.backends.gwt.preloader.DefaultAssetFilter;

public class AssetFilter extends DefaultAssetFilter {
    @Override
    public boolean preload(String file) {
        return !file.endsWith(".png") || file.startsWith("data/hud/");
    }
}