package fr.quatrevieux.araknemu.data.value;

/**
 * Position object into the world
 */
final public class Position {
    final private int map;
    final private int cell;

    public Position(int map, int cell) {
        this.map = map;
        this.cell = cell;
    }

    public int map() {
        return map;
    }

    public int cell() {
        return cell;
    }

    public boolean isNull() {
        return map == 0 && cell == 0;
    }

    @Override
    public boolean equals(Object o) {
        return
            this == o
            || (o instanceof Position && equals((Position) o))
        ;
    }

    public boolean equals(Position other) {
        return other != null && other.cell == cell && other.map == map;
    }

    @Override
    public int hashCode() {
        int result = map;
        result = 31 * result + cell;
        return result;
    }

    @Override
    public String toString() {
        return "(" + map + ", " + cell + ")";
    }
}
