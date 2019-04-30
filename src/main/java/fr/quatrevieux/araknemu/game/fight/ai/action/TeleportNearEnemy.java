package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Try to teleport near enemy
 */
final public class TeleportNearEnemy implements ActionGenerator {
    private SpellConstraintsValidator validator;
    private List<Spell> teleportSpells;

    @Override
    public void initialize(AI ai) {
        validator = new SpellConstraintsValidator(ai.turn());
        teleportSpells = new ArrayList<>();

        for (Spell spell : ai.fighter().spells()) {
            if (spell.effects().stream().anyMatch(spellEffect -> spellEffect.effect() == 4)) {
                teleportSpells.add(spell);
            }
        }

        teleportSpells.sort(Comparator.comparingInt(Castable::apCost));
    }

    @Override
    public Optional<Action> generate(AI ai) {
        if (teleportSpells.isEmpty()) {
            return Optional.empty();
        }

        final int actionPoints = ai.turn().points().actionPoints();

        if (actionPoints < 1) {
            return Optional.empty();
        }

        Optional<Fighter> enemy = ai.enemy();

        if (!enemy.isPresent()) {
            return Optional.empty();
        }

        final CoordinateCell<FightCell> targetCell = new CoordinateCell<>(enemy.get().cell());
        int bestDistance = new CoordinateCell<>(ai.fighter().cell()).distance(targetCell);

        // Already at adjacent cell of the enemy
        if (bestDistance <= 1) {
            return Optional.empty();
        }

        FightCell bestCell = ai.fighter().cell();
        Spell bestSpell = null;

        for (Spell spell : teleportSpells) {
            if (spell.apCost() > actionPoints) {
                continue;
            }

            for (int cellId = 0; cellId < ai.fight().map().size(); ++cellId) {
                FightCell testCell = ai.fight().map().get(cellId);

                // Target or launch is not valid
                if (!testCell.walkable() || validator.validate(spell, testCell) != null) {
                    continue;
                }

                int distance = new CoordinateCell<>(testCell).distance(targetCell);

                if (distance < bestDistance) {
                    bestSpell = spell;
                    bestCell = testCell;
                    bestDistance = distance;
                }

                // Adjacent cell found : no need to continue
                if (distance == 1) {
                    break;
                }
            }
        }

        if (bestSpell == null) {
            return Optional.empty();
        }

        return Optional.of(new Cast(ai.turn(), ai.fighter(), bestSpell, bestCell));
    }
}
