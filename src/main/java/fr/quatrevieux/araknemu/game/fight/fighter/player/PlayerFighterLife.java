package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.player.characteristic.Life;

/**
 * Handle life points for {@link PlayerFighter}
 *
 * The life points will be saved when fight started
 */
final public class PlayerFighterLife implements FighterLife {
    final private Life baseLife;

    private int max;
    private int current;
    private boolean initialised = false;

    public PlayerFighterLife(Life baseLife) {
        this.baseLife = baseLife;
    }

    @Override
    public int current() {
        return initialised ? current : baseLife.current();
    }

    @Override
    public int max() {
        return initialised ? max : baseLife.max();
    }

    /**
     * Initialise the fighter life when fight is started
     */
    public void init() {
        if (initialised) {
            throw new IllegalStateException("Player fighter life is already initialised");
        }

        max = baseLife.max();
        current = baseLife.current();

        initialised = true;
    }
}
