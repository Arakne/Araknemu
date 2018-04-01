package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.FightHandler;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

import java.util.Arrays;
import java.util.Collection;

/**
 * Invitation for a challenge fight (duel)
 */
final public class ChallengeInvitation implements Interaction {
    final private ExplorationPlayer initiator;
    final private ExplorationPlayer challenger;
    final private FightHandler<ChallengeBuilder> fightHandler;

    public ChallengeInvitation(ExplorationPlayer initiator, ExplorationPlayer challenger, FightHandler<ChallengeBuilder> fightHandler) {
        this.initiator = initiator;
        this.challenger = challenger;
        this.fightHandler = fightHandler;
    }

    /**
     * Get the challenge initiator (i.e. who ask challenge)
     */
    public ExplorationPlayer initiator() {
        return initiator;
    }

    /**
     * Get the challenger (i.e. the invitation target)
     */
    public ExplorationPlayer challenger() {
        return challenger;
    }

    /**
     * Get all invitation interlocutors
     */
    public Collection<ExplorationPlayer> interlocutors() {
        return Arrays.asList(initiator, challenger);
    }

    @Override
    public Interaction start() {
        if (!check()) {
            return null;
        }

        challenger.interactions().start(new ChallengerDialog(this));

        return new InitiatorDialog(this).start();
    }

    @Override
    public void stop() {
        interlocutors().forEach(player -> player.interactions().remove());
    }

    /**
     * Cancel / refuse the challenge invitation
     *
     * @param dialog The refuse dialog
     */
    public void cancel(ChallengeDialog dialog) {
        stop();

        send(
            new GameActionResponse(
                "",
                ActionType.REFUSE_CHALLENGE,
                Integer.toString(dialog.self().id()),
                new Object[] {dialog.interlocutor().id()}
            )
        );
    }

    /**
     * Accept the challenge invitation and start the fight
     *
     * @param dialog The accept dialog
     */
    public void accept(ChallengerDialog dialog) {
        stop();

        send(
            new GameActionResponse(
                "",
                ActionType.ACCEPT_CHALLENGE,
                Integer.toString(dialog.self().id()),
                new Object[] {dialog.interlocutor().id()}
            )
        );

        fightHandler.start(builder -> {
            builder
                .map(initiator.map())
                .fighter(initiator.player())
                .fighter(challenger.player())
            ;
        });
    }

    /**
     * Send packet to all interlocutors
     */
    private void send(Object packet) {
        interlocutors().forEach(player -> player.send(packet));
    }

    /**
     * Check if challenge invitation is valid
     */
    private boolean check() {
        if (initiator.interactions().busy()) {
            return error(JoinFightError.CANT_YOU_R_BUSY);
        }

        if (challenger.interactions().busy()) {
            return error(JoinFightError.CANT_YOU_OPPONENT_BUSY);
        }

        if (initiator.map() != challenger.map()) {
            return error(JoinFightError.CANT_BECAUSE_MAP);
        }

        if (!initiator.map().canLaunchFight()) {
            return error(JoinFightError.CANT_BECAUSE_MAP);
        }

        return true;
    }

    private boolean error(JoinFightError error) {
        initiator.send(
            new GameActionResponse(
                "",
                ActionType.JOIN_FIGHT_ERROR,
                Integer.toString(initiator.id()),
                new Object[] {Character.toString(error.error())}
            )
        );

        return false;
    }
}
