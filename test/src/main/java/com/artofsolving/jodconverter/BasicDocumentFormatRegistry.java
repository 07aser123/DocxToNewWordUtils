package com.artofsolving.jodconverter;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
* @author 作者:Administrator

* @createDate 创建时间：2019年5月30日 下午5:37:39
**/

public class BasicDocumentFormatRegistry implements DocumentFormatRegistry {
    private List documentFormats = new ArrayList();

    public BasicDocumentFormatRegistry() {
    }

    public void addDocumentFormat(DocumentFormat documentFormat) {
        this.documentFormats.add(documentFormat);
    }

    protected List getDocumentFormats() {
        return this.documentFormats;
    }

    public DocumentFormat getFormatByFileExtension(String extension) {
        if (extension == null) {
            return null;
        } else {
            if (extension.indexOf("doc") >= 0) {
                extension = "doc";
            }
            if (extension.indexOf("ppt") >= 0) {
                extension = "ppt";
            }
            if (extension.indexOf("xls") >= 0) {
                extension = "xls";
            }
            String lowerExtension = extension.toLowerCase();
            Iterator it = this.documentFormats.iterator();

            DocumentFormat format;
            do {
                if (!it.hasNext()) {
                    return null;
                }

                format = (DocumentFormat)it.next();
            } while(!format.getFileExtension().equals(lowerExtension));

            return format;
        }
    }

    public DocumentFormat getFormatByMimeType(String mimeType) {
        Iterator it = this.documentFormats.iterator();

        DocumentFormat format;
        do {
            if (!it.hasNext()) {
                return null;
            }

            format = (DocumentFormat)it.next();
        } while(!format.getMimeType().equals(mimeType));

        return format;
    }
}


