public class Sound {
   private SoundPlayer player;

   public Sound(String path) {
      int index = path.lastIndexOf(".");
      String ext = path.substring(index + 1);
      if (index == -1) {
         try {
            throw new Exception("No valid extension for music loaded from  " + path);
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      } else {
         if (!ext.equalsIgnoreCase("wav") && !ext.equalsIgnoreCase("aiff") && !ext.equalsIgnoreCase("au")) {
            if (ext.equalsIgnoreCase("mp3")) {
               this.player = new MP3SoundPlayer(path);
            } else {
               try {
                  throw new Exception("Invalid music type loaded from: " + path);
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            }
         } else {
            long byteLength = (long)this.getClass().getResource(path).getFile().length();
            if (byteLength >= 102400L) {
               this.player = new StandardSoundPlayer(path);
            } else {
               this.player = new ClipSoundPlayer(path);
            }
         }

      }
   }

   public void play() {
      this.player.play();
   }

   public boolean isPlaying() {
      return this.player.isPlaying();
   }

   public boolean isPaused() {
      return this.player.isPaused();
   }

   public void stop() {
      this.player.stop();
   }

   public void pause() {
      this.player.pause();
   }

   public void unpause() {
      this.player.unpause();
   }
}
