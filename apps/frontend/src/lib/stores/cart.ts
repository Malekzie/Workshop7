// Contributor(s): Robbie, Mason, Samantha
// Main: Samantha - Shopping cart store and derived totals.
// Assistance: Robbie - Route guards and session rules that affect cart visibility.
// Assistance: Mason - Product catalog fields shown in each cart line.
// Persists under localStorage keys prefixed with pg_cart, scoped by signed-in user id or guest.

import { writable, derived } from 'svelte/store';

/** One catalog line with quantity and a CAD lineTotal kept in sync with unitPrice. */
export interface CartItem {
	productId: number;
	productName: string;
	productImageUrl: string | null;
	unitPrice: number;
	quantity: number;
	lineTotal: number;
}

/** Full cart snapshot including subtotal discount total and aggregate item count. */
export interface CartState {
	items: CartItem[];
	subtotal: number;
	discount: number;
	total: number;
	itemCount: number;
}

const CART_KEY_PREFIX = 'pg_cart';

function cartKey(userId: string | null): string {
	return userId ? `${CART_KEY_PREFIX}_${userId}` : `${CART_KEY_PREFIX}_guest`;
}

let currentKey = cartKey(null);

function loadFromStorage(key = currentKey): CartState {
	if (typeof localStorage === 'undefined') return emptyCart();
	try {
		const raw = localStorage.getItem(key);
		return raw ? JSON.parse(raw) : emptyCart();
	} catch {
		return emptyCart();
	}
}

function emptyCart(): CartState {
	return { items: [], subtotal: 0, discount: 0, total: 0, itemCount: 0 };
}

function recalc(items: CartItem[]): CartState {
	const subtotal = items.reduce((sum, i) => sum + i.lineTotal, 0);
	const discount = 0;
	const total = subtotal - discount;
	const itemCount = items.reduce((sum, i) => sum + i.quantity, 0);
	return { items, subtotal, discount, total, itemCount };
}

/** Writable cart with add update remove clear and switchUser to reload the correct storage bucket. */
function createCartStore() {
	const { subscribe, set, update } = writable<CartState>(loadFromStorage());

	function persist(state: CartState) {
		if (typeof localStorage !== 'undefined') {
			localStorage.setItem(currentKey, JSON.stringify(state));
		}
		return state;
	}

	return {
		subscribe,

		addItem(item: Omit<CartItem, 'lineTotal'>) {
			update((state) => {
				const existing = state.items.find((i) => i.productId === item.productId);
				let items: CartItem[];
				if (existing) {
					const qty = existing.quantity + item.quantity;
					items = state.items.map((i) =>
						i.productId === item.productId
							? { ...i, quantity: qty, lineTotal: +(i.unitPrice * qty).toFixed(2) }
							: i
					);
				} else {
					items = [
						...state.items,
						{ ...item, lineTotal: +(item.unitPrice * item.quantity).toFixed(2) }
					];
				}
				return persist(recalc(items));
			});
		},

		updateQuantity(productId: number, quantity: number) {
			update((state) => {
				let items: CartItem[];
				if (quantity <= 0) {
					items = state.items.filter((i) => i.productId !== productId);
				} else {
					items = state.items.map((i) =>
						i.productId === productId
							? { ...i, quantity, lineTotal: +(i.unitPrice * quantity).toFixed(2) }
							: i
					);
				}
				return persist(recalc(items));
			});
		},

		removeItem(productId: number) {
			update((state) => {
				const items = state.items.filter((i) => i.productId !== productId);
				return persist(recalc(items));
			});
		},

		clear() {
			const empty = emptyCart();
			if (typeof localStorage !== 'undefined') {
				localStorage.removeItem(currentKey);
			}
			set(empty);
		},

		switchUser(userId: string | null) {
			currentKey = cartKey(userId);
			set(loadFromStorage(currentKey));
		}
	};
}

/** Shared cart store used by menu, cart page, and checkout. */
export const cart = createCartStore();

/** Derived total unit count for header badges. */
export const cartCount = derived(cart, ($cart) => $cart.itemCount);
