import type { AsciiCapability, AsciiDiagramType, DiagramFamilyCapability, DiagramType, HostThemePresetName, LintRuleCatalogEntry, MermanWasmLoader, MermanWasmModule, ThemeName } from "./index.js";
export interface MermanRuntimeState {
    defaultLoader: MermanWasmLoader;
    wasmModule: MermanWasmModule | null;
    initPromise: Promise<MermanWasmModule> | null;
    supportedDiagramsCache: DiagramType[] | null;
    asciiSupportedDiagramsCache: AsciiDiagramType[] | null;
    asciiCapabilitiesCache: AsciiCapability[] | null;
    diagramFamilyCapabilitiesCache: DiagramFamilyCapability[] | null;
    lintRuleCatalogCache: LintRuleCatalogEntry[] | null;
    supportedHostThemePresetsCache: HostThemePresetName[] | null;
    supportedThemesCache: ThemeName[] | null;
}
export declare function createMermanRuntimeState(defaultLoader: MermanWasmLoader): MermanRuntimeState;
export declare function currentMermanRuntimeState(defaultState: MermanRuntimeState): MermanRuntimeState;
export declare function withMermanRuntimeState<T>(state: MermanRuntimeState, run: () => T): T;
//# sourceMappingURL=runtime-state.d.ts.map