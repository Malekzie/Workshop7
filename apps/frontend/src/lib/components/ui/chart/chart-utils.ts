// Contributor(s): Robbie, Mason
// Main: Robbie - ChartConfig helpers and Svelte context for staff metric charts.
// Assistance: Mason - Color keys and theme maps consumed by chart Svelte wrappers.

import type { Tooltip } from "layerchart";
import { getContext, setContext, type Component, type Snippet } from "svelte";

/** CSS selector suffix keys used when chart colors differ in light versus dark mode. */
export const THEMES = { light: "", dark: ".dark" } as const;

export type ChartConfig = {
	[k in string]: {
		label?: string;
		icon?: Component;
	} & (
		| { color?: string; theme?: never }
		| { color?: never; theme: Record<keyof typeof THEMES, string> }
	);
};

export type ExtractSnippetParams<T> = T extends Snippet<[infer P]> ? P : never;

export type TooltipPayload = Tooltip.TooltipSeries;

/** Resolves which ChartConfig entry applies to a layerchart tooltip payload for label and color lookups. */
export function getPayloadConfigFromPayload(
	config: ChartConfig,
	payload: TooltipPayload,
	key: string,
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	data?: Record<string, any> | null
) {
	if (typeof payload !== "object" || payload === null) return undefined;

	const payloadConfig =
		"config" in payload && typeof payload.config === "object" && payload.config !== null
			? payload.config
			: undefined;

	let configLabelKey: string = key;

	if (payload.key === key) {
		configLabelKey = payload.key;
	} else if (payload.label === key) {
		configLabelKey = payload.label;
	} else if (key in payload && typeof payload[key as keyof typeof payload] === "string") {
		configLabelKey = payload[key as keyof typeof payload] as string;
	} else if (
		payloadConfig !== undefined &&
		key in payloadConfig &&
		typeof payloadConfig[key as keyof typeof payloadConfig] === "string"
	) {
		configLabelKey = payloadConfig[key as keyof typeof payloadConfig] as string;
	} else if (data != null && key in data && typeof data[key] === "string") {
		configLabelKey = data[key] as string;
	}

	return configLabelKey in config ? config[configLabelKey] : config[key as keyof typeof config];
}

type ChartContextValue = {
	config: ChartConfig;
};

const chartContextKey = Symbol("chart-context");

/** Called by chart container to expose config to descendant snippets. */
export function setChartContext(value: ChartContextValue) {
	return setContext(chartContextKey, value);
}

/** Reads chart config from Svelte context inside chart subcomponents. */
export function useChart() {
	return getContext<ChartContextValue>(chartContextKey);
}
