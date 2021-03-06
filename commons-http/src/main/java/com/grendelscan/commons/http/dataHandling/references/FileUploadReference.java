/**
 * 
 */
package com.grendelscan.commons.http.dataHandling.references;

/**
 * @author dbyrne
 * 
 */
public class FileUploadReference implements DataReference
{

    public enum Part
    {
        filename, mime, filecontent, fieldname
    }

    private static final long serialVersionUID = 1L;
    private final Part part;

    public static final FileUploadReference FIELD_NAME = new FileUploadReference(Part.fieldname);
    public static final FileUploadReference FILENAME = new FileUploadReference(Part.filename);
    public static final FileUploadReference MIME_TYPE = new FileUploadReference(Part.mime);
    public static final FileUploadReference FILE_CONTENT = new FileUploadReference(Part.filecontent);

    private FileUploadReference(final Part part)
    {
        this.part = part;
    }

    @Override
    public FileUploadReference clone()
    {
        return new FileUploadReference(part);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.references.DataReference#debugString()
     */
    @Override
    public String debugString()
    {
        switch (part)
        {
            case mime:
                return "Is the MIME type";
            case filecontent:
                return "Is the file content";
            case filename:
                return "Is the file name";
            case fieldname:
                return "Is the field name";

        }
        return "";
    }

    public Part getPart()
    {
        return part;
    }

    public final boolean isFieldName()
    {
        return part == Part.fieldname;
    }

    public final boolean isFileContent()
    {
        return part == Part.filecontent;
    }

    public final boolean isFilename()
    {
        return part == Part.filename;
    }

    public final boolean isMime()
    {
        return part == Part.mime;
    }

}
