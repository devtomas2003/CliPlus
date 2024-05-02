package Classes;

import java.util.ArrayList;

public class People {
    private int nif;
    private String name;
    private String contact;
    private ArrayList<Animal> animais;

    public int getNif() {
        return nif;
    }

    public void setNif(int nif) {
        this.nif = nif;
    }

    public String getName() {
        return name;
    }

    public void setNome(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContato(String contact) {
        this.contact = contact;
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

    public People(int nif, String name, String contato) {
        this.nif = nif;
        this.name = name;
        this.contact = contato;
    }

    @Override
    public String toString() {
        return
            "Client Details:\n" +
            "NIF: " + nif + "\n" +
            "Nome: " + name + "\n" +
            "Contato: " + contact;
    }
}
