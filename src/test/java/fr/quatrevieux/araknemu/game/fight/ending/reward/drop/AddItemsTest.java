package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.AddItems;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddItemsTest extends FightBaseCase {
    private AddItems action;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemSets();

        action = new AddItems(container.get(ItemService.class));
        fight = createPvmFight();
        fight.nextState();

        fighter = player.fighter();
    }

    @Test
    void applyWithoutItems() {
        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());

        action.apply(reward, fighter);
        assertEquals(0, player.inventory().weight());
    }

    @Test
    void applyWithOneItem() {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(39, 1);

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());
        reward.addItem(39);

        action.apply(reward, fighter);
        assertEquals(4, player.inventory().weight());
        assertEquals(39, player.inventory().get(1).item().template().id());
        assertEquals(1, player.inventory().get(1).quantity());
    }

    @Test
    void applyWithTwoSameItemsWithStats() {
        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());
        reward.addItem(2411, 2);

        action.apply(reward, fighter);
        assertEquals(20, player.inventory().weight());
        assertEquals(2411, player.inventory().get(1).item().template().id());
        assertEquals(1, player.inventory().get(1).quantity());
        assertEquals(2411, player.inventory().get(2).item().template().id());
        assertEquals(1, player.inventory().get(2).quantity());
    }

    @Test
    void applyWithMultipleItems() {
        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());
        reward.addItem(39);
        reward.addItem(40);
        reward.addItem(2425);

        action.apply(reward, fighter);
        assertEquals(39, player.inventory().get(1).item().template().id());
        assertEquals(40, player.inventory().get(2).item().template().id());
        assertEquals(2425, player.inventory().get(3).item().template().id());
    }

    @Test
    void applyOnMonster() {
        MonsterFighter fighter = (MonsterFighter) fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());
        reward.addItem(39);

        action.apply(reward, fighter);
    }
}
