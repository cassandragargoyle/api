import { defineConfig } from "tsup";

// Dual ESM + CJS build with emitted .d.ts declarations.
// Two entry points so the package exposes both `.` and `./log` subpaths.
export default defineConfig({
  entry: ["src/index.ts", "src/log/index.ts"],
  format: ["esm", "cjs"],
  dts: true,
  clean: true,
  sourcemap: true,
  // No top-level side effects: keeps browser bundles tree-shakeable.
  treeshake: true,
});
