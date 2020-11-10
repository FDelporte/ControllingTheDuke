package be.webtechie.controllingtheduke.view.snake;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Cube extends Box {

    public Cube(Color color) {
        super(1, 1, 1);
        setMaterial(new PhongMaterial(color));
    }

    public void set(Point3D p) {
        setTranslateX(p.getX());
        setTranslateY(p.getY());
        setTranslateZ(p.getZ());
    }

    public boolean isColliding(Cube c) {
        return getTranslateX() == c.getTranslateX()
                && getTranslateY() == c.getTranslateY()
                && getTranslateZ() == c.getTranslateZ();
    }
}
