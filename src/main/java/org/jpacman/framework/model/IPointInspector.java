package main.java.org.jpacman.framework.model;

/**
 * Get informed about the number of points
 * earned and the points still available.
 * 
 * @author Arie van Deursen, TU Delft, Jan 21, 2012
 */
public interface IPointInspector {

	int getFoodEaten();
	int totalFoodInGame();
	boolean allEaten();
}
