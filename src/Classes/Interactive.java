package Classes;


import javax.swing.*;

public class Interactive {
    public static int readInt(String question, String title){
        boolean success = false;
        int valor = 0;
        do{
            try{
                String input = JOptionPane.showInputDialog(null, question, title, JOptionPane.INFORMATION_MESSAGE);
                if (input == null) {
                    break;
                }
                valor = Integer.parseInt(input);
                success = true;
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please input a number", "Wrong Type", JOptionPane.ERROR_MESSAGE);
            }
        }while (!success);
        return valor;
    }
}
