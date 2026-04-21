// Contributor(s): Mason
// Main: Mason - Form and postal helpers for registration profile and addresses.

/**
 * Canadian postal code display and input in A1A 1A1 shape with at most six letters or digits plus one space.
 * Strips non-alphanumeric input then uppercases letters.
 */
export function formatCanadianPostalInput(raw: string): string {
	const alnum = raw.toUpperCase().replace(/[^A-Z0-9]/g, '');
	const six = alnum.slice(0, 6);
	if (six.length <= 3) return six;
	return `${six.slice(0, 3)} ${six.slice(3)}`;
}
