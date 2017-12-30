package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Packet for successfully login to game server
 *
 * Parameters :
 * - keyId : If -1 : do not change current key
 *           If 0  : do not use any key (development value)
 *           If [1-16] : Register a new key with race [keyId]
 * - key : The cypher key. Register only if keyId > 0
 *         The key is string encoded with hexadecimal (2 chars per key char)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L735
 */
final public class LoginTokenSuccess {
    final private int keyId;
    final private String key;

    public LoginTokenSuccess(int keyId, String key) {
        this.keyId = keyId;
        this.key = key;
    }

    public LoginTokenSuccess() {
        this(0, "");
    }

    @Override
    public String toString() {
        return "ATK" + Integer.toHexString(keyId).toUpperCase() + key;
    }
}
