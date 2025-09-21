package javazoom.jl.player.advanced;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.decoder.JavaLayerException;

public class jlap {
   public static void main(String[] var0) {
      jlap var1 = new jlap();
      if (var0.length != 1) {
         var1.showUsage();
         System.exit(0);
      } else {
         try {
            var1.play(var0[0]);
         } catch (Exception var3) {
            System.err.println(var3.getMessage());
            System.exit(0);
         }
      }

   }

   public void play(String var1) throws JavaLayerException, IOException {
      jlap.InfoListener var2 = new jlap.InfoListener();
      playMp3(new File(var1), var2);
   }

   public void showUsage() {
      System.out.println("Usage: jla <filename>");
      System.out.println("");
      System.out.println(" e.g. : java javazoom.jl.player.advanced.jlap localfile.mp3");
   }

   public static AdvancedPlayer playMp3(File var0, PlaybackListener var1) throws IOException, JavaLayerException {
      return playMp3((File)var0, 0, Integer.MAX_VALUE, var1);
   }

   public static AdvancedPlayer playMp3(File var0, int var1, int var2, PlaybackListener var3) throws IOException, JavaLayerException {
      return playMp3((InputStream)(new BufferedInputStream(new FileInputStream(var0))), var1, var2, var3);
   }

   public static AdvancedPlayer playMp3(InputStream var0, final int var1, final int var2, PlaybackListener var3) throws JavaLayerException {
      final AdvancedPlayer var4 = new AdvancedPlayer(var0);
      var4.setPlayBackListener(var3);
      (new Thread() {
         public void run() {
            try {
               var4.play(var1, var2);
            } catch (Exception var2x) {
               throw new RuntimeException(var2x.getMessage());
            }
         }
      }).start();
      return var4;
   }

   public class InfoListener extends PlaybackListener {
      public void playbackStarted(PlaybackEvent var1) {
         System.out.println("Play started from frame " + var1.getFrame());
      }

      public void playbackFinished(PlaybackEvent var1) {
         System.out.println("Play completed at frame " + var1.getFrame());
         System.exit(0);
      }
   }
}
