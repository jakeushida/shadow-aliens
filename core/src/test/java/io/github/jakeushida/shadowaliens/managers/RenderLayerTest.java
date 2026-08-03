package io.github.jakeushida.shadowaliens.managers;

import io.github.jakeushida.shadowaliens.rendering.RenderLayer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RenderLayerTest {
    @Test
    public void testEnumValuesPresent() {
        RenderLayer[] vals = RenderLayer.values();
        boolean hasBackground = false, hasShips = false, hasProjectiles = false, hasUI = false;
        for (RenderLayer r : vals) {
            if (r == RenderLayer.BACKGROUND) hasBackground = true;
            if (r == RenderLayer.SHIPS) hasShips = true;
            if (r == RenderLayer.PROJECTILES) hasProjectiles = true;
            if (r == RenderLayer.UI) hasUI = true;
        }
        assertTrue(hasBackground);
        assertTrue(hasShips);
        assertTrue(hasProjectiles);
        assertTrue(hasUI);
    }
}
