package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Send the current community (i.e. country)
 */
final public class Community {
    final private int communityId;

    public Community(int communityId) {
        this.communityId = communityId;
    }

    @Override
    public String toString() {
        return "Ac" + communityId;
    }
}
