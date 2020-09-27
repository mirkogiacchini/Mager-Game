package it.MirkoGiacchini.menu;

import it.MirkoGiacchini.util.Util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

/**
 * etichetta
 * @author Mirko
 *
 */
public class Label implements Disposable
{
  public static GlyphLayout glyph = new GlyphLayout();
  public static final float INFW = 9999999f;
  String text;
  public Menu menu;
  float x, y;
  BitmapFont font;
  
  float offset;
  float maxWidth;
  
  private Color actColor;
  private float actSX, actSY;
  
  /**
   * @param s testo iniziale
   * @param x coordinata x testo
   * @param y coordinata y testo
   * @param c colore testo
   * @param sizeX dimensione testo
   * @param sizeY dimensione testo
   * @param maxWidth massima larghezza raggiungibile
   * @param fontPath font
   * @param offset costante proporzionalità del font
   */
  public Label(String s, float x, float y, Color c, float sizeX, float sizeY, float maxWidth, String fontPath, float offset)
  {
	text = s;
	this.offset = offset;
	this.x = x;
	this.y = y;
	font = new BitmapFont(Util.getHandle(fontPath));
	setColor(c);
	setSize(sizeX, sizeY);
	font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	this.maxWidth = maxWidth;
  }
  
  public Label(String s, float x, float y, Color c, float sx, float sy, float maxWidth, BitmapFont fnt, float offset)
  {
	this.font = fnt;  
	text = s;
	this.offset = offset;
	this.x = x;
	this.y = y;
	this.maxWidth = maxWidth;
	setSize(sx, sy);
	setColor(c);
  }
  
  public Label(String s, float sx, float sy, BitmapFont font, float offset)
  {
	this(s, 0, 0, Color.BLACK, sx, sy, INFW, font, offset);  
  }
  
  public Label(String s, Color c, String fontPath, float offset)
  {
	this(s, 0, 0, c, 1, 1, INFW, fontPath, offset);  
  }
  
  public Label(String s, String fontPath, float offset)
  {
	this(s, 0, 0, Color.BLACK, 1, 1, INFW, fontPath, offset);  
  }
  
  public Label(String s, float sizeX, float sizeY, float maxWidth, String fontPath, float offset)
  {
	this(s, 0, 0, Color.BLACK, sizeX, sizeY, maxWidth, fontPath, offset);  
  }
  
  public Label(String s, float x, float y, Color c, float sizeX, float sizeY, float maxWidth, String fontPath)
  {
	this(s, x, y, c, sizeX, sizeY, maxWidth, fontPath, .5f);  
  }
  
  public Label(String s, float sizeX, float sizeY, float maxWidth, String fontPath)
  {
	this(s, sizeX, sizeY, maxWidth, fontPath, .5f);  
  }
  
  public void set(String s, float x, float y, float sizeX, float sizeY, float maxWidth)
  {
	setText(s);
	setX(x);
	setY(y);
	setSize(sizeX, sizeY);
	setMaxWidth(maxWidth);
  }
  
  public void setText(String s)
  {
	text = s;  
  }
  
  public void setMaxWidth(float mw)
  {
	maxWidth = mw;  
  }
  
  public float getMaxWidth()
  {
	return maxWidth;  
  }
  
  public String getText()
  {
	return text;  
  }
  
  public void setColor(Color c)
  {
	font.setColor(c);  
	actColor = c;
  }

  public void setSize(float sizeX, float sizeY)
  {
	font.getData().setScale(sizeX * offset, sizeY * offset);
	actSX = sizeX;
	actSY = sizeY;
  }
  
  public void draw(SpriteBatch batch)
  { 
	setSize(actSX, actSY);
	setColor(actColor);
	font.draw(batch, text, x, y, 0, text.length(), maxWidth, Align.left, true);
	//font.getData().drawWrapped(batch, text, x, y, maxWidth);
  }
  
  public void setX(float x)
  {
	this.x = x;  
  }
  
  public void setY(float y)
  {
	this.y = y;  
  }
  
  public float getX()
  {
	return x;  
  }
  
  public float getY()
  {
	return y;  
  }
  
  public float getHeight()
  {
	glyph.setText(this.font, getText());
	return glyph.height;
	//return font.getData().getBounds(getText()).height;  
  }
  
  public float getWidth()
  {
	glyph.setText(this.font, getText());
	return glyph.width;
	//return font.getBounds(getText()).width;  
  }
  
  @Override
  public void dispose()
  {
	font.dispose();  
  }
}
