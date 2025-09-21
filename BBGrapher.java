import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BBGrapher extends JPanel implements BBMode, ActionListener, Printable {
   private GraphPanel panel;
   private JPanel west;
   private JPanel east;
   private int numRelations = 0;
   private JLabel mouseLoc;
   private JButton changeWindow;
   private JButton copyGraph;
   private JButton printButton;
   private JButton changeColor;

   public BBGrapher() {
      this.setLayout(new BorderLayout());
      this.panel = new GraphPanel(new CartesianGraph(400, 400), this);
      this.add(this.panel, "Center");
      this.west = new JPanel();
      this.west.setPreferredSize(new Dimension(175, 0));
      this.add(this.west, "West");
      JLabel label = new JLabel("Click to remove", 0);
      this.west.add(label);
      label.setPreferredSize(new Dimension(175, 25));
      this.east = new JPanel();
      this.east.setPreferredSize(new Dimension(175, 0));
      this.add(this.east, "East");
      this.east.add(new JLabel("Mouse Location"));
      this.mouseLoc = new JLabel("     none     ", 0);
      this.mouseLoc.setPreferredSize(new Dimension(175, 25));
      this.east.add(this.mouseLoc);
      JPanel mid = new JPanel(new GridLayout(4, 1, 0, 10));
      this.changeWindow = new JButton("Change Window");
      this.changeWindow.addActionListener(this);
      mid.add(this.changeWindow);
      this.copyGraph = new JButton("Copy Graph");
      this.copyGraph.addActionListener(this);
      mid.add(this.copyGraph);
      this.printButton = new JButton("Print");
      this.printButton.addActionListener(this);
      mid.add(this.printButton);
      this.changeColor = new JButton("Next Color");
      this.changeColor.addActionListener(this);
      mid.add(this.changeColor);
      JPanel pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.east.add(pad);
      this.east.add(mid);
   }

   public void actionPerformed(ActionEvent e) {
      if (this.panel.isAdding()) {
         JOptionPane.showMessageDialog((Component)null, "Please wait for the last equation to be added.");
      } else {
         if (e.getSource() == this.changeWindow) {
            if ((new WindowChooser(this.panel.getGraph())).success()) {
               this.panel.repaint();
            }
         } else if (e.getSource() == this.copyGraph) {
            BufferedImage img = this.panel.createGraphImage();
            if (!System.getProperty("os.name").startsWith("Windows")) {
               this.makeAlpha(img, -1);
            }

            BBGrapher.ImageSelection imgSel = new BBGrapher.ImageSelection(img);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, (ClipboardOwner)null);
         } else if (e.getSource() == this.printButton) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintable(this);
            if (pj.printDialog()) {
               try {
                  pj.print();
               } catch (Exception var4) {
                  var4.printStackTrace();
               }
            }
         } else if (e.getSource() == this.changeColor && (new ColorChooser(this.panel.getGraph())).success()) {
            this.panel.repaint();
         }

      }
   }

   public void makeAlpha(BufferedImage image, int color) {
      int w = image.getWidth();
      int h = image.getHeight();

      for(int i = 0; i < w; ++i) {
         for(int j = 0; j < h; ++j) {
            if (image.getRGB(i, j) == color) {
               image.setRGB(i, j, 16777215);
            }
         }
      }

   }

   public void addedRelation(final Relation r) {
      r.setColor((Color)this.panel.getGraph().colors.peek());
      final JButton button = new JButton(r.toString());
      button.setBackground((Color)this.panel.getGraph().colors.peek());
      if (button.getBackground().equals(Color.BLACK)) {
         button.setForeground(Color.WHITE);
      }

      button.setFont(new Font("Geneva", 1, 12));
      button.setAction(new AbstractAction(r.toString()) {
         public void actionPerformed(ActionEvent e) {
            BBGrapher.this.panel.removeRelation(r);
            BBGrapher.this.west.remove(button);
            BBGrapher var10000 = BBGrapher.this;
            var10000.numRelations = var10000.numRelations - 1;
            BBGrapher.this.validate();
            BBGrapher.this.repaint();
         }
      });
      this.west.add(button);
      ++this.numRelations;
      this.validate();
   }

   public void mouseMovedTo(String x, String y) {
      if (x == null) {
         this.mouseLoc.setText("     none     ");
      } else {
         this.mouseLoc.setText("(" + x + ", " + y + ")");
      }

   }

   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
      if (pageIndex > 0) {
         return 1;
      } else {
         Graphics2D g2 = (Graphics2D)graphics;
         double factor = pageFormat.getImageableWidth() / (double)this.getWidth();
         g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
         g2.scale(factor, factor);
         this.paint(graphics);
         return 0;
      }
   }

   public static class ImageSelection implements Transferable {
      private Image image;

      public ImageSelection(Image image) {
         this.image = image;
      }

      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[]{DataFlavor.imageFlavor};
      }

      public boolean isDataFlavorSupported(DataFlavor flavor) {
         return DataFlavor.imageFlavor.equals(flavor);
      }

      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
         if (!DataFlavor.imageFlavor.equals(flavor)) {
            throw new UnsupportedFlavorException(flavor);
         } else {
            return this.image;
         }
      }
   }
}
