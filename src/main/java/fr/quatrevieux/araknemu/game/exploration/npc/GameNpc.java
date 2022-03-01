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

package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.exchange.Exchange;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Living NPC
 */
public final class GameNpc implements ExplorationCreature {
    private final Npc entity;
    private final NpcTemplate template;
    private final Collection<NpcQuestion> questions;
    private final Map<ExchangeType, ExchangeProvider.Factory> exchangeFactoriesByType = new EnumMap<>(ExchangeType.class);

    private final Sprite sprite;

    private @MonotonicNonNull ExplorationMapCell cell;

    @SuppressWarnings({"assignment", "argument"})
    public GameNpc(Npc entity, NpcTemplate template, Collection<NpcQuestion> questions, Collection<ExchangeProvider.Factory> exchangeFactories) {
        this.entity = entity;
        this.template = template;
        this.questions = questions;

        this.sprite = new NpcSprite(this);
        exchangeFactories.forEach(factory -> exchangeFactoriesByType.put(factory.type(), factory));
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
        if (cell == null) {
            throw new IllegalStateException("The NPC has not join a map");
        }

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
     * Get an exchange on the npc
     *
     * @param type The exchange type
     * @param initiator The player who initiate the exchange
     *
     * @return The exchange
     *
     * @throws NoSuchElementException When the npc do not supports the exchange type
     */
    public Exchange exchange(ExchangeType type, ExplorationPlayer initiator) {
        return exchangeFactory(type).create(initiator, this);
    }

    /**
     * Get an exchange factory
     *
     * @param type The exchange type
     *
     * @throws NoSuchElementException When the npc do not supports the exchange type
     */
    public ExchangeProvider.Factory exchangeFactory(ExchangeType type) {
        if (!exchangeFactoriesByType.containsKey(type)) {
            throw new NoSuchElementException("Exchange " + type + " is not supported by the npc " + template.id());
        }

        return exchangeFactoriesByType.get(type);
    }

    NpcTemplate template() {
        return template;
    }
}
