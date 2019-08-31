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

package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStore;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;

import java.util.Collection;
import java.util.Optional;

/**
 * Living NPC
 */
final public class GameNpc implements ExplorationCreature {
    final private Npc entity;
    final private NpcTemplate template;
    final private Collection<NpcQuestion> questions;
    final private NpcStore store;

    final private Sprite sprite;

    private ExplorationMapCell cell;

    public GameNpc(Npc entity, NpcTemplate template, Collection<NpcQuestion> questions, NpcStore store) {
        this.entity = entity;
        this.template = template;
        this.questions = questions;
        this.store = store;

        this.sprite = new NpcSprite(this);
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public int id() {
        return Sprite.Type.NPC.toSpriteId(entity.id());
    }

    @Override
    public ExplorationMapCell cell() {
        return cell;
    }

    @Override
    public Direction orientation() {
        return entity.orientation();
    }

    @Override
    public void apply(Operation operation) {
        operation.onNpc(this);
    }

    /**
     * Join the given map
     *
     * The cell will be set, and the npc will be added to the map
     */
    public void join(ExplorationMap map) {
        cell = map.get(entity.position().cell());
        map.add(this);
    }

    /**
     * Get the npc position
     */
    public Position position() {
        return entity.position();
    }

    /**
     * Get the initial dialog question of the NPC for the given player
     *
     * @param player The interlocutor player
     *
     * @return The first matching question, or empty if no matching question can be found
     */
    public Optional<NpcQuestion> question(ExplorationPlayer player) {
        return questions.stream()
            .filter(question -> question.check(player))
            .findFirst()
        ;
    }

    /**
     * Get the npc's store
     *
     * @throws UnsupportedOperationException When store is not available
     */
    public NpcStore store() {
        if (store == null) {
            throw new UnsupportedOperationException("Store is not available");
        }

        return store;
    }

    NpcTemplate template() {
        return template;
    }
}
