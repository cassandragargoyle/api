// SPDX-License-Identifier: MIT
// Remove the comments of a JSONC document so that JSON.parse can read it.
// Line comments ('// ...') and block comments ('/* ... */') outside of strings are replaced
// by spaces (a new line stays a new line), so the positions in JSON.parse errors stay right.
// Trailing commas are not supported.

export function stripJsonComments(text) {
	let out = "";
	let i = 0;
	while (i < text.length) {
		const c = text[i];
		const next = text[i + 1];
		if (c === '"') {
			// Copy a string literal as it is, including escaped quotes
			let j = i + 1;
			while (j < text.length && text[j] !== '"') j += text[j] === "\\" ? 2 : 1;
			out += text.slice(i, j + 1);
			i = j + 1;
		} else if (c === "/" && next === "/") {
			let j = i;
			while (j < text.length && text[j] !== "\n") j++;
			out += " ".repeat(j - i);
			i = j;
		} else if (c === "/" && next === "*") {
			const end = text.indexOf("*/", i + 2);
			const j = end === -1 ? text.length : end + 2;
			out += text.slice(i, j).replace(/[^\n]/g, " ");
			i = j;
		} else {
			out += c;
			i++;
		}
	}
	return out;
}
