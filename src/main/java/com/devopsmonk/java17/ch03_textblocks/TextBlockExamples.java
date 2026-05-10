package com.devopsmonk.java17.ch03_textblocks;

/**
 * Java 17 — Text Blocks (JEP 378)
 * Matches blog article: 03-text-blocks.md
 */
public class TextBlockExamples {

    // ── 1. Basic text block ──────────────────────────────────────────────────
    static void basicTextBlock() {
        String json = """
                {
                    "name": "Alice",
                    "age": 30,
                    "city": "London"
                }
                """;
        System.out.println("=== Basic Text Block ===");
        System.out.println(json);
    }

    // ── 2. Text block vs. old-style string ───────────────────────────────────
    static void comparisonWithOldStyle() {
        // Old style — escape hell
        String oldStyle = "{\n" +
                "    \"name\": \"Alice\",\n" +
                "    \"age\": 30\n" +
                "}";

        // Java 17 text block — clean and readable
        String textBlock = """
                {
                    "name": "Alice",
                    "age": 30
                }
                """;

        System.out.println("=== Old Style ===");
        System.out.println(oldStyle);
        System.out.println("=== Text Block ===");
        System.out.println(textBlock);
        System.out.println("Equal: " + oldStyle.equals(textBlock.stripTrailing()));
    }

    // ── 3. HTML text block ────────────────────────────────────────────────────
    static void htmlTextBlock() {
        String html = """
                <!DOCTYPE html>
                <html>
                  <head><title>Java 17</title></head>
                  <body>
                    <h1>Text Blocks</h1>
                    <p>No more escape sequences!</p>
                  </body>
                </html>
                """;
        System.out.println("=== HTML Text Block ===");
        System.out.println(html);
    }

    // ── 4. SQL text block ─────────────────────────────────────────────────────
    static void sqlTextBlock() {
        String tableName = "orders";
        String sql = """
                SELECT o.id,
                       o.customer_name,
                       o.total_amount,
                       o.created_at
                FROM   %s o
                WHERE  o.status = 'PENDING'
                  AND  o.created_at >= CURRENT_DATE - INTERVAL '7 days'
                ORDER  BY o.created_at DESC
                LIMIT  100
                """.formatted(tableName);

        System.out.println("=== SQL Text Block ===");
        System.out.println(sql);
    }

    // ── 5. Indentation control — closing """ position matters ─────────────────
    static void indentationControl() {
        // Closing """ at column 0 — full indentation preserved
        String withIndent = """
                line 1
                line 2
                line 3
            """;  // closing """ at 12 spaces — removes 12 spaces of indentation

        System.out.println("=== Indentation Control ===");
        System.out.print(withIndent);

        // indent() adds N spaces to each line
        String indented = "hello\nworld\n".indent(4);
        System.out.println("=== With indent(4) ===");
        System.out.print(indented);
    }

    // ── 6. String::stripIndent and translateEscapes ───────────────────────────
    static void newStringMethods() {
        String raw = "    line 1\n    line 2\n    line 3\n";
        System.out.println("=== stripIndent() ===");
        System.out.print(raw.stripIndent());

        // translateEscapes — process \n \t etc. in a regular string
        String withLiteralEscape = "Hello\\nWorld";
        System.out.println("=== translateEscapes() ===");
        System.out.println(withLiteralEscape.translateEscapes());
    }

    // ── 7. Line continuation with \ ───────────────────────────────────────────
    static void lineContinuation() {
        // \ at end of line joins the next line — no newline in output
        String oneLine = """
                This is a very long sentence that we want to \
                keep on one line in the output but split \
                across multiple source lines for readability.
                """;
        System.out.println("=== Line Continuation ===");
        System.out.println(oneLine);
    }

    // ── 8. YAML text block ────────────────────────────────────────────────────
    static void yamlTextBlock() {
        String yaml = """
                server:
                  port: 8080
                  host: localhost

                database:
                  url: jdbc:postgresql://localhost:5432/mydb
                  username: admin
                  pool-size: 10

                logging:
                  level: INFO
                """;
        System.out.println("=== YAML Text Block ===");
        System.out.println(yaml);
    }

    public static void main(String[] args) {
        basicTextBlock();
        comparisonWithOldStyle();
        htmlTextBlock();
        sqlTextBlock();
        indentationControl();
        newStringMethods();
        lineContinuation();
        yamlTextBlock();
    }
}
