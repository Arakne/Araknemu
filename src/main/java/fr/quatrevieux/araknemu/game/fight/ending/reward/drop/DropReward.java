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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.DropRewardAction;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.util.CopyOnFirstWriteCollection;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reward for drop
 */
public final class DropReward implements FightReward {
    private final RewardType type;
    private final Fighter fighter;

    private final Map<Integer, @Positive Integer> items = new HashMap<>();
    private final Collection<DropRewardAction> actions;

    private @NonNegative long xp = 0;
    private @NonNegative long mountXp = 0;
    private @NonNegative long guildXp = 0;
    private @NonNegative int kamas = 0;

    public DropReward(RewardType type, Fighter fighter, Collection<DropRewardAction> actions) {
        this.type = type;
        this.fighter = fighter;
        this.actions = new CopyOnFirstWriteCollection<>(actions);
    }

    @Override
    public Fighter fighter() {
        return fighter;
    }

    @Override
    public RewardType type() {
        return type;
    }

    @Override
    public void apply() {
        actions.forEach(action -> action.apply(this, fighter));
    }

    @Override
    public String render() {
        return
            type().id() + ";" +
            fighter().id() + ";" +
            fighter().sprite().name() + ";" +
            fighter().level() + ";" +
            (fighter().dead() ? "1" : "0") + ";" +
            fighter().apply(new FormatExperience()).format() + ";" +
            (xp() != 0 ? xp() : "") + ";" +
            (guildXp() != 0 ? guildXp() : "") + ";" +
            (mountXp() != 0 ? mountXp() : "") + ";" +
            items.entrySet().stream()
                .map(entry -> entry.getKey() + "~" + entry.getValue())
                .collect(Collectors.joining(",")) + ";" +
            (kamas() != 0 ? kamas() : "")
        ;
    }

    /**
     * Get player win xp
     */
    @Pure
    public @NonNegative long xp() {
        return xp;
    }

    /**
     * Xp given to the player's guild
     */
    @Pure
    public @NonNegative long guildXp() {
        return guildXp;
    }

    /**
     * Xp given to the player's mount
     */
    @Pure
    public @NonNegative long mountXp() {
        return mountXp;
    }

    /**
     * Win kamas
     */
    @Pure
    public @NonNegative int kamas() {
        return kamas;
    }

    /**
     * Get list of win items
     *
     * The key is the item id
     * The value is the item quantity
     */
    @Pure
    public Map<Integer, @Positive Integer> items() {
        return items;
    }

    /**
     * Set the player win XP
     *
     * @see DropReward#xp()
     */
    public void setXp(@NonNegative long xp) {
        this.xp = xp;
    }

    /**
     * Set xp give to mount
     *
     * @see DropReward#mountXp()
     */
    public void setMountXp(@NonNegative long mountXp) {
        this.mountXp = mountXp;
    }

    /**
     * Set the xp give to guild
     *
     * @see DropReward#guildXp()
     */
    public void setGuildXp(@NonNegative long guildXp) {
        this.guildXp = guildXp;
    }

    /**
     * Set win kamas
     *
     * @see DropReward#kamas()
     */
    public void setKamas(@NonNegative int kamas) {
        this.kamas = kamas;
    }

    /**
     * Add an item to the win items
     *
     * @param itemId The item template id
     * @param quantity Quantity of the item to add
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate#id()
     */
    public void addItem(int itemId, @Positive int quantity) {
        items.merge(itemId, quantity, (a, b) -> a + b); // Do not use Integer::sum because it does not handle @Positive
    }

    /**
     * Add one item to the win items
     *
     * @param itemId The item template id
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate#id()
     * @see DropReward#addItem(int, int)
     */
    public void addItem(int itemId) {
        addItem(itemId, 1);
    }

    /**
     * Add an end fight action
     */
    public void addAction(DropRewardAction action) {
        actions.add(action);
    }

    /**
     * Format the experience string for the reward line
     */
    private static class FormatExperience implements FighterOperation {
        private String format = "0;0;0";

        @Override
        public void onPlayer(PlayerFighter fighter) {
            format = fighter.properties().experience().min() + ";" + fighter.properties().experience().current() + ";" + fighter.properties().experience().max();
        }

        public String format() {
            return format;
        }
    }
}
