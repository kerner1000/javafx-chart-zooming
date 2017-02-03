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
package com.silicosciences.javafx.charts.zooming;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MiniTest extends Application {

    final static String austria = "Austria";
    final static String brazil = "Brazil";
    final static String france = "France";
    final static String italy = "Italy";
    final static String usa = "USA";

    public static void main(final String[] args) {
	launch(args);

    }

    @Override
    public void start(final Stage stage) throws Exception {

	final CategoryAxis xAxis = new CategoryAxis();
	final NumberAxis yAxis = new NumberAxis();
	final BarChart<String, Number> chart = new BarChart<String, Number>(xAxis, yAxis);
	final XYChart.Series series1 = new XYChart.Series();
	series1.setName("2003");
	series1.getData().add(new XYChart.Data(austria, 25601.34));
	series1.getData().add(new XYChart.Data(brazil, 20148.82));
	series1.getData().add(new XYChart.Data(france, 10000));
	series1.getData().add(new XYChart.Data(italy, 35407.15));
	series1.getData().add(new XYChart.Data(usa, 12000));
	chart.getData().add(series1);
	chart.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
		if (chart.getData().isEmpty()) {
		    chart.getData().add(series1);
		} else {
		    chart.getData().clear();
		}
	    }
	});
	final StackPane pane = new StackPane();
	pane.getChildren().add(chart);
	final Scene scene = new Scene(pane, 800, 600);
	stage.setScene(scene);
	stage.show();
    }

}
