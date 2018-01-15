package fr.quatrevieux.araknemu.common.account;

/**
 * List of admin permissions
 */
public enum Permission {
    ACCESS,
    SUPER_ADMIN;

    /**
     * Get the permission bit
     */
    public int id() {
        return 1 << ordinal();
    }

    /**
     * Check if the permission match with the given value
     */
    public boolean match(int value) {
        return (value & id()) == id();
    }
}
