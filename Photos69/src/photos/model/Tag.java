package photos.model;

import java.io.Serializable;

/**
 * Represents a tag associated with a photo.
 * A tag consists of a name and a value.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String name;
    public final String value;

    /**
     * Constructs a new Tag with the specified name and value.
     * 
     * @param name the name of the tag
     * @param value the value of the tag
     */
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns a string representation of the tag in the format "name=value".
     * 
     * @return the string representation of the tag
     */
    @Override
    public String toString() {
        return name + "=" + value;
    }
}
