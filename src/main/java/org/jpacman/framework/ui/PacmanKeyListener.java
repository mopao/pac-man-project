package main.java.org.jpacman.framework.ui;

import main.java.org.jpacman.framework.model.Controller;
import main.java.org.jpacman.framework.model.Direction;
import main.java.org.jpacman.framework.model.IGameInteractor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Map keyboard events to jpacman events.
 * 
 * @author Arie van Deursen, TU Delft, Jan 29, 2012
 */
public class PacmanKeyListener extends Observable implements KeyListener, IPacmanInteraction, Observer
{
    /**
     * The state of the ongoing match.
     * Initially, we're just waiting.
     */
    private PacmanKeyListener.MatchState currentState = PacmanKeyListener.MatchState.PAUSING;

    /**
     * Window to be deleted upon game exit.
     */
    private Disposable disposableWindow;

    /**
     * Model of the game which can execute basic commands.
     */
    private IGameInteractor gameInteractor;

    /**
     * Various controllers that may have to stopped or restarted.
     */
    private final List<Controller> controllers =
            new ArrayList<Controller>();

	/**
	 * Create a new keyboard listener, given a handler
	 * for model events keyboard events should be mapped to.
	 *
	 */
	PacmanKeyListener() {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// nothing.		
	}

	@Override
	public void keyPressed(KeyEvent event)
    {
		int code;

		code = event.getKeyCode();

        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_K)
        {
            this.up();
        }
        else
        {
            if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_J)
            {
                this.down();
            }
            else
            {
                if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_H)
                {
                    this.left();
                }
                else
                {
                    if(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L)
                    {
                        this.right();
                    }
                    else
                    {
                        if(code == KeyEvent.VK_Q)
                        {
                            this.stop();
                        }
                        else
                        {
                            if(code == KeyEvent.VK_X)
                            {
                                this.exit();
                            }
                            else
                            {
                                if(code == KeyEvent.VK_S)
                                {
                                    this.start();
                                }
                            }
                        }
                    }
                }
            }
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// nothing.
	}

    /**
     * The states a match can be in.
     */
    public enum MatchState {
        PLAYING("Playing"),
        PAUSING("Halted"),
        WON("You have won :-)"),
        LOST("You have lost :-(");

        private String theMessage;

        /**
         * Create one of the states.
         * @param m The message for this state.
         */
        MatchState(String m) {
            theMessage = m;
        }

        /**
         * @return The message that belongs to the current state.
         */
        String message() {
            return theMessage;
        }
    }

    @Override
    public void start() {
        assert (currentState == PacmanKeyListener.MatchState.WON
                && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST
                        && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won()));
        if (currentState == PacmanKeyListener.MatchState.PAUSING) {
            startControllers();
            updateState(PacmanKeyListener.MatchState.PLAYING);
        }

        assert (currentState == PacmanKeyListener.MatchState.WON && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won()));
    }

    @Override
    public void stop() {
        assert (currentState == PacmanKeyListener.MatchState.WON
                && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST
                        && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won())) : "Unexpected state in " + currentState;
        if (currentState == PacmanKeyListener.MatchState.PLAYING) {
            stopControllers();
            updateState(PacmanKeyListener.MatchState.PAUSING);
        }
        assert (currentState == PacmanKeyListener.MatchState.WON
                && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST
                        && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won()));
    }

    @Override
    public void exit() {
        assert (currentState == PacmanKeyListener.MatchState.WON
                && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST
                        && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won()));
        stopControllers();
        disposableWindow.dispose();
    }

    @Override
    public void up() {
        movePlayer(Direction.UP);
    }

    @Override
    public void down() {
        movePlayer(Direction.DOWN);
    }

    @Override
    public void left() {
        movePlayer(Direction.LEFT);
    }

    @Override
    public void right() {
        movePlayer(Direction.RIGHT);
    }

    /**
     * Move the player in the given direction,
     * provided we are in the playing state.
     * @param dir New direction.
     */
    private void movePlayer(Direction dir) {
        assert (currentState == PacmanKeyListener.MatchState.WON
                && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST
                        && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won())) : currentState;
        if (currentState == PacmanKeyListener.MatchState.PLAYING) {
            gameInteractor.movePlayer(dir);
            updateState();
        }
        // else: ignore move event.
        assert (currentState == PacmanKeyListener.MatchState.WON
                && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST
                        && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won()));
    }

    /**
     * Add an external controller, which should be stopped/started
     * via the ui.
     * @param controller The controller to be added.
     * @return Itself, for fluency.
     */
    public PacmanKeyListener controlling(Controller controller) {
        controllers.add(controller);
        return this;
    }

    /**
     * @return The current state of the game.
     */
    public PacmanKeyListener.MatchState getCurrentState() {
        return currentState;
    }

    /**
     * Provide the main window that has to be disposed off during exit.
     * @param win Main window
     * @return Itself, for fluency.
     */
    public PacmanKeyListener withDisposable(Disposable win) {
        disposableWindow = win;
        return this;
    }

    /**
     * Provide the interactor towards the model of the game.
     * @param igame Interactor
     * @return itself for fluency.
     */
    public PacmanKeyListener withGameInteractor(IGameInteractor igame) {
        gameInteractor = igame;
        return this;
    }

    private void stopControllers() {
        for (Controller c : controllers) {
            c.stop();
        }
    }

    private void startControllers() {
        for (Controller c : controllers) {
            c.start();
        }
    }

    /**
     * @return The handle to interact with the underlying game.
     */
    protected IGameInteractor getGame() {
        return gameInteractor;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateState();
    }

    /**
     * The state of the external game may have changed.
     * Verify whether the game was lost/won,
     * and if so update the state accordingly.
     */
    public void updateState() {
        // invariant may have been invalidated by outside world.
        if (currentState == PacmanKeyListener.MatchState.PLAYING && gameInteractor.died()) {
            updateState(PacmanKeyListener.MatchState.LOST);
            stopControllers();
        } else if (currentState == PacmanKeyListener.MatchState.PLAYING && gameInteractor.won()) {
            updateState(PacmanKeyListener.MatchState.WON);
            stopControllers();
        } else if (currentState == PacmanKeyListener.MatchState.WON && !gameInteractor.won()
                || currentState == PacmanKeyListener.MatchState.LOST && !gameInteractor.died()) {
            updateState(PacmanKeyListener.MatchState.PAUSING);
            stopControllers();
        }
        assert (currentState == PacmanKeyListener.MatchState.WON
                && gameInteractor.won()
                ||
                currentState == PacmanKeyListener.MatchState.LOST
                        && gameInteractor.died()
                ||
                (currentState == PacmanKeyListener.MatchState.PLAYING || currentState == PacmanKeyListener.MatchState.PAUSING)
                        && !(gameInteractor.died() || gameInteractor.won()));
    }

    private void updateState(PacmanKeyListener.MatchState nextState) {
        currentState = nextState;
        setChanged();
        notifyObservers();
    }
}