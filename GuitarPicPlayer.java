/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */

import synthesizer.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GuitarPicPlayer {
    private static final double CONCERT_A = 440.0;
    private static final int noteTime = 250;
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static GuitarString[] sounds = new GuitarString[keyboard.length()];

    private static int[] runPython(String filename) throws Exception {
        File pic = new File(filename);
        ProcessBuilder pb = new ProcessBuilder("python3", "PicToArray.py", filename);
        pb.directory(new File("./"));
        Process p = pb.start();
        Thread.sleep(2000);
        ArrayList<Integer> ans = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("./temp.txt"))) {
            String line = br.readLine();
            while (line != null) {
                ans.add(Integer.parseInt(line));
                line = br.readLine();
            }
        }
        int[] temp = new int[ans.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = ans.get(i);
        }
        return temp;
    }

    public static void main(String[] args) throws Exception {
        int[] arr = runPython(args[0]);
        for (int i = 0; i < keyboard.length(); i++) {
            sounds[i] = new GuitarString(CONCERT_A * Math.pow(2.0, ((double) i - 24.0) / 12.0));
        }
        long time = System.currentTimeMillis();
        long diff = 0;
        int count = 0;
        while (true) {
            diff = System.currentTimeMillis() - time;
            if (diff > noteTime) {
                int tmp = arr[count];
                sounds[tmp].pluck();
                count++;
                if (count >= arr.length) {
                    new File("temp.txt").delete();
                    return;
                }
                time = System.currentTimeMillis();
            }

        /* compute the superposition of samples */
            double sample = 0;
            for (int i = 0; i < keyboard.length(); i++) {
                sample += sounds[i].sample();
            }

        /* play the sample on standard audio */
            StdAudio.play(sample);

        /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < keyboard.length(); i++) {
                sounds[i].tic();
            }

        }
    }
}
