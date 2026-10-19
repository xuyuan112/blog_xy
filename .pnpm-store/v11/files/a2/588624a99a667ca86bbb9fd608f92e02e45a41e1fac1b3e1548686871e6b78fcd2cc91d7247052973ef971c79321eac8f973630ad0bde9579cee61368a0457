/* tslint:disable */
/* eslint-disable */

export function abiVersion(): number;

export function analysisFacts(source: string, options_json?: string | null): any;

export function analyze(source: string, options_json?: string | null): any;

export function analyzeDocument(source: string, options_json?: string | null, uri?: string | null): any;

export function analyzeDocumentFacts(source: string, options_json?: string | null, uri?: string | null): any;

export function analyzeJson(source: string, options_json?: string | null): any;

export function asciiCapabilities(): any;

export function asciiSupportedDiagrams(): any;

export function bindingCapabilities(): any;

export function diagramFamilyCapabilities(): any;

export function layoutJson(source: string, options_json?: string | null): string;

export function lintRuleCatalog(): any;

export function packageVersion(): string;

export function parseJson(source: string, options_json?: string | null): string;

export function renderAscii(source: string, options_json?: string | null): string;

export function renderSvg(source: string, options_json?: string | null): string;

export function selectedRegistryProfile(): string;

export function start(): void;

export function supportedDiagrams(): any;

export function supportedHostThemePresets(): any;

export function supportedThemes(): any;

export function validate(source: string, options_json?: string | null): any;

export type InitInput = RequestInfo | URL | Response | BufferSource | WebAssembly.Module;

export interface InitOutput {
    readonly memory: WebAssembly.Memory;
    readonly abiVersion: () => number;
    readonly analysisFacts: (a: number, b: number, c: number, d: number) => [number, number, number];
    readonly analyze: (a: number, b: number, c: number, d: number) => [number, number, number];
    readonly analyzeDocument: (a: number, b: number, c: number, d: number, e: number, f: number) => [number, number, number];
    readonly analyzeDocumentFacts: (a: number, b: number, c: number, d: number, e: number, f: number) => [number, number, number];
    readonly asciiCapabilities: () => [number, number, number];
    readonly asciiSupportedDiagrams: () => [number, number, number];
    readonly bindingCapabilities: () => [number, number, number];
    readonly diagramFamilyCapabilities: () => [number, number, number];
    readonly layoutJson: (a: number, b: number, c: number, d: number) => [number, number, number, number];
    readonly lintRuleCatalog: () => [number, number, number];
    readonly packageVersion: () => [number, number];
    readonly parseJson: (a: number, b: number, c: number, d: number) => [number, number, number, number];
    readonly renderAscii: (a: number, b: number, c: number, d: number) => [number, number, number, number];
    readonly renderSvg: (a: number, b: number, c: number, d: number) => [number, number, number, number];
    readonly selectedRegistryProfile: () => [number, number];
    readonly start: () => void;
    readonly supportedDiagrams: () => [number, number, number];
    readonly supportedHostThemePresets: () => [number, number, number];
    readonly supportedThemes: () => [number, number, number];
    readonly validate: (a: number, b: number, c: number, d: number) => [number, number, number];
    readonly analyzeJson: (a: number, b: number, c: number, d: number) => [number, number, number];
    readonly __wbindgen_malloc: (a: number, b: number) => number;
    readonly __wbindgen_realloc: (a: number, b: number, c: number, d: number) => number;
    readonly __wbindgen_free: (a: number, b: number, c: number) => void;
    readonly __wbindgen_externrefs: WebAssembly.Table;
    readonly __externref_table_dealloc: (a: number) => void;
    readonly __wbindgen_start: () => void;
}

export type SyncInitInput = BufferSource | WebAssembly.Module;

/**
 * Instantiates the given `module`, which can either be bytes or
 * a precompiled `WebAssembly.Module`.
 *
 * @param {{ module: SyncInitInput }} module - Passing `SyncInitInput` directly is deprecated.
 *
 * @returns {InitOutput}
 */
export function initSync(module: { module: SyncInitInput } | SyncInitInput): InitOutput;

/**
 * If `module_or_path` is {RequestInfo} or {URL}, makes a request and
 * for everything else, calls `WebAssembly.instantiate` directly.
 *
 * @param {{ module_or_path: InitInput | Promise<InitInput> }} module_or_path - Passing `InitInput` directly is deprecated.
 *
 * @returns {Promise<InitOutput>}
 */
export default function __wbg_init (module_or_path?: { module_or_path: InitInput | Promise<InitInput> } | InitInput | Promise<InitInput>): Promise<InitOutput>;
