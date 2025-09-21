public class Point2D implements Comparable<Point2D> {
   private double x;
   private double y;

   public Point2D(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public String toString() {
      return "(" + this.x + ", " + this.y + ")";
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public int getIntX() {
      return (int)Math.round(this.x);
   }

   public int getIntY() {
      return (int)Math.round(this.y);
   }

   public double slope(Point2D p) {
      return (this.y - p.y) / (this.x - p.x);
   }

   public Point2D midpoint(Point2D p) {
      return new Point2D((this.x + p.x) / 2.0D, (this.y + p.y) / 2.0D);
   }

   public double distanceSquared(Point2D p) {
      double xDis = this.x - p.x;
      double yDis = this.y - p.y;
      return xDis * xDis + yDis * yDis;
   }

   public int compareTo(Point2D o) {
      if (Math.abs(this.x - o.x) < 1.0E-6D) {
         if (Math.abs(this.y - o.y) < 1.0E-6D) {
            return 0;
         } else {
            return this.y > o.y ? 1 : -1;
         }
      } else {
         return this.x > o.x ? 1 : -1;
      }
   }

   public void setY(double yVal) {
      this.y = yVal;
   }

   public void setX(double xVal) {
      this.x = xVal;
   }
}
