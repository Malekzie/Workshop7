// Contributor(s): Robbie, Mason, Owen
// Main: Mason - Re-exports avatar pieces for profile review chat and staff directory rows.
// Assistance: Robbie - Messaging screens that import avatar parts from this barrel.
// Assistance: Owen - Linked-account views that reuse avatar exports for consistent headshots.

import Root from './avatar.svelte';
import Image from './avatar-image.svelte';
import Fallback from './avatar-fallback.svelte';
import Badge from './avatar-badge.svelte';
import Group from './avatar-group.svelte';
import GroupCount from './avatar-group-count.svelte';

export {
	Root,
	Image,
	Fallback,
	Badge,
	Group,
	GroupCount,
	//
	Root as Avatar,
	Image as AvatarImage,
	Fallback as AvatarFallback,
	Badge as AvatarBadge,
	Group as AvatarGroup,
	GroupCount as AvatarGroupCount
};
