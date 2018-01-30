package fr.quatrevieux.araknemu.data.living.entity.environment;

import fr.quatrevieux.araknemu.data.constant.Alignment;

/**
 * Map subareas. Contains alignment
 */
final public class SubArea {
    final private int id;
    final private int area;
    final private String name;
    final private boolean conquestable;

    private Alignment alignment;

    public SubArea(int id, int area, String name, boolean conquestable, Alignment alignment) {
        this.id = id;
        this.area = area;
        this.name = name;
        this.conquestable = conquestable;
        this.alignment = alignment;
    }

    public int id() {
        return id;
    }

    public int area() {
        return area;
    }

    public String name() {
        return name;
    }

    public boolean conquestable() {
        return conquestable;
    }

    public Alignment alignment() {
        return alignment;
    }
}
