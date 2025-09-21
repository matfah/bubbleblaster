import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

public class BBProbe extends JPanel implements BBMode, ActionListener, Printable {
   private GraphPanel panel;
   private JLabel mouseLoc;
   private JPanel west;
   private JPanel east;
   private JTextArea instructions;
   private JRadioButton[] gameModeButtons;
   private String[] gameModes = new String[]{"Probe Mode", "Guess Mode"};
   private JCheckBox[] allowedGraphsButtons;
   private String[] allowedGraphs = new String[]{"Lines", "Parabolas", "Circles", "Ellipses", "Hyperbolas"};
   private JButton startButton;
   private JButton printButton;
   private String name;
   private JButton clearProbesButton;
   private JLabel scoreLabel;
   private int score;
   private JLabel graphsStillHiddenLabel;
   private ArrayList<Relation> hiddenRelations = new ArrayList();
   private ArrayList<Integer> hiddenRelationTypes = new ArrayList();
   private ArrayList<String> gameLog = new ArrayList();

   public BBProbe() {
      this.setLayout(new BorderLayout());
      this.panel = new GraphPanel(new CartesianGraph(400, 400), this);
      this.panel.setTextFieldEnabled(false);
      this.add(this.panel, "Center");
      this.west = new JPanel();
      this.west.setPreferredSize(new Dimension(175, 0));
      this.add(this.west, "West");
      JLabel allowedLabel = new JLabel("Hidden Graphs", 0);
      allowedLabel.setPreferredSize(new Dimension(150, 25));
      this.west.add(allowedLabel);
      JPanel allowedPanel = new JPanel(new BorderLayout());
      JPanel leftPanel = new JPanel(new GridLayout(this.allowedGraphs.length, 1));
      JPanel rightPanel = new JPanel(new GridLayout(this.allowedGraphs.length, 1));
      allowedPanel.add(leftPanel, "West");
      allowedPanel.add(rightPanel, "East");
      this.allowedGraphsButtons = new JCheckBox[this.allowedGraphs.length];

      for(int i = 0; i < this.allowedGraphs.length; ++i) {
         this.allowedGraphsButtons[i] = new JCheckBox(this.allowedGraphs[i]);
         this.allowedGraphsButtons[i].addActionListener(this);
         leftPanel.add(this.allowedGraphsButtons[i]);
         if (i == 0) {
            this.allowedGraphsButtons[i].setSelected(true);
         }

         JLabel imageLabel = null;
         switch(i) {
         case 0:
            imageLabel = new JLabel(new ImageIcon(AbstractGraph.loadImage("x.png")));
            break;
         case 1:
            imageLabel = new JLabel(new ImageIcon(AbstractGraph.loadImage("o.png")));
            break;
         case 2:
            imageLabel = new JLabel(new ImageIcon(AbstractGraph.loadImage("triangle.png")));
            break;
         case 3:
            imageLabel = new JLabel(new ImageIcon(AbstractGraph.loadImage("square.png")));
            break;
         case 4:
            imageLabel = new JLabel(new ImageIcon(AbstractGraph.loadImage("triangle2.png")));
         }

         if (imageLabel != null) {
            rightPanel.add(imageLabel);
         }
      }

      this.west.add(allowedPanel);
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
      this.graphsStillHiddenLabel = new JLabel("", 0);
      this.graphsStillHiddenLabel.setVisible(false);
      this.graphsStillHiddenLabel.setPreferredSize(new Dimension(125, 25));
      this.graphsStillHiddenLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      this.west.add(this.graphsStillHiddenLabel);
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
      this.instructions = new JTextArea("Instructions:\n\nGraph horizontal and vertical line probes to find parts of the hidden graphs.  Earn points for each graph you guess right... lose points for each probe and incorrect graph guess!");
      this.instructions.setBackground(this.getBackground());
      this.instructions.setEditable(false);
      this.instructions.setLineWrap(true);
      this.instructions.setWrapStyleWord(true);
      this.instructions.setPreferredSize(new Dimension(150, 200));
      this.instructions.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      this.east.add(this.instructions);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 15));
      this.east.add(pad);
      JLabel typesLabel = new JLabel("Game Mode", 0);
      typesLabel.setPreferredSize(new Dimension(150, 25));
      this.east.add(typesLabel);
      JPanel radioButtonPanel = new JPanel(new GridLayout(this.gameModes.length, 1));
      ButtonGroup group = new ButtonGroup();
      this.gameModeButtons = new JRadioButton[this.gameModes.length];

      for(int i = 0; i < this.gameModes.length; ++i) {
         this.gameModeButtons[i] = new JRadioButton(this.gameModes[i]);
         if (i == 0) {
            this.gameModeButtons[i].setSelected(true);
         }

         this.gameModeButtons[i].setEnabled(false);
         radioButtonPanel.add(this.gameModeButtons[i]);
         group.add(this.gameModeButtons[i]);
      }

      this.east.add(radioButtonPanel);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 15));
      this.east.add(pad);
      this.clearProbesButton = new JButton("Clear Probes");
      this.east.add(this.clearProbesButton);
      this.clearProbesButton.addActionListener(this);
      this.clearProbesButton.setEnabled(false);
   }

   public ArrayList<String> allowedGraphs() {
      ArrayList<String> list = new ArrayList();
      int index = 0;
      JCheckBox[] var6;
      int var5 = (var6 = this.allowedGraphsButtons).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         JCheckBox box = var6[var4];
         if (box.isSelected()) {
            list.add(this.allowedGraphs[index]);
         }

         ++index;
      }

      return list;
   }

   public void addedRelation(Relation r) {
      if (this.gameModeButtons[0].isSelected()) {
         if (!r.isSimpleHorizontalOrVerticalLine()) {
            this.panel.removeRelation(r);
            JOptionPane.showMessageDialog((Component)null, "Only horizontal and vertical lines in probe mode, please!");
         } else {
            this.score -= 5;
            this.gameLog.add("Probing with " + r);

            for(int i = 0; i < this.hiddenRelations.size(); ++i) {
               double intercept = r.simpleHorizontalOrVerticalIntercept();
               TreeSet inter;
               if (r.isSimpleVerticalLine()) {
                  inter = ((Relation)this.hiddenRelations.get(i)).findSolutionsWithGiven('x', intercept);
               } else {
                  inter = ((Relation)this.hiddenRelations.get(i)).findSolutionsWithGiven('y', intercept);
               }

               Iterator var7 = inter.iterator();

               while(var7.hasNext()) {
                  Point2D p = (Point2D)var7.next();
                  this.panel.getGraph().addProbe((Integer)this.hiddenRelationTypes.get(i), p);
               }
            }

            (new Sound("sonar.wav")).play();
         }
      } else {
         boolean noneMatched = true;

         int i;
         for(i = this.hiddenRelations.size() - 1; i >= 0; --i) {
            if (((Relation)this.hiddenRelations.get(i)).equals(r)) {
               noneMatched = false;
               this.hiddenRelations.remove(i);
               this.hiddenRelationTypes.remove(i);
            }
         }

         if (noneMatched) {
            this.gameLog.add("Guessing incorrectly with " + r);
            (new Sound("buzz.wav")).play();
            this.panel.removeRelation(r);
            this.score -= 50;
         } else if (this.hiddenRelations.size() == 0) {
            this.gameLog.add("Guessing correctly with " + r);
            (new Sound("winner.wav")).play();
            this.clearProbesButton.setEnabled(false);
            this.panel.setTextFieldEnabled(false);
            this.panel.clearTextField();
            this.startButton.setText("Start Game");

            for(i = 0; i < this.allowedGraphsButtons.length; ++i) {
               this.allowedGraphsButtons[i].setEnabled(true);
            }

            for(i = 0; i < this.gameModeButtons.length; ++i) {
               this.gameModeButtons[i].setEnabled(false);
            }

            this.gameModeButtons[0].setSelected(true);
            this.score += 100;
         } else {
            this.gameLog.add("Guessing correctly with " + r);
            (new Sound("tada.wav")).play();
            this.score += 100;
         }
      }

      this.scoreLabel.setText("Score: " + this.score);
      this.graphsStillHiddenLabel.setText("Hidden Graphs: " + this.hiddenRelations.size());
   }

   public void mouseMovedTo(String x, String y) {
      if (x == null) {
         this.mouseLoc.setText("     none     ");
      } else {
         this.mouseLoc.setText("(" + x + ", " + y + ")");
      }

   }

   public void actionPerformed(ActionEvent e) {
      int i;
      if (e.getSource() instanceof JCheckBox) {
         for(i = 0; i < this.allowedGraphsButtons.length && !this.allowedGraphsButtons[i].isSelected(); ++i) {
         }

         if (i == this.allowedGraphsButtons.length) {
            this.startButton.setEnabled(false);
         } else {
            this.startButton.setEnabled(true);
         }
      } else if (e.getSource() == this.startButton) {
         if (this.startButton.getText().equals("Start Game")) {
            this.gameLog = new ArrayList();
            this.score = 0;
            this.graphsStillHiddenLabel.setVisible(true);
            this.scoreLabel.setVisible(true);
            this.scoreLabel.setText("Score: 0");
            this.clearProbesButton.setEnabled(true);
            this.panel.setTextFieldEnabled(true);
            this.startButton.setText("End Game");

            for(i = 0; i < this.allowedGraphsButtons.length; ++i) {
               this.allowedGraphsButtons[i].setEnabled(false);
            }

            for(i = 0; i < this.gameModeButtons.length; ++i) {
               this.gameModeButtons[i].setEnabled(true);
            }

            this.hiddenRelations.clear();
            this.hiddenRelationTypes.clear();
            this.panel.getGraph().clearProbes();
            this.panel.removeAllRelations();
            this.repaint();
            this.addHiddenRelations();
            this.graphsStillHiddenLabel.setText("Hidden Graphs: " + this.hiddenRelations.size());
         } else {
            this.graphsStillHiddenLabel.setVisible(false);
            this.scoreLabel.setVisible(false);
            this.clearProbesButton.setEnabled(false);
            this.panel.setTextFieldEnabled(false);
            this.panel.clearTextField();
            this.startButton.setText("Start Game");

            for(i = 0; i < this.allowedGraphsButtons.length; ++i) {
               this.allowedGraphsButtons[i].setEnabled(true);
            }

            for(i = 0; i < this.gameModeButtons.length; ++i) {
               this.gameModeButtons[i].setEnabled(false);
            }

            this.gameModeButtons[0].setSelected(true);
         }
      } else if (e.getSource() == this.clearProbesButton) {
         this.panel.getGraph().clearProbeLines();
         this.repaint();
      } else if (e.getSource() == this.printButton) {
         PrinterJob pj = PrinterJob.getPrinterJob();
         pj.setPrintable(this);
         this.name = JOptionPane.showInputDialog("Enter your name: ");
         if (this.name != null && !this.name.equals("") && pj.printDialog()) {
            try {
               pj.print();
            } catch (Exception var4) {
               var4.printStackTrace();
            }
         }
      }

   }

   public void addHiddenRelations() {
      for(int i = 0; i < this.allowedGraphsButtons.length; ++i) {
         if (this.allowedGraphsButtons[i].isSelected()) {
            String eq = "";
            int randomX;
            int randomY;
            int xCh;
            int yCh;
            int a;
            int b;
            int h;
            int k;
            String s;
            switch(i) {
            case 0:
               randomX = (int)(Math.random() * 19.0D) - 9;
               randomY = (int)(Math.random() * 19.0D) - 9;
               xCh = (int)(Math.random() * 3.0D) + 1;

               do {
                  yCh = (int)(Math.random() * 3.0D) + 1;
               } while(xCh == yCh && xCh != 1);

               if (Math.random() < 0.0D) {
                  yCh = -yCh;
               }

               eq = "y - " + randomY + " = " + yCh + "/" + xCh + "(x - " + randomX + ")";
               break;
            case 1:
               randomX = (int)(Math.random() * 13.0D) - 6;
               randomY = (int)(Math.random() * 13.0D) - 6;
               xCh = (int)(Math.random() * 3.0D) + 1;
               if (Math.random() < 0.5D) {
                  xCh = -xCh;
               }

               do {
                  do {
                     yCh = (int)(Math.random() * 3.0D) + 1;
                  } while(xCh == yCh && xCh != 1);
               } while(randomY + yCh > 9 || randomY + yCh < -9 || randomX + xCh > 9 || randomX + xCh < -9);

               eq = "y - " + randomY + " = " + yCh + "/" + xCh + "(x - " + randomX + ")^2";
               break;
            case 2:
               randomX = (int)(Math.random() * 13.0D) - 6;
               randomY = (int)(Math.random() * 13.0D) - 6;
               int randomRadius = 1 + (int)(Math.random() * (double)(9 - Math.max(Math.abs(randomX), Math.abs(randomY))));
               eq = "(x - " + randomX + ")^2 + (y - " + randomY + ")^2 = " + randomRadius + "^2";
               break;
            case 3:
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - a) + 1.0D);
               k = (int)(Math.random() * (double)(9 - b) + 1.0D);
               s = "(x ";
               if (Math.random() < 0.5D) {
                  s = s + " + ";
               } else {
                  s = s + " - ";
               }

               s = s + h + ")^2/" + a + "^2 + (y ";
               if (Math.random() < 0.5D) {
                  s = s + " + ";
               } else {
                  s = s + " - ";
               }

               s = s + k + ")^2 = 1";
               eq = s;
               break;
            case 4:
               boolean isHoriz = Math.random() < 0.5D;
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - a) + 1.0D);
               k = (int)(Math.random() * (double)(9 - b) + 1.0D);
               s = "(x ";
               if (!isHoriz) {
                  s = "-" + s;
               }

               if (Math.random() < 0.5D) {
                  s = s + " + ";
               } else {
                  s = s + " - ";
               }

               if (isHoriz) {
                  s = s + h + ")^2/" + a + "^2 - (y ";
               } else {
                  s = s + h + ")^2/" + a + "^2 + (y ";
               }

               if (Math.random() < 0.5D) {
                  s = s + " + ";
               } else {
                  s = s + " - ";
               }

               s = s + k + ")^2 = 1";
               eq = s;
            }

            try {
               this.hiddenRelations.add(new Relation(eq));
               this.hiddenRelationTypes.add(i);
            } catch (Exception var14) {
               var14.printStackTrace();
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
      message = "Hiddent Graphs Types: " + this.allowedGraphs().toString();
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
      Iterator var18 = this.gameLog.iterator();

      while(var18.hasNext()) {
         String s = (String)var18.next();
         bounds = fm.getStringBounds(s, graphics);
         graphics.drawString(s, x, (int)((double)y + bounds.getHeight()));
         y = (int)((double)y + bounds.getHeight() + 5.0D);
         nextX = Math.max(nextX, (int)((double)x + bounds.getWidth()));
         if (pageFormat.getImageableHeight() - (double)y < 20.0D) {
            y = startY;
            x = nextX + 30;
            if ((double)(x + 200) > pageFormat.getImageableWidth()) {
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
