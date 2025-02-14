package mainPkg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CmpSelectFocus  extends Composite {
   
   public CmpSelectFocus(Composite parent, int style) {
      super(parent, style);
      new Composite(parent, SWT.NONE);
      FormData fd_cmpSelectFocus = new FormData();
      fd_cmpSelectFocus.bottom = new FormAttachment(100);
      fd_cmpSelectFocus.right = new FormAttachment(0, 542);
      fd_cmpSelectFocus.top = new FormAttachment(0, 36);
      fd_cmpSelectFocus.left = new FormAttachment(0);
      setLayoutData(fd_cmpSelectFocus);
      setLayout(new GridLayout(1, false));
      setLayoutData(fd_cmpSelectFocus);
   }

}
