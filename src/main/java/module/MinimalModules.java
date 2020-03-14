/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static module.ModuleName.*;

/**
 *
 * @author hasan
 */
public class MinimalModules {
    
    private static final List<ModuleName> minimalModules = new ArrayList<>();
    
    static {
        minimalModules.addAll(
            Arrays.asList(
                DARKSKY_MINIMAL,
                CLOCK_MINIMAL,
                SPOTIFY_MINIMAL,
                COVID_MINIMAL
            )
        );
    }
    
    public static List<ModuleName> getModules() {
        return minimalModules;
    }
}
