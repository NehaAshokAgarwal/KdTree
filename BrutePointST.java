import dsa.LinkedQueue;
import dsa.MinPQ;
import dsa.Point2D;
import dsa.RectHV;
import dsa.RedBlackBinarySearchTreeST;
import stdlib.StdIn;
import stdlib.StdOut;

public class BrutePointST<Value> implements PointST<Value> {
    private RedBlackBinarySearchTreeST<Point2D, Value> bst; // RedBlackBST

    // Constructs an empty symbol table.
    public BrutePointST() {
        this.bst = new RedBlackBinarySearchTreeST<Point2D, Value>();
    }

    // Returns true if this symbol table is empty, and false otherwise.
    public boolean isEmpty() {
        return bst.isEmpty();
    }

    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        return bst.size();
    }

    // Inserts the given point and value into this symbol table.
    public void put(Point2D p, Value value) {
        // if p is null then throw a NullPointerException exception
        // with a message saying that p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // If value is null then throw a NullPointerException saying that the value
        // is null.
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        // Otherwise, insert the given key-value pair into the symbol table.
        bst.put(p, value);
    }

    // Returns the value associated with the given point in this symbol table, or null.
    public Value get(Point2D p) {
        // if p is null, then throw a NullPointerException with a message saying that
        // p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Otherwise, return the value associated with the given key.
        return bst.get(p);
    }

    // Returns true if this symbol table contains the given point, and false otherwise.
    public boolean contains(Point2D p) {
        // If p is null, then throw a NullPointerException with a message saying that the
        // p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Otherwise, return true or false based on whether p is in the ST or not.
        return bst.contains(p);
    }

    // Returns all the points in this symbol table.
    public Iterable<Point2D> points() {
        // Create a LinkedQueue of Pont2D objects.
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        // For all the key-value pair in the ST,enqueue the point with the given point(key)
        // with the given rank( which is value of i) is enqueue to it.
        for (int i = 0; i < bst.size(); i++) {
            q.enqueue(bst.select(i));
        }
        // return the Linked queue of iterable Point2D objects.
        return q;
    }

    // Returns all the points in this symbol table that are inside the given rectangle.
    public Iterable<Point2D> range(RectHV rect) {
        //  If the given rect is null, then throw a NPE with the message saying that the
        // rect is null.
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }
        // Create a LinkedQueue q of Point2D objects.
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        // For all the key-value pair in the ST...
        for (int i = 0; i < bst.size(); i++) {
            // If the given rect contains the point which is in the ST..
            if (rect.contains(bst.select(i))) {
                // enqueue it into the linked list created above.
                q.enqueue(bst.select(i));
            }
        }
        // return the linked list.
        return q;
    }

    // Returns the point in this symbol table that is different from and closest to the given point,
    // or null.
    public Point2D nearest(Point2D p) {
        // If p is null then throw a NPE saying that the p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Set the closest distance to infinity.
        double closestDistance = Double.POSITIVE_INFINITY;
        // Set j to 0.
        int j = 0;
        // For all the given key-value pair...
        for (int i = 0; i < bst.size(); i++) {
            // If the given point is different from the point at a given rank(i) and the
            // distance between the given point and the point at the given rank(i) is less then,
            // the closest distance found so far, then update the closes distance tro that distance.
            if (!p.equals(bst.select(i)) && p.distanceSquaredTo(bst.select(i)) < closestDistance) {
                closestDistance = p.distanceSquaredTo(bst.select(i));
                // Take a note of the rank of the Point in the ST
                j = i;
            }
        }
        // If the value of the closest distance found so far is positive then, retur the
        // point to the caller.
        if (closestDistance > 0) {
            return bst.select(j);
        }
        // Otherwise, return null.
        return null;
    }

    // Returns up to k points from this symbol table that are different from and closest to the
    // given point.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        // If p is null, then throw a NPE with a message saying that the given p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Create a LinkedQueue of comparable Point2D objects.
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        // Create a MinPQ with a comparator of distanceToOrder from the given point p.
        MinPQ<Point2D> pq = new MinPQ<>(p.distanceToOrder());
        // Set z to 1 to keep track of the number of points returned as only k points
        // are supposed to be returned.
        int z = 1;
        // for all the key-value pair in the ST...
        for (int i = 0; i < bst.size(); i++) {
            // insert the key(Point) with the given rank(i) into the MinPQ.
            pq.insert(bst.select(i));
        }
        // For all the key-value pair in the ST...
        for (int j = 0; j < bst.size(); j++) {
            // Do a delMin on the minPQ in order to get the point which is closest
            // to the given point p.
            Point2D x = pq.delMin();
            // As long as the deleted point is different from the given point and
            // number of points returned is less than the given value of k then enqueue
            // it into the LinkedQueue.
            if (!p.equals(x) && z <= k) {
                q.enqueue(x);
                // Increment z by 1.
                z++;
            } else if (z > k) {
                // Otherwise, if the k number of points have been enqueued, then break the loop.
                break;
            }
        }
        // return the linked list q of K-nearest points to the given point.
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
