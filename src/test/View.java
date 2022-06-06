package test;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * MDD and PC2 (Pointcache) animations
 * @author Leo
 */
public class View extends JPanel {
    
    // to test pc2, change to useMDD = false
    private boolean useMDD = true;
    private boolean usePC2 = !useMDD;
    
    private Wavefront2 wavefront;
    
    private PC2 pc2;
    private MDD mdd;
    
    private Renderer2 renderer;
    private BufferedImage texture;
    
    public View() {
            
    }

    public void start() {
        renderer = new Renderer2();
        
        try {
            texture = ImageIO.read(getClass().getResourceAsStream("/res/female_character2.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        wavefront = new Wavefront2("/res/female_char.obj");
        
        pc2 = new PC2("/res/female_char.pc2");
        mdd = new MDD("/res/female_char.mdd");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        update();
        
        drawMesh((Graphics2D) g);
        
        try {
            Thread.sleep(1000 / 120);
        } catch (InterruptedException ex) { }
        
        repaint();
        
    }
    
    double frame;
    
    public void update() {
        frame += 0.750;
        
        if (useMDD){
            if (frame > mdd.getTotalFrames() - 1) {
                frame -= (mdd.getTotalFrames() - 1);
            }
        }
        else if (usePC2) {
            if (frame > pc2.getPositions().length - 1) {
                frame -= (pc2.getPositions().length - 1);
            }
        }
        
    }

    private final Polygon polygon = new Polygon();

    private double angle = 0;
    
    private void drawMesh(Graphics2D g) {
        double[] vertices = null;
        double[] vertices2 = null;
        
        if (useMDD){
            vertices = mdd.getPoints()[(int) frame];
            vertices2 = mdd.getPoints()[((int) (frame + 1)) % mdd.getPoints().length];
        }
        else if (usePC2){
            vertices = pc2.getPositions()[(int) frame];
            vertices2 = pc2.getPositions()[((int) (frame + 1)) % pc2.getPositions().length];
        }
        
        double p = frame - (int) frame;
        
        renderer.clear();
        
        double[][] va = new double[3][5];
        
        for (int f = 0; f < wavefront.getFaces().length; f++) {
            int[] face = wavefront.getFaces()[f];
            polygon.reset();
            for (int fv = 0; fv < 3; fv++) {
                int vi = face[fv];
                int sti = face[fv + 3];
                
                double px = vertices[vi * 3 + 0] * 60 + p * (vertices2[vi * 3 + 0] * 60 - vertices[vi * 3 + 0] * 60);
                double py = vertices[vi * 3 + 1] * 60 + p * (vertices2[vi * 3 + 1] * 60 - vertices[vi * 3 + 1] * 60);
                double pz = vertices[vi * 3 + 2] * 60 + p * (vertices2[vi * 3 + 2] * 60 - vertices[vi * 3 + 2] * 60);

                double s = wavefront.getTextureCoordinates()[sti][0];
                double t = wavefront.getTextureCoordinates()[sti][1];
                
                va[fv][0] = px; //300 * (px / -pz);
                va[fv][1] = py - 50; //300 * (py / -pz);
                va[fv][2] = pz;
                va[fv][3] = s;
                va[fv][4] = t;
                
                g.fillOval((int) (va[fv][0] - 3), (int) (va[fv][1] - 3), 6, 6);
                polygon.addPoint((int) va[fv][0], (int) va[fv][1]);
            }
            g.draw(polygon);
            
            
            renderer.draw(va[0], va[1], va[2], texture, angle);
            
            va[0][0] += 50;
            va[1][0] += 50;
            va[2][0] += 50;
            renderer.draw(va[0], va[1], va[2], texture, angle);

            va[0][0] += 50;
            va[1][0] += 50;
            va[2][0] += 50;
            renderer.draw(va[0], va[1], va[2], texture, angle);
            
            va[0][0] -= 150;
            va[1][0] -= 150;
            va[2][0] -= 150;
            renderer.draw(va[0], va[1], va[2], texture, angle);

            va[0][0] -= 50;
            va[1][0] -= 50;
            va[2][0] -= 50;
            renderer.draw(va[0], va[1], va[2], texture, angle);
        }
        
        g.drawImage(renderer.nbi, 0, 0, 800, 600, 0, 0, 800, 600, this);
        angle += 0.02;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            JFrame frame = new JFrame();
            frame.setTitle("MDD and PC2 (Pointcache) Animations Test");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }    
    
}
