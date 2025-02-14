package mainPkg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class FocusFileSelector {

   private int fileNum;
   private Button rdoSel;
   
   public FocusFileSelector(Composite parent, String id, int num) {
      fileNum = num;
      rdoSel = new Button(parent, SWT.RADIO);
      rdoSel.setText(String.format("%3d. %s", num + 1, id));
      rdoSel.addSelectionListener(new SelectionAdapter()  {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (rdoSel.getSelection()) {
               LabTrend.SelectFocusFile(fileNum);
            };
         }
     });
   }
   
   public void SetSelection() {
      rdoSel.setSelection(true);
   }
}
