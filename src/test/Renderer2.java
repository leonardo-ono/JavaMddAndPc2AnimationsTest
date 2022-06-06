package test;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.Arrays;

/**
 *
 * @author Leo
 */
public class Renderer2 {

    static final Polygon polygon = new Polygon();
    AffineTransform t1 = new AffineTransform();
    AffineTransform t2 = new AffineTransform();
    private double currentZ;
    
    // 0  1  2  3  4
    // x, y, z, s, t
    public void draw(double[] aor, double[] bor, double[] cor, BufferedImage texture, double angle) {
        double ao[] = aor.clone();
        double bo[] = bor.clone();
        double co[] = cor.clone();
        
        Point2D ps = new Point2D.Double();
        Point2D pd = new Point2D.Double();
        
        AffineTransform at = new AffineTransform();
        at.rotate(angle);
        
        ps.setLocation(ao[0], ao[2]);
        at.transform(ps, pd);
        ao[0] = pd.getX();
        ao[2] = pd.getY() - 100;

        ps.setLocation(bo[0], bo[2]);
        at.transform(ps, pd);
        bo[0] = pd.getX();
        bo[2] = pd.getY() - 100;

        ps.setLocation(co[0], co[2]);
        at.transform(ps, pd);
        co[0] = pd.getX();
        co[2] = pd.getY() - 100;
        
        double d = 300;
        double[] a = {d * ao[0] / -ao[2] + 400, 300 - d * ao[1] / -ao[2], ao[2], texture.getWidth() * ao[3], texture.getHeight() * (1 - ao[4])}; 
        double[] b = {d * bo[0] / -bo[2] + 400, 300 - d * bo[1] / -bo[2], bo[2], texture.getWidth() * bo[3], texture.getHeight() * (1 - bo[4])}; 
        double[] c = {d * co[0] / -co[2] + 400, 300 - d * co[1] / -co[2], co[2], texture.getWidth() * co[3], texture.getHeight() * (1 - co[4])}; 

        if (ao[2] > -1.0 || bo[2] > -1.0 || co[2] > -1.0) {
            return;
        }

        double c1x = b[0] - a[0];
        double c1y = b[1] - a[1];
        double c2x = c[0] - a[0];
        double c2y = c[1] - a[1];
        double cross = c1x * c2y - c1y * c2x;
        if (cross > 0) {
            return;
        }
        
        currentZ = a[2] + b[2] + c[2];
        
        t1.setTransform(a[3] - c[3], a[4] - c[4], b[3] - c[3], b[4] - c[4], c[3], c[4]);
        try {
            t1.invert();
        } catch (NoninvertibleTransformException ex) {
            return;
        }

        polygon.reset();
        polygon.addPoint((int) a[0], (int) a[1]);
        polygon.addPoint((int) b[0], (int) b[1]);
        polygon.addPoint((int) c[0], (int) c[1]);

        Shape originalClip = g.getClip();
        g.clip(polygon);

        t2.setTransform(a[0] - c[0], a[1] - c[1], b[0] - c[0], b[1] - c[1], c[0], c[1]);
        t2.concatenate(t1);
        
        g.drawImage(texture, t2, null);
        
        g.setClip(originalClip);
    }

    int[] data;
    double[] depth = new double[800 * 600];

    public void clear() {
        g.clearRect(0, 0, 800, 600);
        Arrays.fill(depth, -999999.9);
    }
    
    public class MyWritableRaster extends WritableRaster {

        public MyWritableRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point origin) {
            super(sampleModel, dataBuffer, origin);
        }

        @Override
        public void setDataElements(int x, int y, Object inData) {
            if (currentZ >= depth[y * 800 + x]) {
                super.setDataElements(x, y, inData); //To change body of generated methods, choose Tools | Templates.
                depth[y * 800 + x] = currentZ;
            }
        }

    }
    
    public BufferedImage nbimodel = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
    WritableRaster wr = new MyWritableRaster(nbimodel.getSampleModel(), (new DataBufferInt(800 * 600)), new Point()) { };
    public BufferedImage nbi = new BufferedImage(nbimodel.getColorModel(), wr, false, null);
    private Graphics2D g = nbi.createGraphics();
    
}
