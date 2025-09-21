import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

public class ColorChooser extends JDialog implements ActionListener {
   private boolean success;
   private CartesianGraph graph;
   private JRadioButton[] buttons;
   private JPanel[] colors;
   private ButtonGroup group;
   private JButton confirm;
   private JButton cancel;

   public ColorChooser(CartesianGraph g) {
      super((Frame)SwingUtilities.getAncestorOfClass(Frame.class, Runner.theRunner), true);
      this.setResizable(false);
      this.graph = g;
      LinkedList<Color> allColors = g.colors;
      this.group = new ButtonGroup();
      this.buttons = new JRadioButton[allColors.size()];
      this.colors = new JPanel[allColors.size()];

      JPanel middle;
      for(int i = 0; i < allColors.size(); ++i) {
         middle = new JPanel();
         middle.setBackground((Color)allColors.get(i));
         this.colors[i] = middle;
         this.buttons[i] = new JRadioButton();
         this.group.add(this.buttons[i]);
      }

      this.getContentPane().setLayout(new BorderLayout());
      JPanel center = new JPanel();
      this.getContentPane().add(center, "Center");
      middle = new JPanel(new GridLayout(allColors.size(), 2));
      center.add(middle);

      for(int i = 0; i < allColors.size(); ++i) {
         middle.add(this.buttons[i]);
         middle.add(this.colors[i]);
      }

      JPanel south = new JPanel();
      this.getContentPane().add(south, "South");
      this.confirm = new JButton("Okay");
      this.confirm.addActionListener(this);
      this.cancel = new JButton("Cancel");
      this.cancel.addActionListener(this);
      south.add(this.confirm);
      south.add(this.cancel);
      this.setDefaultCloseOperation(2);
      this.pack();
      this.setLocationRelativeTo((Frame)SwingUtilities.getAncestorOfClass(Frame.class, Runner.theRunner));
      this.setVisible(true);
   }

   public boolean success() {
      return this.success;
   }

   public void actionPerformed(ActionEvent arg0) {
      if (arg0.getSource() == this.confirm) {
         this.success = true;
         int index = 0;
         JRadioButton[] var6;
         int var5 = (var6 = this.buttons).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            JRadioButton but = var6[var4];
            if (but.isSelected()) {
               break;
            }

            ++index;
         }

         if (index != this.buttons.length) {
            this.graph.colors.add(0, (Color)this.graph.colors.remove(index));
         }
      } else {
         this.success = false;
      }

      this.setVisible(false);
   }
}
