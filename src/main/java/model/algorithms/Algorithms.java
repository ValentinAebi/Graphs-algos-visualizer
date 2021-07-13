package model.algorithms;

import helpers.Assertions;
import model.Edge;
import model.Graph;
import model.Vertex;

import java.util.*;

public final class Algorithms {

    private static final String COLOR = "color", GREY = "grey", WHITE = "white", BLACK = "black";
    public static final String FINISH_TIME = "f", DISCOVERY_TIME = "d";
    public static final String DISTANCE = "d";
    public static final String INFINITY = "∞";
    public static final String KEY = "key";
    public static final String PARENT = "π";
    public static final String FLOW = "flow";

    private Algorithms(){}

    public static void depthFirstSearch(Graph graph, Vertex startVertex){
        Assertions.assertContains(
                () -> new IllegalArgumentException("Cannot run DFS with a start vertex that is not in the graph"),
                graph.getVertices(), startVertex
        );
        graph.clearVerticesProperties();
        for (Vertex vertex : graph.getVertices())
            vertex.setProperty(COLOR, WHITE);
        dfsVisit(graph, startVertex, 0);
    }

    private static int dfsVisit(Graph graph, Vertex vertex, int time){
        ++time;
        vertex.setProperty(DISCOVERY_TIME, Integer.toString(time));
        vertex.setProperty(COLOR, GREY);
        for (Vertex nextVert: graph.adjencyList(vertex).keySet()){
            if (nextVert.getProperty(COLOR).equals(WHITE))
                time = dfsVisit(graph, nextVert, time);
        }
        vertex.setProperty(COLOR, BLACK);
        ++time;
        vertex.setProperty(FINISH_TIME, Integer.toString(time));
        return time;
    }

    public static void breathFirstSearch(Graph graph, Vertex startVertex){
        for (Vertex vertex: graph.getVertices())
            vertex.setProperty(DISTANCE, INFINITY);
        startVertex.setProperty(DISTANCE, 0);
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(startVertex);
        while (!queue.isEmpty()){
            Vertex currVertex = queue.remove();
            for (Vertex nextVertex: graph.adjencyList(currVertex).keySet()){
                if (nextVertex.getProperty(DISTANCE).equals(INFINITY)){
                    nextVertex.setProperty(DISTANCE, (int)currVertex.getProperty(DISTANCE)+1);
                    queue.add(nextVertex);
                }
            }
        }
    }

    public static Set<Edge> kruskal(Graph graph){
        Set<Edge> spanningEdges = new HashSet<>();
        DisjointSetRepresentation<Vertex> setRepr = new DisjointSetRepresentation<>();
        for (Vertex v: graph.getVertices())
            setRepr.makeSet(v);
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges, Comparator.comparing(Edge::getWeight));
        for (Edge edge: edges){
            Vertex start = edge.getStart(), end = edge.getEnd();
            if (setRepr.findSet(start) != setRepr.findSet(end)){
                spanningEdges.add(edge);
                setRepr.union(start, end);
            }
        }
        return new HashSet<>(spanningEdges);
    }

    public static void fordFulkerson(Graph originalGraph, Vertex sourceArg, Vertex sinkArg){
        Graph graph = originalGraph.clone();
        for (Edge edge: originalGraph.getEdges())
            edge.setUsedWeight(0);
        int flow = 0;
        int newFlow;
        do {
            newFlow = ffAugment(graph, graph.getVertexByName(sourceArg.getName()).get(),
                    graph.getVertexByName(sinkArg.getName()).get(), originalGraph);
            flow += newFlow;
        } while (newFlow != 0);
        sourceArg.setProperty("Flow to "+sinkArg.getName(), flow);
    }

    private static int ffAugment(Graph graph, Vertex source, Vertex sink, Graph originalGraph){
        bfsForFF(graph, source, sink);
        int flow = (int)sink.getProperty(FLOW);
        Vertex currVert = sink;
        while (currVert.getProperty(PARENT) != null){
            Vertex prevVert = (Vertex) currVert.getProperty(PARENT);
            graph.reverseEdge(graph.getEdge(prevVert, currVert).get(), flow);
            originalGraph.getEdge(
                    originalGraph.getVertexByName(prevVert.getName()).get(),
                    originalGraph.getVertexByName(currVert.getName()).get()
            ).get().increaseUsedWeight(flow);
            currVert = prevVert;
        }
        return flow;
    }

    private static void bfsForFF(Graph graph, Vertex source, Vertex sink){
        for (Vertex vertex: graph.getVertices()) {
            vertex.setProperty(PARENT, null);
        }
        sink.setProperty(FLOW, 0);
        Queue<Vertex> vertices = new LinkedList<>();
        vertices.add(source);
        source.setProperty(FLOW, Integer.MAX_VALUE);
        while (!vertices.isEmpty()){
            Vertex currVert = vertices.poll();
            for (Vertex nextVert: graph.adjencyList(currVert).keySet()){
                if (nextVert.getProperty(PARENT) == null && nextVert != source) {
                    nextVert.setProperty(PARENT, currVert);
                    nextVert.setProperty(FLOW, Math.min(graph.getEdge(currVert, nextVert).get().getWeight(),
                            (int) currVert.getProperty(FLOW)));
                    if (nextVert == sink)
                        return;
                    vertices.add(nextVert);
                }
            }
        }

    }

    public static void bellmanFord(Graph graph, Vertex source){
        initSingleSource(graph, source);
        for (int i = 1; i < graph.verticesCount(); ++i){
            for (Edge edge: graph.getEdges())
                relax(edge);
        }
    }

    private static void initSingleSource(Graph graph, Vertex source){
        for (Vertex vertex : graph.getVertices()) {
            vertex.setProperty(DISTANCE, INFINITY);
            vertex.setProperty(PARENT, null);
        }
        source.setProperty(DISTANCE, 0);
    }

    private static void relax(Edge edge){
        Vertex start = edge.getStart(), end = edge.getEnd();
        int weight = edge.getWeight();
        Object startDistProp = start.getProperty(DISTANCE), endDistProp = end.getProperty(DISTANCE);
        int possDist = startDistProp instanceof Integer ? (int) startDistProp + weight : Integer.MAX_VALUE;
        int currDist = endDistProp instanceof Integer ? (int) endDistProp:Integer.MAX_VALUE;
        if (currDist <= possDist)
            return;
        end.setProperty(DISTANCE, possDist);
        end.setProperty(PARENT, start);
    }

}
