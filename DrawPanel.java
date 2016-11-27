package drawrecorder2;

import java.awt.Graphics;
import javax.swing.JPanel;

class DrawPanel extends JPanel
    {
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            for(Shape shape: DrawRecorder2.pageList.get(DrawRecorder2.pageNumber).shapeList)
            {
                if(shape.isVisible)
                {
                    g.setColor(shape.color);
                    for(Point point: shape.pointList)
                        if(shape.pointList.indexOf(point) != shape.pointList.size() - 1)
                            g.drawLine(point.x, point.y, shape.pointList.get(shape.pointList.indexOf(point) + 1).x, shape.pointList.get(shape.pointList.indexOf(point) + 1).y);
                        else 
                            g.drawLine(point.x, point.y, point.x, point.y);
                }
            } 
        }
    }
