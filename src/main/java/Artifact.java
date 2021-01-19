
public class Artifact {

    private String id;
    private String name;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Eser{id='%s', name='%s'}", this.id, this.name);
    }
}
