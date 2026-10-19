import * as root from "./index.js";
import { createMermanRuntimeState, withMermanRuntimeState, } from "./runtime-state.js";
export function bindSurfaceRuntime(surfaceLoader) {
    const state = createMermanRuntimeState(surfaceLoader);
    const withState = (run) => withMermanRuntimeState(state, run);
    return {
        initMerman(init) {
            if (typeof init === "function") {
                return withState(() => root.initMerman(init));
            }
            const options = init ?? {};
            return withState(() => root.initMerman({
                loader: surfaceLoader,
                ...options,
            }));
        },
        getMerman: () => withState(root.getMerman),
        isMermanInitialized: () => withState(root.isMermanInitialized),
        renderSvg: (source, options) => withState(() => root.renderSvg(source, options)),
        renderSvgWithTextMeasurer: (source, measurer, options) => withState(() => root.renderSvgWithTextMeasurer(source, measurer, options)),
        layoutJsonWithTextMeasurer: (source, measurer, options) => withState(() => root.layoutJsonWithTextMeasurer(source, measurer, options)),
        renderSvgElement: (source, options) => withState(() => root.renderSvgElement(source, options)),
        renderSvgToElement: (target, source, options) => withState(() => root.renderSvgToElement(target, source, options)),
        renderAscii: (source, options) => withState(() => root.renderAscii(source, options)),
        parseJson: (source, options) => withState(() => root.parseJson(source, options)),
        parseObject: (source, options) => withState(() => root.parseObject(source, options)),
        layoutJson: (source, options) => withState(() => root.layoutJson(source, options)),
        layoutObject: (source, options) => withState(() => root.layoutObject(source, options)),
        analyze: (source, options) => withState(() => root.analyze(source, options)),
        analyzeJson: (source, options) => withState(() => root.analyzeJson(source, options)),
        analysisFacts: (source, options) => withState(() => root.analysisFacts(source, options)),
        analyzeDocument: (source, options, uri) => withState(() => root.analyzeDocument(source, options, uri)),
        analyzeDocumentFacts: (source, options, uri) => withState(() => root.analyzeDocumentFacts(source, options, uri)),
        validate: (source, options) => withState(() => root.validate(source, options)),
        editorDiagnostics: (source, options, uri) => withState(() => root.editorDiagnostics(source, options, uri)),
        editorCodeActions: (source, options, uri) => withState(() => root.editorCodeActions(source, options, uri)),
        editorCompletions: (source, position, uri, options) => withState(() => root.editorCompletions(source, position, uri, options)),
        editorHover: (source, position, uri, options) => withState(() => root.editorHover(source, position, uri, options)),
        editorDocumentSymbols: (source, uri, options) => withState(() => root.editorDocumentSymbols(source, uri, options)),
        editorWorkspaceSymbols: (source, query, uri, options) => withState(() => root.editorWorkspaceSymbols(source, query, uri, options)),
        editorDefinition: (source, position, uri, options) => withState(() => root.editorDefinition(source, position, uri, options)),
        editorReferences: (source, position, includeDeclaration = true, uri, options) => withState(() => root.editorReferences(source, position, includeDeclaration, uri, options)),
        editorPrepareRename: (source, position, uri, options) => withState(() => root.editorPrepareRename(source, position, uri, options)),
        editorRename: (source, position, newName, uri, options) => withState(() => root.editorRename(source, position, newName, uri, options)),
        editorSemanticTokenLegend: () => withState(root.editorSemanticTokenLegend),
        editorSemanticTokens: (source, uri, options) => withState(() => root.editorSemanticTokens(source, uri, options)),
        bindingCapabilities: () => withState(root.bindingCapabilities),
        selectedRegistryProfile: () => withState(root.selectedRegistryProfile),
        supportedDiagrams: () => withState(root.supportedDiagrams),
        diagramFamilyCapabilities: () => withState(root.diagramFamilyCapabilities),
        lintRuleCatalog: () => withState(root.lintRuleCatalog),
        asciiSupportedDiagrams: () => withState(root.asciiSupportedDiagrams),
        asciiCapabilities: () => withState(root.asciiCapabilities),
        supportedThemes: () => withState(root.supportedThemes),
        supportedHostThemePresets: () => withState(root.supportedHostThemePresets),
        abiVersion: () => withState(root.abiVersion),
        packageVersion: () => withState(root.packageVersion),
    };
}
//# sourceMappingURL=surface-runtime.js.map