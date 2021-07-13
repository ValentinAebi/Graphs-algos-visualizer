package gui;

import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.Edge;
import model.Graph;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class FXEdge extends FXGraphComponent<FXEdge, Edge> {
    private static final double WEIGHT = 5, HIGHLIGHT_WEIGHT = 15;
    private static final int MAX_POINT_LENGTH = 25;
    private static final double LABEL_MARGIN = 10;
    public static final Color HIGHLIGHT_COLOR = Color.LIGHTGRAY;

    private final Edge edge;
    private final Group graphics;
    private final Arrow arrow, highlightArrow;
    private final Label weightLabel;

    public static FXEdge create(FXVertex start, FXVertex end, int weight, FXGraph graph, boolean curve){
        return new FXEdge(new Edge(start.getVertex(), end.getVertex(), weight), start, end, graph, curve);
    }

    private FXEdge(Edge edge, FXVertex start, FXVertex end, FXGraph graph, boolean curve){
        this.edge = edge;
        this.arrow = new Arrow(start.xProperty(), start.yProperty(), end.xProperty(),
                end.yProperty(), MAX_POINT_LENGTH, curve);
        arrow.setStrokeWidth(WEIGHT);
        arrow.strokeProperty().bind(Bindings.when(selectedProperty()).then(Color.BLACK).otherwise(Color.GRAY));
        arrow.setOnMouseClicked(event -> graph.select(this));
        this.highlightArrow = new Arrow(start.xProperty(), start.yProperty(), end.xProperty(),
                end.yProperty(), MAX_POINT_LENGTH, curve);
        highlightArrow.setStrokeWidth(HIGHLIGHT_WEIGHT);
        highlightArrow.setStroke(HIGHLIGHT_COLOR);
        highlightArrow.setVisible(false);
        this.weightLabel = new Label(Integer.toString(edge.getWeight()));
        weightLabel.layoutXProperty().bind(
                arrow.controlXProperty().subtract(LABEL_MARGIN)
        );
        weightLabel.layoutYProperty().bind(
                arrow.controlYProperty().subtract(LABEL_MARGIN)
        );
        weightLabel.setStyle("-fx-background-color: white");
        this.graphics = new Group(highlightArrow, arrow, weightLabel);
    }

    public Edge getEdge(){
        return edge;
    }

    public Group getGraphics(){
        return graphics;
    }

    public void setHighlighted(boolean highlighted){
        highlightArrow.setVisible(highlighted);
    }

    @Override
    public void addToGraph(FXGraph fxGraph, Graph graph, Map<Edge, FXEdge> retrieveMap,
                           Pane graphGraphics, Set<String> usedNames) {
        Optional<FXEdge> previous = fxGraph.getEdge(getEdge().getStart(), getEdge().getEnd());
        previous.ifPresent(fxEdge -> fxEdge.removeFromGraph(fxGraph, graph, retrieveMap, graphGraphics, usedNames));
        graph.addEdge(getEdge());
        graphGraphics.getChildren().add(0, getGraphics());
        retrieveMap.put(getEdge(), this);
    }

    @Override
    public void removeFromGraph(FXGraph fxGraph, Graph graph, Map<Edge, FXEdge> retrieveMap,
                                Pane graphGraphics, Set<String> usedNames) {
        graphGraphics.getChildren().remove(getGraphics());
        graph.removeEdge(getEdge());
        retrieveMap.remove(getEdge());
    }

    public void updateWeight(){
        weightLabel.setText(edge.getUsedWeight() == Edge.DEFAULT_USED_WEIGHT ?
                Integer.toString(edge.getWeight())
                :String.format("%d/%d", edge.getUsedWeight(), edge.getWeight()));
    }

}
