/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.turn.action.FightAction;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.stream.Collectors;

/**
 * Implementation of {@link AiActionFactory} which provide bridge with actual fight actions factory
 * Generated actions can be applied directly to fight
 */
public final class FightAiActionFactoryAdapter implements AiActionFactory<FightAction> {
    private final PlayableFighter fighter;
    private final Fight fight;
    private final ActionsFactory actionFactory;

    public FightAiActionFactoryAdapter(PlayableFighter fighter, Fight fight, ActionsFactory actionFactory) {
        this.fighter = fighter;
        this.fight = fight;
        this.actionFactory = actionFactory;
    }

    @Override
    @SuppressWarnings("argument") // Cell ID is valid
    public FightAction cast(Spell spell, BattlefieldCell target) {
        return actionFactory.cast().create(fighter, spell, fight.map().get(target.id()));
    }

    @Override
    @SuppressWarnings("argument") // Cell ID is valid
    public FightAction move(Path<BattlefieldCell> path) {
        // Recreate path with actual fight cell
        final FightMap map = fight.map();
        final Path<FightCell> actualPath = new Path<>(
            map.decoder(),
            path.stream()
                .map(step -> new PathStep<>(map.get(step.cell().id()), step.direction()))
                .collect(Collectors.toList())
        );

        return actionFactory.move().create(fighter, actualPath);
    }

    @Override
    public CastConstraintValidator<Spell> castSpellValidator() {
        return actionFactory.cast().validator();
    }
}
