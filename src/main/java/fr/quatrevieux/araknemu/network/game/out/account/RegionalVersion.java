package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Send the regional version
 */
final public class RegionalVersion {
    private int region;

    public RegionalVersion(int region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "AV" + region;
    }
}
