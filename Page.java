package drawrecorder2;
import java.io.Serializable;
import java.util.ArrayList;
class Page implements Serializable
{
    ArrayList<Shape> shapeList = new ArrayList();
    int pixelCount = 0;
}
