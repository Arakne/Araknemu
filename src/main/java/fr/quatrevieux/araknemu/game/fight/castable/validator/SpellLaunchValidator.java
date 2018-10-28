package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Check the launched spell list for validate cooldown, launch count and launch per target
 */
public class SpellLaunchValidator implements CastConstraintValidator<Spell> {
    @Override
    public Error validate(FightTurn turn, Spell castable, FightCell target) {
        LaunchedSpells history = turn.fighter().attachment(LaunchedSpells.class);

        if (history != null && !history.valid(castable, target)) {
            return Error.cantCast();
        }

        return null;
    }
}
