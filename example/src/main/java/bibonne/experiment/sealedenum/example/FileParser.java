package bibonne.experiment.sealedenum.example;

import bibonne.experiment.sealedenum.base.SealedEnum;

import java.util.List;

@SealedEnum
public sealed interface FileParser permits StructuredFileParser, TableFileParser, XmlFileParser {

    List<String> parse(String filename);

    default String testEnum(FileParserEnum fileParserEnum){
       return switch (fileParserEnum){
            case XmlFileParser -> "xml" ;
            case JsonFileParser  -> "json" ;
            case StructuredFileParser -> "structured" ;
            case YamlFileParser -> "yaml" ;
            case TableFileParser -> "table" ;
        };
    }

}
