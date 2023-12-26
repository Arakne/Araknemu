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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.module.MonsterInvocationModule;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvokeTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushFunctionalSpells()
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;

        action = new Invoke(container.get(Simulator.class));
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(35, 5))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        assertCast(35, 108);
    }

    @Test
    void shouldPrioritizeInvocationWhichCanReachEnemy() {
        // Tofu can reach target, not bouftou
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(35, 1).spell(34, 5))
            .addEnemy(builder -> builder.player(other).cell(111))
        );

        assertCast(34, 108);

        // Bouftou can reach target, and have better characteristics
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(35, 1).spell(34, 5))
            .addEnemy(builder -> builder.player(other).cell(166))
        );

        assertCast(35, 136);
    }

    @Test
    void shouldPrioritizeDirectKill() {
        // Tofu can reach target, not bouftou
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(35, 5).spell(34, 5))
            .addEnemy(builder -> builder.player(other).cell(111).maxLife(200).currentLife(10))
        );

        assertCast(34, 108);
    }

    @Test
    void shouldPrioritizeHealWhenAlliesAreLowLife() {
        // Allies are full life : no bonus for healing
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(190, 4).spell(39, 4))
            .addEnemy(builder -> builder.player(other).cell(144))
        );

        assertCast(39, 108);

        // Allies are low life : bonus for healing
        player.fighter().life().alter(player.fighter(), -95);
        action.initialize(ai); // Recompute allies life ratio
        assertCast(190, 107);
    }

    @Test
    void noInvocationSpellAvailableShouldDoNothing() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void reachMaxInvocationCountShouldDoNothing() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(190, 4))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        fight.register(new MonsterInvocationModule(
            container.get(MonsterService.class),
            container.get(FighterFactory.class),
            fight
        ));

        fight.fighters().joinTurnList(new DoubleFighter(-42, player.fighter()), fight.map().get(107));

        assertDotNotGenerateAction();
    }

    @Test
    void notEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(190, 4))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        turn.points().useActionPoints(5);
        assertDotNotGenerateAction();
    }
}
