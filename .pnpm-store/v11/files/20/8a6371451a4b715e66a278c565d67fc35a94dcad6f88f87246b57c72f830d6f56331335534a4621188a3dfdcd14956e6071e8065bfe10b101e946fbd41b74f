import {
  buildCacheControlDirectives,
  collectInvalidationTags,
  pathTag,
  setConditionalHeaders
} from "astro/cache/provider-utils";
const factory = () => {
  return {
    name: "cloudflare",
    setHeaders(options, request) {
      const headers = new Headers();
      const directives = buildCacheControlDirectives(options, ["public"]);
      if (directives) {
        headers.set("Cloudflare-CDN-Cache-Control", directives);
      }
      const tags = [...options.tags ?? []];
      const { pathname } = new URL(request.url);
      tags.push(pathTag(pathname));
      headers.set("Cache-Tag", tags.join(","));
      setConditionalHeaders(headers, options);
      return headers;
    },
    async invalidate(options) {
      const { cache } = await import("cloudflare:workers");
      const tags = collectInvalidationTags(options);
      if (tags.length > 0) {
        await cache.purge({ tags });
      }
    }
  };
};
var provider_default = factory;
export {
  provider_default as default
};
