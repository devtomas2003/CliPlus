package Menus;

import Classes.*;
import Utils.CitizenCard;
import Utils.PDFGenerator;
import pt.gov.cartaodecidadao.PTEID_EId;
import pt.gov.cartaodecidadao.PTEID_ExNoCardPresent;
import pt.gov.cartaodecidadao.PTEID_ExNoReader;
import pt.gov.cartaodecidadao.PTEID_Exception;

import javax.swing.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Appointments {
    private static final ZoneId z = ZoneId.of("Europe/Lisbon");
    public static void showMenu(ArrayList<People> persons, ArrayList<Appointment> apps){
        int opcao;

        do {
            opcao = Interactive.readInt("Appointment Operations\n\n1 - Create Appointment\n2 - List Types of Interventions\n3 - Report interventions in a day\n4 - Report interventions for a vet\n5 - Report interventions for a vet in a date\n6 - Report past interventions for a animal\n7 - Report today interventions for a animal\n8 - Report future interventions for a animal\n9 - Report Past/Today Costs\n10 - Report Future Costs\n11 - Generate Invoice\n12 - Unschedule Appointment\n13 - Appointment Detail Record\n0 - Previus Menu", "Appointments", 0);

            switch (opcao) {
                case 1:
                    int findType = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal", 0);
                    if(findType == 1){
                        int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                        if(ccRead == 0) {
                            try {
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                                int chipId = selectAnimalFromClient(pp);
                                createAppointment(chipId, apps, persons);
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
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            int chipId = selectAnimalFromClient(pp);
                            createAppointment(chipId, apps, persons);
                        }
                    }else if(findType == 2){
                        int chipId = 0;
                        boolean isAValidChip = false;

                        do{
                            chipId = Interactive.readInt("Enter the chip ID", "Find Animal", 0);

                            for(People pp : persons){
                                if(pp instanceof Client){
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for(Animal anm : anms){
                                        if (anm.getId() == chipId && anm.getIsActive()) {
                                            isAValidChip = true;
                                        }
                                    }
                                }
                            }
                            if(!isAValidChip){
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        }while (!isAValidChip);

                        createAppointment(chipId, apps, persons);
                    }else{
                        JOptionPane.showMessageDialog(null, "Invalid Option", "Schuler Finder", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "List all types of interventions\n\n- Normal\n- Vaccination\n- Surgery", "Interventions", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 3:
                    PDFGenerator.reportByDate(apps, persons);
                    break;
                case 4:
                    PDFGenerator.reportByVet(apps, persons);
                    break;
                case 5:
                    PDFGenerator.reportByVetAndDate(apps, persons);
                    break;
                case 6:
                    int findTypeReports = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal", 0);
                    if(findTypeReports == 1){
                        int readCC = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                        if(readCC == 0){
                            try{
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                                ArrayList<Animal> animals = null;
                                for(People ppItr : persons){
                                    if(ppItr instanceof Client){
                                        animals.addAll(((Client) ppItr).getAnimals());
                                    }
                                }
                                int anmId = selectAnimalFromClient(pp);
                                Animal anm = Animals.findAnimal(anmId, animals);
                                PDFGenerator.reportPastInterventionsByAnimal(apps, anm, pp);
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
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }
                            int anmId = selectAnimalFromClient(pp);
                            Animal anm = Animals.findAnimal(anmId, animals);
                            PDFGenerator.reportPastInterventionsByAnimal(apps, anm, pp);
                        }
                    }else if(findTypeReports == 2){
                        int chipId = 0;
                        boolean isAValidChip = false;

                        do{
                            chipId = Interactive.readInt("Enter the chip ID (0 - Exit)", "Find Animal", 0);

                            for(People pp : persons){
                                if(pp instanceof Client){
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for(Animal anm : anms){
                                        if (anm.getId() == chipId && anm.getIsActive()) {
                                            isAValidChip = true;
                                        }
                                    }
                                }
                            }

                            if(chipId == 0){
                                isAValidChip = true;
                            }

                            if(!isAValidChip){
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        }while (!isAValidChip);
                        if(chipId == 0){
                            break;
                        }

                        ArrayList<Animal> animals = new ArrayList<>();
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                animals.addAll(((Client) ppItr).getAnimals());
                            }
                        }

                        Client clt = null;
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                for(Animal anmItr : ((Client) ppItr).getAnimals()){
                                    if(anmItr.getId() == chipId){
                                        clt = (Client) ppItr;
                                    }
                                }
                            }
                        }
                        
                        Animal anm = Animals.findAnimal(chipId, animals);
                        PDFGenerator.reportPastInterventionsByAnimal(apps, anm, clt);
                    }else{
                        JOptionPane.showMessageDialog(null, "Invalid Option", "Report Service", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 7:
                    int findTypeReportsToday = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal", 0);
                    if(findTypeReportsToday == 1){
                        int readCC = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                        if(readCC == 0){
                            try{
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                                ArrayList<Animal> animals = null;
                                for(People ppItr : persons){
                                    if(ppItr instanceof Client){
                                        animals.addAll(((Client) ppItr).getAnimals());
                                    }
                                }
                                int anmId = selectAnimalFromClient(pp);
                                Animal anm = Animals.findAnimal(anmId, animals);
                                PDFGenerator.reportPastInterventionsByAnimal(apps, anm, pp);
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
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }
                            int anmId = selectAnimalFromClient(pp);
                            Animal anm = Animals.findAnimal(anmId, animals);
                            PDFGenerator.reportTodayInterventionsByAnimal(apps, anm, pp);
                        }
                    }else if(findTypeReportsToday == 2){
                        int chipId = 0;
                        boolean isAValidChip = false;

                        do{
                            chipId = Interactive.readInt("Enter the chip ID (0 - Exit)", "Find Animal",0);

                            for(People pp : persons){
                                if(pp instanceof Client){
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for(Animal anm : anms){
                                        if (anm.getId() == chipId && anm.getIsActive()) {
                                            isAValidChip = true;
                                        }
                                    }
                                }
                            }

                            if(chipId == 0){
                                isAValidChip = true;
                            }

                            if(!isAValidChip){
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        }while (!isAValidChip);
                        if(chipId == 0){
                            break;
                        }

                        ArrayList<Animal> animals = new ArrayList<>();
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                animals.addAll(((Client) ppItr).getAnimals());
                            }
                        }

                        Client clt = null;
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                for(Animal anmItr : ((Client) ppItr).getAnimals()){
                                    if(anmItr.getId() == chipId){
                                        clt = (Client) ppItr;
                                    }
                                }
                            }
                        }

                        Animal anm = Animals.findAnimal(chipId, animals);
                        PDFGenerator.reportTodayInterventionsByAnimal(apps, anm, clt);
                    }else{
                        JOptionPane.showMessageDialog(null, "Invalid Option", "Report Service", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 8:
                    int findTypeReportsFuture = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal", 0);
                    if(findTypeReportsFuture == 1){
                        int readCC = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                        if(readCC == 0){
                            try{
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                                ArrayList<Animal> animals = null;
                                for(People ppItr : persons){
                                    if(ppItr instanceof Client){
                                        animals.addAll(((Client) ppItr).getAnimals());
                                    }
                                }
                                int anmId = selectAnimalFromClient(pp);
                                Animal anm = Animals.findAnimal(anmId, animals);
                                PDFGenerator.reportPastInterventionsByAnimal(apps, anm, pp);
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
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }
                            int anmId = selectAnimalFromClient(pp);
                            Animal anm = Animals.findAnimal(anmId, animals);
                            PDFGenerator.reportFutureInterventionsByAnimal(apps, anm, pp);
                        }
                    }else if(findTypeReportsFuture == 2){
                        int chipId = 0;
                        boolean isAValidChip = false;

                        do{
                            chipId = Interactive.readInt("Enter the chip ID (0 - Exit)", "Find Animal", 0);

                            for(People pp : persons){
                                if(pp instanceof Client){
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for(Animal anm : anms){
                                        if (anm.getId() == chipId && anm.getIsActive()) {
                                            isAValidChip = true;
                                        }
                                    }
                                }
                            }

                            if(chipId == 0){
                                isAValidChip = true;
                            }

                            if(!isAValidChip){
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        }while (!isAValidChip);
                        if(chipId == 0){
                            break;
                        }

                        ArrayList<Animal> animals = new ArrayList<>();
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                animals.addAll(((Client) ppItr).getAnimals());
                            }
                        }

                        Client clt = null;
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                for(Animal anmItr : ((Client) ppItr).getAnimals()){
                                    if(anmItr.getId() == chipId){
                                        clt = (Client) ppItr;
                                    }
                                }
                            }
                        }

                        Animal anm = Animals.findAnimal(chipId, animals);
                        PDFGenerator.reportFutureInterventionsByAnimal(apps, anm, clt);
                    }else{
                        JOptionPane.showMessageDialog(null, "Invalid Option", "Report Service", JOptionPane.ERROR_MESSAGE);
                    }
                case 9:
                    int readCC = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                    if(readCC == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }

                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }

                            PDFGenerator.reportTodayAndPastCostsByClient(apps, animals, pp);
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
                        int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                        Client pp = Clients.findClient(nif, persons);
                        if(pp == null){
                            JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                            break;
                        }

                        ArrayList<Animal> animals = new ArrayList<>();
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                animals.addAll(((Client) ppItr).getAnimals());
                            }
                        }

                        PDFGenerator.reportTodayAndPastCostsByClient(apps, animals, pp);
                    }
                    break;
                case 10:
                    int readCCFuture = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                    if(readCCFuture == 0){
                        try{
                            PTEID_EId eid = CitizenCard.initiate();
                            int nif = Integer.parseInt(eid.getTaxNo());
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }

                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }

                            PDFGenerator.reportFutureCostsByClient(apps, animals, pp);
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
                        int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                        Client pp = Clients.findClient(nif, persons);
                        if(pp == null){
                            JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                            break;
                        }

                        ArrayList<Animal> animals = new ArrayList<>();
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                animals.addAll(((Client) ppItr).getAnimals());
                            }
                        }

                        PDFGenerator.reportFutureCostsByClient(apps, animals, pp);
                    }
                    break;
                case 11:
                    int findTypeInvoice = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal", 0);
                    if(findTypeInvoice == 1){
                        int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                        if(ccRead == 0) {
                            try {
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }

                                ArrayList<Animal> animals = new ArrayList<>();

                                for (People ppItr : persons) {
                                    if (ppItr instanceof Client) {
                                        animals.addAll(((Client) ppItr).getAnimals());
                                    }
                                }

                                int chipId = selectAnimalFromClient(pp);
                                Animal anm = Animals.findAnimal(chipId, animals);

                                PDFGenerator.GenerateInvoice(anm, apps, pp);
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
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }

                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }

                            int chipId = selectAnimalFromClient(pp);
                            Animal anm = Animals.findAnimal(chipId, animals);

                            PDFGenerator.GenerateInvoice(anm, apps, pp);
                        }
                    }else if(findTypeInvoice == 2){
                        int chipId = 0;
                        boolean isAValidChip = false;

                        do{
                            chipId = Interactive.readInt("Enter the chip ID", "Find Animal", 0);

                            for(People pp : persons){
                                if(pp instanceof Client){
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for(Animal anm : anms){
                                        if (anm.getId() == chipId) {
                                            isAValidChip = true;
                                        }
                                    }
                                }
                            }
                            if(!isAValidChip){
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        }while (!isAValidChip);

                        Client clt = null;
                        ArrayList<Animal> animals = new ArrayList<>();
                        for (People ppItr : persons) {
                            if (ppItr instanceof Client) {
                                animals.addAll(((Client) ppItr).getAnimals());
                                for(Animal anm : ((Client) ppItr).getAnimals()){
                                    if(anm.getId() == chipId){
                                        clt = (Client) ppItr;
                                    }
                                }
                            }
                        }

                        Animal anm = Animals.findAnimal(chipId, animals);
                        PDFGenerator.GenerateInvoice(anm, apps, clt);
                    }else{
                        JOptionPane.showMessageDialog(null, "Invalid Option", "Schuler Finder", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 12:
                    int findTypeUnc = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal", 0);
                    if(findTypeUnc == 1){
                        int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                        if(ccRead == 0) {
                            try {
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }

                                ArrayList<Animal> animals = new ArrayList<>();

                                for (People ppItr : persons) {
                                    if (ppItr instanceof Client) {
                                        animals.addAll(((Client) ppItr).getAnimals());
                                    }
                                }

                                int chipId = selectAnimalFromClient(pp);
                                Animal anm = Animals.findAnimal(chipId, animals);

                                unscheduleAppointment(apps, anm);
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
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }

                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }

                            int chipId = selectAnimalFromClient(pp);
                            Animal anm = Animals.findAnimal(chipId, animals);

                            unscheduleAppointment(apps, anm);
                        }
                    }else if(findTypeUnc == 2) {
                        Animal anmLocated = null;
                        boolean isAValidChip = false;

                        do {
                            int chipId = Interactive.readInt("Enter the chip ID", "Find Animal", 0);

                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for (Animal anm : anms) {
                                        if (anm.getId() == chipId) {
                                            isAValidChip = true;
                                            anmLocated = anm;
                                        }
                                    }
                                }
                            }
                            if (!isAValidChip) {
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (!isAValidChip);

                        unscheduleAppointment(apps, anmLocated);
                    }
                    break;
                case 13:
                    int findTypeRep = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal", 0);
                    if(findTypeRep == 1){
                        int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Find Client", JOptionPane.YES_NO_OPTION);
                        if(ccRead == 0) {
                            try {
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }

                                ArrayList<Animal> animals = new ArrayList<>();

                                for (People ppItr : persons) {
                                    if (ppItr instanceof Client) {
                                        animals.addAll(((Client) ppItr).getAnimals());
                                    }
                                }

                                int chipId = selectAnimalFromClient(pp);
                                Animal anm = Animals.findAnimal(chipId, animals);

                                scheduleDetailRecord(apps, anm, pp);
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
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client", 0);
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }

                            ArrayList<Animal> animals = new ArrayList<>();
                            for (People ppItr : persons) {
                                if (ppItr instanceof Client) {
                                    animals.addAll(((Client) ppItr).getAnimals());
                                }
                            }

                            int chipId = selectAnimalFromClient(pp);
                            Animal anm = Animals.findAnimal(chipId, animals);

                            scheduleDetailRecord(apps, anm, pp);
                        }
                    }else if(findTypeRep == 2) {
                        Animal anmLocated = null;
                        boolean isAValidChip = false;
                        Client owner = null;

                        do {
                            int chipId = Interactive.readInt("Enter the chip ID", "Find Animal", 0);

                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for (Animal anm : anms) {
                                        if (anm.getId() == chipId) {
                                            isAValidChip = true;
                                            anmLocated = anm;
                                            owner = (Client) pp;
                                        }
                                    }
                                }
                            }
                            if (!isAValidChip) {
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (!isAValidChip);

                        scheduleDetailRecord(apps, anmLocated, owner);
                    }
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }

    private static int selectAnimalFromClient(Client client) {
        HashMap<Integer, Integer> codigos = new HashMap<Integer, Integer>();
        String txtToShow = "Choose an animal to select:\n\n";
        int codigo = 1;
        for (Animal anm : client.getAnimals()) {
            if (anm.getIsActive()) {
                txtToShow += codigo + " - " + anm.getName() + "\n";
                codigos.put(codigo, anm.getId());
                codigo++;
            }
        }
        int selectedCode = 0;
        do {
            int selectedCodeOpt = Interactive.readInt(txtToShow, "Select Animal", 0);
            if (!codigos.containsKey(selectedCodeOpt)){
                JOptionPane.showMessageDialog(null, "Invalid Code", "Select Animal", JOptionPane.ERROR_MESSAGE);
            }else{
                selectedCode = codigos.get(selectedCodeOpt);
            }
        } while (selectedCode == 0);
        return selectedCode;
    }

    private static void createAppointment(int chipId, ArrayList<Appointment> apps, ArrayList<People> persons){
        Animal anm = null;

        for(People person : persons){
            if(person instanceof Client){
                for(Animal anmItr : ((Client) person).getAnimals()){
                    if(anmItr.getId() == chipId){
                        anm = anmItr;
                    }
                }
            }
        }

        int opcao = -1;
        do{
            opcao = Interactive.readInt("What appointment do you want to schedule?\n\n1 - Normal\n2 - Vaccination\n3 - Surgery", "Appointments", 0);
            if(opcao != 1 && opcao != 2 && opcao != 3){
                JOptionPane.showMessageDialog(null, "Invalid option!", "Scheduler", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao == -1);

        int opcaoLocation = -1;
        do{
            opcaoLocation = Interactive.readInt("Where this appointment should happen?\n\n1 - On Site\n2 - Remote", "Appointments", 0);
            if(opcaoLocation != 1 && opcaoLocation != 2){
                JOptionPane.showMessageDialog(null, "Invalid option!", "Scheduler", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcaoLocation == -1);

        Appointment.AppointmentLocation aptLocal;

        if(opcaoLocation == 1){
            aptLocal = Appointment.AppointmentLocation.OnSite;
        }else{
            aptLocal = Appointment.AppointmentLocation.Remote;
        }

        double getLocation = 0;
        if(aptLocal == Appointment.AppointmentLocation.Remote){
            do{
                getLocation = Interactive.readDouble("How many KMs?", "Appointments");
            }while (getLocation == 0);
        }

        Vet vetSelected = null;
        do{
            int vetId = Interactive.readInt("ID of Ordem dos Medicos Veterinarios", "Find Vet", 0);
            Vet vetFounded = Vets.findVetByOMV(vetId, persons);
            if(vetFounded == null){
                JOptionPane.showMessageDialog(null, "This OMV ID does not exists!", "Find Vet", JOptionPane.ERROR_MESSAGE);
            }else{
                vetSelected = vetFounded;
            }
        }while (vetSelected == null);

        int opcaoTimeSlot = -1;
        do{
            opcaoTimeSlot = Interactive.readInt("When do you want to schedule?\n\n1 - Check a day\n2 - Next Available\n0 - Cancel", "Appointments", 0);
            if(opcaoTimeSlot != 1 && opcaoTimeSlot != 2){
                JOptionPane.showMessageDialog(null, "Invalid option!", "Scheduler", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }while (opcaoTimeSlot == -1);

        Appointment.AppointmentType aptType;

        if(opcao == 1){
            aptType = Appointment.AppointmentType.Consultation;
        }else if(opcao == 2){
            aptType = Appointment.AppointmentType.Vaccination;
        }else{
            aptType = Appointment.AppointmentType.Surgery;
        }

        double totalToPay = 0;

        if(aptLocal == Appointment.AppointmentLocation.Remote){
            totalToPay = 40 + getLocation;
        }

        if(aptType == Appointment.AppointmentType.Surgery){
            if(anm.getWeight() >= 10){
                totalToPay += 400;
            }else{
                totalToPay += 200;
            }
        }

        if(aptType == Appointment.AppointmentType.Vaccination){
            if(anm.getWeight() >= 10){
                totalToPay += 50;
            }else{
                totalToPay += 100;
            }
        }

        if(aptType == Appointment.AppointmentType.Consultation){
            if(anm.getWeight() >= 10){
                totalToPay += 25;
            }else{
                totalToPay += 50;
            }
        }

        totalToPay *= 1.20;

        if(opcaoTimeSlot == 1){
            LocalDateTime dtCh = Interactive.readDate("Scheduler", false, true);
            ArrayList<Slot> availableSlots = getSlotsAvailable(dtCh, apps, aptType, vetSelected);

            if(availableSlots.isEmpty()){
                JOptionPane.showMessageDialog(null, "There is no slots available for this type of schedule!", "Slots Manager", JOptionPane.ERROR_MESSAGE);
            }else{
                HashMap<Integer, Slot> codigos = new HashMap<Integer, Slot>();

                String txtToShow = "Please choose an slot:\n\n";
                int codigo = 1;
                for(Slot slt : availableSlots){
                    if(aptType == Appointment.AppointmentType.Surgery){
                        LocalDateTime endTime = slt.getStartTime();
                        endTime = endTime.plusHours(2);

                        txtToShow += codigo + " - " + slt.getStartTime().toLocalTime() + " unitl " + endTime.toLocalTime() + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }else{
                        txtToShow += codigo + " - " + slt.getStartTime().toLocalTime() + " unitl " + slt.getEndTime().toLocalTime() + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }
                }

                int codigoToSelect = Interactive.readInt(txtToShow, "Select Schedule", 0);
                if(codigos.containsKey(codigoToSelect)){
                    Appointment appt = new Appointment(aptType, aptLocal, anm, codigos.get(codigoToSelect), vetSelected);
                    if(aptType == Appointment.AppointmentType.Surgery) {
                        Slot lastSlot = codigos.get(codigoToSelect);
                        LocalDateTime startSch = lastSlot.getStartTime().plusMinutes(30);
                        for(int i = 0; i < 3; i++) {
                            LocalDateTime endSch = startSch.plusMinutes(30);
                            Appointment apptNew = new Appointment(aptType, aptLocal, anm, new Slot(startSch, endSch), vetSelected);
                            apptNew.setDistance(getLocation);
                            apps.add(apptNew);
                            startSch = endSch;
                        }
                    }
                    appt.setDistance(getLocation);
                    apps.add(appt);

                    saveAppointmentsInFile(apps);

                    DecimalFormat formato = new DecimalFormat("#.##");
                    JOptionPane.showMessageDialog(null,  aptType + " scheduled with success!\n\nTotal to pay: €" + formato.format(totalToPay), "Scheduler", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Invalid Code", "Scheduler", JOptionPane.ERROR_MESSAGE);
                }
            }
        }else{
            LocalDateTime dtCh = LocalDateTime.now(z);
            LocalDateTime now = LocalDateTime.now(z);

            ArrayList<Slot> availableSlots;

            do{
                availableSlots = getSlotsAvailable(dtCh, apps, aptType, vetSelected);
                if (dtCh.toLocalDate().equals(now.toLocalDate())) {
                    Iterator<Slot> iterator = availableSlots.iterator();
                    while (iterator.hasNext()) {
                        Slot slt = iterator.next();
                        if (slt.getStartTime().isBefore(now)) {
                            iterator.remove();
                        }
                    }
                }
                if(availableSlots.isEmpty()){
                    dtCh = dtCh.plusDays(1);
                }
            }while (availableSlots.isEmpty());
                HashMap<Integer, Slot> codigos = new HashMap<Integer, Slot>();
                DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = dtCh.toLocalDate().format(formatterDay);
                String txtToShow = "Day: " + formattedDate + "\nPlease choose an slot:\n\n";
                int codigo = 1;
                for(Slot slt : availableSlots){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    if(aptType == Appointment.AppointmentType.Surgery){
                        LocalDateTime endTime = slt.getStartTime();
                        endTime = endTime.plusHours(2);
                        txtToShow += codigo + " - " +  slt.getStartTime().toLocalTime().format(formatter) + " unitl " + endTime.toLocalTime().format(formatter) + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }else{
                        txtToShow += codigo + " - " + slt.getStartTime().toLocalTime().format(formatter) + " unitl " + slt.getEndTime().toLocalTime().format(formatter) + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }
                }

                int codigoToSelect = Interactive.readInt(txtToShow, "Select Schedule", 0);
                if(codigos.containsKey(codigoToSelect)){
                    Appointment appt = new Appointment(aptType, aptLocal, anm, codigos.get(codigoToSelect), vetSelected);
                    if(aptType == Appointment.AppointmentType.Surgery) {
                        Slot lastSlot = codigos.get(codigoToSelect);
                        LocalDateTime startSch = lastSlot.getStartTime().plusMinutes(30);
                        for(int i = 0; i < 3; i++) {
                            LocalDateTime endSch = startSch.plusMinutes(30);
                            Appointment apptNew = new Appointment(aptType, aptLocal, anm, new Slot(startSch, endSch), vetSelected);
                            apptNew.setDistance(getLocation);
                            apps.add(apptNew);
                            startSch = endSch;
                        }
                    }
                    appt.setDistance(getLocation);
                    apps.add(appt);

                    saveAppointmentsInFile(apps);
                    DecimalFormat formato = new DecimalFormat("#.##");
                    JOptionPane.showMessageDialog(null,  aptType + " scheduled with success!\n\nTotal to pay: €" + formato.format(totalToPay), "Scheduler", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Invalid Code", "Scheduler", JOptionPane.ERROR_MESSAGE);
                }

        }
    }

    private static ArrayList<Slot> getSlotsAvailable(LocalDateTime chDate, ArrayList<Appointment> apps, Appointment.AppointmentType aptType, Vet vet) {
        LocalDateTime selectedDate = chDate.toLocalDate().atStartOfDay();
        ArrayList<Appointment> appointmentsSelectedDay = new ArrayList<>();

        for (Appointment app : apps) {
            if (app.getTimeSlot().getStartTime().toLocalDate().equals(selectedDate.toLocalDate()) && app.getVet().getNif() == vet.getNif()) {
                appointmentsSelectedDay.add(app);
            }
        }

        ArrayList<Slot> checkSlots = generatePossibleSlots(chDate);
        ArrayList<Slot> possibleSlots = new ArrayList<>();

        if (appointmentsSelectedDay.isEmpty()) {
            possibleSlots.addAll(checkSlots);
        } else {
            boolean[] slotOccupied = new boolean[checkSlots.size()];
            for (Appointment appt : appointmentsSelectedDay) {
                for (int i = 0; i < checkSlots.size(); i++) {
                    Slot slot = checkSlots.get(i);
                    if (slot.getStartTime().isEqual(appt.getTimeSlot().getStartTime()) || slot.getEndTime().isEqual(appt.getTimeSlot().getEndTime()) || (slot.getStartTime().isBefore(appt.getTimeSlot().getEndTime()) && slot.getEndTime().isAfter(appt.getTimeSlot().getStartTime()))) {
                        slotOccupied[i] = true;
                    }
                }
            }

            for (int i = 0; i < checkSlots.size(); i++) {
                if (!slotOccupied[i]) {
                    possibleSlots.add(checkSlots.get(i));
                }
            }
        }

        if (aptType == Appointment.AppointmentType.Surgery) {
            return findConsecutiveSlots(possibleSlots, 4);
        }

        return possibleSlots;
    }

    private static ArrayList<Slot> findConsecutiveSlots(ArrayList<Slot> slots, int requiredConsecutive) {
        ArrayList<Slot> result = new ArrayList<>();
        for (int i = 0; i <= slots.size() - requiredConsecutive; i++) {
            boolean consecutive = true;
            for (int j = 0; j < requiredConsecutive - 1; j++) {
                if (!slots.get(i + j).isAdjacent(slots.get(i + j + 1))) {
                    consecutive = false;
                    break;
                }
            }
            if (consecutive) {
                result.add(slots.get(i));
            }
        }
        return result;
    }

    private static ArrayList<Slot> generatePossibleSlots(LocalDateTime day) {
        ArrayList<Slot> possibleSlots = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(day.toLocalDate(), day.toLocalTime().withHour(9).withMinute(0).withSecond(0));

        for (int itr = 0; itr < 18; itr++) {
            if (!(startTime.getHour() == 12 && (startTime.getMinute() == 0 || startTime.getMinute() == 30))) {
                Slot tmSlot = new Slot(startTime, startTime.plusMinutes(30));
                possibleSlots.add(tmSlot);
            }
            startTime = startTime.plusMinutes(30);
        }

        return possibleSlots;
    }

    public static void saveAppointmentsInFile(ArrayList<Appointment> apps){
        String fileData = "";
        for(Appointment app : apps){
            fileData += app.getAppoType() + "," + app.getAppoLocal() + "," + app.getVet().getNif() + "," + app.getAnimal().getId() + "," + app.getTimeSlot().getStartTime() + "," + app.getTimeSlot().getEndTime() + "," + app.getDistance() + "\n";
        }
        Files.saveData("appointments.txt", fileData);
    }

    private static void unscheduleAppointment(ArrayList<Appointment> apps, Animal anm){
        LocalDateTime dtCh = LocalDateTime.now(z);
        ArrayList<Appointment> appsPossible = new ArrayList<Appointment>();

        for(Appointment app : apps){
            if(app.getAnimal().getId() == anm.getId()){
                if (app.getTimeSlot().getStartTime().isAfter(dtCh)) {
                    appsPossible.add(app);
                }
            }
        }

        String txtToShow = "Please choose the appointment slot:\n\n";
        int codigo = 1;
        HashMap<Integer, Appointment> codigos = new HashMap<Integer, Appointment>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for(int i = 0; i < appsPossible.size(); i++){
            if(appsPossible.get(i).getAppoType() == Appointment.AppointmentType.Surgery){
                LocalDateTime sEnd = appsPossible.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                txtToShow += codigo + " - " +  appsPossible.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatter) + " until " + sEnd.format(formatter) + " (" + appsPossible.get(i).getAppoType() + ")" + "\n";
                i += 3;
                codigos.put(codigo, appsPossible.get(i));
            }else{
                txtToShow += codigo + " - " + appsPossible.get(i).getTimeSlot().getStartTime().format(formatter) + " until " + appsPossible.get(i).getTimeSlot().getEndTime().format(formatter) + " (" + appsPossible.get(i).getAppoType() + ")" + "\n";
                codigos.put(codigo, appsPossible.get(i));
            }
            codigo++;
        }
        int codigoToDelete = Interactive.readInt(txtToShow, "Appointment Slot", 0);
        if(codigos.containsKey(codigoToDelete)){
            ArrayList<Appointment> lApps = new ArrayList<Appointment>();
            if(codigos.get(codigoToDelete).getAppoType() == Appointment.AppointmentType.Surgery){
                for(int i = 0; i < 4; i++) {
                    Slot slotFirst = new Slot(codigos.get(codigoToDelete).getTimeSlot().getStartTime(), codigos.get(codigoToDelete).getTimeSlot().getEndTime());
                    slotFirst.setStartTime(slotFirst.getStartTime().plusMinutes(30 * i));
                    slotFirst.setEndTime(slotFirst.getEndTime().plusMinutes(30 * i));

                    Appointment aptFirst = new Appointment(codigos.get(codigoToDelete).getAppoType(), codigos.get(codigoToDelete).getAppoLocal(), codigos.get(codigoToDelete).getAnimal(), slotFirst, codigos.get(codigoToDelete).getVet());
                    lApps.add(aptFirst);
                }
            }else{
                lApps.add(codigos.get(codigoToDelete));
            }

            if(JOptionPane.showConfirmDialog(null, "Are you sure? Slot Selected: " + codigos.get(codigoToDelete).getTimeSlot().getStartTime().format(formatter) + " - " + codigos.get(codigoToDelete).getTimeSlot().getEndTime().format(formatter), "Confirm Unschedule", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                Iterator<Appointment> iterator = apps.iterator();
                while (iterator.hasNext()) {
                    Appointment app = iterator.next();
                    for (Appointment appToDelete : lApps) {
                        if (app.getTimeSlot().getStartTime().equals(appToDelete.getTimeSlot().getStartTime()) &&
                                app.getTimeSlot().getEndTime().equals(appToDelete.getTimeSlot().getEndTime())) {
                            iterator.remove();
                            break;
                        }
                    }
                }
                saveAppointmentsInFile(apps);
                JOptionPane.showMessageDialog(null, "Appointment deleted with success!", "Appointment Unscheduler", JOptionPane.INFORMATION_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "Invalid Code", "Unschedule Appointment", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void scheduleDetailRecord(ArrayList<Appointment> apps, Animal anm, Client clt){
        LocalDateTime dtCh = LocalDateTime.now(z);
        ArrayList<Appointment> appsPossible = new ArrayList<Appointment>();

        for(Appointment app : apps){
            if(app.getAnimal().getId() == anm.getId()){
                appsPossible.add(app);
            }
        }

        String txtToShow = "Please choose the appointment slot:\n\n";
        int codigo = 1;
        HashMap<Integer, Appointment> codigos = new HashMap<Integer, Appointment>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for(int i = 0; i < appsPossible.size(); i++){
            if(appsPossible.get(i).getAppoType() == Appointment.AppointmentType.Surgery){
                LocalDateTime sEnd = appsPossible.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                txtToShow += codigo + " - " +  appsPossible.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatter) + " until " + sEnd.format(formatter) + " (" + appsPossible.get(i).getAppoType() + ")" + "\n";
                i += 3;
                codigos.put(codigo, appsPossible.get(i));
            }else{
                txtToShow += codigo + " - " + appsPossible.get(i).getTimeSlot().getStartTime().format(formatter) + " until " + appsPossible.get(i).getTimeSlot().getEndTime().format(formatter) + " (" + appsPossible.get(i).getAppoType() + ")" + "\n";
                codigos.put(codigo, appsPossible.get(i));
            }
            codigo++;
        }
        int codigoToDelete = Interactive.readInt(txtToShow, "Appointment Slot", 0);
        if(codigos.containsKey(codigoToDelete)){
            ArrayList<Appointment> lApps = new ArrayList<Appointment>();
            if(codigos.get(codigoToDelete).getAppoType() == Appointment.AppointmentType.Surgery){
                for(int i = 0; i < 4; i++) {
                    Slot slotFirst = new Slot(codigos.get(codigoToDelete).getTimeSlot().getStartTime(), codigos.get(codigoToDelete).getTimeSlot().getEndTime());
                    slotFirst.setStartTime(slotFirst.getStartTime().plusMinutes(30 * i));
                    slotFirst.setEndTime(slotFirst.getEndTime().plusMinutes(30 * i));

                    Appointment aptFirst = new Appointment(codigos.get(codigoToDelete).getAppoType(), codigos.get(codigoToDelete).getAppoLocal(), codigos.get(codigoToDelete).getAnimal(), slotFirst, codigos.get(codigoToDelete).getVet());
                    lApps.add(aptFirst);
                }
            }else{
                lApps.add(codigos.get(codigoToDelete));
            }

            if(JOptionPane.showConfirmDialog(null, "Are you sure? Slot Selected: " + codigos.get(codigoToDelete).getTimeSlot().getStartTime().format(formatter) + " - " + codigos.get(codigoToDelete).getTimeSlot().getEndTime().format(formatter), "Confirm Report Generation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                PDFGenerator.AppointmentReportRecord(codigos.get(codigoToDelete), clt);
            }
        }else{
            JOptionPane.showMessageDialog(null, "Invalid Code", "Unschedule Appointment", JOptionPane.ERROR_MESSAGE);
        }
    }
}