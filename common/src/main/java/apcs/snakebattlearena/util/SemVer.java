package apcs.snakebattlearena.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a semantic version based on 3 components. ie: (1.2.3)
 */
public final class SemVer implements Comparable<SemVer> {
    private int major, minor, patch;

    /**
     * Creates a SemVer directly from the components. All components must be >=0.
     */
    public SemVer(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version cannot be below 0!");
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Parse a SemVer from a string.
     * @param version The SemVer as a string.
     * @throws IllegalArgumentException When the input version is not a valid SemVer version.
     */
    public SemVer(String version) {
        // Split by dot (.)
        String[] parts = version.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Version has incorrect amount of parts!");
        }

        for (int i = 0; i < 3; i++) {
            int parsed;

            try {
                parsed = Integer.parseInt(parts[i]);
            } catch (NumberFormatException ignored) {
                throw new IllegalArgumentException("Version contains invalid part!");
            }

            if (parsed < 0) {
                throw new IllegalArgumentException("Version cannot be below 0!");
            }

            switch (i) {
                case 0: {
                    major = parsed;
                    break;
                }
                case 1: {
                    minor = parsed;
                    break;
                }
                case 2: {
                    patch = parsed;
                    break;
                }
            }
        }
    }

    /**
     * Get the major component of the version (0.x.x)
     */
    public int getMajor() {
        return major;
    }

    /**
     * Get the minor component of the version (x.0.x)
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Get the patch component of the version (x.x.0)
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Checks for a subset of the versions to be matching.
     * @param other       The version to compare against.
     * @param ignoreMinor Ignore the minor version to allow different values.
     *                    This implicitly enables {@param ignorePatch}.
     * @param ignorePatch Ignore the patch version to allow different value.
     */
    public boolean equalsIgnore(@NotNull SemVer other,
                                boolean ignoreMinor,
                                boolean ignorePatch) {
        if (!ignoreMinor && minor != other.minor)
            return false;
        if (!(ignoreMinor || ignorePatch) && patch != other.patch)
            return false;

        return major == other.major;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemVer)) return false;

        SemVer other = (SemVer) o;

        return major == other.major &&
                minor == other.minor &&
                patch == other.patch;
    }

    @Override
    public int compareTo(@Nullable SemVer other) {
        Objects.requireNonNull(other);

        for (int i = 0; i < 3; i++) {
            int cmp1 = -1, cmp2 = -1;

            // Get the correct set of values to compare (in order) major, minor, patch
            switch (i) {
                case 0: {
                    cmp1 = major;
                    cmp2 = other.major;
                    break;
                }
                case 1: {
                    cmp1 = minor;
                    cmp2 = other.minor;
                    break;
                }
                case 2: {
                    cmp1 = patch;
                    cmp2 = other.patch;
                    break;
                }
                default:
            }

            int cmpResult = Integer.compare(cmp1, cmp2);

            if (cmpResult != 0) return cmpResult;
        }

        return 0;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }
}
