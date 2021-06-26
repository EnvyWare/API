package com.envyful.api.math;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * A static utility class for easily generating random values.
 *
 */
public class UtilRandom {

    /**
     *
     * Generates a random integer between the specified minimum and maximum values.
     *
     * @param min The minimum potential value for the random int
     * @param max The maximum potential value for the random int
     * @return The random value generated
     */
    public static int randomInteger(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max + 1 - min) + min;
    }

    /**
     *
     * Generates a random double between 0 and the specified maximum value.
     *
     * @param max The maximum potential value for the random int
     * @return The random value generated
     */
    public static double randomDouble(double max) {
        return randomDouble(0, max);
    }

    /**
     *
     * Generates a random double between the specified minimum and maximum values.
     *
     * @param min The minimum potential value for the random double
     * @param max The maximum potential value for the random double
     * @return The random value generated
     */
    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(max + 1 - min) + min;
    }

    /**
     *
     * Returns a random element from the provided list
     *
     * @param list The list of values
     * @param <T> The type of items in the list
     * @return The random element selected
     */
    public static <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    /**
     *
     * Gets a random element from the provided array
     *
     * @param array The array to select a random value from
     * @param <T> The data type of the array
     * @return The randomly selected element
     */
    public static <T> T getRandomElement(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    /**
     *
     * Gets a random element from the provided array ignoring any values in the {@param excluded} array
     *
     * @param array The array to select a random value from
     * @param excluded The array of values to ignore
     * @param <T> The data type of the two arrays
     * @return The randomly selected element
     */
    public static <T> T getRandomElementExcluding(T[] array, T... excluded) {
        List<T> excludedValues = Arrays.asList(excluded);
        T value = null;

        while (value == null || excludedValues.contains(value)) {
            value = array[ThreadLocalRandom.current().nextInt(array.length)];
        }

        return value;
    }

    /**
     *
     * Gets a random element from the map using the weights provided for higher chances for given elements.
     *
     * @param weights The map of elements with their given weights
     * @param defaultValue The default value to return if none is selected
     * @param <T> The type of the elements
     * @return The randomly selected element
     */
    public static <T> T pickRandomWeighted(Map<T, Double> weights, T defaultValue) {
        return weights.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), -Math.log(ThreadLocalRandom.current().nextDouble()) / entry.getValue()))
                .min(Map.Entry.comparingByValue())
                .map(AbstractMap.SimpleEntry::getKey)
                .orElse(defaultValue);
    }
}
