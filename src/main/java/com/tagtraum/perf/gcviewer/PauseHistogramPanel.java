package com.tagtraum.perf.gcviewer;


import com.tagtraum.perf.gcviewer.math.DoubleData;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class PauseHistogramPanel extends JPanel {

    private GCModel model;
    private ChartFrame histogramChart;

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public PauseHistogramPanel() {
    }

    public void setModel(GCModel model) {
        this.model = model;

        this.add(new ChartPanel(getHistogram()));

    }

    protected JFreeChart getHistogram() {
        JFreeChart chart = null;

        chart = ChartFactory.createHistogram(
                "GC Pause Distribution",
                "GC Time",
                "GC Events",
                getSimpleHistogramDataset(),
                PlotOrientation.VERTICAL,
                false /* legend */,
                false /* tooltips */,
                false /* urls */);
        return chart;
    }

    private SimpleHistogramDataset getSimpleHistogramDataset() {
        Iterator<GCEvent> eventIterator = model.getGCEvents();
        SimpleHistogramDataset dataset = new SimpleHistogramDataset("GC Pauses");

        double min = model.getPause().getMin();
        double max = model.getPause().getMax();
        addBins(90,min,max,dataset);

        while (eventIterator.hasNext()) {
            dataset.addObservation(eventIterator.next().getPause());
        }
        dataset.setAdjustForBinSize(true);


        return dataset;
    }


    private void addBins(int numBins, double min, double max, SimpleHistogramDataset dataset) {
        SimpleHistogramBin bins[] = new SimpleHistogramBin[numBins];

        double binWidth = (max - min) / numBins;
        double lower = min;
        double upper;
        dataset.removeAllBins();
        for (int i = 0; i < numBins; i++) {
            SimpleHistogramBin bin;
            // make sure bins[bins.length]'s upper boundary ends at maximum
            // to avoid the rounding issue. the bins[0] lower boundary is
            // guaranteed start from min
            if (i == numBins - 1) {
                bin = new SimpleHistogramBin(lower, max, true, true);
            } else {
                upper = min + (i + 1) * binWidth;
                bin = new SimpleHistogramBin(lower, upper, true, false);
                lower = upper;
            }
            dataset.addBin(bin);
        }
    }


    public GCModel getModel() {
        return model;
    }
}
