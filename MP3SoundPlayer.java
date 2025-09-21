import javazoom.jl.player.Player;

class MP3SoundPlayer extends SoundPlayer {
   private Player player;

   public MP3SoundPlayer(String path) {
      super(path);
   }

   public void play() {
      if (this.thread != null) {
         if (this.isPaused()) {
            this.unpause();
            return;
         }

         this.stop();
      }

      Runnable r = new Runnable() {
         public void run() {
            try {
               MP3SoundPlayer.this.shouldStop = false;
               MP3SoundPlayer.this.isPaused = false;
               MP3SoundPlayer.this.player = new Player(this.getClass().getResourceAsStream(MP3SoundPlayer.this.path));

               while(!MP3SoundPlayer.this.shouldStop) {
                  boolean success = MP3SoundPlayer.this.player.play(1);
                  if (!success) {
                     break;
                  }

                  while(MP3SoundPlayer.this.isPaused) {
                     Thread.sleep(10L);
                     if (MP3SoundPlayer.this.shouldStop) {
                        break;
                     }
                  }
               }
            } catch (Exception var5) {
               var5.printStackTrace();
            } finally {
               MP3SoundPlayer.this.player.close();
            }

            MP3SoundPlayer.this.thread = null;
         }
      };
      this.thread = new Thread(r);
      this.thread.start();
   }
}
