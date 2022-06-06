package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Leo
 */
public class Wavefront {
    
    private int[][] faces;
    private double[][] textureCoordinates;

    public Wavefront(String res) {
        loadModel(res);
    }
    
    private void loadModel(String res) {
        try (
            InputStreamReader isr 
                = new InputStreamReader(getClass().getResourceAsStream(res));
                
            BufferedReader br = new BufferedReader(isr);
        ) 
        {
            String line;
            List<double[]> textureCoordinatesTmp = new ArrayList<>();
            List<int[]> facesTmp = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                System.out.println("line:" + line);
                if (line.startsWith("vt ")) {
                    String[] data = line.split("\\ ");
                    double[] stData = new double[2];
                    stData[0] = Double.parseDouble(data[1]);
                    stData[1] = Double.parseDouble(data[2]);
                    textureCoordinatesTmp.add(stData);
                }
                else if (line.startsWith("f ")) {
                    String[] data = line.split("\\ ");
                    int[] faceData = new int[6];
                    faceData[0] = Integer.parseInt(data[1].split("/")[0]) - 1;
                    faceData[1] = Integer.parseInt(data[2].split("/")[0]) - 1;
                    faceData[2] = Integer.parseInt(data[3].split("/")[0]) - 1;
                    faceData[3] = Integer.parseInt(data[1].split("/")[1]) - 1;
                    faceData[4] = Integer.parseInt(data[2].split("/")[1]) - 1;
                    faceData[5] = Integer.parseInt(data[3].split("/")[1]) - 1;
                    facesTmp.add(faceData);
                }
            }
            faces = facesTmp.toArray(new int[0][0]);
            textureCoordinates = textureCoordinatesTmp.toArray(new double[0][0]);
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public int[][] getFaces() {
        return faces;
    }

    public double[][] getTextureCoordinates() {
        return textureCoordinates;
    }
    
}
