// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Re-exports sheet parts for filters staff tools and checkout slide-overs.
// Assistance: Robbie - Staff routes that compose sheets from this barrel.
// Assistance: Samantha - Cart and checkout flows that open sheets from this entry.

import Root from './sheet.svelte';
import Portal from './sheet-portal.svelte';
import Trigger from './sheet-trigger.svelte';
import Close from './sheet-close.svelte';
import Overlay from './sheet-overlay.svelte';
import Content from './sheet-content.svelte';
import Header from './sheet-header.svelte';
import Footer from './sheet-footer.svelte';
import Title from './sheet-title.svelte';
import Description from './sheet-description.svelte';

export {
	Root,
	Close,
	Trigger,
	Portal,
	Overlay,
	Content,
	Header,
	Footer,
	Title,
	Description,
	//
	Root as Sheet,
	Close as SheetClose,
	Trigger as SheetTrigger,
	Portal as SheetPortal,
	Overlay as SheetOverlay,
	Content as SheetContent,
	Header as SheetHeader,
	Footer as SheetFooter,
	Title as SheetTitle,
	Description as SheetDescription
};
