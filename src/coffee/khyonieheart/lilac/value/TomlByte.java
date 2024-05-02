/*
 * This file is part of Lilac, a high performance TOML language library.
 * Copyright (C) 2024 Hailey-Jane "Khyonie" Garrett
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package coffee.khyonieheart.lilac.value;

import java.util.Objects;

import coffee.khyonieheart.lilac.TomlParser;
import coffee.khyonieheart.lilac.api.Commentable;
import coffee.khyonieheart.lilac.api.NumberBase;

public class TomlByte implements Commentable, TomlObject<Byte>
{
	private NumberBase base = NumberBase.DECIMAL;
	private byte value;
	private String comment;
	private int newlines = 0;

	public TomlByte(
		byte value
	) {
		this.value = value;
	}

	public TomlByte(
		byte value,
		NumberBase base
	) {
		this.value = value;
		this.base = base;
	}

	@Override
	public Byte get() 
	{
		return this.value;
	}

	@Override
	public TomlObjectType getType() 
	{
		return TomlObjectType.BYTE;
	}

	public NumberBase getSerializerBase()
	{
		return this.base;
	}

	public TomlByte setSerializerBase(
		NumberBase base
	) {
		this.base = base;

		return this;
	}

	public String serialize(
		TomlParser builder
	) {
		Objects.requireNonNull(builder);

		return switch (this.base)
		{
			case BINARY -> "0b" + Integer.toBinaryString(this.value).substring(24);
			case OCTAL -> "0o" + Integer.toOctalString(this.value);
			case DECIMAL -> value + "";
			case HEXADECIMAL -> "0x" + (builder.getIsUppercaseHex() ? Integer.toHexString(this.value).substring(4).toUpperCase() : Integer.toHexString(this.value).substring(2));
		};
	}

	@Override
	public String serialize() 
	{
		return switch (this.base)
		{
			case BINARY -> "0b" + Integer.toBinaryString(this.value).substring(24);
			case OCTAL -> "0o" + Integer.toOctalString(this.value);
			case DECIMAL -> value + "";
			case HEXADECIMAL -> "0x" + Integer.toHexString(this.value).substring(2);
		};
	}

	@Override
	public String getComment() 
	{
		return this.comment;
	}

	@Override
	public void setComment(
		String comment
	) {
		this.comment = comment;
	}

	@Override
	public int getNumberOfTrailingNewlines() 
	{
		return this.newlines;
	}

	@Override
	public void incrementTrailingNewlines() 
	{
		this.newlines++;
	}

	@Override 
	public TomlByte clone()
	{
		return new TomlByte(this.value, this.base);
	}
}
