package com.grendelscan.commons.http.factories;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.commons.http.URIStringUtils;

public class UriFactory
{
    private static Pattern cleanupUriPattern = Pattern.compile("^(\\w+://)?+(.+)$");

    private static void checkUriValidity(final String uriString) throws URISyntaxException
    {
        if (uriString == null || uriString.equals(""))
        {
            throw new URISyntaxException("", "The string is null or empty.");
        }

        if (uriString.length() > 11 && uriString.trim().substring(0, 11).toLowerCase().equals("javascript:"))
        {
            throw new URISyntaxException(uriString, "The string is a JavaScript command, not a URI.");
        }
    }

    private static String cleanupUri(final String uri)
    {
        String newUri = uri;
        Matcher m = cleanupUriPattern.matcher(uri);
        if (m.matches())
        {
            String method = m.group(1);
            if (method == null)
            {
                method = "";
            }
            String rest = m.group(2);
            if (rest == null)
            {
                rest = "";
            }
            newUri = method + rest.replace("//", "/");
        }
        if (newUri.length() > 8)
        {
            String lower = newUri.substring(0, 8).toLowerCase();
            if ((lower.subSequence(0, 7).equals("http://") || lower.equals("https://")) && newUri.lastIndexOf('/') <= 7)
            {
                newUri += "/";
            }
        }
        // if (!newUri.equals(uri))
        // {
        // System.out.print("changed");
        // }
        return newUri;
    }

    public static URI makeAbsoluteUri(final String uriString, final String baseUri) throws URISyntaxException
    {
        return makeAbsoluteUri(uriString, new URI(URIStringUtils.escapeUri(baseUri)));
    }

    public static URI makeAbsoluteUri(final String uriString, final URI baseUri) throws URISyntaxException
    {
        checkUriValidity(uriString);
        URI uri = new URI(URIStringUtils.escapeUri(cleanupUri(uriString)));

        if (!uri.isAbsolute())
        {
            if (baseUri.getPath().equals("") && !uriString.substring(0, 1).equals("/"))
            {
                uri = baseUri.resolve("/" + uri.toASCIIString());
            }
            else
            {
                uri = baseUri.resolve(uri);
            }
        }

        return uri.normalize();
    }

    /**
     * Tries to prevent problems by intelligently escaping a URL.
     * 
     * @param uriString
     * @return
     */
    public static URI makeUri(final String uriString, final boolean requireAbsolute) throws URISyntaxException
    {
        checkUriValidity(uriString);
        String newUri = URIStringUtils.escapeUri(cleanupUri(uriString));
        // if (!newUri.equals(uriString))
        // {
        // System.out.print("changed");
        // }
        URI uri = new URI(newUri);
        if (requireAbsolute && !uri.isAbsolute())
        {
            throw new URISyntaxException(uriString, "The URI was required to be absolute.");
        }
        return uri.normalize();
    }

}
