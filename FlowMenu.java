package com.caval.splitter.andengine;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.vertex.VertexBuffer;

import android.view.MotionEvent;

/**
 * @author Methodin
 * @date 10.1.2011
 */
public class FlowMenu extends MenuScene {
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int FLOAT = 0;
	public static final int FILL = 1;
	public static final float INHERIT = -1;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	protected IOnMenuItemClickListener onMenuItemClickListener;
	protected FlowAnimator menuAnimator = null;
	protected IMenuItem selectedMenuItem;
	protected FlowContainer container;
	protected Camera camera;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public FlowMenu() {
		this(null, FLOAT, LEFT);
	}

	public FlowMenu(final Camera pCamera, int pWidthControl, int pPositionControl) {
		super(pCamera);
		camera = pCamera;
		container =  new FlowContainer(null, pWidthControl, pPositionControl);
		this.setOnSceneTouchListener(this);
		this.setOnAreaTouchListener(this);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public IOnMenuItemClickListener getOnMenuItemClickListener() {
		return this.onMenuItemClickListener;
	}

	public void setOnMenuItemClickListener(final IOnMenuItemClickListener pOnMenuItemClickListener) {
		this.onMenuItemClickListener = pOnMenuItemClickListener;
	}

	@Override
	public MenuScene getChildScene() {
		return (MenuScene)super.getChildScene();
	}

	@Override
	public void setChildScene(final Scene pChildScene, final boolean pModalDraw, final boolean pModalUpdate, final boolean pModalTouch) throws IllegalArgumentException {
		if(pChildScene instanceof MenuScene) {
			super.setChildScene(pChildScene, pModalDraw, pModalUpdate, pModalTouch);
		} else {
			throw new IllegalArgumentException("MenuScene accepts only MenuScenes as a ChildScene.");
		}
	}

	@Override
	public void clearChildScene() {
		if(this.getChildScene() != null) {
			this.getChildScene().reset();
			super.clearChildScene();
		}
	}
	
	public void setMenuAnimator(final FlowAnimator pMenuAnimator) {
		menuAnimator = pMenuAnimator;
	}	

	// ===========================================================
	// Methods
	// ===========================================================
	
	@Override
	public void buildAnimations() {
		this.prepareAnimations();
		this.menuAnimator.buildAnimations(container);
	}	
	
	@Override
	public void prepareAnimations() {
		this.menuAnimator.prepareAnimations(container);
	}
	
	@Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		final IMenuItem menuItem = ((IMenuItem)pTouchArea);
		switch(pSceneTouchEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				if(this.selectedMenuItem != null && this.selectedMenuItem != menuItem) {
					this.selectedMenuItem.onUnselected();
				}
				this.selectedMenuItem = menuItem;
				this.selectedMenuItem.onSelected();
				break;
			case MotionEvent.ACTION_UP:
				if(this.onMenuItemClickListener != null) {
					final boolean handled = this.onMenuItemClickListener.onMenuItemClicked(this, menuItem, pTouchAreaLocalX, pTouchAreaLocalY);
					menuItem.onUnselected();
					this.selectedMenuItem = null;
					return handled;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				menuItem.onUnselected();
				this.selectedMenuItem = null;
				break;
		}
		return true;
	}		
	
	public FlowContainer init() {
		return container;
	}
	
	// ===========================================================
	// Classes
	// ==========================================================
	
	public class FlowContainer extends Shape{
		private int widthControl = FLOAT;
		private int positionControl = LEFT;
		private FlowContainer parent = null;
		public ArrayList<FlowItem> entities = new ArrayList<FlowItem>();

		public FlowContainer() {
			super(0, 0);
		}
		public FlowContainer(FlowContainer pParent) {
			super(0, 0);
			parent = pParent;
			this.mX = 0;
			this.mY = 0;
		}
		public FlowContainer(FlowContainer pParent, int pWidthControl, int pPositionControl) {
			super(0, 0);
			parent = pParent;
			this.mX = 0;
			this.mY = 0;
			positionControl = pPositionControl;
			widthControl = pWidthControl;
		}		
		
		public void draw(float pX, float pY) {
			float bufferedHeight = 0;
			float fullWidth = getWidth();
			float parentWidth = parent == null ? camera.getWidth() : parent.getWidth();
			float parentX = parent == null ? 0 : parent.getX();

			if(positionControl == CENTER) {
				pX = parentX + (parentWidth-fullWidth)/2;
			} else if(positionControl == RIGHT) {
				pX = parentX + parentWidth-fullWidth;
			}

			float childX = pX;
			float childY = pY;
			this.setPosition(pX, pY);

			for(final FlowItem o : entities) {
				if(o.isBreak()) {
					childY += bufferedHeight;
					childX = pX;
					bufferedHeight = 0;
					if(((FlowBreak)o.getItem()).getReset()) {
						childX = pX;
						childY = pY;
					}
					childY += ((FlowBreak)o.getItem()).getHeight();
				} else {
					if(o.isContainer())  
						((FlowContainer)o.getItem()).draw(childX, childY);
					else
						o.setPosition(childX, childY);
					childX += o.getWidth(); 
					if(o.getHeight() > bufferedHeight) bufferedHeight = o.getHeight();
				}
			}
		}
		
		public FlowContainer getParent() { return parent; }
		
		// Creates a new container inside this one
		public FlowContainer container() { return this.container(LEFT,FLOAT); }
		public FlowContainer container(int pPositionControl) { return this.container(pPositionControl,FLOAT); }
		public FlowContainer container(int pPositionControl, int pWidthControl) {
			FlowContainer newContainer = new FlowContainer(this);
			newContainer.widthControl = pWidthControl;
			newContainer.positionControl = pPositionControl;			
			this.entities.add(new FlowItem(newContainer));
			return newContainer;
		}
		
		// Ends the container
		public FlowContainer end() {
			return parent;
		}
	
		// Adds an item to the container
		public FlowContainer add(IShape pItem) { return this.add(pItem, INHERIT, INHERIT); }
		public FlowContainer add(IShape pItem, float pWidth) { return this.add(pItem, pWidth, INHERIT); }
		public FlowContainer add(IShape pItem, float pWidth, float pHeight) {
			FlowMenu.this.attachChild((IShape)pItem);
			final String name = pItem.getClass().getSimpleName();
			if(name.equals("SpriteMenuItem") || name.equals("AnimatedSpriteMenuItem")) {
				FlowMenu.this.registerTouchArea((IMenuItem)pItem);
			}
			entities.add(new FlowItem(pItem, pWidth, pHeight));
			return this;
		}
		
		// Adds a break
		public FlowContainer br() { return this.br(0,false); }
		public FlowContainer br(float pSpacing) { return this.br(pSpacing,false); }
		public FlowContainer br(float pSpacing, boolean pReset) {
			entities.add(new FlowItem(new FlowBreak(pSpacing,pReset)));
			return this;
		}
		
		public float getHeight() {
			float height = 0;
			float bufferedHeight = 0;
			for(final FlowItem o : entities) {
				if(o.isBreak()) {
					height += bufferedHeight;
					bufferedHeight = 0;
				}
				if(o.getHeight() > bufferedHeight) bufferedHeight = o.getHeight();
			}
			height += bufferedHeight;
			return height;
		}
		public float getWidth() {
			if(widthControl == FILL) {
				return FILL;
			}
			float width = 0;
			float bufferedWidth = 0;
			for(final FlowItem o : entities) {
				if(o.isBreak()) {
					if(bufferedWidth > width) width = bufferedWidth;
					bufferedWidth = 0;
				}
				bufferedWidth += o.getWidth();
			}
			if(bufferedWidth > width) width = bufferedWidth;
			return width;
		}
		
		// Unused overrides
		@Override
		protected void drawVertices(GL10 arg0, Camera arg1) {}
		@Override
		protected VertexBuffer getVertexBuffer() { return null; }
		@Override
		protected boolean isCulled(Camera arg0) { return false; }
		@Override
		protected void onUpdateVertexBuffer() {}
		public boolean collidesWith(IShape arg0) { return false; }
		public float getBaseHeight() { return 0; }
		public float getBaseWidth() { return 0;	}
		public boolean contains(float arg0, float arg1) { return false; }
	}
	
	public class FlowItem extends Shape {
		private IShape item;
		private float width;
		private float height;
		private boolean container = false;
		private boolean br = false;
		
		public FlowItem() {
			super(0,0);
		}
		public FlowItem(IShape pShape) {
			this(pShape,INHERIT,INHERIT);
		}
		public FlowItem(IShape pShape,float pWidth,float pHeight) {
			super(0,0);
			item = pShape;
			item.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			width = pWidth;
			height = pHeight;
			if(pShape.getClass().getSimpleName().equals("FlowContainer")) {
				container = true;
			} else if(pShape.getClass().getSimpleName().equals("FlowBreak")) {
				br = true;
			}
		}		
		
		public boolean isContainer() {
			return container;
		}
		public boolean isBreak() {
			return br;
		}
		public IShape getItem() {
			return item;
		}
		
		@Override
		public void setPosition(float pX, float pY) { 
			item.setPosition(pX,pY);
		}
		
		// Unused overrides
		public float getHeight() { 
			if(height == INHERIT) {
				if(br) return 0;
				return item.getHeight();
			} else {
				return width;
			}
		}
		public float getWidth() { 
			if(width == INHERIT) {
				if(br) return 0;
				return item.getWidth();
			} else {
				return width;
			}
		}
		@Override
		protected void drawVertices(GL10 arg0, Camera arg1) {}
		@Override
		protected VertexBuffer getVertexBuffer() { return null; }
		@Override
		protected boolean isCulled(Camera arg0) { return false; }
		@Override
		protected void onUpdateVertexBuffer() {}
		public boolean collidesWith(IShape arg0) { return false; }
		public float getBaseHeight() { return 0; }
		public float getBaseWidth() { return 0;	}
		public boolean contains(float arg0, float arg1) { return false; }
	}
	
	// A basic break element - similar to <br> in html
	public class FlowBreak extends Shape{
		// Reset all elements
		private boolean reset = false;
		// Change spacing (padding)
		private float spacing = 0;
		
		public FlowBreak() {
			this(0,false);
		}
		public FlowBreak(float pSpacing) {
			this(pSpacing,false);
		}
		public FlowBreak(float pSpacing, boolean pReset) {
			super(0, 0);
			reset = pReset;
			spacing = pSpacing;
		}
		
		public boolean getReset() {
			return reset;
		}
		
		// Unused overrides
		public float getHeight() { return spacing; }
		public float getWidth() { return 1; }
		@Override
		protected void drawVertices(GL10 arg0, Camera arg1) {}
		@Override
		protected VertexBuffer getVertexBuffer() { return null; }
		@Override
		protected boolean isCulled(Camera arg0) { return false; }
		@Override
		protected void onUpdateVertexBuffer() {}
		public boolean collidesWith(IShape arg0) { return false; }
		public float getBaseHeight() { return 0; }
		public float getBaseWidth() { return 0;	}
		public boolean contains(float arg0, float arg1) { return false; }
	}	
}