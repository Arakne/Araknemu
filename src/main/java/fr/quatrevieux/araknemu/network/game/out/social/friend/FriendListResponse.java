package fr.quatrevieux.araknemu.network.game.out.social.friend;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.Collection;

public class FriendListResponse {
    private final Collection<Account> accounts;
    private final PlayerService playerService;

    public FriendListResponse(Collection<Account> accounts, PlayerService playerService) {
        this.accounts = accounts;
        this.playerService = playerService;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FL");

        for (Account account : accounts) {
            if(playerService.filter(p -> p.account().id() == account.id()).findFirst().isPresent()) {
                GamePlayer gamePlayer = playerService.filter(p -> p.account().id() == account.id()).findFirst().get();
                Player player = gamePlayer.entity();
                sb
                        .append('|')
                        .append(account.pseudo());
                if(playerService.isOnline(player.name())) {
                    sb.append(";").append(gamePlayer.isFighting() ? "2" : "?"); //Statut (Fighting or not)
                    sb.append(";").append(player.name()); //Player name
                    sb.append(";").append(player.level()); //Level
                    sb.append(";").append("0"); //TODO Alignment
                    sb.append(";").append(player.race().ordinal()); //Race
                    sb.append(";").append(player.gender().ordinal()); //Sex
                    sb.append(";").append(player.race().ordinal()).append(player.gender().ordinal()); //Skin
                }
                ;
            } else {
                sb
                        .append('|')
                        .append(account.pseudo());
            }
        }

        return sb.toString();
    }
}
