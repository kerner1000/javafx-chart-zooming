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

import java.util.Collection;
import java.util.Iterator;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BarChartTest<X, Y> extends Application {

    final static String austria = "Austria";
    final static String brazil = "Brazil";
    final static String france = "France";
    final static String italy = "Italy";
    final static String usa = "USA";

    public static <X, Y> XYChart.Series<X, Y> deepCopySeries(final XYChart.Series<X, Y> series) {
	final XYChart.Series<X, Y> result = new XYChart.Series<X, Y>();
	result.setName(series.getName());
	result.setData(deepCopySeriesData(series.getData()));
	return result;
    }

    public static <X, Y> ObservableList<XYChart.Data<X, Y>> deepCopySeriesData(
	    final Collection<? extends XYChart.Data<X, Y>> data) {
	final ObservableList<XYChart.Data<X, Y>> result = FXCollections.observableArrayList();
	for (final Data<X, Y> i : data) {
	    result.add(new Data<X, Y>(i.getXValue(), i.getYValue()));
	}
	return result;
    }

    private static Number getNumber(final Axis<?> axis, final double cooridnate) {
	final Object object = axis.getValueForDisplay(cooridnate);
	if (object != null) {
	    if (object instanceof Number) {
		final Number number = (Number) object;
		return number;
	    }
	}
	return null;
    }

    public static void main(final String[] args) {
	launch(args);

    }

    private volatile boolean zoomed;

    private ObservableList<XYChart.Series<X, Y>> originalData;

    private XYChart<X, Y> chart;

    private XYChart.Series<X, Y> series1;

    private XYChart.Series<X, Y> series2;

    private XYChart.Series<X, Y> series3;

    private synchronized void backUpData() {
	System.out.println("Backing up data");
	this.originalData = FXCollections.observableArrayList();
	originalData.add(deepCopySeries(series1));
	originalData.add(deepCopySeries(series2));
	originalData.add(deepCopySeries(series3));

    }

    private synchronized void restoreData() {
	chart.getData().clear();
	final ObservableList<XYChart.Series<X, Y>> originalData2 = FXCollections.observableArrayList();
	for (final Series<X, Y> s : originalData) {
	    originalData2.add(deepCopySeries(s));
	}
	System.out.println("Restoring data");
	for (final Series<X, Y> s : originalData2) {
	    System.out.println("Adding " + s.getName() + ", " + s.getData() + ", " + s.getChart());
	    chart.getData().add(s);
	}

    }

    public void setUpZooming(final Rectangle rect, final XYChart<X, Y> chart, final Axis<X> xAxis,
	    final Axis<Y> yAxis) {
	series1 = new XYChart.Series();
	series1.setName("2003");
	series1.getData().add(new XYChart.Data(austria, 25601.34));
	series1.getData().add(new XYChart.Data(brazil, 20148.82));
	series1.getData().add(new XYChart.Data(france, 10000));
	series1.getData().add(new XYChart.Data(italy, 35407.15));
	series1.getData().add(new XYChart.Data(usa, 12000));

	series2 = new XYChart.Series();
	series2.setName("2004");
	series2.getData().add(new XYChart.Data(austria, 57401.85));
	series2.getData().add(new XYChart.Data(brazil, 41941.19));
	series2.getData().add(new XYChart.Data(france, 45263.37));
	series2.getData().add(new XYChart.Data(italy, 117320.16));
	series2.getData().add(new XYChart.Data(usa, 14845.27));

	series3 = new XYChart.Series();
	series3.setName("2005");
	series3.getData().add(new XYChart.Data(austria, 45000.65));
	series3.getData().add(new XYChart.Data(brazil, 44835.76));
	series3.getData().add(new XYChart.Data(france, 18722.18));
	series3.getData().add(new XYChart.Data(italy, 17557.31));
	series3.getData().add(new XYChart.Data(usa, 92633.68));
	backUpData();
	this.chart = chart;
	chart.getData().addAll(series1, series2, series3);

	setUpZoomingRectangle(rect);
	setUpZoomingValueBounds(xAxis, yAxis);

    }

    private void setUpZoomingRectangle(final Rectangle rect) {
	final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
	chart.setOnMousePressed(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
		mouseAnchor.set(new Point2D(event.getX(), event.getY()));
	    }
	});
	chart.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
		    if (zoomed && event.getClickCount() == 2) {
			restoreData();
			zoomed = false;
			event.consume();
		    }
		}
	    }
	});
	chart.setOnMouseDragged(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
		final double x = event.getX();
		final double y = event.getY();
		rect.setX(Math.min(x, mouseAnchor.get().getX()));
		rect.setY(Math.min(y, mouseAnchor.get().getY()));
		rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
		rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
	    }
	});
	chart.setOnMouseReleased(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
		rect.setWidth(0);
		rect.setHeight(0);
	    }
	});
    }

    private void setUpZoomingValueBounds(final Axis<X> xAxis, final Axis<Y> yAxis) {
	final Node chartBackground = chart.lookup(".chart-plot-background");
	final ObjectProperty<Point2D> mouseAnchor2 = new SimpleObjectProperty<>();
	chartBackground.setOnMousePressed(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
		mouseAnchor2.set(new Point2D(event.getX(), event.getY()));
	    }
	});

	chartBackground.setOnMouseReleased(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {

		final Number nx1 = getNumber(xAxis, mouseAnchor2.get().getX());
		final Number nx2 = getNumber(xAxis, event.getX());
		final Number ny1 = getNumber(yAxis, mouseAnchor2.get().getY());
		final Number ny2 = getNumber(yAxis, event.getY());

		System.out.println("Nearest n1x value: " + nx1);
		System.out.println("Nearest n1y value: " + nx2);
		System.out.println("Nearest n2x value: " + ny1);
		System.out.println("Nearest n2y value: " + ny2);

		if (nx1 != null && nx2 != null) {

		}

		if (ny1 != null && ny2 != null) {
		    final double min = Math.min(ny1.doubleValue(), ny2.doubleValue());
		    final double max = Math.max(ny1.doubleValue(), ny2.doubleValue());
		    if (max - min > 1) {
			zoomed = true;
			System.out.println("Filter data for axis " + yAxis + " to be between " + min + " and " + max);

			final Iterator<?> it = chart.getData().iterator();
			while (it.hasNext()) {
			    final Object next = it.next();
			    if (next instanceof XYChart.Series<?, ?>) {
				final XYChart.Series<?, ?> s = (Series<?, ?>) next;
				final Iterator<?> it2 = s.getData().iterator();
				while (it2.hasNext()) {
				    final Object next2 = it2.next();
				    if (next2 instanceof XYChart.Data<?, ?>) {
					final XYChart.Data<?, ?> d = (Data<?, ?>) next2;
					if (d.getYValue() instanceof Number) {
					    final Number n = (Number) d.getYValue();
					    final double dd = n.doubleValue();
					    if (dd < min || dd > max) {
						System.out.println("Removing " + next2);
						it2.remove();
					    } else {
						System.out.println("Keeping " + d);
					    }
					}
				    } else {
					System.out.println("Wrong data type " + next2);
				    }
				    if (s.getData().isEmpty()) {
					it.remove();
				    }
				}
			    } else {
				System.out.println("Wrong data type " + next);
			    }
			}
		    } else {
			System.out.println("Skip tiny zoom");
		    }

		}
	    }
	});
    }

    @Override
    public void start(final Stage stage) throws Exception {
	stage.setTitle("Bar Chart Sample");

	final CategoryAxis xAxis = new CategoryAxis();
	final NumberAxis yAxis = new NumberAxis();
	final BarChart<String, Number> chart = new BarChart<String, Number>(xAxis, yAxis);
	chart.setTitle("Country Summary");
	xAxis.setLabel("Country");
	yAxis.setLabel("Value");

	final StackPane zoomContent = new StackPane();

	final Rectangle zoomRect = new Rectangle();
	zoomRect.setManaged(false);
	zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));

	zoomContent.getChildren().add(chart);
	zoomContent.getChildren().add(zoomRect);
	final Scene scene = new Scene(zoomContent, 800, 600);
	new BarChartTest<String, Number>().setUpZooming(zoomRect, chart, xAxis, yAxis);
	stage.setScene(scene);
	stage.show();

    }

}
