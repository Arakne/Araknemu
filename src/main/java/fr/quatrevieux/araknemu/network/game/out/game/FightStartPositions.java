package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.util.Base64;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Send the start positions on fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L145
 */
final public class FightStartPositions {
    final private List<Integer>[] places;
    final private int team;

    public FightStartPositions(List<Integer>[] places, int team) {
        this.places = places;
        this.team = team;
    }

    @Override
    public String toString() {
        return "GP" +
            Arrays.stream(places)
                .map(
                    list -> list
                        .stream()
                        .map(i -> Base64.encode(i, 2))
                        .collect(Collectors.joining())
                )
                .collect(Collectors.joining("|")) +
            "|" + team
        ;

    }
}
