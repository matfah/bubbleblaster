import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;

class ClipSoundPlayer extends SoundPlayer {
   private Clip clip;
   private long pausePosition;

   public ClipSoundPlayer(String path) {
      super(path);

      try {
         AudioInputStream soundStream = AudioSystem.getAudioInputStream(this.getClass().getResource(path));
         AudioFormat streamFormat = soundStream.getFormat();
         Info clipInfo = new Info(Clip.class, streamFormat);
         this.clip = (Clip)AudioSystem.getLine(clipInfo);
         this.clip.open(soundStream);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void play() {
      if (this.thread != null) {
         if (this.isPaused()) {
            this.unpause();
            return;
         }

         this.stop();
      }

      try {
         this.clip.setMicrosecondPosition(0L);
         this.clip.start();
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public boolean isPlaying() {
      return this.clip.isRunning();
   }

   public boolean isPaused() {
      return this.isPaused;
   }

   public void stop() {
      if (this.isPlaying()) {
         this.clip.stop();
         this.isPaused = false;
      }

   }

   public void pause() {
      if (this.isPlaying()) {
         this.isPaused = true;
         this.pausePosition = this.clip.getMicrosecondPosition();
         this.clip.stop();
      }

   }

   public void unpause() {
      if (this.isPaused) {
         this.isPaused = false;
         this.clip.setMicrosecondPosition(this.pausePosition);
         this.clip.start();
      }

   }
}
