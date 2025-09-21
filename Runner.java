import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class Runner extends JFrame implements ListSelectionListener {
   private JLabel sideLabel;
   private JPanel west;
   private JList sideList;
   private String[] modes = new String[]{"Grapher", "Graph Match", "Bubble Blaster", "Probe"};
   private JPanel[] modePanels = new JPanel[]{new BBGrapher(), new BBGraphMatch(), new BBBlaster(), new BBProbe()};
   public static Runner theRunner;

   public static void main(String[] args) {
      Runner r = new Runner();
      r.setTitle("Bubble Blaster");
      r.setResizable(false);
      r.setDefaultCloseOperation(3);
      r.init();
      r.pack();
      r.start();
      r.setVisible(true);
   }

   public void init() {
      theRunner = this;

      try {
         UIManager.setLookAndFeel(new MetalLookAndFeel());
      } catch (Exception var2) {
      }

      Container c = this.getContentPane();
      c.setLayout(new BorderLayout());
      c.setPreferredSize(new Dimension(950, 450));
      c.setMinimumSize(new Dimension(950, 450));
      this.west = new JPanel(new BorderLayout());
      this.west.setBackground(Color.WHITE);
      this.sideLabel = new JLabel("Activities", 0);
      this.sideLabel.setFont(new Font("Geneva", 1, 24));
      this.sideList = new JList(this.modes);
      this.sideList.setFont(new Font("Geneva", 1, 18));
      this.sideList.setSelectionMode(0);
      this.sideList.setSelectedIndex(0);
      this.sideList.addListSelectionListener(this);
      this.west.add(this.sideLabel, "North");
      this.west.add(this.sideList, "Center");
      c.add(this.west, "West");
   }

   public void start() {
      try {
         AbstractGraph g = new CartesianGraph(400, 400);
         Relation r = new Relation("x^2 + y^2 = 1");
         long before = System.nanoTime();
         g.add(r);
         double diff = (double)(System.nanoTime() - before) / 1000000.0D;
         g.remove(r);
         if (diff > 2000.0D) {
            AbstractGraph.GLOBAL_DIVISIONS = Math.max(400, (int)((double)(1000 * AbstractGraph.GLOBAL_DIVISIONS) / diff));
         }
      } catch (Exception var7) {
      }

      this.getContentPane().add(this.modePanels[0], "Center");
   }

   public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
         int index = this.sideList.getSelectedIndex();
         this.getContentPane().removeAll();
         this.getContentPane().add(this.west, "West");
         this.getContentPane().add(this.modePanels[index], "Center");
         this.getContentPane().validate();
         this.getContentPane().repaint();
      }
   }
}
