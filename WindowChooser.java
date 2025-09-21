import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import parser.Tokenizer;

public class WindowChooser extends JDialog implements ActionListener {
   private JTextField xMin;
   private JTextField xMax;
   private JTextField yMin;
   private JTextField yMax;
   private JTextField xTics;
   private JTextField yTics;
   private JButton ok;
   private JButton cancel;
   private CartesianGraph graph;
   private boolean success = false;

   public WindowChooser(CartesianGraph graph) {
      super((Frame)SwingUtilities.getAncestorOfClass(Frame.class, Runner.theRunner), true);
      this.setResizable(false);
      this.graph = graph;
      this.setLayout(new BorderLayout());
      JPanel south = new JPanel();
      this.ok = new JButton("OK");
      this.ok.addActionListener(this);
      this.cancel = new JButton("Cancel");
      this.cancel.addActionListener(this);
      south.add(this.ok);
      south.add(this.cancel);
      this.add(south, "South");
      JPanel center = new JPanel(new GridLayout(6, 2));
      center.add(new JLabel(" X Min"));
      center.add(this.xMin = new JTextField());
      center.add(new JLabel(" X Max"));
      center.add(this.xMax = new JTextField());
      center.add(new JLabel(" Y Min"));
      center.add(this.yMin = new JTextField());
      center.add(new JLabel(" Y Max"));
      center.add(this.yMax = new JTextField());
      center.add(new JLabel(" X Tics"));
      center.add(this.xTics = new JTextField());
      center.add(new JLabel(" Y Tics"));
      center.add(this.yTics = new JTextField());
      this.add(center, "Center");
      this.xMin.setText(String.valueOf(graph.minX));
      this.xMax.setText(String.valueOf(graph.maxX));
      this.yMin.setText(String.valueOf(graph.minY));
      this.yMax.setText(String.valueOf(graph.maxY));
      this.xTics.setText(String.valueOf(graph.xTics));
      this.yTics.setText(String.valueOf(graph.yTics));
      WindowChooser.FieldFocusAdapter focus = new WindowChooser.FieldFocusAdapter();
      this.xMin.addFocusListener(focus);
      this.xMax.addFocusListener(focus);
      this.yMin.addFocusListener(focus);
      this.yMax.addFocusListener(focus);
      this.xTics.addFocusListener(focus);
      this.yTics.addFocusListener(focus);
      this.setDefaultCloseOperation(2);
      this.pack();
      this.setLocationRelativeTo((Frame)SwingUtilities.getAncestorOfClass(Frame.class, Runner.theRunner));
      this.setVisible(true);
   }

   public boolean success() {
      return this.success;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == this.ok) {
         double xLo = 0.0D;
         double xHi = 0.0D;
         double yLo = 0.0D;
         double yHi = 0.0D;
         double xTic = 0.0D;
         double yTic = 0.0D;
         boolean error = false;

         try {
            xLo = Tokenizer.toTree(Tokenizer.tokenize(this.xMin.getText())).evaluate((TreeMap)null);
            xHi = Tokenizer.toTree(Tokenizer.tokenize(this.xMax.getText())).evaluate((TreeMap)null);
            yLo = Tokenizer.toTree(Tokenizer.tokenize(this.yMin.getText())).evaluate((TreeMap)null);
            yHi = Tokenizer.toTree(Tokenizer.tokenize(this.yMax.getText())).evaluate((TreeMap)null);
            xTic = Tokenizer.toTree(Tokenizer.tokenize(this.xTics.getText())).evaluate((TreeMap)null);
            yTic = Tokenizer.toTree(Tokenizer.tokenize(this.yTics.getText())).evaluate((TreeMap)null);
         } catch (Exception var16) {
            error = true;
         }

         if (!error && (xLo >= xHi || yLo >= yHi || xTic <= 0.0D || yTic <= 0.0D)) {
            error = true;
         }

         if (error) {
            JOptionPane.showMessageDialog((Component)null, "There seems to be an error in your window info...");
            return;
         }

         this.graph.setWindow(xLo, xHi, yLo, yHi, xTic, yTic);
         this.graph.recalcRelations();
         this.success = true;
         this.setVisible(false);
         this.dispose();
      } else {
         this.setVisible(false);
         this.dispose();
      }

   }

   class FieldFocusAdapter extends FocusAdapter {
      public void focusGained(final FocusEvent evt) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               JTextField field = (JTextField)evt.getSource();
               field.selectAll();
            }
         });
      }
   }
}
