package photos.model;

import java.io.Serializable;

/**
 * Represents a name/value tag attached to a photo, for example
 * {@code person=alice} or {@code location=Prague}.
 *
 * Tags are serializable as part of the photo state.
 *
 * @author Kenneth Yan
 * @author Wilmer Joya
 * @version 1.0
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String name;
    public final String value;

    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override public String toString() { return name + "=" + value; }
}
