package mainPkg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import dataEnumerations.Colors;
import dataEnumerations.TrendFonts;
import importPkg.DataPoint;
import utilsPkg.Utils;

public class CmpItemDetails extends Composite {

   private Label lblLeft;
   private Label lblValue;
   private Label lblRight;
   
   public CmpItemDetails(Composite parent, int style) {
       super(parent, style);
       setLayout(new FillLayout(SWT.NONE));
       GridData gdLine =new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1); 
       gdLine.heightHint = 18;
       setLayoutData(gdLine);
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
       
       lblLeft = new Label(this, SWT.NONE);
       GridData gdLeft = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
       gdLeft.widthHint = 260;
       lblLeft.setFont(TrendFonts.FIXED_NORMAL);
       lblLeft.setText("left");
       lblLeft.setLayoutData(gdLeft);
       
       lblValue = new Label(this, SWT.NONE);
       GridData gdValue = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
       gdValue.widthHint = 80;
       gdValue.horizontalIndent = 12;
       lblValue.setFont(TrendFonts.FIXED_BOLD);
       lblValue.setText("value");
       lblValue.setLayoutData(gdValue);
       
       lblRight = new Label(this, SWT.NONE);
       GridData gdRight = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
       gdRight.widthHint = 230;
       lblRight.setAlignment(SWT.LEFT);
       lblRight.setFont(TrendFonts.FIXED_NORMAL);
       lblRight.setLayoutData(gdRight);
       lblRight.setText("right");   
   }
   
   public void setLineText(DataPoint item) {
      lblLeft.setText(item.itemLineLeft());
      lblRight.setText(item.itemLineRight());
      Color clr = Colors.WHITE;
      if (Utils.LegitDoubleString(item.valStr)) {
         if (item.hasLimits()) {
            clr = item.withinLimits()? Colors.PALE_GREEN : Colors.PALE_YELLOW;
         }
      }
      lblValue.setBackground(clr);
      lblValue.setText(item.itemLineValue());
   }

}
