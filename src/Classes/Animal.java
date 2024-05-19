package Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Animal implements Serializable {
    private int id;
    private String name;
    private String specie;
    private String gender;
    private double weight;
    private boolean isEnable;

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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean getIsActive() {
        return isEnable;
    }

    public void swapIsActive() {
        isEnable = !isEnable;
    }

    public Animal(int id, String name, String specie, String gender, double weight){
        this.id = id;
        this.name = name;
        this.specie = specie;
        this.gender = gender;
        this.weight = weight;
        this.isEnable = true;
    }

    public Animal(int id, String name, String specie, String gender, double weight, boolean isActive){
        this.id = id;
        this.name = name;
        this.specie = specie;
        this.gender = gender;
        this.weight = weight;
        this.isEnable = isActive;
    }

    @Override
    public String toString() {
        return
            "Animal Details:\n" +
                "ID: " + id + "\n" +
                "Name: " + name + "\n" +
                "Specie: " + specie + "\n" +
                "Gender: " + gender + "\n" +
                "isEnable: " + isEnable + "\n" +
                "Weight: " + weight;
    }

    public static void ExportAllData(ArrayList<People> peoples){
        ArrayList<Vet> vetsData = new ArrayList<Vet>();
        for(People pessoa : peoples) {
            if (pessoa instanceof Vet) {
                vetsData.add((Vet) pessoa);
            }
        }

        String fileData = "";
        for(People pessoa : peoples){
            if(pessoa instanceof Client){
                Client clt = (Client) pessoa;
                for(Animal anm : clt.getAnimals()){
                    int omvId = 0;
                    for(Vet vetInfo : vetsData){
                        if(vetInfo.getAnimals().contains(anm.getId())){
                            omvId = vetInfo.getIdOV();
                        }
                    }
                    fileData += anm.getName() + "," + anm.getId() + "," + anm.getGender() + "," + anm.getSpecie() + "," + anm.getWeight() + "," + clt.getNif() + "," + omvId + "," + anm.getIsActive() + "\n";
                }
            }
        }

        Files.saveData("animals.csv", fileData);
    }
}
