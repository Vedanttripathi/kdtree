

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;

public class KdTree {

    private Node n;
    private double xmin, ymin, xmax, ymax; // these variables are used for setting up the triangle for a node
    private int sizecount;

    public KdTree()
    {
        n = null;
        xmin = 0.0;
        ymin = 0.0;
        xmax = 1.0;
        ymax = 1.0;
        sizecount = 0;
    }

    public boolean isEmpty()
    {
        return n == null;
    }

    public int size()
    {
        return sizecount;
    }

    public void insert(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        if(n == null)
        {
            sizecount ++;
            n = new Node(p, new RectHV(xmin, ymin, xmax, ymax), true);
        }
        else if (!contains(p))
        {
           n = insert(n, p, new RectHV(xmin, ymin, xmax, ymax), n.split);
        }
    }

    private Node insert(Node n, Point2D p, RectHV rect, boolean split)
    {

        if (n == null)
        {
            sizecount++;
            return new Node(p, rect, !split);
        }
        else
        {
            if (n.p.compareTo(p) > 0)
            {
                n.lb = insert(n.lb, p, leftbottomrect(n), !split);
            }
            else
            {
                n.rt = insert(n.rt, p, righttoprect(n), !split);
            }
        }
        return n;
    }

    private RectHV leftbottomrect(Node n) // to get the left / bottom rectangle for every node
    {
        if (n.split)
        {
            xmin = n.rect.xmin();
            ymin = n.rect.ymin();
            xmax = n.p.x();
            ymax = n.rect.ymax();
            return new RectHV(xmin, ymin, xmax, ymax);
        }
        else
        {
            xmin = n.p.x();
            ymin = n.rect.ymin();
            xmax = n.rect.xmax();
            ymax = n.rect.ymax();
            return new RectHV(xmin, ymin, xmax, ymax);
        }
    }

    private RectHV righttoprect(Node n) // to get the right / top rectangle for every node
    {
        if (n.split)
        {
            xmin = n.rect.xmin();
            ymin = n.rect.ymin();
            xmax = n.rect.xmax();
            ymax = n.p.y();
            return new RectHV(xmin, ymin, xmax, ymax);
        }
        else
        {
            xmin = n.rect.xmin();
            ymin = n.p.y();
            xmax = n.rect.xmax();
            ymax = n.rect.ymax();
            return new RectHV(xmin, ymin, xmax, ymax);
        }
    }

    public boolean contains(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        return contains(n, p);
    }

    private boolean contains(Node n , Point2D p)
    {
        if (n == null) {
            return false;
        }

            if (n.p.equals(p))
            {
                return true;
            }
            else if (n.p.compareTo(p) > 0)
                return contains(n.lb, p);
            else
                return contains(n.rt, p);
    }

    public void draw()
    {
        //
    }

    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null)
            throw new IllegalArgumentException("Argument cannot be null");

        List<Point2D> list = new ArrayList<Point2D>();
        range(n, list, rect);
        return list;
    }

    private void range(Node n, List<Point2D> list, RectHV rect)
    {
        if (n != null)
        {

            if (rect.contains(n.p)) {
                list.add(n.p);
            }
            if (n.intersects(rect)) {
                range(n.lb, list, rect);
                range(n.rt, list, rect);
            }
            else if (n.leftsideof(rect))  // these four conditions only check if the querry rectangle is totally left or right of the splitting line
            {
                range(n.lb, list, rect);
            }
            else {
                range(n.rt, list, rect);
            }
        }
    }

    public Point2D nearest(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        if (this.contains(p))
            return p;

        if(n == null)
            return null;

        double champ = n.p.distanceSquaredTo(p);
        return nearest(n, p, champ, n);
    }

    private Point2D nearest(Node node, Point2D querry, double champdistance, Node champnode)
    {
        if (node == null)
        {
            return champnode.p;
        }

        if (node.p.distanceSquaredTo(querry) < champdistance)
        {
            champdistance = node.p.distanceSquaredTo(querry);
            champnode = node;
        }

        Node winner = node.compareTo(querry) > 0 ? node.lb : node.rt;
        Node loser = node.compareTo(querry) > 0 ? node.rt : node.lb;
            if (winner != null)
            {
                champnode.p = nearest(winner, querry, champdistance, champnode);
            }
            if (loser != null)
            {
                if(loser.rect.distanceSquaredTo(querry) < champdistance)
                    champnode.p = nearest(loser, querry, champdistance, champnode);

            }
        return champnode.p;
    }

    private static class Node implements Comparable<Point2D>{
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private boolean split;

        public Node(Point2D p, RectHV rect, boolean split)
        {
            this.p = p;
            this.rect = rect;
            lb = null;
            rt = null;
            this.split = split;
        }

        @Override
        public int compareTo(Point2D that)
        {
            if(split)
            {
                if (that.x() < this.p.x())
                    return 1;
                else if (that.x() > this.p.x())
                    return -1;
                else
                    return 0;
            }
            else
            {
                if (that.y() < this.p.y())
                    return 1;
                else if (that.y() > this.p.y())
                    return -1;
                else
                    return 0;
            }
        }

        public boolean intersects (RectHV rect)
        {
            if(this.split)
                return rect.xmin() <= this.p.x() && this.p.x() <= rect.xmax();
            else
                return rect.ymin() <= this.p.y() && this.p.y() <= rect.ymax();
        }

        public boolean leftsideof (RectHV rect)
        {
            if (this.split)
               return rect.xmax() < this.p.x() && rect.xmin() < this.p.x();
            else
                return rect.ymax() < this.p.y() && rect.ymin() < this.p.y();
        }
    }
    public static void main(String[] args) {
        KdTree k = new KdTree();
        System.out.println(k.size());
        System.out.println(k.isEmpty());
        k.insert(new Point2D(1.0, 0.0));
        k.insert(new Point2D(1.0, 1.0));
        k.insert(new Point2D(0.75, 0.25));
        k.insert(new Point2D(0.75, 0.25));
        k.insert(new Point2D(0.25, 0.75));
        System.out.println(k.nearest(new Point2D(0.5, 0.3)).x()+ "==>" + k.nearest(new Point2D(0.5, 0.3)).y());
        for (Point2D p : k.range(new RectHV(0.0, 0.0, 1.0, 1.0)))
            System.out.println(p.x() + "==>" + p.y());

    }
}
