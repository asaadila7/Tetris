import javax.sound.sampled.*;
import java.io.IOException;

/*
class sound
retrieves and plays sound file
 */
public class Sound implements Runnable {
    private String fileLocation = "Resources/Soundtrack.wav";
    private AudioInputStream audioInputStream = null;
    private SourceDataLine line = null;
    public volatile boolean shouldQuit; //true when music should stop playing

    public Sound () {
        try { //create new audioinputstream
            audioInputStream = AudioSystem.getAudioInputStream (this.getClass ().getResource (fileLocation));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioFormat audioFormat = audioInputStream.getFormat(); //get file format

        try {
            line = (SourceDataLine) AudioSystem.getLine (new DataLine.Info (SourceDataLine.class, audioFormat)); //use audioinputstream to make sourcedataline for playing sound
            line.open (audioFormat); //open the file inthe appropriate format

            FloatControl floatControl = (FloatControl) line.getControl (FloatControl.Type.MASTER_GAIN); //volume control object

            float value = (floatControl.getMaximum() - floatControl.getMinimum ()) * Main.volume + floatControl.getMinimum (); //map volume to new value between maximum and minimum
            floatControl.setValue (value); //set volume

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run () {
        line.start ();

        while (!shouldQuit) {
            playSound ();
        }

        line.drain(); //release resources
        line.close();

        try {
            audioInputStream.close (); //release resources
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play () {
        Thread t = new Thread (this); //new thread to play audio synchronously
        shouldQuit = false; //do not terminate immediately
        t.start();
    }

    private void playSound () {
        int nBytesRead = 0;
        byte [] abData = new byte [500];

        while (nBytesRead != -1) { //while there is file left to read
            if (shouldQuit) {
                return;
            }

            try {
                nBytesRead = audioInputStream.read (abData, 0, abData.length); //store bytes in abdata
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (nBytesRead >= 0) {
                line.write (abData, 0, nBytesRead); //write bytes
            }
        }
    }
}
