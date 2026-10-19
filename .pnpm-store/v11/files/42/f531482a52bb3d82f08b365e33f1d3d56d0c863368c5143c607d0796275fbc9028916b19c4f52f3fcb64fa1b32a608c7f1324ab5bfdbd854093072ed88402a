const lineEndingRegex = /\r\n?|\n/g;
const openingMarkerRegex = /^(?<indent>[ \t]*)(?<marker>!{3}|\?{3})(?<open>\+?) ?(?<classes>[\w-]+(?: +[\w-]+)*)(?: +"(?<title>.*?)")? *$/;
const fenceRegex = /^[ \t]{0,3}(?<marker>`{3,}|~{3,})/;
function isFileLike(value) {
    return (typeof value === 'object' &&
        value !== null &&
        'toString' in value &&
        typeof value.toString === 'function');
}
function isPositionedNode(value) {
    return typeof value === 'object' && value !== null;
}
function isListItemPositionedNode(value) {
    return (value.type === 'listItem' &&
        typeof value.position?.start?.line === 'number' &&
        typeof value.position?.end?.line === 'number');
}
function getFileText(file) {
    if (!isFileLike(file))
        return undefined;
    if (typeof file.value === 'string')
        return file.value;
    if (file.value instanceof Uint8Array) {
        return new TextDecoder().decode(file.value);
    }
    const content = file.toString();
    return content.length > 0 ? content : undefined;
}
function splitLines(value) {
    const lines = [];
    let start = 0;
    let line = 1;
    for (const match of value.matchAll(lineEndingRegex)) {
        const { index } = match;
        lines.push({
            value: value.slice(start, index),
            ending: match[0],
            line,
        });
        start = index + match[0].length;
        line += 1;
    }
    if (start < value.length) {
        lines.push({ value: value.slice(start), ending: '', line });
    }
    return lines;
}
function leadingWhitespace(value) {
    return /^[ \t]*/.exec(value)?.[0] ?? '';
}
function indentationSize(value) {
    let size = 0;
    for (const character of value) {
        size += character === '\t' ? 4 : 1;
    }
    return size;
}
function isBlank(value) {
    return /^[ \t]*$/.test(value);
}
function stripContainerIndent(value, indent) {
    return indent.length > 0 && value.startsWith(indent)
        ? value.slice(indent.length)
        : value;
}
function sharedWhitespacePrefix(left, right) {
    let index = 0;
    while (index < left.length &&
        index < right.length &&
        left[index] === right[index]) {
        index += 1;
    }
    return left.slice(0, index);
}
function getBodyIndent(lines) {
    let indent;
    for (const line of lines) {
        if (isBlank(line.value))
            continue;
        const whitespace = leadingWhitespace(line.value);
        indent =
            indent === undefined
                ? whitespace
                : sharedWhitespacePrefix(indent, whitespace);
        if (indent.length === 0)
            break;
    }
    return indent ?? '';
}
function stripBodyIndent(value, indent) {
    return indent.length > 0 && value.startsWith(indent)
        ? value.slice(indent.length)
        : value;
}
function parseOpening(value) {
    const match = openingMarkerRegex.exec(value);
    if (!match?.groups)
        return undefined;
    const [type] = match.groups.classes.toLowerCase().split(' ');
    const { title } = match.groups;
    return {
        indent: match.groups.indent,
        marker: match.groups.marker,
        open: match.groups.open,
        type,
        title: title === undefined || title === '' ? undefined : title,
    };
}
function getCalloutOpening(opening) {
    const collapsible = opening.marker === '!!!' ? '' : opening.open === '+' ? '+' : '-';
    const title = opening.title === undefined ? '' : ` ${opening.title}`;
    return `> [!${opening.type}]${collapsible}${title}`;
}
function toBlockquoteLine(value) {
    return value.length > 0 ? `> ${value}` : '>';
}
function isListItemLine(line, listItemLines) {
    return listItemLines.has(line);
}
function getOutputIndent(line, opening, listItemLines) {
    return isListItemLine(line.line, listItemLines) ? opening.indent : '';
}
function findAdmonitionEnd(lines, start, opening) {
    const openingIndentSize = indentationSize(opening.indent);
    let end = start + 1;
    for (; end < lines.length; end += 1) {
        const { value } = lines[end];
        if (isBlank(value))
            continue;
        const indent = leadingWhitespace(value);
        const indentSize = indentationSize(indent);
        if (indentSize <= openingIndentSize)
            break;
    }
    return end;
}
function transformAdmonition(lines, start, opening, listItemLines) {
    const end = findAdmonitionEnd(lines, start, opening);
    let bodyEnd = end;
    while (bodyEnd > start + 1 && isBlank(lines[bodyEnd - 1].value)) {
        bodyEnd -= 1;
    }
    const body = lines.slice(start + 1, bodyEnd).map((line) => ({
        value: stripContainerIndent(line.value, opening.indent),
        ending: line.ending,
        line: line.line,
    }));
    const bodyIndent = getBodyIndent(body);
    const normalizedBody = body.map((line) => ({
        value: stripBodyIndent(line.value, bodyIndent),
        ending: line.ending,
        line: line.line,
    }));
    const transformedBody = transformLines(normalizedBody, listItemLines);
    const outputIndent = getOutputIndent(lines[start], opening, listItemLines);
    lines.splice(start, bodyEnd - start, {
        value: `${outputIndent}${getCalloutOpening(opening)}`,
        ending: lines[start].ending,
        line: lines[start].line,
    }, ...transformedBody.map((line) => ({
        value: `${outputIndent}${toBlockquoteLine(line.value)}`,
        ending: line.ending,
        line: line.line,
    })));
    return start + transformedBody.length + 1;
}
function linesToMarkdown(lines) {
    return lines.map((line) => line.value + line.ending).join('');
}
function transformLines(lines, listItemLines) {
    let fence;
    for (let index = 0; index < lines.length;) {
        const fenceMatch = fenceRegex.exec(lines[index].value);
        if (fence) {
            if (fenceMatch?.groups?.marker.startsWith(fence)) {
                fence = undefined;
            }
            index += 1;
            continue;
        }
        if (fenceMatch?.groups) {
            fence = fenceMatch.groups.marker[0];
            index += 1;
            continue;
        }
        const opening = parseOpening(lines[index].value);
        if (!opening) {
            index += 1;
            continue;
        }
        index = transformAdmonition(lines, index, opening, listItemLines);
    }
    return lines;
}
function collectListItemLines(node, lines = new Set()) {
    if (!isPositionedNode(node))
        return lines;
    if (isListItemPositionedNode(node)) {
        const { position: { start: { line: startLine }, end: { line: endLine }, }, } = node;
        for (let line = startLine; line <= endLine; line += 1) {
            lines.add(line);
        }
    }
    if (Array.isArray(node.children)) {
        for (const child of node.children)
            collectListItemLines(child, lines);
    }
    return lines;
}
function transformMarkdown(value, tree) {
    const lines = splitLines(value);
    transformLines(lines, collectListItemLines(tree));
    return linesToMarkdown(lines);
}
function replaceRoot(tree, nextTree) {
    tree.children = nextTree.children;
    tree.position = nextTree.position;
}
function isRoot(value) {
    return (typeof value === 'object' &&
        value !== null &&
        'type' in value &&
        value.type === 'root' &&
        'children' in value &&
        Array.isArray(value.children));
}
/**
 * Convert Python-Markdown and MkDocs Material admonitions to blockquote callouts.
 *
 * @see https://github.com/lin-stephanie/remark-admonition-to-blockquote-callout
 */
const remarkAdmonitionToBlockquoteCallout = function () {
    return (tree, file) => {
        const markdown = getFileText(file);
        if (!markdown)
            return;
        const nextMarkdown = transformMarkdown(markdown, tree);
        if (nextMarkdown === markdown)
            return;
        const nextTree = this.parse(nextMarkdown);
        if (isRoot(nextTree))
            replaceRoot(tree, nextTree);
    };
};
export default remarkAdmonitionToBlockquoteCallout;
