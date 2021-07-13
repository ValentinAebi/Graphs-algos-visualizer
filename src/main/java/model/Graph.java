package model;

import helpers.Assertions;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public final class Graph implements Serializable {
    private final Map<Vertex, Map<Vertex, Edge>> edges = new HashMap<>();

    public Graph(){}

    public Graph(Set<Vertex> vertices, Set<Edge> edges){
        addVertices(vertices);
        addEdges(edges);
    }

    public Map<Vertex, Edge> adjencyList(Vertex vertex){
        return new HashMap<>(edges.get(vertex));
    }

    public Map<Vertex, Edge> precedencyList(Vertex vertex){
        Map<Vertex, Edge> res = new HashMap<>();
        for (Edge edge: getEdges().stream().filter(e -> e.getEnd() == vertex).collect(Collectors.toSet())){
            res.put(edge.getStart(), edge);
        }
        return new HashMap<>(res);
    }

    public int verticesCount(){
        return edges.size();
    }

    public int edgesCount(){
        return getEdges().size();
    }

    public Graph addVertex(Vertex vertex){
        edges.put(vertex, new HashMap<>());
        return this;
    }

    public Graph addEdge(Edge newEdge){
        Assertions.assertContains(
                () -> new IllegalArgumentException("Start and end of the edge must be registered as vertices"),
                getVertices(), newEdge.getStart(), newEdge.getEnd()
        );
        edges.get(newEdge.getStart()).put(newEdge.getEnd(), newEdge);
        return this;
    }

    public Graph addVertices(Collection<Vertex> newVertices){
        for (Vertex vertex : newVertices)
            addVertex(vertex);
        return this;
    }

    public Graph addEdges(Collection<Edge> newEdges){
        for (Edge edge: newEdges)
            addEdge(edge);
        return this;
    }

    public Graph removeVertex(Vertex vertex){
        Assertions.assertContains(
                () -> new IllegalArgumentException("Cannot remove an unregistered vertex"),
                getVertices(), vertex
        );
        edges.remove(vertex);
        for (Map<Vertex, Edge> map: edges.values())
            map.remove(vertex);
        return this;
    }

    public Graph removeEdge(Edge edge){
        try {
            edges.get(edge.getStart()).remove(edge.getEnd());
        } catch (Exception e){
            throw new IllegalArgumentException("Exception at edge removal: ", e);
        }
        return this;
    }

    public Set<Vertex> getVertices(){
        return edges.keySet();
    }

    public Set<Edge> getEdges(){
        Set<Edge> res = new HashSet<>();
        for (Map<Vertex, Edge> map: edges.values())
            for (Edge edge: map.values())
                res.add(edge);
        return res;
    }

    public Optional<Edge> getEdge(Vertex start, Vertex end){
        Map<Vertex, Edge> map = edges.get(start);
        if (Objects.isNull(map))
            return Optional.empty();
        Edge res = map.get(end);
        if (Objects.isNull(res))
            return Optional.empty();
        return Optional.of(res);
    }

    public void reverseEdge(Edge edge, int reversed){
        Assertions.assertContains(
                () -> new IllegalArgumentException("Cannot reverse an edge that is not in the graph"),
                getEdges(), edge
        );
        Assertions.assertThat(
                () -> new IllegalArgumentException("Cannot reverse an edge for more than its weight"),
                edge.getWeight() >= reversed
        );
        edge.setWeight(edge.getWeight()-reversed);
        if (edge.getWeight() == 0)
            removeEdge(edge);
        Optional<Edge> alreadyExisting = getEdge(edge.getEnd(), edge.getStart());
        if (!alreadyExisting.isPresent())
            addEdge(new Edge(edge.getEnd(), edge.getStart(), reversed));
        else {
            Edge reverseEdge = alreadyExisting.get();
            reverseEdge.setWeight(reverseEdge.getWeight()+reversed);
        }
    }

    public void clearVerticesProperties(){
        for (Vertex vertex: getVertices())
            vertex.clearProperties();
    }

    public Optional<Vertex> getVertexByName(String name){
        for (Vertex vertex : getVertices()) {
            if (vertex.getName().equals(name))
                return Optional.of(vertex);
        }
        return Optional.empty();
    }

    @Override
    public Graph clone(){
        Map<Vertex, Vertex> newVertices = new HashMap<>();
        for (Vertex vertex : getVertices()) {
            newVertices.put(vertex, vertex.clone());
        }
        Set<Edge> newEdges = getEdges().stream()
                .map(e -> new Edge(newVertices.get(e.getStart()), newVertices.get(e.getEnd()), e.getWeight()))
                .collect(Collectors.toSet());
        return new Graph(new HashSet<>(newVertices.values()), newEdges);
    }

    public interface Component {}

}
