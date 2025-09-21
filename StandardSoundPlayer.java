import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;

class StandardSoundPlayer extends SoundPlayer {
   private SourceDataLine line;
   private AudioFormat format;
   private AudioInputStream ain;

   public StandardSoundPlayer(String path) {
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

      this.ain = null;
      this.shouldStop = false;

      try {
         this.ain = AudioSystem.getAudioInputStream(this.getClass().getResource(this.path));
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      this.format = this.ain.getFormat();
      Info info = new Info(SourceDataLine.class, this.format);
      if (!AudioSystem.isLineSupported(info)) {
         AudioFormat pcm = new AudioFormat(this.format.getSampleRate(), 16, this.format.getChannels(), true, false);
         this.ain = AudioSystem.getAudioInputStream(pcm, this.ain);
         this.format = this.ain.getFormat();
         info = new Info(SourceDataLine.class, this.format);
      }

      try {
         this.line = (SourceDataLine)AudioSystem.getLine(info);
         this.line.open(this.format);
         this.line.start();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      Runnable r = new Runnable() {
         public void run() {
            try {
               byte[] buffer = new byte[StandardSoundPlayer.this.line.getBufferSize()];
               int numbytes = 0;
               StandardSoundPlayer.this.shouldStop = false;
               StandardSoundPlayer.this.isPaused = false;

               while(!StandardSoundPlayer.this.shouldStop) {
                  int bytesread = StandardSoundPlayer.this.ain.read(buffer, numbytes, buffer.length - numbytes);
                  if (bytesread == -1) {
                     break;
                  }

                  for(int offset = 0; offset < bytesread; offset += StandardSoundPlayer.this.line.write(buffer, offset, bytesread - offset)) {
                     while(StandardSoundPlayer.this.isPaused) {
                        Thread.sleep(10L);
                        if (StandardSoundPlayer.this.shouldStop) {
                           break;
                        }
                     }
                  }
               }

               StandardSoundPlayer.this.line.drain();
               StandardSoundPlayer.this.line.stop();
            } catch (Exception var13) {
               var13.printStackTrace();
            } finally {
               if (StandardSoundPlayer.this.line != null) {
                  StandardSoundPlayer.this.line.close();
               }

               try {
                  if (StandardSoundPlayer.this.ain != null) {
                     StandardSoundPlayer.this.ain.close();
                  }
               } catch (Exception var12) {
                  var12.printStackTrace();
               }

            }

            StandardSoundPlayer.this.thread = null;
         }
      };
      this.thread = new Thread(r);
      this.thread.start();
   }

   public void pause() {
      super.pause();
      if (this.thread != null) {
         synchronized(this.line) {
            this.line.stop();
         }
      }

   }

   public void unpause() {
      super.unpause();
      if (this.thread != null) {
         synchronized(this.line) {
            this.line.start();
         }
      }

   }
}
