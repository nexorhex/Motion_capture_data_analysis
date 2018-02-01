import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVManager {

    private File file;
    private ArrayList<String> header = new ArrayList<String>();
    private ArrayList<String> data = new ArrayList<String>();

    public CSVManager(File file) throws FileNotFoundException {
        super();
        this.file = file;
        loadFile();
    }

    private void loadFile() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        for (int i = 0; i < 7; i++) {
            this.header.add(scanner.nextLine());
        }
        int i = 0;
        while (scanner.hasNextLine()) {
            i++;
            scanner.nextLine();
            if (i % 10 == 0)
                this.data.add(scanner.nextLine());
        }
        scanner.close();
    }

    public float[][] parseData(String item) {
        String temp, temp2;
        int i = 0, itemPosition = 0, positionT = 0;
        Scanner scanner = new Scanner(header.get(header.size() - 1));
        Scanner scanner2 = new Scanner(header.get(3));
        scanner.useDelimiter(",");
        scanner2.useDelimiter(",");
        boolean notfound = true;
        while (scanner.hasNext() && notfound) {
            i++;
            temp = scanner.next();
            if (temp.contains("Time")) {
                positionT = i;
                notfound = false;
            }
        }
        notfound = true;
        i = 1;
        while (scanner2.hasNext() && notfound) {
            i++;
            temp2 = scanner2.next();
            if (temp2.contains(item)) {
                itemPosition = i;
                notfound = false;
            }
        }

        float matrix[][] = new float[data.size()][4];
        for (int j = 0; j < data.size(); j++) {
            scanner = new Scanner(data.get(j));
            scanner.useDelimiter(",");
            i = 0;
            while (scanner.hasNext()) {
                i++;
                if (i == positionT) {
                    matrix[j][0] = Float.valueOf(scanner.next());
                } else if (i == itemPosition) {
                    matrix[j][1] = Float.valueOf(scanner.next());
                    matrix[j][2] = Float.valueOf(scanner.next());
                    matrix[j][3] = Float.valueOf(scanner.next());
                    i = i + 2;
                } else {
                    scanner.next();
                }
            }
        }
        scanner.close();
        scanner2.close();
        return matrix;
    }

    public void Save(ArrayList<String> items, String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner scanner;
        Scanner scanner2;
        String temp;
        PrintWriter saver = new PrintWriter(file);

        saver.println(header.get(0));

        for (String s : data) {
            scanner = new Scanner(s);
            scanner2 = new Scanner(header.get(3));
            scanner.useDelimiter(",");
            while (scanner.hasNext() && scanner2.hasNext()) {
                scanner.next();
                temp = scanner2.next();
                for (String item : items) {
                    if (temp.contains(item)) {
                        saver.print(scanner.next());
                        saver.print(" , ");
                        saver.print(scanner.next());
                        saver.print(" , ");
                        saver.print(scanner.next());
                    } else {
                        scanner.next();
                    }
                }
            }
            saver.println();
        }
        saver.close();
    }

    public ArrayList<String> getHeader() {
        return header;
    }

    public ArrayList<String> getData() {
        return data;
    }

}