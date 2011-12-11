package com.caval.splitter.andengine;

import java.util.ArrayList;

import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.scene.menu.animator.BaseMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;

import com.caval.splitter.andengine.FlowMenu.FlowContainer;
import com.caval.splitter.andengine.FlowMenu.FlowItem;


/**
 * @author Methodin
 * @date 06.10.2011
 */
public class FlowAnimator extends BaseMenuAnimator {
        private static final float ALPHA_FROM = 0.0f;
        private static final float ALPHA_TO = 1.0f;
        private float duration = 2.0f;

        public FlowAnimator() {}
        public FlowAnimator(float pDuration) {
        	duration = pDuration;
        }

		public void buildAnimations(final FlowContainer container) {
			for(FlowItem o : container.entities) {
				if(o.isContainer()) {
					buildAnimations((FlowContainer)o.getItem());
				} else if(!o.isBreak()) {
					if(!o.getItem().isVisible()) continue;
					float alpha = o.getItem().getAlpha();
					if(alpha == 0) alpha = ALPHA_TO;
					o.getItem().clearEntityModifiers();
					o.getItem().registerEntityModifier(new AlphaModifier(duration, ALPHA_FROM, alpha));
				}
			}
		}

		public void prepareAnimations(final FlowContainer container) {
			container.draw(0,0);
		}

		public void buildAnimations(ArrayList<IMenuItem> arg0, float arg1, float arg2) {}

		public void prepareAnimations(ArrayList<IMenuItem> arg0, float arg1, float arg2) {}
}