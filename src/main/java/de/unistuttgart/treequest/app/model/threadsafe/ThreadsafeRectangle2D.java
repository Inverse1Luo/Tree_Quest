package de.unistuttgart.treequest.app.model.threadsafe;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ThreadsafeRectangle2D extends Rectangle2D {
	
	/**
     * The X coordinate of this <code>Rectangle2D</code>.
     */
    private double x;

    /**
     * The Y coordinate of this <code>Rectangle2D</code>.
     */
    private double y;

    /**
     * The width of this <code>Rectangle2D</code>.
     */
    private double width;

    /**
     * The height of this <code>Rectangle2D</code>.
     */
    private double height;

    /**
     * Constructs a new <code>Rectangle2D</code>, initialized to
     * location (0,&nbsp;0) and size (0,&nbsp;0).
     */
    public ThreadsafeRectangle2D() {
    }

    /**
     * Constructs and initializes a <code>Rectangle2D</code>
     * from the specified <code>double</code> coordinates.
     *
     * @param x the X coordinate of the upper-left corner
     *          of the newly constructed <code>Rectangle2D</code>
     * @param y the Y coordinate of the upper-left corner
     *          of the newly constructed <code>Rectangle2D</code>
     * @param w the width of the newly constructed
     *          <code>Rectangle2D</code>
     * @param h the height of the newly constructed
     *          <code>Rectangle2D</code>
     */
    public ThreadsafeRectangle2D(double x, double y, double w, double h) {
        setRect(x, y, w, h);
    }

    @Override
    public double getX() {
    	synchronized (this) {
    		return x;
		}
    }

    @Override
    public double getY() {
    	synchronized (this) {
    		return y;
		}
    }

    @Override
    public double getWidth() {
    	synchronized (this) {
    		return width;
		}
    }

    @Override
    public double getHeight() {
    	synchronized (this) {
    		return height;
		}
    }

    @Override
    public boolean isEmpty() {
    	synchronized (this) {
    		return (width <= 0.0) || (height <= 0.0);
		}
    }

    @Override
    public void setRect(double x, double y, double w, double h) {
    	synchronized (this) {
    		this.x = x;
    		this.y = y;
    		this.width = w;
    		this.height = h;
		}
    }

    public void setRect(Rectangle2D r) {
    	synchronized (this) {
    		this.x = r.getX();
    		this.y = r.getY();
    		this.width = r.getWidth();
    		this.height = r.getHeight();
		}
    }

    @Override
    public int outcode(double x, double y) {
        int out = 0;
        synchronized (this) {
        	if (this.width <= 0) {
        		out |= OUT_LEFT | OUT_RIGHT;
        	} else if (x < this.x) {
        		out |= OUT_LEFT;
        	} else if (x > this.x + this.width) {
        		out |= OUT_RIGHT;
        	}
        	if (this.height <= 0) {
        		out |= OUT_TOP | OUT_BOTTOM;
        	} else if (y < this.y) {
        		out |= OUT_TOP;
        	} else if (y > this.y + this.height) {
        		out |= OUT_BOTTOM;
        	}
		}
        return out;
    }

    public synchronized Rectangle2D getBounds2D() {
        return new ThreadsafeRectangle2D(x, y, width, height);
    }
    
    @Override
	public boolean contains(Point2D p) {
		synchronized (this) {
			double x0 = p.getX();
	        double y0 = p.getY();
	        return (x <= x0 &&
	                y <= y0 &&
	                x + getWidth() > x0  &&
	                y + getHeight() > y0 );
		}
	}

    @Override
    public Rectangle2D createIntersection(Rectangle2D r) {
    	synchronized (this) {
    		Rectangle2D dest = new Rectangle2D.Double();
    		Rectangle2D.intersect(this, r, dest);
    		return dest;
		}
    }

    @Override
    public Rectangle2D createUnion(Rectangle2D r) {
    	synchronized (this) {
    		Rectangle2D dest = new Rectangle2D.Double();
    		Rectangle2D.union(this, r, dest);
    		return dest;
		}
    }

}
