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

package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.InputDialogListener;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.OptionDialogListener;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.widget.*;

/** Utilities for displaying various dialogs, equivalent of Swing JOptionPane */
public class DialogUtils {

	private static final int BUTTON_CANCEL = 0;
	private static final int BUTTON_YES = 1;
	private static final int BUTTON_NO = 2;
	private static final int BUTTON_OK = 3;
	private static final int BUTTON_DETAILS = 4;

	public static void showOKDialog (Stage stage, String title, String text) {
		VisDialog dialog = new VisDialog(title);
		dialog.text(text);
		dialog.button("OK").padBottom(3);
		dialog.pack();
		dialog.centerWindow();
		stage.addActor(dialog.fadeIn());
	}

	/** @since 0.6.0 */
	public static OptionDialog showOptionDialog (Stage stage, String title, String text, OptionDialogType type, final OptionDialogListener listener) {
		return new OptionDialog(stage, title, text, type, listener);
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, true, null, listener);
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, InputValidator validator, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, true, validator, listener);
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, cancelable, null, listener);
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputValidator validator, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, cancelable, validator, listener);
	}

	public static void showErrorDialog (Stage stage, String text) {
		showErrorDialog(stage, text, (String) null);
	}

	public static void showErrorDialog (Stage stage, String text, Exception exception) {
		if (exception == null)
			showErrorDialog(stage, text, (String) null);
		else
			showErrorDialog(stage, text, getStackTrace(exception));
	}

	public static void showErrorDialog (Stage stage, String text, String details) {
		ErrorDialog dialog = new ErrorDialog(text, details);
		stage.addActor(dialog.fadeIn());
	}

	private static VisScrollPane createScrollPane (Actor widget) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}

	private static String getStackTrace (Throwable throwable) {
		StringBuilder builder = new StringBuilder();

		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append(element);
			builder.append("\n");
		}

		return builder.toString();
	}

	public enum OptionDialogType {
		YES_NO, YES_NO_CANCEL, YES_CANCEL
	}

	public static class InputDialog extends VisWindow {
		private InputDialogListener listener;
		private VisTextField field;
		private VisTextButton okButton;
		private VisTextButton cancelButton;


		public InputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputValidator validator, InputDialogListener listener) {
			super(title);
			this.listener = listener;

			TableUtils.setSpaceDefaults(this);
			setModal(true);

			if (cancelable) {
				addCloseButton();
				closeOnEscape();
			}

			VisTable buttonsTable = new VisTable(true);
			buttonsTable.add(cancelButton = new VisTextButton("Cancel"));
			buttonsTable.add(okButton = new VisTextButton("OK"));

			VisTable fieldTable = new VisTable(true);

			if (validator == null)
				field = new VisTextField();
			else
				field = new VisValidableTextField(validator);

			if (fieldTitle != null) fieldTable.add(new VisLabel(fieldTitle));

			fieldTable.add(field).expand().fill();

			add(fieldTable).padTop(3).spaceBottom(4);
			row();
			add(buttonsTable).padBottom(3);

			addListeners();

			if (validator != null) {
				addValidableFieldListener(field);
				okButton.setDisabled(!field.isInputValid());
			}

			pack();
			centerWindow();

			stage.addActor(fadeIn());
			field.focusField();
		}

		@Override
		protected void close () {
			super.close();
			listener.canceled();
		}

		private void addValidableFieldListener (final VisTextField field) {
			field.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if (field.isInputValid())
						okButton.setDisabled(false);
					else
						okButton.setDisabled(true);
				}
			});
		}

		private void addListeners () {
			okButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					listener.finished(field.getText());
					fadeOut();
				}
			});

			cancelButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					close();
				}
			});

			field.addListener(new InputListener() {
				@Override
				public boolean keyDown (InputEvent event, int keycode) {
					if (keycode == Keys.ENTER && okButton.isDisabled() == false) {
						listener.finished(field.getText());
						fadeOut();
					}

					return super.keyDown(event, keycode);
				}
			});
		}
	}

	public static class OptionDialog extends VisDialog {
		private OptionDialogListener listener;

		private VisTextButton yesButton = new VisTextButton("Yes");
		private VisTextButton noButton = new VisTextButton("No");
		private VisTextButton cancelButton = new VisTextButton("Cancel");

		public OptionDialog (Stage stage, String title, String text, OptionDialogType type, OptionDialogListener listener) {
			super(title);

			this.listener = listener;

			text(text);
			defaults().padBottom(3);

			switch (type) {
				case YES_NO:
					button(noButton, BUTTON_NO);
					button(yesButton, BUTTON_YES);
					break;
				case YES_CANCEL:
					button(yesButton, BUTTON_YES);
					button(cancelButton, BUTTON_CANCEL);
					break;
				case YES_NO_CANCEL:
					button(noButton, BUTTON_NO);
					button(yesButton, BUTTON_YES);
					button(cancelButton, BUTTON_CANCEL);
					break;
			}

			pack();
			centerWindow();
			stage.addActor(fadeIn());
		}

		@Override
		protected void result (Object object) {
			int result = (Integer) object;

			if (result == BUTTON_YES) listener.yes();
			if (result == BUTTON_NO) listener.no();
			if (result == BUTTON_CANCEL) listener.cancel();
		}

		public void setNoButtonText (String text) {
			noButton.setText(text);
		}

		public void setYesButtonText (String text) {
			yesButton.setText(text);
		}

		public void setCancelButtonText (String text) {
			cancelButton.setText(text);
		}
	}

	public static class ErrorDialog extends VisDialog {

		private VisTable detailsTable = new VisTable(true);
		private Cell<?> detailsCell;

		public ErrorDialog (String text, String stackTrace) {
			super("Error");

			text(text);

			if (stackTrace != null) {
				final VisTextButton copyButton = new VisTextButton("Copy");
				final VisLabel errorLabel = new VisLabel(stackTrace);

				copyButton.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						Gdx.app.getClipboard().setContents((errorLabel.getText().toString()));
						copyButton.setText("Copied");
					}
				});

				detailsTable.add(new VisLabel("Details:")).left().expand().padTop(6);
				detailsTable.add(copyButton);
				detailsTable.row();

				VisTable errorTable = new VisTable();
				errorTable.add(errorLabel).top().expand().fillX();
				detailsTable.add(createScrollPane(errorTable)).colspan(2).width(600).height(300);

				getContentTable().row();
				detailsCell = getContentTable().add(detailsTable);
				detailsCell.setActor(null);
				button("Details", BUTTON_DETAILS);
			}

			button("OK", BUTTON_OK).padBottom(3);
			pack();
			centerWindow();
		}

		@Override
		protected void result (Object object) {
			int result = (Integer) object;

			if (result == BUTTON_DETAILS) {
				detailsCell.setActor(detailsCell.hasActor() ? null : detailsTable);
				pack();
				centerWindow();
				cancel();
			}
		}
	}

}
