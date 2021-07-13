package gui;

import helpers.Vector;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;

public final class Arrow extends Group {
    public static final double POINT_ANGLE_DEG = 30;
    private static final double MAX_POINT_PART = 1.0/3.0;
    private static final Color DEFAULT_COLOR = Color.BLACK;
    public static final int CURVE_ANGLE_ENABLED = 25;

    private final QuadCurve line = new QuadCurve();
    private final Line left = new Line(), right = new Line();
    private final ObjectProperty<Paint> stroke = new SimpleObjectProperty<>();

    public Arrow(ObservableDoubleValue startX, ObservableDoubleValue startY,
                 ObservableDoubleValue endX, ObservableDoubleValue endY, double maxPointLength, boolean curve){

        line.startXProperty().bind(startX);
        line.startYProperty().bind(startY);
        line.endXProperty().bind(endX);
        line.endYProperty().bind(endY);

        right.endXProperty().bind(endX);
        right.endYProperty().bind(endY);
        left.endXProperty().bind(endX);
        left.endYProperty().bind(endY);

        double curveAngle = curve? CURVE_ANGLE_ENABLED :0;

        ObjectProperty<Vector> pointModel = new SimpleObjectProperty<>(),
                startLeft = new SimpleObjectProperty<>(),
                startRight = new SimpleObjectProperty<>(),
                delta = new SimpleObjectProperty<>(),
                endVector = new SimpleObjectProperty<>();

        endVector.bind(Bindings.createObjectBinding(
                () -> new Vector(line.getEndX(), line.getEndY()), line.endXProperty(), line.endYProperty()
        ));

        delta.bind(Bindings.createObjectBinding(
                () -> new Vector(line.getStartX(), line.getStartY())
                        .minus(endVector.get()),
                line.startXProperty(), line.startYProperty(), endVector)
        );

        pointModel.bind(Bindings.createObjectBinding(() ->
            delta.get().withNorm(Math.min(maxPointLength, delta.get().getNorm()*MAX_POINT_PART))
                , delta));

        startLeft.bind(Bindings.createObjectBinding(
                () -> pointModel.get().rotateDeg(-POINT_ANGLE_DEG+curveAngle)
                        .plus(endVector.get()), pointModel)
        );
        startRight.bind(Bindings.createObjectBinding(
                () -> pointModel.get().rotateDeg(POINT_ANGLE_DEG+curveAngle)
                        .plus(endVector.get()), pointModel)
        );


        ObjectProperty<Vector> controlPoint = new SimpleObjectProperty<>();
        controlPoint.bind(Bindings.createObjectBinding(
                () -> delta.get().times(0.5).rotateDeg(curveAngle).plus(endVector.get()), delta
        ));
        line.controlXProperty().bind(Bindings.createObjectBinding(() -> controlPoint.get().getX(), controlPoint));
        line.controlYProperty().bind(Bindings.createObjectBinding(() -> controlPoint.get().getY(), controlPoint));

        right.startXProperty().bind(Bindings.createObjectBinding(() -> startRight.get().getX(), startRight));
        right.startYProperty().bind(Bindings.createObjectBinding(() -> startRight.get().getY(), startRight));
        left.startXProperty().bind(Bindings.createObjectBinding(() -> startLeft.get().getX(), startLeft));
        left.startYProperty().bind(Bindings.createObjectBinding(() -> startLeft.get().getY(), startLeft));

        line.strokeProperty().bind(stroke);
        left.strokeProperty().bind(stroke);
        right.strokeProperty().bind(stroke);

        line.setFill(null);
        setStroke(DEFAULT_COLOR);
        getChildren().addAll(line, right, left);
    }

    public void setStrokeWidth(double weight) {
        line.setStrokeWidth(weight);
        left.setStrokeWidth(weight);
        right.setStrokeWidth(weight);
    }

    public void setStroke(Paint paint){
        stroke.set(paint);
    }

    public Paint getStroke(){
        return stroke.get();
    }

    public ObjectProperty<Paint> strokeProperty() {
        return stroke;
    }

    public DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public DoubleProperty startYProperty(){
        return line.startYProperty();
    }

    public DoubleProperty endXProperty(){
        return line.endXProperty();
    }

    public DoubleProperty endYProperty(){
        return line.endYProperty();
    }

    public DoubleProperty controlXProperty(){
        return line.controlXProperty();
    }

    public DoubleProperty controlYProperty(){
        return line.controlYProperty();
    }

}
