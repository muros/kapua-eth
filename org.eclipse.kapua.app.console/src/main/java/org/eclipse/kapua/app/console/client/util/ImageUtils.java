package org.eclipse.kapua.app.console.client.util;

import com.google.gwt.resources.client.ImageResource;

public class ImageUtils
{
    public static String toHTML(ImageResource ir, String altText)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<img src='")
          .append(ir.getSafeUri().asString())
          .append("' alt='")
          .append(altText)
          .append("' title='")
          .append(altText)
          .append("'/>");
        return sb.toString();
    }

    public static String toHTML(ImageResource ir, String altText, int size)
    {
        String sizeInt = String.valueOf(size);
        return toHTML(ir, altText, sizeInt, sizeInt);
    }

    public static String toHTML(ImageResource ir, String altText, String size)
    {
        return toHTML(ir, altText, size, size);
    }

    public static String toHTML(ImageResource ir, String altText, String width, String height)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<img src='")
          .append(ir.getSafeUri().asString())
          .append("' alt='")
          .append(altText)
          .append("' title='")
          .append(altText)
          .append("' width='")
          .append(width)
          .append("' height='")
          .append(height)
          .append("'/>");
        return sb.toString();
    }
}
