package quitru.gnosticchemist.com.github.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class ColorDrawable extends SpriteDrawable {
    static  SpriteDrawable base = new SpriteDrawable(new Sprite(new Texture("branco.png")));

    public ColorDrawable(Color color){
        super(base.tint(color));
    }

    public ColorDrawable(float r, float g, float b, float a){
        super(base.tint(new Color(r,g,b,a)));

    }
}
