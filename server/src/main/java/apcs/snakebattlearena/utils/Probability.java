package apcs.snakebattlearena.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Generates a value based on set of weighted probabilities.
 * @param <V> Item value type
 */
public class Probability<V> {
    private final Random random = new Random();
    private final ArrayList<Item> items = new ArrayList<>();

    /**
     * Add a value to the set of possible values. The sum of probabilities must not exceed 1.0
     * @param value       The target value
     * @param probability The probability of the value being generated.
     *                    Should be in the range of <code>(0.0, 1.0]</code>
     */
    public void addValue(V value, double probability) {
        // Check that the combined probability of all items does not go over 1.0
        double probabilitySum = items.stream().reduce(0.0,
                (acc, item) -> acc + item.getProbability() * item.getItemCount(),
                Double::sum);
        if (probability > 1 || probabilitySum + probability > 1) {
            throw new IllegalArgumentException("Cannot go over 1.0 total probability!");
        }

        Item newItem = new Item(value, probability);
        int insertIndex = Collections.binarySearch(items, newItem); // Search by probability

        // If specified probability doesn't exist then add the new item,
        // otherwise add the value to the existing item.
        if (insertIndex < 0) {
            items.add(-insertIndex - 1, newItem);
        } else {
            items.get(insertIndex).addValue(value);
        }
    }

    /**
     * Generates a random value from the set of possible weighted values.
     */
    public V getRandomValue() {
        double randomProbability = random.nextDouble();
        double probabilitySum = 0;

        for (Item item : items) {
            probabilitySum += item.getProbability();

            if (randomProbability <= probabilitySum) {
                return item.getRandomValue();
            }
        }

        throw new IllegalStateException("Could not generate a random value!");
    }

    private class Item implements Comparable<Item> {
        private final double probability;
        private final ArrayList<V> values;

        public Item(V value, double probability) {
            this.probability = probability;
            this.values = new ArrayList<>(5);
            this.values.add(value);
        }

        public void addValue(V value) {
            values.add(value);
        }

        public V getRandomValue() {
            return values.size() <= 1
                    ? values.get(0)
                    : values.get(random.nextInt(values.size()));
        }

        public double getProbability() {
            return probability;
        }

        public int getItemCount() {
            return values.size();
        }

        @Override
        public int compareTo(@NotNull Probability<V>.Item o) {
            return Double.compare(this.probability, o.getProbability());
        }
    }
}
