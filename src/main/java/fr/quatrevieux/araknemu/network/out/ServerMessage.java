package fr.quatrevieux.araknemu.network.out;

import org.apache.commons.lang3.StringUtils;

/**
 * Display a message box
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Aks.as#L619
 */
final public class ServerMessage {
    /**
     * Message should be directly displayed, or on connection closed ?
     *
     * Set to false for display message on connection closed
     */
    final private boolean displayNow;

    /**
     * The message ID
     *
     * See lang.swf SRV_MSG_[message id]
     */
    final private int messageId;

    /**
     * Parameters for the message
     */
    final private Object[] parameters;

    /**
     * The message box name
     * Can be null
     */
    final private String name;

    public ServerMessage(boolean displayNow, int messageId, Object[] parameters, String name) {
        this.displayNow = displayNow;
        this.messageId = messageId;
        this.parameters = parameters;
        this.name = name;
    }

    @Override
    public String toString() {
        return "M"
            + (displayNow ? "1" : "0") + messageId + "|" +
            StringUtils.join(parameters, ";") +
            (name != null ? "|" + name : "")
        ;
    }

    /**
     * The player do not have enough kamas for open its bank account
     *
     * @param cost The bank cost
     */
    static public ServerMessage notEnoughKamasForBank(long cost) {
        return new ServerMessage(true, 10, new Object[] { cost }, null);
    }
}
