package com.grendelscan.commons.http.transactions;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.factories.HttpRequestFactory;
import com.grendelscan.commons.http.wrappers.HttpRequestWrapper;

public class NonScanHttpTransaction
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 256619227298731217L;
    private byte requestBody[];
    private byte responseBody[];

    private final HttpUriRequest request;
    private HttpResponse response;
    private final String uri;

    public NonScanHttpTransaction(final String method, final String uri)
    {
        this.uri = uri;
        request = (HttpUriRequest) HttpRequestFactory.makeNonScanRequest(method, this.uri, null, null, HttpRequestWrapper.DEFAULT_PROTOCL_VERSION);
    }

    public void addRequestHeader(final Header header)
    {
        request.addHeader(header);
    }

    public void addRequestHeader(final String name, final String value)
    {
        request.addHeader(name, value);
    }

    public void execute(final HttpClient client) throws IOException
    {
        int retries = 0;
        while (response == null && retries++ <= Scan.getScanSettings().getMaxRequestRetries())
        {
            response = client.execute(request);
            responseBody = HttpUtils.entityToByteArray(response.getEntity(), 0);
        }
    }

    public byte[] getRequestBody()
    {
        return requestBody;
    }

    public HttpResponse getResponse()
    {
        return response;
    }

    public byte[] getResponseBody()
    {
        return responseBody;
    }

}
