package Menus;

import Classes.*;
import Utils.CitizenCard;
import pt.gov.cartaodecidadao.PTEID_EId;
import pt.gov.cartaodecidadao.PTEID_ExNoCardPresent;
import pt.gov.cartaodecidadao.PTEID_ExNoReader;
import pt.gov.cartaodecidadao.PTEID_Exception;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

public class Vets {
    public static void showMenu(ArrayList<People> peoples){
        int opcao;

        do {
            opcao = Interactive.readInt("Vets Operations\n\n1 - Create Vet\n2 - List All Vets\n3 - List Animals associated with vets\n4 - List associated Clients\n0 - Previus Menu", "Vets", 0);

            switch (opcao){
                case 1:
                    int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the data from the citizen card?", "Create Vet", JOptionPane.YES_NO_OPTION);
                    if(ccRead == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            String name = eid.getGivenName() + " " + eid.getSurname();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            int idOV = Interactive.readInt("ID of Ordem dos Medicos Veterinarios", "Create Vet", 0);
                            String contact = Interactive.readString("Contact", "Create Vet", "");

                            Vet verDataOMV = findVetByOMV(idOV, peoples);
                            if(verDataOMV != null){
                                JOptionPane.showMessageDialog(null, "This OMV ID already exists!", "Duplicate Record", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            Vet vetDataNif = findVetByNif(nif, peoples);
                            if(vetDataNif != null){
                                JOptionPane.showMessageDialog(null, "This Vet NIF already exists!", "Duplicate Record", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

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
                        String name = Interactive.readString("Name", "Create Vet", "");
                        int nif = Interactive.readInt("NIF", "Create Vet", 0);
                        int idOV = Interactive.readInt("ID of Ordem dos Medicos Veterinarios", "Create Vet", 0);
                        String contact = Interactive.readString("Contact", "Create Vet", "");
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
                                    if (ppData instanceof Client) {
                                        Animal animal = Animals.findAnimal(animalId, ((Client) ppData).getAnimals());
                                        if(animal != null){
                                            if(animal.getIsActive()){
                                                finalTxtToShow.append(animal.toString()).append("\n\n");
                                            }
                                        }
                                    }
                                }

                            }));

                            JOptionPane.showMessageDialog(null, finalTxtToShow.toString(), "Animals associated with vets", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    break;
                case 4:
                    String txtToShow = "";
                    for(People pp : peoples) {
                        if (pp instanceof Vet) {
                            ArrayList<Integer> clientsAlreadyExists = new ArrayList<Integer>();
                            Vet vet = (Vet) pp;
                            txtToShow += "Vet: " + vet.getName();
                            ArrayList<Integer> listAnimals = vet.getAnimals();
                            for(int i = 0; i < listAnimals.size(); i++){
                                for(People ppData : peoples){
                                    if(ppData instanceof Client){
                                        Client clt = (Client) ppData;
                                        ArrayList<Animal> animais = clt.getAnimals();
                                        for(Animal anm : animais){
                                            if(anm.getId() == listAnimals.get(i)){
                                                if(!clientsAlreadyExists.contains(clt.getNif())){
                                                    if(anm.getIsActive()){
                                                        clientsAlreadyExists.add(clt.getNif());
                                                        txtToShow += "\n- " + clt.getName();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            txtToShow += "\n";
                        }
                    }
                    JOptionPane.showMessageDialog(null, txtToShow, "List associated Clients", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }

    private static void confirmVet(Address address, Vet vet, ArrayList<People> allPeople){
        if(vet.getNif() == 0 || vet.getIdOV() == 0 || Objects.equals(vet.getName(), "") || Objects.equals(vet.getContact(), "") || Objects.equals(address.getNstreet(), "") || address.getndoor() == 0 || address.getZipCode() == 0 || Objects.equals(address.getNlocality(), "")){
            JOptionPane.showMessageDialog(null, "Errors found. Please provide the information again!", "Typing error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmData = JOptionPane.showConfirmDialog(null, "Please confirm the data bellow:\n\nName: " + vet.getName() + "\nNIF: " + vet.getNif() + "\nContact: " + vet.getContact() + "\nStreet: " + address.getNstreet() + "\nDoor: " + address.getndoor() + "\nZip Code: " + address.getZipCode() + "\nLocality: " + address.getNlocality(), "Create Vet", JOptionPane.YES_NO_OPTION);
        vet.setAddress(address);
        if(confirmData == 0){
            allPeople.add(vet);
            ExportVets(allPeople);
            JOptionPane.showMessageDialog(null, "Vet added with success!", "Create Vet", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Please provide the information again!", "Typing error!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static Address askAddress(){
        String street = Interactive.readString("Street", "Create Client", "");
        int door = Interactive.readInt("Door", "Create Client", 0);
        int zipCode = 0;
        do{
            try{
                String ZipCode = Interactive.readString("Zip Code", "Create Client", "").replace("-", "");
                zipCode = Integer.parseInt(ZipCode);
                if(ZipCode.length() != 7){
                    JOptionPane.showMessageDialog(null, "ZIP Code with invalid format", "ZIP Code Validate", JOptionPane.ERROR_MESSAGE);
                    zipCode = 0;
                }
            }catch (NumberFormatException nfe){
                JOptionPane.showMessageDialog(null, "Invalid ZIP Code", "ZIP Code Validation", JOptionPane.ERROR_MESSAGE);
            }
        }while (zipCode == 0);
        String Nlocality = Interactive.readString("Locality", "Create Client", "");
        return new Address(street, door, zipCode, Nlocality);
    }

    public static Vet findVetByOMV(int idOV, ArrayList<People> lPeople) {
        for(People person : lPeople) {
            if(person instanceof Vet){
                if(((Vet) person).getIdOV() == idOV) {
                    return (Vet) person;
                }
            }
        }
        return null;
    }
    public static Vet findVetByNif(int vetNif, ArrayList<People> lPeople) {
        for(People person : lPeople) {
            if(person instanceof Vet){
                if(((Vet) person).getNif() == vetNif) {
                    return (Vet) person;
                }
            }
        }
        return null;
    }

    public static void ExportVets(ArrayList<People> allPeople){
        String fileData = "";
        for(People pp : allPeople){
            if(pp instanceof Vet){
                Vet vetInfo = (Vet) pp;
                fileData += vetInfo.getNif() + "," + vetInfo.getName() + "," + vetInfo.getContact() + "," + vetInfo.getAddress().getNstreet() + "," + vetInfo.getAddress().getZipCode() + "," + vetInfo.getAddress().getndoor() + "," + vetInfo.getAddress().getNlocality() + "," + vetInfo.getIdOV() + "\n";
            }
        }
        Files.saveData("vets.txt", fileData);
    }
}
