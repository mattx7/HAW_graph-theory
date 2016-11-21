package algorithms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by MattX7 on 04.11.2016.
 * <p>
 * Ref:
 * http://www.orklaert.de/floyd-warshall-algorithmus.php
 * https://www.cs.usfca.edu/~galles/visualization/Floyd.html
 */
public class FloydWarshall implements Algorithm {

    public Double distance;
    public Integer hits = 0;
    public int steps = -1;
    public static boolean preview = true;
    public LinkedList<Node> shortestPath;
    private Graph graph;
    private Node source;
    private Node target;
    private int n;
    private double[][] distances;
    private List<Node> nodes = new LinkedList<Node>();


    public void init(Graph graph) {
        if (!hasWeights(graph))
            throw new IllegalArgumentException();

        this.graph = graph;
        nodes = ImmutableList.copyOf(graph.getEachNode());
        n = nodes.size();
        hits = n * n; // Zugriffe auf den Graphen TODO stimmt das noch?
        int n = graph.getNodeCount();
        distances = new double[n][n];

        Iterator<Node> nodesForI = graph.getNodeIterator();
        for (int i = 0; i < n; i++) {
            Iterator<Node> nodesForJ = graph.getNodeIterator();
            Node NodeI = nodesForI.next();
            for (int j = 0; j < n; j++) {
                Node NodeJ = nodesForJ.next();
                if (NodeI == NodeJ) {
                    distances[i][j] = 0.0;
                } else {
                    if (NodeI.hasEdgeBetween(NodeJ)) {
                        String weight = NodeI.getEdgeBetween(NodeJ).getAttribute("weight").toString();
                        Double.parseDouble(weight);
                        distances[i][j] = Double.parseDouble(weight);
                    } else {
                        distances[i][j] = Integer.MAX_VALUE;
                    }
                }
            } // TODO andere matrix mit aufbauen
        }
        if (preview) System.out.println("================== Start ======================");
        if (preview) printMatrix();
        if (preview) System.out.println();
    }

    public void compute() {
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) { // TODO k mit inf oder so nicht machen
                for (int j = 0; j < n; j++) {
                    double sum = distances[i][k] + distances[k][j];
                    if (distances[i][j] > sum) {
                        distances[i][j] = sum;
                        // TODO andere matrix

                    }
                }
            }
            if (preview) System.out.println("================== " + k + " ======================");
            if (preview) printMatrix();
            if (preview) System.out.println();
        }
        distance = distances[getIndex(source)][getIndex(target)];
    }

    /**
     * Sets source and target before compute()
     *
     * @param source source node
     * @param target target node
     */
    public void setSourceAndTarget(@NotNull Node source,
                                   @NotNull Node target) {
        if (this.source != null && this.source.hasAttribute("title"))
            this.source.removeAttribute("title");
        if (this.target != null && this.target.hasAttribute("title"))
            this.target.removeAttribute("title");
        this.source = source;
        this.target = target;
        source.setAttribute("title", "source");
        target.setAttribute("title", "target");
    }


    /**
     * @return shortestWay
     */
    public List<Node> getShortestPath() {
        if (hits == 0)
            throw new IllegalArgumentException("do compute before this method");
        LinkedList<Node> ShortestPath = new LinkedList<Node>();
        ShortestPath.add(target);
        Node current = target;
        while (hasPred(current)) {
            shortestPath.add(getPred(current));
            current = getPred(current);
        }
        return Lists.reverse(shortestPath);
    }

    private Node getPred(Node node) {
        int y = getIndex(node);
        int maxColumn = -1;
        for (int x = 1; x < n; x++) {
            if (distances[x][y] != 0.0 && Double.valueOf(distances[x][y]).isInfinite()) {
                maxColumn = x;
            }
        }
        if (maxColumn == -1)
            throw new IllegalArgumentException();
        return nodes.get(maxColumn);
    }

    private boolean hasPred(Node node) {
        int y = getIndex(node);
        for (int x = 1; x < n; x++) {
            if (distances[x][y] != 0.0 && Double.valueOf(distances[x][y]).isInfinite()) {
                return true;
            }
        }
        return false;

    }

    @NotNull
    private Boolean hasWeights(@NotNull Graph graph) {
        boolean hasWeight = true;
        for (Edge edge : graph.getEachEdge()) {
            if (!edge.hasAttribute("weight"))
                hasWeight = false;
        }
        return hasWeight;
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

    /**
     * Output for the distance matrix
     */
    private void printMatrix() {
        for (Node node : nodes) { // print x nodes
            System.out.print("  \t" + node.getId() + " \t");
        }
        System.out.println();
        Iterator<Node> iterator = nodes.iterator();
        for (double[] distance1 : distances) { // print x nodes
            System.out.print(iterator.next() + " | ");
            for (int j = 0; j < distances.length; j++) { // print x nodes
                System.out.print((distance1[j] == 2.147483647E9 ? "Inf" : distance1[j]) + " \t");
            }
            System.out.println();
        }
    }

    // === MAIN ===

    public static void main(String[] args) throws Exception {
        // Graph aus den Folien
        // 02_GKA-Optimale Wege.pdf Folie 2 und 6
        Graph graph = new SingleGraph("graph");

        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addNode("E");

        graph.addEdge("AB", "A", "B", true).addAttribute("weight", 2.0);
        graph.addEdge("AC", "A", "C", true).addAttribute("weight", 3.0);
        graph.addEdge("BD", "B", "D", true).addAttribute("weight", 9.0);
        graph.addEdge("CB", "C", "B", true).addAttribute("weight", 7.0);
        graph.addEdge("CE", "C", "E", true).addAttribute("weight", 5.0);
        graph.addEdge("ED", "E", "D", true).addAttribute("weight", 1.0);

        FloydWarshall floyd = new FloydWarshall();
        floyd.setSourceAndTarget(graph.getNode("A"), graph.getNode("E"));
        floyd.init(graph);
        floyd.compute();
    }
}
