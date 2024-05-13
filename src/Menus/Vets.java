package Menus;

import Classes.*;
import pt.gov.cartaodecidadao.PTEID_EId;
import pt.gov.cartaodecidadao.PTEID_ExNoCardPresent;
import pt.gov.cartaodecidadao.PTEID_ExNoReader;
import pt.gov.cartaodecidadao.PTEID_Exception;

import javax.swing.*;
import java.util.ArrayList;

public class Vets {
    public static void showMenu(ArrayList<People> peoples){
        int opcao;

        do {
            opcao = Interactive.readInt("Vets Operations\n\n1 - Create Vet\n2 - List All Vets\n3 - List Animals associated with vets\n0 - Previus Menu", "Vets");

            switch (opcao){
                case 1:
                    int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the data from the citizen card?", "Create Vet", JOptionPane.YES_NO_OPTION);
                    if(ccRead == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            String name = eid.getGivenName() + " " + eid.getSurname();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            int idOV = Interactive.readInt("ID of Ordem dos Medicos Veterinarios", "Create Vet");
                            String contact = JOptionPane.showInputDialog(null, "Contact", "Create Vet", JOptionPane.INFORMATION_MESSAGE);
                            Address address = askAddress();
                            Vet pp = new Vet(nif, name, idOV, contact);
                            confirmVet(address, pp, peoples);
                        }catch (PTEID_ExNoReader ex){
                            JOptionPane.showMessageDialog(null, "No Reader Found!", "Create Vet", JOptionPane.ERROR_MESSAGE);
                        }catch (PTEID_ExNoCardPresent ex) {
                            JOptionPane.showMessageDialog(null, "No Card Found!", "Create Vet", JOptionPane.ERROR_MESSAGE);
                        } catch (PTEID_Exception e) {
                            JOptionPane.showMessageDialog(null, "GOV PT SDK Problem!", "Create Vet", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            CitizenCard.release();
                        }
                    }else{
                        String name = JOptionPane.showInputDialog(null, "Name", "Create Vet", JOptionPane.INFORMATION_MESSAGE);
                        int nif = Interactive.readInt("NIF", "Create Vet");
                        int idOV = Interactive.readInt("ID of Ordem dos Medicos Veterinarios", "Create Vet");
                        String contact = JOptionPane.showInputDialog(null, "Contact", "Create Vet", JOptionPane.INFORMATION_MESSAGE);
                        Vet pp = new Vet(nif, name, idOV, contact);
                        Address address = askAddress();
                        confirmVet(address, pp, peoples);
                    }
                    break;
                case 2:
                    boolean isInside = false;
                    for(People pp : peoples){
                        if(pp instanceof Vet){
                            isInside = true;
                            Vet vetInfo = (Vet) pp;
                            JOptionPane.showMessageDialog(null, vetInfo.toString(), "Vet", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    if(!isInside) {
                        JOptionPane.showMessageDialog(null, "No records found!", "Vet", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 3:
                    for(People pp : peoples){
                        if(pp instanceof Vet){
                            Vet vetData = (Vet) pp;
                            String txtToShow = vetData.toString() + "\n\n--- Animals ---\n";
                            final StringBuilder finalTxtToShow = new StringBuilder(txtToShow);

                            vetData.getAnimals().forEach((animalId -> {
                                for(People ppData : peoples) {
                                    if (ppData instanceof Client clientData) {
                                        Animal animal = findAnimal(animalId, clientData.getAnimais());
                                        if(animal != null){
                                            finalTxtToShow.append(animal.toString()).append("\n");
                                        }
                                    }
                                }

                            }));

                            JOptionPane.showMessageDialog(null, finalTxtToShow.toString(), "Animals associated with vets", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }

    private static void confirmVet(Address address, Vet vet, ArrayList<People> allPeople){
        int confirmData = JOptionPane.showConfirmDialog(null, "Please confirm the data bellow:\n\nName: " + vet.getName() + "\nNIF: " + vet.getNif() + "\nContact: " + vet.getContact() + "\nStreet: " + address.getNstreet() + "\nDoor: " + address.getndoor() + "\nZip Code: " + address.getZipCode() + "\nLocality: " + address.getNlocality(), "Create Vet", JOptionPane.YES_NO_OPTION);
        vet.setAddress(address);
        if(confirmData == 0){
            allPeople.add(vet);
            String fileData = "";
            for(People pp : allPeople){
                if(pp instanceof Vet){
                    Vet vetInfo = (Vet) pp;
                    fileData += vetInfo.getNif() + "," + vetInfo.getName() + "," + vetInfo.getContact() + "," + vetInfo.getAddress().getNstreet() + "," + vetInfo.getAddress().getZipCode() + "," + vetInfo.getAddress().getndoor() + "," + vetInfo.getAddress().getNlocality() + "," + vetInfo.getIdOV() + "\n";
                }
            }
            Files.saveData("vets.csv", fileData);
            JOptionPane.showMessageDialog(null, "Vet added with success!", "Create Vet", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Please provide the information again!", "Typing error!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static Address askAddress(){
        String street = JOptionPane.showInputDialog(null, "Street", "Create Vet", JOptionPane.INFORMATION_MESSAGE);
        int door = Interactive.readInt("Door", "Create Vet");
        int ZipCode = Interactive.readInt("Zip Code", "Create Vet");
        String Nlocality = JOptionPane.showInputDialog(null, "Locality", "Create Vet", JOptionPane.INFORMATION_MESSAGE);
        return new Address(street, door, ZipCode, Nlocality);
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
