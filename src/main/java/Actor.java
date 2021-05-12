public class Actor {

    private String name;

    private int dob;


    public Actor() {
        this.name = "";
        this.dob = -1;

    }

    public void setName(String name) {
        this.name = name;
    }


    public void setDob(int dob) {
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