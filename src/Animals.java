import Classes.*;
import pt.gov.cartaodecidadao.*;

import javax.swing.*;
import java.util.ArrayList;

import static Menus.Clients.findClient;

public class Animals {
    public static void showMenu(ArrayList<People> persons, ArrayList<Vet> vets){
        int opcao;

        do {
            opcao = Interactive.readInt("Animals Operations\n\n1 - Create Animal\n2 - List All Animals\n 3 - List all interventions for an animal\n0 - Previus Menu", "Clients");

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
                            saveAnimal(pp, vets);
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
                        saveAnimal(pp, vets);
                    }
                    break;
                case 2:
                    if(!persons.isEmpty()){
                        persons.forEach((person) -> {
                            Client clt = (Client) person;
                            clt.getAnimais().forEach((animal -> {
                                String toShow = animal.toString();
                                toShow = toShow + "\nOwner: " + clt.getName();
                                JOptionPane.showMessageDialog(null, toShow, "Vet", JOptionPane.INFORMATION_MESSAGE);
                            }));
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

    private static void saveAnimal(Client pp, ArrayList<Vet> vets){
        String animalName = JOptionPane.showInputDialog(null, "Name of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE);
        int chipId = Interactive.readInt("Animal Chip ID", "Create Animal");
        String specie = JOptionPane.showInputDialog(null, "Specie of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE);
        String genre = JOptionPane.showInputDialog(null, "Genre of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE);
        float weight = Float.parseFloat(JOptionPane.showInputDialog(null, "Weight of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE));

        boolean shouldRequestVetId = true;
        Vet vetFound = null;

        while (shouldRequestVetId){
            int vetId = Interactive.readInt("Vet ID", "Create Animal");

            for(Vet vet : vets) {
                if(vet.getIdOV() == vetId){
                    vetFound = vet;
                }
            }

            if(vetFound == null){
                JOptionPane.showMessageDialog(null, "No OMV ID found!", "Vets", JOptionPane.ERROR_MESSAGE);
            }else{
                shouldRequestVetId = false;
            }
        }

        int confirmAnimalBox = JOptionPane.showConfirmDialog(null, "Please confirm the data bellow:\n\nAnimal Name: " + animalName + "\nChip ID: " + chipId + "\nSpecie: " + specie + "\nGenre: " + genre + "\nWeight: " + weight + "\nVet: " + vetFound.getName(), "Create Animal", JOptionPane.YES_NO_OPTION);
        if(confirmAnimalBox == 0){
            Animal an = new Animal(chipId, animalName, specie, genre, weight);
            pp.addAnimal(an);
            vetFound.addAnimal(chipId);

            JOptionPane.showMessageDialog(null, "Animal added with success!", "Create Client", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Please provide the information again!", "Typing error!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
