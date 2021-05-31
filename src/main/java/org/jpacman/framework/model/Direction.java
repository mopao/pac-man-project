package main.java.org.jpacman.framework.model;

/**
 * Directions in which sprites can move.
 * 
 * @author Arie van Deursen, TU Delft, Jan 23, 2012
 */
public enum Direction {
	
	UP(0, -1), 
	DOWN(0, 1),
	LEFT(-1, 0),
	RIGHT(1, 0);
	
	public int dx, dy;
	
	/**
	 * Create a new direction.
	 * @param dx horizontal move
	 * @param dy vertical move
	 */
	Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

}
