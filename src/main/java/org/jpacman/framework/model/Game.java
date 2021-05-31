package main.java.org.jpacman.framework.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import main.java.org.jpacman.framework.model.IBoardInspector.SpriteType;


public class Game extends Observable 
	implements IGameInteractor {
	

	private Board theBoard;
	private final PointManager pointManager = new PointManager();
	private Player thePlayer;
	private final List<Ghost> ghosts = new ArrayList<Ghost>();

	public void setBoard(Board b) {
		assert b != null : "New board should not be null.";
		theBoard = b;
	}

	public void movePlayer(Direction dir) {
		assert theBoard != null : "Board can't be null when moving";
		Tile target = theBoard.tileAtDirection(thePlayer.getTile(), dir);
		if (tileCanBeOccupied(target) && thePlayer.isAlive()) {
			Sprite currentContent = target.topSprite();
			eatFood(thePlayer, currentContent);
			dieIfGhost(thePlayer, currentContent);
			thePlayer.deoccupy();
			thePlayer.occupy(target);
			thePlayer.setDirection(dir);
			notifyViewers();
		}
	}

	private void eatFood(Player player, Sprite currentSprite) {
		if (currentSprite instanceof Food) {
			Food food = (Food) currentSprite;
            pointManager.consumePointsOnBoard(player, food.points);
			food.deoccupy();
		}
	}

	private void dieIfGhost(Player p, Sprite currentSprite) {
		if (currentSprite instanceof Ghost) {
			p.die();
		}
	}
	
	public void moveGhost(Ghost theGhost, Direction dir) {
		Tile target = theBoard.tileAtDirection(theGhost.getTile(), dir);
		if (tileCanBeOccupied(target)) {
			Sprite currentContent = target.topSprite();
			if (currentContent instanceof Player) {
				((Player) currentContent).die();
			}
			theGhost.deoccupy();
			theGhost.occupy(target);
			notifyViewers();
		} 
	}

	private boolean tileCanBeOccupied(Tile target) {
		assert target != null : "PRE: Argument can't be null";
		Sprite currentOccupier = target.topSprite();
		return 
			currentOccupier == null 
			|| currentOccupier.getSpriteType() != SpriteType.WALL;
	}
	
	public void addPlayer(Player p) {
		thePlayer = p;
	}
	
	public void addGhost(Ghost g) {
		ghosts.add(g);
	}
	
	public void addFood(Food f) {
        pointManager.addPointsToBoard(f.points);
	}
	
	public Board getBoard() {
		return theBoard;
	}
	
	@Override
	public Player getPlayer() {
		return thePlayer;
	}
	
	@Override
	public void attach(Observer o) {
		assert o != null : "Can't add a null observer.";
		addObserver(o);
	}
	
 
    protected void notifyViewers() {
        setChanged();
        notifyObservers();
    }

	public PointManager getPointManager() {
		return pointManager;
	}

	public List<Ghost> getGhosts() {
        List<Ghost> result = new ArrayList<Ghost>();
        result.addAll(ghosts);
		return result;
	}

	public IBoardInspector getBoardInspector() {
		return getBoard();
	}

	public boolean died() {
		return !getPlayer().isAlive();
	}

	public boolean won() {
		return pointManager.allEaten();
	}

}
