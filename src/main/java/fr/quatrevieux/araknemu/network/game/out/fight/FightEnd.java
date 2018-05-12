package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.ending.reward.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardVisitor;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The fight is terminated
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1240
 */
final public class FightEnd {
    static private class RewardsFormatter implements FightRewardVisitor {
        final private List<String> out = new ArrayList<>();

        @Override
        public void onDropReward(DropReward reward) {
            out.add(
                reward.type().id() + ";" +
                reward.fighter().id() + ";" +
                reward.fighter().sprite().name() + ";" +
                reward.fighter().level() + ";" +
                (reward.fighter().dead() ? "1" : "0") + ";" +
                formatExperience(reward.fighter()) + ";" +
                (reward.xp() != 0 ? reward.xp() : "") + ";" +
                (reward.guildXp() != 0 ? reward.guildXp() : "") + ";" +
                (reward.mountXp() != 0 ? reward.mountXp() : "") + ";" +
                ";" + // @todo items
                (reward.kamas() != 0 ? reward.kamas() : "")
            );
        }

        private String formatExperience(Fighter fighter) {
            GamePlayer player = PlayerFighter.class.cast(fighter).player();

            return player.experience().min() + ";" + player.experience().current() + ";" + player.experience().max();

            // @todo handle other fighters types
//            if (fighter instanceof PlayerFighter) {
//                GamePlayer player = ((PlayerFighter) fighter).player();
//
//                return player.experience().min() + ";" + player.experience().current() + ";" + player.experience().max();
//            }
//
//            return "0;0;0";
        }
    }

    final private FightRewardsSheet rewardsSheet;

    public FightEnd(FightRewardsSheet rewardsSheet) {
        this.rewardsSheet = rewardsSheet;
    }

    @Override
    public String toString() {
        RewardsFormatter formatter = new RewardsFormatter();

        rewardsSheet.rewards().forEach(reward -> reward.apply(formatter));

        return "GE" +
            rewardsSheet.results().fight().duration() + "|" +
            rewardsSheet.results().fight().turnList().currentFighter().id() + "|" +
            rewardsSheet.type().ordinal() + "|" +
            StringUtils.join(formatter.out, "|")
        ;
    }
}
