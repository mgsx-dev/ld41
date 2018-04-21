package net.mgsx.ld41.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import net.mgsx.ld41.LD41;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(LD41.WIDTH, LD41.HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LD41();
        }
}