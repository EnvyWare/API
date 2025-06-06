package com.envyful.api.neoforge.gui.ticker;

import com.envyful.api.gui.pane.Pane;
import com.envyful.api.gui.pane.TickHandler;
import com.envyful.api.platform.PlatformProxy;

import java.util.function.Consumer;

/**
 *
 * A Forge implementation of the {@link TickHandler} interface
 *
 */
public class ForgeGuiTickHandler implements TickHandler {

    protected final boolean async;
    protected final int initialDelay;
    protected final int repeatDelay;
    protected final Consumer<Pane> handler;

    protected int ticks = 0;
    protected int lastRun = -1;

    public ForgeGuiTickHandler(boolean async, int initialDelay, int repeatDelay, Consumer<Pane> handler) {
        this.async = async;
        this.initialDelay = initialDelay;
        this.repeatDelay = repeatDelay;
        this.handler = handler;
    }

    @Override
    public void tick(Pane pane) {
        this.ticks++;

        if (!this.shouldRun()) {
            return;
        }

        this.lastRun = this.ticks;

        if (this.async) {
            this.handler.accept(pane);
        } else {
            PlatformProxy.runSync(() -> this.handler.accept(pane));
        }
    }

    private boolean shouldRun() {
        if (this.lastRun == -1) {
            if (this.initialDelay == 0) {
                return true;
            }

            return this.ticks >= this.initialDelay;
        }

        return (this.ticks - this.lastRun) >= this.repeatDelay;
    }

    public static class Builder implements TickHandler.Builder {

        protected boolean async = true;
        protected int initialDelay = 0;
        protected int repeatDelay = -1;
        protected Consumer<Pane> handler;

        public Builder() {
        }

        @Override
        public TickHandler.Builder async() {
            this.async = true;
            return this;
        }

        @Override
        public TickHandler.Builder sync() {
            this.async = false;
            return this;
        }

        @Override
        public TickHandler.Builder initialDelay(int ticks) {
            this.initialDelay = ticks;
            return this;
        }

        @Override
        public TickHandler.Builder repeatDelay(int ticks) {
            this.repeatDelay = ticks;
            return this;
        }

        @Override
        public TickHandler.Builder handler(Consumer<Pane> tickHandler) {
            this.handler = tickHandler;
            return this;
        }

        @Override
        public TickHandler build() {
            return new ForgeGuiTickHandler(this.async, this.initialDelay, this.repeatDelay, this.handler);
        }
    }
}
