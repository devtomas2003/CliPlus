package Utils;

import Classes.*;

import java.io.*;
import java.util.ArrayList;

public class FilesDAT {
    public static ArrayList<Appointment> readAppointments(File appFile) {
        ArrayList<Appointment> result = new ArrayList<Appointment>();
        try {
            FileInputStream fis = new FileInputStream(appFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (fis.available() > 0) {
                try {
                    Appointment obj;
                    obj = (Appointment) ois.readObject();
                    result.add(obj);

                } catch (ClassNotFoundException error) {
                    System.out.println("Error Message (ClassNotFoundException): " + error.getMessage());
                    error.printStackTrace();
                }
            }
            fis.close();
            ois.close();

        } catch (IOException error) {
            System.out.println("Error Message (IOException): " + error.getMessage());
            error.printStackTrace();
        }
        return result;
    }
    public static void writerAppointments(ArrayList<Appointment> list, File pathName) {
        try {
            File f = new File(pathName, "appointments.dat");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            for (Appointment obj: list) {
                oos.writeObject(obj);
            }

            fos.close();
            oos.close();
        } catch (IOException error) {
            System.out.println("Error Message: " + error.getMessage());
            error.printStackTrace();
        }
    }

    public static void writerPeoples(ArrayList<People> list, File folder) {
        try {
            File f = new File(folder, "people.dat");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            for (People obj: list) {
                oos.writeObject(obj);
            }

            fos.close();
            oos.close();
        } catch (IOException error) {
            System.out.println("Error Message: " + error.getMessage());
            error.printStackTrace();
        }
    }
    public static ArrayList<People> readPeoples(File peopleFile) {
        ArrayList<People> result = new ArrayList<People>();
        try {
            FileInputStream fis = new FileInputStream(peopleFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (fis.available() > 0) {
                try {
                    People obj;
                    obj = (People) ois.readObject();
                    result.add(obj);

                } catch (ClassNotFoundException error) {
                    System.out.println("Error Message (ClassNotFoundException): " + error.getMessage());
                    error.printStackTrace();
                }
            }
            fis.close();
            ois.close();

        } catch (IOException error) {
            System.out.println("Error Message (IOException): " + error.getMessage());
            error.printStackTrace();
        }
        return result;
    }
}