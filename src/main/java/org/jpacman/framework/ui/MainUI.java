package main.java.org.jpacman.framework.ui;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.org.jpacman.framework.model.Controller;
import main.java.org.jpacman.framework.model.GhostMover;
import main.java.org.jpacman.framework.factory.FactoryException;
import main.java.org.jpacman.framework.factory.IGameFactory;
import main.java.org.jpacman.framework.model.IGameInteractor;
import main.java.org.jpacman.framework.model.Level;
import main.java.org.jpacman.framework.view.Animator;
import main.java.org.jpacman.framework.view.BoardView;

/**
 * The main user interface for jpacman.
 * 
 * @author Arie van Deursen, TU Delft, Jan 14, 2012
 */
public class MainUI extends JFrame implements Observer, Disposable
{
	
    /**
     * Universal version ID for serialization.
     */
    static final long serialVersionUID = -59470379321937183L;
    
    /**
     * The level we're currently playing.
     */
    private final Level level;
    
    /**
     * The underlying game.
     */
	private transient IGameInteractor theGame;

	/**
	 * Mapping of UI events to model actions.
	 */
	private transient PacmanKeyListener pi;

	/**
	 * The main window components.
	 */
	private PointsPanel points;
	private BoardView boardView;
	private ButtonPanel buttonPanel;
	private JTextField statusField;
	private JPanel statusPanel;
	
	/**
	 * Controllers that will trigger certain events.
	 */
	private transient Controller ghostController;
	private transient Animator animator;
		
	/**
	 * Create a new UI for the default board.
	 */
	public MainUI() {
		level = new Level();
 	}
	

	/**
	 * Create all the ui components and attach appropriate
	 * listeners.
	 * @throws FactoryException If resources for game can't be loaded.
	 * @return The main UI object
	 */
	public MainUI createUI() throws FactoryException {
    	assert getGame() != null;
    	assert ghostController != null;
    	
      	boardView = createBoardView();
      	animator = new Animator(boardView);
    	
      	if (pi == null) { pi = new PacmanKeyListener(); }
      	
      	pi.withDisposable(this)
    		.withGameInteractor(getGame())
    		.controlling(ghostController)
    		.controlling(animator);
      	pi.addObserver(this);

      	// disable automatic focus traversal keys handling, so mainUI doesn't
     	// lose focus because of a traversal key press.
     	setFocusTraversalKeysEnabled(false);
      	
        addKeyListener(pi);
        getGame().attach(pi);

    	createButtonPanel(pi).initialize();
    	createStatusPanel();
    	
    	JPanel mainGrid = createMainGrid();
        getContentPane().add(mainGrid);
        setGridSize();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setName("jpacman.main");
        setTitle("JPacman");  
		return this;
    }
    
    /**
     * Create a panel containing the start/stop buttons.
     * @param pi Interactor capable of performing requested actions.
     * @return The new panel with buttons.
     */
    protected ButtonPanel createButtonPanel(PacmanKeyListener pi) {
    	assert pi != null;
    	if (buttonPanel == null) {
    		buttonPanel = new ButtonPanel();
    	}
    	return buttonPanel
     		.withParent(this)
    		.withInteractor(pi);
    }
        
    /**
     * Create the main grid containing all UI elements.
     * @return The newly created main grid.
     */
    private JPanel createMainGrid() {
    	JPanel mainGrid = new JPanel();
    	mainGrid.setLayout(new BorderLayout());
    	mainGrid.setName("jpacman.topdown");
        mainGrid.add(statusPanel, BorderLayout.NORTH);
    	mainGrid.add(boardView, BorderLayout.CENTER);
    	mainGrid.add(buttonPanel, BorderLayout.SOUTH);
    	return mainGrid;
    }
    
    /**
     * Establish the appropriate size of the main window,
     * based on the sizes of the underlying components.
     */
    private void setGridSize() {
        int width = Math.max(boardView.windowWidth(), 
        		buttonPanel.getWidth());
        
        int height = 
        		points.getHeight()
        		+ boardView.windowHeight()
        		+ buttonPanel.getHeight();
        
        setSize(width, height);
    }
    
    private void createStatusField() {
    	final int statusWidth = 12;
    	statusField = new JTextField("", statusWidth);
    	statusField.setEditable(false);
    	statusField.setName("jpacman.status");
    }
    
    /**
     * Create the status panel displaying points,
     * whether the player is alive, etc.
     */
    protected void createStatusPanel() {
    	statusPanel = new JPanel();
    	
    	createStatusField();
    	
    	points = new PointsPanel();
    	points.initialize(getGame().getPointManager());
    	getGame().attach(points);
    	
    	statusPanel.add(statusField);
    	statusPanel.add(points);
    }

    /**
     * The state of the game has changed.
     * Reset button enabling depending on the state.
     * @param o Ignored
     * @param arg Ignored
     */
	@Override
	public void update(Observable o, Object arg) {
		statusField.setText(pi.getCurrentState().message());
    	boardView.repaint();
    }

	/**
	 * Create the controllers.
	 * @throws FactoryException If required resources can't be loaded.
	 * @return The main UI object.
	 */
    public MainUI initialize() throws FactoryException {
        theGame = createModel();
        getGame().attach(this);
		return this;
	}
	
	/**
	 * Creates the controllers, sets a ghostmover and creates the ui.
	 * Quickstart for normal gameplay.
	 * @throws FactoryException If required resources can't be loaded.
	 * @return The main UI object.
	 */
	public MainUI initializeNormalGame() throws FactoryException {
		initialize();
        withGhostController(new GhostMover(getGame()));
      	createUI();
      	return this;
    }
    	
    /**
     * Actually start the the controllers, and show the UI.
     */
	public void start()  {
		animator.start();
        setVisible(true);
        requestFocus();
	}
	
	private BoardView createBoardView() throws FactoryException {
		return new BoardView(getGame().getBoardInspector());
	}
	
	/**
	 * Read a board from file and load it.
	 * @return The resulting game.
	 * @throws FactoryException
	 */
	private IGameInteractor createModel() throws FactoryException {
		return level.parseMap();
	}
	
	/**
	 * @return The mapping between keyboard events and model events.
	 */
	public IPacmanInteraction eventHandler() {
		return pi;
	}
	
	/**
	 * @return The underlying game.
	 */
	public IGameInteractor getGame() {
		return theGame;
	}

	/**
	 * @return The ghostController
	 */
	public Controller getGhostController() {
		return ghostController;
	}

	/**
	 * Provide a given ghost controller.
	 * This function can only be called before the createUI function.
	 * @param gc The new ghost controller.
	 * @return Itself for fluency.
	 */
	public MainUI withGhostController(Controller gc) {
		assert gc != null;
		//The animator is not null if the createUI has already been called.
		//If this is the case, the GhostController should not be allowed to change,
		//because changes cannot be forwarded correctly.
		assert animator == null;
		ghostController = gc;
		return this;
	}
	
	/**
	 * Provide the name of the file containing the board.
	 * @param fileName Board file name.
	 * @return Itself for fluency.
	 */
	public MainUI withBoard(String fileName) {
		assert fileName != null;
		level.setMapFile(fileName);
		return this;
	}
	
	/**
	 * Provide a factory to create model elements.
	 * @param fact The actual factory
	 * @return Itself for fluency.
	 */
	public MainUI withFactory(IGameFactory fact) {
		assert fact != null;
		assert level != null;
		level.setFactory(fact);
		return this;
	}
	
	/**
	 * Provide the row of buttons.
	 * @param bp The new row of buttons
	 * @return Itself for fluency
	 */
	public MainUI withButtonPanel(ButtonPanel bp) {
		assert bp != null;
		buttonPanel = bp;
		return this;
	}
	
	/**
	 * Provide the interface to interact with the model.
	 * @param pi New model interactor.
	 * @return Itself for fluency.
	 */
	public MainUI withModelInteractor(PacmanKeyListener pi) {
		assert pi != null;
		this.pi = pi;
		return this;
	}
	
	/**
	 * Top level method creating the game, and 
	 * starting up the interactions.
	 * @throws FactoryException If creating the game fails.
	 */
	public void main() throws FactoryException {
		initializeNormalGame();
		start();
	}
		
	/**
	 * Main starting point of the JPacman game.
	 * @param args Ignored
	 * @throws FactoryException If reading game map fails.
	 */
	public static void main(String[] args) throws FactoryException {		
		new MainUI().main();
	}
}
