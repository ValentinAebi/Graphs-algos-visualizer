package gui;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Graph;


public final class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXGraph graph = new FXGraph(new Graph());
        VBox mainPane = new VBox(createCommandBar(graph, primaryStage), graph.getGraphics());
        mainPane.setMinSize(1200, 600);
        ScrollPane descrPane = new ScrollPane(graph.getDescriptionPane());
        descrPane.setStyle("-fx-background-color: lightblue");
        descrPane.setMinWidth(100);
        descrPane.setMaxWidth(200);
        SplitPane rootPane = new SplitPane(mainPane, descrPane);
        primaryStage.setScene(new Scene(rootPane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private HBox createCommandBar(FXGraph graph, Stage primaryStage){
        Button addVerticeButton = new Button("Create");
        TextField verticeNameField = new TextField();
        addVerticeButton.setOnAction(event -> {
            String name = verticeNameField.getText();
            if (graph.isValidName(name)){
                verticeNameField.setStyle("-fx-border-color: lightgrey");
                graph.addVertex(FXVertex.create(name, 10, 10, graph));
                verticeNameField.setText("");
            }
            else {
                verticeNameField.setStyle("-fx-border-color: red");
            }
        });
        verticeNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                addVerticeButton.fire();
            event.consume();
        });
        Spinner<Integer> edgeWeightSpinner = new Spinner<>(0, Integer.MAX_VALUE, 1);
        edgeWeightSpinner.setEditable(true);
        Button addEdgeButton = new Button("Create");
        addEdgeButton.setOnAction(event -> {
            graph.linkSelectedVertices(edgeWeightSpinner.getValue());
            graph.clearSelection();
        });
        Button clearPropertiesButton = new Button("Clear properties");
        clearPropertiesButton.setOnAction(event -> graph.clearProperties());
        Button deleteButton = new Button("Delete selected");
        deleteButton.setOnAction(event -> graph.deleteSelected());
        Label verticeLabel = new Label(" Vertex"), edgeLabel = new Label("Edge");
        verticeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
        edgeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
        Button dfsButton = new Button("DFS");
        dfsButton.setOnAction(event -> {
            if (!graph.runDFS()){
                new Alert(Alert.AlertType.INFORMATION,
                        "Exactly one vertex must be selected when running DFS").show();
            }
        });
        Button bfsButton = new Button("BFS");
        bfsButton.setOnAction(event -> {
            if (!graph.runBFS()){
                new Alert(Alert.AlertType.INFORMATION,
                        "Exactly one vertex must be selected when running BFS").show();
            }
        });
        Button kruskalButton = new Button("Kruskal");
        kruskalButton.setOnAction(event -> graph.runKruskal());
        Button fordFulkersonButton = new Button("FF");
        fordFulkersonButton.setOnAction(event -> {
            if (!graph.runFordFulkerson()){
                new Alert(Alert.AlertType.INFORMATION,
                        "Exactly two vertices must be selected when running FF").show();
            }
        });
        Button bellmanFordButton = new Button("BF");
        bellmanFordButton.setOnAction(event -> {
            if (!graph.runBellmanFord()){
                new Alert(Alert.AlertType.INFORMATION,
                        "Exactly one vertex must be selected when running BF").show();
            }
        });
        HBox res = new HBox(verticeLabel, new Label("Name:"), verticeNameField, addVerticeButton,
                new Separator(Orientation.VERTICAL), edgeLabel, new Label("Weight:"),
                edgeWeightSpinner, addEdgeButton, deleteButton, clearPropertiesButton,
                dfsButton, bfsButton, kruskalButton, fordFulkersonButton, bellmanFordButton);
        res.setStyle("-fx-alignment: baseline-left; -fx-spacing: 10");
        return res;
    }

}
