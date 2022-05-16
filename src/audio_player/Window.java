package audio_player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window extends JFrame 
{
    private AudioInputStream audioStream;
    protected Clip clip;
    protected JProgressBar timeBar;
    private long current_time;
    private boolean status;// false = stop || true = play
    private progressBarThread barThread;
    private String filePath;
    private FloatControl volume;
   // private boolean clipMounted;
    
    public Window() throws LineUnavailableException 
    {
        this.audioStream = null;
        this.clip = AudioSystem.getClip();
        this.current_time = 0;
        this.filePath = "";
        this.status = false;
        //this.clipMounted = false;
        this.volume = null;
        
        setLocationRelativeTo(null);
        setTitle("audio player");
        setVisible(true);
        setSize(new Dimension(300,300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        Window parent = this;
        setResizable(false);
        
        //main top panel
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setPreferredSize(new Dimension(this.getWidth(),65));
        
        
        //north panel
        JPanel  northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileLabel = new JLabel("file:");
        fileLabel.setPreferredSize(new Dimension(30,30));
        northPanel.add(fileLabel);
        JTextField fileName = new JTextField();
        fileName.setEditable(false);
        fileName.setPreferredSize(new Dimension(this.getWidth()-75,20));
        northPanel.add(fileName);
        filePanel.add(northPanel,BorderLayout.NORTH);
        
        
        //south panel
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton chooseButton = new JButton("choose file");
        chooseButton.setPreferredSize(new Dimension(75,20));
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) 
            {
                System.out.println("hi");
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("wav audio", "wav");//setta il tipo di file da cercare
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(parent);
                if (returnValue == JFileChooser.APPROVE_OPTION) // se la scelta è giusta fai questo
                {
                    System.out.println("You chose to open this file: " + fileChooser.getSelectedFile().getName());
                    System.out.println("file path:" + fileChooser.getSelectedFile().getPath());
                    fileName.setText(fileChooser.getSelectedFile().getName());
                    try
                    {
                        filePath = fileChooser.getSelectedFile().getPath();
                        createClip(filePath);
                    }
                    catch(Exception e)
                    {
                        System.out.println();
                    }
                }
            }
        });
        southPanel.add(chooseButton);
        filePanel.add(southPanel,BorderLayout.SOUTH);
        
        add(filePanel,BorderLayout.NORTH);
        
        //main center panel
        JPanel centralPanel = new JPanel(new FlowLayout());
        timeBar = new JProgressBar();
        //timeBar.setSize(new Dimension(this.getWidth(),40));
        timeBar.setPreferredSize(new Dimension(this.getWidth(),20));
        timeBar.setMaximum(100);
        timeBar.setMinimum(0);
        timeBar.setValue(0);
        centralPanel.add(timeBar);
        add(centralPanel,BorderLayout.CENTER);

        JPanel comandPanel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new GridLayout(1,0));
        //buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));//la dichiarazione del boxLayout va separata perche non riesce a vedere il panello non ancora inizializato
        //comandPanel.add(buttonPanel,BorderLayout.CENTER);
        JButton playButton = new JButton("play");
        //playButton.setPreferredSize(new Dimension(this.getWidth()/3,40));
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) 
            {
                if(status == false)
                {
                    clip.setMicrosecondPosition(current_time);
                    clip.start();
                    status = true;
                    barThread.setStatus(status);
                    //barThread.notify();
                }
                else if(clip.getMicrosecondLength() == clip.getMicrosecondPosition())//sostituire con create clip
                {
                    try 
                    {
                        createClip(filePath);
                    } 
                    catch(Exception e) 
                    {
                        System.out.println("errore = " + e.toString());
                    }
                }
            }
        });
        //comandPanel.add(playButton, BorderLayout.CENTER);
        buttonPanel.add(playButton);
                
        JButton stopButton = new JButton("stop");
        //stopButton.setPreferredSize(new Dimension(this.getWidth()/3,40));
        stopButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent arg0) 
            {
                if(status = true)
                {
                    current_time = clip.getMicrosecondPosition();
                    clip.stop();
                    status = false;
                    barThread.setStatus(status);
                }
            }
        });
        //comandPanel.add(stopButton,BorderLayout.EAST);
        buttonPanel.add(stopButton);
        comandPanel.add(buttonPanel,BorderLayout.CENTER);
        
        //sliderPanel
        JPanel sliderPanel = new JPanel(new BorderLayout());
        JLabel volumeLabel = new JLabel("100");
        sliderPanel.add(volumeLabel,BorderLayout.EAST);
        
        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL , 0 , 100 ,100);//direction,min value,max value,initial value
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) 
            {
                if(volume != null)//clipMounted)
                {
                    try{
                    //FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeLabel.setText(""+volumeSlider.getValue());
                    float sliderValue = volumeSlider.getValue();
                    float range = volume.getMaximum() - volume.getMinimum();
                    System.out.println("maximum =" + volume.getMaximum() +" minimum = " + volume.getMinimum());
                    System.out.println("range =" + range);
                    float value = sliderValue * ( range/(float)volumeSlider.getMaximum() /*(float)volumeSlider.getMaximum() /range /*sliderValue /range*/) + volume.getMinimum();
                    System.out.println("volume value =" + value);
                    volume.setValue(value);
                    }catch(Exception e)
                    {
                        System.out.println("error =" + e.toString());
                    }
                }
            }
        });
        sliderPanel.add(volumeSlider , BorderLayout.CENTER);
        
        comandPanel.add(sliderPanel,BorderLayout.SOUTH);
        
        add(comandPanel,BorderLayout.SOUTH);
        
        /* prova fileChooser
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("mp3 audio","mp3");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION)
        {
            System.out.println("You chose to open this file: " + fileChooser.getSelectedFile().getName());
        }
        add(fileChooser,BorderLayout.NORTH);
        */
        
    }
    
    public void createClip(String fileName) throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        if(this.status == true)
        {
            try 
            {
                this.audioStream.close();
                this.clip.close();
                this.barThread.kill();
                this.volume = null;
            } catch (Exception e) 
            {
                System.out.println("errore = " + e.toString());
            }
        }
        this.audioStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
        this.clip = AudioSystem.getClip();
        this.clip.open(this.audioStream);
        this.clip.loop(0);
        this.clip.start();
        this.barThread = new progressBarThread(this.timeBar, this.clip);
        this.barThread.start();
        this.status = true;
        //this.clipMounted = true;
        this.volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    }
    
}
