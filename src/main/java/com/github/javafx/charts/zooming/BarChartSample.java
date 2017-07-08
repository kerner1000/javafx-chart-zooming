/**
 * Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */

package com.github.javafx.charts.zooming;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BarChartSample extends Application {
    final static String austria = "Austria";
    final static String brazil = "Brazil";
    final static String france = "France";
    final static String italy = "Italy";
    final static String usa = "USA";

    public static void main(final String[] args) {
	launch(args);
    }

    @Override
    public void start(final Stage stage) {
	stage.setTitle("Bar Chart Sample");
	final CategoryAxis xAxis = new CategoryAxis();
	final NumberAxis yAxis = new NumberAxis();
	final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
	bc.setTitle("Country Summary");
	xAxis.setLabel("Country");
	yAxis.setLabel("Value");

	final XYChart.Series series1 = new XYChart.Series();
	series1.setName("2003");
	series1.getData().add(new XYChart.Data(austria, 25601.34));
	series1.getData().add(new XYChart.Data(brazil, 20148.82));
	series1.getData().add(new XYChart.Data(france, 10000));
	series1.getData().add(new XYChart.Data(italy, 35407.15));
	series1.getData().add(new XYChart.Data(usa, 12000));

	final XYChart.Series series2 = new XYChart.Series();
	series2.setName("2004");
	series2.getData().add(new XYChart.Data(austria, 57401.85));
	series2.getData().add(new XYChart.Data(brazil, 41941.19));
	series2.getData().add(new XYChart.Data(france, 45263.37));
	series2.getData().add(new XYChart.Data(italy, 117320.16));
	series2.getData().add(new XYChart.Data(usa, 14845.27));

	final XYChart.Series series3 = new XYChart.Series();
	series3.setName("2005");
	series3.getData().add(new XYChart.Data(austria, 45000.65));
	series3.getData().add(new XYChart.Data(brazil, 44835.76));
	series3.getData().add(new XYChart.Data(france, 18722.18));
	series3.getData().add(new XYChart.Data(italy, 17557.31));
	series3.getData().add(new XYChart.Data(usa, 92633.68));

	// DO NOT ADD DATA TO CHART
	// bc.getData().addAll(series1, series2, series3);
	final StackPane pane = new StackPane();
	pane.getChildren().add(bc);
	final Scene scene = new Scene(pane, 500, 400);
	new ZoomManager(pane, bc, series1, series2, series3);
	stage.setScene(scene);
	stage.show();
    }
}
