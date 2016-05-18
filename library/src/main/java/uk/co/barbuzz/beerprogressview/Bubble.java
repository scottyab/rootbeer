package uk.co.barbuzz.beerprogressview;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * The bubble class tracks the size, location and colour of a single bubble.
 */
public class Bubble {

	//standard beer size, speed & fps
	private static final int BUBBLE_SIZE = 20;
	private static final int SPEED = 30;
	private static final int FPS = 30;

	private int step;
	private double amp, freq, skew;
	private float x, y, radius, maxRadius;
	public boolean popped;
	private Paint paint;

	/**
	 * Simple function for getting a random range.
	 *
	 * @param min The minimum int.
	 * @param max The maximum int.
	 * @return The random value.
	 */
	public static int randRange(int min, int max) {
		int mod = max - min;
		double val = Math.ceil(Math.random() * 1000000) % mod;
		return (int)val + min;
	}

	/**
	 * Create a bubble, passing in width & height of view
	 *
	 * @param width
	 * @param height
     */
	public Bubble(int width, int height, int topMargin, int bubbleColour) {
		popped = false;
		paint = new Paint();
		paint.setColor(bubbleColour);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		recycle(true, width, height, topMargin);
	}

	/**
	 * Re-initialises the Bubble properties so that it appears to be a new
	 * bubble.
	 *
	 * Although a bit of elegance is sacrificed this seemed to result in a
	 * performance boost during initial testing.
	 *
	 * @param initial
	 * @param width
	 * @param height
     */
	public void recycle(boolean initial, int width, int height, int topMargin) {
		if(initial) {
			y = randRange(topMargin, height);
		} else {
			// Start at the bottom if not initial
			y = height + (randRange(0, 21) - 10 );
		}
		x = randRange(0, width);
		radius = 1;
		maxRadius = randRange(3, BUBBLE_SIZE);
		paint.setAlpha(randRange(100, 250));
		popped = false;
		step = 0;
		amp = Math.random() * 3;
		freq = Math.random() * 2;
		skew = Math.random() - 0.5;
	}
	
	/**
	 * Update the size and position of a Bubble.
	 * 
	 * @param fps The current FPS
	 * @param angle The angle of the device
	 */
	public void update(int fps, float angle) {
		double speed = (SPEED / FPS) * Math.log(radius);
		y -= speed;
		x += amp * Math.sin(freq * (step++ * speed)) + skew;
		if(radius < maxRadius) {
			radius += maxRadius / (((float)fps / SPEED) * radius);
			if(radius > maxRadius) radius = maxRadius;
		}
	}

	/**
	 * Test whether a bubble is no longer visible.
	 *
	 * @param width Canvas width
	 * @param height Canvas height
	 * @param topMargin offset from top of view that bubble will be popped
	 * @return A boolean indicating that the Bubble has drifted off allowable area
     */
	public boolean popped(int width, int height, int topMargin) {
		if(y + radius <= -20 ||
				y - radius >= height ||
				x + radius <= 0 ||
				x - radius >= width ||
				y - radius <= topMargin) {
			return true;
		}
		return false;
	}
	
	/**
	 * Unified method for drawing the bubble.
	 * 
	 * @param canvas The canvas to draw on
	 */
	public void draw(Canvas canvas) {
		canvas.drawCircle(x, y, radius, paint);
	}
	
}
