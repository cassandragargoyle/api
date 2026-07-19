---
name: create-manifest
description: Create a software manifest following the project methodology. Use when the user wants to create a manifest for a piece of software, a tool, or a technology.
---

# Create a software manifest

Your task is to create a software manifest for the given item (a software, tool, or technology name).

## STEP 1: Determine the item

If the user did not provide an item, ask:

> What software, tool, or technology do you want to create a manifest for?

Wait for the user's answer, then continue.

## STEP 2: Study the methodology

Read `docs/manifests/README.md` to understand the structure and conventions of manifests.

## STEP 3: Review existing manifests

Look at existing manifests in `docs/manifests/` for inspiration on format and content (e.g. `manifest-docker.md`, `manifest-go.md`).

## STEP 4: Research the information

Use web search to gather up-to-date information about:

- Current version and release date
- Official documentation and website
- License terms
- System requirements
- Main use cases

## STEP 5: Create the manifest

Create the file `docs/manifests/manifest-{name}.md` containing:

- Basic Information (name, category, license, version, maintainer, investment type)
- Installation Details
- Platform Support
- Dependencies
- Portunix Integration
- Verification commands
- Best Use Cases (✅ recommended, ⚠️ not recommended)
- Community & Metrics
- Learning Resources

## STEP 6: Update the README

Add the new manifest to the list in `docs/manifests/README.md` in the correct place by category.

## Conventions

- Use icons by investment type: 📈 (publicly traded), 🔒 (private), 🆓 (open-source)
- Use ✅ for recommended use cases and ⚠️ for cases where alternatives should be considered
- Keep the manifest content in English (the project uses English documentation)
- Filename: `manifest-{kebab-case-name}.md`
