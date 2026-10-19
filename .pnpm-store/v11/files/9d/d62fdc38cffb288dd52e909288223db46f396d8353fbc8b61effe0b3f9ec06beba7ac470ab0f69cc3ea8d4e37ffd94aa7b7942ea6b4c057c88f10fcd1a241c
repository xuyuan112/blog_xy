import type { Plugin } from 'vite';
/**
 * A Rolldown plugin that extracts frontmatter from .astro files during
 * dependency optimization scanning. This allows Vite to discover imports
 * in the server-side frontmatter code.
 *
 * This is the Rolldown equivalent of the esbuild plugin in
 * `esbuild-plugin-astro-frontmatter.ts`, needed because Vite 8 uses Rolldown
 * for dependency optimization and ignores `optimizeDeps.esbuildOptions`.
 */
export declare function rolldownAstroFrontmatterScanPlugin(): Plugin;
