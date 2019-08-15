package fr.quatrevieux.araknemu.data.living.entity;

/**
 * Entity type for store kamas
 */
public interface WalletEntity {
    /**
     * Get the current amount of kamas
     * The value is always positive
     */
    public long kamas();

    /**
     * Change the kamas quantity
     *
     * @param kamas The new kamas quantity. Must be positive
     */
    public void setKamas(long kamas);
}
