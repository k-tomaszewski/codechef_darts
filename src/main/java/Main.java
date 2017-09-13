import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.Scanner;


public class Main {
	
	private final JudgeInterface judgeInterface;

	
	public Main(JudgeInterface judgeInterface) {
		this.judgeInterface = judgeInterface;
	}
	
	public void practice(int N) {
		if (N > 100000) {
			throw new IllegalArgumentException("Too many rounds of practicing requested: " + N);
		}
		judgeInterface.publishPracticingDartsCount(N);
		for (int i = 0; i < N; ++i) {
			// FIXME
			judgeInterface.throwDart(new Point2D.Double());
		}
	}
	
	public void compete() {
		// TODO
		int darts = 99999;
	}

	public static void main(String[] args) {
		Main solution = new Main(new StdJudgeInterface());
		solution.practice(1);
		solution.compete();
	}
}


interface JudgeInterface {
	
	void publishPracticingDartsCount(int n);
	ThrowingResult throwDart(Point2D.Double target);
}


class StdJudgeInterface implements JudgeInterface {
	
	private final Scanner scanner = new Scanner(System.in);

	public void publishPracticingDartsCount(int n) {
		System.out.println(n);
	}

	public ThrowingResult throwDart(Point2D.Double target) {
		System.out.format(Locale.ROOT, "%f %f\n", target.x, target.y).flush();
		return new ThrowingResult(new Point2D.Double(scanner.nextDouble(), scanner.nextDouble()), scanner.nextInt(), scanner.nextInt());
	}
}

class ThrowingResult {

	final Point2D.Double point;
	final int multiplier;
	final int points;

	public ThrowingResult(Point2D.Double point, int multiplier, int points) {
		this.point = point;
		this.multiplier = multiplier;
		this.points = points;
	}

	public Point2D.Double getPoint() {
		return point;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public int getPoints() {
		return points;
	}
}


class PolarCoordinates {
	
	final double angle;
	final double radius;

	public PolarCoordinates(double angle, double radius) {
		this.angle = angle;
		this.radius = radius;
	}	
}
