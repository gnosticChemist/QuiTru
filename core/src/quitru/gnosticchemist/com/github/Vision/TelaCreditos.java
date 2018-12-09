package quitru.gnosticchemist.com.github.Vision;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;

import quitru.gnosticchemist.com.github.Control.CustomUtils;
import quitru.gnosticchemist.com.github.Model.ColorDrawable;

public class TelaCreditos extends MyScreen{
    Game game;
    TelaInicial previous;
    InputMultiplexer multiplexer;

    public TelaCreditos(Game game, TelaInicial previous){
        super();
        this.previous = previous;
        this.game = game;

        float cm025x = CustomUtils.cmToPixel(0.25f,true);
        float cm025y = CustomUtils.cmToPixel(0.25f,false);
        Image image = new Image(new ColorDrawable(0,0,0,0.75f));
        image.setBounds(cm025x,cm025y,width-2*cm025x,height-2*cm025y);
        stage.addActor(image);

        String text =
        "[#4D9C9DFF]CRÉDITOS[]\n\n" +
                "Jackson Renato Faquineti Rampazzo\n" +
                "[#4D9C9DFF]Criação, programação e design[]\n\n" +
                "Felipe Augusto Gorla\n" +
                "[#4D9C9DFF]Idealização e orientação[]\n\n" +
                "BadLogic games\n" +
                "[#4D9C9DFF]Framework do jogo (libGDX)[]\n\n" +
                "Nathan Sweet\n" +
                "[#4D9C9DFF]Biblioteca de rede (KryoNet)[]";

        Label label = new Label(text,new Label.LabelStyle(CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) (3*cm025y)),Color.WHITE));
        label.setWrap(true);
        label.getStyle().font.getData().markupEnabled = true;
        label.setAlignment(Align.center);
        label.setBounds(2*cm025x,2*cm025y,width-4*cm025x,height -4*cm025y);

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(null,null,null,null,null);
        ScrollPane scroll = new ScrollPane(label,scrollStyle);
        scroll.setScrollingDisabled(true,false);
        scroll.setBounds(2*cm025x,2*cm025y,width-4*cm025x,height -4*cm025y);

        stage.addActor(scroll);

        Gdx.input.setCatchBackKey(true);
        InputProcessor processor = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE || keycode == Input.Keys.ESCAPE){
                    game.setScreen(previous);
                    previous.regainFocus();
                    TelaCreditos.this.dispose();
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };

        multiplexer = new InputMultiplexer(processor,stage);
    }
    @Override
    public void show(){
        Gdx.input.setInputProcessor(multiplexer);
    }
}
