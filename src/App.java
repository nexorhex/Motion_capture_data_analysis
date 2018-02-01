import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class App extends Application {

    Scene sceneMain;
    Button btnCsv;
    Button btnPlot;
    Button btnAnimation;
    Button btnCom;
    Button btnExport;
    ComboBox plotBox;
    ComboBox animationBox;

    LineChart<Number, Number> figure;
    ArrayList<String> valuesToSave = new ArrayList<>();

    CSVManager reader;
    float matrix[][];
    NumberAxis x = new NumberAxis();
    NumberAxis y = new NumberAxis();

    @Override
    public void start(Stage primaryStage) throws Exception {
        figure = new LineChart<Number, Number>(x, y);
        figure.setMinSize(500, 680);

        VBox layoutMain = new VBox(displayButtons(primaryStage), displayCombo(primaryStage));
        layoutMain.getChildren().add(figure);

        sceneMain = new Scene(layoutMain, 1300, 800);

        primaryStage.setScene(sceneMain);
        primaryStage.show();
        primaryStage.setTitle("Projekt Michal Mazur");

        btnAnimation.setDisable(true);
        btnPlot.setDisable(true);
        btnCom.setDisable(true);
        btnExport.setDisable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private HBox displayButtons(Stage stage) {
        btnCsv = new Button("Read CSV");
        btnCsv.setPrefWidth(100);
        btnCsv.setOnAction(e -> {
            FileChooser fileSelector = new FileChooser();
            fileSelector.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            String d = "C:\\Users\\Student229665\\Desktop\\ZPO"; //do zmiany, jesli bedzie Pani wlaczac program na swoim komputerze
            File userDirectory = new File(d);
            fileSelector.setInitialDirectory(userDirectory);

            try {
                File file = fileSelector.showOpenDialog(stage);
                reader = new CSVManager(file);
                btnPlot.setDisable(false);
                btnCom.setDisable(false);
                btnExport.setDisable(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnPlot = new Button("Plot");
        btnPlot.setPrefWidth(100);
        btnPlot.setOnAction(e -> {
            matrix = new float[reader.getData().size()][4];
            matrix = reader.parseData((String) plotBox.getValue());
            XYChart.Series seriesX;
            seriesX = createChart(matrix, (String) animationBox.getValue());
            figure.setLegendVisible(false);
            figure.setCreateSymbols(false);
            figure.getData().clear();
            figure.getData().add(seriesX);
            btnAnimation.setDisable(false);
        });

        btnAnimation = new Button("Animation");
        btnAnimation.setPrefWidth(100);
        btnAnimation.setOnAction(e -> {
            Animation animation = new Animation(matrix, 250f, 250f);
            animation.start();
            btnAnimation.setDisable(true);
        });

        btnCom = new Button("Plot COM");
        btnCom.setPrefWidth(100);
        btnCom.setOnAction(e -> {
            btnAnimation.setDisable(false);
            String valueBox = (String) animationBox.getValue();
            int coord = 0;
            if (valueBox == "x")
                coord = 1;
            else if (valueBox == "y")
                coord = 2;
            else if (valueBox == "z")
                coord = 3;

            XYChart.Series seriesX = new XYChart.Series();

            float[][] matrixLIAS = new float[reader.getData().size()][4];
            float[][] matrixRIAS = new float[reader.getData().size()][4];
            float[][] matrixLIPS = new float[reader.getData().size()][4];
            float[][] matrixRIPS = new float[reader.getData().size()][4];
            matrixLIAS = reader.parseData("LIAS");
            matrixRIAS = reader.parseData("RIAS");
            matrixLIPS = reader.parseData("LIPS");
            matrixRIPS = reader.parseData("RIPS");
            float COMval = 0;

            for (int i = 0; i < matrixLIAS.length; i++) {
                COMval = (matrixLIAS[i][coord] + matrixRIAS[i][coord] + matrixLIPS[i][coord] + matrixRIPS[i][coord]) / 4;
                seriesX.getData().add(new XYChart.Data(matrixLIAS[i][0], COMval)); //matrixLIAS[i][0] odpowiada za czas - obojetnie z jakiej macierzy go wezmiemy
            }

            figure.setLegendVisible(false);
            figure.setCreateSymbols(false);
            figure.getData().clear();
            figure.getData().add(seriesX);

        });

        btnExport = new Button("Export");
        btnExport.setPrefWidth(100);
        btnExport.setOnAction(e -> {
            FileChooser fileSelector = new FileChooser();
            fileSelector.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            String d = "C:\\Users\\Student229665\\Desktop\\ZPO"; //do zmiany, jesli bedzie Pani wlaczac program na swoim komputerze
            File userDirectory = new File(d);
            fileSelector.setInitialDirectory(userDirectory);

            File file = fileSelector.showSaveDialog(stage);
            valuesToSave.add(plotBox.getPromptText());

            try {
                reader.Save(valuesToSave, file.getPath());
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        HBox hBox = new HBox(btnCsv, btnPlot, btnAnimation, btnCom, btnExport);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(30, 5, 5, 5));
        hBox.setSpacing(5);

        return hBox;
    }

    private HBox displayCombo(Stage stage) {
        plotBox = new ComboBox();
        plotBox.setPrefWidth(100);
        plotBox.getItems().addAll("LIAS", "RIAS", "LIPS", "RIPS", "LFAL", "RFAL");
        plotBox.getSelectionModel().selectFirst();

        animationBox = new ComboBox();
        animationBox.setPrefWidth(100);
        animationBox.getItems().addAll("x", "y", "z");
        animationBox.getSelectionModel().selectFirst();

        HBox hBox = new HBox(plotBox, animationBox);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5, 110, 5, 5));
        hBox.setSpacing(5);

        return hBox;
    }

    public XYChart.Series createChart(float matrix[][], String a) {

        String x = (String) animationBox.getValue();
        int coord = 0;
        if (x == "x")
            coord = 1;
        else if (x == "y")
            coord = 2;
        else if (x == "z")
            coord = 3;

        XYChart.Series chart = new XYChart.Series();

        for (int i = 0; i < matrix.length; i++) {
            chart.getData().add(new XYChart.Data(matrix[i][0], matrix[i][coord]));
        }

        return chart;
    }
}
