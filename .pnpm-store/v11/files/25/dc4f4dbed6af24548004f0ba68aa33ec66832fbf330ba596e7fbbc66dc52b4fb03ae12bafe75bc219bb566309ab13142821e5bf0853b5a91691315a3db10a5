// src/index.ts
import { definePlugin, PluginStyleSettings } from "@expressive-code/core";
var languageBadgeStyleSettings = new PluginStyleSettings({
  defaultValues: {
    languageBadge: {
      fontSize: "0.75rem",
      fontColor: "darkblue  ",
      fontWeight: "bold",
      background: "lightblue",
      borderRadius: "0.25rem",
      opacity: "1",
      borderWidth: "0px",
      borderColor: "transparent"
    }
  }
});
function pluginLanguageBadge(options = {}) {
  const config = {
    textTransform: "uppercase",
    excludeLanguages: [],
    languageMap: {
      cpp: "C++",
      ...options.languageMap
    },
    ...options
  };
  function remapLanguageLabel(lang, languageMap = {}) {
    return languageMap[lang] || lang;
  }
  return definePlugin({
    name: "Language Badge",
    styleSettings: languageBadgeStyleSettings,
    baseStyles: ({ cssVar }) => `
      [data-language]::before {
        content: attr(data-language);
        position: absolute;
        z-index: 2;
        right: 0.5rem;
        top: 0.5rem;
        padding: 0.1rem 0.5rem;
        font-family: "JetBrains Mono Variable", ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
        font-size: ${cssVar("languageBadge.fontSize")};
        font-weight: ${cssVar("languageBadge.fontWeight")};
        text-transform: ${config.textTransform};
        color: ${cssVar("languageBadge.fontColor")};
        background: ${cssVar("languageBadge.background")};
        opacity: ${cssVar("languageBadge.opacity")};
        border-radius: ${cssVar("languageBadge.borderRadius")};
        border-width: ${cssVar("languageBadge.borderWidth")};
        border-color: ${cssVar("languageBadge.borderColor")};
        border-style: solid;
        pointer-events: none;
        transition: opacity 0.3s;
      }
      .frame.has-title [data-language]::before,
      .frame.is-terminal [data-language]::before {
        top: 2.5rem;
      }
      .frame {
        @media (hover: none) {
          & [data-language]::before {
            opacity: 1;
            margin-right: 3rem;
          }
          & [data-language]:active::before {
            opacity: 0;
          }
        }
        @media (hover: hover) {
          & [data-language]::before {
            opacity: 1;
          }
          &:hover [data-language]::before {
            opacity: 0;
          }
        }
      }
    `,
    hooks: {
      postprocessRenderedBlock: async (context) => {
        const preElement = context.renderData.blockAst.children.find(
          (child) => child.type === "element" && child.tagName === "pre"
        );
        if (!preElement || preElement.type !== "element") return;
        const language = preElement.properties?.dataLanguage;
        if (!language) return;
        const originalLanguage = context.codeBlock.language;
        if (config.excludeLanguages.includes(originalLanguage)) {
          delete preElement.properties.dataLanguage;
          return;
        }
        const displayLabel = remapLanguageLabel(language, config.languageMap);
        preElement.properties.dataLanguage = displayLabel;
      }
    }
  });
}
export {
  pluginLanguageBadge
};
//# sourceMappingURL=index.js.map