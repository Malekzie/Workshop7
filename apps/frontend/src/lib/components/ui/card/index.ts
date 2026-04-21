// Contributor(s): Robbie, Mason, Samantha
// Main: Mason - Re-exports card regions for catalog profile staff KPI and checkout summaries.
// Assistance: Robbie - Staff dashboards that assemble cards from this barrel.
// Assistance: Samantha - Payment and confirmation pages that assemble cards from this barrel.

import Root from './card.svelte';
import Content from './card-content.svelte';
import Description from './card-description.svelte';
import Footer from './card-footer.svelte';
import Header from './card-header.svelte';
import Title from './card-title.svelte';
import Action from './card-action.svelte';

export {
	Root,
	Content,
	Description,
	Footer,
	Header,
	Title,
	Action,
	//
	Root as Card,
	Content as CardContent,
	Description as CardDescription,
	Footer as CardFooter,
	Header as CardHeader,
	Title as CardTitle,
	Action as CardAction
};
