package edu.virginia.sgd;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TileSet {
	
	private TextureRegion[] tiles;
	private Texture tex;
	
	public TileSet(String path, int tileWidth, int tileHeight) {
		tex = new Texture("tileset.png");
		
		
		int numCols = tex.getWidth()/tileWidth;
		int numRows = tex.getHeight()/tileHeight;
		
		tiles = new TextureRegion[numRows * numCols];
		
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				tiles[r+c*numRows] = new TextureRegion(tex, r*tileWidth, c*tileHeight, tileWidth, tileHeight);
			}
		}
	}
	
	public TextureRegion getTexture(int id) {
		if (id < 0 || id >= tiles.length) 
			return null;
		
		return tiles[id];
	}

}
