# GitHub Publishing Workflow for Api

This system allows you to separate local development (Gitea) from public publishing (GitHub).

## 🔧 One-time Setup

```bash
# Run the setup script
./scripts/github-00-setup.sh
```

## 📋 Enhanced Workflow

### 1. Local Development
```bash
# Normal development on Gitea
git add .
git commit -m "wip: working on feature X"
git push origin feature-branch
```

### 2. Sync & Publish to GitHub
```bash
# When ready to publish
./scripts/github-02-sync-publish.sh
```

**The enhanced workflow** will interactively guide you through:
1. 📥 **GitHub Sync** - downloads current state from GitHub
2. 📊 **Change Analysis** - analyzes local changes
3. 🌿 **Branch Creation** - creates branch with good naming
4. 📁 **File Sync** - copies files from local repo (excluding private files)
5. ✏️ **Commit Message** - creates descriptive commit
6. 🚀 **Publishing** - pushes branch to GitHub
7. 🧹 **Cleanup** - optional cleanup

### 3. Alternative Quick Workflow
```bash
# For quick squash publishing (original method)
./scripts/github-02-quick-publish.sh
```

## 📁 Files Removed Before Publishing

Based on the cleanup configuration:
- `CLAUDE.md`, `GEMINI.md`, `NOTES.md`
- `bin/`, `*.exe`
- `docs/private/`, `config/dev/`, `config/internal/`
- Build scripts (`.bat`, `.sh`)
- Internal development files
- Packaging scripts
- Test fixtures with sensitive data
- Environment files (`.env*`)
- Keys and certificates (`*.key`, `*.pem`)

## 🎛️ Git Remote Structure

```
origin  -> git@gitea:CassandraGargoyle/Api (development)
github  -> https://github.com/CassandraGargoyle/Api (publishing)
```

## 💡 Best Practices

- **Development**: Commit frequently to Gitea, don't worry about WIP commits
- **Release**: Use scripts for clean GitHub commits
- **Security**: Private files are automatically removed
- **History**: GitHub will have clean history, Gitea preserves everything
- **Testing**: Always test publishing workflow with non-critical changes first

## 🔍 Troubleshooting

### GitHub Remote Doesn't Exist
```bash
git remote add github https://github.com/CassandraGargoyle/Api
```

### Push Conflicts
The script uses `--force-with-lease` for safety. If someone committed to GitHub in the meantime, the script will stop.

### Incorrect Private Files
Edit the file list in the cleanup script's `PRIVATE_FILES` section.

### Setup Script Fails
- Verify you have access to both Gitea and GitHub repositories
- Check that GitHub repository exists and you have push permissions
- Ensure Git credentials are properly configured

### Branch Already Exists on GitHub
The script will ask if you want to overwrite the existing branch or create a new one.

## 🚀 Testing the Workflow

Test with current changes:
```bash
# Test the enhanced workflow
./scripts/github-02-sync-publish.sh

# Or test quick publish
./scripts/github-02-quick-publish.sh
```

## 📋 Checklist for New Projects

- [ ] Run initial setup: `./scripts/github-00-setup.sh`
- [ ] Verify both remotes are configured correctly
- [ ] Test publishing workflow with a small change
- [ ] Review and customize private files list
- [ ] Document any project-specific publishing requirements
- [ ] Train team members on the workflow

## 🔒 Security Notes

- Private files are automatically excluded from public releases
- Secrets and configuration files are not published
- Review the cleanup script before first use
- Consider using separate branches for different release channels

---

**Note**: This workflow is designed to maintain clean public repositories while preserving full development history internally.

*Part of the Api project - maintaining clean public releases while preserving development history.*