import java.awt.geom.Point2D;
import org.junit.Test;
import org.mockito.Mockito;


public class MainTest {
	
	@Test
	public void shouldThrowAsManyPracticingDartsAsDeclared() {
		// given
		JudgeInterface judgeIfaceMock = Mockito.mock(JudgeInterface.class);
		Main solution = new Main(judgeIfaceMock);
		
		// when
		solution.practice(1000);
		
		// then
		Mockito.verify(judgeIfaceMock, Mockito.times(1)).publishPracticingDartsCount(1000);
		Mockito.verify(judgeIfaceMock, Mockito.times(1000)).throwDart(Mockito.any(Point2D.Double.class));
	}
	
}
