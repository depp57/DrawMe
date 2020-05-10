package fr.depp.drawme;

import org.junit.Test;

import java.text.Normalizer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void isWordMatchingSecretWordTest1() {
        String secretWord = "Chaussette";
        String word1 = "Chaussette";
        String word2 = "chaussette";
        String word3 = "chaussete";
        String word4 = "CHAUSETTE";
        String word5 = "haussette";
        String word6 = "chaaussette";
        String word7 = "cháussétté";
        String word8 = "     Chaussette  ";
        String word9 = "aussette";
        String word10 = "chssette";
        String word11 = "causette";
        String badWord1 = "Chaussaittte";
        String badWord2 = "chaussiiie";
        String badWord3 = "";

        assertTrue(isEquals(secretWord, word1));
        assertTrue(isEquals(secretWord, word2));
        assertTrue(isEquals(secretWord, word3));
        assertTrue(isEquals(secretWord, word4));
        assertTrue(isEquals(secretWord, word5));
        assertTrue(isEquals(secretWord, word6));
        assertTrue(isEquals(secretWord, word7));
        assertTrue(isEquals(secretWord, word8));
        assertTrue(isEquals(secretWord, word9));
        assertTrue(isEquals(secretWord, word10));
        assertTrue(isEquals(secretWord, word11));
        assertFalse(isEquals(secretWord, badWord1));
        assertFalse(isEquals(secretWord, badWord2));
        assertFalse(isEquals(secretWord, badWord3));
    }

    @Test
    public void isWordMatchingSecretWordTest2() {
        assertTrue(isEquals("Souris", "Sour"));
        assertFalse(isEquals("Souris", "Siur"));
    }

    private boolean isEquals(String s1, String s2) {
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

    private int getOffset(char[] s1, char[] s2, int i, int j, final int permittedFaults) {
        for (int offset = 1; offset <= permittedFaults; offset++) {
            if (i+offset < s1.length && s1[i+offset] == s2[j]) {
                return offset;
            }
        }
        return 0;
    }

    private String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    @Test
    public void testRxJava() {
        Disposable d = testObservable().subscribe(System.out::println);
        d.dispose();

        d = testCompletable().subscribe(System.out::println);
        d.dispose();
    }

    private @NonNull Maybe<Integer> testObservable() {
        return Observable.just(1,2,3).map(integer -> integer*2).reduce(Integer::sum);
    }

    private Completable testCompletable() {
        return Completable.complete();
    }
}