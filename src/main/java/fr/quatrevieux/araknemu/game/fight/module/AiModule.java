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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.AlterCharacteristicSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.DamageSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.StealLifeSimulator;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;

/**
 * Fight module for enable AI
 */
final public class AiModule implements FightModule {
    final private AiFactory factory;

    public AiModule(AiFactory factory) {
        this.factory = factory;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterInitialized>() {
                @Override
                public void on(FighterInitialized event) {
                    init(event.fighter());
                }

                @Override
                public Class<FighterInitialized> event() {
                    return FighterInitialized.class;
                }
            },
            new Listener<TurnStarted>() {
                @Override
                public void on(TurnStarted event) {
                    start(event.turn());
                }

                @Override
                public Class<TurnStarted> event() {
                    return TurnStarted.class;
                }
            }
        };
    }

    /**
     * Initialize the AI for the fighter (if supported)
     */
    private void init(Fighter fighter) {
        factory.create(fighter).ifPresent(fighter::attach);
    }

    /**
     * Starts the AI on turn start
     */
    private void start(FightTurn turn) {
        FighterAI ai = turn.fighter().attachment(FighterAI.class);

        if (ai != null) {
            ai.start(turn);
        }
    }

    /**
     * Creates the simulator
     *
     * @todo refactor : not public, in game module
     */
    static public Simulator createSimulator() {
        Simulator simulator = new Simulator();

        simulator.register(91, new StealLifeSimulator(Element.WATER));
        simulator.register(92, new StealLifeSimulator(Element.EARTH));
        simulator.register(93, new StealLifeSimulator(Element.AIR));
        simulator.register(94, new StealLifeSimulator(Element.FIRE));
        simulator.register(95, new StealLifeSimulator(Element.NEUTRAL));

        simulator.register(96,  new DamageSimulator(Element.WATER));
        simulator.register(97,  new DamageSimulator(Element.EARTH));
        simulator.register(98,  new DamageSimulator(Element.AIR));
        simulator.register(99,  new DamageSimulator(Element.FIRE));
        simulator.register(100, new DamageSimulator(Element.NEUTRAL));

        // AP
        simulator.register(111, new AlterCharacteristicSimulator(200));
        simulator.register(120, new AlterCharacteristicSimulator(200));
        simulator.register(168, new AlterCharacteristicSimulator(-200));

        // MP
        simulator.register(78,  new AlterCharacteristicSimulator(200));
        simulator.register(128, new AlterCharacteristicSimulator(200));
        simulator.register(169, new AlterCharacteristicSimulator(-200));

        // Characteristics boost
        simulator.register(112, new AlterCharacteristicSimulator(10)); // damage
        simulator.register(115, new AlterCharacteristicSimulator(5)); // critical
        simulator.register(117, new AlterCharacteristicSimulator(5)); // sight
        simulator.register(118, new AlterCharacteristicSimulator()); // strength
        simulator.register(119, new AlterCharacteristicSimulator()); // agility
        simulator.register(122, new AlterCharacteristicSimulator(-5)); // Fail malus
        simulator.register(123, new AlterCharacteristicSimulator()); // luck
        simulator.register(124, new AlterCharacteristicSimulator()); // wisdom
        simulator.register(126, new AlterCharacteristicSimulator()); // intelligence
        simulator.register(138, new AlterCharacteristicSimulator(2)); // percent damage
        simulator.register(178, new AlterCharacteristicSimulator(8)); // heal
        simulator.register(182, new AlterCharacteristicSimulator(10)); // summoned creature

        // Characteristics malus
        simulator.register(116, new AlterCharacteristicSimulator(-5)); // sight malus
        simulator.register(145, new AlterCharacteristicSimulator(-10)); // -damage
        simulator.register(152, new AlterCharacteristicSimulator(-1)); // -luck
        simulator.register(154, new AlterCharacteristicSimulator(-1)); // -agility
        simulator.register(155, new AlterCharacteristicSimulator(-1)); // -intelligence
        simulator.register(156, new AlterCharacteristicSimulator(-1)); // -wisdom
        simulator.register(157, new AlterCharacteristicSimulator(-1)); // -strength
        simulator.register(171, new AlterCharacteristicSimulator(-5)); // -critical
        simulator.register(179, new AlterCharacteristicSimulator(-8)); // -heal
        simulator.register(186, new AlterCharacteristicSimulator(-2)); // -percent damage

        return simulator;
    }
}
