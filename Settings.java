import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/*
class settings
settings dialog box
 */
public class Settings extends JDialog implements ItemListener, ChangeListener {
    //input items
    private JCheckBox checkBox;
    private JSlider slider;

    /*
    method Settings ()
    pre : frame owner to set modality
    post : new settings dialog constructed
     */
    public Settings (Frame owner) {
        super (owner, "Settings", true);
        setUp ();
    }

    /*
    method Settings ()
    pre : dialog owner to set modality
    post : new settings dialog constructed
     */
    public Settings (Dialog owner) {
        super (owner, "Settings", true);
        setUp ();
    }

    /*
    method setup
    pre: none
    post : creates dialog
     */
    public void setUp () {
        setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        BoxLayout layout = new BoxLayout (getContentPane (), BoxLayout.PAGE_AXIS);
        getContentPane ().setLayout (layout);

        slider = new JSlider (0, 100,  (int) (Main.volume * 100));
        slider.addChangeListener (this); //listen for input
        JLabel volume = new JLabel ("Volume");
        volume.setLabelFor (slider); //slider is for volume change
        JPanel volumePane = new JPanel ();
        volumePane.setBorder (BorderFactory.createEmptyBorder (30, 30, 15, 30));
        volumePane.add (volume);
        volumePane.add (Box.createRigidArea (new Dimension (10, 0))); //padding
        volumePane.add (slider);
        add (volumePane);

        checkBox = new JCheckBox ("Show Ghost Piece", Main.showGhostPiece); //show ghost pieces?
        checkBox.addItemListener (this);
        checkBox.setBorder (BorderFactory.createEmptyBorder (15, 30, 30, 30));
        checkBox.setAlignmentX (Component.CENTER_ALIGNMENT); //center align
        add (checkBox);

        layout.layoutContainer (getContentPane ());
        pack ();
        setLocationRelativeTo (null);
        setVisible (true);
    }

    /*
    method itemStateChanged
    pre : actionlistener set to appropriate components
    post: action based on event
     */
    @Override
    public void itemStateChanged (ItemEvent event) {
        if (checkBox.isSelected ()) {
            Main.showGhostPiece = true;
        } else {
            Main.showGhostPiece = false;
	    try {
            	Main.tetris.clearGhostPiece (); //get rid of remaining ghost piece
	    } catch (Exception ignored) {}
        }
    }

    /*
    method StateChanged
    pre : actionlistener set to appropriate components
    post: sets volume in main class if user not still adjusting value
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            Main.volume = (float) source.getValue () / 100;
        }
    }}
