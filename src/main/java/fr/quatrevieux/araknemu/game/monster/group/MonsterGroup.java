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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.monster.group;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.event.CreatureMoving;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

import java.util.List;

/**
 * Group of monsters
 * The group is the only creature which can be found on exploration for interacting with {@link Monster}
 */
public final class MonsterGroup implements ExplorationCreature {
    private final LivingMonsterGroupPosition handler;
    private final int id;
    private final List<Monster> monsters;
    private final Position winFightTeleportPosition;

    private final MonsterGroupSprite sprite;

    private Direction orientation;
    private ExplorationMapCell cell;

    @SuppressWarnings({"assignment", "argument"})
    public MonsterGroup(LivingMonsterGroupPosition handler, int id, List<Monster> monsters, Direction orientation, ExplorationMapCell cell, Position winFightTeleportPosition) {
        this.handler = handler;
        this.id = id;
        this.monsters = monsters;
        this.orientation = orientation;
        this.cell = cell;
        this.winFightTeleportPosition = winFightTeleportPosition;

        this.sprite = new MonsterGroupSprite(this);
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public int id() {
        return Sprite.Type.MONSTER_GROUP.toSpriteId(id);
    }

    @Override
    public ExplorationMapCell cell() {
        return cell;
    }

    @Override
    public Direction orientation() {
        return orientation;
    }

    @Override
    public <R> R apply(Operation<R> operation) {
        return operation.onMonsterGroup(this);
    }

    /**
     * Monsters that compose the group
     */
    public List<Monster> monsters() {
        return monsters;
    }

    /**
     * Get the group position handler
     */
    public LivingMonsterGroupPosition handler() {
        return handler;
    }

    /**
     * Attack the group and start a PvM fight
     * After this call, the monster group and the player will be removed from map to join the fight
     *
     * @param player The attacker
     *
     * @return The created fight
     *
     * @see fr.quatrevieux.araknemu.game.fight.type.PvmType
     */
    public Fight startFight(ExplorationPlayer player) {
        return handler.startFight(this, player);
    }

    /**
     * Move the group
     * The cell will be set at the path target
     *
     * @param path The move path
     */
    public void move(Path<ExplorationMapCell> path) {
        cell = path.target();
        cell.map().dispatch(new CreatureMoving(this, path));
    }

    /**
     * Get the teleport position when win a fight with the group
     * The position may be null, to not teleport after the fight
     *
     * @see Position#isNull()
     * @see fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData#winFightTeleport()
     */
    public Position winFightTeleportPosition() {
        return winFightTeleportPosition;
    }
}
