package dataEnumerations;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

public class TrendFonts {
   public final static Font FIXED_NORMAL = new Font(Display.getDefault(), "Courier", 6, SWT.NORMAL);
   public final static Font FIXED_BOLD = new Font(Display.getDefault(), "Courier", 6, SWT.BOLD);
   public final static Font BOLD_HDR = SWTResourceManager.getFont("Segoe", 10, SWT.BOLD);
   public final static Font DEFAULT = SWTResourceManager.getFont("Segoe", 9, SWT.NORMAL);

}
