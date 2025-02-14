/*   use this class as template */
package utilsPkg;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ScrollingSelectionBox extends Composite {

   Composite cmpList;
   ArrayList<Composite> items = new ArrayList<Composite>();
   /**
    * Create the composite.
    * @param parent
    * @param style
    */
   public ScrollingSelectionBox(Composite parent, int style) {
      super(parent, style);
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      parent.setLayout(new GridLayout(1, false));
      ScrolledComposite scmp = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      scmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      Composite cmpList = new Composite(scmp, SWT.NONE);
      GridLayout gl_cmpList = new GridLayout(1, false);
      gl_cmpList.horizontalSpacing = 0;
      gl_cmpList.marginWidth = 0;
      gl_cmpList.marginHeight = 0;
      gl_cmpList.verticalSpacing = 0;
      gl_cmpList.marginLeft = 0;
      gl_cmpList.marginRight = 0;
      gl_cmpList.verticalSpacing = 0;
      cmpList.setLayout(gl_cmpList);
      scmp.setContent(cmpList);
   }

   public void AddToList(Composite item) {
      items.add(item);
   }
   
   public void UpdateDisplay() {
      this.pack();
      this.layout();
   }
   
   @Override
   protected void checkSubclass() {
      // Disable the check that prevents subclassing of SWT components
   }

}
