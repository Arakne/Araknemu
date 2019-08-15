package fr.quatrevieux.araknemu.data.living.entity.account;

import fr.quatrevieux.araknemu.data.living.entity.WalletEntity;

/**
 * Store the bank information for a server account
 */
final public class AccountBank implements WalletEntity {
    final private int accountId;
    final private int serverId;
    private long kamas;

    public AccountBank(int accountId, int serverId, long kamas) {
        this.accountId = accountId;
        this.serverId = serverId;
        this.kamas = kamas;
    }

    /**
     * The related account id
     * This value is a part of the primary key
     *
     * @see Account#id()
     */
    public int accountId() {
        return accountId;
    }

    /**
     * The game server id
     * This value is a part of the primary key
     */
    public int serverId() {
        return serverId;
    }

    @Override
    public long kamas() {
        return kamas;
    }

    @Override
    public void setKamas(long kamas) {
        this.kamas = kamas;
    }
}
