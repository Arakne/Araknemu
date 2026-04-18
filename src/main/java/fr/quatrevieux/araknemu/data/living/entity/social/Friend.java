package fr.quatrevieux.araknemu.data.living.entity.social;

public class Friend {
    private final int accountId;
    private final int serverId;
    private final int contactId;
    private final boolean isEnemy;

    public Friend(int accountId, int serverId, int contactId, boolean isEnemy) {
        this.accountId = accountId;
        this.serverId = serverId;
        this.contactId = contactId;
        this.isEnemy = isEnemy;
    }

    public int accountId() {
        return accountId;
    }

    public int serverId() {
        return serverId;
    }

    public int contactId() {
        return contactId;
    }

    public boolean isEnemy() {
        return isEnemy;
    }
}
