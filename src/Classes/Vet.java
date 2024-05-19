package Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Vet extends People implements Serializable {
    private int idOV;

    private ArrayList<Integer> animals = new ArrayList<Integer>();
    public Vet(int Nif, String name, int idOV, String Contact){
        super(Nif, name, Contact);
        this.idOV = idOV;
    }

    public int getIdOV() {
        return idOV;
    }

    public void setIdOV(int idOV) {
        this.idOV = idOV;
    }

    public ArrayList<Integer> getAnimals() {
        return animals;
    }

    public void setAnimals(ArrayList<Integer> animals) {
        this.animals = animals;
    }

    public void addAnimal(int idAnimal) {
        this.animals.add(idAnimal);
    }

    @Override
    public String toString() {
        return
            "Vet Details:\n" +
            "NIF: " + this.getNif() + "\n" +
            "Name: " + this.getName() + "\n" +
            "Contact: " + this.getContact() + "\n" +
            "OMV ID: " + idOV;
    }
}
