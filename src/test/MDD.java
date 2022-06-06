package test;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MDD Reader.
 * 
 * Reference:
 * http://rodolphe-vaillant.fr/entry/134/mdd-file-exporter-importer-source-code-c-c
 * 
 * int totalframes;
 * int totalPoints;
 * float *Times; //time for each frame
 * float **points[3];
 * 
 * Note: The MDD file format is in "Motorola Big Endian byte order" 
 *       as opposed to the Intel Little Endian standard.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class MDD {
    
    private int totalFrames;
    private int totalPoints;
    private double[] times;
    private double[][] points;
    
    public MDD(String res) {
        try (
            DataInputStream dis 
                    = new DataInputStream(getClass().getResourceAsStream(res));
        ) 
        {
            totalFrames = dis.readInt();
            totalPoints = dis.readInt();
            times = new double[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                times[i] = dis.readFloat();
            }
            
            points = new double[totalFrames][totalPoints * 3];
            for (int s = 0; s < totalFrames; s++) {
                for (int p = 0; p < totalPoints * 3; p++) {
                    points[s][p] = dis.readFloat();
                }
            }
            System.out.println("finished!" + (totalFrames * totalPoints * 3 * 4));
        } catch (IOException ex) {
            Logger.getLogger(MDD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public double[] getTimes() {
        return times;
    }

    public double[][] getPoints() {
        return points;
    }
    
}
