package test;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.reflect.*;

import org.junit.runner.JUnitCore;

public class ScramblerTest {
	private static int maxL = 5000;
	private static int dataN = 30000000;
	private static int rndKey = 1024;
	private static int incl = 36;
	private static ArrayList<Integer> data = new ArrayList<>();
	private static Class<?> core;
	private static Field f;
	
	@BeforeClass
    public static void loadCore() {
        try {
			core = Class.forName("scrambler.core.Core");
		} catch (ClassNotFoundException e) {
			fail("Class \"scrambler.core.Core\" not found.");
		}
        try {
			f = core.getDeclaredField("maxpiece");
			f.setAccessible(true);
	        f.set(int.class, maxL);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail("Can't take access to required field.");
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
			Method shuffle = core.getDeclaredMethod("doShuffle", new Class[] {List.class, int.class});
			shuffle.setAccessible(true);
			Method unshuffle = core.getDeclaredMethod("undoShuffle", new Class[] {List.class, int.class});
			unshuffle.setAccessible(true);
			List<Integer> a = new ArrayList<>();
			a.addAll(data);
			data = (ArrayList<Integer>) shuffle.invoke(null, data, rndKey);
			List<Integer> b = new ArrayList<>();
			b.addAll(data);
			data = (ArrayList<Integer>) unshuffle.invoke(null, data, rndKey);
			assertTrue(data.equals(a));
			assertFalse(data.equals(b));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Can't take access to required method.");
		} 
    }
 
	@SuppressWarnings("unchecked")
	@Test
    public void stuffingTest() {
        try {
			Method stuffing = core.getDeclaredMethod("doStuffing", new Class[] {List.class, int.class, int.class});
			stuffing.setAccessible(true);
			Method unstuffing = core.getDeclaredMethod("undoStuffing", new Class[] {List.class, int.class, int.class});
			unstuffing.setAccessible(true);
			List<Integer> a = new ArrayList<>();
			a.addAll(data);
			data = (ArrayList<Integer>) stuffing.invoke(null, data, rndKey, incl);
			List<Integer> b = new ArrayList<>();
			b.addAll(data);
			data = (ArrayList<Integer>) unstuffing.invoke(null, data, rndKey, incl);
			assertTrue(data.equals(a));
			assertTrue(b.size() == a.size() + incl);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Can't take access to required method.");
		}
    }
	
	@SuppressWarnings("unchecked")
	@Test
    public void zipTest() {
        try {
			Method zip = core.getDeclaredMethod("putToZip", new Class[] {List.class});
			zip.setAccessible(true);
			Method unzip = core.getDeclaredMethod("getFromZip", new Class[] {List.class});
			unzip.setAccessible(true);
			List<Integer> a = new ArrayList<>();
			a.addAll(data);
			data = (ArrayList<Integer>) zip.invoke(null, data);
			List<Integer> b = new ArrayList<>();
			b.addAll(data);
			data = (ArrayList<Integer>) unzip.invoke(null, data);
			assertTrue(data.equals(a));
			assertFalse(data.equals(b));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail("Can't take access to required method.");
		}
    }

	/**
	 * This method is designed to run a unit test as a standard Java application. It is not recommended.
	 */
    public static void main(String[] args) {
        JUnitCore juc = new JUnitCore();
        juc.addListener(new TestListener());
        juc.run(ScramblerTest.class);
    }
}
