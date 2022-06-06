package test;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 *
 * @author Leo
 */
public class Renderer {

    static final Polygon polygon = new Polygon();
    AffineTransform t1 = new AffineTransform();
    AffineTransform t2 = new AffineTransform();
    private double currentZ;
    
    private boolean drawingTriangle = false;
    
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
        
//        a[0] += 400;
//        b[0] += 400;
//        c[0] += 400;
//        a[1] = 300 - a[1];
//        b[1] = 300 - b[1];
//        c[1] = 300 - c[1];

        polygon.reset();
        polygon.addPoint((int) a[0], (int) a[1]);
        polygon.addPoint((int) b[0], (int) b[1]);
        polygon.addPoint((int) c[0], (int) c[1]);

        Shape originalClip = g.getClip();
        g.clip(polygon);

        t2.setTransform(a[0] - c[0], a[1] - c[1], b[0] - c[0], b[1] - c[1], c[0], c[1]);
        t2.concatenate(t1);
        
        drawingTriangle = true;
        g.drawImage(texture, t2, null);
        drawingTriangle = false;
        
        g.setClip(originalClip);
        
        //g.setColor(Color.BLUE);
        //g.draw(polygon);
    }

    int[] data;
    double[] depth = new double[800 * 600];

    public void clear() {
        //Arrays.fill(data, 0);
        //g.setBackground(Color.RED);
        //currentZ = -Double.MAX_VALUE;
        g.clearRect(0, 0, 800, 600);
        Arrays.fill(depth, -999999.9);
    }
    
    public class MyWritableRaster extends WritableRaster {

        public MyWritableRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point origin) {
            super(sampleModel, dataBuffer, origin);
        }

        @Override
        public void setDataElements(int x, int y, Object inData) {
            //if (drawingTriangle) {
            //    ((int[]) inData)[0] = 0xff0000ff;
            //}
            if (currentZ >= depth[y * 800 + x]) {
                super.setDataElements(x, y, inData); //To change body of generated methods, choose Tools | Templates.
                depth[y * 800 + x] = currentZ;
            }
        }

    }
    
    public class MyDataBuffer extends DataBuffer {

        private DataBufferInt dataBufferInt;
        
        public MyDataBuffer(DataBufferInt dataBufferInt) {
            super(DataBuffer.TYPE_INT, dataBufferInt.getSize());
            this.dataBufferInt = dataBufferInt;
            depth = new double[dataBufferInt.getSize()];
        }

        @Override
        public int getElem(int bank, int i) {
            return 0;
        }

        @Override
        public void setElem(int bank, int i, int val) {
        }

        @Override
        public void setElem(int i, int val) {
            if (currentZ >= depth[i]) {
                dataBufferInt.setElem(i, val);
                depth[i] = currentZ;
            }
        }

        @Override
        public int getElem(int i) {
            return dataBufferInt.getElem(i);
        }
        
    }

    public BufferedImage nbimodel = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
    
    DirectColorModel colorModel = (DirectColorModel) ColorModel.getRGBdefault();
    
    //WritableRaster raster = colorModel.createCompatibleWritableRaster(800, 600);
    //WritableRaster wr = colorModel.createCompatibleWritableRaster(800, 600); // new WritableRaster(nbimodel.getSampleModel(), new DataBufferInt(800 * 600), new Point()) { };
    WritableRaster wr = new MyWritableRaster(nbimodel.getSampleModel(), (new DataBufferInt(800 * 600)), new Point()) { };
    
    
    WritableRaster wr2 = Raster.createPackedRaster( (new DataBufferInt(new int[800 * 600], 800 * 600)), 800, 600, 800, new int[] { 0xff0000, 0xff00, 0xff, 0xff000000 }, new Point());

    SampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, 800, 600, new int[] { 0xff0000, 0xff00, 0xff, 0xff000000 });
    WritableRaster wr3 = Raster.createWritableRaster(sm, new MyDataBuffer(new DataBufferInt(800 * 600)), null);
    
    public BufferedImage nbi = new BufferedImage(colorModel, wr, false, null);
    
    private Graphics2D g = nbi.createGraphics();
    
}
