package Menus;

import Classes.*;
import Utils.CitizenCard;
import pt.gov.cartaodecidadao.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Clients {
    public static void showMenu(ArrayList<People> persons){
        int opcao;

        do {
            opcao = Interactive.readInt("Clients Operations\n\n1 - Create Client\n2 - List Animals of a Client\n3 - Delete Animal\n0 - Previus Menu", "Clients");

            switch (opcao){
                case 1:
                    int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the data from the citizen card?", "Create Client", JOptionPane.YES_NO_OPTION);
                    if(ccRead == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            String name = eid.getGivenName() + " " + eid.getSurname();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            for(People pp : persons){
                                if(pp instanceof Client){
                                    if(pp.getNif() == nif){
                                        JOptionPane.showMessageDialog(null, "This client already exists!", "Create Client", JOptionPane.ERROR_MESSAGE);
                                        break;
                                    }
                                }
                            }
                            String contact = Interactive.readString("Contact", "Create Client");
                            Address address = askAddress();
                            Client pp = new Client(nif, name, contact);
                            confirmPerson(address, pp, persons);
                        }catch (PTEID_ExNoReader ex){
                            JOptionPane.showMessageDialog(null, "No Reader Found!", "Create Client", JOptionPane.ERROR_MESSAGE);
                        }catch (PTEID_ExNoCardPresent ex) {
                            JOptionPane.showMessageDialog(null, "No Card Found!", "Create Client", JOptionPane.ERROR_MESSAGE);
                        } catch (PTEID_Exception e) {
                            JOptionPane.showMessageDialog(null, "GOV PT SDK Problem!", "Create Client", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            CitizenCard.release();
                        }
                    }else{
                        String name = Interactive.readString("Name", "Create Client");
                        int nif = 0;
                        do{
                            nif = Interactive.readInt("NIF", "Create Client");
                            if(String.valueOf(nif).length() != 9){
                                nif = 0;
                                JOptionPane.showMessageDialog(null, "Invalid VAT Number!", "VAT Validation", JOptionPane.ERROR_MESSAGE);
                            }
                        }while (nif == 0);
                        String contact = Interactive.readString("Contact", "Create Client");
                        Client pp = new Client(nif, name, contact);
                        Address address = askAddress();
                        confirmPerson(address, pp, persons);
                    }
                    break;
                case 2:
                    int readCC = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Create Client", JOptionPane.YES_NO_OPTION);
                    if(readCC == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            Client pp = findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            if(!pp.getAnimals().isEmpty()){
                                pp.getAnimals().forEach((animal) -> {
                                    JOptionPane.showMessageDialog(null, animal.toString(), "Animal", JOptionPane.INFORMATION_MESSAGE);
                                });
                            }else{
                                JOptionPane.showMessageDialog(null, "No records found!", "Animals", JOptionPane.ERROR_MESSAGE);
                            }
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
                        if(!pp.getAnimals().isEmpty()){
                            pp.getAnimals().forEach((animal) -> {
                                JOptionPane.showMessageDialog(null, animal.toString(), "Animal", JOptionPane.INFORMATION_MESSAGE);
                            });
                        }else{
                            JOptionPane.showMessageDialog(null, "No records found!", "Animals", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;
                case 3:
                    int CCRead = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                    if(CCRead == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            Client pp = findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            deleteAnimal(nif, persons);
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
                        deleteAnimal(nif, persons);
                    }
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }
    private static Address askAddress(){
        String street = Interactive.readString("Street", "Create Client");
        int door = Interactive.readInt("Door", "Create Client");
        int zipCode = 0;
        do{
           try{
               String ZipCode = Interactive.readString("Zip Code", "Create Client").replace("-", "");
               zipCode = Integer.parseInt(ZipCode);
               if(ZipCode.length() != 7){
                   JOptionPane.showMessageDialog(null, "ZIP Code with invalid format", "ZIP Code Validate", JOptionPane.ERROR_MESSAGE);
                   zipCode = 0;
               }
           }catch (NumberFormatException nfe){
               JOptionPane.showMessageDialog(null, "Invalid ZIP Code", "ZIP Code Validation", JOptionPane.ERROR_MESSAGE);
           }
        }while (zipCode == 0);
        String Nlocality = Interactive.readString("Locality", "Create Client");
        return new Address(street, door, zipCode, Nlocality);
    }
    private static void confirmPerson(Address address, Client clt, ArrayList<People> allPeople){
        if(clt.getNif() == 0 || Objects.equals(clt.getName(), "") || Objects.equals(clt.getContact(), "") || Objects.equals(address.getNstreet(), "") || address.getndoor() == 0 || address.getZipCode() == 0 || Objects.equals(address.getNlocality(), "")){
            JOptionPane.showMessageDialog(null, "Errors found. Please provide the information again!", "Typing error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmData = JOptionPane.showConfirmDialog(null, "Please confirm the data bellow:\n\nName: " + clt.getName() + "\nNIF: " + clt.getNif() + "\nContact: " + clt.getContact() + "\nStreet: " + address.getNstreet() + "\nDoor: " + address.getndoor() + "\nZip Code: " + address.getZipCode() + "\nLocality: " + address.getNlocality(), "Create Client", JOptionPane.YES_NO_OPTION);
        if(confirmData == 0){
            clt.setAddress(address);
            allPeople.add(clt);

            ExportClients(allPeople);
            JOptionPane.showMessageDialog(null, "Client added with success!", "Create Client", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Please provide the information again!", "Typing error!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public static Client findClient(int nif, ArrayList<People> lPeople) {
        for(People person : lPeople) {
            if(person instanceof Client){
                if(person.getNif() == nif) {
                    return (Client) person;
                }
            }
        }
        return null;
    }

    private static void deleteAnimal(int nif, ArrayList<People> peoples){
        for(People pp : peoples){
            if(pp instanceof Client){
                if(pp.getNif() == nif){
                    HashMap<Integer, Integer> codigos = new HashMap<Integer, Integer>();
                    Client clt = (Client) pp;
                    String txtToShow = "Choose an animal to delete:\n";
                    int codigo = 1;
                    for(Animal anm : clt.getAnimals()){
                        if(anm.getIsActive()){
                            txtToShow += codigo + " - " + anm.getName() + "\n";
                            codigos.put(codigo, anm.getId());
                            codigo++;
                        }
                    }
                    int codigoToDelete = Interactive.readInt(txtToShow, "Delete Animal");
                    if(codigos.containsKey(codigoToDelete)){
                        for(Animal anm : clt.getAnimals()){
                            if(anm.getId() == codigos.get(codigoToDelete)){
                                anm.swapIsActive();
                                break;
                            }
                        }
                        Animal.ExportAllData(peoples);
                        JOptionPane.showMessageDialog(null, "Animal deleted with success!", "Delete Animal", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "Invalid Code", "Delete Animal", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public static void ExportClients(ArrayList<People> allPeople){
        String fileData = "";
        for(People pp : allPeople){
            if(pp instanceof Client){
                fileData += pp.getNif() + "," + pp.getName() + "," + pp.getContact() + "," + pp.getAddress().getNstreet() + "," + pp.getAddress().getZipCode() + "," + pp.getAddress().getndoor() + "," + pp.getAddress().getNlocality() + "\n";
            }
        }
        Files.saveData("clients.csv", fileData);
    }
}