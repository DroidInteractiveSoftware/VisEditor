/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;

/**
 * MenuItem displayed in {@link Menu} and {@link PopupMenu}. MenuItem contains text or text with icon.
 * Best icon size is 22px. MenuItem can also have hotkey text.
 */
public class MenuItem extends Button {
	private Image image;
	private Label label;
	private TextButtonStyle style;

	private boolean generateDisabledImage = true;

	private Cell<VisLabel> shortcutLabelCell;

	public MenuItem (String text) {
		this(text, (Image) null, VisUI.getSkin().get(TextButtonStyle.class));
	}

	public MenuItem (String text, ChangeListener changeListener) {
		this(text, (Image) null, VisUI.getSkin().get(TextButtonStyle.class));
		addListener(changeListener);
	}

	public MenuItem (String text, Drawable drawable) {
		this(text, drawable, VisUI.getSkin().get(TextButtonStyle.class));
	}

	public MenuItem (String text, Drawable drawable, ChangeListener changeListener) {
		this(text, drawable, VisUI.getSkin().get(TextButtonStyle.class));
		addListener(changeListener);
	}

	public MenuItem (String text, Image image) {
		this(text, image, VisUI.getSkin().get(TextButtonStyle.class));
	}

	public MenuItem (String text, Image image, ChangeListener changeListener) {
		this(text, image, VisUI.getSkin().get(TextButtonStyle.class));
		addListener(changeListener);
	}

	// Base constructors

	public MenuItem (String text, Image image, TextButtonStyle style) {
		super(style);
		init(text, image, style);
	}

	public MenuItem (String text, Drawable drawable, TextButtonStyle style) {
		super(style);
		init(text, new Image(drawable), style);
	}

	private void init (String text, Image image, TextButtonStyle style) {
		this.style = style;
		this.image = image;
		setSkin(VisUI.getSkin());

		defaults().space(3);

		if (image != null) image.setScaling(Scaling.fit);
		add(image).size(22);

		label = new Label(text, new LabelStyle(style.font, style.fontColor));
		label.setAlignment(Align.left);
		add(label).expand().fill();
	}

	@Override
	public TextButtonStyle getStyle () {
		return style;
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (!(style instanceof TextButtonStyle)) throw new IllegalArgumentException("style must be a TextButtonStyle.");
		super.setStyle(style);
		this.style = (TextButtonStyle) style;
		if (label != null) {
			TextButtonStyle textButtonStyle = (TextButtonStyle) style;
			LabelStyle labelStyle = label.getStyle();
			labelStyle.font = textButtonStyle.font;
			labelStyle.fontColor = textButtonStyle.fontColor;
			label.setStyle(labelStyle);
		}
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		Color fontColor;
		if (isDisabled() && style.disabledFontColor != null)
			fontColor = style.disabledFontColor;
		else if (isPressed() && style.downFontColor != null)
			fontColor = style.downFontColor;
		else if (isChecked() && style.checkedFontColor != null)
			fontColor = (isOver() && style.checkedOverFontColor != null) ? style.checkedOverFontColor : style.checkedFontColor;
		else if (isOver() && style.overFontColor != null)
			fontColor = style.overFontColor;
		else
			fontColor = style.fontColor;
		if (fontColor != null) label.getStyle().fontColor = fontColor;

		if (image != null && generateDisabledImage) {
			if (isDisabled())
				image.setColor(Color.GRAY);
			else
				image.setColor(Color.WHITE);
		}

		super.draw(batch, parentAlpha);
	}

	public boolean isGenerateDisabledImage () {
		return generateDisabledImage;
	}

	/**
	 * Changes generateDisabledImage property, when true that function is enabled. When it is enabled and this MenuItem is disabled then image color will be changed
	 * to gray meaning that it is disabled, by default it is enabled.
	 */
	public void setGenerateDisabledImage (boolean generateDisabledImage) {
		this.generateDisabledImage = generateDisabledImage;
	}

	/**
	 * Set shortcuts text displayed in this menu item.
	 * This DOES NOT set actual hot key for this menu item, it only makes shortcut text visible in item.
	 * @param keycode from {@link Keys}.
	 */
	public MenuItem setShortcut (int keycode) {
		return setShortcut(Keys.toString(keycode));
	}

	/**
	 * Set shortcuts text displayed in this menu item.
	 * Displayed as modifier+keycode (eg. Ctrl+F5). This DOES NOT set actual hot key for this menu item, it only makes shortcut text visible in item.
	 * @param modifier form {@link Keys}
	 * @param keycode  form {@link Keys}
	 */
	public MenuItem setShortcut (int modifier, int keycode) {
		return setShortcut(Keys.toString(modifier) + "+" + Keys.toString(keycode));
	}

	public String getShortcut () {
		return shortcutLabelCell.getActor().getText().toString();
	}

	/**
	 * Set shortcuts text displayed in this menu item. This DOES NOT set actual hot key for this menu item,
	 * it only makes shortcut text visible in item.
	 * @param text text that will be displayed
	 * @return this object for the purpose of chaining methods
	 */
	public MenuItem setShortcut (String text) {
		if (shortcutLabelCell == null)
			shortcutLabelCell = add(new VisLabel(text, "menuitem-shortcut")).padLeft(10).right();
		else
			shortcutLabelCell.getActor().setText(text);

		return this;
	}

	public Image getImage () {
		return image;
	}

	public Cell<?> getImageCell () {
		return getCell(image);
	}

	public Label getLabel () {
		return label;
	}

	public Cell<?> getLabelCell () {
		return getCell(label);
	}

	public CharSequence getText () {
		return label.getText();
	}

	public void setText (CharSequence text) {
		label.setText(text);
	}
}
