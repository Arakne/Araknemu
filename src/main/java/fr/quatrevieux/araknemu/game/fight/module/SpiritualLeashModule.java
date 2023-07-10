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

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations.InvokeLastDeadFighterHandler;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Module for register effect handler of spiritual leash spell
 * This module will listen for fighter death to get the fighter to resuscitate
 *
 * Fighters removed from fight (e.g. leaving the fight) will also be removed from dead fighters queue.
 * Actual fighters will always be prioritized compared with invocations.
 *
 * Static invocations will be ignored.
 */
public final class SpiritualLeashModule implements FightModule, InvokeLastDeadFighterHandler.DeadFighterResolver {
    private final Fight fight;
    private final Map<Team<?>, DeadFighterQueue> deadFightersQueue = new HashMap<>();

    public SpiritualLeashModule(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(780, new InvokeLastDeadFighterHandler(fight, this));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterDie>() {
                @Override
                public void on(FighterDie event) {
                    final Fighter fighter = event.fighter();

                    if (!(fighter instanceof PlayableFighter)) {
                        return;
                    }

                    deadFightersQueue
                        .computeIfAbsent(event.fighter().team(), fightTeam -> new DeadFighterQueue())
                        .push((PlayableFighter) fighter)
                    ;
                }

                @Override
                public Class<FighterDie> event() {
                    return FighterDie.class;
                }
            },
            new Listener<FighterRemoved>() {
                @Override
                public void on(FighterRemoved event) {
                    final Fighter fighter = event.fighter();
                    final DeadFighterQueue fighters;

                    if (!(fighter instanceof PlayableFighter)) {
                        return;
                    }

                    fighters = deadFightersQueue.get(event.fighter().team());

                    if (fighters != null) {
                        fighters.remove((PlayableFighter) fighter);
                    }
                }

                @Override
                public Class<FighterRemoved> event() {
                    return FighterRemoved.class;
                }
            },
        };
    }

    @Override
    public @Nullable PlayableFighter getLastDeadFighter(Team<?> team) {
        final DeadFighterQueue fighters = deadFightersQueue.get(team);

        if (fighters == null || fighters.isEmpty()) {
            return null;
        }

        PlayableFighter fighter;

        do {
            fighter = fighters.pop();
        } while (fighter != null && !fighter.dead());

        return fighter;
    }

    @Override
    public boolean hasDeadFighter(Team<?> team) {
        final DeadFighterQueue fighters = deadFightersQueue.get(team);

        return fighters != null && fighters.hasDeadFighter();
    }

    private static class DeadFighterQueue {
        private final Deque<PlayableFighter> fighters = new ArrayDeque<>();
        private final Deque<PlayableFighter> invocations = new ArrayDeque<>();

        public void push(PlayableFighter fighter) {
            if (fighter.invoked()) {
                invocations.add(fighter);
            } else {
                fighters.add(fighter);
            }
        }

        /**
         * Get the last dead fighter
         * Priority is given to fighters over invocations
         *
         * Note: this method will not check if the fighter actually dead
         */
        public @Nullable PlayableFighter pop() {
            PlayableFighter fighter = fighters.pollLast();

            if (fighter == null) {
                fighter = invocations.pollLast();
            }

            return fighter;
        }

        /**
         * Check if there is at least one fighter on queue
         *
         * Note: this method will not check if the fighter actually dead
         */
        public boolean isEmpty() {
            return fighters.isEmpty() && invocations.isEmpty();
        }

        /**
         * Check if there is at least one dead fighter on queue
         */
        public boolean hasDeadFighter() {
            for (PlayableFighter fighter : fighters) {
                if (fighter.dead()) {
                    return true;
                }
            }

            for (PlayableFighter fighter : invocations) {
                if (fighter.dead()) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Remove a fighter from queue
         */
        public void remove(PlayableFighter fighter) {
            fighters.remove(fighter);
            invocations.remove(fighter);
        }
    }
}
