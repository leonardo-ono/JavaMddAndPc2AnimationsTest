package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stanford file format
 * Note: this implementation is imcomplete and can only read blender
 *       exported files with specific configuration .
 * @author Leo
 */
public class Ply {
    
    private double[][] vertices;
    private int[][] faces;

    public Ply(String res) {
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
            int vertexCount = 0;
            int facesCount = 0;
            boolean startData = false;
            while ((line = br.readLine()) != null) {
                System.out.println("line:" + line);
                if (startData) {
                    if (vertexCount > 0) {
                        int vi = vertices.length - vertexCount;
                        String[] data = line.split("\\ ");
                        for (int i = 0; i < 5; i++) {
                            vertices[vi][i] = Double.parseDouble(data[i]);
                        }
                        vertexCount--;
                    }
                    else if (facesCount > 0) {
                        int fi = faces.length - facesCount;
                        String[] data = line.split("\\ ");
                        for (int i = 0; i < 3; i++) {
                            faces[fi][i] = Integer.parseInt(data[i + 1]);
                        }
                        facesCount--;
                    }
                }
                else if (line.startsWith("element vertex")) {
                    vertexCount = Integer.parseInt(line.split("\\ ")[2]);
                }
                else if (line.startsWith("element face")) {
                    facesCount = Integer.parseInt(line.split("\\ ")[2]);
                }
                else if (line.startsWith("end_header")) {
                    startData = true;
                    vertices = new double[vertexCount][5];
                    faces = new int[facesCount][3];
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public double[][] getVertices() {
        return vertices;
    }

    public int[][] getFaces() {
        return faces;
    }
    
}
