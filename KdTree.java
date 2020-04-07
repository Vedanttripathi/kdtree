

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;

public class KdTree {

    private Node n;
    private double xmin, ymin, xmax, ymax;
            // these variables are used for setting up the triangle for a node
    private int sizecount;
    private double champdist;
    private Node champnode;

    public KdTree() {
        xmin = 0.0;
        ymin = 0.0;
        xmax = 1.0;
        ymax = 1.0;
        sizecount = 0;
    }

    public boolean isEmpty() {
        return n == null;
    }

    public int size() {
        return sizecount;
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        return contains(n, p);
    }

    private boolean contains(Node n, Point2D p) {
        if (n == null) {
            return false;
        }

        if (n.p.equals(p)) {
            return true;
        }

        if (n.p.compareTo(p) > 0)
            return contains(n.lb, p);
        else
            return contains(n.rt, p);
    }

    public void insert(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        if (isEmpty())
        {
            sizecount++;
            n = new Node(p, new RectHV(xmin, ymin, xmax, ymax), true);
        }
        else if (contains(p)) // this condition simply checks for duplicates
        {
            // Does nothing
        }
        else
        {
            sizecount++;
            n = insert(n, p, n.split);
        }
    }

    private Node insert(Node n, Point2D p, boolean split)
    {
        Node node = n;

        if (node == null)
        {
            node = new Node(p, new RectHV(xmin, ymin, xmax, ymax), !split);
        }
        else
        {
            if(split) {
                if (node.p.compareTo(p) > 0) {
                    xmin = n.rect.xmin();
                    ymin = n.rect.ymin();
                    xmax = n.p.x();
                    ymax = n.rect.ymax();
                    node.lb = insert(node.lb, p, !split);
                }
                else {
                    xmin = n.p.x();
                    ymin = n.rect.ymin();
                    xmax = n.rect.xmax();
                    ymax = n.rect.ymax();
                    node.rt = insert(node.rt, p, !split);
                }
            }
            else
            {
                if (node.p.compareTo(p) > 0) {
                    xmin = n.rect.xmin();
                    ymin = n.rect.ymin();
                    xmax = n.rect.xmax();
                    ymax = n.p.y();
                    node.lb = insert(node.lb, p, !split);
                }
                else
                {
                    xmin = n.rect.xmin();
                    ymin = n.p.y();
                    xmax = n.rect.xmax();
                    ymax = n.rect.ymax();
                    node.rt = insert(node.rt, p, !split);
                }
            }
        }
        return node;
    }


    public void draw() {
        //
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("Argument cannot be null");

        List<Point2D> list = new ArrayList<Point2D>();
        range(n, list, rect);
        return list;
    }

    private void range(Node n, List<Point2D> list, RectHV rect) {
        if (n != null) {

            if (rect.contains(n.p)) {
                list.add(n.p);
            }
            if (n.intersects(rect)) {
                range(n.lb, list, rect);
                range(n.rt, list, rect);
            }
            else if (n.leftsideof(
                    rect))  // these four conditions only check if the querry rectangle is totally left or right of the splitting line
            {
                range(n.lb, list, rect);
            }
            else {
                range(n.rt, list, rect);
            }
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Argument cannot be null");

        if (this.contains(p))
            return p;

        if (n == null)
            return null;

        champdist = n.p.distanceSquaredTo(p);
        champnode = n;
        nearest(n, p);
        return champnode.p;
    }

    private void nearest(Node node, Point2D querry) {
        if (node == null) {
            return;
        }

        if (node.p.distanceSquaredTo(querry) < champdist) {
            champdist = node.p.distanceSquaredTo(querry);
            champnode = node;
        }

        Node winner = node.compareTo(querry) > 0 ? node.lb : node.rt;
        Node loser = node.compareTo(querry) > 0 ? node.rt : node.lb;

        if (winner != null) {
            nearest(winner, querry);
        }
        if (loser != null) {
            if (loser.rect.distanceSquaredTo(querry) < champdist)
                nearest(loser, querry);

        }
    }

    private class Node implements Comparable<Point2D> {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private boolean split;

        Node(Point2D p, RectHV rect, boolean split) {
            this.p = p;
            this.rect = rect;
            lb = null;
            rt = null;
            this.split = split;
        }

        @Override
        public int compareTo(Point2D that) {
            if (split) {
                if (that.x() < this.p.x())
                    return 1;
                else if (that.x() > this.p.x())
                    return -1;
                else
                    return 0;
            }
            else {
                if (that.y() < this.p.y())
                    return 1;
                else if (that.y() > this.p.y())
                    return -1;
                else
                    return 0;
            }
        }

        public boolean intersects(RectHV rect) {
            if (this.split)
                return rect.xmin() <= this.p.x() && this.p.x() <= rect.xmax();
            else
                return rect.ymin() <= this.p.y() && this.p.y() <= rect.ymax();
        }

        public boolean leftsideof(RectHV rect) {
            if (this.split)
                return rect.xmax() < this.p.x() && rect.xmin() < this.p.x();
            else
                return rect.ymax() < this.p.y() && rect.ymin() < this.p.y();
        }

        public Node create(Point2D data, RectHV rect, boolean split) {
            return new Node(data, rect, split);
        }
    }

    public static void main(String[] args) {
        KdTree k = new KdTree();
        System.out.println("Size : " + k.size());
        System.out.println("Empty : " + k.isEmpty());
        System.out.println("Checking insert method : ");
        k.insert(new Point2D(1.0, 0.0));
        k.insert(new Point2D(1.0, 1.0));
        k.insert(new Point2D(0.75, 0.25));
        k.insert(new Point2D(0.75, 0.25));
        k.insert(new Point2D(0.25, 0.75));
        System.out.println("Checking contains method : ");
        System.out.println(k.contains(new Point2D(0.75, 0.25)));
        System.out.println("Size : " + k.size());
        System.out.println("Empty : " + k.isEmpty());
        System.out.println("Checking nearest method : ");
        System.out.println(
                k.nearest(new Point2D(0.5, 0.3)).x() + "==>" + k.nearest(new Point2D(0.5, 0.3))
                                                                .y());
        System.out.println("Checking range method : ");
        for (Point2D p : k.range(new RectHV(0.0, 0.0, 1.0, 1.0)))
            System.out.println(p.x() + "==>" + p.y());

    }
}
