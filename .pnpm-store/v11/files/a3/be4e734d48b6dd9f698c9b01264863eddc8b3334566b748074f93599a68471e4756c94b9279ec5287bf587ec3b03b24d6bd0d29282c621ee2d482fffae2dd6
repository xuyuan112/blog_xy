import { passthroughImageService } from "astro/config";
function normalizeImageServiceConfig(config) {
  if (!config || typeof config === "string") {
    const mode = config ?? "cloudflare-binding";
    return {
      buildService: mode,
      runtimeService: mode === "compile" ? "passthrough" : mode,
      // Only `compile` opts in to build-time transforms via the string shorthand.
      // String `cloudflare-binding` preserves the historical runtime-only behavior.
      transformAtBuild: mode === "compile"
    };
  }
  return {
    buildService: config.build,
    runtimeService: config.runtime ?? (config.build === "compile" ? "passthrough" : config.build),
    transformAtBuild: true
  };
}
const GENERIC_ENDPOINT = { entrypoint: "astro/assets/endpoint/generic" };
const CLOUDFLARE_PASSTHROUGH_ENDPOINT = {
  entrypoint: "@astrojs/cloudflare/image-passthrough-endpoint"
};
const WORKERD_IMAGE_SERVICE = { entrypoint: "@astrojs/cloudflare/image-service-workerd" };
const SHARP_IMAGE_SERVICE = "astro/assets/services/sharp";
function hasUserImageService(config) {
  return !!config.service?.entrypoint && config.service.entrypoint !== SHARP_IMAGE_SERVICE && config.service.entrypoint !== WORKERD_IMAGE_SERVICE.entrypoint;
}
function setImageConfig(service, config, command, logger) {
  const { buildService, runtimeService } = normalizeImageServiceConfig(service);
  switch (buildService) {
    case "passthrough":
      return {
        ...config,
        service: passthroughImageService(),
        endpoint: command === "dev" ? GENERIC_ENDPOINT : CLOUDFLARE_PASSTHROUGH_ENDPOINT
      };
    case "cloudflare":
      if (command === "dev") {
        return {
          ...config,
          service: passthroughImageService(),
          endpoint: GENERIC_ENDPOINT
        };
      }
      return {
        ...config,
        service: { entrypoint: "@astrojs/cloudflare/image-service" }
      };
    case "cloudflare-binding":
      return {
        ...config,
        service: WORKERD_IMAGE_SERVICE,
        endpoint: command === "dev" || runtimeService === "cloudflare-binding" ? { entrypoint: "@astrojs/cloudflare/image-transform-endpoint" } : CLOUDFLARE_PASSTHROUGH_ENDPOINT
      };
    case "compile": {
      const endpoint = command === "dev" || runtimeService === "cloudflare-binding" ? { entrypoint: "@astrojs/cloudflare/image-transform-endpoint" } : CLOUDFLARE_PASSTHROUGH_ENDPOINT;
      return {
        ...config,
        service: hasUserImageService(config) ? config.service : WORKERD_IMAGE_SERVICE,
        endpoint
      };
    }
    case "custom":
      if (command === "dev" && config.service.entrypoint === SHARP_IMAGE_SERVICE) {
        logger.warn(
          `The Sharp image service cannot run inside the workerd runtime, so '/_image' requests will fail in dev and production. Configure a workerd-compatible 'image.service', or set 'imageService' to 'compile' for build-time optimization. See https://docs.astro.build/en/guides/integrations-guide/cloudflare/#imageservice`
        );
      }
      return {
        ...config,
        // Astro's default dev endpoint imports `vite` and `node:fs`, which are
        // unavailable in workerd. Use the generic (fetch-based) endpoint instead.
        ...command === "dev" && !config.endpoint?.entrypoint && { endpoint: GENERIC_ENDPOINT }
      };
    default:
      if (config.service.entrypoint === "astro/assets/services/sharp") {
        logger.warn(
          `The current configuration does not support image optimization. To allow your project to build with the original, unoptimized images, the image service has been automatically switched to the 'passthrough' option. See https://docs.astro.build/en/reference/configuration-reference/#imageservice`
        );
        return {
          ...config,
          service: passthroughImageService(),
          ...command === "dev" && !config.endpoint?.entrypoint && { endpoint: GENERIC_ENDPOINT }
        };
      }
      return {
        ...config,
        ...command === "dev" && !config.endpoint?.entrypoint && { endpoint: GENERIC_ENDPOINT }
      };
  }
}
export {
  hasUserImageService,
  normalizeImageServiceConfig,
  setImageConfig
};
