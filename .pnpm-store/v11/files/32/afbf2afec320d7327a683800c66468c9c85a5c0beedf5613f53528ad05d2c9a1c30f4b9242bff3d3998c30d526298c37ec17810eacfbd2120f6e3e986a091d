import { imageConfig } from "astro:assets";
import { isRemotePath } from "@astrojs/internal-helpers/path";
import { isRemoteAllowed } from "@astrojs/internal-helpers/remote";
import { fetchWithRedirects } from "astro/assets";
const qualityTable = {
  low: 25,
  mid: 50,
  high: 80,
  max: 100
};
async function transformStream(body, params, images) {
  const supportedFormats = {
    jpeg: "image/jpeg",
    jpg: "image/jpeg",
    png: "image/png",
    gif: "image/gif",
    webp: "image/webp",
    avif: "image/avif"
  };
  const outputFormat = supportedFormats[params.get("f") ?? ""];
  if (!outputFormat) {
    return new Response(`Unsupported format: ${params.get("f")}`, { status: 400 });
  }
  return (await images.input(body).transform({
    width: params.has("w") ? Number.parseInt(params.get("w")) : void 0,
    height: params.has("h") ? Number.parseInt(params.get("h")) : void 0,
    fit: params.get("fit")
  }).output({
    quality: params.get("q") ? qualityTable[params.get("q")] ?? Number.parseInt(params.get("q")) : void 0,
    format: outputFormat
  })).response();
}
async function transform(rawUrl, images, assets) {
  const url = new URL(rawUrl);
  const href = url.searchParams.get("href");
  if (!href || isRemotePath(href) && !isRemoteAllowed(href, imageConfig)) {
    return new Response("Forbidden", { status: 403 });
  }
  const imageSrc = new URL(href, url.origin);
  let content;
  if (isRemotePath(href)) {
    try {
      content = await fetchWithRedirects({
        url: imageSrc,
        imageConfig
      });
      if (!isRemoteAllowed(content.url, imageConfig)) {
        return new Response("Forbidden", { status: 403 });
      }
    } catch {
      return new Response("Not Found", { status: 404 });
    }
  } else {
    content = await assets.fetch(imageSrc);
  }
  if (!content.body) {
    return new Response(null, { status: 404 });
  }
  return transformStream(content.body, url.searchParams, images);
}
export {
  transform,
  transformStream
};
