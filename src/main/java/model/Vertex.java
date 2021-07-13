package model;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Vertex implements Graph.Component {
    private final String name;
    private Parent parent = Parent.UNKNOWN;
    private final Map<String, Object> properties = new HashMap<>();

    public Vertex(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setProperty(String propertyName, Object value){
        properties.put(propertyName, value);
    }

    public Object getProperty(String propName){
        return properties.get(propName);
    }

    public void clearProperties(){
        properties.clear();
        parent = Parent.UNKNOWN;
    }

    public Map<String, Object> getProperties(){
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }

    @Override
    public Vertex clone(){
        return new Vertex(name);
    }

    public static abstract class Parent {

        public static final Parent NONE = new None(), UNKNOWN = new Unknown();

        private static final class Real extends Parent {
            private final Vertex vertex;

            private Real(Vertex vertex){
                this.vertex = vertex;
            }

            @Override
            public String toString(){
                return vertex.toString();
            }
        }

        private static final class None extends Parent {
            @Override
            public String toString(){
                return "[none];";
            }
        }

        private static final class Unknown extends Parent {
            @Override
            public String toString(){
                return "[?]";
            }
        }

    }


}
