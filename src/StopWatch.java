import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
class stopwatch
Counter of time
 */
public class StopWatch {

    int timePassed =0; //total time
    int seconds=0; //seconds
    int minutes=0; //minutes
    int hours =0; // hours

    String seconds_string= String.format("%02d",seconds); //displays 0 before seconds
    String minutes_string= String.format("%02d",minutes); //displays 0 before minutes
    String hours_string= String.format("%02d",hours); //displays 0 before hours
    String time = hours_string+":"+minutes_string+":"+seconds_string; //total formatted string

    Timer timer;

    /*
    method start ()
    Pre : n/a
    post: timer initialized and started
     */
    public void start () {
        initializeTimer ();
        timer.start();
    }

    /*
    method stop ()
    Pre : timer started
    post : timer stopped
     */
    public void stop (){
        timer.stop();
    }

    /*
    method getTime
    pre : timer started
    post : returns string of elapsed time
     */
    public String getTime () {
        return time;
    }

    /*
    method initializeTimer
    Pre : n/a
    Post : timer set to new timer variale with actionlistener and delay 1000 milliseconds
     */
    private void initializeTimer () {
        timer = new Timer (1000, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {

                timePassed=timePassed+1000;
                hours= (timePassed/3600000);  //milliseconds-hour/min/sec
                minutes= (timePassed/60000)%60;  //gives us no more than 60 minutes
                seconds= (timePassed/1000)%60;

                seconds_string=String.format("%02d",seconds);
                minutes_string=String.format("%02d",minutes);
                hours_string=String.format("%02d",hours);

                time = hours_string+":"+minutes_string+":"+seconds_string;
            }
        });
    }
}