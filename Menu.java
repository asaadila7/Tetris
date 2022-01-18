import java.io.File;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/* Menu ( public class extends Container)
 * Menu container with options for adjusting settings, viewing instructions, seeing the high scores and starting a new game
 */
public class Menu extends Container implements ActionListener {

    //Buttons
    private JButton instructions = new JButton (new ImageIcon (new File ("./Instructions.png").getPath ())); //instructions button
    private JButton settings = new JButton (new ImageIcon(new File ("./settings.png").getPath ())); // settings button
    private JButton play = new JButton ("New Game"); //play new game button

    //Text area for highscores
    private JTextArea highScores = new JTextArea (3, 10); //highscores display component

    /*Menu (public method)
     *@param (none) - Creates the layout of the menu.
     *@return (none)
     * pre: none
     * post: new container with menu layout
     */
    public Menu () {
        Background backgroundPanel; //image-decorated background

        setLayout(new BorderLayout());

        //Background Panel

        backgroundPanel = new Background();
        backgroundPanel.setLayout (new BorderLayout());

        //actionlistener to implement actions on button click
        play.addActionListener (this);
        instructions.addActionListener (this);
        settings.addActionListener (this);

        //Button Layout

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.PAGE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30)); //padding
        play.setAlignmentX(Component.CENTER_ALIGNMENT); //center align
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        settings.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPane.add(play);
        buttonPane.add(Box.createRigidArea(new Dimension(0, 10))); //padding
        buttonPane.add(instructions);
        buttonPane.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPane.add(settings);
        buttonPane.setOpaque(false); //set transparent

        backgroundPanel.add(buttonPane, BorderLayout.CENTER);

        setHighScores (); //adds highscores to text area

        highScores.setFont (new Font ("SANS_SERIF", Font.BOLD, 12));
        highScores.setBackground (Color.white); //set font for text area

        //Highscore Layout

        JPanel scorePane = new JPanel ();
        scorePane.setLayout (new BoxLayout (scorePane, BoxLayout.PAGE_AXIS));
        scorePane.setBorder (BorderFactory.createEmptyBorder(10, 30, 30, 30)); //padding
        JLabel scoreLabel = new JLabel ("High Scores");
        highScores.setEditable (false); //highscores are not edible
        scoreLabel.setLabelFor (highScores); //scorelabel is for component highscores
        scorePane.add (scoreLabel);
        scorePane.add (Box.createRigidArea(new Dimension(0, 5)));
        scorePane.add (highScores);
        scorePane.setOpaque(false); //set transparent

        backgroundPanel.add(scorePane, BorderLayout.SOUTH);

        add (backgroundPanel);
    }



    /*actionPerformed (public method)
     *@param name: event type: ActionEvent - //When user clicks on the Play/Instruction buttons
     *@return void
     * pre: actionlisteners set to appropriate components
     * post : action performed based on event
     */
    public void actionPerformed (ActionEvent event) {
        if (event.getSource () == play) {
            Main.frame.startGame ();
        } else if (event.getSource () == instructions) {
            new Instructions (Main.frame);
        } else if (event.getSource () == settings) {
            new Settings (Main.frame);
        }
    }

    /*setHighScores (public method)
     *@param (none) - //Displays best 3 highscores for user
     *@return void
     * pre : highscores array initialized
     * post : high scores text area filled
     */
    public void setHighScores () {
        String [] ordinals = {"First", "Second", "Third"}; //for labels

        highScores.setText (""); //clear old text

        for (int i = 0; i < 3; i++) {
            highScores.append ("   " + ordinals [i] + ": " + Main.highScores [i] + "\n"); //append each high score to the list
        }
    }
}
