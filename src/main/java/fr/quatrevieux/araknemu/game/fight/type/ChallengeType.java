package fr.quatrevieux.araknemu.game.fight.type;

/**
 * Fight type for challenge
 */
final public class ChallengeType implements FightType {
    @Override
    public int id() {
        return 0;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public boolean hasPlacementTimeLimit() {
        return false;
    }

    @Override
    public int placementTime() {
        return 0;
    }
}
