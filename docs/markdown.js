class MarkdownParserElement extends HTMLElement {
	constructor() {
		super();
	}

	render() {
		let unorderedListRegexp = /^- (.*)$/;
		let orderedListRegexp = /^(\d+)\. (.*)$/;

		let codeBlockCounter = 0;

		let italicRegexp = /(\*)(.*?)\1/;
		let boldRegexp = /(\*\*)(.*?)\1/;
		let strikethroughRegexp = /(\~\~)(.*?)\1/;
		let underlineRegexp = /(\+\+)(.*?)\1/;
		let inlineCodeRegexp = /(\`)(.*?)\1/;

		let imgRegexp = /!\[(.*?)\]\{(.*?)\}\{(.*?)\}\((.*?)\)/;
		let imgClassRegexp = /!\[(.*?)\]\{(.*?)\}\((.*?)\)/;
		let linkRegexp = /\[(.*?)\]\((.*?)\)/;

        let mathRegexp = /\$\$(.*?)\$\$/;

		let headerCounter = [];
		let tableOfContents = [];

		let tocAttrValue = this.getAttribute("table-of-contents-title");
		let tableOfContentsTitle = "";
		if (tocAttrValue != null) {
			tableOfContentsTitle = `<h1>${tocAttrValue}</h1><quote-element>Table of Contents</quote-element>`;
		} else {
			tableOfContentsTitle = `<h1>Table of Contents</h1>`;
		}

		let openParagraph = false;
		let anyTagRegexp = /<.*?>/;

		// Close paragraph if needed
		function CPIN() {
			if (openParagraph) {
				openParagraph = false;
				return "</p>";
			}
			return "";
		}

		let text = this.innerHTML
			.split("\n")
			.map((line) => line.trim())
			.map((line) => {
				if (line.length == 0) { 
					openParagraph = false;
					return "<br>";
				}

				for (let i = 6; i > 0; i--) {
					if (line.startsWith("#".repeat(i))) {
						headerCounter[i] = headerCounter[i] ?? 0;
						let index = "";
						for (let j = 1; j <= 6; j++) {
							if (j == i) {
								headerCounter[j]++;
								for (let k = j + 1; k <= 6; k++) {
									headerCounter[k] = 0;
								}
							}
							if (headerCounter[j] == undefined) headerCounter[j] = 0;
							if (j <= i) index += `${headerCounter[j]}.`;
						}
						index = index.slice(0, -1);
						tableOfContents.push(
							`<fake-link style="margin-top: ${headerCounter[i] == 1 ? (1/i) : 0}rem; margin-left: ${i * 0.75 + 0.5}rem" tag-type="h${i}" data-index="${index}">${index}. ${line.slice(i)}</fake-link>`
						);
						return `<h${i} data-index="${index}">${index}. ${line.slice(i)}</h${i}>`;
					}
				}

				if (unorderedListRegexp.test(line)) {
					return `<unordered-list-element>${line.replace(unorderedListRegexp, "$1")}</unordered-list-element>`;
				} else if (orderedListRegexp.test(line)) {
					return `<ordered-list-element data-number=${line.replace(orderedListRegexp, "$1")}>${line.replace(
						orderedListRegexp,
						"$2"
					)}</ordered-list-element>`;
				} else if (line.startsWith("&gt;")) {
					return `<quote-element>${line.slice(4)}</quote-element>`;
				} else if (line.startsWith("```")) {
					if (codeBlockCounter == 0) {
						codeBlockCounter++;
						return `<code-block>${line.slice(3)}`;
					} else {
						codeBlockCounter--;
						return `${line.slice(3)}</code-block>`;
					}
				} else {
					let res = `${openParagraph ? "" : "<p>"}${line}`;
					openParagraph = true;
					return res;
				}
			})
			.map((line) => {
				if (anyTagRegexp.test(line) && !line.includes("<p>")) {
					return CPIN() + line;
				}
				return " " + line;
			})
			.map((line) => {
				let result = line;
				while (boldRegexp.test(result) && !mathRegexp.test(result)) {
					result = result.replace(boldRegexp, "<b>$2</b>");
				}
				while (italicRegexp.test(result) && !mathRegexp.test(result)) {
					result = result.replace(italicRegexp, "<i>$2</i>");
				}
				while (strikethroughRegexp.test(result)) {
					result = result.replace(strikethroughRegexp, "<s>$2</s>");
				}
				while (inlineCodeRegexp.test(result)) {
					result = result.replace(inlineCodeRegexp, "<inline-code>$2</inline-code>");
				}
				while (underlineRegexp.test(result)) {
					result = result.replace(underlineRegexp, "<u>$2</u>");
				}
				while (imgRegexp.test(result)) {
					result = result.replace(imgRegexp, '<img src="$4" alt="$1" width="$2" height="$3">');
				}
				while (imgClassRegexp.test(result)) {
					result = result.replace(imgClassRegexp, '<img class="$2" src="$3" alt="$1">');
				}
				while (linkRegexp.test(result)) {
					result = result.replace(linkRegexp, '<a href="$2">$1</a>');
				} 
				return result;
			})
			.join("");
		this.innerHTML = `${tableOfContentsTitle}<div>${tableOfContents.join("")}</div>${text}`;
	}

	connectedCallback() {
		this.render();
	}
}

class UnorderedListElement extends HTMLElement {
	constructor() {
		super();
	}
}

class OrderedListElement extends HTMLElement {
	constructor() {
		super();
	}
}

class QuoteElement extends HTMLElement {
	constructor() {
		super();
	}
}

class CodeBlock extends HTMLElement {
	constructor() {
		super();
	}
}

class InlineCodeElement extends HTMLElement {
	constructor() {
		super();
	}
}

class FakeLinkElement extends HTMLElement {
	constructor() {
		super();
	}

	connectedCallback() {
		this.addEventListener("click", () => {
			let element = document.querySelector(
				`${this.getAttribute("tag-type")}[data-index="${this.getAttribute("data-index")}"]`
			);
			element.scrollIntoView({ behavior: "smooth", block: "start", inline: "nearest" });
		});
	}
}

window.customElements.define("markdown-parser", MarkdownParserElement);
window.customElements.define("unordered-list-element", UnorderedListElement);
window.customElements.define("ordered-list-element", OrderedListElement);
window.customElements.define("quote-element", QuoteElement);
window.customElements.define("code-block", CodeBlock);
window.customElements.define("inline-code", InlineCodeElement);
window.customElements.define("fake-link", FakeLinkElement);

var styles = document.head.appendChild(document.createElement("link"));
styles.rel = "stylesheet";
styles.href = "markdown.css";
