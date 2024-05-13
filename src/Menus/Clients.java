package Menus;

import Classes.*;
import pt.gov.cartaodecidadao.*;

import javax.swing.*;
import java.util.ArrayList;

public class Clients {
    public static void showMenu(ArrayList<People> persons){
        int opcao;

        do {
            opcao = Interactive.readInt("Clients Operations\n\n1 - Create Client\n2 - List Animals of a Client\n0 - Previus Menu", "Clients");

            switch (opcao){
                case 1:
                    int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the data from the citizen card?", "Create Client", JOptionPane.YES_NO_OPTION);
                    if(ccRead == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            String name = eid.getGivenName() + " " + eid.getSurname();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            String contact = JOptionPane.showInputDialog(null, "Contact", "Create Client", JOptionPane.INFORMATION_MESSAGE);
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
                        String name = JOptionPane.showInputDialog(null, "Name", "Create Client", JOptionPane.INFORMATION_MESSAGE);
                        int nif = Interactive.readInt("NIF", "Create Client");
                        String contact = JOptionPane.showInputDialog(null, "Contact", "Create Client", JOptionPane.INFORMATION_MESSAGE);
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
                            if(!pp.getAnimais().isEmpty()){
                                pp.getAnimais().forEach((animal) -> {
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
                        if(!pp.getAnimais().isEmpty()){
                            pp.getAnimais().forEach((animal) -> {
                                JOptionPane.showMessageDialog(null, animal.toString(), "Animal", JOptionPane.INFORMATION_MESSAGE);
                            });
                        }else{
                            JOptionPane.showMessageDialog(null, "No records found!", "Animals", JOptionPane.ERROR_MESSAGE);
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
    private static Address askAddress(){
        String street = JOptionPane.showInputDialog(null, "Street", "Create Client", JOptionPane.INFORMATION_MESSAGE);
        int door = Interactive.readInt("Door", "Create Client");
        int ZipCode = Interactive.readInt("Zip Code", "Create Client");
        String Nlocality = JOptionPane.showInputDialog(null, "Locality", "Create Client", JOptionPane.INFORMATION_MESSAGE);
        return new Address(street, door, ZipCode, Nlocality);
    }

    private static void confirmPerson(Address address, Client clt, ArrayList<People> allPeople){
        int confirmData = JOptionPane.showConfirmDialog(null, "Please confirm the data bellow:\n\nName: " + clt.getName() + "\nNIF: " + clt.getNif() + "\nContact: " + clt.getContact() + "\nStreet: " + address.getNstreet() + "\nDoor: " + address.getndoor() + "\nZip Code: " + address.getZipCode() + "\nLocality: " + address.getNlocality(), "Create Client", JOptionPane.YES_NO_OPTION);
        clt.setAddress(address);
        if(confirmData == 0){
            allPeople.add(clt);
            String fileData = "";
            for(People pp : allPeople){
                if(pp instanceof Client){
                    fileData += pp.getNif() + "," + pp.getName() + "," + pp.getContact() + "," + pp.getAddress().getNstreet() + "," + pp.getAddress().getZipCode() + "," + pp.getAddress().getndoor() + "," + pp.getAddress().getNlocality() + ",cliente\n";
                }
            }
            Files.saveData("clients.csv", fileData);
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
}
