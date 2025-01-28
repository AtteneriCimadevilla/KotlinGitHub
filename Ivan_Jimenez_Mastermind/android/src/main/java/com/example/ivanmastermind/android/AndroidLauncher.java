package com.example.ivanmastermind.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.example.ivanmastermind.MastermindGame;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        // Configuraciones específicas para móviles
        config.useImmersiveMode = true; // Pantalla completa
        config.useWakelock = true;      // Evitar que la pantalla se apague

        initialize(new MastermindGame(), config);
    }
}
