package mainPkg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import dataEnumerations.TrendFonts;

public class CmpTrendItemName extends Composite {

   private Label lblName;
   
   public CmpTrendItemName(Composite parent, int style, String name) {
      super(parent, style);
      setLayout(new FillLayout(SWT.NONE));
      GridData gdLine =new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1); 
      gdLine.heightHint = 18;
      setLayoutData(gdLine);
      GridLayout gl = new GridLayout(1, false);
      gl.horizontalSpacing = 0;
      gl.marginTop = 0;
      gl.marginBottom = 0;
      gl.marginWidth = 0;
      gl.marginHeight = 0;
      gl.verticalSpacing = 0;
      gl.marginLeft = 0;
      gl.marginRight = 0;
      setLayout(gl);
       
      lblName = new Label(this, SWT.NONE);
      GridData gdLeft = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
      gdLeft.widthHint = 120;
      lblName.setFont(TrendFonts.BOLD_HDR);
      lblName.setText(name);
      lblName.setLayoutData(gdLeft);       
   }

}
