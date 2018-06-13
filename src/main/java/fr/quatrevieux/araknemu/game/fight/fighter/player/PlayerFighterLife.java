package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import fr.quatrevieux.araknemu.game.player.characteristic.Life;

/**
 * Handle life points for {@link PlayerFighter}
 *
 * The life points will be saved when fight started
 */
final public class PlayerFighterLife implements FighterLife {
    final private Life baseLife;
    final private Fighter fighter;

    private int max;
    private int current;
    private boolean initialised = false;
    private boolean dead = false;

    public PlayerFighterLife(Life baseLife, Fighter fighter) {
        this.baseLife = baseLife;
        this.fighter = fighter;
    }

    @Override
    public int current() {
        return initialised ? current : baseLife.current();
    }

    @Override
    public int max() {
        return initialised ? max : baseLife.max();
    }

    @Override
    public boolean dead() {
        return dead;
    }

    @Override
    public int alter(Fighter caster, int value) {
        if (dead) {
            return 0;
        }

        if (value < -current) {
            value = -current;
        } else if (value > max - current) {
            value = max - current;
        }

        current += value;

        fighter.fight().dispatch(new FighterLifeChanged(fighter, caster, value));

        if (current == 0) {
            dead = true;
            fighter.fight().dispatch(new FighterDie(fighter, caster));
        }

        return value;
    }

    @Override
    public void kill(Fighter caster) {
        current = 0;
        dead = true;
        fighter.fight().dispatch(new FighterDie(fighter, caster));
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
