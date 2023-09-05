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

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.arakne.utils.maps.BattlefieldCell;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.util.Asserter;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulate monster invocation effect
 *
 * This simulator will compute high score for invocation with more effective spells, life points and distance from enemy (depending on if it's a healer or not).
 *
 * The score is the sum of :
 * - monster life points
 * - average effect of its spells, multiplied by the number of times it can be cast
 * - distance from enemy (if it's a healer, prioritize away from enemy, else prioritize close to enemy)
 *
 * If the invocation will be killed by its own spells, and it cannot reach the enemy, its score will be set to 1.0
 * If the invocation can (theoretically) attack the enemy (by taking in account the range of attack spells and its movement points),
 * the theoretical damage will be set as enemy damage on simulator, so {@link fr.quatrevieux.araknemu.game.fight.ai.action.Attack} can use an invocation as attack.
 * If the invocation perform healing, the theoretical heal will be added to simulator.
 *
 * Note: characteristics will not be taken in account directly on score, but via spells effects.
 *
 * @see CastSimulation#invocation() The base invocation score
 * @see CastSimulation#enemiesLife() Will be set if the invocation can directly attack the enemy
 * @see CastSimulation#killedEnemies() Same as above, but set only if the theoritical damage are higher than the enemy life
 * @see CastSimulation#selfLife() When the invocation is healer. Theoritical heal effect will be set here
 */
public final class InvokeMonsterSimulator implements EffectSimulator {
    private final Map<Monster, SpellScore> spellScoreCache = new HashMap<>();

    private final MonsterService monsterService;
    private final Simulator simulator;

    public InvokeMonsterSimulator(MonsterService monsterService, Simulator simulator) {
        this.monsterService = monsterService;
        this.simulator = simulator;
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final Monster monster = monsterService
            .load(effect.effect().min())
            .get(effect.effect().max())
        ;

        final int monsterMp = monster.characteristics().get(Characteristic.MOVEMENT_POINT);
        final int enemyDistance = ai.enemy().map(enemy -> enemy.cell().coordinate().distance(simulation.target())).orElse(-1);

        final int baseScore = monster.life();
        final SpellScore spellsScore = monsterSpellsScore(monster);

        final boolean canAttackEnemy = enemyDistance != -1 && enemyDistance <= spellsScore.attackRange() + monsterMp;

        // The invocation behave like an attack, but can't reach enemy
        // So set a score of 1 to only mark it as invocation with low priority
        if (spellsScore.isSuicide() && !canAttackEnemy) {
            simulation.addInvocation(1.0);
            return;
        }

        // The invocation can attack enemy, so consider it as an attack
        if (spellsScore.isAttack() && canAttackEnemy) {
            ai.enemy().ifPresent(enemy -> simulation.addDamage(Interval.of(spellsScore.damage()), enemy));
        }

        if (spellsScore.isHeal()) {
            // @todo target lowest HP ally ?
            simulation.addHeal(Interval.of(spellsScore.heal()), ai.fighter());
        }

        final int distanceScore;

        // When invocation is a healer, prioritize target away from enemy,
        // else prioritize target close to enemy
        if (enemyDistance == -1) {
            distanceScore = 0;
        } else if (spellsScore.isHeal()) {
            distanceScore = enemyDistance;
        } else {
            distanceScore = -enemyDistance;
        }

        simulation.addInvocation(baseScore + spellsScore.score() + distanceScore);
    }

    /**
     * Get or compute the score of monster spells
     */
    private SpellScore monsterSpellsScore(Monster monster) {
        return spellScoreCache.computeIfAbsent(monster, this::computeMonsterSpellsScore);
    }

    private SpellScore computeMonsterSpellsScore(Monster monster) {
        final SpellScore aggregatedSpellsScore = new SpellScore();
        final int monsterAp = monster.characteristics().get(Characteristic.ACTION_POINT);

        for (Spell spell : monster.spells()) {
            final int spellApCost = spell.apCost();

            if (monsterAp < spellApCost) {
                continue;
            }

            final SpellScore currentSpellScore = simulator.score(spell, monster.characteristics());

            // If the attack is suicidal, it can't be used multiple times, so don't multiply its score
            if (!currentSpellScore.isSuicide()) {
                currentSpellScore.multiply(Asserter.castPositive(monsterAp / spellApCost));
            }

            aggregatedSpellsScore.merge(currentSpellScore);
        }

        return aggregatedSpellsScore;
    }
}
