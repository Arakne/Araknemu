package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.characteristic.event.LifeChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Handle player life
 */
final public class Life {
    final private GamePlayer player;
    final private Player entity;

    private int max;

    public Life(GamePlayer player, Player entity) {
        this.player = player;
        this.entity = entity;

        init();
    }

    /**
     * Get the maximum player life
     */
    public int max() {
        return max;
    }

    /**
     * Get the current life point
     */
    public int current() {
        return entity.life();
    }

    /**
     * Get the current percent life
     */
    public byte percent() {
        return (byte) (100 * current() / max());
    }

    /**
     * Add to player current life
     *
     * @param value Value to add. If the value is upper than remaining life, only the remaining life will be added
     */
    public void add(int value) {
        set(value + current());
    }

    /**
     * Change the current life
     *
     * @param value The new life value
     */
    public void set(int value) {
        if (value < 0) {
            value = 0;
        } else if (value > max) {
            value = max;
        }

        int last = current();

        entity.setLife(value);
        player.dispatch(new LifeChanged(last, value));
    }

    /**
     * Check if the player is full life
     */
    public boolean isFull() {
        return max() == current();
    }

    /**
     * Rebuild the life points
     */
    public void rebuild() {
        int percent = percent();

        max = computeMaxLife();
        entity.setLife(max * percent / 100);
    }

    private int computeMaxLife() {
        return player.race().life(entity.level()) + player.characteristics().get(Characteristic.VITALITY);
    }

    private void init() {
        max = computeMaxLife();

        if (current() < 0 || current() > max) {
            entity.setLife(max);
        }
    }
}
