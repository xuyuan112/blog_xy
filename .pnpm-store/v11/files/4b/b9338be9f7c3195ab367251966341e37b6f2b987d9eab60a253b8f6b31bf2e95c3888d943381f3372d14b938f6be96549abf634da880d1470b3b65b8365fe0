import type { AstroConfig, AstroIntegrationLogger, HookParameters } from 'astro';
export type ImageServiceMode = 'passthrough' | 'cloudflare' | 'cloudflare-binding' | 'compile' | 'custom';
export type ImageServiceConfig = ImageServiceMode | {
    build: 'compile';
    runtime?: 'passthrough' | 'cloudflare-binding';
} | {
    build: 'cloudflare-binding';
    runtime?: 'cloudflare-binding' | 'passthrough';
};
/** Normalize string | compound config into separate build/runtime modes.
 *  `transformAtBuild` is true when the compound config is used; this opts the user
 *  in to build-time image optimization. The string form preserves runtime-only behavior. */
export declare function normalizeImageServiceConfig(config: ImageServiceConfig | undefined): {
    buildService: ImageServiceMode;
    runtimeService: ImageServiceMode;
    transformAtBuild: boolean;
};
export declare function hasUserImageService(config: AstroConfig['image']): boolean;
export declare function setImageConfig(service: ImageServiceConfig | undefined, config: AstroConfig['image'], command: HookParameters<'astro:config:setup'>['command'], logger: AstroIntegrationLogger): {
    service: {
        entrypoint: string;
    };
    endpoint: {
        entrypoint: string;
    };
    dangerouslyProcessSVG: boolean;
    domains: string[];
    remotePatterns: {
        protocol?: string | undefined;
        hostname?: string | undefined;
        port?: string | undefined;
        pathname?: string | undefined;
    }[];
    responsiveStyles: boolean;
    layout?: "fixed" | "none" | "constrained" | "full-width" | undefined;
    objectFit?: string | undefined;
    objectPosition?: string | undefined;
    breakpoints?: number[] | undefined;
} | {
    endpoint: {
        entrypoint: string;
    } | {
        route: string;
        entrypoint?: string | undefined;
    };
    service: import("astro").ImageServiceConfig<Record<string, any>>;
    dangerouslyProcessSVG: boolean;
    domains: string[];
    remotePatterns: {
        protocol?: string | undefined;
        hostname?: string | undefined;
        port?: string | undefined;
        pathname?: string | undefined;
    }[];
    responsiveStyles: boolean;
    layout?: "fixed" | "none" | "constrained" | "full-width" | undefined;
    objectFit?: string | undefined;
    objectPosition?: string | undefined;
    breakpoints?: number[] | undefined;
};
