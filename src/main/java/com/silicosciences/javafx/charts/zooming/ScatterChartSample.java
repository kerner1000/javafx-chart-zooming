package com.silicosciences.javafx.charts.zooming;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ScatterChartSample extends Application {

	public static void main(final String[] args) {
		launch(args);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(final Stage stage) {
		stage.setTitle("Scatter Chart Sample");
		final NumberAxis xAxis = new NumberAxis(0, 10, 1);
		final NumberAxis yAxis = new NumberAxis(-100, 500, 100);
		xAxis.setAutoRanging(true);
		xAxis.setForceZeroInRange(false);
		yAxis.setAutoRanging(true);
		yAxis.setForceZeroInRange(false);
		final ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
		xAxis.setLabel("Age (years)");
		yAxis.setLabel("Returns to date");
		sc.setTitle("Investment Overview");

		final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
		series1.setName("Equities");
		series1.getData().add(new XYChart.Data(4.2, 193.2));
		series1.getData().add(new XYChart.Data(2.8, 33.6));
		series1.getData().add(new XYChart.Data(6.2, 24.8));
		series1.getData().add(new XYChart.Data(1, 14));
		series1.getData().add(new XYChart.Data(1.2, 26.4));
		series1.getData().add(new XYChart.Data(4.4, 114.4));
		series1.getData().add(new XYChart.Data(8.5, 323));
		series1.getData().add(new XYChart.Data(6.9, 289.8));
		series1.getData().add(new XYChart.Data(9.9, 287.1));
		series1.getData().add(new XYChart.Data(0.9, -9));
		series1.getData().add(new XYChart.Data(3.2, 150.8));
		series1.getData().add(new XYChart.Data(4.8, 20.8));
		series1.getData().add(new XYChart.Data(7.3, -42.3));
		series1.getData().add(new XYChart.Data(1.8, 81.4));
		series1.getData().add(new XYChart.Data(7.3, 110.3));
		series1.getData().add(new XYChart.Data(2.7, 41.2));

		final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
		series2.setName("Mutual funds");
		series2.getData().add(new XYChart.Data(5.2, 229.2));
		series2.getData().add(new XYChart.Data(2.4, 37.6));
		series2.getData().add(new XYChart.Data(3.2, 49.8));
		series2.getData().add(new XYChart.Data(1.8, 134));
		series2.getData().add(new XYChart.Data(3.2, 236.2));
		series2.getData().add(new XYChart.Data(7.4, 114.1));
		series2.getData().add(new XYChart.Data(3.5, 323));
		series2.getData().add(new XYChart.Data(9.3, 29.9));
		series2.getData().add(new XYChart.Data(8.1, 287.4));

		// DO NOT ADD DATA TO CHART
		// sc.getData().addAll(series1, series2);

		final StackPane pane = new StackPane();
		pane.getChildren().add(sc);
		final Scene scene = new Scene(pane, 500, 400);
		new ZoomManager(pane, sc, series1, series2);
		stage.setScene(scene);
		stage.show();
	}
}
