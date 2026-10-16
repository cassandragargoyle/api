// SPDX-License-Identifier: MIT
// Identifier check of the VIM prescriptive layer: the rules that JSON Schema Draft 2020-12
// cannot express, so vim-document.schema.json does not check them.
//
// - provision_id, reference_id, and term_id are unique in the layer
// - provisions[].references[] holds reference_id values of the layer
// - provisions[].terms[] holds term_id values of the layer
// - provisions[].relations[].provision_id holds a provision_id of the layer
//
// Run it after the schema check, on a document that the schema accepts.
// Other projects can import checkVimIdentifiers() and use the same rules.

/**
 * Checks the identifiers of the prescriptive layer of a VIM document
 *
 * @param {object} doc VIM document that vim-document.schema.json accepts
 * @returns {{path: string, message: string}[]} found problems, empty when the document is consistent
 */
export function checkVimIdentifiers(doc) {
	const layer = doc?.layers?.prescriptive;
	if (!layer) return [];

	const errors = [];
	const base = "/layers/prescriptive";
	const provisions = layer.provisions ?? [];
	const references = layer.references ?? [];
	const terms = layer.terms ?? [];

	const provisionIds = collectIds(provisions, "provision_id", `${base}/provisions`, errors);
	const referenceIds = collectIds(references, "reference_id", `${base}/references`, errors);
	const termIds = collectIds(terms, "term_id", `${base}/terms`, errors);

	provisions.forEach((provision, i) => {
		const at = `${base}/provisions/${i}`;
		(provision.references ?? []).forEach((id, j) => {
			if (!referenceIds.has(id)) {
				errors.push({ path: `${at}/references/${j}`, message: `no reference with reference_id '${id}'` });
			}
		});
		(provision.terms ?? []).forEach((id, j) => {
			if (!termIds.has(id)) {
				errors.push({ path: `${at}/terms/${j}`, message: `no term with term_id '${id}'` });
			}
		});
		(provision.relations ?? []).forEach((relation, j) => {
			if (!provisionIds.has(relation.provision_id)) {
				errors.push({
					path: `${at}/relations/${j}/provision_id`,
					message: `no provision with provision_id '${relation.provision_id}'`,
				});
			}
		});
	});

	return errors;
}

// Collect the identifiers of one array and report every repeated value
function collectIds(items, key, at, errors) {
	const ids = new Set();
	items.forEach((item, i) => {
		const id = item[key];
		if (ids.has(id)) {
			errors.push({ path: `${at}/${i}/${key}`, message: `duplicate ${key} '${id}'` });
		}
		ids.add(id);
	});
	return ids;
}
