package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.BaseFighterLife;
import fr.quatrevieux.araknemu.game.world.creature.Life;

/**
 * Handle life points for {@link PlayerFighter}
 *
 * The life points will be saved when fight started
 */
final public class PlayerFighterLife implements FighterLife {
    final private Life baseLife;
    final private Fighter fighter;

    private BaseFighterLife delegate;

    public PlayerFighterLife(Life baseLife, Fighter fighter) {
        this.baseLife = baseLife;
        this.fighter = fighter;
    }

    @Override
    public int current() {
        return delegate != null ? delegate.current() : baseLife.current();
    }

    @Override
    public int max() {
        return delegate != null ? delegate.max() : baseLife.max();
    }

    @Override
    public boolean dead() {
        return delegate != null && delegate.dead();
    }

    @Override
    public int alter(Fighter caster, int value) {
        if (delegate == null) {
            throw new IllegalStateException("PlayerFighterLife must be initialized");
        }

        return delegate.alter(caster, value);
    }

    @Override
    public void kill(Fighter caster) {
        if (delegate == null) {
            throw new IllegalStateException("PlayerFighterLife must be initialized");
        }

        delegate.kill(caster);
    }

    /**
     * Initialise the fighter life when fight is started
     */
    public void init() {
        if (delegate != null) {
            throw new IllegalStateException("Player fighter life is already initialised");
        }

        delegate = new BaseFighterLife(fighter, baseLife.current(), baseLife.max());
    }
}
