/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.ApplySpellOnStartTurnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.object.AddGlyphHandler;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.event.SpectatorJoined;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RemoveBattlefieldObjects;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.SendBattlefieldObjectsToSpectator;
import fr.quatrevieux.araknemu.game.spell.SpellService;

/**
 * Module for register fight effects which apply another spell effects
 * So all effects which needs {@link SpellService} should be registered here
 */
public final class IndirectSpellApplyEffectsModule implements FightModule {
    private final Fight fight;
    private final SpellService spellService;

    public IndirectSpellApplyEffectsModule(Fight fight, SpellService spellService) {
        this.fight = fight;
        this.spellService = spellService;
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(787, new ApplySpellOnStartTurnHandler(fight, spellService));
        handler.register(401, new AddGlyphHandler(fight, spellService));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new RemoveBattlefieldObjects(fight),
            new Listener<SpectatorJoined>() {
                @Override
                public void on(SpectatorJoined event) {
                    final Spectator spectator = event.spectator();

                    spectator.dispatcher().add(new SendBattlefieldObjectsToSpectator(spectator));
                }

                @Override
                public Class<SpectatorJoined> event() {
                    return SpectatorJoined.class;
                }
            },
        };
    }
}
