package controllers.investments.alternative;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.log4j.Log4j2;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import yahooapi.YahooStockAPI;
import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class Plot extends Task<Image> {

        private AlternativeModel alternativeModel;
        private YahooStockAPI yahooStockAPI;

        Plot(AlternativeModel alternativeModel) {
            this.yahooStockAPI = new YahooStockAPI();
            this.alternativeModel = alternativeModel;
        }

        @Override
        protected Image call() {
            alternativeModel.setAlternative(yahooStockAPI.getAlternative(alternativeModel.getAlternative().getSymbol()));
            return buildXYChart();
        }

        private Image buildXYChart() {
            List<Date> xList = new ArrayList();
            List<Double> yList = new ArrayList();
            alternativeModel.setHistory(new ArrayList<>(yahooStockAPI.getHistory(alternativeModel.getAlternative().getSymbol())));
            alternativeModel.getHistory().forEach(h -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = simpleDateFormat.parse(h.getDate());
                    xList.add(date);
                    yList.add(h.getClosedPrice().doubleValue());
                } catch (ParseException e) {
                    log.error(e);
                }


            });

            XYChart chart =
                    new XYChartBuilder()
                            .width(800)
                            .height(400)
                            .title(alternativeModel.getAlternative().getName())
                            .xAxisTitle("Zeit")
                            .yAxisTitle("Wert in €")
                            .build();

            chart.getStyler().setMarkerSize(1);
            Color color = new Color(51, 73, 85);
            chart.getStyler().setChartBackgroundColor(new Color(244, 244, 244));
            chart.getStyler().setPlotBackgroundColor(new Color(244, 244, 244));
            chart.getStyler().setChartTitleVisible(false);
            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setXAxisLabelRotation(0);
            chart.getStyler().setChartFontColor(color);
            chart.getStyler().setPlotBorderColor(color);
            chart.getStyler().setAxisTickMarksColor(color);
            chart.getStyler().setAxisTickLabelsColor(color);
            chart.getStyler().setDatePattern("dd.MM");
            Color[] colors = new Color[xList.size()];
            for (int i = 0; i < xList.size(); i++) {
                colors[i] = new Color(249, 170, 51);
            }
            chart.getStyler().setSeriesColors(colors);
            // Series
            chart.addSeries(alternativeModel.getAlternative().getName(), xList, yList);

            return SwingFXUtils.toFXImage(BitmapEncoder.getBufferedImage(chart), null);
        }

}
