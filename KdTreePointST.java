import dsa.LinkedQueue;
import dsa.MaxPQ;
import dsa.Point2D;
import dsa.RectHV;
import stdlib.StdIn;
import stdlib.StdOut;

public class KdTreePointST<Value> implements PointST<Value> {
    private Node root; // Reference pointer to the root of the 2dTree
    private int n; // Number of nodes in the tress

    
    public KdTreePointST() {
        root = null;
        n = 0;
    }
   
    public boolean isEmpty() {
        return this.n == 0;
    }

    .
    public int size() {
        return this.n;
    }


    public void put(Point2D p, Value value) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        RectHV rect = new RectHV(Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        root = this.put(root, p, value, rect, true);
    }
    
    public Value get(Point2D p) {

        if (p == null) {
            throw new NullPointerException("p is null");
        }
        return get(root, p, true);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        return this.get(p) != null;
    }

    public Iterable<Point2D> points() {
        LinkedQueue<Node> traversal = new LinkedQueue<>();
        LinkedQueue<Point2D> collection = new LinkedQueue<>();
        traversal.enqueue(root);
        for (int i = 0; i < n; i++) {
            Node l = traversal.dequeue();
            collection.enqueue(l.p);
            if (l != null && l.rt != null) {
                traversal.enqueue(l.rt);
            }
            if (l != null && l.lb != null) {
                traversal.enqueue(l.lb);
            }
        }
        return collection;
    }
    
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        this.range(root, rect, q);
        return q;
    }
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        
        Point2D z = null;
        return nearest(root, p, z, true);
    }

    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        MaxPQ<Point2D> pq = new MaxPQ<>(p.distanceToOrder());
        nearest(root, p, k, pq, true);
        return pq;
    }

    // Note: In the helper methods that have lr as a parameter, its value specifies how to
    // compare the point p with the point x.p. If true, the points are compared by their
    // x-coordinates; otherwise, the points are compared by their y-coordinates. If the
    // comparison of the coordinates (x or y) is true, the recursive call is made on x.lb;
    // otherwise, the call is made on x.rt.

 
    private Node put(Node x, Point2D p, Value value, RectHV rect, boolean lr) {
        if (x == null) {
            n++;
            return new Node(p, value, rect);
        }
        if (p.equals(x.p)) {
            x.value = value;
        } else if (lr) {
            if (p.x() < x.p.x()) {.
                RectHV r = new RectHV(rect.xMin(), rect.yMin(), x.p.x(), rect.yMax());
                x.lb = put(x.lb, p, value, r, !lr);
            } else {
                RectHV r = new RectHV(x.p.x(), rect.yMin(), rect.xMax(), rect.yMax());
                x.rt = put(x.rt, p, value, r, !lr);
            }
        } else {
            if (p.y() < x.p.y()) {
                RectHV r = new RectHV(rect.xMin(), rect.yMin(), rect.xMax(), x.p.y());
                x.lb = put(x.lb, p, value, r, !lr);
            } else {
                RectHV r = new RectHV(rect.xMin(), x.p.y(), rect.xMax(), rect.yMax());
                x.rt = put(x.rt, p, value, r, !lr);
            }
        }
        return x;
    }

    private Value get(Node x, Point2D p, boolean lr) {

        if (x == null) {
            return null;
        }
        if (x.p.x() == p.x() && x.p.y() == p.y()) {
            return x.value;

        } else if (!lr && p.y() >= x.p.y() || lr && p.x() >= x.p.x()) {
            return get(x.rt, p, !lr);
        } 
        else if (!lr && p.y() < x.p.y() || lr && p.x() < x.p.x()) {
            return get(x.lb, p, !lr);
        }
        return null;
    }
    private void range(Node x, RectHV rect, LinkedQueue<Point2D> q) {
        // If x is null, then return null.
        if (x == null) {
            return;
        }
        if (rect.contains(x.p)) {
            q.enqueue(x.p);
        }
        if (rect.intersects(x.rect)) {
            range(x.lb, rect, q);
            range(x.rt, rect, q);
        }

    }
    private Point2D nearest(Node x, Point2D p, Point2D nearest, boolean lr) {
        Point2D nearPoint = nearest;
        if (x == null) {
            return nearPoint;
        }
        double bestDist = Double.POSITIVE_INFINITY;
        if (nearPoint != null) {
            bestDist = p.distanceSquaredTo(nearest);
        }
        if (!x.p.equals(p) && x.p.distanceSquaredTo(p) < bestDist) {
            nearPoint = x.p;
        }
        Node first = x.lb;
        Node second = x.rt;
        if (lr && x.p.x() <= p.x() || !lr && x.p.y() <= p.y()) {
            first = x.rt;
            second = x.lb;
        }
        Point2D l = nearest(first, p, nearPoint, !lr);
        return nearest(second, p, l, !lr);
    }

    private void nearest(Node x, Point2D p, int k, MaxPQ<Point2D> pq, boolean lr) {
        if (x == null || pq.size() > k) {
            return;
        }
        if (!p.equals(x.p)) {
            pq.insert(x.p);
        }
        if (pq.size() > k) {
            pq.delMax();
        }
        if (x.rect.distanceSquaredTo(p) < p.distanceSquaredTo(pq.max())) {
            Node first = x.lb;
            Node second = x.rt;
            if (lr && x.p.x() <= p.x() || !lr && x.p.y() <= p.y()) {
                first = x.rt;
                second = x.lb;
            }
            nearest(first, p, k, pq, !lr);
            nearest(second, p, k, pq, !lr);
        }
    }

    private class Node {
        private Point2D p;   // the point (key)
        private Value value; // the value
        private RectHV rect; // the axis-aligned rectangle
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree

        Node(Point2D p, Value value, RectHV rect) {
            this.p = p;
            this.value = value;
            this.rect = rect;
        }
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        KdTreePointST<Integer> st = new KdTreePointST<>();
        double qx = Double.parseDouble(args[0]);
        double qy = Double.parseDouble(args[1]);
        int k = Integer.parseInt(args[2]);
        Point2D query = new Point2D(qx, qy);
        RectHV rect = new RectHV(-1, -1, 1, 1);
        int i = 0;
        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();
            Point2D p = new Point2D(x, y);
            st.put(p, i++);
        }
        StdOut.println("st.empty()? " + st.isEmpty());
        StdOut.println("st.size() = " + st.size());
        StdOut.printf("st.contains(%s)? %s\n", query, st.contains(query));
        StdOut.printf("st.range(%s):\n", rect);
        for (Point2D p : st.range(rect)) {
            StdOut.println("  " + p);
        }
        StdOut.printf("st.nearest(%s) = %s\n", query, st.nearest(query));
        StdOut.printf("st.nearest(%s, %d):\n", query, k);
        for (Point2D p : st.nearest(query, k)) {
            StdOut.println("  " + p);
        }
    }
}
