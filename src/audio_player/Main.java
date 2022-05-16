package audio_player;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Main 
{

    public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException 
    {
        
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                try
                {
                    new Window();                
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println("error:" + e.toString());
                }
            }
        });
        
        
        
        /*
        //prova audio 
        //String fileName = "super-eurobeat-betty-feat-annalise-the-one-for-me.mp3";
        Clip clip = AudioSystem.getClip();
        try
        {
            //AudioInputStream AudioStream = AudioSystem.getAudioInputStream( Main.class.getResourceAsStream("C:/Users/user/Music/" + "super-eurobeat-betty-feat-annalise-the-one-for-me.mp3") );
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("C:/Users/user/Music/" + "super-eurobeat-betty-feat-annalise-the-one-for-me.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioStream);//prende la risorsa
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            //clip.start();
            Scanner zz = new Scanner(System.in);
            int i = zz.nextInt();
        }
        catch(Exception e)
        {
            System.out.println("error =" + e.toString());
            System.err.println(e.getMessage());
        }
        */
    }
    
}
