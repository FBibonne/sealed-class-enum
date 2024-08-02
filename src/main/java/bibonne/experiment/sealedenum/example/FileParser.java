package bibonne.experiment.sealedenum.example;

import java.util.List;

public sealed interface FileParser permits CsvFileParser, JsonFileParser {

    List<String> parse(String filename);

}
