package Tests;

import Code.Entry;
import Code.SortedTreeMap;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomSortedTreeTest {



    @Test
    public void test_find_by_key(){
        SortedTreeMap<Integer, String> tm = new SortedTreeMap<>(Comparator.naturalOrder());
        tm.add(1, "hello");
        tm.add(2, "yay");

        assertEquals("hello", tm.getValue(1));
        assertEquals("yay", tm.getValue(2));
        assertThrows(NoSuchElementException.class, ()->tm.getValue(3));
    }

    @Test
    public void test_get_value_or_higher(){
        SortedTreeMap<Integer, String> tm = new SortedTreeMap<>(Comparator.naturalOrder());
        tm.add(2, "yup");
        tm.add(1, "Ney");
        tm.add(6, "balls");
        tm.add(3, "keks");
        tm.add(8, "yuppers");
        /*
                    2
                   / \
                  1   6
                     / \
                    3   8
         */
        Entry<Integer, String> expected = tm.getEntry(6);
        System.out.println(expected.key);
        System.out.println(tm.higherOrEqualEntry(6).key);
        assertEquals(expected, tm.higherOrEqualEntry(6));
    }

    @Test
    public void test_contains_value_but_not_other(){
        SortedTreeMap<Integer, String> tm = new SortedTreeMap<>(Comparator.naturalOrder());
        tm.add(1, "Hello");
        tm.add(2, "Nogo");
        tm.add(5, "rkku");
        tm.add(-2, "kek");
        tm.add(6, "pwrevv");
        tm.add(-11, "iifv");
        tm.add(2, "cxlenru");

        assertEquals(true, tm.containsValue("Hello"));
        assertEquals(true, tm.containsValue("cxlenru"));
        assertEquals(true, tm.containsValue("rkku"));
        assertEquals(true, tm.containsValue("kek"));
        assertEquals(true, tm.containsValue("pwrevv"));
        assertEquals(true, tm.containsValue("iifv"));
        assertEquals(false, tm.containsValue("No Value"));
        assertEquals(false, tm.containsValue("Nogo"));
        assertEquals(false, tm.containsValue("ojeztnuzucp"));

    }


}
