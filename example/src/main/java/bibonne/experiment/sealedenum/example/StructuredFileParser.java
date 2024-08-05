package bibonne.experiment.sealedenum.example;

import java.util.List;

public abstract sealed class StructuredFileParser implements FileParser {

    public static final class YamlFileParser extends StructuredFileParser {
        @Override
        public List<String> parse(String filename) {
            return List.of();
        }
    }

}

final class JsonFileParser extends StructuredFileParser {
    @Override
    public List<String> parse(String filename) {
        return List.of();
    }
}
