package gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.util.Map;

public final class VertexDescriptionPane extends BorderPane {
    private final FXVertex vertex;
    private final ListView<String> table = new ListView<>();

    public VertexDescriptionPane(FXVertex vertex){
        this.vertex = vertex;
        Label titleLabel = new Label(vertex.getVertex().getName());
        setTop(titleLabel);
        setCenter(table);
        update();
        table.setFixedCellSize(25);
        setMaxWidth(200);
        setMaxHeight(200);
        setMinHeight(50);
        setPrefHeight(100);
    }

    public void update(){
        table.getItems().clear();
        for (Map.Entry<String, Object> entry: vertex.getVertex().getProperties().entrySet())
            table.getItems().add(String.format("%s: %s", entry.getKey(), entry.getValue()));
    }

    public FXVertex getVertex(){
        return vertex;
    }

    public String getVertexName(){
        return getVertex().getVertex().getName();
    }

}
