import { defineConfig } from "tsup";

// Dual ESM + CJS build with emitted .d.ts declarations.
// One entry point per subpath export: `.`, `./log` and `./preferences`.
export default defineConfig({
  entry: ["src/index.ts", "src/log/index.ts", "src/preferences/index.ts"],
  format: ["esm", "cjs"],
  dts: true,
  clean: true,
  sourcemap: true,
  // No top-level side effects: keeps browser bundles tree-shakeable.
  treeshake: true,
});
