package com.grendelscan.GUI2.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.GUI.customControls.basic.GLabel;
import com.grendelscan.GUI.customControls.basic.GTable;
import com.grendelscan.GUI.customControls.basic.GTableColumn;
import com.grendelscan.GUI.customControls.basic.GTableItem;
import com.grendelscan.GUI.customControls.basic.GText;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.Verifiers.EnforceDecimalNumbersOnly;
import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.proxy.reverseProxy.ReverseProxyConfig;
import com.grendelscan.scan.Scan;


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
public class ReverseProxySettingsComposite extends com.grendelscan.GUI.customControls.basic.GComposite implements GrendelSettingsControl{

	protected GTable proxyTable;

	protected GLabel webHostnameLabel;
	protected GText webHostnameTextbox;

	protected GLabel localIPLabel;
	protected GText localIPTextbox;
	
	protected GLabel localPortLabel;
	protected GText localPortTextbox;

	protected GLabel remoteIPLabel;
	protected GText remoteIPTextbox;
	
	protected GLabel remotePortLabel;
	protected GText remotePortTextbox;

	protected GButton sslCheckbox;

	protected GButton addButton;
	protected GButton removeButton;

	protected GButton startStopButton;

	

	
	public ReverseProxySettingsComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style) 
	{
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			this.setLayout(new FormLayout());
			EnforceIntegersOnly integersOnly = new EnforceIntegersOnly();
			EnforceDecimalNumbersOnly decimalNumbersOnly = new EnforceDecimalNumbersOnly();
			int currentTop = 5;
			int currentLeft = 5;
			{
				{
					webHostnameLabel = new GLabel(this, SWT.NONE);
					webHostnameLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					webHostnameLabel.setText("Web hostname:");
					FormData layoutData = new FormData();
					layoutData.width = 90;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					webHostnameLabel.setLayoutData(layoutData);
					currentLeft += layoutData.width + 5;
				}
				{
					webHostnameTextbox = new GText(this, SWT.BORDER);
					webHostnameTextbox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					FormData layoutData = new FormData();
					layoutData.width = 200;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					webHostnameTextbox.setLayoutData(layoutData);
					currentLeft += layoutData.width + 25;
				}
	
				{
					localIPLabel = new GLabel(this, SWT.NONE);
					localIPLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					localIPLabel.setText("Local IP:");
					FormData layoutData = new FormData();
					layoutData.width = 50;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					localIPLabel.setLayoutData(layoutData);
					currentLeft += layoutData.width + 5;
				}
				{
					localIPTextbox = new GText(this, SWT.BORDER);
					localIPTextbox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					FormData layoutData = new FormData();
					localIPTextbox.setTextLimit(15);
					layoutData.width = 100;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					localIPTextbox.setLayoutData(layoutData);
					currentLeft += layoutData.width + 25;
					localIPTextbox.addVerifyListener(decimalNumbersOnly);
				}
	
				{
					localPortLabel = new GLabel(this, SWT.NONE);
					localPortLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					localPortLabel.setText("Local port:");
					FormData layoutData = new FormData();
					layoutData.width = 65;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					localPortLabel.setLayoutData(layoutData);
					currentLeft += layoutData.width + 5;
				}
				{
					localPortTextbox = new GText(this, SWT.BORDER);
					localPortTextbox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					localPortTextbox.setTextLimit(5);
					FormData layoutData = new FormData();
					layoutData.width = 40;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					localPortTextbox.setLayoutData(layoutData);
					currentLeft += layoutData.width + 25;
					localPortTextbox.addVerifyListener(integersOnly);
				}
	
			}

			
			
			currentTop += 40;
			currentLeft = 5;
			{
				{
					remoteIPLabel = new GLabel(this, SWT.NONE);
					remoteIPLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					remoteIPLabel.setText("Remote hostname/IP:");
					FormData layoutData = new FormData();
					layoutData.width = 125;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					remoteIPLabel.setLayoutData(layoutData);
					currentLeft += layoutData.width + 5;
				}
				{
					remoteIPTextbox = new GText(this, SWT.BORDER);
					remoteIPTextbox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					FormData layoutData = new FormData();
					layoutData.width = 200;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					remoteIPTextbox.setLayoutData(layoutData);
					currentLeft += layoutData.width + 25;
				}
	
				{
					remotePortLabel = new GLabel(this, SWT.NONE);
					remotePortLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					remotePortLabel.setText("Remote port:");
					FormData layoutData = new FormData();
					layoutData.width = 80;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					remotePortLabel.setLayoutData(layoutData);
					currentLeft += layoutData.width + 5;
				}
				{
					remotePortTextbox = new GText(this, SWT.BORDER);
					remotePortTextbox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					remotePortTextbox.setTextLimit(5);
					FormData layoutData = new FormData();
					layoutData.width = 40;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					remotePortTextbox.setLayoutData(layoutData);
					currentLeft += layoutData.width + 25;
					remotePortTextbox.addVerifyListener(integersOnly);
				}
				{
					sslCheckbox = new GButton(this, SWT.CHECK | SWT.LEFT);
					sslCheckbox.setText("SSL");
					sslCheckbox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					FormData layoutData = new FormData();
					layoutData.width = 50;
					layoutData.height = 19;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					sslCheckbox.setLayoutData(layoutData);
					currentLeft += layoutData.width + 25;
				}
			}
			
			
			currentLeft = 5;
			currentTop += 35;
			{
				{
					addButton = new GButton(this, SWT.PUSH | SWT.CENTER);
					addButton.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					addButton.setText("Add");
					FormData layoutData = new FormData();
					layoutData.width = 60;
					layoutData.height = 27;
					layoutData.left =  new FormAttachment(0, 1000, currentLeft);
					layoutData.top =  new FormAttachment(0, 1000, currentTop);
					addButton.setLayoutData(layoutData);
					currentLeft += layoutData.width + 10;
					addButton.addSelectionListener(new SelectionAdapter() 
					{
						@Override
	                    public void widgetSelected(SelectionEvent evt) 
						{
							String messageText = "";
							if (remoteIPTextbox.getText().equals(""))
							{
								messageText += "Remote IP address is required.\n";
							}
							if (remotePortTextbox.getText().equals(""))
							{
								messageText += "Remote port is required.\n";
							}
							if (localIPTextbox.getText().equals(""))
							{
								messageText += "Local IP address is required.\n";
							}
							if (localPortTextbox.getText().equals(""))
							{
								messageText += "Local port is required.\n";
							}
							if (webHostnameTextbox.getText().equals(""))
							{
								messageText += "Web hostname is required.\n";
							}
							if (messageText.equals(""))
							{
								ReverseProxyConfig config = new ReverseProxyConfig();
								config.setRemoteHost(remoteIPTextbox.getText());
								config.setRemotePort(Integer.valueOf(remotePortTextbox.getText()));
								config.setBindIP(localIPTextbox.getText());
								config.setBindPort(Integer.valueOf(localPortTextbox.getText()));
								config.setWebHostname(webHostnameTextbox.getText());
								config.setSsl(sslCheckbox.getSelection());
								
								displayReverseProxyConfig(config);
								Scan.getScanSettings().addReverseProxyConfig(config);
								Scan.getInstance().getProxies().addReverseProxy(config);

								remoteIPTextbox.setText("");
								remotePortTextbox.setText("");
								localIPTextbox.setText("");
								localPortTextbox.setText("");
								webHostnameTextbox.setText("");
								sslCheckbox.setSelection(false);
							}
							else
							{
								MainWindow.getInstance().displayMessage("Error:", messageText, true);
							}
						}
					});
				}
				{
					removeButton = new GButton(this, SWT.PUSH | SWT.CENTER);
					removeButton.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					removeButton.setText("Remove");
					FormData buttonLData = new FormData();
					buttonLData.width = 60;
					buttonLData.height = 27;
					buttonLData.left =  new FormAttachment(0, 1000, currentLeft);
					buttonLData.top =  new FormAttachment(0, 1000, currentTop);
					removeButton.setLayoutData(buttonLData);
					currentLeft += buttonLData.width + 20;
					removeButton.addSelectionListener(new SelectionAdapter() 
					{
						@Override
	                    public void widgetSelected(SelectionEvent evt) 
						{
							int index = proxyTable.getSelectionIndex();
							if (index >= 0)
							{
								GTableItem item = proxyTable.getItem(index);
								ReverseProxyConfig config = (ReverseProxyConfig) item.getData();
								if (MainWindow.getInstance().displayPrompt("Warning:", "Removing this proxy will stop it too.",
										SWT.OK | SWT.CANCEL, true) == SWT.CANCEL)
								{
									return;
								}
								Scan.getInstance().getProxies().stopAndRemoveReverseProxy(config);
								Scan.getScanSettings().removeReverseProxyConfig(config);
								
								remoteIPTextbox.setText(config.getRemoteHost());
								remotePortTextbox.setText(String.valueOf(config.getRemotePort()));
								localIPTextbox.setText(config.getBindIP());
								localPortTextbox.setText(String.valueOf(config.getBindPort()));
								webHostnameTextbox.setText(config.getWebHostname());
								sslCheckbox.setSelection(config.isSsl());

								proxyTable.remove(index);
								item.dispose();
							}
						}
					});
				}

				{
					startStopButton = new GButton(this, SWT.PUSH | SWT.CENTER);
					startStopButton.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					startStopButton.setText("Start");
					FormData buttonLData = new FormData();
					buttonLData.width = 60;
					buttonLData.height = 27;
					buttonLData.left =  new FormAttachment(0, 1000, currentLeft);
					buttonLData.top =  new FormAttachment(0, 1000, currentTop);
					startStopButton.setLayoutData(buttonLData);
					currentLeft += buttonLData.width + 10;
					startStopButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
	                    public void widgetSelected(SelectionEvent evt) 
						{
							int index = proxyTable.getSelectionIndex();
							if (index >= 0)
							{
								GTableItem item = proxyTable.getItem(index);
								ReverseProxyConfig config = (ReverseProxyConfig) item.getData();
								if (startStopButton.getText().equals("Start"))
								{
									Scan.getInstance().getProxies().startReverseProxy(config);
								}
								else
								{
									Scan.getInstance().getProxies().stopReverseProxy(config);
								}
								updateStartStop();
							}
						}
					});
				}
			}
			
			currentLeft = 5;
			currentTop += 40;
			
			FormData proxyTableLData = new FormData();
			proxyTableLData.width = 737;
			proxyTableLData.height = 187;
			proxyTableLData.left =  new FormAttachment(0, 1000, currentLeft);
			proxyTableLData.top =  new FormAttachment(0, 1000, currentTop);
			proxyTableLData.bottom =  new FormAttachment(1000, 1000, -5);
			proxyTableLData.right =  new FormAttachment(1000, 1000, -5);
			proxyTable = new GTable(this, SWT.FULL_SELECTION | SWT.BORDER);
			proxyTable.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
			proxyTable.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						updateStartStop();
					}
					
				});
			proxyTable.setLayoutData(proxyTableLData);
			proxyTable.setHeaderVisible(true);
			{
				GTableColumn column = new GTableColumn(proxyTable, SWT.NONE);
				column.setText("Web hostname");
				column.setWidth(170);
			}
			{
				GTableColumn column = new GTableColumn(proxyTable, SWT.NONE);
				column.setText("Local IP");
				column.setWidth(80);
			}
			{
				GTableColumn column = new GTableColumn(proxyTable, SWT.NONE);
				column.setText("Local port");
				column.setWidth(100);
			}
			{
				GTableColumn column = new GTableColumn(proxyTable, SWT.NONE);
				column.setText("SSL");
				column.setWidth(40);
			}
			{
				GTableColumn column = new GTableColumn(proxyTable, SWT.NONE);
				column.setText("Remote host/IP");
				column.setWidth(150);
			}
			{
				GTableColumn column = new GTableColumn(proxyTable, SWT.NONE);
				column.setText("Remote port");
				column.setWidth(100);
			}
			{
				GTableColumn column = new GTableColumn(proxyTable, SWT.NONE);
				column.setText("Status");
				column.setWidth(100);
			}
			updateTableStatuses();
			this.layout();
	}
	
	public void displayReverseProxyConfigs(List<ReverseProxyConfig> configs)
	{
		for(GTableItem item: proxyTable.getItems())
		{
			item.dispose();
		}
		proxyTable.clearAll();
		
		for(ReverseProxyConfig config: configs)
		{
			displayReverseProxyConfig(config);
		}
	}
	
	
	
	protected void displayReverseProxyConfig(ReverseProxyConfig config)
	{
		GTableItem item = new GTableItem(proxyTable, SWT.NONE);
		item.setText(getProxyConfigStrings(config));
		item.setData(config);
	}
	
	public ReverseProxyConfig[] getReverseProxyConfigs()
	{
		ArrayList<ReverseProxyConfig> configs = new ArrayList<ReverseProxyConfig>(proxyTable.getItemCount());
		for (GTableItem item: proxyTable.getItems())
		{
			configs.add((ReverseProxyConfig) item.getData());
		}
		return configs.toArray(new ReverseProxyConfig[0]);
	}

	protected String[] getProxyConfigStrings(ReverseProxyConfig config)
	{
		/*
		 * web hostname
		 * local ip
		 * local port
		 * ssl
		 * remote host
		 * remote port
		 */

		return new String[] {config.getWebHostname(), config.getBindIP(), 
				String.valueOf(config.getBindPort()),  
				String.valueOf(config.isSsl()), config.getRemoteHost(),
				String.valueOf(config.getRemotePort())};
	}

	protected void updateStartStop()
	{
		int index = proxyTable.getSelectionIndex();
		if (index >= 0)
		{
			GTableItem item = proxyTable.getItem(index);
			ReverseProxyConfig config = (ReverseProxyConfig) item.getData();
			startStopButton.setText(Scan.getInstance().getProxies().isReverseProxyRunning(config) ? "Stop" : "Start");
		}
		else
		{
			startStopButton.setEnabled(false);
		}
	}
	
	protected void updateTableStatuses()
	{
		if (!isDisposed())
		{
			getDisplay().timerExec(250, new Runnable()
			{
				@Override
				public void run()
				{
					for(GTableItem item: proxyTable.getItems())
					{
						ReverseProxyConfig config = (ReverseProxyConfig) item.getData();
						String text = "Not Running";
						if (Scan.getInstance().getProxies().isReverseProxyRunning(config))
							text = "Running"; 
						item.setText(6, text);
					}
					updateTableStatuses();
				}
			});
		}
	}

	@Override
	public void updateFromSettings()
	{
		// Managed in control
	}

	@Override
	public String updateToSettings()
	{
		// Managed in control
		return "";
	}
}