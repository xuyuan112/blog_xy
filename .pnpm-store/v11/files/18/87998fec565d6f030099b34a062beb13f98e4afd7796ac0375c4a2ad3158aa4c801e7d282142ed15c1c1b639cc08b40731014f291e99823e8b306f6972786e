export declare const SUPPORTED_THEMES: readonly ["default", "base", "dark", "forest", "neutral", "neo", "neo-dark", "redux", "redux-dark", "redux-color", "redux-dark-color"];
export type ThemeName = (typeof SUPPORTED_THEMES)[number];
export declare const SUPPORTED_HOST_THEME_PRESETS: readonly ["editor-light", "editor-dark", "one-dark", "gruvbox-light", "gruvbox-dark", "ayu-light", "ayu-dark"];
export type HostThemePresetName = (typeof SUPPORTED_HOST_THEME_PRESETS)[number];
export declare const SUPPORTED_DIAGRAMS: readonly ["architecture", "block", "c4", "class", "er", "flowchart", "gantt", "gitgraph", "info", "journey", "kanban", "mindmap", "packet", "pie", "quadrantchart", "radar", "requirement", "sankey", "sequence", "state", "timeline", "treemap", "venn", "xychart", "zenuml"];
export type DiagramType = (typeof SUPPORTED_DIAGRAMS)[number];
export declare const SUPPORTED_ASCII_DIAGRAMS: readonly ["class", "er", "flowchart", "gantt", "gitgraph", "journey", "kanban", "mindmap", "packet", "sequence", "state", "timeline", "treeView", "xychart", "zenuml"];
export type AsciiDiagramType = (typeof SUPPORTED_ASCII_DIAGRAMS)[number];
export declare const BINDING_STATUS_CODE_NAMES: readonly ["MERMAN_OK", "MERMAN_INVALID_ARGUMENT", "MERMAN_UTF8_ERROR", "MERMAN_OPTIONS_JSON_ERROR", "MERMAN_NO_DIAGRAM", "MERMAN_PARSE_ERROR", "MERMAN_RENDER_ERROR", "MERMAN_UNSUPPORTED_FORMAT", "MERMAN_PANIC", "MERMAN_INTERNAL_ERROR", "MERMAN_RESOURCE_LIMIT_EXCEEDED"];
export type BindingStatusCodeName = (typeof BINDING_STATUS_CODE_NAMES)[number];
export interface BindingErrorPayload {
    version: number;
    ok: false;
    code: number;
    code_name: BindingStatusCodeName | string;
    message: string;
}
export interface BindingCapabilities {
    render: boolean;
    analysis: boolean;
    ascii: boolean;
    core_full: boolean;
    core_host: boolean;
    elk_layout: boolean;
    ratex_math: boolean;
    editor_language: boolean;
    text_measurement: TextMeasurementCapabilities;
}
export interface TextMeasurementCapabilities {
    vendored: boolean;
    deterministic: boolean;
    host_callback: boolean;
    font_assets: boolean;
}
export type RegistryProfile = "full" | "tiny";
export interface DiagramFamilyCapability {
    diagram_type: string;
    metadata_id: DiagramType | null;
    has_semantic_parser: boolean;
    has_render_parser: boolean;
}
export type LintRuleSeverity = "error" | "warning" | "info" | "hint";
export type LintRuleCategory = "parse" | "semantic" | "config" | "resource" | "compatibility" | "layout" | "render" | "internal";
export type LintRuleProfile = "core" | "recommended" | "strict";
export type LintRuleOrigin = "mermaid_syntax" | "mermaid_compatibility" | "merman_authoring" | "merman_resource_policy" | "merman_internal";
export interface LintRuleCatalogEntry {
    id: string;
    description: string;
    evidence: string[];
    default_severity: LintRuleSeverity;
    category: LintRuleCategory;
    default_enabled: boolean;
    default_profile: LintRuleProfile;
    origin: LintRuleOrigin;
    configurable: boolean;
    fixable: boolean;
}
export interface LintRuleCatalogResponse {
    version: number;
    rules: LintRuleCatalogEntry[];
}
export interface LintRuleSeverityOverrideOptions {
    rule_id: string;
    severity: LintRuleSeverity;
}
export interface LintBindingOptions {
    profile?: LintRuleProfile;
    enable_rules?: string[];
    disable_rules?: string[];
    rule_severities?: LintRuleSeverityOverrideOptions[];
}
export type AsciiSupportLevel = "full" | "partial" | "summary" | "unsupported";
export type AsciiEvidenceKind = "mermaid_ascii_oracle" | "beautiful_mermaid_prior_art" | "local_semantic_probe" | "local_advantage" | "support_matrix" | "gap_registry";
export interface AsciiCapabilityEvidence {
    kind: AsciiEvidenceKind | string;
    source: string;
    note: string;
}
export interface AsciiCapability {
    diagram_type: AsciiDiagramType | string;
    display_name: string;
    support_level: AsciiSupportLevel;
    summary_fallback: boolean;
    supported_semantics: string[];
    limits: string[];
    evidence: AsciiCapabilityEvidence[];
}
export declare const CORE_BINDING_CAPABILITIES: BindingCapabilities;
export declare const RENDER_BINDING_CAPABILITIES: BindingCapabilities;
export declare const RENDER_ONLY_BINDING_CAPABILITIES: BindingCapabilities;
export declare const ASCII_BINDING_CAPABILITIES: BindingCapabilities;
export declare const FULL_BINDING_CAPABILITIES: BindingCapabilities;
export declare const DEFAULT_BINDING_CAPABILITIES: BindingCapabilities;
export declare function isThemeName(theme: string): theme is ThemeName;
export declare function isHostThemePresetName(preset: string): preset is HostThemePresetName;
export declare function isDiagramType(diagram: string): diagram is DiagramType;
export declare function isAsciiDiagramType(diagram: string): diagram is AsciiDiagramType;
export declare function isBindingStatusCodeName(codeName: string): codeName is BindingStatusCodeName;
export declare function isBindingErrorPayload(error: unknown): error is BindingErrorPayload;
export declare function normalizeThemeName(theme: string | null | undefined): ThemeName;
export declare function normalizeHostThemePresetName(preset: string | null | undefined): HostThemePresetName | null;
//# sourceMappingURL=public-catalog.d.ts.map