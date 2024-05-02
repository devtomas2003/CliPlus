package Classes;

public class Animal {
    private int id;
    private String name;
    private String specie;
    private String gender;
    private float weight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Animal(int id, String name, String specie, String gender, float weight){
        this.id = id;
        this.name = name;
        this.specie = specie;
        this.gender = gender;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return
            "Animal Details:\n" +
                "ID: " + id + "\n" +
                "Name: " + name + "\n" +
                "Specie: " + specie + "\n" +
                "Gender: " + gender + "\n" +
                "Weight: " + weight;
    }
}
