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
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.AlterResistanceHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.HealOrMultiplyDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.ReduceDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.ReflectDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.SpellReturnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddActionPointsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddCharacteristicHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddCharacteristicOnDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddMovementPointsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddNotDispellableCharacteristicHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddVitalityHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddVitalityNotDispellableHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AlterVitalityHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.BoostCasterSightHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.DecreaseCasterSightHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.RemoveActionPointsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.RemoveCharacteristicHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.RemoveMovementPointsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.RemoveVitalityHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.StealCharacteristicHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.StealVitalityHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point.ActionPointLostHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point.MovementPointLostHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point.StealActionPointHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point.StealMovementPointHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageOnActionPointUseHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.FixedCasterDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.FixedDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.FixedStealLifeHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.IndirectPercentLifeDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.PercentLifeDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.PercentLifeLostDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.PunishmentHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.StealLifeHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.FixedHealHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.GivePercentLifeHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.HealHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.HealOnAttackHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal.HealOnDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.ChangeAppearanceHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.DispelHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.InvisibilityHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.KillHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.NoEffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.PushStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.RemoveStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.RevealInvisibleHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.SkipTurnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.modifier.BoostSpellHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.modifier.MaximizeTargetEffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.modifier.MinimizeCastedEffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.modifier.MultiplyDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.AvoidDamageByMovingBackHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.MoveBackHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.MoveFrontHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.MoveToTargetCellHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.SwitchPositionHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.SwitchPositionOnAttackHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.TeleportHandler;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Module for register common fight effects
 */
public final class CommonEffectsModule implements FightModule {
    private final Fight fight;

    public CommonEffectsModule(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(4, new TeleportHandler(fight));
        handler.register(5, new MoveBackHandler(fight));
        handler.register(6, new MoveFrontHandler(fight));
        handler.register(8, new SwitchPositionHandler(fight));
        handler.register(9, new AvoidDamageByMovingBackHandler(fight));
        handler.register(765, new SwitchPositionOnAttackHandler(fight));
        handler.register(783, new MoveToTargetCellHandler(fight));

        handler.register(132, new DispelHandler(fight));

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

        handler.register(85, new PercentLifeDamageHandler(Element.WATER, fight));
        handler.register(86, new PercentLifeDamageHandler(Element.EARTH, fight));
        handler.register(87, new PercentLifeDamageHandler(Element.AIR, fight));
        handler.register(88, new PercentLifeDamageHandler(Element.FIRE, fight));
        handler.register(89, new PercentLifeDamageHandler(Element.NEUTRAL, fight));

        handler.register(131, new DamageOnActionPointUseHandler(fight));
        handler.register(671, new IndirectPercentLifeDamageHandler(Element.NEUTRAL, fight));

        handler.register(276, new PercentLifeLostDamageHandler(Element.EARTH, fight));
        handler.register(279, new PercentLifeLostDamageHandler(Element.NEUTRAL, fight));

        handler.register(82, new FixedStealLifeHandler());
        handler.register(109, new FixedCasterDamageHandler());
        handler.register(144, new FixedDamageHandler());
        handler.register(672, new PunishmentHandler(fight));

        handler.register(81, new HealOnDamageHandler());
        handler.register(90, new GivePercentLifeHandler());
        handler.register(108, new HealHandler());
        handler.register(143, new FixedHealHandler());
        handler.register(786, new HealOnAttackHandler());

        handler.register(140, new SkipTurnHandler(fight));
        handler.register(149, new ChangeAppearanceHandler(fight));
        handler.register(950, new PushStateHandler(fight));
        handler.register(951, new RemoveStateHandler());
        handler.register(141, new KillHandler());
        handler.register(150, new InvisibilityHandler(fight));
        handler.register(202, new RevealInvisibleHandler(fight));

        handler.register(79,  new HealOrMultiplyDamageHandler());
        handler.register(105, new ReduceDamageHandler());
        handler.register(106, new SpellReturnHandler(fight));
        handler.register(107, new ReflectDamageHandler());
        handler.register(172, AlterResistanceHandler.reduceMagical());
        handler.register(183, AlterResistanceHandler.increaseMagical());
        handler.register(184, AlterResistanceHandler.increasePhysical());
        handler.register(265, new ReduceDamageHandler());

        handler.register(114, new MultiplyDamageHandler());
        handler.register(781, new MinimizeCastedEffectsHandler());
        handler.register(782, new MaximizeTargetEffectsHandler());

        handler.register(111, new AddActionPointsHandler(fight));
        handler.register(120, new AddActionPointsHandler(fight));
        handler.register(168, new RemoveActionPointsHandler(fight));

        handler.register(78,  new AddMovementPointsHandler(fight));
        handler.register(128, new AddMovementPointsHandler(fight));
        handler.register(169, new RemoveMovementPointsHandler(fight));

        handler.register(77, new StealMovementPointHandler(fight, 127, 128));
        handler.register(84, new StealActionPointHandler(fight, 101, 111));
        handler.register(101, new ActionPointLostHandler(fight));
        handler.register(127, new MovementPointLostHandler(fight));

        handler.register(110, new AddVitalityHandler(fight)); // Not sure for this effect
        handler.register(112, new AddCharacteristicHandler(fight, Characteristic.FIXED_DAMAGE));
        handler.register(115, new AddCharacteristicHandler(fight, Characteristic.CRITICAL_BONUS));
        handler.register(117, new AddCharacteristicHandler(fight, Characteristic.SIGHT_BOOST));
        handler.register(118, new AddCharacteristicHandler(fight, Characteristic.STRENGTH));
        handler.register(119, new AddCharacteristicHandler(fight, Characteristic.AGILITY));
        handler.register(122, new AddCharacteristicHandler(fight, Characteristic.FAIL_MALUS));
        handler.register(123, new AddCharacteristicHandler(fight, Characteristic.LUCK));
        handler.register(124, new AddCharacteristicHandler(fight, Characteristic.WISDOM));
        handler.register(125, new AddVitalityHandler(fight));
        handler.register(126, new AddCharacteristicHandler(fight, Characteristic.INTELLIGENCE));
        handler.register(138, new AddCharacteristicHandler(fight, Characteristic.PERCENT_DAMAGE));
        handler.register(142, new AddCharacteristicHandler(fight, Characteristic.PHYSICAL_DAMAGE));
        handler.register(178, new AddCharacteristicHandler(fight, Characteristic.HEALTH_BOOST));
        handler.register(182, new AddCharacteristicHandler(fight, Characteristic.MAX_SUMMONED_CREATURES));
        handler.register(220, new AddCharacteristicHandler(fight, Characteristic.COUNTER_DAMAGE));

        handler.register(606, new AddNotDispellableCharacteristicHandler(fight, Characteristic.WISDOM));
        handler.register(607, new AddNotDispellableCharacteristicHandler(fight, Characteristic.STRENGTH));
        handler.register(608, new AddNotDispellableCharacteristicHandler(fight, Characteristic.LUCK));
        handler.register(609, new AddNotDispellableCharacteristicHandler(fight, Characteristic.AGILITY));
        handler.register(610, new AddVitalityNotDispellableHandler(fight));
        handler.register(611, new AddNotDispellableCharacteristicHandler(fight, Characteristic.INTELLIGENCE));

        handler.register(116, new RemoveCharacteristicHandler(fight, Characteristic.SIGHT_BOOST));
        handler.register(145, new RemoveCharacteristicHandler(fight, Characteristic.FIXED_DAMAGE));
        handler.register(152, new RemoveCharacteristicHandler(fight, Characteristic.LUCK));
        handler.register(153, new RemoveVitalityHandler(fight));
        handler.register(154, new RemoveCharacteristicHandler(fight, Characteristic.AGILITY));
        handler.register(155, new RemoveCharacteristicHandler(fight, Characteristic.INTELLIGENCE));
        handler.register(156, new RemoveCharacteristicHandler(fight, Characteristic.WISDOM));
        handler.register(157, new RemoveCharacteristicHandler(fight, Characteristic.STRENGTH));
        handler.register(171, new RemoveCharacteristicHandler(fight, Characteristic.CRITICAL_BONUS));
        handler.register(179, new RemoveCharacteristicHandler(fight, Characteristic.HEALTH_BOOST));
        handler.register(186, new RemoveCharacteristicHandler(fight, Characteristic.PERCENT_DAMAGE));

        handler.register(160, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_ACTION_POINT));
        handler.register(161, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_MOVEMENT_POINT));
        handler.register(210, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_EARTH));
        handler.register(211, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_WATER));
        handler.register(212, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_AIR));
        handler.register(213, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_FIRE));
        handler.register(214, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_NEUTRAL));
        handler.register(240, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_EARTH));
        handler.register(241, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_WATER));
        handler.register(242, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_AIR));
        handler.register(243, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_FIRE));
        handler.register(244, new AddCharacteristicHandler(fight, Characteristic.RESISTANCE_NEUTRAL));

        handler.register(162, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_ACTION_POINT));
        handler.register(163, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_MOVEMENT_POINT));
        handler.register(215, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_EARTH));
        handler.register(216, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_WATER));
        handler.register(217, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_AIR));
        handler.register(218, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_FIRE));
        handler.register(219, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_PERCENT_NEUTRAL));
        handler.register(248, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_FIRE));
        handler.register(249, new RemoveCharacteristicHandler(fight, Characteristic.RESISTANCE_NEUTRAL));

        handler.register(267, new StealVitalityHandler(fight, 125, 153));
        handler.register(268, new StealCharacteristicHandler(fight, Characteristic.AGILITY, 119, 154));
        handler.register(269, new StealCharacteristicHandler(fight, Characteristic.INTELLIGENCE, 126, 155));
        handler.register(271, new StealCharacteristicHandler(fight, Characteristic.STRENGTH, 118, 157));
        handler.register(320, new StealCharacteristicHandler(fight, Characteristic.SIGHT_BOOST, 117, 116));

        handler.register(135, new DecreaseCasterSightHandler(fight));
        handler.register(136, new BoostCasterSightHandler(fight));

        handler.register(284, new BoostSpellHandler(SpellsBoosts.Modifier.HEAL));
        handler.register(285, new BoostSpellHandler(SpellsBoosts.Modifier.AP_COST));
        handler.register(286, new BoostSpellHandler(SpellsBoosts.Modifier.REDUCE_DELAY));
        handler.register(287, new BoostSpellHandler(SpellsBoosts.Modifier.CRITICAL));
        handler.register(290, new BoostSpellHandler(SpellsBoosts.Modifier.LAUNCH_PER_TURN));
        handler.register(293, new BoostSpellHandler(SpellsBoosts.Modifier.BASE_DAMAGE));

        handler.register(788, new AddCharacteristicOnDamageHandler(fight)
            .register(108, AlterVitalityHook.add(fight))
            .register(118, Characteristic.STRENGTH)
            .register(119, Characteristic.AGILITY)
            .register(123, Characteristic.LUCK)
            .register(126, Characteristic.INTELLIGENCE)
            .register(138, Characteristic.PERCENT_DAMAGE)
        );

        handler.register(666, new NoEffectHandler());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[0];
    }
}
