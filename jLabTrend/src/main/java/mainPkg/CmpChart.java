package mainPkg;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IAxisTick;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.swtchart.ISeriesSet;

import chartsPkg.ChartMouseListener;
import trendListPkg.TrendListItem;
import utilsPkg.Utils;

public class CmpChart extends Composite {
   Chart chart;
   Composite cmpThisChart;
   ChartMouseListener mouseListener;
   IAxisSet axisSet;

   public CmpChart(Composite parent, int style,
                              List<TrendListItem> chartItems) {
      super(parent, style);
      cmpThisChart = new Composite(parent, SWT.NONE);
      cmpThisChart.setLayout(new GridLayout(1, false));
      GridData gdChart = new GridData(SWT.FILL,SWT.TOP, true, false,1,1);
      gdChart.grabExcessHorizontalSpace = true;
      gdChart.horizontalAlignment = SWT.FILL;
      cmpThisChart.setLayoutData(gdChart);
      chart = new Chart(cmpThisChart, SWT.NONE);
      ILineSeries<?> []scatterSeries = new ILineSeries[chartItems.size()];
      for (int ci = 0; ci < chartItems.size(); ci++) {
         TrendListItem tli = chartItems.get(ci); 
         String title = tli.grpName;
         java.util.Date []uDates = new java.util.Date[tli.trendData.size()];
         double []vals = new double[tli.trendData.size()];
         for (int it = 0; it < tli.trendData.size(); it++) {
            uDates[it] = Utils.convertToDateViaInstant(
                        tli.trendData.get(it).rptFile.collectionDate);
            vals[it] = Double.parseDouble(tli.trendData.get(it).data.valStr);
         }
         String yAxisLabel = tli.trendData.get(0).data.units;
         chart.getTitle().setText(title);
         ISeriesSet sSet = chart.getSeriesSet();
         axisSet = chart.getAxisSet();
         IAxis xAxis = axisSet.getXAxis(0);
         xAxis.getTitle().setText("Date");
         IAxis yAxis = axisSet.getYAxis(0);
         yAxis.getTitle().setText(yAxisLabel);
         scatterSeries[ci] = (ILineSeries<?>)sSet.createSeries(
                                      SeriesType.LINE, tli.grpName);
         scatterSeries[ci].setSymbolSize(2);
//            xAxis.setCategorySeries(axisTimeStrs);
            //       scatterSeries[acct].setXSeries(epochTimes);
         scatterSeries[ci].setXDateSeries(uDates);
         scatterSeries[ci].setYSeries(vals);
         axisSet.adjustRange();
         mouseListener = new ChartMouseListener(chart);
      }
      IAxisTick xTick = axisSet.getXAxis(0).getTick();
      xTick.setFormat(new SimpleDateFormat("MM/YY"));
      //  double epoch time is seconds since 1970-01-01T00:00:00Z
      //      double []epochTimes = new double[selectedDateIndices.length];
//      String []axisTimeStrs = new String[selectedDateIndices.length];
//      java.util.Date []uDates = new java.util.Date[selectedDateIndices.length];
//      double [][]ys = new double[nSelAccts][selectedDateIndices.length];
//      for (int se = 0; se < selectedDateIndices.length; se++) {
//         DataPoint dp = ExcelIo.importData.get(selectedDateIndices[se]);
//         //         epochTimes[se] = dp.GetEpochSec();
//         //         axisTimeStrs[se] = dp.GetAxisLabelStr();
//         uDates[se] = dp.GetUtilDate();
//         // switch statement on cType here
//         double []amts = new double[nSelAccts];
//         amts = dp.GetAmtsArray(ChartSelections.accountsToChart);
//         switch (cType) {
//            case BALANCES:
//               title = "Account Balances";
//               yAxisLabel = "Balances";
//               for (int acct = 0; acct < nSelAccts; acct++) {
//                  ys[acct][se] = amts[acct];
//               }
//               break;
//            case DELTA_DOLLARS:
//               refDate = getSelectedDate(cboSelRefDate);
//               if (refDate == null) {
//                  OpMsgLog.LogMsg("No Reference date is set\n",  OpMsgLogger.LogLevel.ERROR);
//               } else if (ChartSelections.accountsToChart.size() == 0) {
//                     OpMsgLog.LogMsg("No acccounts are selected\n",  OpMsgLogger.LogLevel.ERROR);
//               } else {
////                  double []refAmts = ExcelIo.GetDataPoint(refDate).GetAmtsArray(ChartSelections.accountsToChart);
////                  title = "Balance Change From Reference";
////                  yAxisLabel = "$ Change";
////                  for (int acct = 0; acct < nSelAccts; acct++) {
////                     ys[acct][se] = amts[acct] - refAmts[acct];
////                  }
//               }
//               break;
//            case DELTA_PERCENT:
//               refDate = getSelectedDate(cboSelRefDate);
//               if (refDate == null) {
//                  OpMsgLog.LogMsg("No Reference date is set\n",  OpMsgLogger.LogLevel.ERROR);
//               } else if (ChartSelections.accountsToChart.size() == 0) {
//                     OpMsgLog.LogMsg("No acccounts are selected\n",  OpMsgLogger.LogLevel.ERROR);
//               } else {
//                  double []refAmts = ExcelIo.GetDataPoint(refDate).GetAmtsArray(ChartSelections.accountsToChart);
//                  title = "% Change From Reference of " + refDate.toString();
//                  yAxisLabel = "% Change";
//                  for (int acct = 0; acct < nSelAccts; acct++) {
//                     if (refAmts[acct] == 0) {
//                        ys[acct][se] = 0;
//                     } else {
//                        ys[acct][se] = 100 * (amts[acct] - refAmts[acct]) / refAmts[acct];
//                     }
//                  }
//               }
//               break;
//            default:
//         }
      //      xAxis.enableCategory(true);
//      for (int acct = 0; acct < nSelAccts; acct++) {
//         xAxis.setCategorySeries(axisTimeStrs);
//         //       scatterSeries[acct].setXSeries(epochTimes);
//         scatterSeries[acct].setXDateSeries(uDates);
//         scatterSeries[acct].setYSeries(ys[acct]);
//      }
//      axisSet.adjustRange();
      mouseListener = new ChartMouseListener(chart);
   }
}
