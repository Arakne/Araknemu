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
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.HealOrMultiplyDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.ReduceDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.SpellReturnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.*;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.StealLifeHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.DebuffHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.PushStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.RemoveStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.SkipTurnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.TeleportHandler;

/**
 * Module for register common fight effects
 */
final public class CommonEffectsModule implements FightModule {
    final private Fight fight;

    public CommonEffectsModule(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(4, new TeleportHandler(fight));

        handler.register(132, new DebuffHandler());

        handler.register(91, new StealLifeHandler(Element.WATER, fight));
        handler.register(92, new StealLifeHandler(Element.EARTH, fight));
        handler.register(93, new StealLifeHandler(Element.AIR, fight));
        handler.register(94, new StealLifeHandler(Element.FIRE, fight));
        handler.register(95, new StealLifeHandler(Element.NEUTRAL, fight));

        handler.register(96,  new DamageHandler(Element.WATER, fight));
        handler.register(97,  new DamageHandler(Element.EARTH, fight));
        handler.register(98,  new DamageHandler(Element.AIR, fight));
        handler.register(99,  new DamageHandler(Element.FIRE, fight));
        handler.register(100, new DamageHandler(Element.NEUTRAL, fight));

        handler.register(140, new SkipTurnHandler(fight));
        handler.register(950, new PushStateHandler(fight));
        handler.register(951, new RemoveStateHandler());

        handler.register(79,  new HealOrMultiplyDamageHandler());
        handler.register(105, new ReduceDamageHandler());
        handler.register(106, new SpellReturnHandler(fight));
        handler.register(265, new ReduceDamageHandler());

        handler.register(111, new AddActionPointsHandler(fight));
        handler.register(120, new AddActionPointsHandler(fight));
        handler.register(168, new RemoveActionPointsHandler(fight));

        handler.register(78,  new AddMovementPointsHandler(fight));
        handler.register(128, new AddMovementPointsHandler(fight));
        handler.register(169, new RemoveMovementPointsHandler(fight));

        handler.register(112, new AddCharacteristicHandler(fight, Characteristic.FIXED_DAMAGE));
        handler.register(115, new AddCharacteristicHandler(fight, Characteristic.CRITICAL_BONUS));
        handler.register(117, new AddCharacteristicHandler(fight, Characteristic.SIGHT_BOOST));
        handler.register(118, new AddCharacteristicHandler(fight, Characteristic.STRENGTH));
        handler.register(119, new AddCharacteristicHandler(fight, Characteristic.AGILITY));
        handler.register(122, new AddCharacteristicHandler(fight, Characteristic.FAIL_MALUS));
        handler.register(123, new AddCharacteristicHandler(fight, Characteristic.LUCK));
        handler.register(124, new AddCharacteristicHandler(fight, Characteristic.WISDOM));
        handler.register(126, new AddCharacteristicHandler(fight, Characteristic.INTELLIGENCE));
        handler.register(138, new AddCharacteristicHandler(fight, Characteristic.PERCENT_DAMAGE));
        handler.register(178, new AddCharacteristicHandler(fight, Characteristic.HEALTH_BOOST));
        handler.register(182, new AddCharacteristicHandler(fight, Characteristic.MAX_SUMMONED_CREATURES));

        handler.register(116, new RemoveCharacteristicHandler(fight, Characteristic.SIGHT_BOOST));
        handler.register(145, new RemoveCharacteristicHandler(fight, Characteristic.FIXED_DAMAGE));
        handler.register(152, new RemoveCharacteristicHandler(fight, Characteristic.LUCK));
        handler.register(154, new RemoveCharacteristicHandler(fight, Characteristic.AGILITY));
        handler.register(155, new RemoveCharacteristicHandler(fight, Characteristic.INTELLIGENCE));
        handler.register(156, new RemoveCharacteristicHandler(fight, Characteristic.WISDOM));
        handler.register(157, new RemoveCharacteristicHandler(fight, Characteristic.STRENGTH));
        handler.register(171, new RemoveCharacteristicHandler(fight, Characteristic.CRITICAL_BONUS));
        handler.register(179, new RemoveCharacteristicHandler(fight, Characteristic.HEALTH_BOOST));
        handler.register(186, new RemoveCharacteristicHandler(fight, Characteristic.PERCENT_DAMAGE));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[0];
    }
}
