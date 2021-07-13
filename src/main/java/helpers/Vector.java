package helpers;

public final class Vector {
    public static final Vector ZERO = new Vector(0, 0);

    private final double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector opposite(){
        return new Vector(-x, -y);
    }

    public Vector plus(Vector that){
        return new Vector(this.x+that.x, this.y+that.y);
    }

    public Vector minus(Vector that){
        return this.plus(that.opposite());
    }

    public Vector times(double scalar){
        return new Vector(scalar*x, scalar*y);
    }

    public double getNorm(){
        return Math.hypot(x, y);
    }

    public Vector withNorm(double norm){
        return this.times(norm/getNorm());
    }

    public Vector rotateRad(double angleRad){
        double sin = Math.sin(angleRad), cos = Math.cos(angleRad);
        return new Vector(x*cos-y*sin, x*sin+y*cos);
    }

    public Vector rotateDeg(double angleDeg){
        return rotateRad(Math.toRadians(angleDeg));
    }

}
