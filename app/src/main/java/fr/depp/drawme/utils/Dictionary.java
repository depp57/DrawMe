package fr.depp.drawme.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Dictionary {

    private static AssetManager assetManager;
    private static final int NB_WORDS = 100;

    public static void setAssetManager(Context context) {
        assetManager = context.getAssets();
    }

    public static String getRandomWord() {
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(assetManager.open("dictionary.txt"))).lines()) {
            Optional<String> word = lines.skip((int) (Math.random() * NB_WORDS)).findFirst();
            return word.orElse("Erreur lors de la génération d'un mot aléatoire, signalez ce bug svp");
        }
        catch (IOException e) {
            Log.e("Dictionary", "getRandomWord: ", e);
            return "Erreur lors de la génération d'un mot aléatoire, signalez ce bug svp";
        }
    }

    // compare strings, they may have faults
    public static boolean isEquals(String s1, String s2) {
        s1 = s1.trim(); s2 = s2.trim(); // remove all trailing whitespace before and after the word
        final int permittedFaults = s1.length() / 6 + 1; // 1 fault + 1 every 6 char
        if (Math.abs(s1.length() - s2.length()) > permittedFaults) return false;
        s1 = stripAccents(s1); s2 = stripAccents(s2); // remove all accent
        s1 = s1.toLowerCase(); s2 = s2.toLowerCase(); // replace all uppercase by lowercase char

        char[] charS1 = s1.toCharArray(), charS2 = s2.toCharArray();
        int i = 0, j = 0, faults = 0;

        while (faults <= permittedFaults && i < s1.length() && j < s2.length()) {
            if (charS1[i] == charS2[j]) {
                i++; j++;
                continue;
            }

            if (charS1.length < charS2.length) {
                int offset = getOffset(charS2, charS1, j, i, permittedFaults);
                j += offset;
                if (offset > 1) faults+=(offset - 1);
            }
            else if (charS1.length > charS2.length){
                int offset = getOffset(charS1, charS2, i, j, permittedFaults);
                i += offset;
                if (offset > 1) faults+=(offset - 1);
            }

            i++; j++;
            faults++;
        }

        faults += (s1.length()-i); faults+= (s2.length()-j);

        return faults <= permittedFaults;
    }

    private static int getOffset(char[] s1, char[] s2, int charIndexS1, int charIndexS2, final int permittedFaults) {
        for (int offset = 1; offset <= permittedFaults; offset++) {
            if (charIndexS1+offset < s1.length && s1[charIndexS1+offset] == s2[charIndexS2]) {
                return offset;
            }
        }
        return 0;
    }

    private static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
}
