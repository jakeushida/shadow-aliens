package io.github.jakeushida.shadowaliens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

/**
 * Base class for tests that need libGDX initialized.
 * Provides headless backend and mocked GL20.
 */
public abstract class GdxTestRunner {
    private static HeadlessApplication application;

    @BeforeAll
    public static void initGdx() {
        if (application == null) {
            application = new HeadlessApplication(new ApplicationAdapter() {});
            Gdx.gl = Mockito.mock(GL20.class);
            Gdx.gl20 = Mockito.mock(GL20.class);
        }
    }

    @AfterAll
    public static void cleanupGdx() {
        if (application != null) {
            application.exit();
            application = null;
        }
    }
}
