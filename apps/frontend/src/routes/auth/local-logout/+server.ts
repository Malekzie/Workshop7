// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - POST handler clears auth cookies for local-only logout flows.

import type { RequestHandler } from './$types';

export const POST: RequestHandler = async ({ cookies }) => {
	cookies.delete('token', { path: '/' });
	cookies.delete('JSESSIONID', { path: '/' });

	return new Response(null, { status: 204 });
};
