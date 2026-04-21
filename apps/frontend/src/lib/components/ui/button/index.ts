// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Re-exports button variants for shared imports across browse staff and checkout.
// Assistance: Robbie - Auth and staff shells that default-import button from here.
// Assistance: Samantha - Cart and checkout actions that default-import button from here.

import Root, {
	type ButtonProps,
	type ButtonSize,
	type ButtonVariant,
	buttonVariants
} from './button.svelte';

export {
	Root,
	type ButtonProps as Props,
	//
	Root as Button,
	buttonVariants,
	type ButtonProps,
	type ButtonSize,
	type ButtonVariant
};
