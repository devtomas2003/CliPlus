package Classes;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Files {
    public static void saveData(String fileName, String content){
        try{
            FileWriter writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
        }catch (IOException e){
            JOptionPane.showMessageDialog(null, "Cannot write file " + fileName, "File Operations", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String readData(String fileName){
        StringBuilder valueReader = new StringBuilder();
        try {
            File myObj = new File(fileName);
            Scanner reader = new Scanner(myObj);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                valueReader.append(data).append("\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Cannot read file " + fileName, "File Operations", JOptionPane.ERROR_MESSAGE);
        }
        return valueReader.toString();
    }
}
