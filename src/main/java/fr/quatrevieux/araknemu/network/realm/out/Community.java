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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Community community = (Community) o;

        return communityId == community.communityId;
    }

    @Override
    public int hashCode() {
        return communityId;
    }
}
