import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.ImageIcon;

public abstract class AbstractGraph {
   private ArrayList<Relation> relations = new ArrayList();
   private ArrayList<BufferedImage> pathImages = new ArrayList();
   protected double minX;
   protected double maxX;
   protected double minY;
   protected double maxY;
   protected int width;
   protected int height;
   private ArrayList<Point2D> bubbles = new ArrayList();
   private ArrayList<Point2D> brokenBubbles = new ArrayList();
   private TreeMap<Integer, TreeSet<Point2D>> probes = new TreeMap();
   private TreeMap<Integer, Image> probeImages = new TreeMap();
   public static final int LINEAR_PROBE = 0;
   public static final int QUADRATIC_PROBE = 1;
   public static final int CIRCULAR_PROBE = 2;
   public static final int ELIPITICAL_PROBE = 3;
   public static final int HYPERBOLIC_PROBE = 4;
   public final LinkedList<Color> colors = new LinkedList();
   private static final Image bubbleImage = loadImage("bubble.gif");
   private static final Image bustedImage = loadImage("busted.gif");
   private boolean killAdd = false;
   public static int GLOBAL_DIVISIONS = 800;

   public AbstractGraph(int w, int h) {
      this.resetColors();

      for(int i = 0; i <= 4; ++i) {
         this.probes.put(i, new TreeSet());
      }

      this.probeImages.put(0, loadImage("x.png"));
      this.probeImages.put(1, loadImage("o.png"));
      this.probeImages.put(2, loadImage("triangle.png"));
      this.probeImages.put(3, loadImage("square.png"));
      this.probeImages.put(4, loadImage("triangle2.png"));
      this.width = w;
      this.height = h;
      this.minX = -10.0D;
      this.maxX = 10.0D;
      this.minY = -10.0D;
      this.maxY = 10.0D;
   }

   public void setMinX(double m) {
      this.minX = m;
      this.recalcRelations();
   }

   public void setMaxX(double m) {
      this.maxX = m;
      this.recalcRelations();
   }

   public void setMinY(double m) {
      this.minY = m;
      this.recalcRelations();
   }

   public void setMaxY(double m) {
      this.maxY = m;
      this.recalcRelations();
   }

   public void setWindow(double xmin, double xmax, double ymin, double ymax) {
      this.minY = ymin;
      this.maxY = ymax;
      this.minX = xmin;
      this.maxX = xmax;
   }

   public void removeAllRelations() {
      for(int i = this.relations.size() - 1; i >= 0; --i) {
         this.remove((Relation)this.relations.get(i));
      }

   }

   public void recalcRelations() {
      ArrayList<Relation> temp = this.relations;
      this.relations = new ArrayList();
      this.pathImages.clear();
      Iterator var3 = temp.iterator();

      while(var3.hasNext()) {
         Relation f = (Relation)var3.next();
         this.resetColors();
         this.colors.add(f.getColor());
         this.add(f);
      }

   }

   public void resetColors() {
      this.colors.clear();
      this.colors.add(Color.RED);
      this.colors.add(Color.BLUE);
      this.colors.add(Color.GREEN);
      this.colors.add(Color.PINK);
      this.colors.add(Color.YELLOW);
      this.colors.add(Color.MAGENTA);
      this.colors.add(Color.CYAN);
      this.colors.add(Color.LIGHT_GRAY);
      this.colors.add(Color.BLACK);
   }

   public void killAdd() {
      this.killAdd = true;
   }

   public void inequalityPoints(TreeSet<Point2D> points, double xIncr, double yIncr, Relation r) {
      double[] vals = new double[2];
      char[] vars = new char[]{'x', 'y'};

      for(double y = this.minY; y <= this.maxY; y += yIncr) {
         for(double x = this.minX; x <= this.maxX; x += xIncr) {
            vals[0] = x;
            vals[1] = y;
            double diff = r.sideDifference(vars, vals);
            Point2D convert;
            Point2D point;
            if (r.getComparator().equals("<")) {
               if (diff < 0.0D) {
                  point = new Point2D(x, y);
                  convert = this.convertRelationXYtoGraphXY(point, this.width, this.height);
                  points.add(convert);
               }
            } else if (r.getComparator().equals("<=")) {
               if (diff <= 0.0D) {
                  point = new Point2D(x, y);
                  convert = this.convertRelationXYtoGraphXY(point, this.width, this.height);
                  points.add(convert);
               }
            } else if (r.getComparator().equals(">")) {
               if (diff > 0.0D) {
                  point = new Point2D(x, y);
                  convert = this.convertRelationXYtoGraphXY(point, this.width, this.height);
                  points.add(convert);
               }
            } else if (r.getComparator().equals(">=") && diff >= 0.0D) {
               point = new Point2D(x, y);
               convert = this.convertRelationXYtoGraphXY(point, this.width, this.height);
               points.add(convert);
            }
         }
      }

   }

   public void scan(boolean horizFirst, TreeSet<Point2D> points, char[] vars, double[] vals, double xIncr, double yIncr, Point2D[] arrayPoints, Relation f) {
      TreeSet<Point2D> previous = null;
      double y = this.minY;
      double x = this.minX;
      double MAX_SIDE_DIFF = 20.0D;
      int BINARY_SEARCH_DEPTH = true;
      double var19 = 2.147483647E9D;

      while(true) {
         if (this.killAdd) {
            return;
         }

         if (horizFirst) {
            if (y > this.maxY) {
               break;
            }

            x = this.minX;
         } else {
            if (x > this.maxX) {
               break;
            }

            y = this.minY;
         }

         TreeSet<Point2D> current = new TreeSet();
         double lastDiff = 0.0D;

         while(true) {
            if (this.killAdd) {
               return;
            }

            if (horizFirst) {
               if (x > this.maxX) {
                  break;
               }
            } else if (y > this.maxY) {
               break;
            }

            vals[0] = x;
            vals[1] = y;
            double diff = f.sideDifference(vars, vals);
            boolean shouldAdd = false;
            if (horizFirst && Math.abs(diff) < yIncr / 2.0D || !horizFirst && Math.abs(diff) < xIncr / 2.0D) {
               shouldAdd = true;
            } else {
               double firstSpot;
               double secondSpot;
               if (lastDiff < 0.0D && diff > 0.0D || lastDiff > 0.0D && diff < 0.0D) {
                  shouldAdd = true;
                  if (Math.abs(lastDiff) > 20.0D || Math.abs(diff) > 20.0D) {
                     shouldAdd = false;
                     if (horizFirst) {
                        firstSpot = (x + (x - xIncr)) / 2.0D;
                        secondSpot = y;
                     } else {
                        secondSpot = (y + (y - yIncr)) / 2.0D;
                        firstSpot = x;
                     }

                     vals[0] = firstSpot;
                     vals[1] = secondSpot;
                     double newDiff = f.sideDifference(vars, vals);
                     if (newDiff * diff > 0.0D) {
                        if (Math.abs(newDiff) < Math.abs(diff)) {
                           shouldAdd = true;
                        }
                     } else if (Math.abs(newDiff) < Math.abs(lastDiff)) {
                        shouldAdd = true;
                     }
                  }
               } else if (Double.isNaN(lastDiff) && !Double.isNaN(diff) || !Double.isNaN(lastDiff) && Double.isNaN(diff)) {
                  boolean firstIsNaN = Double.isNaN(lastDiff);
                  if (horizFirst) {
                     firstSpot = x - xIncr;
                     secondSpot = x;
                  } else {
                     firstSpot = y - yIncr;
                     secondSpot = y;
                  }

                  for(int i = 0; i < 50; ++i) {
                     double midSpot = (firstSpot + secondSpot) / 2.0D;
                     if (horizFirst) {
                        vals[0] = midSpot;
                        vals[1] = y;
                     } else {
                        vals[0] = x;
                        vals[1] = midSpot;
                     }

                     double midDiff = f.sideDifference(vars, vals);
                     if (Double.isNaN(midDiff)) {
                        if (firstIsNaN) {
                           firstSpot = midSpot;
                        } else {
                           secondSpot = midSpot;
                        }
                     } else if (!firstIsNaN) {
                        if (lastDiff >= 0.0D && midDiff <= 0.0D || lastDiff <= 0.0D && midDiff >= 0.0D) {
                           shouldAdd = true;
                           break;
                        }

                        firstSpot = midSpot;
                     } else {
                        if (diff >= 0.0D && midDiff <= 0.0D || diff <= 0.0D && midDiff >= 0.0D) {
                           shouldAdd = true;
                           break;
                        }

                        secondSpot = midSpot;
                     }
                  }
               }
            }

            if (shouldAdd) {
               Point2D point = new Point2D(x, y);
               current.add(point);
               Point2D convert = this.convertRelationXYtoGraphXY(point, this.width, this.height);
               points.add(convert);
            }

            lastDiff = diff;
            if (horizFirst) {
               x += xIncr;
            } else {
               y += yIncr;
            }
         }

         if (horizFirst) {
            y += yIncr;
         } else {
            x += xIncr;
         }
      }

   }

   public void remove(Relation f) {
      for(int i = 0; i < this.relations.size(); ++i) {
         if (this.relations.get(i) == f) {
            this.relations.remove(i);
            this.pathImages.remove(i);
            return;
         }
      }

   }

   public void addProbe(int type, Point2D p) {
      ((TreeSet)this.probes.get(type)).add(p);
   }

   public void addBubble(Point2D p) {
      this.bubbles.add(p);
   }

   public void breakBubble(Point2D p) {
      this.bubbles.remove(p);
      this.brokenBubbles.add(p);
   }

   public void unbreakBubble(Point2D p) {
      this.brokenBubbles.remove(p);
      this.bubbles.add(p);
   }

   public void clearPoints() {
      this.bubbles.clear();
      this.brokenBubbles.clear();
   }

   public void clearProbes() {
      Iterator var2 = this.probes.keySet().iterator();

      while(var2.hasNext()) {
         int key = (Integer)var2.next();
         ((TreeSet)this.probes.get(key)).clear();
      }

   }

   public void clearProbeLines() {
      for(int i = this.relations.size() - 1; i >= 0; --i) {
         if (((Relation)this.relations.get(i)).isSimpleHorizontalOrVerticalLine()) {
            this.remove((Relation)this.relations.get(i));
         }
      }

   }

   public boolean add(Relation f) {
      this.killAdd = false;
      this.relations.add(f);
      char[] vars = new char[]{'x', 'y'};
      double[] vals = new double[]{0.0D, 0.0D};
      TreeSet<Point2D> points = new TreeSet();
      if (!f.isFunctionalFor('x') && !f.isFunctionalFor('y')) {
         Point2D[] arrayPoints = new Point2D[2];
         double xIncr = (this.maxX - this.minX) / (double)GLOBAL_DIVISIONS;
         double yIncr = (this.maxY - this.minY) / (double)GLOBAL_DIVISIONS;
         if (!f.isInequality() || f.isEqualToInequality()) {
            this.scan(true, points, vars, vals, xIncr, yIncr, arrayPoints, f);
            this.scan(false, points, vars, vals, xIncr, yIncr, arrayPoints, f);
         }

         TreeSet<Point2D> ineqPoints = new TreeSet();
         if (f.isInequality()) {
            xIncr *= 4.0D;
            yIncr *= 4.0D;
            this.inequalityPoints(ineqPoints, xIncr, yIncr, f);
         }

         if (this.killAdd) {
            this.relations.remove(this.relations.size() - 1);
         } else {
            BufferedImage img = new BufferedImage(this.width, this.height, 6);
            Graphics g = img.getGraphics();
            Color c = (Color)this.colors.peek();
            g.setColor(c);
            Iterator var29 = points.iterator();

            Point2D p;
            while(var29.hasNext()) {
               p = (Point2D)var29.next();
               g.fillOval(p.getIntX() - 2, p.getIntY() - 2, 4, 4);
            }

            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 40);
            g.setColor(c);
            var29 = ineqPoints.iterator();

            while(var29.hasNext()) {
               p = (Point2D)var29.next();
               g.fillOval(p.getIntX() - 2, p.getIntY() - 2, 4, 4);
            }

            this.pathImages.add(img);
         }
      } else {
         BufferedImage img = new BufferedImage(this.width, this.height, 6);
         Graphics g = img.getGraphics();
         GeneralPath path = new GeneralPath();
         Point2D previous = null;
         double delta = Double.NaN;
         double ASYMPTOTE_INDICATTOR_SLOPE = this.maxY - this.minY;
         double xIncr = (this.maxX - this.minX) / 800.0D;
         double yIncr = (this.maxY - this.minY) / 800.0D;
         double y;
         double x;
         Point2D point;
         double newDeltaX;
         if (f.isFunctionalFor('y')) {
            for(y = this.minX; y <= this.maxX; y += xIncr) {
               x = f.evaluateFunction('y', 'x', y);
               if (!Double.isNaN(x) && !Double.isInfinite(x)) {
                  point = new Point2D(y, x);
                  point = this.convertRelationXYtoGraphXY(point, this.width, this.height);
                  if (point.getX() <= -2.147483647E9D) {
                     point.setX((double)(-this.width * this.width));
                  } else if (point.getX() >= 2.147483647E9D) {
                     point.setX((double)(this.width * this.width));
                  }

                  if (point.getY() <= -2.147483647E9D) {
                     point.setY((double)(-this.height * this.height));
                  } else if (point.getY() >= 2.147483647E9D) {
                     point.setY((double)(this.height * this.height));
                  }

                  newDeltaX = Double.NaN;
                  if (previous != null) {
                     newDeltaX = point.getY() - previous.getY();
                     if (!Double.isNaN(delta) && !(newDeltaX * delta > 0.0D)) {
                        newDeltaX = Double.NaN;
                        if (Math.abs(delta) > ASYMPTOTE_INDICATTOR_SLOPE) {
                           if (delta < 0.0D) {
                              path.moveTo(previous.getX(), previous.getY());
                              path.lineTo(previous.getX(), (double)(this.height - this.height * this.height));
                           } else {
                              path.moveTo(previous.getX(), previous.getY());
                              path.lineTo(previous.getX(), (double)(this.height * this.height - this.height));
                           }
                        }
                     } else {
                        path.moveTo(previous.getX(), previous.getY());
                        path.lineTo(point.getX(), point.getY());
                     }
                  }

                  previous = point;
                  delta = newDeltaX;
               }
            }
         } else {
            for(y = this.minY; y <= this.maxY; y += yIncr) {
               x = f.evaluateFunction('x', 'y', y);
               if (!Double.isNaN(x) && !Double.isInfinite(x)) {
                  point = new Point2D(x, y);
                  point = this.convertRelationXYtoGraphXY(point, this.width, this.height);
                  if (point.getX() <= -2.147483647E9D) {
                     point.setX((double)(-this.width * this.width));
                  } else if (point.getX() >= 2.147483647E9D) {
                     point.setX((double)(this.width * this.width));
                  }

                  if (point.getY() <= -2.147483647E9D) {
                     point.setY((double)(-this.height * this.height));
                  } else if (point.getY() >= 2.147483647E9D) {
                     point.setY((double)(this.height * this.height));
                  }

                  newDeltaX = Double.NaN;
                  if (previous != null) {
                     newDeltaX = point.getX() - previous.getX();
                     if (!Double.isNaN(delta) && !(newDeltaX * delta > 0.0D)) {
                        newDeltaX = Double.NaN;
                        if (Math.abs(delta) > ASYMPTOTE_INDICATTOR_SLOPE) {
                           if (delta < 0.0D) {
                              path.moveTo(previous.getX(), previous.getY());
                              path.lineTo((double)(this.width - this.width * this.width), previous.getY());
                           } else {
                              path.moveTo(previous.getX(), previous.getY());
                              path.lineTo((double)(this.width * this.width - this.width), previous.getY());
                           }
                        }
                     } else {
                        path.moveTo(previous.getX(), previous.getY());
                        path.lineTo(point.getX(), point.getY());
                     }
                  }

                  previous = point;
                  delta = newDeltaX;
               }
            }
         }

         g.setColor((Color)this.colors.peek());
         ((Graphics2D)g).setStroke(new BasicStroke(3.0F));
         ((Graphics2D)g).draw(path);
         if (this.killAdd) {
            this.relations.remove(this.relations.size() - 1);
         } else {
            this.pathImages.add(img);
         }
      }

      if (this.killAdd) {
         this.killAdd = false;
         return false;
      } else {
         return true;
      }
   }

   public Point2D convertRelationXYtoGraphXY(Point2D p, int width, int height) {
      double xScale = (double)width / (this.maxX - this.minX);
      double yScale = (double)height / (this.maxY - this.minY);
      double graphX = xScale * (p.getX() - this.minX);
      double graphY = (double)height - yScale * (p.getY() - this.minY);
      return new Point2D(graphX, graphY);
   }

   public Point2D convertGraphXYToRelationXY(Point2D p, int width, int height) {
      double relX = (this.maxX - this.minX) * p.getX() / (double)width + this.minX;
      double relY = (this.maxY - this.minY) * (((double)height - p.getY()) / (double)height) + this.minY;
      return new Point2D(relX, relY);
   }

   public static Image loadImage(String path) {
      try {
         URL url = AbstractGraph.class.getResource(path);
         return (new ImageIcon(url)).getImage();
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public abstract void drawAxes(Graphics var1);

   public void draw(Graphics g) {
      this.drawAxes(g);
      this.drawBubbles(g);
      this.drawBustedBubbles(g);
      this.drawProbes(g);
      this.drawPaths(g);
   }

   public void drawBubbles(Graphics g) {
      Iterator var3 = this.bubbles.iterator();

      while(var3.hasNext()) {
         Point2D b = (Point2D)var3.next();
         Point2D conv = this.convertRelationXYtoGraphXY(b, this.width, this.height);
         g.drawImage(bubbleImage, conv.getIntX() - 8, conv.getIntY() - 8, (ImageObserver)null);
      }

   }

   public void drawBustedBubbles(Graphics g) {
      Iterator var3 = this.brokenBubbles.iterator();

      while(var3.hasNext()) {
         Point2D b = (Point2D)var3.next();
         Point2D conv = this.convertRelationXYtoGraphXY(b, this.width, this.height);
         g.drawImage(bustedImage, conv.getIntX() - 8, conv.getIntY() - 8, (ImageObserver)null);
      }

   }

   public void drawPaths(Graphics g) {
      for(int i = 0; i < this.pathImages.size(); ++i) {
         BufferedImage img = (BufferedImage)this.pathImages.get(i);
         g.drawImage(img, 0, 0, (ImageObserver)null);
      }

   }

   public void drawProbes(Graphics g) {
      Iterator var3 = this.probes.keySet().iterator();

      while(var3.hasNext()) {
         int key = (Integer)var3.next();
         Iterator var5 = ((TreeSet)this.probes.get(key)).iterator();

         while(var5.hasNext()) {
            Point2D p = (Point2D)var5.next();
            Point2D conv = this.convertRelationXYtoGraphXY(p, this.width, this.height);
            g.drawImage((Image)this.probeImages.get(key), conv.getIntX() - 8, conv.getIntY() - 8, (ImageObserver)null);
         }
      }

   }
}
