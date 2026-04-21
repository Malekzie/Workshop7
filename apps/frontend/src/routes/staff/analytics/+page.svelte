<script lang="ts">
// Contributor(s): Robbie, Mason
// Main: Robbie, Mason - Staff analytics charts: admin metrics client reads date-range query params and bakery filter.

	import { scaleBand } from 'd3-scale';
	import { curveLinearClosed } from 'd3-shape';
	import { onMount } from 'svelte';
	import { Skeleton } from '$lib/components/ui/skeleton';
	import * as Chart from '$lib/components/ui/chart/index.js';
	import { BarChart, LineChart, PieChart, Text } from 'layerchart';
	import KpiCard from '$lib/components/staff/KpiCard.svelte';
	import {
		getBakeryNames,
		getTotalRevenue,
		getAverageOrderValue,
		getCompletionRate,
		getRevenueOverTime,
		getRevenueByBakery,
		getTopProducts,
		getSalesByEmployee
	} from '$lib/services/analytics';
	import { formatPriceCad } from '$lib/utils/money';

	type AnalyticsPoint = { label: string; value: number };
	type EmployeeSalesPoint = { employee: string; sales: number };

	interface Props {
		data: { startDate: string; endDate: string };
	}

	let { data }: Props = $props();

	// svelte-ignore state_referenced_locally
	let startDate = $state(data.startDate);
	// svelte-ignore state_referenced_locally
	let endDate = $state(data.endDate);
	let selectedBakery = $state('');

	// A pending promise that never resolves  -  used as a placeholder during SSR
	// and before the client-side fetches are kicked off in onMount. The {#await}
	// blocks will render their skeletons while these are pending, so the page
	// HTML ships immediately instead of waiting on backend queries.
	const pending = <T,>(): Promise<T> => new Promise<T>(() => {});

	let bakeryNamesPromise = $state<Promise<string[]>>(pending());
	let kpisPromise = $state<Promise<[number, number, number]>>(pending());
	let revenueOverTimePromise = $state<Promise<AnalyticsPoint[]>>(pending());
	let revenueByBakeryPromise = $state<Promise<AnalyticsPoint[]>>(pending());
	let topProductsPromise = $state<Promise<AnalyticsPoint[]>>(pending());
	let salesByEmployeePromise = $state<Promise<AnalyticsPoint[]>>(pending());

	const chartConfig = {
		employee: { label: 'Employee' },
		sales: { label: 'Sales', color: '#2C1A0E' },
		value: { label: 'Revenue', color: '#C25F1A' },
		units: { label: 'Units Sold', color: '#8A9E7F' }
	} satisfies Chart.ChartConfig;

	const paletteVars = [
		'var(--chart-1)',
		'var(--chart-2)',
		'var(--chart-3)',
		'var(--chart-4)',
		'var(--chart-5)'
	];

	function withPalette(points: AnalyticsPoint[]) {
		return points.map((p, i) => ({
			label: p.label,
			value: p.value,
			color: paletteVars[i % paletteVars.length]
		}));
	}

	function formatDateLabel(raw: string): string {
		const date = new Date(raw);
		if (Number.isNaN(date.getTime())) return raw;
		return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
	}

	function toEmployeeSalesSeries(points: AnalyticsPoint[]): EmployeeSalesPoint[] {
		return points.map((point) => ({
			employee: point.label,
			sales: point.value
		}));
	}

	function loadData() {
		const bakery = selectedBakery || undefined;
		kpisPromise = Promise.all([
			getTotalRevenue(startDate, endDate, bakery),
			getAverageOrderValue(startDate, endDate, bakery),
			getCompletionRate(startDate, endDate, bakery)
		]);
		revenueOverTimePromise = getRevenueOverTime(startDate, endDate, bakery);
		revenueByBakeryPromise = getRevenueByBakery(startDate, endDate);
		topProductsPromise = getTopProducts(startDate, endDate, bakery);
		salesByEmployeePromise = getSalesByEmployee(startDate, endDate, bakery);
	}

	onMount(() => {
		bakeryNamesPromise = getBakeryNames();
		loadData();
	});

	function formatCurrency(val: number | null) {
		if (val == null) return '—';
		return formatPriceCad(val);
	}

	function formatPercent(val: number | null) {
		if (val == null) return '—';
		return `${(Number(val) * 100).toFixed(1)}%`;
	}
</script>

<main class="flex-1 overflow-y-auto p-8 lg:p-10">
	<div class="mx-auto max-w-5xl space-y-8">
		<div>
			<h1 class="text-2xl font-bold tracking-tight text-foreground">Analytics</h1>
			<p class="mt-1 text-sm text-muted-foreground">Revenue and performance metrics</p>
		</div>

		<div class="flex flex-wrap items-end gap-3">
			<div class="flex flex-col gap-1">
				<label
					for="fromDateInput"
					class="text-xs font-semibold tracking-widest text-muted-foreground uppercase"
				>
					From
				</label>
				<input
					id="fromDateInput"
					type="date"
					bind:value={startDate}
					class="rounded-md border border-border bg-background px-3 py-2 text-sm text-foreground"
				/>
			</div>
			<div class="flex flex-col gap-1">
				<label
					for="toDateInput"
					class="text-xs font-semibold tracking-widest text-muted-foreground uppercase"
				>
					To
				</label>
				<input
					id="toDateInput"
					type="date"
					bind:value={endDate}
					class="rounded-md border border-border bg-background px-3 py-2 text-sm text-foreground"
				/>
			</div>
			<div class="flex flex-col gap-1">
				<label
					for="bakerySelectInput"
					class="text-xs font-semibold tracking-widest text-muted-foreground uppercase"
				>
					Bakery
				</label>
				{#await bakeryNamesPromise}
					<Skeleton class="h-10 w-32 rounded-md" />
				{:then bakeryNames}
					<select
						id="bakerySelectInput"
						bind:value={selectedBakery}
						class="rounded-md border border-border bg-background px-3 py-2 text-sm text-foreground"
					>
						<option value="">All Bakeries</option>
						{#each bakeryNames as name (name)}
							<option value={name}>{name}</option>
						{/each}
					</select>
				{:catch}
					<p class="text-xs text-destructive">Failed to load bakeries</p>
				{/await}
			</div>
			<button
				onclick={loadData}
				class="rounded-md bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground hover:opacity-90"
			>
				Apply
			</button>
		</div>

		<div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
			{#await kpisPromise}
				{#each [0, 1, 2] as i (i)}
					<Skeleton class="h-28 rounded-xl" />
				{/each}
			{:then [totalRevenue, aov, completionRate]}
				<KpiCard label="Total Revenue" value={formatCurrency(totalRevenue)} />
				<KpiCard label="Avg Order Value" value={formatCurrency(aov)} />
				<KpiCard label="Completion Rate" value={formatPercent(completionRate)} />
			{:catch}
				<p class="text-sm text-destructive">Failed to load KPI metrics.</p>
			{/await}
		</div>

		<div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
			<div class="rounded-xl border border-border bg-card p-5">
				<p class="mb-4 text-sm font-semibold text-foreground">Revenue Over Time</p>
				{#await revenueOverTimePromise}
					<Skeleton class="h-48 rounded-xl" />
				{:then revenueOverTime}
					{#if revenueOverTime.length === 0}
						<p class="py-8 text-center text-xs text-muted-foreground">No data</p>
					{:else}
						<Chart.Container config={chartConfig} class="h-56 w-full">
							<LineChart
								data={revenueOverTime}
								x="label"
								y="value"
								axis="x"
								series={[{ key: 'value', label: 'Revenue', color: '#C25F1A' }]}
								points={{ r: 3, fill: '#C25F1A' }}
								padding={{ bottom: 24, left: 8, right: 8 }}
								props={{
									xAxis: {
										format: formatDateLabel,
										tickLength: 4,
										ticks: (scale: { domain: () => unknown[] }) => {
											const domain = scale.domain();
											const max = 8;
											if (domain.length <= max) return domain;
											const stride = Math.ceil(domain.length / max);
											return domain.filter((_, i) => i % stride === 0);
										}
									},
									yAxis: {
										format: (v: number) => formatPriceCad(v)
									}
								}}
							>
								{#snippet tooltip()}
									<Chart.Tooltip labelFormatter={(d: unknown) => formatDateLabel(String(d))} />
								{/snippet}
							</LineChart>
						</Chart.Container>
					{/if}
				{:catch}
					<p class="py-4 text-center text-xs text-destructive">Failed to load chart</p>
				{/await}
			</div>

			<div class="rounded-xl border border-border bg-card p-5">
				<p class="mb-4 text-sm font-semibold text-foreground">Revenue by Bakery</p>
				{#await revenueByBakeryPromise}
					<Skeleton class="h-48 rounded-xl" />
				{:then revenueByBakery}
					{@const revenueByBakeryChart = withPalette(revenueByBakery)}
					{@const totalBakeryRevenue = revenueByBakery.reduce((sum, p) => sum + p.value, 0)}
					{#if revenueByBakery.length === 0}
						<p class="py-8 text-center text-xs text-muted-foreground">No data</p>
					{:else}
						<Chart.Container config={chartConfig} class="mx-auto aspect-square max-h-65">
							<PieChart
								data={revenueByBakeryChart}
								key="label"
								value="value"
								c="color"
								innerRadius={70}
								padding={24}
								props={{ pie: { motion: 'tween', cornerRadius: 4, padAngle: 0.01 } }}
							>
								{#snippet aboveMarks()}
									<Text
										value={formatPriceCad(totalBakeryRevenue)}
										textAnchor="middle"
										verticalAnchor="middle"
										class="fill-foreground text-xl! font-bold"
										dy={-2}
									/>
									<Text
										value="Total"
										textAnchor="middle"
										verticalAnchor="middle"
										class="fill-muted-foreground! text-xs"
										dy={18}
									/>
								{/snippet}
								{#snippet tooltip()}
									<Chart.Tooltip hideLabel hideIndicator class="min-w-48!">
										{#snippet formatter({ value, item })}
											<div class="flex w-full items-center justify-between gap-6 py-0.5">
												<div class="flex items-center gap-2">
													<span
														class="size-2.5 shrink-0 rounded-[2px]"
														style="background-color: {item.color}"
													></span>
													<span class="text-muted-foreground">{item.key}</span>
												</div>
												<span class="font-mono font-medium text-foreground tabular-nums">
													{formatPriceCad(Number(value))}
												</span>
											</div>
										{/snippet}
									</Chart.Tooltip>
								{/snippet}
							</PieChart>
						</Chart.Container>
					{/if}
				{:catch}
					<p class="py-4 text-center text-xs text-destructive">Failed to load chart</p>
				{/await}
			</div>

			<div class="rounded-xl border border-border bg-card p-5">
				<p class="mb-4 text-sm font-semibold text-foreground">Top Products</p>
				{#await topProductsPromise}
					<Skeleton class="h-48 rounded-xl" />
				{:then topProducts}
					{#if topProducts.length === 0}
						<p class="py-8 text-center text-xs text-muted-foreground">No data</p>
					{:else}
						<Chart.Container config={chartConfig} class="h-64 w-full">
							<BarChart
								data={topProducts}
								orientation="horizontal"
								y="label"
								x="value"
								yScale={scaleBand().padding(0.25)}
								axis="y"
								padding={{ left: 140, right: 32 }}
								series={[{ key: 'value', label: 'Units Sold', color: 'var(--color-units)' }]}
								labels={{
									offset: 8,
									class: 'fill-foreground text-xs font-medium tabular-nums'
								}}
								props={{
									bars: { stroke: 'none', rounded: 'all', radius: 6, fillOpacity: 0.9 },
									yAxis: {
										tickLength: 0,
										format: (d: string) => (d.length > 18 ? `${d.slice(0, 18)}…` : d),
										tickLabelProps: { class: 'fill-muted-foreground text-[11px]' }
									}
								}}
							>
								{#snippet tooltip()}
									<Chart.Tooltip indicator="line" />
								{/snippet}
							</BarChart>
						</Chart.Container>
					{/if}
				{:catch}
					<p class="py-4 text-center text-xs text-destructive">Failed to load chart</p>
				{/await}
			</div>

			<div class="rounded-xl border border-border bg-card p-5">
				<p class="mb-4 text-sm font-semibold text-foreground">Sales by Employee</p>
				{#await salesByEmployeePromise}
					<Skeleton class="h-48 rounded-xl" />
				{:then salesByEmployee}
					{@const salesByEmployeeChart = toEmployeeSalesSeries(salesByEmployee)}
					{#if salesByEmployee.length === 0}
						<p class="py-8 text-center text-xs text-muted-foreground">No data</p>
					{:else}
						<Chart.Container config={chartConfig} class="mx-auto aspect-square max-h-[260px]">
							<LineChart
								data={salesByEmployeeChart}
								series={[{ key: 'sales', label: 'Sales', color: '#2C1A0E' }]}
								radial
								x="employee"
								y="sales"
								xScale={scaleBand()}
								points={{ r: 4, fill: '#2C1A0E' }}
								padding={12}
								props={{
									spline: {
										curve: curveLinearClosed,
										fill: 'var(--color-sales)',
										fillOpacity: 0.25,
										stroke: 'var(--color-sales)',
										strokeWidth: 2,
										motion: 'tween'
									},
									xAxis: {
										tickLength: 0
									},
									yAxis: {
										format: () => ''
									},
									grid: {
										radialY: 'linear'
									},
									tooltip: {
										context: {
											mode: 'voronoi'
										}
									},
									highlight: {
										lines: true,
										points: true
									}
								}}
							>
								{#snippet tooltip()}
									<Chart.Tooltip label="employee" />
								{/snippet}
							</LineChart>
						</Chart.Container>

						<!-- <Chart.Container config={chartConfig} class="h-64 w-full">
							<BarChart
								data={salesByEmployee}
								orientation="horizontal"
								y="label"
								x="value"
								bandPadding={0.3}
								axis="y"
								padding={{ left: 120 }}
								series={[{ key: 'value', label: 'Sales ($)', color: '#2C1A0E' }]}
							/>
						</Chart.Container> -->
					{/if}
				{:catch}
					<p class="py-4 text-center text-xs text-destructive">Failed to load chart</p>
				{/await}
			</div>
		</div>
	</div>
</main>
