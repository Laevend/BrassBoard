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
package coffee.khyonieheart.lilac.parser.productions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import coffee.khyonieheart.lilac.TomlSyntaxException;
import coffee.khyonieheart.lilac.parser.LilacDecoder;
import coffee.khyonieheart.lilac.value.TomlObject;
import coffee.khyonieheart.lilac.value.TomlTable;
import coffee.khyonieheart.lilac.value.TomlTableArray;

public class ProductionTableArray
{
	public static boolean parse(
		LilacDecoder parser
	)
		throws TomlSyntaxException
	{
		if (!parser.parseLiteral("[["))
		{
			return false;
		}

		while (parser.consumeCharacters(' ', '\t'));
		Optional<List<String>> keysOption = ProductionKey.parse(parser);

		if (keysOption.isEmpty())
		{
			throw new TomlSyntaxException("Expected at least one key for TableArray", parser.getLine(), parser.getLinePointer(), 1, parser.getCurrentDocument());
		}

		Map<String, TomlObject<?>> targetTable = parser.getTomlData();
		List<String> parents = new ArrayList<>();
		for (String key : keysOption.get())
		{
			parents.add(key);
			if (!targetTable.containsKey(key))
			{
				parser.addKeyValuePair(parents, new TomlTable(parents));
			}

			targetTable = switch (targetTable.get(key).getType())
			{
				case TABLE -> ((TomlTable) targetTable.get(key)).get();
				case INLINE_TABLE -> throw new TomlSyntaxException("Cannot modify inline table after creation", parser.getLine(), parser.getLinePointer(), key.length(), parser.getCurrentDocument());
				case TABLE_ARRAY -> throw new TomlSyntaxException("Cannot modify table array after creation", parser.getLine(), parser.getLinePointer(), key.length(), parser.getCurrentDocument());
				default -> throw new TomlSyntaxException("Table array cannot redefine key " + key, parser.getLine(), parser.getLinePointer(), key.length(), parser.getCurrentDocument());
			};
		}

		while (parser.consumeCharacters(' ', '\t'));

		if (!parser.parseLiteral("]]"))
		{
			throw new TomlSyntaxException("Expected double-bracket end \"]]\" to end array of tables", parser.getLine(), parser.getLinePointer(), 2, parser.getCurrentDocument());
		}

		while (parser.consumeCharacters(' ', '\t'));

		TomlTableArray tableArray = new TomlTableArray(keysOption.get());

		if (tableArray.equalsTable(parser.getCurrentTableArray()))
		{
			parser.getCurrentTableArray().startNextTable();
			return true;
		}
		
		parser.clearCurrentTableArray();
		parser.addKeyValuePair(keysOption.get(), tableArray);
		parser.setCurrentTableArray(tableArray);

		return true;
	}
}
