package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;

/**
 * Handle life points for fighters
 */
final public class BaseFighterLife implements FighterLife {
    final private Fighter fighter;

    private int max;
    private int current;
    private boolean dead = false;

    public BaseFighterLife(Fighter fighter, int life, int max) {
        this.max = max;
        this.current = life;
        this.fighter = fighter;
    }

    public BaseFighterLife(Fighter fighter, int life) {
        this(fighter, life, life);
    }

    @Override
    public int current() {
        return current;
    }

    @Override
    public int max() {
        return max;
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
}
