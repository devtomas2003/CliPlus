package Menus;

import Classes.*;
import Utils.CitizenCard;
import pt.gov.cartaodecidadao.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

import static Menus.Clients.findClient;

public class Animals {
    public static void showMenu(ArrayList<People> persons){
        int opcao;

        do {
            opcao = Interactive.readInt("Animals Operations\n\n1 - Create Animal\n2 - List All Animals\n0 - Previus Menu", "Animals");

            switch (opcao){
                case 1:
                    int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                    if(ccRead == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            Client pp = findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            saveAnimal(pp, persons);
                        }catch (PTEID_ExNoReader ex){
                            JOptionPane.showMessageDialog(null, "No Reader Found!", "Find Client", JOptionPane.ERROR_MESSAGE);
                        }catch (PTEID_ExNoCardPresent ex) {
                            JOptionPane.showMessageDialog(null, "No Card Found!", "Find Client", JOptionPane.ERROR_MESSAGE);
                        } catch (PTEID_Exception e) {
                            JOptionPane.showMessageDialog(null, "GOV PT SDK Problem!", "Find Client", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            CitizenCard.release();
                        }
                    }else{
                        int nif = Interactive.readInt("Enter the client NIF", "Find Client");
                        Client pp = findClient(nif, persons);
                        if(pp == null){
                            JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        saveAnimal(pp, persons);
                    }
                    break;
                case 2:
                    if(!persons.isEmpty()){
                        persons.forEach((person) -> {
                            if(person instanceof Client){
                                Client clt = (Client) person;
                                clt.getAnimals().forEach((animal -> {
                                    String toShow = animal.toString();
                                    toShow = toShow + "\nOwner: " + clt.getName();
                                    JOptionPane.showMessageDialog(null, toShow, "Vet", JOptionPane.INFORMATION_MESSAGE);
                                }));
                            }
                        });
                    }else{
                        JOptionPane.showMessageDialog(null, "No records found!", "Animals", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }

    private static void saveAnimal(Client pp, ArrayList<People> peoples){
        String animalName = Interactive.readString("Name of animal", "Create Animal");

        int chipId = 0;
        do{
            chipId = Interactive.readInt("Animal Chip ID", "Create Animal");

            ArrayList<Animal> anms = new ArrayList<Animal>();

            for(People ppData : peoples) {
                if(ppData instanceof Client){
                    ArrayList<Animal> anmsLocal = ((Client) ppData).getAnimals();
                    anms.addAll(anmsLocal);
                }
            }

            Animal anm = Animals.findAnimal(chipId, anms);
            if(anm != null){
                JOptionPane.showMessageDialog(null, "This CHIP ID already exists!", "Create Animal", JOptionPane.ERROR_MESSAGE);
                chipId = 0;
            }
        }while (chipId == 0);

        String specie = Interactive.readString("Specie of animal", "Create Animal");
        String genre = Interactive.readString("Genre of animal", "Create Animal");
        double weight = Interactive.readDouble("Weight of animal", "Create Animal");

        boolean shouldRequestVetId = true;
        Vet vetFound = null;

        while (shouldRequestVetId){
            int vetId = Interactive.readInt("Vet ID", "Create Animal");

            for(People ppData : peoples) {
                if(ppData instanceof Vet){
                    Vet vetInfo = (Vet) ppData;
                    if(vetInfo.getIdOV() == vetId){
                        vetFound = vetInfo;
                    }
                }
            }

            if(vetFound == null){
                JOptionPane.showMessageDialog(null, "No OMV ID found!", "Vets", JOptionPane.ERROR_MESSAGE);
            }else{
                shouldRequestVetId = false;
            }
        }

        if(Objects.equals(animalName, "") || chipId == 0 || Objects.equals(specie, "") || Objects.equals(genre, "") || weight == 0.00){
            JOptionPane.showMessageDialog(null, "Errors found. Please provide the information again!", "Typing error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmAnimalBox = JOptionPane.showConfirmDialog(null, "Please confirm the data bellow:\n\nAnimal Name: " + animalName + "\nChip ID: " + chipId + "\nSpecie: " + specie + "\nGenre: " + genre + "\nWeight: " + weight + "\nVet: " + vetFound.getName(), "Create Animal", JOptionPane.YES_NO_OPTION);
        if(confirmAnimalBox == 0){
            Animal an = new Animal(chipId, animalName, specie, genre, weight);
            pp.addAnimal(an);
            vetFound.addAnimal(chipId);

            Animal.ExportAllData(peoples);

            JOptionPane.showMessageDialog(null, "Animal added with success!", "Create Client", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Please provide the information again!", "Typing error!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public static Animal findAnimal(int chipId, ArrayList<Animal> lAnimals) {
        for(Animal animal : lAnimals) {
            if(animal.getId() == chipId) {
                return animal;
            }
        }
        return null;
    }
}
