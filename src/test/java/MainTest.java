import java.awt.geom.Point2D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class MainTest {
	
	JudgeInterface judgeIfaceMock;

	
	@Before
	public void init() {
		judgeIfaceMock = Mockito.mock(JudgeInterface.class);	
	}
	
	@Test
	public void shouldThrowAsManyPracticingDartsAsDeclared() {
		// given
		Main solution = new Main(judgeIfaceMock);
		
		// when
		solution.practice(1000);
		
		// then
		Mockito.verify(judgeIfaceMock, Mockito.times(1)).publishPracticingDartsCount(1000);
		Mockito.verify(judgeIfaceMock, Mockito.times(1000)).throwDart(Mockito.any(Point2D.Double.class));
	}
	
	@Test
	public void shouldCorrectlyHandle1stSampleCase() {
		// given
		Mockito.when(judgeIfaceMock.throwDart(Mockito.nullable(Point2D.Double.class)))
			.thenReturn(new ThrowingResult(null, 2, 20), new ThrowingResult(null, 3, 19), new ThrowingResult(null, 2, 25));
		
		Main solution = new Main(judgeIfaceMock);
		
		// when
		int scoreAfter = solution.compete(3, Main.DARTS501_INITIAL_SCORE);
		
		// then
		Assert.assertEquals(354, scoreAfter);
	}
	
	@Test
	public void shouldCorrectlyHandle2ndSampleCase() {
		// given
		Mockito.when(judgeIfaceMock.throwDart(Mockito.nullable(Point2D.Double.class)))
			.thenReturn(new ThrowingResult(null, 3, 20), new ThrowingResult(null, 2, 20));
		
		Main solution = new Main(judgeIfaceMock);
		
		// when
		int scoreAfter = solution.compete(2, 100);
		
		// then
		Assert.assertEquals(Main.DARTS501_INITIAL_SCORE, scoreAfter);		
	}
	
	@Test
	public void shouldCorrectlyHandle3rdSampleCase() {
		// given
		Mockito.when(judgeIfaceMock.throwDart(Mockito.nullable(Point2D.Double.class)))
			.thenReturn(new ThrowingResult(null, 1, 19));
		
		Main solution = new Main(judgeIfaceMock);
		
		// when
		int scoreAfter = solution.compete(3, 20);
		
		// then
		Assert.assertEquals(20, scoreAfter);
		Mockito.verify(judgeIfaceMock, Mockito.times(1)).throwDart(Mockito.nullable(Point2D.Double.class));
	}
}
