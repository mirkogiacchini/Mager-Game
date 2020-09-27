package it.MirkoGiacchini.world3d.heightmap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Disposable;

import it.MirkoGiacchini.world3d.PhysicsEntity;

/**
 * terreno con colline
 * @author Mirko
 *
 */
public class Heightmap extends PhysicsEntity implements Disposable
{ 
  public static final int VERTEX_SIZE = 4;
  float length, maxHeight;
  int width, height;
  float vertices[];
  short indices[];
  Model model;
  Mesh mesh;
  Material mat;
  TextureData data;
	  
  public Heightmap(FileHandle heightmap, Texture texture, float length, float maxHeight) throws Exception
  {
	Pixmap image = new Pixmap(heightmap);
	Pixmap text;
	width = image.getWidth();
	height = image.getHeight();
	
	this.maxHeight = maxHeight;
	this.length = length;
	
	if(width * height > Short.MAX_VALUE) 
	 throw new Exception("File too big! width*height <= "+Short.MAX_VALUE);
		
	data = texture.getTextureData();
	data.prepare();
	text = data.consumePixmap();
		
	vertices = new float[VERTEX_SIZE * width * height];
	indices = new short[6 * (width-1) * (height-1)];
		
	mat = new Material();
	mat.set(TextureAttribute.createDiffuse(texture));
		
	buildIndices();
	buildVertices(image, text);
	buildModel();
	createBody(PhysicsEntity.DEF_VALUE, Bullet.obtainStaticNodeShape(instance.nodes), 0);
  }
	  
  private void buildIndices()
  {
	int ind = 0;
		
	for(int i=0; i<height-1; i++)  
	 for(int j=0; j<width-1; j++)
	 {
	   indices[ind++] = (short) (width*i+j); //alto-sx
	   indices[ind++] = (short) (width*i+j+width); //basso-sx
	   indices[ind++] = (short) (width*i+j+width+1); //basso-dx
		   
	   indices[ind++] = (short) (width*i+j+width+1); //basso-dx
	   indices[ind++] = (short) (width*i+j+1); //alto-dx
	   indices[ind++] = (short) (width*i+j); //alto-sx
     }
  }
	  
  private void buildVertices(Pixmap pixmap, Pixmap texture)
  {
	Color color = new Color();
		
	int ind = 0;
		
	for(int i=0; i<height; i++)
	 for(int j=0; j<width; j++)
	 {
	   Color.rgba8888ToColor(color, pixmap.getPixel(j, i));
	   vertices[ind++] = j * length; //x
	   vertices[ind++] = color.r * maxHeight; //y
	   vertices[ind++] = i * length; //z
	   Color.rgba8888ToColor(color, texture.getPixel(j, i));
	   vertices[ind++] = color.toFloatBits();
	 }
  }
	  
  private void buildModel()
  {
	mesh = new Mesh(true, vertices.length/3, indices.length, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
				                                                 new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));
	mesh.setVertices(vertices);
	mesh.setIndices(indices);
	ModelBuilder mb = new ModelBuilder();
	mb.begin();
	mb.part("chunk", mesh, GL20.GL_TRIANGLES, mat);
	//mb.part("chunk", mesh, GL20.GL_LINES, mat);
	model = mb.end();
	instance = new ModelInstance(model);
  }
	  
  @Override
  public void dispose()
  {
	super.dispose();
	model.dispose();
	data.disposePixmap();
  }
}
