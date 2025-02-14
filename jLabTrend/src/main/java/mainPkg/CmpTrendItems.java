package mainPkg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CmpTrendItems  extends Composite {
   
   public CmpTrendItems(Composite parent, int style) {
      super(parent, style);
      new Composite(parent, SWT.NONE);
      FormData fd_cmpTrendItems = new FormData();
      fd_cmpTrendItems.bottom = new FormAttachment(100);
      fd_cmpTrendItems.right = new FormAttachment(0, 542);
      fd_cmpTrendItems.top = new FormAttachment(0, 36);
      fd_cmpTrendItems.left = new FormAttachment(0);
      setLayoutData(fd_cmpTrendItems);
      setLayout(new GridLayout(1, false));
   }

}
