import { env as globalEnv } from "cloudflare:workers";
import {
  compileImageConfig,
  isPrerender,
  cacheProviderEnabled
} from "virtual:astro-cloudflare:config";
import { createApp } from "astro/app/entrypoint";
import { setGetEnv } from "astro/env/setup";
import { createGetEnv } from "../utils/env.js";
import {
  isStaticPathsRequest,
  isPrerenderRequest,
  handleStaticPathsRequest,
  handlePrerenderRequest,
  isStaticImagesRequest,
  handleStaticImagesRequest,
  isImageTransformRequest,
  handleImageTransformRequest,
  installPrerenderErrorPropagation
} from "./prerender.js";
import {
  injectSessionBinding,
  matchStaticAsset,
  fallbackToAssets,
  createErrorPageFetch,
  createLocals,
  getClientAddress
} from "./cf.js";
setGetEnv(createGetEnv(globalEnv));
const app = createApp();
if (isPrerender) {
  installPrerenderErrorPropagation(app);
}
async function handle(request, env, context) {
  if (isPrerender) {
    if (compileImageConfig) {
      const { installAddStaticImage } = await import("./static-image-collection.js");
      installAddStaticImage(compileImageConfig);
    }
    if (isStaticPathsRequest(request)) {
      return handleStaticPathsRequest(app);
    }
    if (isPrerenderRequest(request)) {
      return handlePrerenderRequest(app, request);
    }
    if (isStaticImagesRequest(request)) {
      return handleStaticImagesRequest();
    }
    if (isImageTransformRequest(request)) {
      const imagesBindingName = globalThis.__ASTRO_IMAGES_BINDING_NAME;
      return handleImageTransformRequest(request, {
        images: compileImageConfig?.transformWithBinding && imagesBindingName ? env[imagesBindingName] : void 0,
        assets: compileImageConfig?.transformWithBinding ? env.ASSETS : void 0
      });
    }
  }
  injectSessionBinding(app.manifest, env);
  const staticAsset = matchStaticAsset(app.manifest, request.url, env);
  if (staticAsset) return staticAsset;
  let routeData = void 0;
  if (app.isDev()) {
    const result = await app.devMatch(app.getPathnameFromRequest(request));
    if (result) {
      routeData = result.routeData;
    }
  } else {
    routeData = app.match(request);
  }
  if (!routeData) {
    const asset = await fallbackToAssets(request.url, env);
    if (asset) return asset;
  }
  const locals = createLocals(context);
  const waitUntil = context.waitUntil.bind(context);
  let response = await app.render(request, {
    routeData,
    locals,
    waitUntil,
    prerenderedErrorPageFetch: createErrorPageFetch(env),
    clientAddress: getClientAddress(request)
  });
  const setCookieHeaders = app.setCookieHeaders ? [...app.setCookieHeaders(response)] : [];
  const needsNoStoreDefault = cacheProviderEnabled && !response.headers.has("Cloudflare-CDN-Cache-Control");
  if (setCookieHeaders.length > 0 || needsNoStoreDefault) {
    const applyHeaders = (res) => {
      for (const setCookieHeader of setCookieHeaders) {
        res.headers.append("Set-Cookie", setCookieHeader);
      }
      if (needsNoStoreDefault) {
        res.headers.set("Cloudflare-CDN-Cache-Control", "no-store");
      }
    };
    try {
      applyHeaders(response);
    } catch {
      response = new Response(response.body, response);
      applyHeaders(response);
    }
  }
  return response;
}
export {
  handle
};
