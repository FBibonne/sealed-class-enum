package bibonne.experiment.sealedenum.example;

import java.util.List;

public record CsvFileParser() implements FileParser {
    @Override
    public List<String> parse(String filename) {
        return List.of();
    }
}
