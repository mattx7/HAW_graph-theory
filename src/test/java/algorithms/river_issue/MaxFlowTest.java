package algorithms.river_issue;

import algorithms.utility.GraphGenerator;
import algorithms.utility.IOGraph;
import algorithms.utility.Stopwatch;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static algorithms.utility.IOGraph.fromFile;
import static org.junit.Assert.assertEquals;

/**
 * Created by MattX7 on 25.11.2016.
 * 1.) Testen Sie f¨ur Graph4 dabei Ford und Fulkerson sowie Edmonds und Karp und geben Sie den maximalen Fluss, sowie die Laufzeit der beiden Algorithmen an.
 * 2.) Implementierung eines Netzwerks (gerichteter, gewichteter Graph mit Quelle und Senke) BigNet Gruppe TeamNr mit 50 Knoten und etwa 800 Kanten. Beschreiben Sie die Konstruktion von BigNet Gruppe TeamNr
 * und erl¨autern sie, inwiefern die Konstruktion ein Netzwerk liefert.
 * 3.) Lassen Sie bitte beide Algorithmen auf dem Netzwerk BigNet Gruppe TeamNr, den maximalen Fluss berechnen und vergleichen diese.
 * 4.) Benutzen Sie bitte unterschiedlich große Big-Net-Graphen mit 800 Knoten und 300.000 Kanten und mit 2.500 Knoten und 2.000.000 Kanten. Und lassen Sie bitte Ihre die Algorithmen jeweils 100 durchlaufen.
 */
public class MaxFlowTest {
    private Graph graph4;
    private final Logger LOG = Logger.getLogger(MaxFlowTest.class);

    @BeforeClass
    public static void closeLogger() throws Exception {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
                logger.setLevel(Level.OFF);
        }
        Logger.getLogger(MaxFlowTest.class).setLevel(Level.DEBUG);
    }

    @Before
    public void setUp() throws Exception {
        IOGraph.attributeKeyValue = "capacity";
        graph4 = fromFile("graph4", new File("src/main/resources/input/BspGraph/graph04.gka"));
        FordFulkerson.preview = false;
        EdmondsKarp.preview = true;

    }

    @Test(expected = IllegalArgumentException.class)
    public void graph4Test() throws Exception {
        Stopwatch stopwatch = new Stopwatch();
        FordFulkerson ford = new FordFulkerson();
        ford.init(graph4, graph4.getNode("q"), graph4.getNode("s"));
        ford.compute();
        stopwatch.stop();
        System.out.println(stopwatch.getActualTimeString());
        System.out.println(stopwatch.getEndTimeString());

        Stopwatch stopwatch2 = new Stopwatch();
        EdmondsKarp edmond = new EdmondsKarp();
        edmond.init(graph4, graph4.getNode("q"), graph4.getNode("s"));
        edmond.compute();
        stopwatch2.stop();
        System.out.println(stopwatch2.getActualTimeString());
        System.out.println(stopwatch2.getEndTimeString());

    }

    @Test
    public void smallNetwork() throws Exception {
        // 50 nodes and 814 Edges
        int nodes = 50;
        Graph big = GraphGenerator.createGritNetworkGraph(nodes);
        LOG.debug(String.format("BigNetwork with %d nodes and %d edges started", nodes, GraphGenerator.gritNetworkEdges(nodes)));
        FordFulkerson ford = new FordFulkerson();
        EdmondsKarp edmond = new EdmondsKarp();

        edmond.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));
        ford.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));

        edmond.compute();
        ford.compute();

        assertEquals(edmond.maxFlow, 2.0, 0.001);
        assertEquals(ford.maxFlow, 2.0, 0.001);
//        long endTime = edmond.stopwatch.getEndTime();
//        long actualTime = ford.stopwatch.getEndTime();
//        assertTrue(endTime < actualTime);
    }

    @Test
    public void bigNetwork() throws Exception {
        // 100x 800 Nodes and 171.513 Edges
        List<Long> fordRuntimes = new ArrayList<>();
        List<Long> edmondRuntimes = new ArrayList<>();
        int nodes = 800;
        Graph big = GraphGenerator.createGritNetworkGraph(nodes);
        LOG.debug(String.format("BigNetwork with %d nodes and %d edges started", nodes, GraphGenerator.gritNetworkEdges(nodes)));
        FordFulkerson ford = new FordFulkerson();
        EdmondsKarp edmond = new EdmondsKarp();

        int rounds = 10;
        for (int i = 0; i <= rounds; i++) {
            ford.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));
            edmond.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));
            ford.compute();
            fordRuntimes.add(ford.stopwatch.getEndTime());
            edmond.compute();
            edmondRuntimes.add(edmond.stopwatch.getEndTime());
        }

        LOG.debug(String.format("Min: %s/%s (FordFulkerson/EdmondsKarp)", Collections.min(fordRuntimes), Collections.min(edmondRuntimes)));
        LOG.debug(String.format("AVG: %s/%s (FordFulkerson/EdmondsKarp)", (fordRuntimes.stream().reduce(0l, (x, y) -> x + y)) / rounds, (edmondRuntimes.stream().reduce(0l, (x, y) -> x + y)) / rounds));
        LOG.debug(String.format("Max: %s/%s (FordFulkerson/EdmondsKarp)", Collections.max(fordRuntimes), Collections.max(edmondRuntimes)));
    }

    @Ignore
    @Test
    public void superBigNetwork() throws Exception {
        // 100x 2500 Nodes and 1.625.625 Edges
        List<Long> fordRuntimes = new ArrayList<>();
        List<Long> edmondRuntimes = new ArrayList<>();
        int nodes = 2500;
        Graph big = GraphGenerator.createGritNetworkGraph(nodes);
        LOG.debug(String.format("BigNetwork with %d nodes and %d edges started", nodes, GraphGenerator.gritNetworkEdges(nodes)));
        FordFulkerson ford = new FordFulkerson();
        EdmondsKarp edmond = new EdmondsKarp();

        ford.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));
        edmond.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));

        int rounds = 10;
        for (int i = 0; i <= rounds; i++) {
            ford.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));
            edmond.init(big, big.getNode(0), big.getNode(big.getNodeCount() - 1));
            ford.compute();
            fordRuntimes.add(ford.stopwatch.getEndTime());
            edmond.compute();
            edmondRuntimes.add(edmond.stopwatch.getEndTime());
        }

        LOG.debug(String.format("Min: %s/%s (FordFulkerson/EdmondsKarp)", Collections.min(fordRuntimes), Collections.min(edmondRuntimes)));
        LOG.debug(String.format("AVG: %s/%s (FordFulkerson/EdmondsKarp)", (fordRuntimes.stream().reduce(0l, (x, y) -> x + y)) / rounds, (edmondRuntimes.stream().reduce(0l, (x, y) -> x + y)) / rounds));
        LOG.debug(String.format("Max: %s/%s (FordFulkerson/EdmondsKarp)", Collections.max(fordRuntimes), Collections.max(edmondRuntimes)));

    }

}
