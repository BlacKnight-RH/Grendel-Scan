package com.grendelscan.ui.http;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;

import com.grendelscan.ui.customControls.basic.GShell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class FileNotFoundDialog extends com.grendelscan.ui.customControls.basic.GDialog {

	private GShell dialogShell;
	private Browser browser;

	public FileNotFoundDialog(GShell parent, int style) {
		super(parent, style);
	}

	public void open() {
		try {
			GShell parent = getParent();
			dialogShell = new GShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			dialogShell.setLayout(new FormLayout());
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(674, 417);
			{
				FormData browserLData = new FormData();
				browserLData.left =  new FormAttachment(0, 1000, 12);
				browserLData.top =  new FormAttachment(0, 1000, 73);
				browserLData.width = 642;
				browserLData.height = 304;
				browser = new Browser(dialogShell, SWT.NONE);
				browser.setLayoutData(browserLData);
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
