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
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

public class BBGraphMatch extends JPanel implements BBMode, ActionListener, Printable {
   private GraphPanel panel;
   private JLabel mouseLoc;
   private JPanel west;
   private JPanel east;
   private JRadioButton[] graphTypeButtons;
   private String[] graphTypes = new String[]{"Lines", "Absolute Value", "Parabolas", "Circles", "Ellipses", "Hyperbolas", "Mixed"};
   private JRadioButton[] modeButtons;
   private String[] modes = new String[]{"Easy to Hard", "Start at level...", "Mixed Difficulty"};
   private JButton startButton;
   private JButton printButton;
   private String name;
   private JLabel levelLabel;
   private JLabel winLabel;
   private JButton showAnswerButton;
   private JButton clearGraphsButton;
   private JButton nextProblemButton;
   private int currentLevel;
   private int startLevel;
   private Relation currentRelation;
   private boolean cheated;
   private boolean wrong;
   private int numAttempted;
   private int numRight;
   private JLabel answerLabel;
   private ArrayList<String> pastProblems = new ArrayList();
   private JTextArea instructions;
   private TreeMap<Integer, ArrayList<String>> gameLog = new TreeMap();

   public BBGraphMatch() {
      this.setLayout(new BorderLayout());
      this.panel = new GraphPanel(new CartesianGraph(400, 400), this);
      this.panel.setTextFieldEnabled(false);
      this.add(this.panel, "Center");
      this.west = new JPanel();
      this.west.setPreferredSize(new Dimension(175, 0));
      this.add(this.west, "West");
      JLabel typesLabel = new JLabel("Graph Types", 0);
      typesLabel.setPreferredSize(new Dimension(150, 25));
      this.west.add(typesLabel);
      JPanel radioButtonPanel = new JPanel(new GridLayout(this.graphTypes.length, 1));
      ButtonGroup group = new ButtonGroup();
      this.graphTypeButtons = new JRadioButton[this.graphTypes.length];

      for(int i = 0; i < this.graphTypes.length; ++i) {
         this.graphTypeButtons[i] = new JRadioButton(this.graphTypes[i]);
         if (i == 0) {
            this.graphTypeButtons[i].setSelected(true);
         }

         radioButtonPanel.add(this.graphTypeButtons[i]);
         group.add(this.graphTypeButtons[i]);
      }

      this.west.add(radioButtonPanel);
      JPanel pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.west.add(pad);
      JLabel modesLabel = new JLabel("Game Modes", 0);
      modesLabel.setPreferredSize(new Dimension(150, 25));
      this.west.add(modesLabel);
      radioButtonPanel = new JPanel(new GridLayout(this.modes.length, 1));
      group = new ButtonGroup();
      this.modeButtons = new JRadioButton[this.modes.length];

      for(int i = 0; i < this.modes.length; ++i) {
         this.modeButtons[i] = new JRadioButton(this.modes[i]);
         if (i == 0) {
            this.modeButtons[i].setSelected(true);
         }

         this.modeButtons[i].addActionListener(this);
         radioButtonPanel.add(this.modeButtons[i]);
         group.add(this.modeButtons[i]);
      }

      this.west.add(radioButtonPanel);
      pad = new JPanel();
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
      this.instructions = new JTextArea("Instructions:\n\nWrite the equation of the graph to the left.");
      this.instructions.setBackground(this.getBackground());
      this.instructions.setEditable(false);
      this.instructions.setLineWrap(true);
      this.instructions.setWrapStyleWord(true);
      this.instructions.setPreferredSize(new Dimension(150, 100));
      this.instructions.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      this.east.add(this.instructions);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.east.add(pad);
      this.levelLabel = new JLabel("Level 1 of 20");
      this.east.add(this.levelLabel);
      this.levelLabel.setVisible(false);
      pad = new JPanel();
      pad.setPreferredSize(new Dimension(175, 25));
      this.east.add(pad);
      JPanel mid = new JPanel(new GridLayout(3, 1, 0, 10));
      this.showAnswerButton = new JButton("Reveal Answer");
      this.showAnswerButton.addActionListener(this);
      mid.add(this.showAnswerButton);
      this.showAnswerButton.setVisible(false);
      this.clearGraphsButton = new JButton("Clear Graphs");
      this.clearGraphsButton.addActionListener(this);
      mid.add(this.clearGraphsButton);
      this.clearGraphsButton.setVisible(false);
      this.nextProblemButton = new JButton("Next Problem");
      this.nextProblemButton.addActionListener(this);
      mid.add(this.nextProblemButton);
      this.nextProblemButton.setVisible(false);
      this.east.add(mid);
      this.answerLabel = new JLabel("x", 0);
      this.answerLabel.setPreferredSize(new Dimension(175, 25));
      this.east.add(this.answerLabel);
      this.answerLabel.setVisible(false);
      this.winLabel = new JLabel("<html><p>You Win!</p></html>", 0);
      this.winLabel.setFont(new Font("Geneva", 1, 18));
      this.winLabel.setPreferredSize(new Dimension(125, 60));
      this.winLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      this.winLabel.setVisible(false);
      this.east.add(this.winLabel);
   }

   public void actionPerformed(ActionEvent e) {
      if (this.panel.isAdding()) {
         JOptionPane.showMessageDialog((Component)null, "Please wait for the last equation to be added.");
      } else {
         if (e.getSource() == this.startButton) {
            JRadioButton butt;
            int var3;
            int var4;
            JRadioButton[] var5;
            if (this.startButton.getText().equals("Start Game")) {
               this.numAttempted = 0;
               this.numRight = 0;
               this.gameLog = new TreeMap();
               if (this.modeButtons[1].isSelected()) {
                  this.currentLevel = this.startLevel;
               } else {
                  this.currentLevel = 1;
               }

               this.startButton.setText("End Game");
               var4 = (var5 = this.graphTypeButtons).length;

               for(var3 = 0; var3 < var4; ++var3) {
                  butt = var5[var3];
                  butt.setEnabled(false);
               }

               var4 = (var5 = this.modeButtons).length;

               for(var3 = 0; var3 < var4; ++var3) {
                  butt = var5[var3];
                  butt.setEnabled(false);
               }

               this.panel.removeAllRelations();
               this.panel.setTextFieldEnabled(true);
               this.levelLabel.setText("Level " + this.currentLevel + " of  20");
               this.levelLabel.setVisible(true);
               this.showAnswerButton.setVisible(true);
               this.winLabel.setVisible(false);
               this.pastProblems.clear();
               this.showNextEquation();
            } else {
               this.startButton.setText("Start Game");
               var4 = (var5 = this.graphTypeButtons).length;

               for(var3 = 0; var3 < var4; ++var3) {
                  butt = var5[var3];
                  butt.setEnabled(true);
               }

               var4 = (var5 = this.modeButtons).length;

               for(var3 = 0; var3 < var4; ++var3) {
                  butt = var5[var3];
                  butt.setEnabled(true);
               }

               this.panel.clearTextField();
               this.panel.setTextFieldEnabled(false);
               this.panel.removeAllRelations();
               this.levelLabel.setVisible(false);
               this.showAnswerButton.setVisible(false);
               this.clearGraphsButton.setVisible(false);
               this.nextProblemButton.setVisible(false);
               this.answerLabel.setVisible(false);
               this.winLabel.setVisible(false);
               this.panel.rotateBackColor();
               this.repaint();
            }
         } else if (e.getSource() == this.modeButtons[1]) {
            while(true) {
               String choice = JOptionPane.showInputDialog((Component)null, "Enter the start level (from 1 to 20).");

               try {
                  this.startLevel = Integer.parseInt(choice);
                  if (this.startLevel >= 1 && this.startLevel <= 20) {
                     break;
                  }

                  JOptionPane.showMessageDialog((Component)null, "Please only pick numbers between 1 and 20.");
               } catch (Exception var7) {
                  JOptionPane.showMessageDialog((Component)null, "That wasn't a valid level number (1 to 20)");
               }
            }
         } else if (e.getSource() == this.nextProblemButton) {
            if (!this.cheated && !this.wrong) {
               ++this.currentLevel;
            }

            this.panel.rotateBackColor();
            this.panel.setTextFieldEnabled(true);
            this.levelLabel.setText("Level " + this.currentLevel + " of  20");
            this.panel.removeAllRelations();
            this.showNextEquation();
            this.cheated = false;
            this.wrong = false;
            this.nextProblemButton.setVisible(false);
            this.showAnswerButton.setVisible(true);
            this.answerLabel.setVisible(false);
            this.panel.clearTextField();
         } else if (e.getSource() == this.showAnswerButton) {
            ((ArrayList)this.gameLog.get(this.numAttempted - 1)).add("Player cheated by showing the answer...");
            this.cheated = true;
            this.answerLabel.setVisible(true);
            this.answerLabel.setText(this.currentRelation.toString());
         } else if (e.getSource() == this.clearGraphsButton) {
            this.panel.removeAllRelations();
            this.panel.rotateBackColor();
            this.panel.addRelation(this.currentRelation);
            this.clearGraphsButton.setVisible(false);
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

   public void addedRelation(Relation r) {
      if (this.currentRelation == r) {
         this.panel.rotateToNextColor();
      } else {
         if (this.currentRelation.equals(r)) {
            ((ArrayList)this.gameLog.get(this.numAttempted - 1)).add("Correct: " + r);
            if (this.currentLevel == 20 && !this.cheated && !this.wrong) {
               ++this.numRight;
               (new Sound("winner.wav")).play();
               this.panel.rotateBackColor();
               this.nextProblemButton.setVisible(false);
               this.showAnswerButton.setVisible(false);
               this.clearGraphsButton.setVisible(false);
               this.panel.setTextFieldEnabled(false);
               this.startButton.setText("Start Game");
               JRadioButton[] var5;
               int var4 = (var5 = this.graphTypeButtons).length;

               JRadioButton butt;
               int var3;
               for(var3 = 0; var3 < var4; ++var3) {
                  butt = var5[var3];
                  butt.setEnabled(true);
               }

               var4 = (var5 = this.modeButtons).length;

               for(var3 = 0; var3 < var4; ++var3) {
                  butt = var5[var3];
                  butt.setEnabled(true);
               }

               this.winLabel.setVisible(true);
            } else {
               if (!this.cheated && !this.wrong) {
                  ++this.numRight;
                  this.nextProblemButton.setText("Next Level");
               } else {
                  this.nextProblemButton.setText("Next Problem");
               }

               (new Sound("tada.wav")).play();
               this.nextProblemButton.setVisible(true);
               this.showAnswerButton.setVisible(false);
               this.clearGraphsButton.setVisible(false);
               this.panel.setTextFieldEnabled(false);
            }
         } else {
            ((ArrayList)this.gameLog.get(this.numAttempted - 1)).add("Wrong Guess: " + r);
            this.wrong = true;
            this.clearGraphsButton.setVisible(true);
         }

      }
   }

   public void mouseMovedTo(String x, String y) {
      if (x == null) {
         this.mouseLoc.setText("     none     ");
      } else {
         this.mouseLoc.setText("(" + x + ", " + y + ")");
      }

   }

   public void showNextEquation() {
      try {
         ++this.numAttempted;
         this.cheated = false;
         this.wrong = false;
         int tries = 0;

         String nextEQ;
         do {
            nextEQ = this.nextEquation();
            ++tries;
         } while(this.pastProblems.contains(nextEQ) && tries < 10);

         this.pastProblems.add(nextEQ);
         this.currentRelation = new Relation(nextEQ);
         this.panel.addRelation(this.currentRelation, false);
         this.gameLog.put(this.numAttempted - 1, new ArrayList());
         ((ArrayList)this.gameLog.get(this.numAttempted - 1)).add("#" + this.numAttempted + ": " + this.currentRelation.toString());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public String nextEquation() {
      int level = this.currentLevel;
      if (this.modeButtons[2].isSelected()) {
         level = (int)(Math.random() * 20.0D) + 1;
      }

      if (this.graphTypeButtons[0].isSelected()) {
         return this.nextLineEquation(level);
      } else if (this.graphTypeButtons[1].isSelected()) {
         return this.nextAbsoluteValueEquation(level);
      } else if (this.graphTypeButtons[2].isSelected()) {
         return this.nextParabolaEquation(level);
      } else if (this.graphTypeButtons[3].isSelected()) {
         return this.nextCircleEquation(level);
      } else if (this.graphTypeButtons[4].isSelected()) {
         return this.nextEllipseEquation(level);
      } else {
         return this.graphTypeButtons[5].isSelected() ? this.nextHyperbolaEquation(level) : this.nextMixedEquation(level);
      }
   }

   public String getGraphType() {
      int index = 0;
      JRadioButton[] var5;
      int var4 = (var5 = this.graphTypeButtons).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         JRadioButton butt = var5[var3];
         if (butt.isSelected()) {
            return this.graphTypes[index];
         }

         ++index;
      }

      return "Unknown Graph Type";
   }

   public String getGameMode() {
      int index = 0;
      JRadioButton[] var5;
      int var4 = (var5 = this.modeButtons).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         JRadioButton butt = var5[var3];
         if (butt.isSelected()) {
            return this.modes[index];
         }

         ++index;
      }

      return "Unknown Graph Mode";
   }

   private String nextMixedEquation(int level) {
      int rand = (int)(Math.random() * 6.0D);
      if (rand == 0) {
         return this.nextLineEquation(level);
      } else if (rand == 1) {
         return this.nextParabolaEquation(level);
      } else if (rand == 2) {
         return this.nextCircleEquation(level);
      } else if (rand == 3) {
         return this.nextEllipseEquation(level);
      } else {
         return rand == 4 ? this.nextAbsoluteValueEquation(level) : this.nextHyperbolaEquation(level);
      }
   }

   private String nextHyperbolaEquation(int level) {
      boolean isHoriz = Math.random() < 0.5D;
      int a;
      if (level <= 3) {
         a = (int)(Math.random() * 9.0D) + 1;
         return isHoriz ? "x^2/" + a + "^2 - y^2 = 1" : "-x^2/" + a + "^2 + y^2 = 1";
      } else if (level <= 6) {
         a = (int)(Math.random() * 9.0D) + 1;
         return isHoriz ? "x^2 - y^2/" + a + "^2 = 1" : "-x^2 + y^2/" + a + "^2 = 1";
      } else {
         int b;
         if (level <= 9) {
            a = (int)(Math.random() * 9.0D) + 1;
            b = (int)(Math.random() * 9.0D) + 1;
            return isHoriz ? "x^2/" + a + "^2 - y^2/" + b + "^2 = 1" : "-x^2/" + a + "^2 + y^2/" + b + "^2 = 1";
         } else {
            int h;
            String s;
            if (level <= 12) {
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - a) + 1.0D);
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
                  s = s + h + ")^2/" + a + "^2 - y^2/" + b + "^2 = 1";
               } else {
                  s = s + h + ")^2/" + a + "^2 + y^2/" + b + "^2 = 1";
               }

               return s;
            } else if (level <= 15) {
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - b) + 1.0D);
               if (isHoriz) {
                  s = "x^2/" + a + "^2 - (y ";
               } else {
                  s = "-x^2/" + a + "^2 + (y ";
               }

               if (Math.random() < 0.5D) {
                  s = s + " + ";
               } else {
                  s = s + " - ";
               }

               s = s + h + ")^2/" + b + "^2 = 1";
               return s;
            } else {
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - a) + 1.0D);
               int k = (int)(Math.random() * (double)(9 - b) + 1.0D);
               String s = "(x ";
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
               return s;
            }
         }
      }
   }

   private String nextEllipseEquation(int level) {
      int a;
      if (level <= 3) {
         a = (int)(Math.random() * 9.0D) + 1;
         return "x^2/" + a + "^2 + y^2 = 1";
      } else if (level <= 6) {
         a = (int)(Math.random() * 9.0D) + 1;
         return "x^2 + y^2/" + a + "^2 = 1";
      } else {
         int b;
         if (level <= 9) {
            a = (int)(Math.random() * 9.0D) + 1;
            b = (int)(Math.random() * 9.0D) + 1;
            return "x^2/" + a + "^2 + y^2/" + b + "^2 = 1";
         } else {
            int h;
            String s;
            if (level <= 12) {
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - a) + 1.0D);
               s = "(x ";
               if (Math.random() < 0.5D) {
                  s = s + " + ";
               } else {
                  s = s + " - ";
               }

               s = s + h + ")^2/" + a + "^2 + y^2/" + b + "^2 = 1";
               return s;
            } else if (level <= 15) {
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - b) + 1.0D);
               s = "x^2/" + a + "^2 + (y ";
               if (Math.random() < 0.5D) {
                  s = s + " + ";
               } else {
                  s = s + " - ";
               }

               s = s + h + ")^2/" + b + "^2 = 1";
               return s;
            } else {
               a = (int)(Math.random() * 9.0D) + 1;
               b = (int)(Math.random() * 9.0D) + 1;
               h = (int)(Math.random() * (double)(9 - a) + 1.0D);
               int k = (int)(Math.random() * (double)(9 - b) + 1.0D);
               String s = "(x ";
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
               return s;
            }
         }
      }
   }

   private String nextCircleEquation(int level) {
      if (level <= 4) {
         return "x^2 + y^2 = " + (int)(Math.random() * 9.0D + 1.0D) + "^2";
      } else {
         int h;
         int k;
         String s;
         if (level <= 8) {
            h = (int)(Math.random() * 9.0D + 1.0D);
            k = (int)(Math.random() * (double)(9 - h) + 1.0D);
            s = "(x ";
            if (Math.random() < 0.5D) {
               s = s + "- " + h + ")";
            } else {
               s = s + "+ " + h + ")";
            }

            s = s + "^2 + y^2 = " + k + "^2";
            return s;
         } else if (level <= 12) {
            h = (int)(Math.random() * 9.0D + 1.0D);
            k = (int)(Math.random() * (double)(9 - h) + 1.0D);
            s = "x^2 + ";
            s = s + "(y ";
            if (Math.random() < 0.5D) {
               s = s + "- " + h + ")";
            } else {
               s = s + "+ " + h + ")";
            }

            s = s + "^2 = " + k + "^2";
            return s;
         } else {
            h = (int)(Math.random() * 9.0D + 1.0D);
            k = (int)(Math.random() * 9.0D + 1.0D);
            int radius = (int)(Math.random() * (double)(9 - Math.max(h, k)) + 1.0D);
            String s = "(x ";
            if (Math.random() < 0.5D) {
               s = s + "- " + h + ")";
            } else {
               s = s + "+ " + h + ")";
            }

            s = s + "^2 + (y ";
            if (Math.random() < 0.5D) {
               s = s + "- " + k + ")";
            } else {
               s = s + "+ " + k + ")";
            }

            s = s + "^2 = " + radius + "^2";
            return s;
         }
      }
   }

   private String nextParabolaEquation(int level) {
      int aNumer;
      String s;
      if (level <= 4) {
         do {
            aNumer = (int)(Math.random() * 11.0D) - 5;
         } while(aNumer == 0);

         if (Math.random() < 0.5D) {
            s = "y = x^2 ";
         } else {
            s = "y = -x^2 ";
         }

         if (aNumer < 0) {
            s = s + "- ";
         } else {
            s = s + "+ ";
         }

         s = s + Math.abs(aNumer);
         return s;
      } else if (level <= 8) {
         do {
            aNumer = (int)(Math.random() * 11.0D) - 5;
         } while(aNumer == 0);

         if (Math.random() < 0.5D) {
            s = "y = (x ";
         } else {
            s = "y = -(x ";
         }

         if (aNumer < 0) {
            s = s + "- ";
         } else {
            s = s + "+ ";
         }

         s = s + Math.abs(aNumer);
         s = s + ")^2";
         return s;
      } else {
         int k;
         if (level <= 12) {
            do {
               aNumer = (int)(Math.random() * 11.0D) - 5;
            } while(aNumer == 0);

            do {
               k = (int)(Math.random() * 11.0D) - 5;
            } while(k == 0);

            String s;
            if (Math.random() < 0.5D) {
               s = "y = (x ";
            } else {
               s = "y = -(x ";
            }

            if (aNumer < 0) {
               s = s + "- ";
            } else {
               s = s + "+ ";
            }

            s = s + Math.abs(aNumer);
            s = s + ")^2 ";
            if (k < 0) {
               s = s + "- ";
            } else {
               s = s + "+ ";
            }

            s = s + Math.abs(k);
            return s;
         } else {
            int gcd;
            String s;
            if (level <= 16) {
               do {
                  aNumer = (int)(Math.random() * 7.0D) - 3;
               } while(aNumer == 0);

               k = (int)(Math.random() * 3.0D) + 1;
               gcd = gcd(Math.abs(aNumer), Math.abs(k));
               aNumer /= gcd;
               k /= gcd;
               if (k < 0) {
                  aNumer *= -1;
                  k *= -1;
               }

               s = "y = ";
               if (k == 1) {
                  s = s + aNumer;
               } else {
                  s = s + aNumer + "/" + k;
               }

               s = s + "x^2";
               return s;
            } else {
               do {
                  aNumer = (int)(Math.random() * 7.0D) - 3;
               } while(aNumer == 0);

               k = (int)(Math.random() * 3.0D) + 1;
               gcd = gcd(Math.abs(aNumer), Math.abs(k));
               aNumer /= gcd;
               k /= gcd;
               if (k < 0) {
                  aNumer *= -1;
                  k *= -1;
               }

               s = "y = ";
               if (k == 1) {
                  s = s + aNumer;
               } else {
                  s = s + aNumer + "/" + k;
               }

               int h = (int)(Math.random() * 11.0D) - 5;

               int k;
               do {
                  do {
                     k = (int)(Math.random() * 11.0D) - 5;
                  } while(k + aNumer >= 9);
               } while(k + aNumer <= -9);

               if (h < 0) {
                  s = s + "(x - " + Math.abs(h) + ")^2";
               } else if (h > 0) {
                  s = s + "(x + " + Math.abs(h) + ")^2";
               } else {
                  s = s + "x^2";
               }

               if (k < 0) {
                  s = s + " - " + Math.abs(k);
               } else {
                  s = s + " + " + Math.abs(k);
               }

               return s;
            }
         }
      }
   }

   public String nextAbsoluteValueEquation(int level) {
      if (level <= 3) {
         return "y = abs(x + " + ((int)(Math.random() * 19.0D) - 9) + ")";
      } else if (level <= 6) {
         return "y = abs(x) + " + ((int)(Math.random() * 19.0D) - 9);
      } else if (level <= 9) {
         return "y = abs(x + " + ((int)(Math.random() * 19.0D) - 9) + ") + " + ((int)(Math.random() * 19.0D) - 9);
      } else if (level <= 12) {
         return "y = -abs(x + " + ((int)(Math.random() * 19.0D) - 9) + ") + " + ((int)(Math.random() * 19.0D) - 9);
      } else {
         return level <= 15 ? "y = " + ((int)(Math.random() * 7.0D) - 3) + "abs(x + " + ((int)(Math.random() * 13.0D) - 6) + ") + " + ((int)(Math.random() * 13.0D) - 6) : "y = " + ((int)(Math.random() * 3.0D + 1.0D) - 3) + "/" + (int)(Math.random() * 3.0D + 1.0D) + "*abs(x + " + ((int)(Math.random() * 13.0D) - 6) + ") + " + ((int)(Math.random() * 13.0D) - 6);
      }
   }

   public String nextLineEquation(int level) {
      if (level <= 4) {
         return Math.random() < 0.5D ? "x = " + ((int)(Math.random() * 19.0D) - 9) : "y = " + ((int)(Math.random() * 19.0D) - 9);
      } else {
         int x1;
         int y1;
         if (level <= 6) {
            x1 = (int)(Math.random() * 19.0D) - 9;

            do {
               y1 = (int)(Math.random() * 2.0D) - 1;
            } while(y1 == 0);

            return "y = " + y1 + "x + " + x1;
         } else if (level <= 8) {
            x1 = (int)(Math.random() * 19.0D) - 9;

            do {
               y1 = (int)(Math.random() * 11.0D) - 5;
            } while(y1 == 0);

            return "y = " + y1 + "x + " + x1;
         } else {
            int x2;
            int y2;
            if (level <= 16) {
               x1 = (int)(Math.random() * 19.0D) - 9;
               y1 = (int)(Math.random() * 11.0D) - 5;
               x2 = (int)(Math.random() * 4.0D) + 1;
               y2 = gcd(Math.abs(y1), Math.abs(x2));
               y1 /= y2;
               x2 /= y2;
               if (x2 < 0) {
                  y1 *= -1;
                  x2 *= -1;
               }

               return x2 == 1 ? "y = " + y1 + "x + " + x1 : "y = " + y1 + "/" + x2 + "x + " + x1;
            } else {
               int slopeNumer;
               int slopeDenomer;
               do {
                  do {
                     x1 = (int)(Math.random() * 19.0D) - 9;
                     y1 = (int)(Math.random() * 19.0D) - 9;
                     x2 = (int)(Math.random() * 19.0D) - 9;
                     y2 = (int)(Math.random() * 19.0D) - 9;
                     slopeNumer = y2 - y1;
                     slopeDenomer = x2 - x1;
                     int gcd = gcd(Math.abs(slopeNumer), Math.abs(slopeDenomer));
                     slopeNumer /= gcd;
                     slopeDenomer /= gcd;
                     if (slopeDenomer < 0) {
                        slopeNumer *= -1;
                        slopeDenomer *= -1;
                     }
                  } while(x1 == x2 && y1 == y2);
               } while(x2 - x1 == 0 || y2 - y1 == 0 || slopeDenomer == 1 || slopeDenomer > 4 || Math.abs(slopeNumer) > 4);

               String str = "y ";
               if (y1 < 0) {
                  str = str + "+ ";
               } else {
                  str = str + "- ";
               }

               str = str + Math.abs(y1) + " = " + slopeNumer + "/" + slopeDenomer + "(x";
               if (x1 < 0) {
                  str = str + "+ ";
               } else {
                  str = str + "- ";
               }

               str = str + Math.abs(x1) + ")";
               return str;
            }
         }
      }
   }

   public static int gcd(int a, int b) {
      return b == 0 ? a : gcd(b, a % b);
   }

   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
      int pageCount = 1;
      Graphics2D g2 = (Graphics2D)graphics;
      g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY() - pageFormat.getHeight() * (double)pageIndex);
      FontMetrics fm = graphics.getFontMetrics();
      int y = 0;
      String message = "Name: " + this.name;
      Rectangle2D bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      int y = (int)((double)y + bounds.getHeight() + 5.0D);
      message = "Login Name: " + System.getProperty("user.name");
      bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      y = (int)((double)y + bounds.getHeight() + 5.0D);
      message = "Game Type: " + this.getGraphType();
      bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      y = (int)((double)y + bounds.getHeight() + 5.0D);
      message = "Game Mode: " + this.getGameMode();
      bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      y = (int)((double)y + bounds.getHeight() + 5.0D);
      message = "Game Statistics: (" + this.numRight + "/" + this.numAttempted + ") = " + (new DecimalFormat("0.00%")).format((double)this.numRight / (double)this.numAttempted);
      bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      y = (int)((double)y + bounds.getHeight() + 15.0D);
      message = "Game Log";
      bounds = fm.getStringBounds(message, graphics);
      graphics.drawString(message, 0, (int)((double)y + bounds.getHeight()));
      y = (int)((double)y + bounds.getHeight() + 5.0D);
      int startY = y;
      int x = 0;

      for(Iterator var14 = this.gameLog.keySet().iterator(); var14.hasNext(); y += 10) {
         int probNum = (Integer)var14.next();
         ArrayList<String> info = (ArrayList)this.gameLog.get(probNum);
         int nextX = x;
         Iterator var17 = info.iterator();

         while(var17.hasNext()) {
            String s = (String)var17.next();
            bounds = fm.getStringBounds(s, graphics);
            graphics.drawString(s, x, (int)((double)y + bounds.getHeight()));
            y = (int)((double)y + bounds.getHeight() + 5.0D);
            nextX = Math.max(nextX, (int)((double)x + bounds.getWidth()));
            if (pageFormat.getImageableHeight() - (double)y < 20.0D) {
               y = startY;
               x = nextX + 30;
               if ((double)(x + 100) > pageFormat.getImageableWidth()) {
                  x = 0;
                  y = 0;
                  g2.translate(0.0D, pageFormat.getHeight());
                  ++pageCount;
               }
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
