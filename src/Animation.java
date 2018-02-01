import java.util.ArrayList;

import javafx.animation.PathTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Animation {
    private float xc = 250f;
    private float yc = 250f;
    private float[][] matrix;
    private MoveTo startPos;

    public Animation(float[][] matrix, float xc, float yc) {
        super();
        this.xc = xc;
        this.yc = yc;
        this.matrix = matrix;
    }

    public void start() {
        Stage stage = new Stage();

        Circle circle = new Circle();

        circle.setCenterX(xc);
        circle.setCenterY(yc);

        circle.setRadius(20.0);

        circle.setFill(Color.RED);

        circle.setStrokeWidth(10);

        ArrayList<LineTo> posOut = calculatePositions();

        Path path = new Path();
        path.getElements().add(startPos);

        for (LineTo pos : posOut) {
            path.getElements().add(pos);
        }

        PathTransition pathTransition = new PathTransition();

        pathTransition.setDuration(Duration.seconds(100));

        pathTransition.setNode(circle);

        pathTransition.setPath(path);

        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        pathTransition.setCycleCount(1);

        pathTransition.setAutoReverse(false);

        pathTransition.play();

        Group root = new Group(circle);

        Scene scene = new Scene(root, 500, 500);

        stage.setTitle("Animation");

        stage.setScene(scene);

        stage.show();
    }

    public ArrayList<LineTo> calculatePositions() {
        ArrayList<LineTo> posOut = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            float x = (matrix[i][3] - findMinValue(3)) / findMaxValue(3) * xc + xc - 50;
            float y = (matrix[i][2] - findMinValue(2)) / findMaxValue(2) * -yc + yc;

            if (i == 0) {
                startPos = new MoveTo(x, y);
            } else {
                posOut.add(new LineTo(x, y));
            }
        }
        return posOut;
    }

    private float findMaxValue(int column) {
        float max = matrix[0][column];
        for (int i = 0; i < matrix.length; i++) {
            if (max < Math.abs(matrix[i][column])) {
                max = Math.abs(matrix[i][column]);
            }
        }
        return max;
    }

    private float findMinValue(int column) {
        float min = matrix[0][column];
        for (int i = 0; i < matrix.length; i++) {
            if (min > Math.abs(matrix[i][column])) {
                min = Math.abs(matrix[i][column]);
            }
        }
        return min;
    }
}