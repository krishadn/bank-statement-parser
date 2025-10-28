package kpes.finapp.service;

import java.io.IOException;

public interface ITextExtractor {

    /**
     * Interface for text extractor from an external file
     * @param forExtraction external file from where the text will be extracted
     * @param credentials to use for opening encrypted files
     * @return text extracted from the file
     * @throws IOException if there is a problem in accessing the external file
     */
    public abstract String extractText(Object forExtraction, Object credentials) throws IOException;
    
}
