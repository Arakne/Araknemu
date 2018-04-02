package fr.quatrevieux.araknemu.game.player.sprite;

/**
 * Store size of the sprite
 */
final public class SpriteSize {
    final private int x;
    final private int y;

    public SpriteSize(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public String toString() {
        return x + "x" + y;
    }
}
