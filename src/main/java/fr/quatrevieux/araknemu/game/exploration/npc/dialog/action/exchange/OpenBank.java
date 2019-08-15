package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.exchange;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.BankExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;

/**
 * Action for open the bank
 *
 * @todo bank cost
 */
final public class OpenBank implements Action {
    final static public class Factory implements ActionFactory {
        final private BankService service;

        public Factory(BankService service) {
            this.service = service;
        }

        @Override
        public String type() {
            return "BANK";
        }

        @Override
        public Action create(ResponseAction entity) {
            return new OpenBank(service);
        }
    }

    final private BankService service;

    public OpenBank(BankService service) {
        this.service = service;
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        new BankExchangeParty(player, service.load(player.account())).start();
    }
}
