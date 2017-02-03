package com.silicosciences.javafx.charts.zooming;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ZoomManager<X, Y> {

	static <X, Y> ObservableList<XYChart.Series<X, Y>> deepCopySeries(final Collection<XYChart.Series<X, Y>> data) {
		final ObservableList<XYChart.Series<X, Y>> backup = FXCollections.observableArrayList();
		for (final Series<X, Y> s : data) {
			backup.add(deepCopySeries(s));
		}
		return backup;
	}

	static <X, Y> XYChart.Series<X, Y> deepCopySeries(final XYChart.Series<X, Y> series) {
		final XYChart.Series<X, Y> result = new XYChart.Series<X, Y>();
		result.setName(series.getName());
		result.setData(deepCopySeriesData(series.getData()));
		return result;
	}

	static <X, Y> ObservableList<XYChart.Data<X, Y>> deepCopySeriesData(
			final Collection<? extends XYChart.Data<X, Y>> data) {
		final ObservableList<XYChart.Data<X, Y>> result = FXCollections.observableArrayList();
		for (final Data<X, Y> i : data) {
			result.add(new Data<X, Y>(i.getXValue(), i.getYValue()));
		}
		return result;
	}

	static Number getNumber(final Axis<?> axis, final double cooridnate) {
		final Object object = axis.getValueForDisplay(cooridnate);
		if (object != null) {
			if (object instanceof Number) {
				final Number number = (Number) object;
				return number;
			}
		}
		return null;
	}

	private final ObservableList<XYChart.Series<X, Y>> series;

	private final XYChart<X, Y> chart;

	private volatile boolean zoomed;

	public ZoomManager(final Pane chartParent, final XYChart<X, Y> chart,
			final Collection<? extends Series<X, Y>> series) {
		super();
		this.chart = chart;
		this.series = FXCollections.observableArrayList(series);
		restoreData();
		final Rectangle zoomRect = new Rectangle();
		zoomRect.setManaged(false);
		zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
		chartParent.getChildren().add(zoomRect);
		setUpZooming(zoomRect, chart);

	}

	public ZoomManager(final Pane chartParent, final XYChart<X, Y> chart, final Series<X, Y>... series) {
		this(chartParent, chart, Arrays.asList(series));

	}

	private void doZoom(final boolean x, final Number n1, final Number n2) {
		final double min = Math.min(n1.doubleValue(), n2.doubleValue());
		final double max = Math.max(n1.doubleValue(), n2.doubleValue());
		if (max - min > 1) {
			zoomed = true;
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
							final Object value;
							if (x) {
								value = d.getXValue();
							} else {
								value = d.getYValue();
							}
							if (value instanceof Number) {
								final Number n = (Number) value;
								final double dd = n.doubleValue();
								if (dd < min || dd > max) {
									it2.remove();
								} else {
								}
							}
						} else {
							System.err.println("Wrong data type " + next2);
						}
						if (s.getData().isEmpty()) {
							it.remove();
						}
					}
				} else {
					System.err.println("Wrong data type " + next);
				}
			}
		} else {
			// System.out.println("Skip tiny zoom");
		}

	}

	private synchronized void restoreData() {
		// make a tmp variable of data, since we will modify it but need to be
		// able to restore
		final ObservableList<XYChart.Series<X, Y>> backup2 = deepCopySeries(series);
		chart.getData().setAll(backup2);

	}

	private void setUpZooming(final Rectangle rect, final XYChart<X, Y> chart) {

		setUpZoomingRectangle(rect);
		setUpZoomingValueBounds(chart.getXAxis(), chart.getYAxis());

	}

	/**
	 * Displays a colored rectangle that will indicate zooming boundaries
	 *
	 * @param rect
	 */
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

				if (nx1 != null && nx2 != null) {
					doZoom(true, nx1, nx2);
				}

				if (ny1 != null && ny2 != null) {
					doZoom(false, ny1, ny2);
				}
			}
		});
	}
}
