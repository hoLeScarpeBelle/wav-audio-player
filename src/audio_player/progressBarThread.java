package audio_player;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import javax.swing.JProgressBar;

public class progressBarThread extends Thread 
{
    private JProgressBar progressBar;
    private Clip clip;
    private long percentuale;
    private long maxValue;
    private long current_time;
    private boolean status;
    private boolean isAlive;
    
    public progressBarThread(JProgressBar progressBar,Clip clip) 
    {
        this.progressBar = progressBar;
        this.clip = clip;
        this.maxValue =clip.getMicrosecondLength();
        this.status = true;
        this.percentuale = this.maxValue/100;
        this.isAlive = true;
    }
    
    public void run()
    {
        long value;
        while(this.isAlive)
        {
            if(this.status)
            {
                this.current_time = clip.getMicrosecondPosition();
                value = this.current_time/this.percentuale;
                //long percentuale = this.maxValue/100;
                //percentuale = this.current_time/percentuale;
                this.progressBar.setValue((int)value);
                //System.out.println("percentuale = " + value);
            }
            else
                try {
                    sleep(100);
                } catch (InterruptedException ex)
                {
                    System.out.println("error = " + ex.toString());
                    Logger.getLogger(progressBarThread.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
    
    public void setStatus(boolean status)
    {
        this.status = status;
    }
    
    public void kill() throws InterruptedException
    {
        this.isAlive = false;
        join();
    }
}
