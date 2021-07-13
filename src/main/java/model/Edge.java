package model;

import helpers.Assertions;

public final class Edge implements Graph.Component {
    public static final int DEFAULT_USED_WEIGHT = -1;

    private final Vertex start, end;
    private int weight, usedWeight = DEFAULT_USED_WEIGHT;

    public static boolean isValidWeight(int weight){
        return weight >= 0;
    }

    public Edge(Vertex start, Vertex end, int weight){
        Assertions.assertThat(
                () -> new IllegalArgumentException("Weight must be non negative"),
                isValidWeight(weight)
        );
        this.start = start;
        this.end = end;
        setWeight(weight);
    }

    public Vertex getStart(){
        return start;
    }

    public Vertex getEnd(){
        return end;
    }

    public int getWeight(){
        return weight;
    }

    public int getUsedWeight(){
        return usedWeight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void setUsedWeight(int usedWeight){
        this.usedWeight = usedWeight;
    }

    public void increaseUsedWeight(int delta){
        usedWeight += delta;
    }

    @Override
    public String toString() {
        return String.format("( %s -> %s )", start, end);
    }

}
