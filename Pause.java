import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
class pause
pause dialog box
 */
public class Pause extends JDialog implements ActionListener {
    //buttons
    private JButton settings = new JButton (new ImageIcon (new File ("./settings.png").getPath ()));
    private JButton instructions = new JButton (new ImageIcon (new File ("./Instructions.png").getPath ()));
    private JButton resume = new JButton ("Resume");
    private JButton quit = new JButton ("Quit");

    public Pause (Frame owner) {
        //sets modal, owner and title
        super (owner, "Paused", true);
        setDefaultCloseOperation (WindowConstants.DO_NOTHING_ON_CLOSE); //cannot exit via close button
        BoxLayout layout = new BoxLayout (getContentPane (), BoxLayout.PAGE_AXIS);
        getContentPane ().setLayout (layout);

        //action listeners to dostuff on click
        settings.addActionListener (this);
        instructions.addActionListener (this);
        resume.addActionListener (this);
        quit.addActionListener (this);

        add (Box.createRigidArea (new Dimension (150, 20)));//padding
        add (resume);
        add (Box.createRigidArea (new Dimension (150, 20)));
        add (quit);
        add (Box.createRigidArea (new Dimension (150, 20)));
        add (instructions);
        add (Box.createRigidArea (new Dimension (150, 20)));
        add (settings);
        add (Box.createRigidArea (new Dimension (150, 20)));

        settings.setAlignmentX (Component.CENTER_ALIGNMENT); //align centre
        instructions.setAlignmentX (Component.CENTER_ALIGNMENT);
        resume.setAlignmentX (Component.CENTER_ALIGNMENT);
        quit.setAlignmentX (Component.CENTER_ALIGNMENT);

        layout.layoutContainer (getContentPane ());
        pack (); //make smallest possible size
        setLocationRelativeTo (null);
        setVisible (true);
    }

    @Override
    public void actionPerformed (ActionEvent event) {
        if (event.getSource () == resume) {
            Main.tetris.resume ();
            dispose (); //et rid of frame
        } else if (event.getSource () == quit) {
            Main.frame.endGame ();
            dispose ();
        } else if (event.getSource () == instructions) {
            new Instructions (this);
        } else if (event.getSource () == settings) {
            new Settings (this);
        }
    }
}
