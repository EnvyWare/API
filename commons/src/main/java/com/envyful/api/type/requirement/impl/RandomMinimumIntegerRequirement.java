package com.envyful.api.type.requirement.impl;

import com.envyful.api.math.UtilRandom;

/**
 *
 * Random minimum integer implementation of the {@link com.envyful.api.type.requirement.Requirement} interface.
 * Using the {@link MinimumIntegerRequirement} but it randomly selects the minimum value based on the min
 * and max provided in the constructor
 *
 */
public class RandomMinimumIntegerRequirement extends MinimumIntegerRequirement {

    public RandomMinimumIntegerRequirement(int min, int max) {
        super(UtilRandom.randomInteger(min, max));
    }
}
