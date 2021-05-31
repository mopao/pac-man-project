package main.java.org.jpacman.framework.model;

public interface IBoardInspector {

	int getWidth();
	int getHeight();
	
	public enum SpriteType { 
		PLAYER, 
		GHOST, 
		FOOD, 
		EMPTY, 
		WALL, 
		OTHER
	};
		
	Sprite spriteAt(int x, int y);	

	SpriteType spriteTypeAt(int x, int y);

	Tile tileAt(int x, int y);	
}
