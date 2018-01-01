package fr.quatrevieux.araknemu.data.value;

/**
 * Dimensions for 2D object
 */
final public class Dimensions {
    final private int width;
    final private int height;

    public Dimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return equals((Dimensions) o);
    }

    public boolean equals(Dimensions other) {
        return other != null && other.height == height && other.width == width;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }
}
