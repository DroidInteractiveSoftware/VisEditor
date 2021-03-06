/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.project;

public class Project {
	public Type type;

	/** Root of project, for LibGDX type this is root of Gradle folder, for generic this is same as assets folder */
	public String root;
	/** Assets export directory, for LibGDX this is usually gradle_root/android/assets, for generic this is directory provided by user */
	public String assets;

	public Project (Type type) {
		this.type = type;
	}

	public enum Type {LibGDX, Generic}
}
