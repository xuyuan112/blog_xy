import type { ElementContent, Element, Properties } from 'hast';
import type { BlockquoteElement, CreateProperties, UserOptions, RequiredOptions, Callouts } from './types.js';
export declare const calloutRegex: RegExp;
export declare const splitByNewlineRegex: RegExp;
export declare const themes: {
    github: import("./types.js").DefaultCallouts;
    obsidian: import("./types.js").DefaultCallouts;
    vitepress: import("./types.js").DefaultCallouts;
    docusaurus: import("./types.js").DefaultCallouts;
};
export declare const defaultClassNames: {
    container: string;
    title: string;
    content: string;
    titleIcon: string;
    foldIcon: string;
    titleText: string;
};
/**
 * Call a function to get a return value or use the value.
 */
export declare function createIfNeeded(value: CreateProperties | Properties | null, node: BlockquoteElement, type: string): Properties | null;
/**
 * Constructs the configuration.
 */
export declare function getConfig(userOptions: UserOptions | undefined): RequiredOptions;
/**
 * Expands the original callouts object based on aliases.
 */
export declare function expandCallouts(callouts: Callouts, aliases: Record<string, string[]>): Record<string, string>;
/**
 * Cleanup due to double spaces after title in Markdown being
 * converted to <br> tags.
 */
export declare function handleBrAfterTitle(children: ElementContent[]): ElementContent[];
/**
 * Finds the index of the first text node containing a newline.
 */
export declare function findFirstNewline(children: ElementContent[]): number;
/**
 * Merges consecutive text nodes in a HAST children array
 * until the first non-text node is encountered.
 *
 * In Svelte, the AST will be:
 * ```
 * children: [
 *   { type: 'text', value: '[!note]', position: [Object] },
 *   { type: 'text', value: '- xxx', position: [Object] },
 * ]
 * ```
 * instead of:
 * ```
 * children: [
 *   { type: 'text', value: '[!note]- xxx', position: [Object] }
 * ]
 * ```
 * when markdown is: `![note]- xxx`
 */
export declare function mergeConsecutiveTextNodes(children: ElementContent[]): void;
/**
 * Merges user-defined `class` or `className` fields
 * with a default class name, returning a new properties object.
 */
export declare function getProperties(props: Properties | null, defaultClassName: string): Properties;
/**
 * Fetches a callout's visual indicator.
 */
export declare function getIndicator(callouts: Callouts, type: string, tag: string, props: Properties | null): Element | null;
/**
 * Get fold icon when callout is collapsible.
 */
export declare function getFoldIcon(tag: string, props: Properties | null): Element;
