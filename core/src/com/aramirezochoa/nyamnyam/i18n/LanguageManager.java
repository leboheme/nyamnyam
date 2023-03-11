package com.aramirezochoa.nyamnyam.i18n;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Created by boheme on 13/01/15.
 */
public enum LanguageManager {
    INSTANCE;

    private I18NBundle languageBundle;

    LanguageManager() {
        languageBundle = I18NBundle.createBundle(Gdx.files.internal("data/i18n/literals"));
    }

    public String getString(String literal) {
        return languageBundle.get(literal);
    }

    public String getString(String literal, String... args) {
        return languageBundle.format(literal, args);
    }
}