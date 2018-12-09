package quitru.gnosticchemist.com.github.Vision;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Collections;

import quitru.gnosticchemist.com.github.Control.CustomUtils;
import quitru.gnosticchemist.com.github.Control.GameController;
import quitru.gnosticchemist.com.github.Model.Card;
import quitru.gnosticchemist.com.github.Model.ColorDrawable;
import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.Player;

public class TelaGame extends Player implements Screen {
    Game game;
    TelaInicial telaInicial;
    SpriteBatch batch;
    Stage stage;
    Texture background;
    ColorDrawable blackBackground =  new ColorDrawable(0,0,0,0.5f);

    int width, height;
    float xCards[],xCardsSelected[];
    int zOrder[] = {1,3,5,4,1};
    float heightBar;
    float playerCardsXY[][] = new float[2][2];
    float cardWidth;
    float scaleSelected = 0.25f, scaleNormal = 0.2f;

    Card selected, enemy;
    ArrayList<Card> cartasMao;
    AttributeChooser chooser;
    String attriuteTurnString = "";
    Label attributeTurn, gameStats, waitinhChoose;
    Image underBar;
    GameController gc;
    float chooserY, waitX, waitY;
    Element.Attributes attTurn;
    Label backgroundAttribute;
    boolean selectionLocked = false, throwLocked = true;
    Runnable unlockSelection = () -> {selectionLocked = false;};
    Runnable lockSelection = () -> {selectionLocked = true;};
    public TelaGame(Game game, TelaInicial telaInicial){
        super();
        this.game = game;
        this.telaInicial = telaInicial;
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture("Fundo.jpg");


        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        heightBar = Gdx.graphics.getDensity()*160*0.4f;
        cartasMao = new ArrayList<Card>();

        //Botões
        ImageButton setaEsquerda = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("SetaEsquerda.png"))),new TextureRegionDrawable(new TextureRegion(new Texture("SetaEsquerdaDown.png"))));
        ImageButton setaDireita = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("SetaDireita.png"))),new TextureRegionDrawable(new TextureRegion(new Texture("SetaDireitaDown.png"))));
        ImageButton jogarCarta = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("BotaoCarta.png"))),new TextureRegionDrawable(new TextureRegion(new Texture("BotaoCartaDown.png"))));

        setaEsquerda.setBounds(heightBar/4,0,heightBar,heightBar);
        setaEsquerda.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(selectionLocked)return;
                if (selected == null) changeSelected(cartasMao.get(2));
                else {
                    int i = cartasMao.indexOf(selected);
                    if (i > 0) changeSelected(cartasMao.get(--i));
                    else changeSelected(cartasMao.get(4));
                }
            }
        });

        setaDireita.setBounds(width-5*heightBar/4,0,heightBar,heightBar);
        setaDireita.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(selectionLocked)return;
                if(selected==null)changeSelected(cartasMao.get(2));
                else{
                    int i = cartasMao.indexOf(selected);
                    if(i<4)changeSelected(cartasMao.get(++i));
                    else changeSelected(cartasMao.get(0));
                }
            }
        });

        jogarCarta.setHeight(heightBar);
        jogarCarta.setPosition((width - jogarCarta.getWidth())/2,0);
        jogarCarta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                throwCard();
            }
        });

        underBar = new Image(blackBackground);
        underBar.setBounds(0,0,width,heightBar);
        stage.addActor(underBar);
        stage.addActor(setaDireita);
        stage.addActor(setaEsquerda);
        stage.addActor(jogarCarta);

        cartasMao.add(new Card());
        cartasMao.add(new Card());
        cartasMao.add(new Card());
        cartasMao.add(new Card());
        cartasMao.add(new Card());

        //cardWidth = cartasMao.get(0).getWidth()*0.2f;
        cardWidth = width*0.36f;
        scaleNormal = cardWidth/cartasMao.get(0).getWidth();
        float cardHeight = cartasMao.get(0).getHeight()*scaleNormal;
        xCards = new float[]{0, (width / 3 - cardWidth / 2), (width - cardWidth) / 2, (2 * width / 3 - cardWidth / 2), width - cardWidth};
        //cardWidth = cartasMao.get(0).getWidth()*scaleSelected;
        cardWidth = width*0.45f;
        scaleSelected = cardWidth/cartasMao.get(0).getWidth();
        xCardsSelected = new float[]{0, (width / 3 - cardWidth / 2), (width - cardWidth) / 2, (2 * width / 3 - cardWidth / 2), width - cardWidth};

        int order[] = {0,4,1,3,2};
        for(int i=0;i<5;i++){
            int c = order[i];
            Card card = cartasMao.get(c);
            //card.flip();
            stage.addActor(card);
            card.setPosition(xCards[c],heightBar);
            card.setScale(scaleNormal);
            card.setZIndex(zOrder[c]);
            card.addListener(new InputListener(){
                //Card n = card;
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(!selectionLocked)TelaGame.this.changeSelected(card);
                    return false;
                }
            });
        }

        chooser = new AttributeChooser(width,height - heightBar - cardHeight);
        chooserY = heightBar+cardHeight;
        chooser.setPosition(width,chooserY);
        stage.addActor(chooser);

        Label.LabelStyle labelStyle = new Label.LabelStyle(CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) (heightBar/2)),Color.WHITE);
        labelStyle.background = blackBackground;

        attributeTurn = new Label(attriuteTurnString + "--",labelStyle);
        attributeTurn.setAlignment(Align.center);
        attributeTurn.setBounds(0,height - heightBar,width,heightBar/2);

        gameStats = new Label("0 x 0 | Cartas: 30",labelStyle);
        gameStats.setAlignment(Align.center);
        gameStats.setBounds(0,height - heightBar/2,width,heightBar/2);

        float sizeWC = (height - 3*heightBar - cardHeight);
        labelStyle.font = CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) (sizeWC/9));

        waitinhChoose = new Label("Esperando escolherem o atributo\n...",labelStyle);
        waitX = width/6;
        waitY = heightBar + cardHeight + sizeWC/6;
        waitinhChoose.setBounds(waitX,waitY,2*width/3,2*sizeWC/3);
        waitinhChoose.setAlignment(Align.center);
        waitinhChoose.setWrap(true);
        stage.addActor(waitinhChoose);
        waitinhChoose.toBack();
        waitinhChoose.addAction(Actions.alpha(0));
        //stage.addActor(waitinhChoose);
        //waitinhChoose.addAction(Actions.moveTo(width,height));

        stage.addActor(attributeTurn);
        stage.addActor(gameStats);
        enemy = new Card();
        enemy.setScale(scaleSelected);
        playerCardsXY[0][0] = width/4 -cardWidth/2;
        playerCardsXY[0][1] = (sizeWC-cardHeight)/2 +heightBar +cardHeight;
        playerCardsXY[1][0] = 3*width/4 -cardWidth/2;
        playerCardsXY[1][1] = playerCardsXY[0][1];
        enemy.setPosition(playerCardsXY[1][0],height);
        //enemy.flip();
        stage.addActor(enemy);
        //stage.addActor(chooser);
        //chooser.addAction(Actions.alpha(0));

        //cartasMao.get(2).toFront();
    }

    void throwCard(){
        if(selectionLocked || throwLocked)return;
        if(selected!=null){
            selectionLocked = true;
            throwLocked = true;
            flipCard(selected);
            selected.addAction(Actions.sequence(
                    Actions.delay(1),
                    Actions.moveTo(playerCardsXY[0][0],playerCardsXY[0][1],0.5f),
                    Actions.run(()->{
                        gc.setTurnCard(selected.element,TelaGame.this);
                    })
            ));
        }
    }

    Runnable updateStats = () -> {updateStats();};
    Runnable clearAttribute = () ->{ attTurn = null;updateStats();};
    void updateStats() {
        if(gc!=null)gameStats.setText(gc.getPlacar() + " | Cartas: " + gc.getCards());
        if (attTurn != null)attributeTurn.setText(attriuteTurnString + Element.attributeName(attTurn));
        else attributeTurn.setText(attriuteTurnString + "--");
    }

    Runnable reorderCards = () -> {reorderCards();};
    void reorderCards(){
        cartasMao.get(0).toBack();
        cartasMao.get(4).toBack();
        cartasMao.get(2).toFront();
        if(selected!=null)selected.toFront();
    }

    public void flipCard(Card card){
        float x = card.getX();
        float y = card.getY();
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
        card.addAction(flip);
    }
    /*
    public Action generateFlip(Card card){
        float x = card.getX();
        float y = card.getY();
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
    }*/

    public Action generateFlip(Card card){
        float scale = card.getScaleX();
        float deltaX = card.getWidth()*scale/2;
        Action flip = Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(0,scale,0.5f),
                        Actions.moveBy(deltaX,0,0.5f)
                ),
                Actions.run(() ->{card.flip();}),
                Actions.parallel(
                        Actions.scaleTo(scale,scale,0.5f),
                        Actions.moveBy(-deltaX,0,0.5f)
                )
        );
        return flip;
    }

    public void changeSelected(Card card){
        selectionLocked = true;
        underBar.addAction(Actions.delay(0.6f,Actions.run(unlockSelection)));
        if(selected!=null){
            int i = cartasMao.indexOf(selected);
            selected.addAction(
                    Actions.parallel(Actions.scaleTo(scaleNormal,scaleNormal,0.25f),
                    Actions.moveTo(xCards[i],heightBar,0.25f))
            );
            /*selected.toBack();
            selected.setZIndex(zOrder[i]);ers[0];
                            player.requestCardChoose(this,turnAttribute);
                        break;
                        case SocketPackage.ENEMY_CHOOSED :
                            player.updateEnemyChoosed();
            cartasMao.get(2).toFront();*/
            if(selected.equals(card)){
                selected = null;
                reorderCards();
                return;
            }
            reorderCards();
        }
        selected = card;
        selected.addAction(
                Actions.parallel(Actions.scaleTo(scaleSelected,scaleSelected,0.25f),
                Actions.moveTo(xCardsSelected[cartasMao.indexOf(selected)],heightBar,0.25f))
            );
        //selected.setZIndex(7);
        reorderCards();//selected.toFront();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0,0,width,height);
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
        batch.dispose();
        background.dispose();
    }

    float timeDelay = 0;
    Runnable remove50milis = ()->{timeDelay-=0.5f;};
    boolean chooseTurn = false;
    @Override
    public void requestAttributeChoose(GameController gc) {
        //stage.addActor(chooser);
        //chooser.addAction(Actions.alpha(0));
        System.out.println("Attribute Choose "  + timeDelay);
        chooser.setGameController(gc);
        attributeTurn.addAction(Actions.delay(timeDelay,Actions.alpha(0,0.5f)));
        gameStats.addAction(Actions.delay(timeDelay,Actions.alpha(0,0.5f)));
        chooser.addAction(Actions.sequence(Actions.delay(timeDelay),Actions.moveTo(0,chooserY),Actions.alpha(1,0.5f),Actions.run(remove50milis)));
        timeDelay += 0.5f;
        chooseTurn = true;
    }

    @Override
    public void requestCardChoose(GameController gc, Element.Attributes choosedAttribute) {
        System.out.println("Card Choose " + timeDelay);
        attributeTurn.addAction(Actions.delay(timeDelay+0.5f,Actions.run(()->{
            attTurn = choosedAttribute;
            updateStats();
            throwLocked = false;
        })));
        if(chooseTurn){
            chooseTurn = false;
            chooser.addAction(Actions.sequence(Actions.delay(timeDelay,Actions.alpha(0,0.5f)),Actions.moveTo(width,height),Actions.run(remove50milis)));
            timeDelay += 0.5f;
            attributeTurn.addAction(Actions.delay(timeDelay,Actions.alpha(1,0.5f)));
            gameStats.addAction(Actions.delay(timeDelay,Actions.alpha(1,0.5f)));
        }else //waitinhChoose.addAction(Actions.sequence(Actions.delay(timeDelay,fadeOut),Actions.removeActor()));
            waitinhChoose.addAction(Actions.delay(timeDelay,Actions.alpha(0,0.5f)));
        underBar.addAction(Actions.delay(timeDelay,Actions.run(remove50milis)));
        timeDelay+=0.5f;
        this.gc = gc;

    }

    Runnable flipEnemy = () -> {
        enemy.setFliped(true);
    };
    @Override
    public void updateEnemyChoosed() {
        /*enemy.fliped = true;
        System.out.println("Choosing");
        if(enemy.element!=null){
            System.out.println("AAAA");
            enemy.symbol.setText("PORRA");
        }*/
        System.out.println("Enemy choosed card " + timeDelay);
        enemy.addAction(Actions.sequence(Actions.delay(timeDelay,Actions.run(flipEnemy)),Actions.moveTo(playerCardsXY[1][0],playerCardsXY[1][1],0.5f),Actions.run(()->{timeDelay-=1;})));
        timeDelay+=1;
    }

    @Override
    public void updateResult(Element element[], Player Winner) {
        System.out.println("Update Result " + timeDelay);
        enemy.setElement(element[1]);
        /* selected.addAction(Actions.delay(timeDelay,Actions.run(()->{
            flipCard(enemy);
            flipCard(selected);
            timeDelay-=3;
        })));*/

        selected.addAction(Actions.delay(timeDelay,generateFlip(selected)));
        enemy.addAction(Actions.sequence(Actions.delay(timeDelay,generateFlip(enemy)),Actions.run(()->{timeDelay-=3;})));
        timeDelay+=3;
        attributeTurn.addAction(Actions.delay(timeDelay,Actions.run(clearAttribute)));

        Action loose = Actions.delay(timeDelay,Actions.scaleTo(scaleNormal,scaleNormal,0.5f));
        if(Winner!=null){
            if(Winner.equals(this))enemy.addAction(loose);
            else selected.addAction(loose);
        }else{
            enemy.addAction(loose);
            selected.addAction(loose);
        }
        timeDelay+=0.5f;
        underBar.addAction(Actions.delay(timeDelay,Actions.run(remove50milis)));

        enemy.addAction(Actions.sequence(Actions.delay(timeDelay,Actions.moveTo(width,playerCardsXY[1][1],0.5f)),Actions.scaleTo(scaleSelected,scaleSelected),Actions.moveTo(playerCardsXY[1][0],height)));
        returnSelected();
    }

    void returnSelected(){
        selected.addAction(Actions.sequence(Actions.delay(timeDelay,Actions.moveTo(-cardWidth,playerCardsXY[0][1],0.5f)),Actions.scaleTo(scaleNormal,scaleNormal),Actions.run(remove50milis)));
        timeDelay+=0.5f;

        if(gc.getCards() == 0){
            int index = cartasMao.indexOf(selected);
            boolean move = false;
            switch (gc.getTurns()){
                case 5:
                    if(index!=2){
                        Collections.swap(cartasMao,index,2);
                        move = true;
                    }
                    break;
                case 4:
                    if(index==1){
                        Collections.swap(cartasMao,3,2);
                        move = true;
                        index = 2;
                    }else if(index==3){
                        Collections.swap(cartasMao,1,2);
                        move = true;
                        index = 2;
                    }
                    break;
                case 3:
                    if(index==0){
                        Collections.swap(cartasMao,0,2);
                        move = true;
                        index = 0;
                    }else if(index==4){
                        Collections.swap(cartasMao,4,2);
                        move = true;
                        index = 4;
                    }
                    break;
                case 2:
                    if(index==0){
                        Collections.swap(cartasMao,4,2);
                        move = true;
                        index = 2;
                    }else if(index==4){
                        Collections.swap(cartasMao,0,2);
                        move = true;
                        index = 2;
                    }
                    break;
            }
            if(move)cartasMao.get(index).addAction(Actions.delay(timeDelay,Actions.moveTo(xCards[index],heightBar,0.5f)));

            underBar.addAction(Actions.delay(timeDelay,Actions.sequence(Actions.run(reorderCards),Actions.run(updateStats),Actions.run(unlockSelection),Actions.run(remove50milis))));
            timeDelay+= 0.5f;
            selected = null;
        }
    }

    @Override
    public void updateGameEnd(Player Winner) {
        String text;
        selectionLocked = true;
        throwLocked = true;
        if(Winner != null){
            if(equals(Winner))text = "Você ganhou !!!";
            else text = "Você perdeu ...";
        }else text = "Empate !!!";
        waitinhChoose.setText(text);
        waitinhChoose.addAction(Actions.delay(timeDelay + 0.5f,Actions.sequence(Actions.alpha(1,0.5f),Actions.delay(5,Actions.run(()->{
            telaInicial.regainFocus();
            TelaGame.this.dispose();
        })))));
    }

    @Override
    public void updateEnemyChoosing() {
        System.out.println("Enemy choosing attribute " + timeDelay);
        waitinhChoose.addAction(Actions.delay(timeDelay,Actions.alpha(1,0.5f)));
        timeDelay+=3;
        underBar.addAction(Actions.delay(timeDelay,Actions.run(()->{
            timeDelay-=3;
        })));
    }

    int count = 0;
    @Override
    public void giveCard(Element element) {
        System.out.println("Give card "  + timeDelay);
        super.giveCard(element);
        if(count<5){
            cartasMao.get(count).setElement(element);
            flipCard(cartasMao.get(count));
            count++;
        }else{
            int i = cartasMao.indexOf(selected);
            selected.addAction(Actions.delay(timeDelay,Actions.sequence(Actions.moveTo(xCards[i] - width,heightBar),Actions.run(()->{
                selected.setElement(element);
                selected.setZIndex(zOrder[i]);
                selected.setFliped(false);
                selected = null;
            }),Actions.moveTo(xCards[i],heightBar,0.5f),Actions.run(remove50milis),Actions.run(unlockSelection),Actions.run(reorderCards))));
            timeDelay+=0.5f;
        }
        gameStats.addAction(Actions.delay(timeDelay,Actions.run(updateStats)));
    }
}

class AttributeChooser extends Group{
    private GameController gc;
    public AttributeChooser(float width, float height){
        float bWidth = 2*width/3;
        float textHeight = height/7;
        float bHeight = 2*textHeight/3;
        float gap = textHeight/3;
        float x = (width - bWidth)/2;
        float y = getY() + gap/2;

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ubuntu-bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (width/" Eletronegatividade ".length());
        style.font = generator.generateFont(parameter);
        style.fontColor = Color.WHITE;
        //TextureRegionDrawable draw =  new TextureRegionDrawable(new TextureRegion(new Texture("preto50.png")));
        ColorDrawable draw = new ColorDrawable(0,0,0,0.5f);
        style.up = draw;

        Image image = new Image(draw);
        image.setBounds(getX(),getY(),width,height);
        addActor(image);

        parameter.size = (int) (1.5f*width/"Escolha um atributo".length());
        Label title = new Label("Escolha um atributo", new Label.LabelStyle(generator.generateFont(parameter),Color.WHITE));
        title.setAlignment(Align.center);
        title.setBounds(getX(),height-textHeight,width,textHeight);
        addActor(title);

        TextButton buttonNumber = new TextButton(Element.attributeName(Element.Attributes.Número_atômico),style);
        buttonNumber.getLabel().setAlignment(Align.center);
        buttonNumber.setBounds(x,y,bWidth,bHeight);
        buttonNumber.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gc.setTurnAttribute(Element.Attributes.Número_atômico);
            }
        });
        addActor(buttonNumber);
        y+= bHeight + gap;

        TextButton buttonMass = new TextButton(Element.attributeName(Element.Attributes.Massa_atômica),style);
        buttonMass.getLabel().setAlignment(Align.center);
        buttonMass.setBounds(x,y,bWidth,bHeight);
        buttonMass.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gc.setTurnAttribute(Element.Attributes.Massa_atômica);
            }
        });
        addActor(buttonMass);
        y+= bHeight + gap;

        TextButton buttonIonizacao = new TextButton(Element.attributeName(Element.Attributes.x_Energia_de_ionização),style);
        buttonIonizacao.getLabel().setAlignment(Align.center);
        buttonIonizacao.setBounds(x,y,bWidth,bHeight);
        buttonIonizacao.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gc.setTurnAttribute(Element.Attributes.x_Energia_de_ionização);
            }
        });
        addActor(buttonIonizacao);
        y+= bHeight + gap;

        TextButton buttonRadius = new TextButton(Element.attributeName(Element.Attributes.Raio_atômico),style);
        buttonRadius.getLabel().setAlignment(Align.center);
        buttonRadius.setBounds(x,y,bWidth,bHeight);
        buttonRadius.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gc.setTurnAttribute(Element.Attributes.Raio_atômico);
            }
        });
        addActor(buttonRadius);
        y+= bHeight + gap;

        TextButton buttonFusion = new TextButton(Element.attributeName(Element.Attributes.Temperatura_de_fusão),style);
        buttonFusion.getLabel().setAlignment(Align.center);
        buttonFusion.setBounds(x,y,bWidth,bHeight);
        buttonFusion.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gc.setTurnAttribute(Element.Attributes.Temperatura_de_fusão);
            }
        });
        addActor(buttonFusion);
        y+= bHeight + gap;

        TextButton buttonElectronegavity = new TextButton(Element.attributeName(Element.Attributes.Eletronegatividade),style);
        buttonElectronegavity.getLabel().setAlignment(Align.center);
        buttonElectronegavity.setBounds(x,y,bWidth,bHeight);
        buttonElectronegavity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gc.setTurnAttribute(Element.Attributes.Eletronegatividade);
            }
        });
        addActor(buttonElectronegavity);
    }

    public void setGameController(GameController gameController) {
        this.gc = gameController;
    }
}