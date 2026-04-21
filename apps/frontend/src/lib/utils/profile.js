// Contributor(s): Mason
// Main: Mason - Form and postal helpers for registration profile and addresses.

/** True when name phone and full address exist and phone is not the OAuth sentinel prefix. */
export function isProfileComplete(profile) {
	return !!(
		profile?.firstName &&
		profile?.lastName &&
		profile?.phone &&
		!profile.phone.toUpperCase().startsWith('OAUTH-') &&
		profile?.address?.line1 &&
		profile?.address?.city &&
		profile?.address?.province &&
		profile?.address?.postalCode
	);
}
