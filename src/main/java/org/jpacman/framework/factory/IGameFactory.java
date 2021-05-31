package main.java.org.jpacman.framework.factory;

import main.java.org.jpacman.framework.model.Game;
import main.java.org.jpacman.framework.model.Board;
import main.java.org.jpacman.framework.model.Food;
import main.java.org.jpacman.framework.model.Ghost;
import main.java.org.jpacman.framework.model.Player;
import main.java.org.jpacman.framework.model.Wall;

/**
 * Interface to create objects for the various
 * sprites participating in a game.
 * Implements the Abstract Factory Design Pattern.
 * 
 * @author Arie van Deursen, TU Delft, December, 2011
 */
public interface IGameFactory {
	
	/**
	 * @return A newly created game.
	 */
	Game makeGame();
	
	/**
	 * Create a new board of given width and height.
	 * @param w Width of the board
	 * @param h Height of the board
	 * @return A new board of requested width and height.
	 */
	Board makeBoard(int w, int h);
	
	/**
	 * @return A newly created player.
	 */
	Player makePlayer();
	
	/**
	 * @return A newly created ghost.
	 */
	Ghost makeGhost();
	
	/**
	 * @return A newly created food sprite.
	 */
	Food makeFood();
	
	/**
	 * @return A newly created wall sprite.
	 */	
	Wall makeWall();
}
