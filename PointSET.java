/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PointSET {

    private SET<Point2D> s;

    public PointSET()
    {
        s = new SET<Point2D>();
    }

    public boolean isEmpty()
    {
        return s.isEmpty();
    }

    public int size()
    {
        return s.size();
    }

    public void insert(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        if(!contains(p))
            s.add(p);
    }

    public boolean contains(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        return s.contains(p);
    }

    public void draw()
    {
        for (Point2D point : s) {
            StdDraw.point(point.x(), point.y());
        }
    }

    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null)
            throw new IllegalArgumentException("Argument cannot be null");

        List<Point2D> ls = new ArrayList<>();
        Iterator<Point2D> i = s.iterator();
        for (Point2D p : s)
        {
            if(rect.contains(p))
                ls.add(p);
        }
        return ls;
    }

    public Point2D nearest(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        Point2D min = null;
        for (Point2D point : s)
        {
            if (min == null || point.distanceSquaredTo(p) < min.distanceSquaredTo(p))
                min = point;
        }
        return min;
    }
    public static void main(String[] args) {

    }
}
