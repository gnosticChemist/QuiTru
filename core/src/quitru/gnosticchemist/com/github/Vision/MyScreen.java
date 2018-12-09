package quitru.gnosticchemist.com.github.Vision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import quitru.gnosticchemist.com.github.Control.CustomUtils;

/**
 *  Generic Screen for code reduction purpose
 */
public class MyScreen implements Screen {
    static SpriteBatch batch = new SpriteBatch();//Object that draws the Background
    Stage stage;                                 //User Interface controller and drawer
    int width, height;                           //Screen sizes

    public MyScreen(){
        stage = new Stage();

        //Screen size setting
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
    }

    /**
     * Called when the view screen change and this screen is showed,
     * so it must change the input processor
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Method for render the screen, by rendering the background (batch)
     * and the user interface (stage)
     * @param delta
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(CustomUtils.background, 0, 0,width,height);
        batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
