package quitru.gnosticchemist.com.github.Vision;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Arrays;

import quitru.gnosticchemist.com.github.Control.CustomUtils;
import quitru.gnosticchemist.com.github.Control.GameController;
import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.PlayerBot;

public class TelaInicial implements Screen {
	private Stage stage;
	static final byte VIEW_MAIN = 0, VIEW_CREDITS = 1, VIEW_OUTSIDE = 2, VIEW_GAME1 = 3, VIEW_OPTIONS = 4, VIEW_GAME2 = 5, VIEW_WIFI = 6, VIEW_SAIR = 7;
	byte actualState = VIEW_OUTSIDE;

	Game game; //Screen manager
	SpriteBatch batch; //Grapichs manager
	Texture background, title; //Images
	TextButton bJogar, bBaralho, bCreditos, bOpcoes; //Buttons
	TextButton bOpcoesDeJogo[] = new TextButton[4];
	int wButton, hButton, xButton, yButton; //Coords of Buttons (Responsive)

	//Setting to fit the View
	int width, height, xTitle, yTitle, wTitle, hTitle;

	public TelaInicial(Game game){
		//Graphics and Outside things
		this.game = game;
		stage = new Stage(new ScreenViewport());
		batch = new SpriteBatch();

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		//Loading files
		background = new Texture("Fundo.jpg");
		title = new Texture("Title.png");
		Skin skin = new Skin(Gdx.files.internal("skin.json"));

		//Screen size calculations
		int sizeView = Gdx.graphics.getWidth();
		width = Gdx.graphics.getWidth();
		height =  Gdx.graphics.getHeight();

		//Title position and size calculation
		yTitle =(height+width)/2;
		xTitle = width/2;
		wTitle = 3*width/4;
		hTitle = wTitle *title.getHeight()/title.getWidth();
		xTitle -= wTitle /2;
		yTitle -= hTitle /2;

		//Buttons base positions and size calculation
		wButton = 2*sizeView/3;
		hButton = wButton/4;
		xButton = width -wButton;
		yButton = height/2 ;

		//Button font and style configuration
		TextButton.TextButtonStyle buttonStyle = skin.get(TextButton.TextButtonStyle.class);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ubuntu-bold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = hButton*3/4;
		buttonStyle.font = generator.generateFont(parameter);

		//Buttons Configuration
			bJogar = new TextButton("Jogar ",buttonStyle);
			bJogar.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    TelaInicial.this.changeState(TelaInicial.VIEW_OPTIONS);
                }
            });
			bJogar.getLabel().setAlignment(Align.right);
			bJogar.setSize(wButton,hButton);
			stage.addActor(bJogar);
			//bJogar.setPosition(xStartAction,yButton);
			//bJogar.addAction(Actions.moveTo(xButton,yButton,1));

			bBaralho = new TextButton("Baralho ",buttonStyle);
			bBaralho.getLabel().setAlignment(Align.right);
			bBaralho.setSize(wButton,hButton);
			bBaralho.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					TelaInicial.this.changeState(VIEW_OUTSIDE);
				}
			});
			stage.addActor(bBaralho);

			bCreditos = new TextButton("Creditos ",buttonStyle);
			bCreditos.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					TelaInicial.this.changeState(VIEW_CREDITS);
				}
			});
			bCreditos.getLabel().setAlignment(Align.right);
			bCreditos.setSize(wButton,hButton);
			stage.addActor(bCreditos);

			bOpcoes = new TextButton("Sair ",buttonStyle);
			bOpcoes.getLabel().setAlignment(Align.right);
			bOpcoes.setSize(wButton,hButton);
			bOpcoes.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					TelaInicial.this.changeState(VIEW_SAIR);
				}
			});
			stage.addActor(bOpcoes);

			bOpcoesDeJogo[0] = new TextButton("1 Player ", buttonStyle);
			bOpcoesDeJogo[0].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					TelaInicial.this.changeState(VIEW_GAME1);
				}
			});
			bOpcoesDeJogo[1] = new TextButton("2 Players", buttonStyle);
			bOpcoesDeJogo[1].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					TelaInicial.this.changeState(VIEW_GAME2);
				}
			});
			bOpcoesDeJogo[2] = new TextButton("Jogo LAN", buttonStyle);
			bOpcoesDeJogo[2].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					TelaInicial.this.changeState(VIEW_WIFI);
				}
			});
			bOpcoesDeJogo[3] = new TextButton("Voltar ", buttonStyle);
			bOpcoesDeJogo[3].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					TelaInicial.this.changeState(VIEW_MAIN);
				}
			});

			for(TextButton button : bOpcoesDeJogo){
				button.getLabel().setAlignment(Align.right);
				button.setSize(wButton,hButton);
				button.setPosition(width,0);
				stage.addActor(button);
			}

		changeState(VIEW_MAIN);
	}

	public void regainFocus(){
		game.setScreen(this);
		changeState(VIEW_MAIN);
	}

	private void changeState(byte newState){
		if(newState==actualState)return;
		float timeBacking = 0;
		switch (actualState){
			case VIEW_MAIN:
				Gdx.input.setCatchBackKey(false);
				int yStep = -3*hButton/2, yTemp = yButton;
				bJogar.addAction(Actions.moveTo(width,yTemp,1));
				yTemp+=yStep;
				bBaralho.addAction(Actions.sequence(Actions.delay(0.25f),Actions.moveTo(width, yTemp,1)));
				yTemp+=yStep;
				bCreditos.addAction(Actions.sequence(Actions.delay(0.5f),Actions.moveTo(width,yTemp,1)));
				yTemp+=yStep;
				bOpcoes.addAction(Actions.sequence(Actions.delay(0.75f),Actions.moveTo(width,yTemp,1)));
				timeBacking = 1;
			break;
			case VIEW_OPTIONS:
				yStep = -3*hButton/2; yTemp = yButton;
				float timeDelay = 0;
				for(TextButton button : bOpcoesDeJogo){
					button.addAction(Actions.sequence(Actions.delay(timeDelay),Actions.moveTo(width, yTemp,1)));
					yTemp+=yStep;
					timeDelay+=0.25f;
				}
				if(newState==VIEW_MAIN)timeBacking = 1;
				else timeBacking=1.75f;
			break;
		}
		switch (newState){
			case VIEW_MAIN:
				Gdx.input.setCatchBackKey(false);
				int yStep = -3*hButton/2, yTemp = yButton;
				bJogar.setPosition(width,yTemp);
				bJogar.addAction(Actions.delay(timeBacking,Actions.moveTo(xButton,yTemp,1)));
				yTemp+=yStep;
				bBaralho.setPosition(width,yTemp);
				bBaralho.addAction(Actions.sequence(Actions.delay(0.25f + timeBacking),Actions.moveTo(xButton, yTemp,1)));
				yTemp+=yStep;
				bCreditos.setPosition(width,yTemp);
				bCreditos.addAction(Actions.sequence(Actions.delay(0.5f + timeBacking),Actions.moveTo(xButton,yTemp,1)));
				yTemp+=yStep;
				bOpcoes.setPosition(width,yTemp);
				bOpcoes.addAction(Actions.sequence(Actions.delay(0.75f + timeBacking),Actions.moveTo(xButton,yTemp,1)));
			break;
			case VIEW_OUTSIDE:
				stage.addAction(Actions.delay(timeBacking+0.5f,Actions.run(()->{
					game.setScreen(new TelaBaralho(game,this));
				})));
			break;
			case VIEW_CREDITS:
				stage.addAction(Actions.sequence(Actions.delay(timeBacking + 0.25f),Actions.run(() ->{
					game.setScreen(new TelaCreditos(game,this));
				})));
			break;
			case VIEW_WIFI:
				stage.addAction(Actions.sequence(Actions.delay(timeBacking + 0.25f),Actions.run(() ->{
					game.setScreen(new TelaConexao(game,this));
				})));
			break;
			case VIEW_OPTIONS:
				yStep = -3*hButton/2; yTemp = yButton;
				float timeDelay = timeBacking;
				for(TextButton button : bOpcoesDeJogo){
					button.setPosition(width,yTemp);
					button.addAction(Actions.sequence(Actions.delay(timeDelay),Actions.moveTo(xButton, yTemp,1)));
					yTemp+=yStep;
					timeDelay+=0.25f;
				}
			break;
			case VIEW_GAME1:
				stage.addAction(Actions.sequence(Actions.delay(timeBacking),Actions.run(()->{
					TelaGame nextTela = new TelaGame(game,this);
					Element[] elementos = CustomUtils.getSelectedDeck();
					for(Element e : elementos) System.out.println(e.getSymbol());
					GameController gameController = new GameController(new ArrayList<Element>(Arrays.asList(elementos)), nextTela,new PlayerBot());
					game.setScreen(nextTela);
					gameController.startGame();
				})));
			break;
			case VIEW_GAME2:
				stage.addAction(Actions.sequence(Actions.delay(timeBacking),Actions.run(()->{
					TelaGameTwoPlayers nextTela2 = new TelaGameTwoPlayers(game,this);
					Json json = new Json();
					Element[] elementos = CustomUtils.getSelectedDeck();
					GameController gameController = new GameController(new ArrayList<Element>(Arrays.asList(elementos)), nextTela2,nextTela2.otherPlayer);
					gameController.startGame();
				})));
			break;
			case VIEW_SAIR:
				stage.addAction(Actions.delay(timeBacking+0.5f,Actions.run(()->{
					System.exit(0);
				})));
			break;
		}
		actualState = newState;
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
		batch.draw(background, 0, 0,width,height);
		batch.draw(title, xTitle, yTitle, wTitle, hTitle);
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
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
