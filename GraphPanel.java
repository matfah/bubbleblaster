import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GraphPanel extends JPanel implements ActionListener, MouseMotionListener {
   private JTextField eqField;
   private JButton graphButton;
   private JButton graphButton2;
   private JProgressBar progress;
   private JButton killAddButton;
   private CartesianGraph graph;
   private boolean adding = false;
   private JPanel south;
   private JPanel southWest;
   private JPanel southEastNoProgress;
   private JPanel southEastProgress;
   private BBMode mode;

   public GraphPanel(CartesianGraph g, BBMode mode) {
      this.mode = mode;
      this.setLayout(new BorderLayout());
      this.graph = g;
      this.south = new JPanel(new GridLayout(1, 2));
      this.southWest = new JPanel(new BorderLayout());
      this.eqField = new JTextField();
      this.eqField.addActionListener(this);
      this.graphButton = new JButton("Graph");
      this.graphButton2 = new JButton("Graph");
      this.graphButton.addActionListener(this);
      this.graphButton2.addActionListener(this);
      this.southEastNoProgress = new JPanel(new FlowLayout(0));
      this.southEastNoProgress.add(this.graphButton);
      this.southWest.add(this.eqField, "Center");
      this.south.add(this.southWest);
      this.south.add(this.southEastNoProgress);
      this.southEastProgress = new JPanel(new FlowLayout(0));
      this.southEastProgress.add(this.graphButton2);
      this.progress = new JProgressBar();
      this.progress.setIndeterminate(true);
      this.southEastProgress.add(this.progress);
      this.killAddButton = new JButton("x");
      this.killAddButton.addActionListener(this);
      this.southEastProgress.add(this.killAddButton);
      this.add(this.south, "South");
      this.addMouseMotionListener(this);
   }

   public void setTextFieldEnabled(boolean isEnabled) {
      this.eqField.setEnabled(isEnabled);
      this.graphButton.setEnabled(isEnabled);
      this.graphButton2.setEnabled(isEnabled);
      if (isEnabled) {
         this.eqField.requestFocus();
      }

   }

   public CartesianGraph getGraph() {
      return this.graph;
   }

   public BufferedImage createGraphImage() {
      int extra = 2;
      BufferedImage img = new BufferedImage(this.graph.width + extra, this.graph.height + extra, 2);
      Graphics2D g = img.createGraphics();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, this.graph.width + extra, this.graph.height + extra);
      this.graph.draw(g);
      return img;
   }

   public void paint(Graphics g) {
      super.paint(g);
      int graphWidth = this.graph.width;
      AffineTransform old = ((Graphics2D)g).getTransform();
      old.translate((double)((this.getWidth() - graphWidth) / 2), 0.0D);
      ((Graphics2D)g).setTransform(old);
      this.graph.draw(g);
      old.translate((double)(-(this.getWidth() - graphWidth) / 2), 0.0D);
      ((Graphics2D)g).setTransform(old);
   }

   public boolean isAdding() {
      return this.adding;
   }

   public void removeRelation(Relation r) {
      this.graph.remove(r);
   }

   public void removeAllRelations() {
      this.graph.removeAllRelations();
   }

   public void addRelation(Relation r, boolean showProgress) {
      if (showProgress) {
         this.addRelation(r);
      } else {
         this.adding = true;
         boolean success = this.graph.add(r);
         this.adding = false;
         if (success) {
            this.mode.addedRelation(r);
            this.repaint();
         }
      }

   }

   public void addRelation(final Relation r) {
      this.adding = true;
      if (!r.isFunctionalFor('x') && !r.isFunctionalFor('y')) {
         this.south.remove(this.southEastNoProgress);
         this.south.add(this.southEastProgress);
         this.south.validate();
         this.south.repaint();
      }

      Runnable runner = new Runnable() {
         public void run() {
            boolean success = false;

            try {
               success = GraphPanel.this.graph.add(r);
            } catch (Exception var5) {
               var5.printStackTrace();
            }

            GraphPanel.this.adding = false;
            final boolean result = success;

            try {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     if (!r.isFunctionalFor('x') && !r.isFunctionalFor('y')) {
                        GraphPanel.this.south.removeAll();
                        GraphPanel.this.south.add(GraphPanel.this.southWest);
                        GraphPanel.this.south.add(GraphPanel.this.southEastNoProgress);
                        GraphPanel.this.south.validate();
                     }

                     GraphPanel.this.repaint();
                     if (result) {
                        GraphPanel.this.mode.addedRelation(r);
                     }

                  }
               });
            } catch (Exception var4) {
               var4.printStackTrace();
            }

         }
      };
      Thread thread = new Thread(runner);
      thread.start();
   }

   public void stopAdding() {
      if (this.adding) {
         this.graph.killAdd();
      }

   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == this.killAddButton) {
         this.stopAdding();
      } else if (e.getSource() == this.eqField || e.getSource() == this.graphButton || e.getSource() == this.graphButton2) {
         try {
            Relation r = new Relation(this.eqField.getText());
            this.addRelation(r);
         } catch (Exception var3) {
            JOptionPane.showMessageDialog((Component)null, "There was an error graphing your equation!");
            var3.printStackTrace();
         }
      }

   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent e) {
      int x = e.getX() - (this.getWidth() - this.graph.width) / 2;
      int y = e.getY();
      if (x >= 0 && y >= 0 && x <= this.graph.width && y <= this.graph.height) {
         Point2D result = this.graph.convertGraphXYToRelationXY(new Point2D((double)x, (double)y), this.graph.width, this.graph.height);
         int x1 = (int)(result.getX() * 100.0D);
         String xRes = Math.abs(x1 / 100) + ".";
         if (result.getX() < 0.0D) {
            xRes = "-" + xRes;
         }

         if (Math.abs(x1) % 100 < 10) {
            xRes = xRes + 0;
         }

         xRes = xRes + Math.abs(x1) % 100;
         int y1 = (int)(result.getY() * 100.0D);
         String yRes = Math.abs(y1 / 100) + ".";
         if (result.getY() < 0.0D) {
            yRes = "-" + yRes;
         }

         if (Math.abs(y1) % 100 < 10) {
            yRes = yRes + 0;
         }

         yRes = yRes + Math.abs(y1) % 100;
         this.mode.mouseMovedTo(xRes, yRes);
      } else {
         this.mode.mouseMovedTo((String)null, (String)null);
      }
   }

   public void clearTextField() {
      this.eqField.setText("");
   }

   public void rotateToNextColor() {
      this.graph.colors.add((Color)this.graph.colors.removeFirst());
   }

   public void rotateBackColor() {
      this.graph.colors.add(0, (Color)this.graph.colors.removeLast());
   }
}
