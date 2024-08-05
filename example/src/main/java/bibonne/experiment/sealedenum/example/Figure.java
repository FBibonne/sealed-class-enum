package bibonne.experiment.sealedenum.example;

import bibonne.experiment.sealedenum.base.SealedEnum;

public sealed class Figure{ }

@SealedEnum
final class Circle extends Figure {
    float radius;
}
non-sealed class Square extends Figure {
    float side;
}
sealed class Rectangle extends Figure {
    float length, width;
}
final class FilledRectangle extends Rectangle {
    int red, green, blue;
}
