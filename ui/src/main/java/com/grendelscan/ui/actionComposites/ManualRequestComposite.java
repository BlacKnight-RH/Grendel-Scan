package com.grendelscan.ui.actionComposites;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.HttpFormatException;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.ui.MainWindow;
import com.grendelscan.ui.Verifiers.EnforceDecimalNumbersOnly;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GText;
import com.grendelscan.ui.http.transactionDisplay.TransactionComposite;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ManualRequestComposite extends com.grendelscan.ui.customControls.basic.GComposite
{

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualRequestComposite.class);

    GButton executeRequestButton;
    private GButton testManualRequestsCheckbox;
    TransactionComposite transactionComposite;
    private GButton sslCheck;
    private GText networkPortText;
    private GLabel networkPortabel;
    private GText networkHostText;
    private GLabel networkHostLabel;
    private final RequestOptions requestOptions;

    public ManualRequestComposite(final com.grendelscan.ui.customControls.basic.GComposite parent, final int style)
    {
        super(parent, style);
        requestOptions = new RequestOptions();
        requestOptions.validateUriFormat = false;
        requestOptions.reason = "Manual request";
        initGUI();
    }

    public void displayTransactionInManualRequest(final int transactionID)
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
        if (transaction.getRequestWrapper().getNetworkHost() == null || transaction.getRequestWrapper().getNetworkHost().isEmpty())
        {
            networkHostText.setText(transaction.getRequestWrapper().getHost());
        }
        else
        {
            networkHostText.setText(transaction.getRequestWrapper().getNetworkHost());
        }

        if (transaction.getRequestWrapper().getNetworkPort() > 0)
        {
            networkPortText.setText(transaction.getRequestWrapper().getNetworkPort() + "");
        }
        else
        {
            networkPortText.setText("" + (transaction.getRequestWrapper().isSecure() ? 443 : 80));
        }
        sslCheck.setSelection(transaction.getRequestWrapper().isSecure());

        transactionComposite.clearData();
        transactionComposite.switchToParsedRequest();
        try
        {
            transactionComposite.updateRequestData(transaction.getRequestWrapper().getBytes());
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
            LOGGER.error(e.toString(), e);
            throw ise;
        }

    }

    protected void executeRequestButtonWidgetSelected(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        setExecuteButtonEnabled(false);
        try
        {
            transaction.execute();
            if (transaction.isSuccessfullExecution())
            {
                getDisplay().asyncExec(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        transactionComposite.updateResponseData(transaction.getResponseWrapper().getBytes());
                    }
                });
            }
        }
        catch (UnrequestableTransaction e)
        {
            MainWindow.getInstance().displayMessage("Invalid request", "The request cannot be sent.\n" + "\n" + "The error message was: " + e.toString(), true);
        }

        finally
        {
            setExecuteButtonEnabled(true);
        }
    }

    protected StandardHttpTransaction getTransaction()
    {
        try
        {
            StandardHttpTransaction transaction = transactionComposite.makeHttpRequest(TransactionSource.MANUAL_REQUEST);

            requestOptions.testTransaction = Scan.getScanSettings().isTestManualRequests();
            requestOptions.followRedirects = false;
            requestOptions.handleSessions = false;
            if (!networkHostText.getText().isEmpty())
            {
                transaction.getRequestWrapper().setNetworkHost(networkHostText.getText());
            }
            if (!networkPortText.getText().isEmpty())
            {
                int port = Integer.valueOf(networkPortText.getText());
                if (port > 65535)
                {
                    MainWindow.getInstance().displayMessage("Error:", "Port numbers must be less than 65536", true);
                    return null;
                }
                transaction.getRequestWrapper().setNetworkPort(port);
            }
            transaction.getRequestWrapper().setSecure(sslCheck.getSelection());
            transaction.setRequestOptions(requestOptions);
            return transaction;
        }
        catch (HttpFormatException e)
        {
            MainWindow.getInstance().displayMessage("Invalid request format",
                            "The format of the HTTP request is invalid. Future versions of Grendel-Scan will support " + "arbitrary request formats. For now, please use a format that resembles RFC 2616.\n" + "\n" + "The error message was: " + e.toString(),
                            true);
        }
        catch (IOException e)
        {
            MainWindow.getInstance().displayMessage("Error:", "Problem parsing request: " + e.toString(), true);
        }
        catch (HttpException e)
        {
            MainWindow.getInstance().displayMessage("Error:", "Problem parsing request: " + e.toString(), true);
        }
        return null;
    }

    public final TransactionComposite getTransactionComposite()
    {
        return transactionComposite;
    }

    private void initGUI()
    {
        FormLayout thisLayout = new FormLayout();
        setLayout(thisLayout);
        {
            sslCheck = new GButton(this, SWT.CHECK | SWT.LEFT);
            FormData sslCheckLData = new FormData();
            sslCheckLData.left = new FormAttachment(0, 1000, 420);
            sslCheckLData.top = new FormAttachment(0, 1000, 5);
            sslCheckLData.width = 42;
            sslCheckLData.height = 16;
            sslCheck.setLayoutData(sslCheckLData);
            sslCheck.setText("SSL");
        }
        {
            FormData networkPortTextLData = new FormData();
            networkPortTextLData.left = new FormAttachment(0, 1000, 357);
            networkPortTextLData.top = new FormAttachment(0, 1000, 5);
            networkPortTextLData.width = 39;
            networkPortTextLData.height = 13;
            networkPortText = new GText(this, SWT.BORDER);
            networkPortText.setLayoutData(networkPortTextLData);
            networkPortText.addVerifyListener(new EnforceDecimalNumbersOnly());
        }
        {
            networkPortabel = new GLabel(this, SWT.NONE);
            FormData networkPortabelLData = new FormData();
            networkPortabelLData.left = new FormAttachment(0, 1000, 327);
            networkPortabelLData.top = new FormAttachment(0, 1000, 5);
            networkPortabelLData.width = 24;
            networkPortabelLData.height = 13;
            networkPortabel.setLayoutData(networkPortabelLData);
            networkPortabel.setText("Port:");
        }
        {
            FormData networkHostTextLData = new FormData();
            networkHostTextLData.left = new FormAttachment(0, 1000, 161);
            networkHostTextLData.top = new FormAttachment(0, 1000, 5);
            networkHostTextLData.width = 148;
            networkHostTextLData.height = 19;
            networkHostText = new GText(this, SWT.BORDER);
            networkHostText.setLayoutData(networkHostTextLData);
        }
        {
            networkHostLabel = new GLabel(this, SWT.NONE);
            FormData networkHostLabelLData = new FormData();
            networkHostLabelLData.left = new FormAttachment(0, 1000, 87);
            networkHostLabelLData.top = new FormAttachment(0, 1000, 5);
            networkHostLabelLData.width = 68;
            networkHostLabelLData.height = 13;
            networkHostLabel.setLayoutData(networkHostLabelLData);
            networkHostLabel.setText("Network host:");
        }

        {
            executeRequestButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData executeRequestButtonLData = new FormData();
            executeRequestButtonLData.width = 69;
            executeRequestButtonLData.height = 27;
            executeRequestButtonLData.left = new FormAttachment(0, 1000, 12);
            executeRequestButtonLData.top = new FormAttachment(0, 1000, 0);
            executeRequestButton.setLayoutData(executeRequestButtonLData);
            executeRequestButton.setText("Execute");
            executeRequestButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent evt)
                {
                    final StandardHttpTransaction transaction = getTransaction();
                    if (transaction == null)
                    {
                        return;
                    }

                    Thread t = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                executeRequestButtonWidgetSelected(transaction);
                            }
                            catch (InterruptedScanException e)
                            {
                                LOGGER.info("Scan aborted: " + e.toString(), e);
                            }
                        }
                    });
                    t.start();
                }
            });
        }
        {
            testManualRequestsCheckbox = new GButton(this, SWT.CHECK | SWT.LEFT);
            FormData testManualRequestsCheckboxLData = new FormData();
            testManualRequestsCheckboxLData.width = 161;
            testManualRequestsCheckboxLData.height = 19;
            testManualRequestsCheckboxLData.left = new FormAttachment(0, 1000, 489);
            testManualRequestsCheckboxLData.top = new FormAttachment(0, 1000, 5);
            testManualRequestsCheckbox.setLayoutData(testManualRequestsCheckboxLData);
            testManualRequestsCheckbox.setText("Test manual requests");
        }
        {
            FormData transactionCompositeLData = new FormData();
            transactionCompositeLData.width = 803;
            transactionCompositeLData.height = 619;
            transactionCompositeLData.left = new FormAttachment(0, 1000, 0);
            transactionCompositeLData.top = new FormAttachment(0, 1000, 30);
            transactionCompositeLData.bottom = new FormAttachment(1000, 1000, -60);
            transactionCompositeLData.right = new FormAttachment(1000, 1000, 0);
            transactionComposite = new TransactionComposite(this, SWT.BORDER, true, false, true);
            transactionComposite.setLayoutData(transactionCompositeLData);
        }
        this.layout();
    }

    private void setExecuteButtonEnabled(final boolean enabled)
    {
        getDisplay().syncExec(new Runnable()
        {

            @Override
            public void run()
            {
                executeRequestButton.setEnabled(enabled);
            }
        });
    }

}
