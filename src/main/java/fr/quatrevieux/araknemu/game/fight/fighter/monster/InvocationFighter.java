package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import java.util.Optional;

import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

final public class InvocationFighter extends AbstractFighter {
    final private MonsterFighter fighter;
    final private Optional<PassiveFighter> invoker;

    public InvocationFighter(MonsterFighter fighter, PassiveFighter invoker) {
        this.fighter = fighter;
        this.invoker = Optional.of(invoker);
    }

    @Override
    public <O extends FighterOperation> O apply(O operation) {
        return fighter.apply(operation);
    }

    @Override
    public int level() {
        return fighter.level();
    }

    @Override
    public boolean ready() {
        return fighter.ready();
    }

    @Override
    public FightTeam team() {
        return fighter.team();
    }

    @Override
    public CastableWeapon weapon() {
        return fighter.weapon();
    }

    @Override
    public int id() {
        return fighter.id();
    }

    @Override
    public Sprite sprite() {
        return fighter.sprite();
    }

    @Override
    public SpellList spells() {
        return fighter.spells();
    }

    @Override
    public FighterCharacteristics characteristics() {
        return fighter.characteristics();
    }

    @Override
    public FighterLife life() {
        return fighter.life();
    }

    @Override
    public Optional<PassiveFighter> invoker() {
        return invoker;
    }
}
