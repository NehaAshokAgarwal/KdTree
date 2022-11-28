import dsa.LinkedQueue;
import dsa.MaxPQ;
import dsa.Point2D;
import dsa.RectHV;
import stdlib.StdIn;
import stdlib.StdOut;

public class KdTreePointST<Value> implements PointST<Value> {
    private Node root; // Reference pointer to the root of the 2dTree
    private int n; // Number of nodes in the tress

    // Constructs an empty symbol table.
    public KdTreePointST() {
        root = null;
        n = 0;
    }

    // Returns true if this symbol table is empty, and false otherwise.
    public boolean isEmpty() {
        return this.n == 0;
    }

    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        return this.n;
    }

    // Inserts the given point and value into this symbol table.
    public void put(Point2D p, Value value) {
        // if p is null, then throw a NPE with a message saying that the p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // If the value is null, then throw a NPE saying that the value is null.
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        // Otherwise, create a rectangle object rect for the root Node which
        // is [−∞,∞]x[∞,∞].
        RectHV rect = new RectHV(Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        // I call the private put method by passing the appropriate values - root as a Node,
        // the given p and value, rect created above and true as we use x-coordinate for the root.
        root = this.put(root, p, value, rect, true);
    }

    // Returns the value associated with the given point in this symbol table, or null.
    public Value get(Point2D p) {
        // I check if the value of p is null or not. If it is then, I throw a NPE saying thst
        // the given p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // I call the private get method by passing the appropriate values - root as the
        // given p, true as the root compares the x-coordinate of a point.
        return get(root, p, true);
    }

    // Returns true if this symbol table contains the given point, and false otherwise.
    public boolean contains(Point2D p) {
        // I first check if the p is null or not. If it is then I throw a NPE,
        // saying that the p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // I call the get method and check if the returned values is null or not.
        // if it is not then i return true else return false.
        return this.get(p) != null;
    }

    // Returns all the points in this symbol table.
    public Iterable<Point2D> points() {
        // I created a LinkedQueue traversal of Node objects.
        LinkedQueue<Node> traversal = new LinkedQueue<>();
        // I create a linkedQueue collection of Point2D object.
        LinkedQueue<Point2D> collection = new LinkedQueue<>();
        // I enqueue the root Node in the traversal Node as it will aid me in traversing the tree.
        traversal.enqueue(root);
        // I iterate through all the Nodes in th tree...
        for (int i = 0; i < n; i++) {
            // I dequeue a Node from the traversal linkedList(initially it will be root and so on)
            Node l = traversal.dequeue();
            // Then I enqueue the point from dequeued Node in to the collection linkedList.
            collection.enqueue(l.p);
            // Then I check if the dequeued Node is null or not, If not and if it's right
            // node is not null as well, then I enqueue the right node and the process continues.
            if (l != null && l.rt != null) {
                traversal.enqueue(l.rt);
            }
            // I also check the left Node. If the left node and the Node itself is not null, then
            // I enqueue it into the traversal linkedList and the process continues.
            if (l != null && l.lb != null) {
                traversal.enqueue(l.lb);
            }
        }
        // At the end, I return the collection linkedList of iterable Point2D objects.
        return collection;
    }

    // Returns all the points in this symbol table that are inside the given rectangle.
    public Iterable<Point2D> range(RectHV rect) {
        // I check if the given rect is null or not.If it is, then I throw a NPE, saying that the
        // given rect is null.
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }
        // I create a LinkedQueue q of comparable Pont2D objects.
        LinkedQueue<Point2D> q = new LinkedQueue<>();
        // I call the private range() method by giving appropriate arguments - root as Node,
        // given rect and q.
        this.range(root, rect, q);
        // return the q of iterable Point2D objects.
        return q;
    }

    // Returns the point in this symbol table that is different from and closest to the given point,
    // or null.
    public Point2D nearest(Point2D p) {
        // If p is null, then I throw a NPE, saying that p is null. If it is, then I throw a
        // NPE saying tha the given p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }

        // I create a Point2D object z and set it to null.
        Point2D z = null;
        // I call the private the nearest method passing the root as a Node, z, the point and
        // true as the root compares the x-coordinate.
        return nearest(root, p, z, true);
    }

    // Returns up to k points from this symbol table that are different from and closest to the
    // given point.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        // If p is null, then I throw a NPE saying that the given p is null.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // I create a MaxPQ with a comparator of op.distanceToOrder().
        MaxPQ<Point2D> pq = new MaxPQ<>(p.distanceToOrder());
        // I call the private the nearest method by passing the appropriate arguments -
        // root as a Node, given p, given q, pq, true as root compares the x-coordinate.
        nearest(root, p, k, pq, true);
        // returns the pq of iterable Point2D objects.
        return pq;
    }

    // Note: In the helper methods that have lr as a parameter, its value specifies how to
    // compare the point p with the point x.p. If true, the points are compared by their
    // x-coordinates; otherwise, the points are compared by their y-coordinates. If the
    // comparison of the coordinates (x or y) is true, the recursive call is made on x.lb;
    // otherwise, the call is made on x.rt.

    // Inserts the given point and value into the KdTree x having rect as its axis-aligned
    // rectangle, and returns a reference to the modified tree.
    private Node put(Node x, Point2D p, Value value, RectHV rect, boolean lr) {
        // if node is null, then create and return the  node with the given arguments.
        // Also, increment n(number of nodes in the tree) by 1.
        if (x == null) {
            n++;
            return new Node(p, value, rect);
        }
        // If the given p is same as the p of the Node, then update the value of the p
        // as ST contains unique keys.
        if (p.equals(x.p)) {
            x.value = value;
        } else if (lr) {
            // x-coordinate
            // left
            if (p.x() < x.p.x()) {
                // Constructing the rectangle with the given arguments.
                RectHV r = new RectHV(rect.xMin(), rect.yMin(), x.p.x(), rect.yMax());
                // recursively calling the put on the left Node.
                x.lb = put(x.lb, p, value, r, !lr);
            } else {
                // right
                // Constructing the rectangle ith the given arguments.
                RectHV r = new RectHV(x.p.x(), rect.yMin(), rect.xMax(), rect.yMax());
                // recursively calling the put on the right Node.
                x.rt = put(x.rt, p, value, r, !lr);
            }
        } else {
            // Y-coordinate
            // bottom
            if (p.y() < x.p.y()) {
                // Constructing the rectangle with the arguments.
                RectHV r = new RectHV(rect.xMin(), rect.yMin(), rect.xMax(), x.p.y());
                // Recursively calling the put on the left Node.
                x.lb = put(x.lb, p, value, r, !lr);
            } else {
                // up
                // Constructing the rectangle with the given arguments.
                RectHV r = new RectHV(rect.xMin(), x.p.y(), rect.xMax(), rect.yMax());
                // recursively calling put on the right Node.
                x.rt = put(x.rt, p, value, r, !lr);
            }
        }
        // Returning the root pointer back to the caller.
        return x;
    }

    // Returns the value associated with the given point in the KdTree x, or null.
    private Value get(Node x, Point2D p, boolean lr) {
        // If x is null, then return null.
        if (x == null) {
            return null;
        }
        // If the given point and Node's point is same then update the value of the point
        // as ST contains unique keys.
        if (x.p.x() == p.x() && x.p.y() == p.y()) {
            return x.value;

            // Whether the root is x-aligner or y-aligned and if the x/y coordinate
            // of the given point is greater than the Node's point. Then call put
            // recursively on the right side of the tree.

        } else if (!lr && p.y() >= x.p.y() || lr && p.x() >= x.p.x()) {
            return get(x.rt, p, !lr);

            // Whether the root is x-aligner or y-aligned and if the x/y coordinate
            // of the given point is less than the Node's point. Then call put
            // recursively on the left side of the tree.
        } else if (!lr && p.y() < x.p.y() || lr && p.x() < x.p.x()) {
            return get(x.lb, p, !lr);
        }
        // Otherwise, return null.
        return null;
    }

    // Collects in the given queue all the points in the KdTree x that are inside rect.
    private void range(Node x, RectHV rect, LinkedQueue<Point2D> q) {
        // If x is null, then return null.
        if (x == null) {
            return;
        }
        // pruning rule says that if the given rectangle intersects the
        // node.rect, then explore it and enqueue the points otherwise don't.

        // If the given rectangle contains the Node's point, then enqueue the point.
        if (rect.contains(x.p)) {
            q.enqueue(x.p);
        }
        // If the given rectangle and the node.rectangle intersects then call range recursively on
        // its children that is explore the Node.
        if (rect.intersects(x.rect)) {
            range(x.lb, rect, q);
            range(x.rt, rect, q);
        }

    }

    // Returns the point in the KdTree x that is closest to p, or null; nearest is the closest
    // point discovered so far.
    private Point2D nearest(Node x, Point2D p, Point2D nearest, boolean lr) {
        // If x is null, then return nearest.
        Point2D nearPoint = nearest;
        if (x == null) {
            return nearPoint;
        }
        // Set the best_distance to infinity.
        double bestDist = Double.POSITIVE_INFINITY;

        // If nearest i snot null, then update the best_dist to the distance between the
        // given point and the nearest point so far.
        if (nearPoint != null) {
            bestDist = p.distanceSquaredTo(nearest);
        }
        // If the given point and node's point is not same and if the distance between
        // the node's point and the given point is less than the best_distance found so far
        // then update nearest to node's point.
        if (!x.p.equals(p) && x.p.distanceSquaredTo(p) < bestDist) {
            nearPoint = x.p;
        }
        // set first as the left node of the current node.
        Node first = x.lb;
        // set second as the right node of the current node
        Node second = x.rt;
        // If whether the Node is x-aligned or y-aligned and the x/y coordinate
        // of the given point is greater than that of the node's point then,
        // first as the x.rt and x.lb.

        // This switch help in recurring back.
        if (lr && x.p.x() <= p.x() || !lr && x.p.y() <= p.y()) {
            first = x.rt;
            second = x.lb;
        }
        // Recursively call the nearest method on the first Node and then on the second Node.
        Point2D l = nearest(first, p, nearPoint, !lr);
        return nearest(second, p, l, !lr);
    }

    // Collects in the given max-PQ up to k points from the  KdTree x that are different from and
    // closest to p.
    private void nearest(Node x, Point2D p, int k, MaxPQ<Point2D> pq, boolean lr) {
        // If x is null or if the size of the pq is greater than k, then return.
        if (x == null || pq.size() > k) {
            return;
        }
        // If the given point and the node's point is different then insert it into the
        // maxPQ.
        if (!p.equals(x.p)) {
            pq.insert(x.p);
        }
        // If the size of the pq is less than k, then do a delMin on the maxPQ.
        if (pq.size() > k) {
            pq.delMax();
        }
        // According, the pruning rule - If the distance between the node's rect and
        // the given point is less than the distance between the given point and the
        // best point so far then explore the Node.
        if (x.rect.distanceSquaredTo(p) < p.distanceSquaredTo(pq.max())) {
            // set first as the left node of the current node.
            Node first = x.lb;
            // set second as the right node of the current node.
            Node second = x.rt;

            // If whether the Node is x-aligned or y-aligned and the x/y coordinate
            // of the given point is greater than that of the node's point then,
            // first as the x.rt and x.lb.
            if (lr && x.p.x() <= p.x() || !lr && x.p.y() <= p.y()) {
                first = x.rt;
                second = x.lb;
            }
            // Recursively call the nearest method on the first Node and then on the second Node.
            nearest(first, p, k, pq, !lr);
            nearest(second, p, k, pq, !lr);
        }
    }

    // A representation of node in a KdTree in two dimensions (ie, a 2dTree). Each node stores a
    // 2d point (the key), a value, an axis-aligned rectangle, and references to the left/bottom
    // and right/top subtrees.
    private class Node {
        private Point2D p;   // the point (key)
        private Value value; // the value
        private RectHV rect; // the axis-aligned rectangle
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree

        // Constructs a node given the point (key), the associated value, and the
        // corresponding axis-aligned rectangle.
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
