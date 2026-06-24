# CLAUDE.md

**Project**: Order — 酒吧/餐厅点单管理精简版

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

## 5. APK Location After Build

**After every successful build, output the APK path. Default to debug build (`assembleDebug`). Only use release build (`assembleRelease`) when the user explicitly requests it.**

```
APK 位置: app\build\outputs\apk\debug\app-debug.apk
```

## 6. Auto Git Push After Build

**When the user continues conversation after a build (with no explicit instruction to skip), first commit and push to preserve changes:**

- If there are uncommitted changes, create a commit with a concise message summarizing the changes made in the previous turn.
- Then run `git push`.
- If the user explicitly says to skip or do something else first, follow that instead.
- Never force push. If push fails (e.g., no remote, no permission), report the error and continue.

## 7. Build Environment

**Windows only — no WSL.** Use `.\gradlew.bat` from project root. Set JAVA_HOME before every build (PowerShell sets it per-process):

```powershell
$env:JAVA_HOME = "D:\software\AndroidStudio\jbr"; .\gradlew.bat assembleDebug
```

**JDK path**: `D:\software\AndroidStudio\jbr` (JetBrains Runtime bundled with Android Studio).  
**IDE**: Android Studio.  
**First build** of a session takes ~1 minute (Dex, etc.) due to cold Gradle daemon. Set `timeout_ms = 300000` (5 min) to avoid abort. Subsequent builds reuse configuration cache and finish faster.  
**Release build** only when user explicitly requests `assembleRelease`.

## 8. Prefer Edit Over Write + Sync STRUCTURE.md

**Prefer modifying files with the Edit tool rather than rewriting entire files. Only use Write when creating new files or when the scope of changes exceeds 50% of the file.**

**After any structural change (new/removed files, changed component responsibilities, navigation flow updates), sync the changes to `STRUCTURE.md`.**

## 9. Project Constraints (DO NOT VIOLATE)

- **This is a standalone local app. No networking, no LAN, no server.** Do NOT add any network-related code or dependencies.
- **No Ktor, no NSD, no HTTP.** The original Orderpp project has these; this project removed them intentionally.
- **Guest and staff modes are merged.** There is a single `isEditing` boolean toggle in MainViewModel. Edit mode enables all staff editing features (add/edit/delete menu items, add/delete tables, settle orders). Guest mode is the default read-only ordering view.
- **Tablet: one page** with menu panel on the left (2/3) and bill panel on the right (1/3). No bottom navigation bar on tablet.
- **Phone: two pages** with HorizontalPager (non-swipeable) + Bottom NavigationBar. Page 0 = 菜单, Page 1 = 账单.
- **Table selection is required.** Users must pick a table before ordering.

## 10. Key Architecture Decisions

- Single ViewModel (`MainViewModel`) handles all state — no separate ViewModels per feature.
- SQLite via `SQLiteOpenHelper` (not Room).
- SharedPreferences stores the last selected table ID for persistence.
- Edit mode toggle: `viewModel.toggleEdit()` switches between guest (default) and staff editing. When toggleing off (saving), data is reloaded from DB.
- Recipe editing is done inside `RecipeSheet` (ModalBottomSheet), which receives `isStaff = isEditing`.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.
