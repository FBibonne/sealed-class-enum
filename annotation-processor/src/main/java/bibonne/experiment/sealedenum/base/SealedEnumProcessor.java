package bibonne.experiment.sealedenum.base;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SupportedAnnotationTypes("bibonne.experiment.sealedenum.base.SealedEnum")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class SealedEnumProcessor extends AbstractProcessor {

    private Set<? extends TypeElement> targetTypeElement;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (targetTypeElement.equals(annotations)) {
            doProcess(roundEnv);
            return true;
        }
        return false;
    }

    private void doProcess(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(SealedEnum.class).forEach(this::processElement);
    }

    private void processElement(Element element) {
        if (element instanceof TypeElement typeElement) {
            var allSubTypes = findAllSubTypes(typeElement);
            if (!allSubTypes.isEmpty()) {
                createEnumFor(typeElement, allSubTypes);
            }
        }
    }

    private List<TypeMirror> findAllSubTypes(TypeElement typeElement) {
        PermittedSubclassesVisitor declaredTypeVisitor = new PermittedSubclassesVisitor();
        return typeElement.getPermittedSubclasses().stream()
                .flatMap(typeMirror -> typeMirror.accept(declaredTypeVisitor, null))
                .toList();
    }

    private void createEnumFor(TypeElement typeElement, List<? extends TypeMirror> permitedTypes) {
        TypeElementVisitor typeElementVisitor = new TypeElementVisitor();
        Filer filer = processingEnv.getFiler();
        ElementName elementName = ElementName.of(typeElement);
        String simpleEnumName = elementName.simpleName() + "Enum";
        try (var writer = new PrintWriter(filer.createSourceFile(fullyQualifiedName(elementName.packageName(), simpleEnumName)).openWriter());) {
            printPackage(elementName.packageName(), writer);
            printEnumDeclaration(simpleEnumName, writer);
            printEnumConstants(toSimpleNames(permitedTypes, typeElementVisitor), writer);
            printEnumBody(elementName.simpleName(), simpleEnumName, writer);
            printCloseEnum(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printEnumBody(String simpleElementName, String simpleEnumName, PrintWriter writer) {
        /*
          Class<? extends SealedClass<?>> clazz;
          
          SealedClassEnum(Class<? extends SealedClass<?>> clazz){
              this.clazz = clazz;
          }
          
          public Class<? extends SealedClass<?>> getType(){
              return clazz;
          }
        */
        writer.println("""
                Class<? extends %s> clazz;
                %s(Class<? extends %s> clazz){
                    this.clazz = clazz;
                }
                public Class<? extends %s> getType(){
                    return clazz;
                }
                """.formatted(simpleElementName, simpleEnumName, simpleElementName, simpleElementName));
    }

    private void printEnumConstants(Stream<String> simpleNames, PrintWriter writer) {
        writer.print(simpleNames.collect(Collectors.joining(",")));
        writer.println(";");
    }

    private Stream<String> toSimpleNames(List<? extends TypeMirror> permitedTypes, TypeElementVisitor typeElementVisitor) {
        return permitedTypes.stream()
                .map(typeMirror -> typeMirror.accept(typeElementVisitor, null))
                .map(typeElement -> typeElement.getSimpleName() + "(" + typeElement.getQualifiedName() + ".class)");
        //ex:  StringDecoder(ParameterValueDecoder.StringDecoder.class)
    }

    private void printCloseEnum(PrintWriter writer) {
        writer.println("}");
    }

    private void printEnumDeclaration(String enumName, PrintWriter writer) {
        writer.println("public enum " + enumName + " {");
    }

    private void printPackage(Optional<String> packageName, PrintWriter writer) {
        packageName.ifPresent(name -> writer.println("package " + name + ";"));
    }

    private CharSequence fullyQualifiedName(Optional<String> packageName, String simpleEnumName) {
        return packageName.map(name -> name + "." + simpleEnumName).orElse(simpleEnumName);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        TypeElement sealedEnumTypeElement = processingEnv.getElementUtils().getTypeElement(SealedEnum.class.getCanonicalName());
        targetTypeElement = Set.of(sealedEnumTypeElement);
    }

    private record ElementName(Optional<String> packageName, String simpleName) {

        private static final Pattern namePattern = Pattern.compile("^(.*)\\.([^.]*)$");

        public static ElementName of(TypeElement typeElement) {
            String typeElementFullyQualifiedName = typeElement.getQualifiedName().toString();
            Matcher matcher = namePattern.matcher(typeElementFullyQualifiedName);
            if (matcher.matches()) {
                // the regular exepression matches so there is a non empty package :
                return new ElementName(Optional.of(matcher.group(1)), matcher.group(2));
            } else {
                return new ElementName(Optional.empty(), typeElementFullyQualifiedName);
            }
        }
    }
}
