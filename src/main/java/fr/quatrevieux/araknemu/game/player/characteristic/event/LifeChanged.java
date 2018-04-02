package fr.quatrevieux.araknemu.game.player.characteristic.event;

/**
 * Event trigger when player life changed
 */
final public class LifeChanged {
    final private int last;
    final private int current;

    public LifeChanged(int last, int current) {
        this.last = last;
        this.current = current;
    }

    public int last() {
        return last;
    }

    public int current() {
        return current;
    }

    public int diff() {
        return current - last;
    }
}
