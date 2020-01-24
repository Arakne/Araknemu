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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GeolocationServiceTest extends GameBaseCase {
    private GeolocationService service;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        service = new GeolocationService(
            container.get(ExplorationMapService.class),
            container.get(AreaService.class),
            container.get(MapTemplateRepository.class)
        );

        dataSet
            .pushAreas()
            .pushSubAreas()
            .pushMaps()
        ;
    }

    @Test
    void findSuccess() {
        GeolocationService.GeolocationContext context = new GeolocationService.GeolocationContext();
        context.superArea(3);

        ExplorationMap map = service.find(new Geolocation(3, 6), context);

        assertEquals(10340, map.id());
        assertSame(map, service.find(new Geolocation(3, 6), context));
    }

    @Test
    void findWithSubArea() throws SQLException {
        pushMapsOnSameGeoposition();

        GeolocationService.GeolocationContext context = new GeolocationService.GeolocationContext();
        assertEquals(99003, service.find(new Geolocation(42, 24), context.subArea(4)).id());

        assertEquals(99002, service.find(new Geolocation(42, 24), context.subArea(1)).id());
    }

    @Test
    void findWithSuperArea() throws SQLException {
        pushMapsOnSameGeoposition();

        GeolocationService.GeolocationContext context = new GeolocationService.GeolocationContext();
        assertEquals(99002, service.find(new Geolocation(42, 24), context.superArea(0)).id());

        assertEquals(99001, service.find(new Geolocation(42, 24), context.superArea(3)).id());
    }

    @Test
    void findNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.find(new Geolocation(40, 4), new GeolocationService.GeolocationContext()));
    }

    @Test
    void findShouldReturnTheBiggerMap() throws SQLException {
        dataSet
            .pushMap(99001, "", 0, 0, "", "Hhaaeaaaaa", "", new Geolocation(42, 24), 1, false)
            .pushMap(99002, "", 0, 0, "", "HhaaeaaaaaHhaaeaaaaa", "", new Geolocation(42, 24), 1, false)
        ;

        assertEquals(99002, service.find(new Geolocation(42, 24), new GeolocationService.GeolocationContext()).id());
    }

    @Test
    void findWithIndoor() throws SQLException {
        dataSet
            .pushMap(99001, "", 0, 0, "", "Hhaaeaaaaa", "", new Geolocation(42, 24), 1, true)
            .pushMap(99002, "", 0, 0, "", "HhaaeaaaaaHhaaeaaaaa", "", new Geolocation(42, 24), 1, false)
        ;

        assertEquals(99001, service.find(new Geolocation(42, 24), new GeolocationService.GeolocationContext().indoor(true)).id());
    }

    private void pushMapsOnSameGeoposition() throws SQLException {
        dataSet
            .pushMap(99001, "", 0, 0, "", "", "", new Geolocation(42, 24), 440, false)
            .pushMap(99002, "", 0, 0, "", "", "", new Geolocation(42, 24), 1, false)
            .pushMap(99003, "", 0, 0, "", "", "", new Geolocation(42, 24), 4, false)
        ;
    }
}
