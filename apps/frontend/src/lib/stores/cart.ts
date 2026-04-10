import { writable, derived } from 'svelte/store';

export interface CartItem {
	productId: number;
	productName: string;
	productImageUrl: string | null;
	unitPrice: number;
	quantity: number;
	lineTotal: number;
}

export interface CartState {
	items: CartItem[];
	subtotal: number;
	discount: number;
	total: number;
	itemCount: number;
}

const CART_KEY = 'pg_cart';

function loadFromStorage(): CartState {
	if (typeof localStorage === 'undefined') return emptyCart();
	try {
		const raw = localStorage.getItem(CART_KEY);
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

function createCartStore() {
	const { subscribe, set, update } = writable<CartState>(loadFromStorage());

	function persist(state: CartState) {
		if (typeof localStorage !== 'undefined') {
			localStorage.setItem(CART_KEY, JSON.stringify(state));
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
							? { ...i, quantity: qty, lineTotal: Math.ceil(i.unitPrice * qty * 100) / 100 }
							: i
					);
				} else {
					items = [
						...state.items,
						{ ...item, lineTotal: Math.ceil(item.unitPrice * item.quantity * 100) / 100 }
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
							? { ...i, quantity, lineTotal: Math.ceil(i.unitPrice * quantity * 100) / 100 }
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
				localStorage.removeItem(CART_KEY);
			}
			set(empty);
		}
	};
}

export const cart = createCartStore();

export const cartCount = derived(cart, ($cart) => $cart.itemCount);
