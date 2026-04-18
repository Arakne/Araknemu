package fr.quatrevieux.araknemu.game.exploration.event;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;

public class FriendAdded {
    private final Account account;

    public FriendAdded(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
