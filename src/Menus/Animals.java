package Menus;

import Classes.*;
import pt.gov.cartaodecidadao.*;

import javax.swing.*;
import java.util.ArrayList;

import static Menus.Clients.findClient;

public class Animals {
    public static void showMenu(ArrayList<People> persons){
        int opcao;

        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(null, "Animals Operations\n\n1 - Create Animal\n0 - Previus Menu", "Clients", JOptionPane.INFORMATION_MESSAGE));

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
                            saveAnimal(pp);
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
                        int nif = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the client NIF", "Find Client", JOptionPane.INFORMATION_MESSAGE));
                        Client pp = findClient(nif, persons);
                        if(pp == null){
                            JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        saveAnimal(pp);
                    }
                    break;
                case 2:
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }

    private static void saveAnimal(Client pp){
        String animalName = JOptionPane.showInputDialog(null, "Name of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE);
        int chipId = Integer.parseInt(JOptionPane.showInputDialog(null, "Animal Chip ID", "Create Animal", JOptionPane.INFORMATION_MESSAGE));
        String specie = JOptionPane.showInputDialog(null, "Specie of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE);
        String genre = JOptionPane.showInputDialog(null, "Genre of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE);
        float weight = Float.parseFloat(JOptionPane.showInputDialog(null, "Weight of animal", "Create Animal", JOptionPane.INFORMATION_MESSAGE));

        int confirmAnimalBox = JOptionPane.showConfirmDialog(null, "Please confirm the data bellow:\n\nAnimal Name: " + animalName + "\nChip ID: " + chipId + "\nSpecie: " + specie + "\nGenre: " + genre + "\nWeight: " + weight, "Create Animal", JOptionPane.YES_NO_OPTION);
        if(confirmAnimalBox == 0){
            Animal an = new Animal(chipId, animalName, specie, genre, weight);
            pp.addAnimal(an);
            JOptionPane.showMessageDialog(null, "Animal added with success!", "Create Client", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Please provide the information again!", "Typing error!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
