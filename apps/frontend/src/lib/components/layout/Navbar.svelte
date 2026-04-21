<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Global top nav links cart badge auth menu theme toggle and staff entry when the session role allows.

	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { ShoppingCart, User, Menu, X } from '@lucide/svelte';
	import { isLoggedIn, user } from '$lib/stores/authStore';
	import { page } from '$app/state';
	import { SunIcon, MoonIcon } from '@lucide/svelte';
	import { toggleMode } from 'mode-watcher';
	import { Button } from '$lib/components/ui/button/index';

	interface Props {
		cartCount?: number;
	}

	let { cartCount = 0 }: Props = $props();
	let menuOpen = $state(false);
	let categoryOpen = $state(false);

	function handleClickOutside(e: MouseEvent) {
		const target = e.target as HTMLElement;
		if (!target.closest('.category-dropdown')) {
			categoryOpen = false;
		}
		if (!target.closest('nav')) {
			menuOpen = false;
		}
	}

	function handleCartClick() {
		goto(resolve('/cart'));
	}

	// handles where to direct user when clicking profile based on if they are logged in or not
	function handleProfileClick() {
		if ($user?.role === 'admin' || $user?.role === 'employee') {
			goto(resolve('/staff/dashboard'));
		} else if ($isLoggedIn) {
			goto(resolve('/profile'));
		} else {
			goto(resolve('/login'));
		}
	}

	function handleMenuClick() {
		goto(resolve('/menu'));
	}
</script>

<svelte:window onclick={handleClickOutside} />

<nav class="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur">
	<div class="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
		<!-- Logo -->
		<a href={resolve('/')} class="font-serif text-xl font-bold tracking-tight text-foreground">
			<img src="/images/Peelin' Good.png" alt="Peelin' Good Logo" class="mr-1 inline h-10 w-15" />
		</a>

		<!-- Desktop Nav -->
		<div class="hidden items-center gap-12 md:flex">
			<Button
				class="flex items-center gap-1 text-sm font-medium transition-colors hover:cursor-pointer hover:text-primary
        		{page.url.pathname === '/menu' ? 'text-primary' : 'text-foreground'}"
				aria-expanded={categoryOpen}
				onclick={handleMenuClick}
				variant="ghost"
			>
				Menu
			</Button>
			<Button
				href={resolve('/about')}
				class="text-sm font-medium transition-colors hover:text-primary
        		{page.url.pathname === '/about' ? 'text-primary' : 'text-foreground'}"
				variant="ghost"
			>
				About
			</Button>
			<Button
				href={resolve('/locations')}
				class="text-sm font-medium transition-colors hover:text-primary
        		{page.url.pathname === '/locations' ? 'text-primary' : 'text-foreground'}"
				variant="ghost"
			>
				Locations
			</Button>
		</div>

		<!-- Right icons -->
		<div class="hidden items-center gap-6 md:flex">
			<!-- Dark Mode Buttons -->
			<Button onclick={toggleMode} variant="outline" size="icon">
				<SunIcon
					class="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all! dark:scale-0 dark:-rotate-90"
				/>
				<MoonIcon
					class="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all! dark:scale-100 dark:rotate-0"
				/>
				<span class="sr-only">Toggle Theme</span>
			</Button>
			<Button
				onclick={handleProfileClick}
				aria-label="Account"
				class="transition-colors hover:cursor-pointer hover:text-primary
        		{page.url.pathname.startsWith('/profile') || page.url.pathname.startsWith('/staff')
					? 'text-primary'
					: 'text-foreground'}"
				variant="outline"
			>
				<User size={20} />
			</Button>
			<Button
				onclick={handleCartClick}
				aria-label="Cart ({cartCount} items)"
				class="relative transition-colors hover:text-primary
        		{page.url.pathname === '/cart' ? 'text-primary' : 'text-foreground'}"
				variant="outline"
			>
				<ShoppingCart size={20} />
				{#if cartCount > 0}
					<span
						class="absolute -top-1.5 -right-1.5 flex h-4 w-4 items-center justify-center rounded-full bg-primary text-[10px] font-bold text-primary-foreground"
					>
						{cartCount}
					</span>
				{/if}
			</Button>
		</div>

		<!-- Mobile hamburger -->
		<Button
			aria-label={menuOpen ? 'Close menu' : 'Open menu'}
			class="text-foreground md:hidden"
			onclick={() => (menuOpen = !menuOpen)}
		>
			{#if menuOpen}
				<X size={22} />
			{:else}
				<Menu size={22} />
			{/if}
		</Button>
	</div>

	<!-- Mobile menu -->
	{#if menuOpen}
		<div class="flex flex-col gap-4 border-t border-border bg-background px-6 py-4 md:hidden">
			<a
				href={resolve('/menu')}
				onclick={() => (menuOpen = false)}
				class="text-xs font-semibold tracking-widest uppercase transition-colors
        {page.url.pathname === '/menu' ? 'text-primary' : 'text-foreground'}">Menu</a
			>
			<a
				href={resolve('/about')}
				onclick={() => (menuOpen = false)}
				class="text-sm transition-colors hover:text-primary
        {page.url.pathname === '/about' ? 'text-primary' : 'text-foreground'}">About</a
			>
			<a
				href={resolve('/locations')}
				onclick={() => (menuOpen = false)}
				class="text-sm transition-colors hover:text-primary
        {page.url.pathname === '/locations' ? 'text-primary' : 'text-foreground'}">Locations</a
			>

			<div class="flex gap-4 pt-2">
				<Button
					onclick={() => {
						handleProfileClick();
						menuOpen = false;
					}}
					aria-label="Account"
					class="transition-colors hover:text-primary
        {page.url.pathname.startsWith('/profile') || page.url.pathname.startsWith('/staff')
						? 'text-primary'
						: 'text-foreground'}"
				>
					<User size={20} />
				</Button>
				<Button
					onclick={() => {
						handleCartClick();
						menuOpen = false;
					}}
					aria-label="Cart ({cartCount} items)"
					class="relative transition-colors hover:text-primary
        {page.url.pathname === '/cart' ? 'text-primary' : 'text-foreground'}"
				>
					<ShoppingCart size={20} />
					{#if cartCount > 0}
						<span
							class="absolute -top-1.5 -right-1.5 flex h-4 w-4 items-center justify-center rounded-full bg-primary text-[10px] font-bold text-primary-foreground"
						>
							{cartCount}
						</span>
					{/if}
				</Button>
			</div>
		</div>
	{/if}
</nav>
