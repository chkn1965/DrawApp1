package drawrecorder2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class DrawPlayer2 extends JPanel implements ActionListener
{
    File graphicsInputFile, audioInputFile;
    AudioPlayer player;
    static ArrayList<Page> pageList;
    static ArrayList<Integer> intList;
    public static int pageNumber = 0, counter = 0;
    static Color currentColor = Color.BLACK;
    static Timer timer;
    
    public DrawPlayer2() throws FileNotFoundException, IOException, ClassNotFoundException
    {
        String fileName = "testing123";  //JOptionPane.showInputDialog("Enter File Name");
        graphicsInputFile = new File(fileName + ".drw");
        audioInputFile = new File(fileName + ".wav");
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(graphicsInputFile));
        pageList = (ArrayList<Page>) ois.readObject();
        intList = (ArrayList<Integer>) ois.readObject();
        AudioPlayer player = new AudioPlayer(audioInputFile);
        timer = new Timer(100, this);
        timer.start();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        int pixels = 0;
        Page page = pageList.get(pageNumber);
        for(Shape shape: page.shapeList)
        {
            if(shape.isClientVisible)
            {
                g.setColor(shape.color);
                for(Point point: shape.pointList)
                {
                    if(shape.pointList.indexOf(point) != shape.pointList.size() - 1)
                        g.drawLine(point.x, point.y, shape.pointList.get(shape.pointList.indexOf(point) + 1).x, shape.pointList.get(shape.pointList.indexOf(point) + 1).y);
                    else 
                        g.drawLine(point.x, point.y, point.x, point.y);
                    pixels++;
                    if(pixels >= page.pixelCount)
                        return;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        if(counter >= intList.size() - 2)
        {
            timer.stop();
            System.out.println("Timer stopped");
        }
        int value = intList.get(++counter);
        System.out.println(value);
        if(value >= 0)
        {
            pageList.get(pageNumber).pixelCount += value;
            repaint();
            return;
        }
        pageList.get(pageNumber).pixelCount += intList.get(++counter);
        switch (value) {
            case -1:
                pageNumber++;
                break;
            case -2:
                pageNumber--;
                break;
            case -3:
                int lastShapeNumber = pageList.get(pageNumber).shapeList.size();
                while(!pageList.get(pageNumber).shapeList.get(--lastShapeNumber).isClientVisible);
                pageList.get(pageNumber).shapeList.get(lastShapeNumber).isClientVisible = false;
                pageList.get(pageNumber).pixelCount -= pageList.get(pageNumber).shapeList.get(lastShapeNumber).pointList.size();
                break;
            case -4:
                pageNumber = intList.get(++counter);
                break;
            default:
                break;
        }
        repaint();
    }   
}
