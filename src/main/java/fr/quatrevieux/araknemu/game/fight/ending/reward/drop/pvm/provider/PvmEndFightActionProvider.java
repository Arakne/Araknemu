package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;

/**
 * Add end fight actions
 *
 * Actually only teleport action is available
 */
final public class PvmEndFightActionProvider implements DropRewardProvider {
    static private class Scope implements DropRewardProvider.Scope {
        final private Position position;

        public Scope(Position position) {
            this.position = position;
        }

        @Override
        public void provide(DropReward reward) {
            reward.addAction((reward1, fighter) -> fighter.apply(new FighterOperation() {
                @Override
                public void onPlayer(PlayerFighter fighter) {
                    fighter.player().setPosition(position);
                }
            }));
        }
    }

    @Override
    public DropRewardProvider.Scope initialize(EndFightResults results) {
        return results.loosers().stream()
            .map(Fighter::team)
            .filter(team -> team instanceof MonsterGroupTeam)
            .map(team -> ((MonsterGroupTeam) team).group().winFightTeleportPosition())
            .findFirst()
            .filter(position -> !position.isNull())
            .<DropRewardProvider.Scope>map(Scope::new)
            .orElse(DropRewardProvider.Scope.NOOP)
        ;
    }
}
