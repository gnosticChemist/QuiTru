package quitru.gnosticchemist.com.github.Model;

import com.badlogic.gdx.Game;

import quitru.gnosticchemist.com.github.Vision.TelaInicial;

public class Starter extends Game {

    public void create () {
        this.setScreen(new TelaInicial(this));
    }

    public void render () {
        super.render();
    }

    public void dispose () {
    }
}
