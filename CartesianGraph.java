import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class CartesianGraph extends AbstractGraph {
   protected double xTics = 1.0D;
   protected double yTics = 1.0D;

   public CartesianGraph(int width, int height) {
      super(width, height);
   }

   public void setXTics(double x) {
      this.xTics = x;
   }

   public void setYTics(double y) {
      this.yTics = y;
   }

   public void setWindow(double xmin, double xmax, double ymin, double ymax, double xtic, double ytic) {
      super.setWindow(xmin, xmax, ymin, ymax);
      this.xTics = xtic;
      this.yTics = ytic;
   }

   public void drawDots(Graphics g) {
      int dotRadius = 2;
      int dotDiameter = dotRadius * 2;

      Point2D temp;
      double xStart;
      double yStart;
      for(xStart = this.xTics; xStart <= this.maxX; xStart += this.xTics) {
         for(yStart = this.yTics; yStart <= this.maxY; yStart += this.yTics) {
            temp = this.convertRelationXYtoGraphXY(new Point2D(xStart, yStart), this.width, this.height);
            g.fillOval(temp.getIntX() - dotRadius, temp.getIntY() - dotRadius, dotDiameter, dotDiameter);
         }

         for(yStart = -this.yTics; yStart >= this.minY; yStart -= this.yTics) {
            temp = this.convertRelationXYtoGraphXY(new Point2D(xStart, yStart), this.width, this.height);
            g.fillOval(temp.getIntX() - dotRadius, temp.getIntY() - dotRadius, dotDiameter, dotDiameter);
         }
      }

      for(xStart = -this.xTics; xStart >= this.minX; xStart -= this.xTics) {
         for(yStart = this.yTics; yStart <= this.maxY; yStart += this.yTics) {
            temp = this.convertRelationXYtoGraphXY(new Point2D(xStart, yStart), this.width, this.height);
            g.fillOval(temp.getIntX() - dotRadius, temp.getIntY() - dotRadius, dotDiameter, dotDiameter);
         }

         for(yStart = -this.yTics; yStart >= this.minY; yStart -= this.yTics) {
            temp = this.convertRelationXYtoGraphXY(new Point2D(xStart, yStart), this.width, this.height);
            g.fillOval(temp.getIntX() - dotRadius, temp.getIntY() - dotRadius, dotDiameter, dotDiameter);
         }
      }

   }

   public void draw(Graphics g) {
      super.draw(g);
      this.drawDots(g);
   }

   public void drawAxes(Graphics g) {
      g.setColor(Color.black);
      int lineLength = 2;
      int lengthenEvery = 5;
      ((Graphics2D)g).setStroke(new BasicStroke(3.0F));
      Point2D temp1;
      Point2D temp2;
      double xVal;
      int ticCount;
      if (this.minX <= 0.0D && 0.0D <= this.maxX) {
         temp1 = this.convertRelationXYtoGraphXY(new Point2D(0.0D, this.minY), this.width, this.height);
         temp2 = this.convertRelationXYtoGraphXY(new Point2D(0.0D, this.maxY), this.width, this.height);
         g.drawLine(temp1.getIntX(), temp1.getIntY(), temp2.getIntX(), temp2.getIntY());
         if (this.minY > 0.0D) {
            xVal = this.minY / this.yTics - this.minY % this.yTics;

            for(ticCount = 0; xVal <= this.maxY; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(0.0D, xVal), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX() - lineLength, temp1.getIntY(), temp1.getIntX() + lineLength, temp1.getIntY());
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal += this.yTics;
            }
         } else if (this.maxY < 0.0D) {
            xVal = this.maxY / this.yTics - this.maxY % this.yTics;

            for(ticCount = 0; xVal >= this.minY; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(0.0D, xVal), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX() - lineLength, temp1.getIntY(), temp1.getIntX() + lineLength, temp1.getIntY());
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal -= this.yTics;
            }
         } else {
            xVal = this.yTics;

            for(ticCount = 0; xVal <= this.maxY; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(0.0D, xVal), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX() - lineLength, temp1.getIntY(), temp1.getIntX() + lineLength, temp1.getIntY());
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal += this.yTics;
            }

            xVal = -this.yTics;

            for(ticCount = 0; xVal >= this.minY; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(0.0D, xVal), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX() - lineLength, temp1.getIntY(), temp1.getIntX() + lineLength, temp1.getIntY());
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal -= this.yTics;
            }
         }
      }

      if (this.minY <= 0.0D && 0.0D <= this.maxY) {
         temp1 = this.convertRelationXYtoGraphXY(new Point2D(this.minX, 0.0D), this.width, this.height);
         temp2 = this.convertRelationXYtoGraphXY(new Point2D(this.maxX, 0.0D), this.width, this.height);
         g.drawLine(temp1.getIntX(), temp1.getIntY(), temp2.getIntX(), temp2.getIntY());
         if (this.minX > 0.0D) {
            xVal = this.minX / this.xTics - this.minX % this.xTics;

            for(ticCount = 0; xVal <= this.maxX; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(xVal, 0.0D), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX(), temp1.getIntY() - lineLength, temp1.getIntX(), temp1.getIntY() + lineLength);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal += this.xTics;
            }
         } else if (this.maxX < 0.0D) {
            xVal = this.maxX / this.xTics - this.maxX % this.xTics;

            for(ticCount = 0; xVal >= this.minX; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(xVal, 0.0D), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX(), temp1.getIntY() - lineLength, temp1.getIntX(), temp1.getIntY() + lineLength);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal -= this.xTics;
            }
         } else {
            xVal = this.xTics;

            for(ticCount = 0; xVal <= this.maxX; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(xVal, 0.0D), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX(), temp1.getIntY() - lineLength, temp1.getIntX(), temp1.getIntY() + lineLength);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal += this.xTics;
            }

            xVal = -this.xTics;

            for(ticCount = 0; xVal >= this.minX; ++ticCount) {
               temp1 = this.convertRelationXYtoGraphXY(new Point2D(xVal, 0.0D), this.width, this.height);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength *= 4;
               }

               g.drawLine(temp1.getIntX(), temp1.getIntY() - lineLength, temp1.getIntX(), temp1.getIntY() + lineLength);
               if (ticCount % lengthenEvery == lengthenEvery - 1) {
                  lineLength /= 4;
               }

               xVal -= this.xTics;
            }
         }
      }

   }
}
