package mainPkg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import dataEnumerations.Colors;
import dataEnumerations.TrendFonts;
import importPkg.Reading;
import trendListPkg.TrendListItem;
import utilsPkg.Utils;

public class CmpTrendItemDetails extends Composite {
   public Button chkChart;
   
   private Label lblName;
   
   public CmpTrendItemDetails(Composite parent, int style,
                              TrendListItem tli) {
      super(parent, style);
      // contents of a trend item:
      //  1. composite cmpHdr containing an optional chart button + the
      //     group name - cmpHdr spans 3 columns and layout data is 2 columns
      //  2. composite this of tli.size() rows, each with 3 columns; left, value, and right
      final int ROW_HEIGHT = 18;
      GridLayout gl = new GridLayout(3, false);
      gl.horizontalSpacing = 0;
      gl.marginTop = 0;
      gl.marginBottom = 0;
      gl.marginWidth = 0;
      gl.marginHeight = 0;
      gl.verticalSpacing = 0;
      gl.marginLeft = 0;
      gl.marginRight = 0;
      setLayout(gl);
      //  cmpHdr contains optional chart button + group name
      //  in a 2 column composite
      Composite cmpHdr = new Composite(this, SWT.NONE);
      GridLayout glHdr = new GridLayout(2, false);
      glHdr.marginBottom = 0;
      glHdr.marginHeight = 0;
      glHdr.verticalSpacing = 0;
      cmpHdr.setLayout(glHdr);
      GridData gdHdr = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
      gdHdr.horizontalSpan = 3;
      cmpHdr.setLayoutData(gdHdr);
       
      if (tli.trendData.size() > 1) {
         // cmpHdr contains a chart checkbox button followed by group name
         GridData gdChk = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
         gdChk.widthHint = 44;
         chkChart = new Button(cmpHdr, SWT.CHECK);
         chkChart.setLayoutData(gdChk);
         chkChart.setText("chart");
         chkChart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               boolean selected = chkChart.getSelection();
               LabTrend.addToChart(tli, selected);
            }  
         });     
      } else {
         // cmpHdr contains a blank spacer followed by group name
         GridData gdSpc = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
         gdSpc.widthHint = 44;
         Label spacer = new Label(cmpHdr, SWT.NONE);
         spacer.setLayoutData(gdSpc);
         spacer.setText(" ");
      }
      lblName = new Label(cmpHdr, SWT.NONE);
      GridData gdName = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
      gdName.widthHint = 200;
      gdName.heightHint = ROW_HEIGHT;
      gdName.horizontalSpan = 1;
      gdName.horizontalAlignment = SWT.BOTTOM;
      gdName.verticalIndent = 8;
      lblName.setFont(TrendFonts.FIXED_BOLD);
      lblName.setLayoutData(gdName);
      lblName.setText(tli.grpName);
      
      GridData gdLeft = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
      gdLeft.widthHint = 120;
      gdLeft.heightHint = ROW_HEIGHT;
      gdLeft.verticalIndent = 0;
       
      GridData gdValue = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
      gdValue.widthHint = 54;
      gdValue.horizontalIndent = 12;
      gdValue.verticalIndent = 0;
      gdValue.heightHint = ROW_HEIGHT;
       
      GridData gdRight = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
      gdRight.verticalIndent = 0;
      gdRight.widthHint = 220;
      gdRight.heightHint = ROW_HEIGHT;
      
      for (int rptf = 0; rptf < tli.trendData.size(); rptf++) {
         // itemLine is in three parts to allow color set on value
         setLineText(tli.trendData.get(rptf));
      }  
   }
   private String fValue(String vStr) {
      final String UNDEFINED = "     -- ";
      try {
         String ret = vStr;
         if (vStr.length() == 2 && vStr.equals("--")) {
            ret = "     -- ";
         } else {
            try {
               double val = Double.parseDouble(vStr);
               ret = String.format("%8.2f", val);
            } catch (Exception e) {}
         }
         return ret;
      } catch (Exception ne) {
         return UNDEFINED;
      }
   }
   
   private void setLineText(Reading r) {
      Label lblLeft;
      StyledText stxtValue;
      Label lblRight;
      GridData gdLeft = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
      GridData gdValue = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
      GridData gdRight = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
      
      lblLeft = new Label(this, SWT.NONE);
      lblLeft.setLayoutData(gdLeft);
      String vMinStr = fValue(r.data.minNormalStr);
      lblLeft.setText(r.rptFile.collectionDate + vMinStr);
      lblLeft.setFont(TrendFonts.FIXED_NORMAL);
      Color clr = Colors.WHITE;
      if (Utils.LegitDoubleString(r.data.valStr)) {
         if (r.data.hasLimits()) {
            clr = r.data.withinLimits()? Colors.PALE_GREEN : Colors.PALE_YELLOW;
         }
      }
      stxtValue = new StyledText(this, SWT.NONE);
      String vStr = fValue(r.data.valStr);
      int startPos = 0;
      for (int s = 0; s < vStr.length(); s++) {
         if (vStr.charAt(s) != ' ') {
            startPos = s;
            break;
         }
      }
      stxtValue.setText(vStr);
      stxtValue.setLayoutData(gdValue);
      stxtValue.setFont(TrendFonts.FIXED_BOLD);    
      stxtValue.setBackground(clr);
      StyleRange sr = new StyleRange();
      sr.start = startPos;
      sr.length = vStr.length() - startPos;
      sr.underline = clr == Colors.PALE_YELLOW;
//      StyledText test = new StyledText(this, SWT.NONE);
//      test.setText("0123456789");
//      int sp;
//      sp = 0;
//      while (sp >= 0) {
//         sr.start = sp;
//         test.setStyleRange(sr);
//         int bp=2;
//      }
      stxtValue.setStyleRange(sr);
      
      lblRight = new Label(this, SWT.NONE);
      lblRight.setLayoutData(gdRight);
      lblRight.setFont(TrendFonts.FIXED_NORMAL);
      String vMaxStr = fValue(r.data.maxNormalStr);
      lblRight.setText(String.format("%s  %s", vMaxStr, r.data.units));
      lblRight.setAlignment(SWT.LEFT);
      
   }
   
//   public void clearLineText() {
//      lblLeft.setText("");
//      lblRight.setText("");
//      Color clr = Colors.WHITE;
//      lblValue.setBackground(clr);
//      lblValue.setText(" ");
//   }

}
