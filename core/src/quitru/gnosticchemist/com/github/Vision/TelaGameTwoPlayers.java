package quitru.gnosticchemist.com.github.Vision;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import quitru.gnosticchemist.com.github.Control.CustomUtils;
import quitru.gnosticchemist.com.github.Model.Card;
import quitru.gnosticchemist.com.github.Model.ColorDrawable;
import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.Player;


public class TelaGameTwoPlayers extends TelaGame {
    TelaGameTwoPlayers otherPlayer;
    Redirect redirect;
    private static int ID = 1;
    int id;
    public TelaGameTwoPlayers(Game game, TelaInicial telaInicial) {
        super(game, telaInicial);
        id = ID++;
        redirect = new Redirect(game);
        otherPlayer = new TelaGameTwoPlayers(game, telaInicial, this,redirect);
        redirect.waitPlayer(this);
        redirect.firstThrow = true;
        playerCardsXY[0][0] = (width-cardWidth)/2;
        playerCardsXY[0][1] = height;
        playerCardsXY[1][1] = height;
    }

    private TelaGameTwoPlayers(Game game, TelaInicial telaInicial, TelaGameTwoPlayers otherPlayer, Redirect redirect) {
        super(game, telaInicial);
        id = ID++;
        this.redirect = redirect;
        this.otherPlayer = otherPlayer;
        playerCardsXY[0][0] = (width-cardWidth)/2;
        playerCardsXY[0][1] = height;
        playerCardsXY[1][1] = height;
    }

    Runnable change = () -> {redirect.waitPlayer(otherPlayer);};
    @Override
    void throwCard(){
        System.out.println("--> " + selectionLocked + " " + throwLocked);
        if(selectionLocked || throwLocked || selected == null)return;
        super.throwCard();
        underBar.addAction(Actions.delay(timeDelay+2,Actions.run(change)));
    }

    @Override
    public void updateResult(Element element[], Player winner){
        if(redirect.isActualPlayer(this)){
            if(id == 2)element = new Element[]{element[1],element[0]};
            Element[] finalElement = element;
            underBar.addAction(Actions.delay(timeDelay + 0.5f,Actions.run(()->{redirect.updateResult(finalElement,(TelaGameTwoPlayers)winner);})));
        }
        returnSelected();
    }

    @Override
    public void giveCard(Element element) {
        super.giveCard(element);
        attributeTurn.addAction(Actions.run(()->{
            attTurn = null;
            attributeTurn.setText("--");
        }));
    }

    @Override
    public void updateGameEnd(Player winner){
        redirect.updateGameEnd(winner);
    };
    @Override
    public void updateEnemyChoosing(){};
    @Override
    public void updateEnemyChoosed(){};

    @Override
    void updateStats(){
        super.updateStats();
        redirect.updateStats();
    }
}

class Redirect implements Screen {
    private Game game;
    private TelaGameTwoPlayers nextPlayer; TelaGameTwoPlayers actualPlayer;
    private Card card1, card2;
    private float cardsCoords[][] = new float[2][2];
    private TextButton buttonNext;
    private Button buttonJump;
    private Stage stage;
    Label attributeTurn, gameStats;
    String texto = "Vez do jogador ";
    int width;
    float cardWidth, scale;
    private TelaGameTwoPlayers winner;

    public Redirect(Game game){
        super();
        this.game = game;

        stage = new Stage();

        width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        float heightBar = Gdx.graphics.getDensity()*160*0.4f;

        Image background = new Image(CustomUtils.background);
        background.setSize(width,height);
        stage.addActor(background);

        cardWidth = width*0.45f;
        card1 = new Card();
        card2 = new Card();
        scale = cardWidth/card1.getWidth();
        card1.setScale(scale);
        card2.setScale(scale);
        float heigthB = height - heightBar;
        cardsCoords[0][0] = (width-cardWidth)/2;
        cardsCoords[0][1] = heigthB/2 + (heigthB/2 - card1.getHeight()*scale)/2;
        cardsCoords[1][0] = cardsCoords[0][0];
        cardsCoords[1][1] = (heigthB/2 - card1.getHeight()*scale)/2;
        card1.setPosition(-cardWidth,cardsCoords[0][1]);
        card2.setPosition(width,cardsCoords[1][1]);
        stage.addActor(card1);
        stage.addActor(card2);

        BitmapFont font = CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold,96);
        font.getData().setScale(heightBar/96);
        font.setColor(Color.WHITE);

        //Labels
        Label.LabelStyle labelStyle = new Label.LabelStyle(CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) (heightBar/2)),Color.WHITE);
        labelStyle.background = new ColorDrawable(0,0,0,0.5f);

        attributeTurn = new Label("--",labelStyle);
        attributeTurn.setAlignment(Align.center);
        attributeTurn.setBounds(0,height - heightBar,width,heightBar/2);
        stage.addActor(attributeTurn);

        gameStats = new Label("0 x 0 | Cartas: 30",labelStyle);
        gameStats.setAlignment(Align.center);
        gameStats.setBounds(0,height - heightBar/2,width,heightBar/2);
        stage.addActor(gameStats);

        //Buttons
        buttonJump = new Button(new ColorDrawable(0,0,0,0));
        buttonJump.setSize(width,height);
        buttonJump.setPosition(0,0);
        buttonJump.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                card1.addAction(Actions.moveTo(-cardWidth,cardsCoords[0][1],0.5f));
                card2.addAction(Actions.moveTo(width,cardsCoords[1][1],0.5f));
                buttonJump.addAction(Actions.delay(0.5f,Actions.run(()->{
                    buttonJump.toBack();
                    buttonNext.toFront();
                    card2.setFliped(true);
                    card2.setScale(scale);
                    card1.setFliped(true);
                    card1.setScale(scale);
                    firstThrow = true;
                    waitPlayer(winner);
                    firstThrow = true;
                })));
            }
        });
        stage.addActor(buttonJump);
        buttonJump.toBack();

        Drawable draw = new ColorDrawable(0,0,0,0.75f);
        buttonNext = new TextButton(texto,new TextButton.TextButtonStyle(draw,draw,draw,font));
        buttonNext.setSize(width,height);
        buttonNext.setPosition(0,0);
        buttonNext.getLabel().setAlignment(Align.center);
        buttonNext.getLabel().setWrap(true);
        buttonNext.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(nextPlayer == null)return;
                actualPlayer = nextPlayer;
                nextPlayer.updateStats();
                game.setScreen(nextPlayer);
                Gdx.input.setInputProcessor(nextPlayer.stage);
            }
        });
        stage.addActor(buttonNext);
    }

    boolean firstThrow = true;
    public void waitPlayer(TelaGameTwoPlayers nextPlayer){
        this.nextPlayer = nextPlayer;
        if(!firstThrow)return;
        firstThrow = false;
        buttonNext.setText(texto + nextPlayer.id);
        game.setScreen(this);
        Gdx.input.setInputProcessor(stage);
    }

    private Action resizeInPlace(Card card,float scale){
        float deltaX = card.getWidth()*(card.getScaleX()-scale)/2,
        deltaY = card.getHeight()*(card.getScaleY() - scale)/2;
        return Actions.delay(2,Actions.parallel(Actions.moveBy(deltaX,deltaY,0.5f),Actions.scaleTo(scale,scale,0.5f)));
    }

    private Action generateFlip(Card card,float x, float y){
        float scale = card.getScaleX();
        float tX = x + card.getWidth()*scale/2;
        Action flip = Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(0,scale,0.5f),
                        Actions.moveTo(tX,y,0.5f)
                ),
                Actions.run(() ->{card.flip();}),
                Actions.parallel(
                        Actions.scaleTo(scale,scale,0.5f),
                        Actions.moveTo(x,y,0.5f)
                )
        );
        return flip;
    }

    void updateResult(Element element[], TelaGameTwoPlayers winner){
        game.setScreen(this);
        Gdx.input.setInputProcessor(stage);
        buttonNext.toBack();

        card1.setElement(element[0]);
        card2.setElement(element[1]);
        card1.addAction(Actions.sequence(Actions.moveTo(cardsCoords[0][0],cardsCoords[0][1],0.5f),generateFlip(card1,cardsCoords[0][0],cardsCoords[0][1])));
        card2.addAction(Actions.sequence(Actions.moveTo(cardsCoords[1][0],cardsCoords[1][1],0.5f),generateFlip(card2,cardsCoords[1][0],cardsCoords[1][1])));
        if(winner == null){
            card1.addAction(Actions.delay(2,resizeInPlace(card1,scale-0.05f)));
            card2.addAction(Actions.delay(2,resizeInPlace(card2,scale-0.05f)));
            winner = nextPlayer;
        }else if(winner.id == 1) card2.addAction(Actions.delay(2,resizeInPlace(card2,scale-0.05f)));
        else card1.addAction(Actions.delay(2,resizeInPlace(card1,scale-0.05f)));

        this.winner = winner;
        buttonJump.addAction(Actions.delay(3f,Actions.run(()->{
            buttonJump.toFront();
        })));
    }

    void updateStats() {
        gameStats.setText(nextPlayer.gameStats.getText());
        attributeTurn.setText(Element.attributeName(nextPlayer.gc.getTurnAttribute()));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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

    public boolean isActualPlayer(TelaGameTwoPlayers telaGameTwoPlayers) {
        return telaGameTwoPlayers.id == actualPlayer.id;
    }

    public void updateGameEnd(Player winner) {
        String ganhador;
        if(winner == null) ganhador = " Empate !";
        else {
            TelaGameTwoPlayers ttpWinner = (TelaGameTwoPlayers)winner;
            ganhador = "O jogador " + ttpWinner.id + " ganhou !";
        }
        buttonNext.setText(ganhador);
        buttonNext.toFront();
        nextPlayer = null;
        buttonNext.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Redirect.this.actualPlayer.telaInicial.regainFocus();
                Redirect.this.actualPlayer.otherPlayer.dispose();
                Redirect.this.actualPlayer.dispose();
                Redirect.this.dispose();
            }
        });

    }
}
