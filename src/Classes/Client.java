package Classes;

import java.util.ArrayList;

public class Client extends People {
    private ArrayList<Animal> animais;

    public Client(int nif, String name, String contato) {
        super(nif, name, contato);
        animais = new ArrayList<Animal>();
    }

    public void addAnimal(Animal animal) {
        animais.add(animal);
    }

    public void removeAnimal(int animalId){
        animais.forEach((animal -> {
            if(animal.getId() == animalId){
                animais.remove(animal);
            }
        }));
    }
    public ArrayList<Animal> getAnimais() {
        return animais;
    }
}
