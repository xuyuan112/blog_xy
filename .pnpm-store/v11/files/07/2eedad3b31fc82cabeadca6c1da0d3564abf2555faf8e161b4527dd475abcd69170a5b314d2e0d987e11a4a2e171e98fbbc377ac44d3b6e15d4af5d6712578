import { readFile } from "node:fs/promises";
const FRONTMATTER_RE = /^---\r?\n([\s\S]*?)\r?\n---/;
const RETURN_REPLACE_RE = /(\/\/[^\n]*|\/\*[\s\S]*?\*\/|`(?:[^`\\]|\\.)*`|"(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')|(?<!\.)\breturn(\s*;|\b)/g;
function replaceTopLevelReturns(code) {
  return code.replace(RETURN_REPLACE_RE, (_match, skip, tail) => {
    if (skip !== void 0) return skip;
    return tail.trim() === ";" ? "throw 0;" : "throw ";
  });
}
function rolldownAstroFrontmatterScanPlugin() {
  return {
    name: "astro-frontmatter-scan",
    async load(id) {
      if (!id.endsWith(".astro")) return;
      let code;
      try {
        code = await readFile(id, "utf-8");
      } catch {
        return { code: "export default {}", moduleType: "ts" };
      }
      const frontmatterMatch = FRONTMATTER_RE.exec(code);
      if (frontmatterMatch) {
        const contents = replaceTopLevelReturns(frontmatterMatch[1]);
        return {
          code: contents + "\nexport default {}",
          moduleType: "ts"
        };
      }
      return { code: "export default {}", moduleType: "ts" };
    }
  };
}
export {
  rolldownAstroFrontmatterScanPlugin
};
