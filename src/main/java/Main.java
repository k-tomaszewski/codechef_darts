import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

// X-axis - normal
// Y-axis - inverted, top of the board is (0, -1)
public class Main {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	static int DARTS501_INITIAL_SCORE = 501;
	static int DARTS_PER_TURN = 3;
	static double BOARD_RADIUS_MM = 170.0;
	static double MAX_ALLOWED_RADIUS = 1.2;
	
	private final JudgeInterface judgeInterface;
	private final Set<BoardField> boardFields;

	
	public Main(JudgeInterface judgeInterface) {
		this.judgeInterface = judgeInterface;
		this.boardFields = Collections.unmodifiableSet(generateBoardFields());
	}
	
	public void practice(int N) {
		if (N > 100000) {
			throw new IllegalArgumentException("Too many rounds of practicing requested: " + N);
		}
		final int dartsPerField = N / (2 * boardFields.size());
		judgeInterface.publishPracticingDartsCount(dartsPerField * boardFields.size() * 2);

		for (BoardField field : boardFields) {
			LOG.info(">>> Calculating average targeting delta for {}...", field);
			final var fieldCenter = field.getCenterAsCartesian();
			for (int i = 0; i < dartsPerField; ++i) {
				ThrowingResult result = judgeInterface.throwDart(fieldCenter);
				field.addTargetingDelta(new Point2D.Double(fieldCenter.x - result.point.x, fieldCenter.y - result.point.y));
			}

			LOG.info(">>> Testing accuracy for {} with correction {}...", field, field.getAvgTargetingDelta());
			double hits = 0;
			final var correctedTarget = field.getCorrectedTarget();
			for (int i = 0; i < dartsPerField; ++i) {
				ThrowingResult result = judgeInterface.throwDart(correctedTarget);
				if (result.score == field.getScore() && result.getMultiplier() == field.getMultiplier()) {
					++hits;
				}
			}
			field.setAccuracy(hits / dartsPerField);
			LOG.info(">>>> Accuracy: {}", field.getAccuracy());
		}
	}
	
	public int compete(int darts, int score) {
		int turnDarts = DARTS_PER_TURN;
		while (darts > 0) {			
			final Point2D.Double target = selectTarget(score);
			final ThrowingResult result = judgeInterface.throwDart(target);
			--darts;
			--turnDarts;
			final int dartScore = (result.score > 0) ? result.multiplier * result.score : 0;
			score -= dartScore;
			
			if (score < 0 || (score == 0 && result.multiplier != 2) || score == 1) {	// invalid turn
				
				score += dartScore;
				darts -= turnDarts;
				turnDarts = 0;
				
			} else if (score == 0 && result.multiplier == 2) {							// leg end
				
				score = DARTS501_INITIAL_SCORE;
				turnDarts = 0;
			}			
			
			// leg/turn end
			if (turnDarts == 0) {
				turnDarts = DARTS_PER_TURN;
			}
		}
		return score;
	}

	public static void main(String[] args) {
		LOG.info("Started.");
		try {
			Main solution = new Main(new StdJudgeInterface());
			solution.practice(6000);
			solution.compete(99999, DARTS501_INITIAL_SCORE);
			LOG.info("Completed normally.");
		} catch (RuntimeException e) {
			LOG.error("Completed with error.", e);
		}
	}

	Point2D.Double selectTarget(int score) {
		throw new UnsupportedOperationException("Throwing darts not implemented yet");	// FIXME
	}

	static Set<BoardField> generateBoardFields() {
		Set<BoardField> fields = new HashSet<>();
		fields.add(new BoardField(0.0, 2.0 * Math.PI, 0.0, 12.7, 2, 25));
		fields.add(new BoardField(0.0, 2.0 * Math.PI, 12.7, 31.8, 1, 25));

		int[] scores = new int[] {20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5};
		final double fieldAngle = Math.PI / 10.0;
		double angle = -fieldAngle / 2.0;
		for (int i = 0; i < 20; ++i) {
			final double minAngle = angle;
			final double maxAngle = minAngle + fieldAngle;
			angle = maxAngle;
			fields.add(new BoardField(minAngle, maxAngle, 31.8, 99, 1, scores[i]));
			fields.add(new BoardField(minAngle, maxAngle, 99, 107, 3, scores[i]));
			fields.add(new BoardField(minAngle, maxAngle, 107, 162, 1, scores[i]));
			fields.add(new BoardField(minAngle, maxAngle, 162, 170, 2, scores[i]));
		}

		return fields;
	}

	static String toString(Point2D.Double p) {
	    return p.toString().substring("Point2D.Double".length());
    }
}

///////////////////////////////////////////////////////////////////////////////
interface JudgeInterface {
	
	void publishPracticingDartsCount(int n);
	ThrowingResult throwDart(Point2D.Double target);
}

///////////////////////////////////////////////////////////////////////////////
class StdJudgeInterface implements JudgeInterface {
	
	private final Scanner scanner = new Scanner(System.in);

	public void publishPracticingDartsCount(int n) {
		System.out.println(n);
		System.out.flush();
		Main.LOG.info("Practice session with {} darts.", n);
	}

	public ThrowingResult throwDart(Point2D.Double target) {
		System.out.format(Locale.ROOT, "%f %f\n", target.x, target.y);
		System.out.flush();
		Main.LOG.debug("Dart targeted at {}.", Main.toString(target));

		var value1 = scanner.next();
		var value2 = scanner.next();
		var value3 = scanner.next();
		var value4 = scanner.next();
		return new ThrowingResult(new Point2D.Double(parseDouble(value1), parseDouble(value2)), parseInt(value3), parseInt(value4));
	}
}

///////////////////////////////////////////////////////////////////////////////
class ThrowingResult {

	final Point2D.Double point;
	final int multiplier;
	final int score;

	public ThrowingResult(Point2D.Double point, int multiplier, int score) {
		this.point = point;
		this.multiplier = multiplier;
		this.score = score;
		Main.LOG.info("Result: {}; score: {}x{}", Main.toString(point), score, multiplier);
	}

	public Point2D.Double getPoint() {
		return point;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public int getScore() {
		return score;
	}
}

///////////////////////////////////////////////////////////////////////////////
class PolarCoordinates {
	
	final double angle;
	final double radius;

	public PolarCoordinates(double angle, double radius) {
		this.angle = angle;
		this.radius = radius;
	}

	public Point2D.Double toCartesian() {
		return new Point2D.Double(radius * Math.sin(angle), -radius * Math.cos(angle));
	}
}

///////////////////////////////////////////////////////////////////////////////
class BoardField {

	private final double minAngle;
	private final double maxAngle;
	private final double minRadius;
	private final double maxRadius;
	private final int multiplier;
	private final int score;
	private final List<Point2D.Double> targetingDeltas = new LinkedList<>();
	private Point2D.Double avgTargetingDelta = new Point2D.Double(0.0, 0.0);
	private double accuracy = -1.0;

	public BoardField(double minAngle, double maxAngle, double minRadiusMm, double maxRadiusMm, int multiplier, int score) {
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
		this.minRadius = minRadiusMm / Main.BOARD_RADIUS_MM;
		this.maxRadius = maxRadiusMm / Main.BOARD_RADIUS_MM;
		this.multiplier = multiplier;
		this.score = score;
	}

	public PolarCoordinates getCenterAsPolar() {
		return new PolarCoordinates((maxAngle + minAngle) / 2.0, (minRadius + maxRadius) / 2.0);
	}

	public Point2D.Double getCenterAsCartesian() {
		return getCenterAsPolar().toCartesian();
	}

	public Point2D.Double getCorrectedTarget() {
		return add(getCenterAsCartesian(), avgTargetingDelta);
	}

	public void addTargetingDelta(Point2D.Double delta) {
		targetingDeltas.add(delta);
		avgTargetingDelta = targetingDeltas.stream().reduce(new Point2D.Double(0.0, 0.0),
				(a, b) -> new Point2D.Double(a.x + b.x, a.y + b.y));
		final int count = targetingDeltas.size();
		avgTargetingDelta.setLocation(avgTargetingDelta.x / count, avgTargetingDelta.y / count);
		// TODO ograniczenie odleglosci delty od srodka tarczy
	}

	public Point2D.Double getAvgTargetingDelta() {
		return new Point2D.Double(avgTargetingDelta.x, avgTargetingDelta.y);
	}

	public int getMultiplier() {
		return multiplier;
	}

	public int getScore() {
		return score;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public String toString() {
		return String.format("%dx%d (radius range: %f-%f)", score, multiplier, minRadius, maxRadius);
	}

	private static Point2D.Double add(Point2D.Double a, Point2D.Double b) {
		return new Point2D.Double(a.x + b.x, a.y + b.y);
	}
}
