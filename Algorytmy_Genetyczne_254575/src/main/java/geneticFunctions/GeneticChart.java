package geneticFunctions;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.List;

public class GeneticChart extends JFrame {

    List<Integer> bests;
    List<Integer> worsts;
    List<Integer> averages;

    public GeneticChart(List<Integer> bests, List<Integer> worsts, List<Integer> averages) {
        this.bests = bests;
        this.worsts = worsts;
        this.averages = averages;
        initUI();
        this.setVisible(true);
    }

    private void initUI() {

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        add(chartPanel);

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private XYDataset createDataset() {

        var average = new XYSeries("avg");
        var best = new XYSeries("best");
        var worst = new XYSeries("worst");


        for (int i = 0; i < this.bests.size(); i++) {
            best.add(i, this.bests.get(i));
        }
        for (int i = 0; i < this.averages.size(); i++) {
            average.add(i, this.averages.get(i));
        }
        for (int i = 0; i < this.worsts.size(); i++) {
            worst.add(i, worsts.get(i));
        }

        var dataset = new XYSeriesCollection();
        dataset.addSeries(best);
        dataset.addSeries(worst);
        dataset.addSeries(average);

        return dataset;
    }

    private JFreeChart createChart(final XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Adaptation progress",
                "Generation",
                "Objective function",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer(true, false);

        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.ORANGE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.BLACK);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Adaptation progress",
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }
}
