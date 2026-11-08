package com.aramirezochoa.nyamnyam.client;

import org.teavm.extension.Autoregistered;
import org.teavm.extension.spi.reflection.SimpleReflectionPolicy;

@Autoregistered
public final class TeaVMReflectionPolicy extends SimpleReflectionPolicy {
    @Override
    protected void setup() {
        selectPackage("com.aramirezochoa.nyamnyam.screen.game.core.entity", true)
            .foundByName()
            .reflectableMembers(member -> true);
    }
}
