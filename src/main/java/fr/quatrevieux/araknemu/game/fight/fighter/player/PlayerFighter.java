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

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.*;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerSessionScope;
import fr.quatrevieux.araknemu.game.player.inventory.slot.WeaponSlot;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Fighter for a player
 */
final public class PlayerFighter extends AbstractFighter implements Fighter, PlayerSessionScope {
    final private GamePlayer player;
    final private PlayerFighterProperties properties;
    final private PlayerFighterSprite sprite;

    private boolean ready = false;
    private CastableWeapon weapon;
    private FightTeam team;

    public PlayerFighter(GamePlayer player) {
        this.player = player;
        this.properties = new PlayerFighterProperties(this, player.properties());
        this.sprite = new PlayerFighterSprite(this, player.spriteInfo());
    }

    @Override
    public void init() {
        properties.life().init();

        super.init();
    }

    /**
     * Get the base player data
     */
    public GamePlayer player() {
        return player;
    }

    @Override
    public int id() {
        return player.id();
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public FighterLife life() {
        return properties.life();
    }

    @Override
    public FighterCharacteristics characteristics() {
        return properties.characteristics();
    }

    /**
     * Get the properties of the current character
     */
    @Override
    public PlayerFighterProperties properties() {
        return properties;
    }

    @Override
    public SpellList spells() {
        return properties.spells();
    }

    @Override
    public void dispatch(Object event) {
        player.dispatch(event);
    }

    @Override
    public void register(GameSession session) {
        session.setFighter(this);
    }

    @Override
    public void unregister(GameSession session) {
        destroy();

        session.setFighter(null);
    }

    @Override
    public CastableWeapon weapon() {
        if (weapon != null) {
            return weapon;
        }

        return weapon = player.inventory()
            .bySlot(WeaponSlot.SLOT_ID)
            .map(Weapon.class::cast)
            .map(CastableWeapon::new)
            .orElseThrow(() -> new FightException("The fighter do not have any weapon"))
        ;
    }

    @Override
    public int level() {
        return player.properties().experience().level();
    }

    @Override
    public FightTeam team() {
        return team;
    }

    /**
     * Set the fighter team
     */
    public void setTeam(FightTeam team) {
        this.team = team;
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    @Override
    public boolean ready() {
        return ready;
    }

    @Override
    public <O extends FighterOperation> O apply(O operation) {
        operation.onPlayer(this);

        return operation;
    }

    /**
     * Change the ready flag
     */
    public void setReady(boolean ready) {
        this.ready = ready;
        fight().dispatch(new FighterReadyStateChanged(this));
    }
}
