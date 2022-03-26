import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

/* class Main
extends jframe and makes custom frame for application
*/

class Main extends JFrame {
    public static Menu menu; //menu container
    public static Tetris tetris; //tetris container
    public static Main frame; //JFrame

    public static int highScores [] = new int [3]; //array for high scores
    public static float volume; //float value for volume
    public static boolean showGhostPiece; //show ghost pieces to guide play?

    /*method main
    *pre: none
    * post : frame and menu container created, initial window set up
    */
    public static void main (String[] args) {
        frame = new Main ();
        menu = new Menu ();
        frame.setContentPane (menu); //insert contentpane
        frame.pack ();
        frame.setVisible (true);
    }

    /*
    method startGame
    pre : n/a
    post : tetris initialized and frame set to new game
     */
    public void startGame () {
        tetris = new Tetris ();
        frame.setContentPane (tetris); //swap out menu content pane
        frame.pack ();
        frame.revalidate (); //make sure display has been updated
    }

    /*
    method endGame
    pre : game started
    post : return to menu
     */
    public void endGame () {
        tetris.timer.stop (); //stop timer

        String message = "Your score is " + tetris.lines; //exit message

        //checks if new high score and implements appropriate action
        for (int k = 0; k < 3; k++) {

            if (tetris.lines > Main.highScores [k]) { //if new high score
                message = "You got a new high score!\n" + message + ".\nYou are now at place " + (k + 1);

                System.arraycopy(Main.highScores, k, Main.highScores, k + 1, 2 - k); //updatehigh scores array
                Main.highScores [k] = tetris.lines;

                break;
            }
        }

        message += "\n\nYour time is: " + tetris.timer.getTime ();

        //show exit message
        JOptionPane.showMessageDialog (Main.frame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        //stop sound
        tetris.sound.shouldQuit = true;
        tetris = null; //dispose of game

        //swap out contentpanes
        menu.setHighScores ();
        frame.setContentPane (menu);
        frame.pack ();
        frame.revalidate ();
    }

    /*
    method Main
    Pre : none
    Post : makes frame and dispatches new thread to run game
     */
    public Main () {
        super ("Tetris");

        String savedInfo = this.getClass ().getResource ("Resources/SavedInfo.txt").toString ();

        //runnable to run game
        final Runnable r = new Runnable() {
            public void run () {
                while (true) {
                    try {
                        tetris.playGame ();
                    } catch (Exception ignored) {}
                }
            }
        };

        //scanner for reading file
        try (Scanner scanner = new Scanner (savedInfo)) {
            for (int i = 0; i < 3; i++) {
                highScores [i] = scanner.nextInt (); //get saved high scores
            }

            //update other saved info
            volume = scanner.nextFloat ();
            showGhostPiece = scanner.nextBoolean ();

        } catch (Exception e) {
            System.out.println ("Unable to retrieve saved info");
        }

        this.addWindowListener (new WindowAdapter () { //window listener to round up resources on close and pause game on minimize
            public void windowClosing (WindowEvent event) {
                try (FileWriter fileWriter = new FileWriter (savedInfo)) { //writes to saved info file
                    for (int i = 0; i < 3; i++) {
                        fileWriter.write (highScores [i] + "\n");
                    }

                    fileWriter.write (volume + "\n");
                    fileWriter.write (showGhostPiece + "\n");

                } catch (Exception e) {
                    System.out.println ("Unable to store info");
                }
            }

            @Override
            public void windowDeiconified (WindowEvent e) {
                if (tetris != null) {
                    tetris.resume ();
                }
            }

            @Override
            public void windowIconified (WindowEvent e) {
                if (tetris != null) {
                    tetris.pause ();
                }
            }
        });

        new Thread(r).start();

        setResizable (false); //not resizable
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
    }
}