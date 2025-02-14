package trendListPkg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import mainPkg.LabTrend;

public class TrendItemSelector {
   
   public Button chkSel;
   public int tli;
   public boolean selected = false;
   boolean btnSel = true;
   
   public TrendItemSelector(Composite parent, String id, int trendListIndex) {
      chkSel = new Button(parent, SWT.CHECK);
      chkSel.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (btnSel) {
               selected = chkSel.getSelection();
            }
            LabTrend.ShowTrendItemSelections();
         }  
      });     

      tli = trendListIndex;
      chkSel.setText(String.format("%3d. %s", tli + 1, id));
   }
   
   public void SetSelection(boolean sel) {
      selected = sel;
      btnSel = false;
      chkSel.setSelection(sel);
      btnSel = true;
   }
}
