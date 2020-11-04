import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class MainTest {
	
	JudgeInterface judgeIfaceMock;
    static final Point2D.Double SOME_POINT = new Point2D.Double(1.0, 2.0);
	
	@Before
	public void init() {
		judgeIfaceMock = Mockito.mock(JudgeInterface.class);	
	}
	
	//@Test
	public void shouldThrowAsManyPracticingDartsAsDeclared() {
		// given
		Main solution = new Main(judgeIfaceMock);
		
		// when
		solution.practice(1000);
		
		// then
		Mockito.verify(judgeIfaceMock, Mockito.times(1)).publishPracticingDartsCount(1000);
		Mockito.verify(judgeIfaceMock, Mockito.times(1000)).throwDart(Mockito.any(Point2D.Double.class));
	}
	
	//@Test
	public void shouldCorrectlyHandle1stSampleCase() {
		// given
		Mockito.when(judgeIfaceMock.throwDart(Mockito.nullable(Point2D.Double.class)))
			.thenReturn(new ThrowingResult(SOME_POINT, 2, 20), new ThrowingResult(SOME_POINT, 3, 19), new ThrowingResult(SOME_POINT, 2, 25));
		
		Main solution = new Main(judgeIfaceMock);
		
		// when
		int scoreAfter = solution.compete(3, Main.DARTS501_INITIAL_SCORE);
		
		// then
		Assert.assertEquals(354, scoreAfter);
	}
	
	//@Test
	public void shouldCorrectlyHandle2ndSampleCase() {
		// given
		Mockito.when(judgeIfaceMock.throwDart(Mockito.nullable(Point2D.Double.class)))
			.thenReturn(new ThrowingResult(SOME_POINT, 3, 20), new ThrowingResult(SOME_POINT, 2, 20));
		
		Main solution = new Main(judgeIfaceMock);
		
		// when
		int scoreAfter = solution.compete(2, 100);
		
		// then
		Assert.assertEquals(Main.DARTS501_INITIAL_SCORE, scoreAfter);		
	}
	
	//@Test
	public void shouldCorrectlyHandle3rdSampleCase() {
		// given
		Mockito.when(judgeIfaceMock.throwDart(Mockito.nullable(Point2D.Double.class)))
			.thenReturn(new ThrowingResult(SOME_POINT, 1, 19));
		
		Main solution = new Main(judgeIfaceMock);
		
		// when
		int scoreAfter = solution.compete(3, 20);
		
		// then
		Assert.assertEquals(20, scoreAfter);
		Mockito.verify(judgeIfaceMock, Mockito.times(1)).throwDart(Mockito.nullable(Point2D.Double.class));
	}

	@Test
	public void shouldGenerateProperBoard() {
		// when
		Set<BoardField> fields = Main.generateBoardFields();
		// then
		Assert.assertEquals(82, fields.size());
	}

	@Test
	public void shouldConvertPolarToCartesianCoordinates() {
		// given
		PolarCoordinates top = new PolarCoordinates(0, 1);
		PolarCoordinates right = new PolarCoordinates(Math.PI / 2.0, 1);
		PolarCoordinates bottom = new PolarCoordinates(Math.PI, 1.0);
		PolarCoordinates left = new PolarCoordinates(Math.PI * 3.0 / 2.0, 1.0);

		// when
		Assert.assertTrue(areEqual(new Point2D.Double(0.0, -1.0), top.toCartesian()));
		Assert.assertTrue(areEqual(new Point2D.Double(0.0, 1.0), bottom.toCartesian()));
		Assert.assertTrue(areEqual(new Point2D.Double(1.0, 0.0), right.toCartesian()));
		Assert.assertTrue(areEqual(new Point2D.Double(-1.0, 0.0), left.toCartesian()));
	}

	static String printCoords(Point2D.Double p) {
		return p.toString().substring("Point2D.Double".length());
	}

	static boolean areEqual(Point2D.Double a, Point2D.Double b) {
		return areEqual(a.x, b.x) && areEqual(a.y, b.y);
	}

	static boolean areEqual(double a, double b) {
		return Math.abs(a - b) < 0.000001;
	}
}
