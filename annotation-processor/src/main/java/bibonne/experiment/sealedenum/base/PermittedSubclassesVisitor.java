package bibonne.experiment.sealedenum.base;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor14;
import java.util.stream.Stream;

public class PermittedSubclassesVisitor extends SimpleTypeVisitor14<Stream<TypeMirror>, Void> {

    @Override
    protected Stream<TypeMirror> defaultAction(TypeMirror t, Void unused) {
        throw new UnsupportedOperationException("Can't process type " + t);
    }

    @Override
    public Stream<TypeMirror> visitDeclared(DeclaredType t, Void unused) {
        TypeElement typeElement = (TypeElement) t.asElement();
        if (isFinal(typeElement) || isNonSealed(typeElement)){
            return Stream.of(typeElement.asType());
        }
        return Stream.concat(typeElement.getPermittedSubclasses().stream()
                .flatMap(typeMirror -> typeMirror.accept(this, null)),
                Stream.of(typeElement.asType()));
    }

    static boolean isFinal(TypeElement typeElement) {
        return typeElement.getModifiers().contains(Modifier.FINAL);
    }

    static boolean isNonSealed(TypeElement typeElement) {
        return typeElement.getModifiers().contains(Modifier.NON_SEALED);
    }
}
