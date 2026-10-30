// SPDX-License-Identifier: MIT
// Consistency check of a reco form result: the rules that JSON Schema Draft 2020-12
// cannot express, so reco-form-result.schema.json does not check them.
//
// - markers_expected is the number of marker_details
// - markers_detected is the number of marker_details with detected true
// - run_info.run.status follows the phase statuses: 'failed' when a phase failed,
//   'partial' when a phase is partial, otherwise 'ok' (RunRecord.finish in portunix-reco)
//
// Run it after the schema check, on a document that the schema accepts.
// Other projects can import checkRecoFormResult() and use the same rules.

/**
 * Checks the marker counts and the run status of a reco form result
 *
 * @param {object} doc reco form result that reco-form-result.schema.json accepts
 * @returns {{path: string, message: string}[]} found problems, empty when the result is consistent
 */
export function checkRecoFormResult(doc) {
	const errors = [];
	const details = doc?.marker_details ?? [];

	if (details.length > 0) {
		if (doc.markers_expected !== details.length) {
			errors.push({
				path: "/markers_expected",
				message: `is ${doc.markers_expected}, but marker_details has ${details.length} markers`,
			});
		}
		const detected = details.filter((d) => d.detected).length;
		if (doc.markers_detected !== detected) {
			errors.push({
				path: "/markers_detected",
				message: `is ${doc.markers_detected}, but ${detected} markers of marker_details are detected`,
			});
		}
	}

	const status = doc?.run_info?.run?.status;
	if (status !== undefined) {
		const expected = runStatus(doc.run_info.phases ?? []);
		if (status !== expected) {
			errors.push({
				path: "/run_info/run/status",
				message: `is '${status}', but the phase statuses give '${expected}'`,
			});
		}
	}
	return errors;
}

// Run status that the phase statuses give: failed wins over partial, partial over ok
function runStatus(phases) {
	const statuses = new Set(phases.map((p) => p.status));
	if (statuses.has("failed")) return "failed";
	if (statuses.has("partial")) return "partial";
	return "ok";
}
