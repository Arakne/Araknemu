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
 * Copyright (c) 2017-2021 Vincent Quatrevieux Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import java.util.Optional;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.BaseFighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

final public class InvocationFighter implements Fighter {
    final private Fighter fighter;
    final private PassiveFighter invoker;
    final private FighterLife life;

    public InvocationFighter(Fighter fighter, PassiveFighter invoker) {
        this.fighter = fighter;
        this.invoker = invoker;

        float rate = 1 + ((Fighter)invoker).level() / 100 ;
        int vitality = Math.round(fighter.life().max() * rate);

        this.life = new BaseFighterLife(fighter, vitality, vitality);
        this.scaleCharacteristicsBasedOnInvokerLevel(rate);
    }

    private void scaleCharacteristicsBasedOnInvokerLevel(float rate) {
        int strength = fighter.characteristics().get(Characteristic.STRENGTH);
        int wisdom = fighter.characteristics().get(Characteristic.WISDOM);
        int luck = fighter.characteristics().get(Characteristic.LUCK);
        int agility = fighter.characteristics().get(Characteristic.AGILITY);

        fighter.characteristics().alter(Characteristic.STRENGTH, Math.round(strength * rate));
        fighter.characteristics().alter(Characteristic.WISDOM, Math.round(wisdom * rate));
        fighter.characteristics().alter(Characteristic.LUCK, Math.round(luck * rate));
        fighter.characteristics().alter(Characteristic.AGILITY, Math.round(agility * rate));
    }

    @Override
    public <O extends FighterOperation> O apply(O operation) {
        operation.onInvocation(this);

        return operation;
    }

    @Override
    public int level() {
        return fighter.level();
    }

    @Override
    public boolean ready() {
        return fighter.ready();
    }

    @Override
    public FightTeam team() {
        return fighter.team();
    }

    @Override
    public CastableWeapon weapon() {
        return fighter.weapon();
    }

    @Override
    public int id() {
        return fighter.id();
    }

    @Override
    public Sprite sprite() {
        return fighter.sprite();
    }

    @Override
    public SpellList spells() {
        return fighter.spells();
    }

    @Override
    public FighterCharacteristics characteristics() {
        return fighter.characteristics();
    }

    @Override
    public FighterLife life() {
        return life;
    }

    @Override
    public Optional<PassiveFighter> invoker() {
        return Optional.of(invoker);
    }

    @Override
    public void attach(Object key, Object value) {
        fighter.attach(key, value);
    }

    @Override
    public Fight fight() {
        return fighter.fight();
    }

    @Override
    public void init() {
        fighter.init();
    }

    @Override
    public boolean isOnFight() {
        return fighter.isOnFight();
    }

    @Override
    public void joinFight(Fight fight, FightCell startCell) {
        fighter.joinFight(fight, startCell);
    }

    @Override
    public void play(FightTurn turn) {
        fighter.play(turn);
    }

    @Override
    public void setOrientation(Direction orientation) {
        fighter.setOrientation(orientation);
    }

    @Override
    public void stop() {
        fighter.stop();
    }

    @Override
    public FightCell cell() {
        return fighter.cell();
    }

    @Override
    public Direction orientation() {
        return fighter.orientation();
    }

    @Override
    public void dispatch(Object event) {
        fighter.dispatch(event);
    }

    @Override
    public Object attachment(Object key) {
        return fighter.attachment(key);
    }

    @Override
    public void move(FightCell cell) {
        fighter.move(cell);
    }

    @Override
    public States states() {
        return fighter.states();
    }

    @Override
    public FightTurn turn() {
        return fighter.turn();
    }

    @Override
    public BuffList buffs() {
        return fighter.buffs();
    }
}
