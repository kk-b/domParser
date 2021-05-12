
public class Actor {

    private final String name;

    private final int dob;


    public Actor(String name, int dob) {
        this.name = name;
        this.dob = dob;

    }

    public String getName() {
        return name;
    }

    public int getDob() {
        return dob;
    }

    public String toString() {

        return "Name:" + getName() + ", " +
                "DOB:" + getDob() + ", ";
    }
}
