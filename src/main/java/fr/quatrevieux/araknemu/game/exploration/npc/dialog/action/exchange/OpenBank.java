package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.exchange;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.out.ServerMessage;

/**
 * Action for open the bank
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
        // Always true ?
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        Bank bank = service.load(player.account());

        if (payBankTax(player, bank)) {
            bank.exchange(player).start();
        }
    }

    /**
     * Try to pay the bank tax
     *
     * Check, in order, if has enough kamas :
     * - The player
     * - The bank
     * - The bank + player kamas
     *
     * Send information message when payed
     *
     * @return True on success, or false if there not enough kamas
     */
    private boolean payBankTax(ExplorationPlayer player, Bank bank) {
        final long cost = bank.cost();

        if (cost == 0) {
            return true;
        }

        if (player.inventory().kamas() >= cost) {
            player.inventory().removeKamas(cost);
            player.send(Information.bankTaxPayed(cost));

            return true;
        }

        if (bank.kamas() >= cost) {
            bank.removeKamas(cost);

            return true;
        }

        if (player.inventory().kamas() + bank.kamas() < cost) {
            player.send(ServerMessage.notEnoughKamasForBank(cost));

            return false;
        }

        final long payedByPlayer = cost - bank.kamas();

        bank.removeKamas(bank.kamas());
        player.inventory().removeKamas(payedByPlayer);
        player.send(Information.bankTaxPayed(payedByPlayer));

        return true;
    }
}
