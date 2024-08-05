package bibonne.experiment.sealedenum.base;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor14;

public class TypeElementVisitor extends SimpleTypeVisitor14<TypeElement, Void> {

    @Override
    protected TypeElement defaultAction(TypeMirror t, Void unused) {
        throw new UnsupportedOperationException("Can't process type " + t);
    }

    @Override
    public TypeElement visitDeclared(DeclaredType t, Void unused) {
        return (TypeElement) t.asElement();
    }

}