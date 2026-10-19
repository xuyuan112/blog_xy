# remark-admonition-to-blockquote-callout

[![version][version-badge]][version-link]
[![codecov][coverage-badge]][coverage]
[![npm downloads][npm-downloads-src]][npm-downloads-href]
[![jsDocs.io][jsdocs-src]][jsdocs-href]

A [remark](https://github.com/remarkjs/remark) plugin for converting [Python-Markdown admonitions](https://python-markdown.github.io/extensions/admonition/) and [MkDocs Material admonitions](https://squidfunk.github.io/mkdocs-material/reference/admonitions/) to [blockquote callouts](https://help.obsidian.md/Editing+and+formatting/Callouts).

## What is this?

This plugin lets source files keep Python-Markdown and MkDocs Material admonition syntax while converting it to blockquote callout syntax:

- Basic and titled admonitions: `!!! note` -> `> [!note]`, `!!! note "Title"` -> `> [!note] Title`.
- Markdown titles are preserved: `!!! note "A **bold** title"` -> `> [!note] A **bold** title`.
- Explicit empty titles are treated the same as omitted titles: `!!! note ""` -> `> [!note]`.
- Collapsible blocks: `??? note` -> `> [!note]-`, `???+ note` -> `> [!note]+`.
- Nested admonitions become nested blockquotes: `!!! note` -> `> > [!note]`.
- Extra classes are ignored after the type: `!!! danger highlight blink "Title"` -> `> [!danger] Title`.
- Inline modifiers are ignored: `!!! info inline "Title"` -> `> [!info] Title`, `!!! info inline end "Title"` -> `> [!info] Title`.
- MkDocs Material and custom types are kept as callout types.
- Fenced code blocks inside admonitions are preserved.
- Admonitions inside list items keep their list-item indentation.

## When should I use this?

Use this plugin when your Markdown content is written with Python-Markdown or MkDocs Material admonition syntax, but the rest of your unified pipeline expects blockquote-based callouts.

This is especially useful when migrating documentation to a Markdown/MDX site powered by frameworks such as Astro or Next.js: keep Python-Markdown admonition syntax in source files, convert it in remark, and consider [`rehype-callouts`](https://github.com/lin-stephanie/rehype-callouts) if you also need to [render the generated blockquote-based callouts as HTML](#example-render-python-markdown-admonitions-as-html).

## Installation

This package is [ESM only](https://gist.github.com/sindresorhus/a39789f98801d908bbc7ff3ecc99d99c). In Node.js (version 16+), install with your package manager:

```sh
npm install remark-admonition-to-blockquote-callout
yarn add remark-admonition-to-blockquote-callout
pnpm add remark-admonition-to-blockquote-callout
```

In Deno with [`esm.sh`](https://esm.sh/):

```js
import remarkAdmonitionToBlockquoteCallout from 'https://esm.sh/remark-admonition-to-blockquote-callout'
```

In browsers with [`esm.sh`](https://esm.sh/):

```html
<script type="module">
  import remarkAdmonitionToBlockquoteCallout from 'https://esm.sh/remark-admonition-to-blockquote-callout?bundle'
</script>
```

## Usage

Import and use the plugin **before other remark plugins whose mdast changes must be preserved**.

Say `example.md` contains:

```md
!!! note
    Basic content.

!!! info "Custom **title**"
    Markdown title content.

???+ tip inline "Open"
    Open by default.

    ```js
    const marker = "!!! note";
    ```

    !!! success ""
        Nested empty-title admonition.

??? failure inline end "Closed"
    Closed by default.

* In a list item:

    !!! custom highlight
        Extra classes are ignored.
```

And module `example.js` contains:

```js
import { remark } from 'remark'
import remarkAdmonitionToBlockquoteCallout from 'remark-admonition-to-blockquote-callout'
import { read } from 'to-vfile'

const file = await remark()
  .use(remarkAdmonitionToBlockquoteCallout)
  .process(await read('example.md'))

// `remark` escapes callout markers as `\[!type]` for safe Markdown output,
// preventing `[!type]` from being parsed as a link reference. Restore only
// blockquote callouts for Markdown text; AST-based unified transforms do not need it.
const output = String(file).replaceAll(
  /^(\s*>(?: >)*\s*)\\(\[![^\]\n]+])/gm,
  '$1$2'
)

console.log(output)
```

Then running `node example.js` yields:

```md
> [!note]
> Basic content.

> [!info] Custom **title**
> Markdown title content.

> [!tip]+ Open
> Open by default.
>
> ```js
> const marker = "!!! note";
> ```
>
> > [!success]
> > Nested empty-title admonition.

> [!failure]- Closed
> Closed by default.

* In a list item:

  > [!custom]
  > Extra classes are ignored.
```

> [!warning]
> When this plugin converts admonitions, it reparses the transformed Markdown and replaces the current mdast root. Place it before remark plugins that mutate mdast if those mutations must be preserved.
>
> When writing admonitions, avoid mixing spaces and tabs for indentation. Mixed indentation can make admonition boundaries ambiguous and may prevent correct conversion.

> [!note]
> This plugin is intentionally lenient when collecting admonition bodies. After an opening marker is found, following lines with deeper indentation are treated as body content, even if they do not follow the indentation rules required by Python-Markdown.
>
> For example, both of these inputs are converted:
>
> ```md
> !!! note
>   This is a note.
>
> !!! note
>        This is a note.
> ```
>
> ```md
> > [!note]
> > This is a note.
>
> > [!note]
> > This is a note.
> ```

## API

This package exports no identifiers. The default export is `remarkAdmonitionToBlockquoteCallout`.

### `unified().use(remarkAdmonitionToBlockquoteCallout)`

Used to convert Python-Markdown and MkDocs Material admonitions to blockquote callouts.

###### Parameters

There are no options.

###### Returns

Transform ([`Transformer`](https://github.com/unifiedjs/unified#transformer)).

## Example

### Example: Render Python-Markdown Admonitions as HTML

This plugin only converts admonition Markdown syntax to blockquote callouts; use [`rehype-callouts`](https://github.com/lin-stephanie/rehype-callouts) later in the pipeline when those callouts should be rendered as HTML.

Module `example.js` contains:

```js
import { unified } from 'unified'
import remarkParse from 'remark-parse'
import remarkAdmonitionToBlockquoteCallout from 'remark-admonition-to-blockquote-callout'
import remarkRehype from 'remark-rehype'
import rehypeCalloouts from 'rehype-callouts'
import rehypeStringify from 'rehype-stringify'
import { readSync } from 'to-vfile'

const processor = unified()
  .use(remarkParse)
  .use(remarkAdmonitionToBlockquoteCallout)
  .use(remarkRehype)
  .use(rehypeCalloouts, {
    callouts: {
      custom: { title: 'Custom Type' },
    },
  })
  .use(rehypeStringify)

const output = String(
  processor.processSync(readSync('example.md'))
)

console.log(output)
```

Then running `node example.js` yields:

```html
<div class="callout" data-callout="note" data-collapsible="false">
  <div class="callout-title">
    <div class="callout-title-icon" aria-hidden="true">
      <!-- svg icon-->
    </div>
    <div class="callout-title-text">Note</div>
  </div>
  <div class="callout-content"><p>Basic content.</p></div>
</div>
<div class="callout" data-callout="info" data-collapsible="false">
  <div class="callout-title">
    <div class="callout-title-icon" aria-hidden="true">
      <!-- svg icon-->
    </div>
    <div class="callout-title-text">Custom <strong>title</strong></div>
  </div>
  <div class="callout-content"><p>Markdown title content.</p></div>
</div>
<details class="callout" data-callout="tip" data-collapsible="true" open>
  <summary class="callout-title">
    <div class="callout-title-icon" aria-hidden="true">
      <!-- svg icon-->
    </div>
    <div class="callout-title-text">Open</div>
    <div class="callout-fold-icon" aria-hidden="true">
      <!-- svg icon-->, 
    </div>
  </summary>
  <div class="callout-content">
    <p>Open by default.</p>
    <pre><code class="language-js">const marker = "!!! note";</code></pre>
    <div class="callout" data-callout="success" data-collapsible="false">
      <div class="callout-title">
        <div class="callout-title-icon" aria-hidden="true">
          <!-- svg icon-->
        </div>
        <div class="callout-title-text">Success</div>
      </div>
      <div class="callout-content"><p>Nested empty-title admonition.</p></div>
    </div>
  </div>
</details>
<details class="callout" data-callout="failure" data-collapsible="true">
  <summary class="callout-title">
    <div class="callout-title-icon" aria-hidden="true">
      <!-- svg icon-->
    </div>
    <div class="callout-title-text">Closed</div>
    <div class="callout-fold-icon" aria-hidden="true">
      <!-- svg icon-->
    </div>
  </summary>
  <div class="callout-content"><p>Closed by default.</p></div>
</details>
<ul>
  <li>
    <p>In a list item:</p>
    <div class="callout" data-callout="custom" data-collapsible="false">
      <div class="callout-title">
        <div class="callout-title-text">Custom Type</div>
      </div>
      <div class="callout-content"><p>Extra classes are ignored.</p></div>
    </div>
  </li>
</ul>
```

## Types

This package is fully typed with [TypeScript](https://www.typescriptlang.org/). It exports no additional public types.

## Contribution

If you see any errors or room for improvement on this plugin, feel free to open an [issues](https://github.com/lin-stephanie/remark-admonition-to-blockquote-callout/issues) or [pull request](https://github.com/lin-stephanie/remark-admonition-to-blockquote-callout/pulls) . Thank you in advance for contributing!

## License

[MIT](https://github.com/lin-stephanie/remark-admonition-to-blockquote-callout/blob/main/LICENSE) © 2026-PRESENT [Stephanie Lin](https://github.com/lin-stephanie)

<!-- Badges -->

[version-badge]: https://img.shields.io/github/v/release/lin-stephanie/remark-admonition-to-blockquote-callout?label=release&style=flat&colorA=080f12&colorB=f87171
[version-link]: https://github.com/lin-stephanie/remark-admonition-to-blockquote-callout/releases
[coverage-badge]: https://img.shields.io/codecov/c/github/lin-stephanie/remark-admonition-to-blockquote-callout?style=flat&colorA=080f12&colorB=f87171
[coverage]: https://codecov.io/github/lin-stephanie/remark-admonition-to-blockquote-callout
[npm-downloads-src]: https://img.shields.io/npm/dm/remark-admonition-to-blockquote-callout?style=flat&colorA=080f12&colorB=f87171
[npm-downloads-href]: https://npmjs.com/package/remark-admonition-to-blockquote-callout
[jsdocs-src]: https://img.shields.io/badge/jsdocs-reference-080f12?style=flat&colorA=080f12&colorB=f87171
[jsdocs-href]: https://www.jsdocs.io/package/remark-admonition-to-blockquote-callout
