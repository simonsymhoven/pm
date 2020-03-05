package controllers.aktien;

import YahooAPI.YahooStockAPI;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.log4j.Log4j2;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class Plot extends Task<Image> {

        private AktienModel aktienModel;
        private YahooStockAPI yahooStockAPI;

        Plot(AktienModel aktienModel) {
            this.yahooStockAPI = new YahooStockAPI();
            this.aktienModel = aktienModel;
        }

        @Override
        protected Image call() {
            aktienModel.setStock(yahooStockAPI.getStock(aktienModel.getAktie().getSymbol()));
            return buildXYChart();
        }

        private Image buildXYChart() {
            List<Date> xList = new ArrayList();
            List<Double> yList = new ArrayList();
            aktienModel.getStock().getHistory().forEach(h -> {
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
                            .width(830)
                            .height(315)
                            .title(aktienModel.getAktie().getName())
                            .xAxisTitle("Zeit")
                            .yAxisTitle("Wert in €")
                            .build();

            chart.getStyler().setMarkerSize(0);
            chart.getStyler().setChartBackgroundColor(new Color(244,244,244));
            chart.getStyler().setLegendBackgroundColor(new Color(244,244,244));
            chart.getStyler().setPlotBackgroundColor(new Color(244,244,244));
            chart.getStyler().setChartTitleVisible(false);
            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setXAxisLabelRotation(30);
            chart.getStyler().setChartFontColor(new Color(20,25,29));
            chart.getStyler().setPlotBorderColor(new Color(20,25,29));
            chart.getStyler().setAxisTickMarksColor(new Color(20,25,29));
            chart.getStyler().setAxisTickLabelsColor(new Color(20,25,29));
            chart.getStyler().setDatePattern("MM/yyyy");
            // Series
            chart.addSeries(aktienModel.getAktie().getName(), xList, yList);

            return SwingFXUtils.toFXImage(BitmapEncoder.getBufferedImage(chart), null);
        }

}
