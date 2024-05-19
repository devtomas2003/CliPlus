package Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Client extends People implements Serializable {
    private ArrayList<Animal> animals;

    public Client(int nif, String name, String contato) {
        super(nif, name, contato);
        animals = new ArrayList<Animal>();
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }
}
