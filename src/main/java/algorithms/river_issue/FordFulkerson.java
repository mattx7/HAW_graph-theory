package algorithms.river_issue;

import algorithms.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by MattX7 on 25.11.2016.
 */
public class FordFulkerson implements Algorithm {
    private static Logger logger = Logger.getLogger(FordFulkerson.class);
    static boolean preview = true;

    private Graph graph;
    private Node source;
    private Node sink;

    private Double capacity[][]; // capacity matrix
    private Double flow[][]; // flow matrix

    private List<Node> nodes = new LinkedList<Node>();

    /**
     * Initialization of the algorithm. This method has to be called before the
     * {@link #compute()} method to initialize or reset the algorithm according
     * to the new given graph.
     *
     * @param graph The graph this algorithm is using.
     */
    public void init(Graph graph) throws IllegalArgumentException {
        Preconditions.isNetwork(graph);
        // TODO schwach zusammenhängend
        // TODO schlicht
        // TODO simple directed graph
        //Implementation
        this.graph = graph;
        nodes = ImmutableList.copyOf(graph.getEachNode());
        int size = nodes.size();

        capacity = new Double[size][size]; // capacity matrix
        flow = new Double[size][size]; // flow matrix
        pred = new int[size];  // array to store augmenting path

        // Initialize empty flow & capacity.
        Iterator<Node> iIterator = nodes.iterator();
        for (int i = 0; i < size; i++) {
            Node nodeI = iIterator.next();
            Iterator<Node> jIterator = nodes.iterator();
            for (int j = 0; j < size; j++) {
                Node nodeJ = jIterator.next();
                capacity[i][j] = nodeI.getEdgeBetween(nodeJ).getAttribute("capacity");
                flow[i][j] = 0.0;
            }
        }


    }

    /**
     * Run the algorithm. The {@link #init(Graph)} method has to be called
     * before computing.
     *
     * @see #init(Graph)
     */
    public void compute() {
        path = PathIterator();
        while (hasPath(source, sink)) {
            List<Node> path = path.next();
            int bottleNeck = getBottleNeck(path);
            setFlow(path, bottleNeck);

        }
    }

    /**
     * Returns index of a Node
     *
     * @param node from we want to know the index
     * @return index
     * @throws NoSuchElementException no such node in the list
     */
    @NotNull
    private Integer getIndex(@NotNull Node node) {
        int i = nodes.indexOf(node);
        if (i < 0) throw new NoSuchElementException();
        return i;
    }

    @NotNull
    private Double min(double x, double y) {
        return x < y ? x : y;  // returns minimum of x and y
    }
}
