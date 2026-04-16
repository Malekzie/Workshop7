import type { RequestHandler } from './$types';

export const POST: RequestHandler = async ({ cookies }) => {
	cookies.delete('token', { path: '/' });
	cookies.delete('JSESSIONID', { path: '/' });

	return new Response(null, { status: 204 });
};
