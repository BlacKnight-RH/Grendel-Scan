/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

/**
 * @author david
 * 
 */
public class UrlPathDataReference implements DataReference
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final boolean directory;
    public final static UrlPathDataReference DIRECTORY_COMPONENT = new UrlPathDataReference(true);
    public final static UrlPathDataReference FILENAME_COMPONENT = new UrlPathDataReference(false);

    private UrlPathDataReference(final boolean directory)
    {
        this.directory = directory;
    }

    @Override
    public UrlPathDataReference clone()
    {
        return new UrlPathDataReference(directory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
     */
    @Override
    public String debugString()
    {
        return directory ? "Is the directory component" : "Is the filename component";
    }

    public final boolean isDirectory()
    {
        return directory;
    }
}
