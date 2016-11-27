package drawrecorder2;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class DrawRecorder2 extends JPanel implements ActionListener
{
    static JFrame frame;
    File graphicsOutputFile, audioOutputFile;
    AudioRecorder recorder;
    final static int NEWPAGE = 4, NEXT = 5, PREV = 6, UNDO = 7, START = 8;
    static DrawPanel drawPanel;
    static JPanel buttonPanel = new JPanel(new GridBagLayout()), colorPanel = new JPanel(new GridBagLayout());
    Color buttonColors[] = { Color.BLACK, Color.BLUE, Color.RED, Color.GREEN };
    static Font buttonFont = new Font("TimesRoman", Font.BOLD, 22);
    String buttonNames[] = { "BLACK", "BLUE", "RED", "GREEN", "NEW PAGE", "NEXT", "PREV", "UNDO"};
    JButton button[] = new JButton[9];
    static ArrayList<Page> pageList = new ArrayList();
    static ArrayList<Integer> intList = new ArrayList();
    public static int pageNumber = 0, pixels = 0;
    static Color currentColor = Color.BLACK;
    static Timer timer;
    static boolean isRecording = false;
    
    public DrawRecorder2()
    {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        drawPanel = new DrawPanel();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = .95;
        c.weighty = .90;
        c.fill = GridBagConstraints.BOTH;
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setSize(900, 900);
        this.add(drawPanel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = .05;
        c.weighty = .85;
        buttonPanel.setSize(100, 900);
        this.add(buttonPanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = .85;
        c.weighty = .10;
        this.add(colorPanel, c);
        
        for(int i = 0; i < 4; i++)
        {
            c.gridx = i;
            c.gridy = 0;
            c.weightx = 0.25;
            c.weighty = 1;
            button[i] = new JButton(buttonNames[i]);
            button[i + 4] = new JButton(buttonNames[i + 4]);
            button[i].setBackground(buttonColors[i]);
            button[i].setForeground(Color.WHITE);
            button[i].setFont(buttonFont);
            button[i + 4].setFont(buttonFont);
            button[i].addActionListener(this);
            button[i + 4].addActionListener(this);
            button[i + 4].setEnabled(false);
            colorPanel.add(button[i], c);
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 1;
            c.weighty = 0.25;
            buttonPanel.add(button[i + 4], c);
        }
        button[NEWPAGE].setEnabled(true);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = .05;
        c.weighty = .1;
        
        button[8] = new JButton("START");
        button[8].setFont(buttonFont);
        button[8].addActionListener(this);
        this.add(button[8], c);
        
        String fileName = "testing123"; //JOptionPane.showInputDialog("Enter Name of File");
        graphicsOutputFile = new File(fileName + ".drw");
        audioOutputFile = new File(fileName + ".wav");
        
        pageList.add(new Page());
        
        addMouseListener(new MouseAdapter() {@Override public void mousePressed(MouseEvent e) { if(isRecording) pressed(e); } } );
        addMouseMotionListener(new MouseMotionAdapter() {@Override public void mouseDragged(MouseEvent e) { if(isRecording) dragged(e); } } );
        
        timer = new Timer(100, this);
        timer.setActionCommand("timer");
    }
    
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException 
    {
        /*frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DrawRecorder2 panel = new DrawRecorder2();
        frame.setSize(1200, 1000);
        frame.setResizable(false);
        frame.setContentPane(panel);
        
        while(!isRecording);
        while(isRecording);*/
        
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        DrawPlayer2 panel2 = new DrawPlayer2();
        frame.setSize(1200, 1000);
        frame.setResizable(false);
        frame.setContentPane(panel2);
    }  
    
    void pressed(MouseEvent e)
    {
        pageList.get(pageNumber).shapeList.add(new Shape(currentColor));
        button[UNDO].setEnabled(true);
        dragged(e);
    }
    
    void dragged(MouseEvent e)
    {
        pageList.get(pageNumber).shapeList.get(pageList.get(pageNumber).shapeList.size() - 1).pointList.add(new Point(e.getX(), e.getY()));
        //pageList.get(pageNumber).pixelCount++;
        pixels++;
        repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        if(ae.getActionCommand().equals("timer"))
        {
            intList.add(pixels);
            pixels = 0;
        }
        else
        {
            JButton buttonPressed = (JButton) ae.getSource();
            String buttonText = buttonPressed.getText();
            if(buttonText.equals("BLACK"))
                currentColor = Color.BLACK;
            else if(buttonText.equals("BLUE"))
                currentColor = Color.BLUE;
            else if(buttonText.equals("RED"))
                currentColor = Color.RED;
            else if(buttonText.equals("GREEN"))
                currentColor = Color.GREEN;
            else if(buttonText.equals("NEW PAGE"))
            {
                pageNumber = pageList.size();
                pageList.add(new Page());
                button[NEXT].setEnabled(false);
                button[PREV].setEnabled(true);
                intList.add(-4);
                intList.add(pixels);
                intList.add(pageNumber);
                pixels = 0;
                timer.restart();
            }
            else if(buttonText.equals("NEXT"))
            {
                if(++pageNumber == pageList.size() - 1)
                    button[NEXT].setEnabled(false);
                button[PREV].setEnabled(true);
                intList.add(-1);
                intList.add(pixels);
                pixels = 0;
                timer.restart();
            }
            else if(buttonText.equals("PREV"))
            {
                if(--pageNumber == 0)
                    button[PREV].setEnabled(false);
                button[NEXT].setEnabled(true);
                intList.add(-2);
                intList.add(pixels);
                pixels = 0;
                timer.restart();
            }
            else if(buttonText.equals("UNDO"))
            {
                int lastShapeNumber = pageList.get(pageNumber).shapeList.size();
                while(!pageList.get(pageNumber).shapeList.get(--lastShapeNumber).isVisible && lastShapeNumber > 0);
                pageList.get(pageNumber).shapeList.get(lastShapeNumber).isVisible = false;
                if(lastShapeNumber == 0)
                    button[UNDO].setEnabled(false);
                intList.add(-3);
                intList.add(pixels);
                pixels = 0;
                timer.restart();
            }
            else if(buttonText.equals("START"))
            {
                AudioFormat	audioFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,	44100.0F, 16, 2, 4, 44100.0F, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
                TargetDataLine targetDataLine = null;
                try
                {
                    targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                    targetDataLine.open(audioFormat);
                }
                catch (LineUnavailableException e){}
                AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
                recorder = new AudioRecorder( targetDataLine, targetType, audioOutputFile);
                recorder.start();
                buttonPressed.setText("STOP");
                isRecording = true;
                timer.start();
            }
            else if(buttonText.equals("STOP")) // Add code to terminate drawing and possibly start a new drawing
            {
                recorder.stopRecording();
                try 
                {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(graphicsOutputFile));
                    oos.writeObject(pageList);
                    oos.writeObject(intList);
                    Thread.sleep(2000);
                } 
                catch (FileNotFoundException ex) { Logger.getLogger(DrawRecorder2.class.getName()).log(Level.SEVERE, null, ex); } 
                catch (IOException | InterruptedException ex) { Logger.getLogger(DrawRecorder2.class.getName()).log(Level.SEVERE, null, ex); }
                System.out.println("File saved");
                isRecording = false;
                //System.exit(0);
            }
        }
        repaint();
    }    
}
