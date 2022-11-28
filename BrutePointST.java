import dsa.LinkedQueue;
import dsa.MinPQ;
import dsa.Point2D;
import dsa.RectHV;
import dsa.RedBlackBinarySearchTreeST;
import stdlib.StdIn;
import stdlib.StdOut;

public class BrutePointST<Value> implements PointST<Value> {
    private RedBlackBinarySearchTreeST<Point2D, Value> bst; // RedBlackBST
    
    public BrutePointST() {
        this.bst = new RedBlackBinarySearchTreeST<Point2D, Value>();
    }

    public boolean isEmpty() {
        return bst.isEmpty();
    }
    
    public int size() {
        return bst.size();
    }

    public void put(Point2D p, Value value) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        bst.put(p, value);
    }
    
    public Value get(Point2D p) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        return bst.get(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        return bst.contains(p);
    }
    
    public Iterable<Point2D> points() {
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        for (int i = 0; i < bst.size(); i++) {
            q.enqueue(bst.select(i));
        }
        return q;
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        for (int i = 0; i < bst.size(); i++) {
            if (rect.contains(bst.select(i))) {
                q.enqueue(bst.select(i));
            }
        }
        return q;
    }
    public Point2D nearest(Point2D p) {
        // If p is null then throw a NPE saying that the p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        double closestDistance = Double.POSITIVE_INFINITY;
        // Set j to 0.
        int j = 0;
        for (int i = 0; i < bst.size(); i++) {
            if (!p.equals(bst.select(i)) && p.distanceSquaredTo(bst.select(i)) < closestDistance) {
                closestDistance = p.distanceSquaredTo(bst.select(i));
                j = i;
            }
        }
        if (closestDistance > 0) {
            return bst.select(j);
        }
        return null;
    }

    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        MinPQ<Point2D> pq = new MinPQ<>(p.distanceToOrder());
        int z = 1;
        for (int i = 0; i < bst.size(); i++) {
            pq.insert(bst.select(i));
        }
        for (int j = 0; j < bst.size(); j++) {
            Point2D x = pq.delMin();
            if (!p.equals(x) && z <= k) {
                q.enqueue(x);
                z++;
            } else if (z > k) {
                break;
            }
        }
        return q;
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        BrutePointST<Integer> st = new BrutePointST<Integer>();
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
