package it.MirkoGiacchini.world3d.heightmap;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/**
 * loader di una heightmap
 * @author Mirko
 *
 */
public class HeightmapLoader extends AsynchronousAssetLoader<Heightmap, HeightmapLoader.HeightmapParameter>
{
  Heightmap heightmap;
  
  public HeightmapLoader(FileHandleResolver resolver) 
  {
    super(resolver);
  }

  @Override
  public void loadAsync(AssetManager manager, String fileName, FileHandle file, HeightmapParameter parameter) 
  {
  }

  @Override
  public Heightmap loadSync(AssetManager manager, String fileName, FileHandle file, HeightmapParameter parameter) 
  {
	heightmap = null;
	try 
	{
	  heightmap = new Heightmap(file, manager.get(parameter.texturePath, Texture.class), parameter.length, parameter.maxHeight);
	} catch (Exception e){ e.printStackTrace(); }
	return heightmap;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, HeightmapParameter parameter) 
  {
	Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
	TextureLoader.TextureParameter textureParams = new TextureLoader.TextureParameter();
	AssetDescriptor descriptor = new AssetDescriptor(resolve(parameter.texturePath), Texture.class, textureParams);
	deps.add(descriptor);
	return deps;
  }
	
  static public class HeightmapParameter extends AssetLoaderParameters<Heightmap> 
  {
    String texturePath;
    float length, maxHeight;
    public HeightmapParameter(String str, float l, float mh) 
    {
      texturePath = str; 
      length = l;
      maxHeight = mh;
    }
  }
}
