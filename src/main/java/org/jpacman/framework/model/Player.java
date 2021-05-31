package main.java.org.jpacman.framework.model;

import main.java.org.jpacman.framework.model.IBoardInspector.SpriteType;

/**
 * A player, responsible for keeping track of the
 * amount of food eaten, and whether or not he is still
 * alive.
 * 
 * @author Arie van Deursen, TU Delft, 2012.
 */
public class Player extends Sprite {
	
	public int points = 0;
	private boolean alive = true;
	private Direction direction = Direction.LEFT;

	protected boolean playerInvariant() {
		return points >= 0 && spriteInvariant();
	}

	public int addPoints(int extraPoints) {
		assert isAlive();
		assert playerInvariant();
		points += extraPoints;
		assert playerInvariant();
		return points;
	}

	/**
	 * This player dies.
	 */
	public void die() {
		alive = false;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public SpriteType getSpriteType() {
		return SpriteType.PLAYER;
	}
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction nextDir) {
		direction = nextDir;
	}

	public void resurrect() {
		alive = true;
	}
}
