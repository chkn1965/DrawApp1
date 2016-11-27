package drawrecorder2;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
class Shape implements Serializable
{
    ArrayList<Point> pointList = new ArrayList();
    Color color;
    boolean isVisible = true, isClientVisible = true;    
    public Shape(Color color)
    { this.color = color; }
}
