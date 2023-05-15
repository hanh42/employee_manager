package tdc.edu.vn.quanlynhansu.data_models;

public class Person {
    private String name, degree, hobbies;

    public String getName() {
        return name;
    }

    public String getDegree() {
        return degree;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return name + " # " + degree + " # " + hobbies;
    }
}
