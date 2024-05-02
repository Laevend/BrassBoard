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
package coffee.khyonieheart.lilac.parser.productions.value.string;

import java.util.Optional;

import coffee.khyonieheart.lilac.TomlSyntaxException;
import coffee.khyonieheart.lilac.parser.LilacDecoder;

public class ProductionMultilineBasicString
{
	public static Optional<String> parse(
		LilacDecoder parser
	)
		throws TomlSyntaxException
	{
		String data = parser.getCurrentDocument().substring(parser.getPointer());

		if (data.length() < 6)
		{
			return Optional.empty();
		}

		if (!data.startsWith("\"\"\""))
		{
			return Optional.empty();
		}

		int pointer = 3; // 3 to account for the """ delimiter
		parser.incrementPointer(3);

		if (data.charAt(pointer) == '\n')
		{
			parser.nextLine();
			pointer++;
			parser.incrementPointer(1);
		}

		boolean escaped = false;
		StringBuilder builder = new StringBuilder();
		char currentChar;

		boolean foundEnd = false;
		while (pointer + 3 <= data.length())
		{
			if (data.substring(pointer, pointer + 3).equals("\"\"\"") && !escaped)
			{
				foundEnd = true;
				break;
			}

			currentChar = data.charAt(pointer);

			if (escaped)
			{
				escaped = false;

				switch (currentChar)
				{
					case 'n' -> { 
						builder.append('\n');
						parser.incrementPointer(1);
						pointer++;
					}
					case 'b' -> { 
						builder.append('\b');
						parser.incrementPointer(1);
						pointer++;
					}
					case 't' -> { 
						builder.append('\t');
						parser.incrementPointer(1);
						pointer++;
					}
					case 'f' -> { 
						builder.append('\f');
						parser.incrementPointer(1);
						pointer++;
					}
					case 'r' -> { 
						builder.append('\r');
						parser.incrementPointer(1);
						pointer++;
					}
					case '"' -> { 
						builder.append('"');
						parser.incrementPointer(1);
						pointer++;
					}
					case '\\' -> {
						builder.append('\\');
						parser.incrementPointer(1);
						pointer++;
					}
					case '\n' -> { // Line-ending backslash
						parser.nextLine();
						parser.incrementPointer(1);
						pointer++;
						while (Character.isWhitespace(data.charAt(pointer)))
						{
							if (data.charAt(pointer) == '\n')
							{
								parser.nextLine();
							}

							pointer++;
							parser.incrementPointer(1);
						}

						continue;
					}
					case ' ' -> { // Line-ending space
						parser.incrementPointer(1);
						pointer++;
						while (Character.isWhitespace(data.charAt(pointer)))
						{
							if (data.charAt(pointer) == '\n')
							{
								parser.nextLine();
							}

							pointer++;
							parser.incrementPointer(1);
						}

						continue;
					}
					case '\t' -> { // Line-ending tab
						parser.incrementPointer(1);
						pointer++;
						while (Character.isWhitespace(data.charAt(pointer)))
						{
							if (data.charAt(pointer) == '\n')
							{
								parser.nextLine();
							}

							pointer++;
							parser.incrementPointer(1);
						}

						continue;
					}
					case 'u' -> { // Unicode hex literal
						builder.append("\\u");
						parser.incrementPointer(1);
						pointer++;
					}
					case 'U' -> { // Unicode hex literal
						builder.append("\\u");
						parser.incrementPointer(1);
						pointer++;
					}
					case 'x' -> { // Unicode hex literal
						builder.append("\\x");
						parser.incrementPointer(1);
						pointer++;
					}
					// TODO Unicode U+xxxx and U+xxxxxxxx escapes
					default -> throw new TomlSyntaxException("Illegal escaped character '\\" + currentChar + "'", parser.getLine(), parser.getLinePointer(), 1, parser.getCurrentDocument());
				}

				continue;
			}

			pointer++;
			parser.incrementPointer(1);

			if (currentChar == '\n')
			{
				parser.nextLine();
			}

			if (currentChar == '\\')
			{
				escaped = true;
				continue;
			}

			builder.append(currentChar);
		}

		if (!foundEnd)
		{
			throw new TomlSyntaxException("Unterminated multiline basic string", parser.getLine(), parser.getLinePointer(), 3, parser.getCurrentDocument());
		}

		// Scout ahead to make sure we're consuming as much as possible
		parser.incrementPointer(3);
		pointer += 3;
		if (pointer < data.length())
		{
			while (data.charAt(pointer) == '"')
			{
				parser.incrementPointer(1);
				pointer++;
				builder.append('"');

				if (pointer >= data.length())
				{
					break;
				}
			}
		}

		//System.out.println("Ending char: " + parser.charAtPointer());
		//System.out.println("Captured string: " + parser.getCurrentDocument().substring(originalPointer, parser.getPointer()));
		return Optional.of(builder.toString());
	}
}
