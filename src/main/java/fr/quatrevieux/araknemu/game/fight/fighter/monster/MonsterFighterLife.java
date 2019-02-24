package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;

/**
 * Handle life points for {@link MonsterFighter}
 */
final public class MonsterFighterLife implements FighterLife {
    final private Fighter fighter;

    private int max;
    private int current;
    private boolean dead = false;

    public MonsterFighterLife(Fighter fighter, int life) {
        this.max = life;
        this.current = life;
        this.fighter = fighter;
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
