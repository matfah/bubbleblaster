abstract class SoundPlayer {
   protected Thread thread;
   protected String path;
   protected boolean shouldStop;
   protected boolean isPaused;

   public SoundPlayer(String path) {
      this.path = path;
   }

   public abstract void play();

   public boolean isPlaying() {
      return this.thread != null;
   }

   public boolean isPaused() {
      return this.isPaused;
   }

   public void stop() {
      if (this.thread != null) {
         this.shouldStop = true;
         this.isPaused = false;
      }

   }

   public void pause() {
      if (this.thread != null) {
         this.isPaused = true;
      }

   }

   public void unpause() {
      if (this.thread != null) {
         this.isPaused = false;
      }

   }
}
