package main.java.org.jpacman.framework.model;

/**
 * Keep track of points (still) in the game.
 * 
 * @author Arie van Deursen, TU Delft, Jan 21, 2012
 */
public class PointManager implements IPointInspector {
	
	private int pointsPutOnBoard = 0;
	private int pointsEarned = 0;
	

	public void addPointsToBoard(int delta) {
		assert delta >= 0;
		pointsPutOnBoard += delta;
		assert (pointsEarned >= 0 && pointsEarned <= pointsPutOnBoard);
	}
	
	private void consumePointsOnBoard(int delta) {
		pointsEarned += delta;
        assert (pointsEarned >= 0 && pointsEarned <= pointsPutOnBoard);
	}
	
	/**
	 * While playing, let the player consume food.
	 * @param p Player actually eating.
	 * @param delta Amount of food eaten.
	 */
	public void consumePointsOnBoard(Player p, int delta) {
		p.addPoints(delta);
		consumePointsOnBoard(delta);
        assert (pointsEarned >= 0 && pointsEarned <= pointsPutOnBoard);
	}
		
	
	/**
	 * The game is over if everything has been eaten.
	 * @return Whether all points have been consumed.
	 */
	@Override
	public boolean allEaten() {
        assert (pointsEarned >= 0 && pointsEarned <= pointsPutOnBoard);
		return pointsEarned == pointsPutOnBoard;
	}

	public int getFoodEaten() {
		return pointsEarned;
	}
	
	public int totalFoodInGame() {
		return pointsPutOnBoard;
	}

}
