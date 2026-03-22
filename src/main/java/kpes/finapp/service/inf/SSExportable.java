package kpes.finapp.service.inf;

import java.nio.file.Path;

/**
 * Interface for saving bank statements to spreadsheet
 * @author Krizzia Santillan
 */
public interface SSExportable {

    void saveToSpreadSheet(Path p);

}
