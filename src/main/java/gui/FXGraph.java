package gui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.algorithms.Algorithms;
import model.Edge;
import model.Graph;
import model.Vertex;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.*;

public final class FXGraph {
    private static final List<Color> DEFAULT_COLORS =
            Arrays.asList(BLUE, RED, GREEN, YELLOW, PURPLE, BROWN, SKYBLUE, ORANGE, LIGHTGREEN, DARKBLUE, PINK);

    private final Graph graph;
    private Graph savedGraph = null;
    private final Pane graphics = new Pane();
    private final Pane descriptions = new VBox();
    private final Map<Vertex, VertexDescriptionPane> descrRetrieval = new HashMap<>();
    private final Map<Vertex, FXVertex> vertices = new HashMap<>();
    private final Map<Edge, FXEdge> edges = new HashMap<>();

    private final Set<String> usedNames = new HashSet<>();
    private int colorIndex = 0;

    private final Selection selection = new Selection();

    public FXGraph(Graph graph){
        this.graph = graph;
        descriptions.setMinWidth(150);
    }

    public Map<FXVertex, FXEdge> adjencyList(Vertex vertex){
        Map<FXVertex, FXEdge> res = new HashMap<>();
        for (Map.Entry<Vertex, Edge> entry: graph.adjencyList(vertex).entrySet()){
            res.put(vertices.get(entry.getKey()), edges.get(entry.getValue()));
        }
        return new HashMap<>(res);
    }

    public Map<FXVertex, FXEdge> precedencyList(Vertex vertex){
        Map<FXVertex, FXEdge> res = new HashMap<>();
        for (Map.Entry<Vertex, Edge> entry: graph.precedencyList(vertex).entrySet()){
            res.put(vertices.get(entry.getKey()), edges.get(entry.getValue()));
        }
        return new HashMap<>(res);
    }

    public Set<FXEdge> getEdges(){
        return graph.getEdges().stream().map(edges::get).collect(Collectors.toSet());
    }

    public boolean isValidName(String name){
        return !name.isEmpty()
                && !usedNames.contains(name)
                && Character.isAlphabetic(name.charAt(0))
                && checkAlphabeticOrDigit(name);
    }

    private boolean checkAlphabeticOrDigit(String str){
        for (int i = 0; i < str.length(); ++i){
            char car = str.charAt(i);
            if (!Character.isAlphabetic(car) || Character.isDigit(car))
                return false;
        }
        return true;
    }

    public FXGraph addVertex(FXVertex vertex){
        vertex.addToGraph(this, graph, vertices, graphics, usedNames);
        return this;
    }

    public FXGraph addEdge(FXEdge edge){
        edge.addToGraph(this, graph, edges, graphics, usedNames);
        return this;
    }

    public FXGraph removeVertex(FXVertex vertex){
        vertex.removeFromGraph(this, graph, vertices, graphics, usedNames);
        return this;
    }

    public FXGraph removeEdge(FXEdge edge){
        edge.removeFromGraph(this, graph, edges, graphics, usedNames);
        return this;
    }

    public Pane getGraphics(){
        return graphics;
    }

    public Pane getDescriptionPane(){
        return descriptions;
    }

    public Color getNextColor(){
        colorIndex = (colorIndex+1) % DEFAULT_COLORS.size();
        return DEFAULT_COLORS.get(colorIndex);
    }

    public void clearSelection(){
        selection.clear();
    }

    public void select(FXVertex vertex){
        selection.selectVertex(vertex);
    }

    public void select(FXEdge edge){
        selection.selectEdge(edge);
    }

    public void linkSelectedVertices(int weight){
        if (selection.selectedVerticesCount() == 2){
            FXVertex start = selection.selectedVertices().get(0),
                    end = selection.selectedVertices().get(1);
            addEdge(FXEdge.create(start, end, weight, this,
                    getEdge(end.getVertex(), start.getVertex()).isPresent()));
        }
    }

    public Optional<FXEdge> getEdge(Vertex start, Vertex end){
        Optional<Edge> edge = graph.getEdge(start, end);
        return edge.map(edges::get);
    }

    public void deleteSelected(){
        for (FXVertex vertex: selection.selectedVertices())
            removeVertex(vertex);
        for (FXEdge edge: selection.selectedEdges())
            removeEdge(edge);
        selection.clear();
    }

    public void addVertexDescr(FXVertex vertex){
        VertexDescriptionPane pane = new VertexDescriptionPane(vertex);
        descrRetrieval.put(pane.getVertex().getVertex(), pane);
        List<VertexDescriptionPane> panes = new ArrayList<>(descrRetrieval.values());
        Collections.sort(panes, Comparator.comparing(VertexDescriptionPane::getVertexName));
        descriptions.getChildren().clear();
        descriptions.getChildren().addAll(panes);
    }

    public void removeVertexDescr(FXVertex vertex){
        descriptions.getChildren().remove(descrRetrieval.remove(vertex.getVertex()));
    }

    public void updateDescriptions(){
        for (VertexDescriptionPane pane: descrRetrieval.values())
            pane.update();
        for (FXEdge edge: getEdges())
            edge.updateWeight();
    }

    public void clearProperties(){
        graph.clearVerticesProperties();
        for (FXEdge edge : getEdges()) {
            edge.setHighlighted(false);
            edge.getEdge().setUsedWeight(Edge.DEFAULT_USED_WEIGHT);
        }
        updateDescriptions();
    }

    public boolean runDFS(){
        return runGraphSourceAlgo(Algorithms::depthFirstSearch);
    }

    public boolean runBFS(){
        return runGraphSourceAlgo(Algorithms::breathFirstSearch);
    }

    public boolean runBellmanFord(){
        return runGraphSourceAlgo(Algorithms::bellmanFord);
    }

    private boolean runGraphSourceAlgo(BiConsumer<Graph, Vertex> algo){
        if (!(selection.selectedVerticesCount() == 1 && selection.selectedEdgesCount() == 0))
            return false;
        clearProperties();
        algo.accept(graph, selection.selectedVertices().get(0).getVertex());
        updateDescriptions();
        clearSelection();
        return true;
    }

    public void runKruskal(){
        clearProperties();
        for (FXEdge edge: Algorithms.kruskal(graph)
                .stream()
                .map(edges::get)
                .collect(Collectors.toSet()))
            edge.setHighlighted(true);
        updateDescriptions();
        clearSelection();
    }

    public boolean runFordFulkerson(){
        if (selection.selectedVerticesCount() != 2)
            return false;
        clearProperties();
        Algorithms.fordFulkerson(graph, selection.selectedVertices().get(0).getVertex(),
                selection.selectedVertices().get(1).getVertex());
        updateDescriptions();
        clearSelection();
        return true;
    }

    private static final class Selection {
        private static final int MAX_SELECTED_NB = 2;

        private final List<FXGraphComponent> vertices = new LinkedList<>();
        private final List<FXGraphComponent> edges = new LinkedList<>();
        private final List<FXGraphComponent> components = new LinkedList<>();

        List<FXVertex> selectedVertices(){
            return new ArrayList<>(vertices.stream().map(v -> (FXVertex)v).collect(Collectors.toList()));
        }

        List<FXEdge> selectedEdges(){
            return new ArrayList<>(edges.stream().map(v -> (FXEdge)v).collect(Collectors.toList()));
        }

        void clear(){
            for (FXGraphComponent comp: components)
                comp.setSelected(false);
            vertices.clear();
            edges.clear();
            components.clear();
        }

        int selectedVerticesCount(){
            return vertices.size();
        }

        int selectedEdgesCount(){
            return edges.size();
        }

        void selectVertex(FXVertex vertex){
            if (vertices.contains(vertex)){
                vertices.remove(vertex);
                components.remove(vertex);
                vertex.setSelected(false);
            }
            else {
                vertices.add(vertex);
                components.add(vertex);
                removeHeadIFNecessary();
                vertex.setSelected(true);
            }
        }

        void selectEdge(FXEdge edge){
            if (edges.contains(edge)){
                edges.remove(edge);
                components.remove(edge);
                edge.setSelected(false);
            }
            else {
                edges.add(edge);
                components.add(edge);
                removeHeadIFNecessary();
                edge.setSelected(true);
            }
        }

        private void removeHeadIFNecessary(){
            if (components.size() > MAX_SELECTED_NB){
                FXGraphComponent removed = components.remove(0);
                vertices.remove(removed);
                edges.remove(removed);
            }
        }

    }

}
