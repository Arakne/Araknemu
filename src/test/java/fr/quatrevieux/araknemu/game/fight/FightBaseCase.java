package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.module.StatesModule;
import fr.quatrevieux.araknemu.game.fight.state.*;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.slot.WeaponSlot;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FightBaseCase extends GameBaseCase {
    protected GamePlayer player;
    protected GamePlayer other;

    private int lastPlayerId = 5;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        player = gamePlayer(true);
        other  = makeOtherPlayer();
    }

    public Fight createFight(boolean init) throws Exception {
        Fight fight = new Fight(
            1,
            new ChallengeType(),
            container.get(FightService.class).map(
                container.get(ExplorationMapService.class).load(10340)
            ),
            new ArrayList<>(Arrays.asList(
                createTeam0(),
                createTeam1()
            )),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                new PlacementState(false),
                new ActiveState(),
                new FinishState()
            )
        );

        fight.register(new StatesModule(fight));

        if (init) {
            fight.nextState();
        }

        return fight;
    }

    public Fight createFight() throws Exception {
        return createFight(true);
    }

    public FightTeam createTeam0() {
        return new SimpleTeam(
            new PlayerFighter(player),
            Arrays.asList(122, 123, 124),
            0
        );
    }

    public FightTeam createTeam1() {
        return new SimpleTeam(
            new PlayerFighter(other),
            Arrays.asList(125, 126, 127),
            0
        );
    }

    public Fight createSimpleFight(ExplorationMap map) throws ContainerException, SQLException {
        GamePlayer player1 = makeSimpleGamePlayer(++lastPlayerId);
        GamePlayer player2 = makeSimpleGamePlayer(++lastPlayerId);

        return container.get(FightService.class)
            .handler(ChallengeBuilder.class)
            .start(builder -> builder
                .map(map)
                .fighter(player1)
                .fighter(player2)
            )
        ;
    }

    public void equipWeapon(GamePlayer player, int weaponId) throws ContainerException, InventoryException, SQLException {
        dataSet.pushItemTemplates();
        Item weapon = container.get(ItemService.class).create(weaponId);

        player.inventory().add(weapon, 1, WeaponSlot.SLOT_ID);
    }

    public void equipWeapon(GamePlayer player) throws ContainerException, InventoryException, SQLException {
        equipWeapon(player, 40);
    }

    public CastScope makeCastScope(Fighter caster, Castable castable, SpellEffect effect, FightCell target) {
        return new CastScope(castable, caster, target).withEffects(Collections.singletonList(effect));
    }

    public CastScope makeCastScopeForEffect(int effectId) {
        return makeCastScopeForEffect(effectId, player.fighter(), other.fighter().cell());
    }

    public CastScope makeCastScopeForEffect(int effectId, Fighter caster, FightCell target) {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(effectId);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        return makeCastScope(caster, spell, effect, target);
    }
}
