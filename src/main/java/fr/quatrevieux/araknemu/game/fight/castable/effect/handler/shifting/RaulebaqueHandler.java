package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.module.RaulebaqueModule;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;

/**
 * Handle the Raulebaque effect
 */
final public class RaulebaqueHandler implements EffectHandler {
    final private Fight fight;
    final private RaulebaqueModule module;

    public RaulebaqueHandler(Fight fight, RaulebaqueModule module) {
        this.fight = fight;
        this.module = module;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        module.startPositions().forEach((fighter, startCell) -> {
            if (fighter.dead()) {
                return;
            }

            FightCell lastCell = fighter.cell();

            if (lastCell.equals(startCell)) {
                return;
            }

            fighter.move(null);

            // Cell is not free : exchange place
            startCell.fighter().ifPresent(other -> other.move(lastCell));
            fighter.move(startCell);
        });

        fight.send(new FighterPositions(fight.fighters()));
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use Raulebaque as buff effect");
    }
}
