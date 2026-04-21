// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Re-exports button group pieces for toolbars cart lines and staff filters.
// Assistance: Robbie - Inbox and dashboard imports of grouped controls from this barrel.
// Assistance: Samantha - Checkout quantity groups that import from this barrel.

import Root, { buttonGroupVariants, type ButtonGroupOrientation } from './button-group.svelte';
import Text from './button-group-text.svelte';
import Separator from './button-group-separator.svelte';

export {
	Root,
	Text,
	Separator,
	buttonGroupVariants,
	type ButtonGroupOrientation,
	//
	Root as ButtonGroup,
	Text as ButtonGroupText,
	Separator as ButtonGroupSeparator
};
