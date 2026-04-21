// Contributor(s): Mason
// Main: Mason - Form and postal helpers for registration profile and addresses.

/** Static checks for email, NANP-style phone, and Canadian postal shape before PATCH customers me or registration flows. */
export class FormValidationUtil {
	private static readonly emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
	private static readonly phoneRegex = /^\(\d{3}\) \d{3}-\d{4}$/;
	private static readonly canadianPostalRegex = /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/;

	/** True when the trimmed value matches a simple single-address email pattern. */
	static isValidEmail(value: string): boolean {
		return this.emailRegex.test(value.trim());
	}

	/** True when the value is already formatted as (000) 000-0000. */
	static isValidPhone(value: string): boolean {
		return this.phoneRegex.test(value.trim());
	}

	/** True for Canadian postal codes with an optional space between the third and fourth character. */
	static isValidCanadianPostalCode(value: string): boolean {
		return this.canadianPostalRegex.test(value.trim());
	}

	/** Normalizes digits-only input into (000) 000-0000 as the user types. */
	static formatPhone(raw: string): string {
		const digits = raw.replace(/\D/g, '').substring(0, 10);
		const parts: string[] = [];
		if (digits.length > 0) parts.push('(' + digits.substring(0, 3));
		if (digits.length >= 4) parts.push(') ' + digits.substring(3, 6));
		if (digits.length >= 7) parts.push('-' + digits.substring(6, 10));
		return parts.join('');
	}
}
