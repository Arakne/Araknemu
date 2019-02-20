package fr.quatrevieux.araknemu.data.value;

import java.util.Arrays;

/**
 * Colors value type
 */
final public class Colors {
    final static public Colors DEFAULT = new Colors(-1, -1, -1);

    final private int color1;
    final private int color2;
    final private int color3;

    public Colors(int color1, int color2, int color3) {
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    public int color1() {
        return color1;
    }

    public int color2() {
        return color2;
    }

    public int color3() {
        return color3;
    }

    /**
     * Get colors into an array
     */
    public int[] toArray() {
        return new int[] {color1, color2, color3};
    }

    /**
     * Get colors as hex array
     *
     * If color is default color (i.e. -1), "-1" will be generated
     */
    public String[] toHexArray() {
        return Arrays
            .stream(toArray())
            .mapToObj(value -> value == -1 ? "-1" : Integer.toHexString(value))
            .toArray(String[]::new)
        ;
    }
}
