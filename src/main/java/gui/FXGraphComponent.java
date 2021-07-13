package gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import model.Graph;

import java.util.Map;
import java.util.Set;

public abstract class FXGraphComponent<T extends FXGraphComponent, U extends Graph.Component> {
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public void setSelected(boolean selected){
        this.selected.set(selected);
    }

    public boolean isSelected(){
        return selected.get();
    }

    public BooleanProperty selectedProperty(){
        return selected;
    }

    public abstract void addToGraph(FXGraph fxGraph, Graph graph, Map<U, T> retrieveMap,
                                    Pane graphGraphics, Set<String> usedNames);
    public abstract void removeFromGraph(FXGraph fxGraph, Graph graph, Map<U, T> retrieveMap,
                                         Pane graphGraphics, Set<String> usedNames);

}
