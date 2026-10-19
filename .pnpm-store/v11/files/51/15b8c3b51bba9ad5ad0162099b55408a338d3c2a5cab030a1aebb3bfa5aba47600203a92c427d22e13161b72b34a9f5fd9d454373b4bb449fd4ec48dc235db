export const SUPPORTED_THEMES = [
    "default",
    "base",
    "dark",
    "forest",
    "neutral",
    "neo",
    "neo-dark",
    "redux",
    "redux-dark",
    "redux-color",
    "redux-dark-color",
];
export const SUPPORTED_HOST_THEME_PRESETS = [
    "editor-light",
    "editor-dark",
    "one-dark",
    "gruvbox-light",
    "gruvbox-dark",
    "ayu-light",
    "ayu-dark",
];
export const SUPPORTED_DIAGRAMS = [
    "architecture",
    "block",
    "c4",
    "class",
    "er",
    "flowchart",
    "gantt",
    "gitgraph",
    "info",
    "journey",
    "kanban",
    "mindmap",
    "packet",
    "pie",
    "quadrantchart",
    "radar",
    "requirement",
    "sankey",
    "sequence",
    "state",
    "timeline",
    "treemap",
    "venn",
    "xychart",
    "zenuml",
];
export const SUPPORTED_ASCII_DIAGRAMS = [
    "class",
    "er",
    "flowchart",
    "gantt",
    "gitgraph",
    "journey",
    "kanban",
    "mindmap",
    "packet",
    "sequence",
    "state",
    "timeline",
    "treeView",
    "xychart",
    "zenuml",
];
export const BINDING_STATUS_CODE_NAMES = [
    "MERMAN_OK",
    "MERMAN_INVALID_ARGUMENT",
    "MERMAN_UTF8_ERROR",
    "MERMAN_OPTIONS_JSON_ERROR",
    "MERMAN_NO_DIAGRAM",
    "MERMAN_PARSE_ERROR",
    "MERMAN_RENDER_ERROR",
    "MERMAN_UNSUPPORTED_FORMAT",
    "MERMAN_PANIC",
    "MERMAN_INTERNAL_ERROR",
    "MERMAN_RESOURCE_LIMIT_EXCEEDED",
];
export const CORE_BINDING_CAPABILITIES = bindingCapabilities({
    render: false,
    analysis: true,
    ascii: false,
    core_full: false,
    core_host: false,
    elk_layout: false,
    ratex_math: false,
    editor_language: false,
});
export const RENDER_BINDING_CAPABILITIES = bindingCapabilities({
    render: true,
    analysis: true,
    ascii: false,
    core_full: false,
    core_host: false,
    elk_layout: false,
    ratex_math: false,
    editor_language: false,
});
export const RENDER_ONLY_BINDING_CAPABILITIES = bindingCapabilities({
    render: true,
    analysis: false,
    ascii: false,
    core_full: false,
    core_host: false,
    elk_layout: false,
    ratex_math: false,
    editor_language: false,
});
export const ASCII_BINDING_CAPABILITIES = bindingCapabilities({
    render: false,
    analysis: false,
    ascii: true,
    core_full: false,
    core_host: false,
    elk_layout: false,
    ratex_math: false,
    editor_language: false,
});
export const FULL_BINDING_CAPABILITIES = bindingCapabilities({
    render: true,
    analysis: true,
    ascii: true,
    core_full: true,
    core_host: true,
    elk_layout: true,
    ratex_math: false,
    editor_language: true,
});
export const DEFAULT_BINDING_CAPABILITIES = FULL_BINDING_CAPABILITIES;
function bindingCapabilities(flags) {
    return {
        ...flags,
        text_measurement: {
            vendored: flags.render,
            deterministic: flags.render,
            host_callback: flags.render,
            font_assets: false,
        },
    };
}
export function isThemeName(theme) {
    return SUPPORTED_THEMES.includes(theme);
}
export function isHostThemePresetName(preset) {
    return SUPPORTED_HOST_THEME_PRESETS.includes(preset);
}
export function isDiagramType(diagram) {
    return SUPPORTED_DIAGRAMS.includes(diagram);
}
export function isAsciiDiagramType(diagram) {
    return SUPPORTED_ASCII_DIAGRAMS.includes(diagram);
}
export function isBindingStatusCodeName(codeName) {
    return BINDING_STATUS_CODE_NAMES.includes(codeName);
}
export function isBindingErrorPayload(error) {
    if (!error || typeof error !== "object") {
        return false;
    }
    const payload = error;
    return (payload.ok === false &&
        typeof payload.version === "number" &&
        typeof payload.code === "number" &&
        typeof payload.code_name === "string" &&
        typeof payload.message === "string");
}
export function normalizeThemeName(theme) {
    return theme && isThemeName(theme) ? theme : "default";
}
export function normalizeHostThemePresetName(preset) {
    return preset && isHostThemePresetName(preset) ? preset : null;
}
//# sourceMappingURL=public-catalog.js.map