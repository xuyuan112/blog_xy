import * as _expressive_code_core from '@expressive-code/core';

interface LanguageBadgePluginOptions {
    /** Mapping of language identifiers to display labels. Default: `{ cpp: 'C++', sh: 'bash' }` */
    languageMap?: Record<string, string>;
    /** Text transform for language labels. Default: `'uppercase'` */
    textTransform?: 'uppercase' | 'lowercase';
    /** Array of languages to exclude from showing badges. Default: `[]` */
    excludeLanguages?: string[];
}
/**
 * Creates an Expressive Code plugin that adds language badge functionality to code blocks
 */
declare function pluginLanguageBadge(options?: LanguageBadgePluginOptions): _expressive_code_core.ExpressiveCodePlugin;
interface LanguageBadgeStyleSettings {
    /** The font size for the language badge. Default: `'0.75rem'` */
    fontSize: string;
    /** The font color for the language badge. Default: `'da'` */
    fontColor: string;
    /** The font weight for the language badge. Default: `'bold'` */
    fontWeight: string;
    /** The background color for the language badge. Default: `'oklch(0.33 0.035 var(--hue))'` */
    background: string;
    /** The border radius for the language badge. Default: `'0.5rem'` */
    borderRadius: string;
    /** The opacity for the language badge. Default: `'1'` */
    opacity: string;
    /** The border width for the language badge. Default: `'1px'` */
    borderWidth: string;
    /** The border color for the language badge. Default: `'black'` */
    borderColor: string;
}
declare module "@expressive-code/core" {
    interface StyleSettings {
        /** Style overrides for the language badge plugin. */
        languageBadge: LanguageBadgeStyleSettings;
    }
}

export { pluginLanguageBadge };
