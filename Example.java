/*
Using the MenuExample from andengine
http://code.google.com/p/andengineexamples/source/browse/src/org/anddev/andengine/examples/MenuExample.java
*/

public ChangeableText statusText = new ChangeableText(0, 0, font, "YOU LOSE");
public ChangeableText splitsText = new ChangeableText(0, 0, font, "1234");
public ChangeableText scoreText = new ChangeableText(0, 0, font, "0123456789");
public FlowMenu menu;

// Init objects
public void init() {
		menu = new FlowMenu(options.camera, FlowMenu.FLOAT, FlowMenu.CENTER);

	    // Preloaded texture
        final SpriteMenuItem nextMenuItem = new SpriteMenuItem(Options.MENU_NEXT, textureNext);
        nextMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
   	
   		// Transparent background over our game for the menu
    	Rectangle rect = new Rectangle(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
    	rect.setColor(0.0f, 0.0f, 0.0f);
    	rect.setAlpha(0.8f);
    	
    	BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Font font = new Font(fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
        getEngine().getTextureManager().loadTexture(fontTexture);
        getEngine().getFontManager().loadFont(font);

        // Create our menu
        menu.init()
	        .add(rect)
	        .br(90, true)
	        .container(FlowMenu.CENTER)
	        	.container(FlowMenu.CENTER)
	        		.add(options.statusText)
	        	.end()
				.br(40)
	    		.container()
	    			.add(new Text(0, 0, font, "Score:"),250)
	    			.add(options.scoreText)
	    			.br()
	    			.add(new Text(0, 0, font, "Splits:"),250)
	    			.add(options.splitsText)
	    		.end()
	        	.br(40)
	        	.container(FlowMenu.CENTER)
	        		.add(nextMenuItem)
	        	.end()
        	.end();
        
        menu.setMenuAnimator(new FlowAnimator(0.5f));
        menu.buildAnimations();
        menu.setBackgroundEnabled(false);
        menu.setOnMenuItemClickListener(this);
}

// Show the menu
public void showMenu(boolean win) {
	statusText.setText(win ? "YOU WIN" : "YOU LOSE");
	menu.buildAnimations();
	scene.setChildScene(menu, false, true, true);
}