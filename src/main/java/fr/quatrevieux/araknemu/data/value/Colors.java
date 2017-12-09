package fr.quatrevieux.araknemu.data.value;

/**
 * Colors value type
 */
final public class Colors {
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
}
