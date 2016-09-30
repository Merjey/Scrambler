package test;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;
import java.lang.reflect.*;

import org.junit.runner.JUnitCore;

public class ScramblerTest {
	private static int maxL = 200;
	private static int dataN = 300;
	private static int rndKey = 1024;
	private static int incl = 36;
	private static ArrayList<Integer> data = new ArrayList<>();
	@SuppressWarnings("rawtypes")
	private static Class core;
	private static Field f;
	
	@BeforeClass
    public static void loadCore() {
        try {
			core = Class.forName("scrambler.core.Core");
		} catch (ClassNotFoundException e) {
			fail("Class \"scrambler.core.Core\" not found.");
		}
        try {
			f = core.getDeclaredField("maxpice");
			f.setAccessible(true);
	        f.set(int.class, maxL);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail("Class \"scrambler.core.Core\" not found.");
		}
    }
	
	@Before
    public void prepareData() {
        data.clear();
        Random rnd = new Random();
        for (int i = 0; i < dataN; i++) {data.add(rnd.nextInt(256));}
    }
	
	@SuppressWarnings("unchecked")
	@Test
    public void shuffleTest() {
        try {
			Method shuffle = core.getDeclaredMethod("doShuffle", new Class[] {ArrayList.class, int.class});
			shuffle.setAccessible(true);
			Method unshuffle = core.getDeclaredMethod("undoShuffle", new Class[] {ArrayList.class, int.class});
			unshuffle.setAccessible(true);
			ArrayList<Integer> a = new ArrayList<>();
			a.addAll(data);
			data = (ArrayList<Integer>) shuffle.invoke(null, data, rndKey);
			ArrayList<Integer> b = new ArrayList<>();
			b.addAll(data);
			data = (ArrayList<Integer>) unshuffle.invoke(null, data, rndKey);
			assertTrue(data.equals(a));
			assertFalse(data.equals(b));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Can't take access to required method.");
		}
        
    }
 
 
    public static void main(String[] args) {
        JUnitCore juc = new JUnitCore();
        juc.run(ScramblerTest.class);
    }

}
