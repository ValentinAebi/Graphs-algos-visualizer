package gui;

import helpers.Assertions;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Graph;
import model.Vertex;
import java.util.Map;
import java.util.Set;


public final class FXVertex extends FXGraphComponent<FXVertex, Vertex> {
    private static final int RADIUS = 10, DRAG_CIRCLE_RADIUS = 50;
    private static final Color NO_COLOR = null;
    public static final int DRAG_TIME_MARGIN = 500;

    private final Vertex vertex;
    private final Group graphics;
    private final Circle circle;

    private double mouseDeltaX = -100, mouseDeltaY = -100;
    private long lastDragTime = 0;

    public static FXVertex create(String name, double startX, double startY, FXGraph graph){
        return new FXVertex(new Vertex(name), startX, startY, graph);
    }

    private FXVertex(Vertex vertex, double startX, double startY, FXGraph graph){
        this.vertex = vertex;
        this.circle = new Circle(RADIUS);
        circle.setStrokeWidth(1);
        circle.strokeProperty().bind(Bindings.when(selectedProperty()).then(Color.BLACK).otherwise(NO_COLOR));
        Label labelName = new Label(vertex.getName());
        labelName.layoutXProperty().bind(circle.centerXProperty().add(RADIUS));
        labelName.layoutYProperty().bind(circle.centerYProperty().add(RADIUS));
        Circle dragCircle = new Circle(DRAG_CIRCLE_RADIUS);
        dragCircle.centerXProperty().bind(circle.centerXProperty());
        dragCircle.centerYProperty().bind(circle.centerYProperty());
        dragCircle.setFill(Color.TRANSPARENT);
        this.graphics = new Group(circle, labelName, dragCircle);
        dragCircle.setOnMouseDragged(event -> {
            if (mouseDeltaX < 0){
                mouseDeltaX = circle.getCenterX()-event.getX();
                mouseDeltaY = circle.getCenterY()-event.getY();
            }
            else {
                setPos(event.getX()+mouseDeltaX, event.getY()+mouseDeltaY);
            }
            lastDragTime = System.currentTimeMillis();
        });
        dragCircle.setOnMouseReleased(event -> {
            mouseDeltaX = mouseDeltaY = -100;
        });
        dragCircle.setOnMouseClicked(event -> {
            long currTime = System.currentTimeMillis();
            if (currTime-lastDragTime > DRAG_TIME_MARGIN)
                graph.select(this);
        });
        setPos(startX, startY);
    }

    public FXVertex setColor(Color color){
        circle.setFill(color);
        return this;
    }

    public FXVertex setPos(double x, double y){
        circle.setCenterX(x);
        circle.setCenterY(y);
        return this;
    }

    public FXVertex movePos(double dx, double dy){
        return setPos(circle.getCenterX()+dx, circle.getCenterY()+dy);
    }

    public ObservableDoubleValue xProperty(){
        return circle.centerXProperty();
    }

    public ObservableDoubleValue yProperty(){
        return circle.centerYProperty();
    }

    public Vertex getVertex(){
        return vertex;
    }

    public Group getGraphics(){
        return graphics;
    }

    @Override
    public void addToGraph(FXGraph fxGraph, Graph graph, Map<Vertex, FXVertex> retrieveMap,
                           Pane graphGraphics, Set<String> usedNames) {
        Assertions.assertThat(
                () -> new IllegalArgumentException("Name is already used"),
                fxGraph.isValidName(getVertex().getName())
        );
        setColor(fxGraph.getNextColor());

        graph.addVertex(getVertex());
        graphGraphics.getChildren().add(getGraphics());
        retrieveMap.put(getVertex(), this);
        usedNames.add(getVertex().getName());
        fxGraph.addVertexDescr(this);
    }

    @Override
    public void removeFromGraph(FXGraph fxGraph, Graph graph, Map<Vertex, FXVertex> retrieveMap,
                                Pane graphGraphics, Set<String> usedNames) {
        for (FXEdge edge: fxGraph.adjencyList(vertex).values())
            fxGraph.removeEdge(edge);
        for (FXEdge edge: fxGraph.precedencyList(vertex).values())
            fxGraph.removeEdge(edge);

        graph.removeVertex(getVertex());
        graphGraphics.getChildren().remove(getGraphics());
        retrieveMap.remove(getVertex());
        usedNames.remove(getVertex().getName());
        fxGraph.removeVertexDescr(this);
    }

}
