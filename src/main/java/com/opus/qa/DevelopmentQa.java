package com.opus.qa;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Optional task selector for focused development-server QA.
 * Probes run ONLY when explicitly chosen with {@code -Dopus.qa.only=<task>};
 * a plain dev-world launch never executes the world-mutating contract probes.
 */
public final class DevelopmentQa {
    private DevelopmentQa() { }
    public static boolean enabled(int task) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return false;
        String only = System.getProperty("opus.qa.only", "").trim();
        return !only.isEmpty() && only.equals(Integer.toString(task));
    }
}
