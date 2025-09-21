import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class BBBlaster extends JPanel implements BBMode, ActionListener, Printable {
   private GraphPanel panel;
   private JPanel west;
   private JPanel east;
   private JLabel mouseLoc;
   private JButton startButton;
   private JButton printButton;
   private JButton clearGraphs;
   private JButton undoLastShot;
   private JTextArea instructions;
   private JLabel scoreLabel;
   private int score;
   private String name;
   private JLabel winLabel;
   private ArrayList<Point2D> remainingPoints = new ArrayList();
   private ArrayList<Relation> addedRelations = new ArrayList();
   private ArrayList<TreeSet<Point2D>> pointsHitByAddedRelation = new ArrayList();
   private ArrayList<String> gameInfo = new ArrayList();

   public BBBlaster() {
      this.setLayout(new BorderLayout());
      this.panel = new GraphPanel(new CartesianGraph(400, 400), this);
      this.panel.setTextFieldEnabled(false);
      this.add(this.panel, "Center");
      this.west = new JPanel();
      this.west.setPreferredSize(new Dimension(175, 0));
      JPanel pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.west.add(pad);
      JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
      this.startButton = new JButton("Start Game");
      this.startButton.addActionListener(this);
      buttonPanel.add(this.startButton);
      this.printButton = new JButton("Print");
      this.printButton.addActionListener(this);
      buttonPanel.add(this.printButton);
      this.west.add(buttonPanel);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.west.add(pad);
      this.scoreLabel = new JLabel("Score: 0", 0);
      this.scoreLabel.setVisible(false);
      this.scoreLabel.setPreferredSize(new Dimension(125, 25));
      this.scoreLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      this.west.add(this.scoreLabel);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.west.add(pad);
      this.winLabel = new JLabel("<html><p>You Win!</p></html>", 0);
      this.winLabel.setFont(new Font("Geneva", 1, 18));
      this.winLabel.setPreferredSize(new Dimension(125, 75));
      this.winLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      this.winLabel.setVisible(false);
      this.west.add(this.winLabel);
      this.add(this.west, "West");
      this.east = new JPanel();
      this.east.setPreferredSize(new Dimension(175, 0));
      this.add(this.east, "East");
      this.east.add(new JLabel("Mouse Location"));
      this.mouseLoc = new JLabel("     none     ", 0);
      this.mouseLoc.setPreferredSize(new Dimension(175, 25));
      this.east.add(this.mouseLoc);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.east.add(pad);
      this.instructions = new JTextArea("Instructions:\n\nWrite equations that intersect points on the graph.  The more points you intersect at once, the higher your score!");
      this.instructions.setBackground(this.getBackground());
      this.instructions.setEditable(false);
      this.instructions.setLineWrap(true);
      this.instructions.setWrapStyleWord(true);
      this.instructions.setPreferredSize(new Dimension(150, 150));
      this.instructions.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      this.east.add(this.instructions);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.east.add(pad);
      JPanel mid = new JPanel(new GridLayout(2, 1, 0, 10));
      this.clearGraphs = new JButton("Clear Graphs");
      this.clearGraphs.setVisible(false);
      this.clearGraphs.addActionListener(this);
      this.undoLastShot = new JButton("Undo Last Shot");
      this.undoLastShot.setVisible(false);
      this.undoLastShot.addActionListener(this);
      mid.add(this.clearGraphs);
      mid.add(this.undoLastShot);
      this.east.add(mid);
      this.add(this.east, "East");
   }

   public void addedRelation(Relation r) {
      if (r.isInequality()) {
         this.panel.removeRelation(r);
         this.repaint();
         JOptionPane.showMessageDialog((Component)null, "Inequalitites are not allowed!");
      } else {
         this.addedRelations.add(r);
         this.clearGraphs.setVisible(true);
         this.undoLastShot.setVisible(true);
         char[] var10000 = new char[]{'x', 'y'};
         double[] vals = new double[2];
         TreeSet<Point2D> justRemoved = new TreeSet();
         BufferedImage img = new BufferedImage(this.panel.getGraph().width, this.panel.getGraph().height, 1);
         Graphics g = img.getGraphics();
         this.panel.getGraph().drawPaths(g);
         int w = img.getWidth((ImageObserver)null);
         int h = img.getHeight((ImageObserver)null);
         int[] rgbs = new int[w * h];
         img.getRGB(0, 0, w, h, rgbs, 0, w);

         for(int i = this.remainingPoints.size() - 1; i >= 0; --i) {
            Point2D p = (Point2D)this.remainingPoints.get(i);
            Point2D conv = this.panel.getGraph().convertRelationXYtoGraphXY(p, this.panel.getGraph().width, this.panel.getGraph().height);
            int pixelsAround = 8;
            if ((rgbs[conv.getIntY() * w + conv.getIntX()] & 16711680) >> 16 > 0) {
               this.panel.getGraph().breakBubble(p);
               this.remainingPoints.remove(i);
               justRemoved.add(p);
            } else {
               boolean done = false;

               for(double radius = 1.0D; radius <= (double)pixelsAround && !done; ++radius) {
                  for(double angle = 0.0D; angle < 6.283185307179586D && !done; ++angle) {
                     if ((rgbs[(int)Math.round(((double)conv.getIntY() + radius * Math.sin(angle)) * (double)w + (double)conv.getIntX() + radius * Math.cos(angle))] & 16711680) == 16711680) {
                        this.panel.getGraph().breakBubble(p);
                        this.remainingPoints.remove(i);
                        justRemoved.add(p);
                        done = true;
                     }
                  }
               }
            }
         }

         double newPoints = Math.pow(2.0D, (double)justRemoved.size()) - 1.0D;
         this.gameInfo.add("Added " + r.toString() + "and earned " + (int)newPoints + " points");
         this.score = (int)((double)this.score + newPoints);
         this.scoreLabel.setText("Score: " + this.score);
         this.pointsHitByAddedRelation.add(justRemoved);
         if (this.remainingPoints.size() == 0) {
            (new Sound("winner.wav")).play();
            this.clearGraphs.setVisible(false);
            this.undoLastShot.setVisible(false);
            this.panel.setTextFieldEnabled(false);
            this.startButton.setText("Start Game");
            this.winLabel.setVisible(true);
         } else {
            (new Sound("swoosh.wav")).play();
         }

         this.repaint();
      }
   }

   public void mouseMovedTo(String x, String y) {
      if (x == null) {
         this.mouseLoc.setText("     none     ");
      } else {
         this.mouseLoc.setText("(" + x + ", " + y + ")");
      }

   }

   public void actionPerformed(ActionEvent e) {
      if (this.panel.isAdding()) {
         JOptionPane.showMessageDialog((Component)null, "Please wait for the last equation to be added.");
      } else {
         if (e.getSource() == this.startButton) {
            if (this.startButton.getText().equals("Start Game")) {
               this.gameInfo = new ArrayList();
               this.panel.removeAllRelations();
               this.panel.getGraph().clearPoints();
               this.winLabel.setVisible(false);
               this.remainingPoints.clear();
               this.addedRelations.clear();
               this.pointsHitByAddedRelation.clear();
               this.startButton.setText("End Game");
               this.score = 0;
               this.scoreLabel.setText("Score: 0");
               this.scoreLabel.setVisible(true);
               this.panel.setTextFieldEnabled(true);

               while(this.remainingPoints.size() < 13) {
                  Point2D p = new Point2D((double)((int)(Math.random() * 19.0D) - 9), (double)((int)(Math.random() * 19.0D) - 9));
                  if (!this.remainingPoints.contains(p)) {
                     this.remainingPoints.add(p);
                     this.panel.getGraph().addBubble(p);
                  }
               }

               this.repaint();
            } else {
               this.startButton.setText("Start Game");
               this.scoreLabel.setVisible(false);
               this.panel.setTextFieldEnabled(false);
               this.clearGraphs.setVisible(false);
               this.undoLastShot.setVisible(false);
            }
         } else if (e.getSource() == this.clearGraphs) {
            this.clearGraphs.setVisible(false);
            this.panel.removeAllRelations();
            this.repaint();
         } else if (e.getSource() == this.undoLastShot) {
            Relation r = (Relation)this.addedRelations.get(this.addedRelations.size() - 1);
            this.panel.removeRelation((Relation)this.addedRelations.remove(this.addedRelations.size() - 1));
            TreeSet<Point2D> undoPoints = (TreeSet)this.pointsHitByAddedRelation.remove(this.pointsHitByAddedRelation.size() - 1);
            Iterator var5 = undoPoints.iterator();

            while(var5.hasNext()) {
               Point2D p = (Point2D)var5.next();
               this.panel.getGraph().unbreakBubble(p);
               this.remainingPoints.add(p);
            }

            if (this.pointsHitByAddedRelation.size() == 0) {
               this.undoLastShot.setVisible(false);
               this.clearGraphs.setVisible(false);
            }

            double lessPoints = Math.pow(2.0D, (double)undoPoints.size()) - 1.0D;
            this.gameInfo.add("Undid " + r.toString() + "and lost " + (int)lessPoints + " points");
            this.score = (int)((double)this.score - lessPoints);
            this.scoreLabel.setText("Score: " + this.score);
            this.repaint();
         } else if (e.getSource() == this.printButton) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintable(this);
            this.name = JOptionPane.showInputDialog("Enter your name: ");
            if (this.name != null && !this.name.equals("") && pj.printDialog()) {
               try {
                  pj.print();
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }
         }

      }
   }

   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
      int pageCount = 1;
      Graphics2D g2 = (Graphics2D)graphics;
      int y = 0;
      g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY() - pageFormat.getHeight() * (double)pageIndex);
      FontMetrics fm = graphics.getFontMetrics();
      Rectangle2D bounds = fm.getStringBounds(this.name, graphics);
      graphics.drawString("Name: " + this.name, 0, (int)((double)y + bounds.getHeight()));
      int y = (int)((double)y + bounds.getHeight() + 5.0D);
      String message = "Login Name: " + System.getProperty("user.name");
      bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      y = (int)((double)y + bounds.getHeight() + 5.0D);
      BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), 2);
      Graphics imgG = img.getGraphics();
      this.paint(imgG);
      imgG.dispose();
      double factor = pageFormat.getImageableWidth() / (double)this.getWidth();
      graphics.drawImage(img, 0, y, (int)pageFormat.getImageableWidth(), (int)((double)this.getHeight() * factor), (ImageObserver)null);
      y = (int)((double)y + (double)this.getHeight() * factor + 15.0D);
      message = "Game Log";
      bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      y = (int)((double)y + bounds.getHeight() + 5.0D);
      int startY = y;
      int x = 0;
      int nextX = x;
      Iterator var18 = this.gameInfo.iterator();

      while(var18.hasNext()) {
         String s = (String)var18.next();
         bounds = fm.getStringBounds(s, graphics);
         graphics.drawString(s, x, (int)((double)y + bounds.getHeight()));
         y = (int)((double)y + bounds.getHeight() + 5.0D);
         nextX = Math.max(nextX, (int)((double)x + bounds.getWidth()));
         if (pageFormat.getImageableHeight() - (double)y < 20.0D) {
            y = startY;
            x = nextX + 30;
            if ((double)x + bounds.getWidth() > pageFormat.getImageableWidth()) {
               x = 0;
               y = 0;
               g2.translate(0.0D, pageFormat.getHeight());
               ++pageCount;
            }
         }
      }

      if (pageIndex >= pageCount) {
         return 1;
      } else {
         return 0;
      }
   }
}
