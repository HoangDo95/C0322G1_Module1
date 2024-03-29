package bai_thi.khung.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadAndWrite {
    public static void writeToFile(String pathFile, String data) {
        File file = new File(pathFile);
        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readToFile(String pathFile) {
        File file = new File(pathFile);
        List<String[]> stringList = new ArrayList<>();
        String line;
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while ((line = bufferedReader.readLine()) != null && !line.equals("")) {
                String[] stringArr = line.split(",");
                stringList.add(stringArr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringList;
    }
}
