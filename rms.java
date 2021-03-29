import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class rms {
    private final static String sSplitValue = " +";
    static String[] arrSplit = new String[3];
    static int size = 1586128;
    static int[] arr = new int[size];

    // Function that Calculate Root
    // Mean Square
    static float rmsValue(int arr[], int n) {
        int square = 0;
        float mean = 0;
        float root = 0;

        // Calculate square.
        for (int i = 0; i < n-1; i++) {
            square += Math.pow(arr[i], 2);
        }

        // Calculate Mean.
        mean = (square / (float) (n));

        // Calculate Root.
        root = (float) Math.sqrt(mean);

        return root;
    }

    // Driver Code
    public static void main(String args[]) throws IOException {
        File fInputDataSet = new File(args[0]);
        int i=0;
        try (BufferedReader br = new BufferedReader(new FileReader(fInputDataSet))) {
            String line;
           
            while ((line = br.readLine()) != null) {
                
                arrSplit = line.split(sSplitValue);
               int arr1 = Integer.parseInt(arrSplit[2]);
               arr[i] = arr1;
               i++;
            }     
    int n = arr.length; 
System.out.println(rmsValue(arr, n)); 
} 
}}
