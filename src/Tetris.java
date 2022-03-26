import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/*
class Tetris
game container
 */
public class Tetris extends Container {
    private static final String colours [] = {"blue", "brown", "green", "orange", "purple", "red", "yellow"}; //names of tile files

    private final ImageIcon [] tiles = new ImageIcon [7]; //for actual pieces
    private final ImageIcon [] ghostTiles = new ImageIcon [7]; //for ghost pieces
    private final ImageIcon empty = new ImageIcon (this.getClass ().getResource ("Resources/SmallerTiles/empty.png")); //empty spaces

    private final JPanel game = new JPanel (new GridLayout (20, 10, 2, 2)); //game panel
    private final JPanel nextPieces = new JPanel (new GridLayout (10,6, 2, 2)); //next pieces panel
    private final JPanel holdPiece = new JPanel (new GridLayout (4, 6, 2, 2)); //hold piece panel

    private final JLabel score = new JLabel ("0000");

    private final int [] [] board = new int [20] [10]; //game layout
    private final int [] [] next = new int [10] [6]; //next pieces layout
    private final int [] [] hold = new int [4] [6]; //hold piece layout

    private final Piece [] pieces = new Piece [4]; //array of pieces
    private Piece saved; //saved piece in hold panel
    public Sound sound;

    public volatile boolean isPaused;
    private int posX, posY, delayTime = 1000; //position of piece, delay time for falling
    public int lines = 0; //lines cleared
    private boolean turn = false, move = false, direction = false, held = false, canHold = true;
    //move true when sideways moevement, turn true when turning, direction :left = true and clokwise = true, held true if current piece was held, canhold true if hold not yet called on currentpiece

    public StopWatch timer = new StopWatch ();
    private JLabel timeLabel = new JLabel ();

    public Tetris () {
        JPanel backgroundPane = new JPanel(); //image decorated
        JButton pause = new JButton (new ImageIcon (this.getClass ().getResource ("Resources/pause.png")));

        JLabel holdLabel = new JLabel ("Hold");
        JLabel scoreLabel = new JLabel ("Score");
        JLabel nextLabel = new JLabel ("Next");

        JPanel nextPane = new JPanel ();
        JPanel holdPane = new JPanel ();
        JPanel scorePane = new JPanel ();

        setLayout (new FlowLayout ());
        isPaused = false;
        timeLabel.setText ("Time: " + timer.getTime ());

        //initialize tiles
        for (int i = 0; i < 7; i++) {
            tiles [i] = new ImageIcon (this.getClass ().getResource ("Resources/SmallerTiles/" + colours [i] + ".png"));
        }

        for (int i = 0; i < 7; i++) {
            ghostTiles [i] = new ImageIcon (this.getClass ().getResource ("Resources/SmallerTiles/empty" + colours [i] + ".png"));
        }

        //initialize pieces
        for (int i = 0; i < 4; i++) {
            pieces [i] = new Piece ();
        }

        //add empty labels to everything
        for (int i = 0; i < 200; i++) {
            game.add (new JLabel (empty));
        }

        for (int i = 0; i < hold.length * hold [0].length; i++) {
            holdPiece.add (new JLabel (empty));
        }

        for (int i = 0; i < next.length * next [0].length; i++) {
            nextPieces.add (new JLabel (empty));
        }

        holdLabel.setLabelFor (holdPiece);
        nextLabel.setLabelFor (nextPieces);
        scoreLabel.setLabelFor (score);

        //center align
        holdLabel.setAlignmentX (CENTER_ALIGNMENT);
        nextLabel.setAlignmentX (CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX (CENTER_ALIGNMENT);

        //restrict sizes
        holdPiece.setMaximumSize (new Dimension ((empty.getIconWidth () * hold [0].length) + (2 * hold [0].length) + 2, (empty.getIconHeight () * hold.length) + (2 * hold.length) + 2));
        nextPieces.setMaximumSize (new Dimension ((empty.getIconWidth() * next [0].length) + (2 * next [0].length) + 2, (empty.getIconHeight () * next.length) + (2 * next.length) + 2));

        //set font on labels
        Font headingFont = new Font ("SANS_SERIF", Font.BOLD, 14);
        holdLabel.setFont (headingFont);
        scoreLabel.setFont (headingFont);
        nextLabel.setFont (headingFont);
        timeLabel.setFont (headingFont);

        game.setBorder (BorderFactory.createEmptyBorder (30, 30, 30, 30)); //padding
        game.setOpaque (false); //transparent

        //specify keymap and corresponding actions
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("Z"), "speedUp");
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("z"), "speedUp");
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("UP"), "turnAnticlockwise");
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("DOWN"), "turnClockwise");
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("LEFT"), "moveLeft");
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("RIGHT"), "moveRight");
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("X"), "hold");
        game.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (KeyStroke.getKeyStroke ("x"), "hold");

        //spceify actionmap with action codes
        game.getActionMap ().put ("speedUp",
                new AbstractAction () {
                    @Override
                    public void actionPerformed (ActionEvent e) {
                        delayTime = 100; //reduce delay
                    }
                });

        game.getActionMap ().put("turnClockwise",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        direction = true; //clockwise
                        turn = true;
                    }
                });

        game.getActionMap ().put ("turnAnticlockwise",
                new AbstractAction () {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        direction = false; //anticlockwise
                        turn = true;
                    }
                });

        game.getActionMap ().put("moveLeft",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        direction = true; //left
                        move = true;
                    }
                });

        game.getActionMap ().put("moveRight",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        direction = false; //right
                        move = true;
                    }
                });

        game.getActionMap ().put("hold", //hold action
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (canHold) {
                            held = true;
                        }
                    }
                });

        pause.setAlignmentX (Component.CENTER_ALIGNMENT);
        pause.addActionListener (
                new ActionListener () {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        pause ();
                        new Pause (Main.frame);
                    }
                }
        );

        score.setAlignmentX (Component.CENTER_ALIGNMENT);
        score.setFont (new Font ("SANS_SERIF", Font.BOLD, 16));

        //layout scorepane
        scorePane.setLayout (new BoxLayout (scorePane, BoxLayout.PAGE_AXIS));
        scorePane.add (scoreLabel);
        scorePane.add (Box.createRigidArea (new Dimension (0, 5)));
        scorePane.add (score);

        //layout hold pane
        holdPane.setLayout (new BoxLayout (holdPane, BoxLayout.PAGE_AXIS));
        holdPane.setOpaque (false);
        holdPane.add (Box.createVerticalGlue ());
        holdPane.add (holdLabel);
        holdPane.add (Box.createRigidArea (new Dimension (0, 10)));
        holdPiece.setOpaque (false);
        holdPane.add (holdPiece);

        //layout next pieces pane
        nextPane.setLayout (new BoxLayout (nextPane, BoxLayout.PAGE_AXIS));
        nextPane.setOpaque (false);
        nextPane.add (nextLabel);
        nextPane.add (Box.createRigidArea (new Dimension (0, 10)));
        nextPieces.setOpaque (false);
        nextPane.add (nextPieces);
        nextPane.add (Box.createVerticalGlue ());

        //layout panel on left side
        JPanel leftPane = new JPanel ();
        leftPane.setOpaque (false);
        leftPane.setLayout (new BoxLayout (leftPane, BoxLayout.PAGE_AXIS));
        leftPane.add (pause);
        leftPane.add (Box.createRigidArea (new Dimension (0, 20)));
        leftPane.add (scorePane);
        leftPane.add (Box.createRigidArea (new Dimension (0, 20)));
        leftPane.add (timeLabel);
        leftPane.add (Box.createVerticalGlue ());
        leftPane.setBorder (BorderFactory.createEmptyBorder (30, 30, 30, 30));

        //layout panel on right side
        JPanel rightPane = new JPanel ();
        rightPane.setOpaque (false);
        rightPane.setLayout (new BoxLayout (rightPane, BoxLayout.PAGE_AXIS));
        rightPane.add (nextPane);
        rightPane.add (Box.createVerticalGlue ());
        rightPane.add (holdPane);
        rightPane.setBorder (BorderFactory.createEmptyBorder (30, 30, 30, 30));


        //add everything
        backgroundPane.setLayout (new BoxLayout (backgroundPane, BoxLayout.LINE_AXIS));
        backgroundPane.add (leftPane);
        backgroundPane.add (game);
        backgroundPane.add (rightPane);
        add (backgroundPane);

        timer.start ();
        startMusic ();
    }

    /*
    method startMusic
    pre : n/a
    post : sound playing
     */
    public void startMusic () {
        sound = new Sound ();
        sound.play ();
    }

    /*
    method clearPiece
    pre : n/a
    post: removes live piece from board (live piece = moveable piece)
     */
    private void clearPiece () { //start from known position and travel the length and width of the piece
        for (int i = posY; i < posY + pieces [0].getShape ().length; i++) {
            for (int j = posX; j < posX + pieces [0].getShape () [0].length; j++) {
                if (board [i] [j] > 0 && board [i] [j] < 8) {
                    board [i] [j] = 0; //set previously occupied tiles to empty
                }
            }
        }
    }

    /*
    method newPiece
    pre : pieces initialized
    post : each piece shifted down and new one created at tail end
     */
    private void newPiece () {
        for (int i = 0; i < 3; i++) {
            pieces [i] = pieces [i + 1];
        }

        pieces [3] = new Piece ();
    }

    /*
    method hold
    pre : n/a
    post : current piece inhold and hold piece isn now current piece.
     */
    private void hold () {
        canHold = false;
        held = false;

        clearPiece (); //remove current piece from board

        if (saved != null) {//swap pieces
            Piece temp = pieces [0];
            pieces [0] = saved;
            saved = temp;

        } else { //if no piece saved, generate new one
            saved = pieces [0];
            newPiece ();
        }

        saved.setShape (saved.flatten ()); //turn piece on its side

        //clear hold pane
        for (int i = 1; i < 3; i++) {
            for (int j = 1; j < 5; j++) {
                hold [i] [j] = 0;
            }
        }

        //set new saved piece inhold pane
        for (int i = 1; i < 1 + saved.getShape().length; i++) {
            for (int j = 1; j < 1 + saved.getShape () [0].length; j++) {
                if (saved.getShape () [i - 1] [j - 1]) {
                    hold [i] [j] = saved.getColour() + 1; //hold [] [] holds code values for tile icons
                }
            }
        }

        updateDisplay (holdPiece, hold); //refreshdisplay
    }

    /*
    method updateGhostPiece
    pre : live normal piece present
    post : sets ghost piece inappropriate location
     */
    private void updateGhostPiece () {
        int posY = this.posY;

        clearGhostPiece(); //clear old piece

        while (canPlace (pieces [0].getShape (), posX, posY)) {
            posY++; //move down as far as possible
        }

        posY--;

        if (posY == this.posY || posY - this.posY < pieces [0].getShape ().length) { //ifno space between live piece and ghost piece
            return;
        }

        setPiece (posX, posY, true); //set ghost piece
        updateDisplay (game, board);
    }

    /*
    method setPiece
    pre : position onboard and whether or notitsa ghost piece
    post : game layout with piece represented in ints
     */
    private void setPiece (int posX, int posY, boolean ghostPiece) { //cycle through the length and with of the piece
        for (int i = posY; i < posY + pieces [0].getShape ().length; i++) {
            for (int j = posX; j < posX + pieces [0].getShape () [0].length; j++) {
                if (pieces [0].getShape () [i - posY] [j - posX]) { //check with piece arraay
                    board [i] [j] = pieces [0].getColour() + ((ghostPiece)? 8 : 1); //set tile value
                }
            }
        }
    }

    /*
    method playGame
    pre : n/a
    post : one piece goes all the way down and lands
     */
    public void playGame () {
        System.out.println ("Playing game");

        //sets next pieces display
        for (int i = 1; i < 4; i++) {
            boolean [] [] flatPiece = pieces [i].flatten (); //make horizontal

            for (int j = 10 - (i * 3); j < 12 - (i * 3); j++) {
                for (int k = 1; k < 5; k++) {
                    next [j] [k] = 0; //clears display
                }
            }

            for (int j = 10 - (i * 3); j < 10 + flatPiece.length - (i * 3); j++) {
                for (int k = 1; k < 1 + flatPiece [0].length; k++) {
                    if (flatPiece [j - 10 + (i * 3)] [k - 1]) {
                        next [j] [k] = pieces [i].getColour () + 1; //draws new piece in
                    }
                }
            }

            updateDisplay (nextPieces, next);
        }

        posX = 3; posY = 0; //set start position of new piece

        if (!canPlace (pieces [0].getShape (), posX, posY)) {
            Main.frame.endGame(); //if no more space,end the game
            return;
        }

        setPiece (posX, posY, false); //move thepiece to initial position
        updateDisplay (game, board);

        if (Main.showGhostPiece) {
            updateGhostPiece ();
        }

        //check for changes by other threads
        while (isPaused) { //should i pause play?
            if (Main.tetris == null) {
                return;
            }
        }
        if (held) { //should i hold the piece?
            hold ();
            return;
        }

        long currentTime = System.currentTimeMillis ();

        while (isAlive()) { //stop if piece lands and dies
            while (isPaused) { //wait until resumes
                if (Main.tetris == null) { //if the user decidedto qui via pause menu, return
                    return;
                }
            }
            if (held) {
                hold ();
                return;
            }

            if (System.currentTimeMillis () - currentTime >= delayTime && isAlive()) { // delay time has passed
               currentTime = System.currentTimeMillis(); //reset time
                for (int i = posY + pieces [0].getShape ().length - 1; i >= posY; i--) {
                    for (int j = posX; j < posX + pieces [0].getShape () [0].length; j++) {
                        if (board[i] [j] > 0 && board [i] [j] < 8) { //find a live piece
                            board[i + 1] [j] = board[i] [j]; //move every live tile down
                            board[i] [j] = 0;// reset the tile above
                        }
                    }
                }

                posY++;//increase position on y
                updateDisplay (game, board);

               if (lines < 25) {
                   delayTime = 1000 - (lines * 20); //increase speed as the game progresses
               }

                if (Main.showGhostPiece) {
                    updateGhostPiece ();
                }

            } else if (move && isAlive()) {
               try {
                   move (direction);

                   if (Main.showGhostPiece) {
                       updateGhostPiece ();
                   }

               } catch (Exception e) {
                   System.out.println ("Could not move");
               }

               move = false;

           } else if (turn && isAlive()) {
               try {
                   turn (direction);

                   if (Main.showGhostPiece) {
                       updateGhostPiece ();
                   }

               } catch (Exception e) {
                   System.out.println ("Could not turn");
               }

               turn = false;
           }
        }

        clearLine (); //checkif a line has been filled andclear it

        //checkif top row has pieces
        for (int i = 0; i < 10; i++) {
            if (board [0] [i] < 0) {
                Main.frame.endGame ();
            }
        }

        //generate new piece
        newPiece();
        canHold = true;
    }

    /*
    method updateDisplay
    pre: panel with components and layout for it
    post : panel's display updated
     */
    public void updateDisplay (JPanel panel, int [] [] layout) {
        //get rows and columns
        int rows = ((GridLayout) panel.getLayout()).getRows ();
        int columns = ((GridLayout) panel.getLayout()).getColumns ();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) { //iterate through every component
                if (!hasIcon (panel.getComponent ((i * columns) + j), getTile (layout [i] [j]))) { //if layout and component donot match
                    panel.remove ((i * columns) + j); //remove it
                    panel.add (new JLabel (getTile (layout [i] [j])), (i * columns) + j); //replace it
                }
            }
        }

        timeLabel.setText ("Time: " + timer.getTime ()); //updatetime
        revalidate ();
        game.repaint ();
	holdPiece.repaint ();
	nextPieces.repaint ();
    }

    /*
    method hasIcon
    pre : component with icon, icon to check for
    post : returns whether the component's iconmatches given icon
     */
    private boolean hasIcon (Component comp, ImageIcon icon) {
        return ((JLabel) comp).getIcon ().equals (icon);
    }

    /*
    method getTime
    pre : value of tile in the layout
    post : corresponding imageicon returned
     */
    private ImageIcon getTile (int value) {
        if (value == 0) {
            return (empty);
        } else if (value > 7) {
          return ghostTiles [value - 8]; //ghost tiles have values 8 - 14
        } else {
            return tiles [Math.abs (value) - 1];// dead pieces have negative values
        }
    }

    /*
    method clearLine
    pre : n/a
    post : checks if lines filled, clears them
     */
    private void clearLine () {
        String score;
        boolean hasLine = false;

        outerLoop: // if any tile in a row is empty, skip the row
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i] [j] == 0 || board [i] [j] > 8) {
                    continue outerLoop;
                }
            }

            // if all the tiles are filled

            System.arraycopy(board, 0, board, 1, i); //delete filled line and shift array
            board[0] = new int [10]; //new row at top

            hasLine = true;
            lines++; //increase lines cleared
            if (lines < 25) {
                delayTime = 1000 - (lines * 20);
            } //set speed of pieces
        }

        if (hasLine) { //if any lines were cleared
            long currentTime = System.currentTimeMillis ();

            while (System.currentTimeMillis () - currentTime < 500) { //delay
                if (Main.tetris == null) {
                    return;
                }
            }

            score = "" + lines; //update score
            while (score.length () < 4) {
                score = "0" + score;
            }
            this.score.setText (score);

            updateDisplay (game, board);
        }
    }

    /*
    method isAlive
    pre: n/a
    post : kills any live pieces that have landed and returns true if live piece can still move
     */
    private boolean isAlive () {
        boolean isDead = true;

        outerLoop: //check if the piece is on top of a dead one or at bottom of board
        for (int i = posY + pieces [0].getShape ().length - 1; i >= 0; i--) {
            for (int j = posX; j < posX + pieces [0].getShape () [0].length; j++) {
                if (board[i] [j] > 0 && board [i] [j] < 8) {
                    isDead = false;

                    try {
                        if (board[i + 1] [j] < 0) {
                            isDead = true;
                            break outerLoop;
                        }

                    } catch (Exception e) {
                        isDead = true;
                        break outerLoop;
                    }
                }
            }
        }

        if (!isDead) {
            return true;
        }

        //if the piece is dead, kill it
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                board[i] [j] = -1 * Math.abs (board[i] [j]);
            }
        }

        return false;
    }

    /*
    method move
    pre : boolean left?
    post : moves the piece if there is room to move
     */
    public void move (boolean left) throws Exception {
        int posX = this.posX + ((left)? -1 : 1); //shift piece

        if (!canPlace (pieces [0].getShape (), posX, posY)) { //if there is not room
            throw new Exception ();
        }

        clearPiece (); //get rid of old iece

        this.posX = posX; //update horizontal position

        setPiece (posX, posY, false);
        updateDisplay (game, board);
    }

    /*
    method turn
    pre : boolean clockwise?
    post : turns piece ifthere is room
     */
    private void turn (boolean clockwise) throws Exception { //check if dead after turning
        boolean [] [] turned = pieces [0].turn (clockwise);
        int posX = this.posX, posY = this.posY;

        //tries to align turned piece with top left, bottom left, top right and thenbottom right corners
        if (!canPlace (turned, posX, posY)) {
            if (!canPlace (turned, posX + pieces [0].getShape () [0].length - turned [0].length, posY)) {
                if (!canPlace (turned, posX, posY + pieces [0].getShape ().length - turned.length)) {
                    if (!canPlace (turned, posX + pieces [0].getShape () [0].length - turned [0].length, posY + pieces [0].getShape ().length - turned.length)) {
                        throw new Exception (); //throws exception if cannot turn
                    } else {
                        posX = posX + pieces [0].getShape () [0].length - turned [0].length;
                        posY = posY + pieces [0].getShape ().length - turned.length;
                    }
                } else {
                    posY = posY + pieces [0].getShape ().length - turned.length;
                }
            } else {
                posX = posX + pieces [0].getShape () [0].length - turned [0].length;
            }
        }

        clearPiece ();

        pieces [0].setShape (turned); //update piece if turned
        this.posX = posX; //update position
        this.posY = posY;

        setPiece (posX, posY, false);
        updateDisplay (game, board);
    }

    /*
    method canPlace
    pre : shape array and position of piece
    post : returns wehter the shape can be placed at given location
     */
    private boolean canPlace (boolean [] [] shape, int posX, int posY) {
        if (posX < 0 || posX + shape [0].length > 10 || posY < 0 || posY + shape.length > 20) {
            return false;
        } //if location out of bounds, return false

        //check whether overlap with dead piece if placed at position
        for (int i = posY; i < posY + shape.length; i++) {
            for (int j = posX; j < posX + shape [0].length; j++) {
                if (shape [i - posY] [j - posX] && board[i] [j] < 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /*
    method clearGhostPiece
    pre : n/a
    post : removes ghost piece from screen
    */
    public void clearGhostPiece () {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                if (board [i] [j] > 7) {
                    board [i] [j] = 0;
                }
            }
        }
    }

    /*
    method pause
    pre : game started and running
    post : stop sound and timer and whole game
     */
    public void pause () {
        sound.shouldQuit = true;
        isPaused = true;
        timer.stop ();
    }

    /*
    method resume
    pre : is paused
    post : normal function resumed
     */
    public void resume () {
        isPaused = false;
        timer.start ();
        startMusic ();
    }
}