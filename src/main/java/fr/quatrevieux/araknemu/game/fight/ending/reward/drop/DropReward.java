package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reward for drop
 */
final public class DropReward implements FightReward {
    final private RewardType type;
    final private Fighter fighter;
    final private List<DropRewardAction> actions;

    final private long xp;
    final private int kamas;
    final private Map<Integer, Integer> items;

    public DropReward(RewardType type, Fighter fighter) {
        this(type, fighter, Collections.emptyList(), 0, 0, Collections.emptyMap());
    }

    public DropReward(RewardType type, Fighter fighter, List<DropRewardAction> actions, long xp, int kamas, Map<Integer, Integer> items) {
        this.type = type;
        this.fighter = fighter;
        this.actions = actions;
        this.xp = xp;
        this.kamas = kamas;
        this.items = items;
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
            formatExperience(fighter()) + ";" +
            (xp() != 0 ? xp() : "") + ";" +
            (guildXp() != 0 ? guildXp() : "") + ";" +
            (mountXp() != 0 ? mountXp() : "") + ";" +
            items.entrySet().stream()
                .map(entry -> entry.getKey() + "~" + entry.getValue())
                .collect(Collectors.joining(",")) + ";" +
            (kamas() != 0 ? kamas() : "")
        ;
    }

    public long xp() {
        return xp;
    }

    public long guildXp() {
        return 0;
    }

    public long mountXp() {
        return 0;
    }

    public int kamas() {
        return kamas;
    }

    /**
     * Get list of win items
     *
     * The key is the item id
     * The value is the item quantity
     */
    public Map<Integer, Integer> items() {
        return items;
    }

    /**
     * Format the experience string for the reward line
     */
    static private String formatExperience(Fighter fighter) {
        // @todo handle other fighters types (visitor on fighter)
        if (fighter instanceof PlayerFighter) {
            GamePlayer player = ((PlayerFighter) fighter).player();

            return player.properties().experience().min() + ";" + player.properties().experience().current() + ";" + player.properties().experience().max();
        }

        return "0;0;0";
    }
}
